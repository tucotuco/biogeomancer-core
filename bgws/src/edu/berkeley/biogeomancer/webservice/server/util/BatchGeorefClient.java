/**
 * 
 */
package edu.berkeley.biogeomancer.webservice.server.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * The BatchGeorefClient class connects to the BatchGeorefServlet web service.
 * 
 */
public class BatchGeorefClient {

  private static final String BATCH_URL = "http://bg.berkeley.edu/ws/batch";

  // "http://localhost:8088/edu.berkeley.biogeomancer.webservice.Client/batch";

  public static void main(String[] argv) throws IOException {
    BatchGeorefClient c = new BatchGeorefClient();
    if (argv.length == 0) {
      System.out.println("BatchGeorefClient requires an example batch request "
          + "file passed in as a command line argument.");
      System.exit(1);
    }
    String fname = argv[0];
    c.test(fname);
  }

  /**
   * Returns an example XML request from a file.
   * 
   * @return the example XML request as a String
   * @throws IOException
   */
  public String getBatchRequest(String fname) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(fname));
    StringBuilder sb = new StringBuilder();
    String line = null;
    while ((line = reader.readLine()) != null) {
      sb.append(line);
    }
    return sb.toString();
  }

  /**
   * Posts the request and prints the result.
   * 
   * @param fname the filename of the XML request.
   * @throws IOException
   */
  public void test(String fname) throws IOException {
    HttpClient client = new HttpClient();
    PostMethod post = new PostMethod(BATCH_URL);
    NameValuePair[] data = { new NameValuePair("b", getBatchRequest(fname)) };
    post.setRequestBody(data);

    try {
      int statusCode = client.executeMethod(post);

      if (statusCode != HttpStatus.SC_OK) {
        System.err.println("Method failed: " + post.getStatusLine());
      }

      InputStream in = post.getResponseBodyAsStream();
      System.out.print(toString(in));

    } catch (HttpException e) {
      System.err.println("Fatal protocol violation: " + e.getMessage());
      e.printStackTrace();
    } catch (IOException e) {
      System.err.println("Fatal transport error: " + e.getMessage());
      e.printStackTrace();
    } finally {
      // Release the connection.
      post.releaseConnection();
    }
  }

  /**
   * Writes the content of the input stream to a <code>String<code>.
   */
  private String toString(InputStream inputStream) throws IOException {
    String string;
    StringBuilder outputBuilder = new StringBuilder();
    if (inputStream != null) {
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(inputStream));
      while (null != (string = reader.readLine())) {
        outputBuilder.append(string).append('\n');
      }
    }
    return outputBuilder.toString();
  }
}
