package org.openiot.gsn.networking;

import org.openiot.gsn.utils.ValidityTools;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.apache.log4j.Logger;

public class ActionPort {

	public static void listen(final int port,final NetworkAction action){
		Thread t = new Thread(){
			ServerSocket ss;
			Logger logger = Logger.getLogger ( this.getClass() );
			{
				try {
					ss = new ServerSocket(port, 0, InetAddress.getByName("localhost"));
				}
				catch (Exception e) {
					logger.error (e.getMessage(), e) ;
				}
			}
			public void run () {

				boolean running = true;

				while (running ) {
					try {
						Socket socket = ss.accept();
						if (logger.isDebugEnabled())
							logger.debug("Opened connection on control socket.");
						socket.setSoTimeout(30000);

						// Only connections from localhost are allowed
						if (ValidityTools.isLocalhost(socket.getInetAddress().getHostAddress()) == false) {
							try {
								logger.warn("Connection request from IP address >" + socket.getInetAddress().getHostAddress() + "< was denied.");
								socket.close();
							} catch (IOException ioe) {
								// do nothing
							}
							continue;
						}
						running = action.actionPerformed(socket);

					} catch (SocketTimeoutException e) {
						if (logger.isDebugEnabled())
							logger.debug("Connection timed out. Message was: " + e.getMessage());
					} catch (IOException e) {
						logger.warn("Error while accepting control connection: " + e.getMessage());
					}
				}
			}
		};
		t.start();
	}
}
