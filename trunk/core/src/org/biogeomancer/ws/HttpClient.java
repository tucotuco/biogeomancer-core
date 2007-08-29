/**
 * Copyright 2007 University of California at Berkeley.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

// This example is from _Java Examples in a Nutshell_. (http://www.oreilly.com)
// Copyright (c) 1997 by David Flanagan
// This example is provided WITHOUT ANY WARRANTY either expressed or implied.
// You may study, use, modify, and distribute it for non-commercial purposes.
// For any commercial use, see http://www.davidflanagan.com/javaexamples
package org.biogeomancer.ws;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;

/**
 * This program connects to a Web server and downloads the specified URL from
 * it. It uses the HTTP protocol directly.
 */
public class HttpClient {
  public void download(String fromurl, String tofile) {
    try {
      // Get an output stream to write the URL contents to
      OutputStream to_file;
      to_file = new FileOutputStream(tofile);

      // Now use the URL class to parse the user-specified URL into
      // its various parts: protocol, host, port, filename. Check the protocol
      URL url = new URL(fromurl);
      String protocol = url.getProtocol();
      if (!protocol.equals("http"))
        throw new IllegalArgumentException("URL must use 'http:' protocol");
      String host = url.getHost();
      int port = url.getPort();
      if (port == -1)
        port = 80; // if no port, use the default HTTP port
      String filename = url.getFile();
      // Open a network socket connection to the specified host and port
      System.out.println("Host = " + host);
      Socket socket = new Socket(host, port);
      // Get input and output streams for the socket
      InputStream from_server = socket.getInputStream();
      PrintWriter to_server = new PrintWriter(new OutputStreamWriter(socket
          .getOutputStream()));

      System.out.println("Host = " + socket.getInetAddress().getHostName());

      // Send the HTTP GET command to the Web server, specifying the file.
      // This uses an old and very simple version of the HTTP protocol
      to_server.println("GET " + filename);
      to_server.flush(); // Send it right now!

      // Now read the server's response, and write it to the file
      byte[] buffer = new byte[4096];
      int bytes_read;
      while ((bytes_read = from_server.read(buffer)) != -1)
        to_file.write(buffer, 0, bytes_read);

      // When the server closes the connection, we close our stuff
      socket.close();
      to_file.close();
    } catch (Exception e) { // Report any errors that arise
      System.err.println(e);
      System.err.println("Usage: java HttpClient <URL> [<filename>]");
    }
  }
}
