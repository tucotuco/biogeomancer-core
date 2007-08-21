package edu.berkeley.biogeomancer.webservice.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.biogeomancer.records.Georef;

import edu.berkeley.biogeomancer.webservice.util.BgUtil;

public class BgClient implements BioGeomancerClient {

  public static void main(String[] argv) throws MalformedURLException,
      IOException {
    String serviceUrl = "http://bg.berkeley.edu:8080/ws/batch";
    String data = "Hello world!";
    HttpURLConnection connection = (HttpURLConnection) (new URL(serviceUrl))
        .openConnection();

    connection.setDoInput(true);
    connection.setDoOutput(true);
    connection.setRequestMethod("POST");
    // connection.setRequestProperty("Content-Type", "application/atom+xml");

    // Post the data item
    OutputStream outputStream = connection.getOutputStream();
    outputStream.write(data.getBytes());
    outputStream.flush();
    outputStream.close();

    // Retrieve the output
    int responseCode = connection.getResponseCode();
    InputStream inputStream;
    if (responseCode == HttpURLConnection.HTTP_OK) {
      inputStream = connection.getInputStream();
    } else {
      inputStream = connection.getErrorStream();
    }

    // write the output to the console
    System.out.println(toString(inputStream));

  }

  /**
   * Writes the content of the input stream to a <code>String<code>.
   */
  private static String toString(InputStream inputStream) throws IOException {
    String string;
    StringBuilder outputBuilder = new StringBuilder();
    if (inputStream != null) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(
          inputStream));
      while (null != (string = reader.readLine())) {
        outputBuilder.append(string).append('\n');
      }
    }
    return outputBuilder.toString();
  }

  private final BgUtil bgUtil = new BgUtil();

  public List<Georef> georeference(File file) {
    // TODO...
    return null;
  }

  public List<Georef> georeference(String xmlRequest) {
    // TODO
    return null;
  }

  public Georef georeference(String locality, String higherGeography,
      String interpreter) {
    // TODO
    return null;
  }

  public void setServiceUrl(URL serviceUrl) {
    // TODO

  }
}
