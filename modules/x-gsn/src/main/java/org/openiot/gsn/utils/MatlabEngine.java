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

package org.openiot.gsn.utils;
		
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * <b>Java MatlabEngine for Matlab via Runtime</b><br>
 * This class demonstrates how to call Matlab from a Java program, 
 * thereby employing Matlab as the computation engine. 
 * The Matlab engine operates by running in the background as a separate
 * process from your own program.
 * <p>
 * Date: 04.04.03
 * <p>
 * Copyright (c) 2003 Andreas Klimke, Universit�t Stuttgart
 * <p>
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions: 
 * <p>
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * Original author: W. Andreas Klimke, University of Stuttgart 
 *         (klimke@ians.uni-stuttgart.de)
 *
 */
public class MatlabEngine {
	
	/**
	 * The constructor initializes the buffer storing Matlab's output.
	 */
	public MatlabEngine() {
		// Initialize the ouput buffer (stores Matlab's output).
		outputBuffer = new char[DEFAULT_BUFFERSIZE];
	}

	// indicates wether a Matlab session is currently open or closed
	private boolean isOpen = false;

	// a handle to the Matlab process
	private Process p;

	// the output stream of the Matlab process is piped to the stream 'in'
	private BufferedReader in;

	// the input stream of the Matlab process is piped to the stream 'out'
	private BufferedWriter out;

	// the error stream is piped to the stream 'err'
	private BufferedReader err;

	// Buffer size for receiving Matlab output. If buffer is full, remaining
	// data is retrieved and discarded.
	static final int DEFAULT_BUFFERSIZE = 65536;
  private char[] outputBuffer;

	// Once the output buffer is full, the remaining data is discarded into
	// a buffer, reading DEFAULT_SKIP characters at a time.
	private static final int DEFAULT_SKIP = 65536;

	// Stores the actual number of bytes read after last command.
	private int totalCharsRead;


	/**
	 * Starts a Matlab session on the current host using the command 'matlab'.
	 * @throws IOException Thrown if invoking Matlab was not successful.
	 */
	public void open() throws IOException {
		open("matlab");
	}

	/**
	 * Starts a process using the string passed as an argument.
	 * The operating system executes the string passed in as argument  
	 * <it>litteraly</it>.<br>
	 * Examples for starting Matlab (Unix-based systems):<br>
	 * <ul>
	 * <li> <code>startcmd = "matlab -nojvm -nosplash"</code> starts Matlab 
	 * on the current host without splash screen and whithout Java support.
	 * <li> <code>startcmd = "rsh hostname \"/bin/csh -c 
	 * 'setenv DISPLAY hostname:0; matlab'\""</code> starts Matlab on the host 
	 * "hostname" (under Linux).
	 * <li> <code>startcmd = "ssh hostname /bin/csh -c 
	 * 'setenv DISPLAY hostname:0; matlab'"</code> starts Matlab through a 
	 * secure shell (under Linux).
	 * </ul>
	 * @param startcmd The start command string.
	 * @throws IOException Thrown if starting the process was not successful.
	 */
	public void open(String startcmd) throws IOException {
		if (isOpen) {
			System.err.println("Matlab session already open.");
			return;
		}
		try {
			// System.err.println("Opening Matlab...");
			synchronized(this) {
				p = Runtime.getRuntime().exec(startcmd);
				// get handle on the input/output strings
				out = new BufferedWriter(new OutputStreamWriter
																 (p.getOutputStream()));
				in  = new BufferedReader(new InputStreamReader
																 (p.getInputStream()));
				err = new BufferedReader(new InputStreamReader
																 (p.getErrorStream()));
				isOpen = true;
			}
			// Wait for the Matlab process to respond.
			receive();
		}
		catch(IOException e) {
      System.err.println("Matlab could not be opened.");
			throw(e);
		}
	}

