package org.openiot.gsn.networking;

import java.net.Socket;

public interface NetworkAction {
	public boolean actionPerformed(Socket socket);
}
