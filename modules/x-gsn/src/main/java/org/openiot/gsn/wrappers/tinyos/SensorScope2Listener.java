/**
*    Copyright (c) 2011-2014, OpenIoT
*   
*    This file is part of OpenIoT.
*
*    OpenIoT is free software: you can redistribute it and/or modify
*    it under the terms of the GNU Lesser General Public License as published by
*    the Free Software Foundation, version 3 of the License.
*
*    OpenIoT is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU Lesser General Public License for more details.
*
*    You should have received a copy of the GNU Lesser General Public License
*    along with OpenIoT.  If not, see <http://www.gnu.org/licenses/>.
*
*     Contact: OpenIoT mailto: info@openiot.eu
*/

package org.openiot.gsn.wrappers.tinyos;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SensorScope2Listener implements Runnable {

    private static final int MAX_BUFFER_SIZE = 128;
    private boolean initialized = false;
    private boolean running = false;

    private static SensorScope2Listener instance = null;

    private transient Logger logger = Logger.getLogger(this.getClass());
    private int server_port;
    long counter = 0;

    private ServerSocket serverSocket = null;
    private Socket client = null;

    private Thread thread;
    private static final long PACKET_AGE_LIMIT = 600000; // packet stay for a maximum of PACKET_AGE_LIMIT (ms) before being recycled

    public ConcurrentLinkedQueue<SensorScope2Packet> queue = new ConcurrentLinkedQueue<SensorScope2Packet>();

    //public SensorScope2Packet

    public SensorScope2Packet getNextPacketAfter(SensorScope2Packet latestPacket) {

        RecyclePackets();

        long timestamp = 0;

        if (latestPacket != null)
            timestamp = latestPacket.timestamp;

        Iterator iter = queue.iterator();
        boolean found = false;
        SensorScope2Packet packet = null;
        while (iter.hasNext() && !found) {
            packet = (SensorScope2Packet) iter.next();
            if (packet.timestamp >= timestamp) {  // duplicate timestamps are possible because of multiple station ids
                if (packet != latestPacket) // skip same packet already processed
                    if (queue.contains(latestPacket)) {
                        if (indexOf(packet) > indexOf(latestPacket))
                        found = true;
                    } else {
                        found = true;
                    }
            }
        }
        if (found)
            return packet;
        else
            return null;
    }

    private int indexOf(SensorScope2Packet packet) {
        Iterator iter  = queue.iterator();
        int index = -1;
        boolean found = false;
        while (iter.hasNext() && !found) {
            index++;
            if (packet == iter.next())
                found = true;
        }
        if (found)
            return index;
        else
            return -1;
    }

    /*
    * Delete elements older than age limit
    * */
    public void RecyclePackets() {
        Iterator iter = queue.iterator();
        long currentTimeMillis = System.currentTimeMillis();
        int n_packets_deleted = 0;
        SensorScope2Packet packet = null;
        while (iter.hasNext()) {
            packet = (SensorScope2Packet) iter.next();
            if (currentTimeMillis - packet.timestamp > PACKET_AGE_LIMIT) {
                queue.remove(packet);
                n_packets_deleted++;

            }
        }
        if (n_packets_deleted != 0)
            logger.warn("Deleted " + n_packets_deleted + " packet(s). New size => " + queue.size());
    }

    public void start() {
        if (!running && initialized) {
            this.thread.start();
            running = true;
        } else {
            logger.warn("Listener thread already running or not initialized.");
        }
    }

    public void setPort(int server_port) {
        if (initialized) {
            logger.warn("Port already initialized. (Singleton can be initialized only once)");
            return; // initialize only once
        }
        this.server_port = server_port;
        this.initialized = true;
        // Create a server socket
        logger.warn("Trying to open a server socket on port " + server_port);
        try {
            serverSocket = new ServerSocket(server_port);
        } catch (IOException e) {
            logger.error("Cannot open a server socket on port " + server_port + ".");
            this.initialized = false;
        }
    }

    public static SensorScope2Listener getInstance() {
        if (instance != null) {
            return instance;
        } else {
            instance = new SensorScope2Listener();
            return instance;
        }
    }

    private SensorScope2Listener() {
        this.thread = new Thread(this);
    }

    public void run() {
        if (!initialized)
            return;

        logger.warn("Started SensorScope listener on port " + server_port + ".");

        while (running) {
            byte[] packet = null;

            try {

                // Wait for a  request
                client = serverSocket.accept();

                // Get the streams
                packet = new byte[MAX_BUFFER_SIZE];

                int n_read = client.getInputStream().read(packet);

                byte[] real_packet = new byte[n_read];

                System.arraycopy(packet, 0, real_packet, 0, n_read);

                SensorScope2Packet _packet = new SensorScope2Packet(System.currentTimeMillis(), real_packet);

                queue.add(_packet);

                String order = "[" + String.format("%8d", counter) + "]";

                logger.warn(order + " " + _packet.toString());

                counter++;

                //RecyclePackets(); // recycle old packets. Calling

            } catch (IOException e) {
                logger.error("Error in Server: " + e.getMessage(), e);
            } finally {
                try {
                    //inbound.close();
                    //outbound.close();
                    client.close();
                    //serverSocket.close();
                } catch (IOException e) {
                    logger.warn("Cannot close stream " + e.getMessage(), e);
                }
            }
        }

    }

    public void stopAcquisition() {
        running = false;
    }
}
