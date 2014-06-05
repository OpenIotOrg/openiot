/**
 * Copyright (c) 2011-2014, OpenIoT
 *
 * This file is part of OpenIoT.
 *
 * OpenIoT is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, version 3 of the License.
 *
 * OpenIoT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenIoT. If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact: OpenIoT mailto: info@openiot.eu
 */
package org.openiot.cupus.entity.broker;

import com.google.android.gcm.server.Result;
import com.sun.corba.se.pept.broker.Broker;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.UUID;
import org.apache.commons.codec.binary.Base64;

import org.openiot.cupus.common.MinimalistLinkedHashQueue;
import org.openiot.cupus.message.Message;

/**
 * This class is an implementation of a queue assigned to a single subscriber
 * that is registered to the broker and it's job is to hold the outgoing
 * messages to that subscriber and send them when able (i.e. when connection
 * exists).
 *
 * @author Eugen
 *
 */
public class SubscriberQueue implements Runnable {

    private int CAPACITY = -1;

    private MinimalistLinkedHashQueue<Message> queue;
    private Object queueMutex = new Object();

    private UUID entityID;
    private Socket socket;
    private ObjectOutputStream outToSubscriber;
    private boolean isGCMEntity;
    private String GCMId;

    private DeliveryService broker;

    private boolean isConnected = false;

    public SubscriberQueue(UUID entityID, int queueCapacity, DeliveryService broker, boolean GCMEntity) {
        this.entityID = entityID;
        this.CAPACITY = queueCapacity;
        this.broker = broker;
        queue = new MinimalistLinkedHashQueue<Message>(CAPACITY);
        this.isGCMEntity = GCMEntity;
    }

    @Override
    public void run() {
        try {
            if (isGCMEntity) {
                if (GCMId == null ||GCMId.isEmpty()) {
                    return;
                }
            } else {
                if (socket == null || socket.isClosed()) {
                    return;
                }
            }
            isConnected = true;
            while (isConnected && broker.isRunning) {
                if (!isGCMEntity) {
                    if (outToSubscriber == null) {
                        break;
                    }
                }
                synchronized (queueMutex) {
                    Message next = queue.peek();
                    if (next == null) {
                        try {
                            queueMutex.wait(); //block it until something for sending comes along...
                        } catch (InterruptedException e) {
                            /* ignore (something will have already called disconnect if something is wrong
                             * with the connection, and if not the thread will just go another cycle...
                             */
                        }
                    } else {
                        if (sendMessage(next)) //if the message is successfully sent then remove it from queue
                        {
                            queue.poll();
                        }
                    }
                }
            }
        } catch (Exception e) {
            terminateConnection();
        }
    }

    /**
     * Putting matching publications in the queue to send to subscriber. (avoids
     * putting duplicates in because of MinimalistLinkedHashQueue
     * implementation)
     */
    public boolean put(Message msg) {
        synchronized (queueMutex) {
            if (!queue.offer(msg)) {
                if (queue.isFull()) {
                    broker.informBroker("Entity ID=" + entityID + " not notified of publication or subscription"
                            + " " + msg + " because queue is full!", true);
                }
                return false;
            } else {
                queueMutex.notify(); //let the sending thread know there is something new in the queue
                return true;
            }
        }
    }

    /**
     * Used for unpublishing yet unsent publications...
     */
    public boolean remove(Message msg) {
        if (msg == null) {
            return false;
        }
        synchronized (queueMutex) {
            return queue.remove(msg);
        }
    }

    /**
     * Method for sending messages.<br>
     * (terminates connection if sending fails)
     *
     * @param msgToSend Message to be sent
     */
    private boolean sendMessage(Message msgToSend) {
        if (isGCMEntity) {
            String forDelivery = Base64.encodeBase64String(serialize(msgToSend));
            com.google.android.gcm.server.Message message = new com.google.android.gcm.server.Message.Builder()
                    .delayWhileIdle(true)
                    .addData("message", forDelivery)
                    .build();
            //TODO: serialization + base64
            try {
                if (broker.getSender() != null) {
                    Result r = broker.getSender().send(message, GCMId, 1);
                    broker.informBroker("GCM Message: " + r.getMessageId(), false);
                    return true;
                } else {
                    broker.informBroker("GCM Sender is not initialized - missing application key", false);
                    return false;
                }

            } catch (Exception e) {
                broker.informBroker("GCM Sender cannot send message to a mobile broker " + e.getMessage(), false);
                return false;
            }
        } else {
            try {
                outToSubscriber.writeObject(msgToSend);
                outToSubscriber.flush();
            } catch (Exception e) {
                //SubscriberForBroker will have already sent a disconnect message
                terminateConnection();
                return false;
            }
            return true;
        }
    }

    /**
     * For stoping the thread that sends messages from this queue...
     */
    public void terminateConnection() {
        if (isGCMEntity) {
            isConnected = false;
        } else {
            if (socket == null && !isConnected) {
                return;
            }
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception e) {
                //ignoring
            }
            socket = null;
            outToSubscriber = null;
            isConnected = false;
        }
    }

    /**
     * For setting up a new connection with a previously registered subscriber
     * (existing queue). It disconnects the queue first (if it was connected,
     * and it shouldn't have been.
     */
    public void setConnection(Socket socket) {
        try {
            isConnected = false;
            terminateConnection();

            this.socket = socket;
            this.outToSubscriber = new ObjectOutputStream(
                    socket.getOutputStream());
            outToSubscriber.flush();
        } catch (IOException ex) {
            outToSubscriber = null;
            try {
                socket.close();
            } catch (IOException ex1) {

            }
        }
    }

    public void setGCMId(String id) {
        this.GCMId = id;
    }

    public byte[] serialize(Object obj) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(out);
            os.writeObject(obj);
            return out.toByteArray();
        } catch (IOException e) {
            broker.informBroker("Cannot serialize a message", false);
            return new byte[0];
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public UUID getEntityID() {
        return entityID;
    }
}
