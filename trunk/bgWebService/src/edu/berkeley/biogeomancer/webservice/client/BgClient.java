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
  public static String data = 
	  "<?xml version=\"1.0\" encoding=\"utf-8\"?>" + 
      "<biogeomancer xmlns=\"http://bg.berkeley.edu\" xmlns:dwc=\"http://rs.tdwg.org/tapir/1.0\">" + 
      "<request type=\"batch\" interpreter=\"Yale\">" +
      "<record>" +
      "<dwc:Locality>Berkeley</dwc:Locality>" +
      "<dwc:HigherGeography>California</dwc:HigherGeography>" +
      "</record>" +
      "<record>" +
      "<dwc:Locality>Stuttgart</dwc:Locality>" +
      "<dwc:HigherGeography>Germany</dwc:HigherGeography>" +
      "</record>" +
      "<record>" +
      "<dwc:Locality>St. Petersburg</dwc:Locality>" +
      "<dwc:HigherGeography>Russia</dwc:HigherGeography>" +
      "</record>" +
      "</request>" +
      "</biogeomancer>";
  public static void main(String[] argv) throws MalformedURLException,
      IOException {
	BgClient bgClient = new BgClient();
    String serviceUrl = "http://localhost:8080/ws/batch";
    URL connectUrl = new URL(serviceUrl);
    bgClient.setServiceUrl(connectUrl);
    /*HttpURLConnection connection = (HttpURLConnection) (new URL(serviceUrl)) .openConnection();

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
    System.out.println(toString(inputStream));*/

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

 // private final BgUtil bgUtil = new BgUtil();

  public List<Georef> georeference(String fileName, String interpreter) {
    return BgUtil.georeference(fileName, interpreter);
  }

  public List<Georef> georeference(String xmlRequest) {
    // TODO
    return null;
  }

  public List<Georef> georeference(String locality, String higherGeography,
      String interpreter) {
    return BgUtil.georeference(locality, higherGeography, interpreter);
  }
  /**
   * @param URL service URL
   * connect to URL: set URL Method to POST and write batch interface for doPost to read
   * the interface is in String data (class static variable)
   */
  public void setServiceUrl(URL serviceUrl){
    // TODO
	  HttpURLConnection connection;
	try {
		connection = (HttpURLConnection) serviceUrl .openConnection();
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
		System.out.println(toString(inputStream));
	} catch (IOException e) {
		e.printStackTrace();
	} 
  }
}
