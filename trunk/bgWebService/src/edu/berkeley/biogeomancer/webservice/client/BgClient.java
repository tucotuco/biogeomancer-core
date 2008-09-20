package edu.berkeley.biogeomancer.webservice.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.biogeomancer.records.Georef;
import org.biogeomancer.records.Rec;

import edu.berkeley.biogeomancer.webservice.util.BgUtil;

public class BgClient implements BioGeomancerClient {
  public static String data = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
      + "<biogeomancer xmlns=\"http://bg.berkeley.edu\" xmlns:dwcore=\"http://rs.tdwg.org/dwc/dwcore\" xmlns:dwgeo=\"http://rs.tdwg.org/dwc/dwgeo\">"
      + "<request type=\"batch\" interpreter=\"yale\">" + "<record>"
      + "<dwcore:Locality>Berkeley</dwcore:Locality>"
      + "<dwcore:StateProvince>California</dwcore:StateProvince>" + "</record>"
      + "<record>" + "<dwcore:Locality>Stuttgart</dwcore:Locality>"
      + "<dwcore:Country>Germany</dwcore:Country>" + "</record>" + "<record>"
      + "<dwcore:Locality>3 mi E Lolo</dwcore:Locality>"
      + "<dwcore:County>Missoula</dwcore:County>" + "</record>" + "</request>"
      + "</biogeomancer>";

  public static void main(String[] argv) throws MalformedURLException,
      IOException {
    BgClient bgClient = new BgClient();
    // String serviceUrl = "http://localhost:8080/ws-test/batch";
    String serviceUrl = "http://bg.berkeley.edu:8080/ws-test/batch";

    URL connectUrl = new URL(serviceUrl);
    bgClient.setServiceUrl(connectUrl);
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

  public List<Georef> georeference(Rec r, String interpreter) {
    return BgUtil.georeference(r, interpreter);
  }

  /**
   * @param URL
   *          service URL connect to URL: set URL Method to POST and write batch
   *          interface for doPost to read the interface is in String data
   *          (class static variable) write the xml text to a file, default file
   *          name is autoGenerate.xml store in current working directory
   */
  public void setServiceUrl(URL serviceUrl) {
    // TODO
    HttpURLConnection connection;
    try {
      connection = (HttpURLConnection) serviceUrl.openConnection();
      connection.setDoInput(true);
      connection.setDoOutput(true);
      connection.setRequestMethod("POST");

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
      String xmlText = toString(inputStream);
      System.out.println(xmlText);
      BgUtil.recordToFile("autoGenerate.xml", xmlText);
      connection.disconnect();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
