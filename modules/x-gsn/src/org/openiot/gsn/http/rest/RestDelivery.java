package org.openiot.gsn.http.rest;

import org.openiot.gsn.beans.DataField;
import org.openiot.gsn.beans.StreamElement;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.io.WriterOutputStream;

import com.thoughtworks.xstream.XStream;

public class RestDelivery implements DeliverySystem {

    private Continuation continuation;
    private ObjectOutputStream objectStream;

    private static final StreamElement keepAliveMsg = new StreamElement(new DataField[]{new DataField("keepalive", "string")}, new Serializable[]{"keep-alive message"}, Long.MIN_VALUE);

    public RestDelivery(Continuation connection) throws IOException {
        this.continuation = connection;
        XStream dataStream = StreamElement4Rest.getXstream();
        objectStream = dataStream.createObjectOutputStream((new WriterOutputStream(continuation.getServletResponse().getWriter())));
    }

    private static transient Logger logger = Logger.getLogger(RestDelivery.class);

    public void writeStructure(DataField[] fields) throws IOException {
        objectStream.writeObject(fields);
        objectStream.flush();
        continuation.getServletResponse().flushBuffer();
    }

    public synchronized boolean writeStreamElement(StreamElement se) {
        try {
            objectStream.writeObject(new StreamElement4Rest(se));
            objectStream.flush();
            continuation.resume();
            return ((LinkedBlockingQueue<Boolean>) continuation.getAttribute("status")).take();
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
            return false;
        }
    }

    public boolean writeKeepAliveStreamElement() {
        logger.debug("Sending the keepalive message.");
        return writeStreamElement(keepAliveMsg);
    }

    public void close() {
        try {
            if (objectStream != null){
                objectStream.close();
                continuation.complete();
            }
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
        }
    }

    public boolean isClosed() {
        try {
            return continuation.getServletResponse().getWriter().checkError();
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }

    }
}