	/**
	 * Shuts down the Matlab session. After the "exit" command is written to
	 * the output stream to close Matlab, the method waits for the Matlab
	 * process to terminate. Finally, the input and the output streams are 
	 * closed.
	 * @throws InterruptedException Thrown if interrupt occured while waiting
	 *                              for the Matlab process to terminate. 
	 * @throws IOException Thrown if I/O streams could not be closed.
	 */
	public void close() throws InterruptedException, IOException {
		// System.err.println("Closing the Matlab session...");
		send("exit");
		try {
			synchronized(this) {
				p.waitFor();
				out.close();
				in.close();
				isOpen = false; 
			}
		}
		catch(InterruptedException e) {
			throw(e);
		}
		catch(IOException e) {
			System.err.println("Error while closing input/output streams.");
			throw(e);
		}
	}

	/**
	 * Send data to the Matlab process.
	 */
	private void send(String str) throws IOException {
 		try {
			// System.err.println("Evaluation string: "+str);
			// Adding the end of line character sequence will cause the Matlab
			// process to execute the command string.
			str+="\n";
			synchronized(this) {
				// write string to Matlab's input stream
				out.write(str, 0, str.length());
				out.flush();
			}
		}
		catch(IOException e) {
			System.err.println("IOException occured while sending data to the"
												 +" Matlab process.");
			throw(e);
		}
	}

	/**
	 * Skip over data from the Matlab output.
	 * Instead of directly using the skip method of the BufferedReader,
	 * it was necessary to use read instead, since skip did work properly
	 * with our Java version (J2RE SE, 1.4.1) and operating system (Linux).
	 * The skip method of BufferedReader used to block instead of returning
	 * the actual number of characters skipped.
	 */
	private void skip() throws IOException {
		char[] skipBuffer = new char[DEFAULT_SKIP];
		while(in.ready()) {
			in.read(skipBuffer,0,DEFAULT_SKIP);
		}
		System.err.println("Warning: Some output information was "+
											 "dropped, since the available number of "+
											 "bytes exceeded the output buffer "+
											 "size.");			
	}
	
	/**
	 * Receive data from the Matlab process.
	 */
	private void receive() throws IOException {

		int charsRead = 0;
		int numberToRead;
		
		totalCharsRead = 0;
		// System.err.println("Receiving...");
 		try {
			synchronized(this) {
				while (totalCharsRead == 0) {
					while (in.ready()) {
						if ((numberToRead = DEFAULT_BUFFERSIZE-totalCharsRead) > 0) {
							charsRead = in.read(outputBuffer, totalCharsRead, numberToRead);
							if (charsRead > 0) {
								totalCharsRead += charsRead;
							}
						}
						else {
							skip();
							return;
						}
					}
				}
			}
		}
		catch(IOException e) {
			System.err.println("IOException occured while receiving data from"
												 +" the Matlab process.");
			throw(e);
		}
	}

	/**
	 * Evaluates the expression contained in <code>str</code> using
	 * the current Matlab session, previously started by <code>open</code>.
	 * Note: The method blocks until the result stream is received from
	 * Matlab. The result is written to a private buffer, and may be
	 * retrieved with <code>getOutputString</code>.
	 * @param str Expression to be evaluated.
	 * @throws IOException Thrown if data could not be sent to Matlab. This
	 *                     may happen if Matlab is no longer running.
	 * @see #open
	 * @see #getOutputString
	 */
	public void evalString(String str) throws IOException {		
		send(str);
		receive();
	}

	/**
	 * Reads a maximum of <code>numberOfChars</code> characters from the 
	 * buffer containg Matlab's output.
	 * @return string containing the Matlab output.
	 * @see #open
	 */
	public String getOutputString (int numberOfChars) {
		if (totalCharsRead < numberOfChars) numberOfChars = totalCharsRead;

		char[] result = new char[numberOfChars];
		System.arraycopy(outputBuffer, 0, result, 0, numberOfChars);
		return new String(result);
	}
}
