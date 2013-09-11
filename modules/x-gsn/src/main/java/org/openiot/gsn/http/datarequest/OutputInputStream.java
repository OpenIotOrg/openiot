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

package org.openiot.gsn.http.datarequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;

public class OutputInputStream {

	private OISInputStream 	oisi = null;
	private OISOutputStream	oiso = null;
	private boolean oisiClosed = false;
	private boolean oisoClosed = false;
	private ArrayBlockingQueue<Integer> circularBuffer = null;

	public OutputInputStream (int bufferSize) {
		circularBuffer = new ArrayBlockingQueue<Integer> (bufferSize) ;		
	}

	public void close () throws IOException {
		synchronized (this) {
			if (oisi != null && ! oisiClosed) oisi.close();
			if (oiso != null && ! oisoClosed) oiso.close();
			circularBuffer = null;
//			System.out.println("OutputInputStream >" + this + "< has been closed");
		}
	}

	public InputStream getInputStream () {
		if (oisi == null) oisi = new OISInputStream () ;
		return oisi;
	}

	public OutputStream getOutputStream () {
		if (oiso == null) oiso = new OISOutputStream () ;
		return oiso;
	}
	
	private class OISOutputStream extends OutputStream {
		@Override
		public void write(int b) throws IOException {
			if (oisoClosed) throw new IOException("Outputstream is closed");
			try {
				circularBuffer.put(b);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		@Override
		public void close () throws IOException {
			synchronized (OutputInputStream.this) {
				oisoClosed = true;
//				System.out.println("OISOutputStream >" + this + " has been closed<");
				if (oisiClosed) OutputInputStream.this.close(); 
			}
		}
	}

	private class OISInputStream extends InputStream {
		@Override
		public int read() throws IOException {
			if (oisiClosed) throw new IOException("InputStream has been closed");
			int nextValue = -1;
			try {
				if ( ! oisoClosed) {
					nextValue = circularBuffer.take();
				}
				else {
					nextValue = available() > 0 ? circularBuffer.take() : -1 ;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return nextValue;
		}
		@Override
		public int read (byte[] b) throws IOException {
			if (oisiClosed) throw new IOException("InputStream has been closed");
			if (b == null || b.length == 0) return 0;
			int available = available () ;
			if (available == 0) {
				if (oisoClosed) return -1;
				else {
					try {
						b[0] = circularBuffer.take().byteValue();  // TODO
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					return 1;
				}
			}
			else {
				int dataLength = Math.min(available, b.length);
				for (int i = 0 ; i < dataLength ; i++) {
					try {
						b[i] = circularBuffer.take().byteValue();  // TODO
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				return dataLength;
			}
		}
		@Override 
		public void close() throws IOException {
			synchronized (OutputInputStream.this) {
				oisiClosed = true;
//				System.out.println("OISInputStream >" + this + " has been closed<");
				OutputInputStream.this.close();
			}
		}
		@Override 
		public int available () {
			int available = 0;
			synchronized (circularBuffer) {
				available = circularBuffer.size();
			}
			return available;
		}
	}

	public static void main (String[] args) {
		final OutputInputStream ois = new OutputInputStream (4) ;
		new Thread(
				new Runnable () {
					public void run () {
						try {
							ois.getOutputStream().write("ABCDEFGHIJKLMNOPQRSTUVWXYZ".getBytes());
							ois.getOutputStream().close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
		).start();
		InputStream is = ois.getInputStream();
		int nextValue = -1;
		try {
			while ((nextValue = is.read()) != -1) {
				System.out.println("read: " + nextValue);
			}
			ois.getInputStream().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
