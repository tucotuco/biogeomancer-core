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

/*
 * Created on May 2, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package edu.yale.ws;

/**
 * @author youjun
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 * 
 * 
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.encoding.XMLType;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.JAFDataHandlerDeserializerFactory;
import org.apache.axis.encoding.ser.JAFDataHandlerSerializerFactory;
import org.biogeomancer.managers.GeorefManager;
import org.biogeomancer.managers.GeorefPreferences;
import org.biogeomancer.records.RecSet;

public class BGService {

  public static void main(String[] args) {
    // this main method is a sample service client with service url hard-coded
    // to yale server,
    // you may want to add the service url as a para.

    DataHandler dhSource = new DataHandler(new FileDataSource(args[0]));
    QName qnameAttachment = new QName("DataHandler");

    try {
      Service service = new Service();
      Call call = (Call) service.createCall();

      call.removeAllParameters();
      call
          .setTargetEndpointAddress("http://georef22.peabody.yale.edu:8080/axis/services/BGService");
      call.setOperationName("georeference");

      call.registerTypeMapping(dhSource.getClass(), qnameAttachment,
          JAFDataHandlerSerializerFactory.class,
          JAFDataHandlerDeserializerFactory.class);

      call.addParameter("interpreter", XMLType.XSD_STRING, ParameterMode.IN);
      call.addParameter("dh", qnameAttachment, ParameterMode.IN);

      call.setReturnType(qnameAttachment);
      DataHandler dh = null;

      // dh = (DataHandler) call.invoke(new Object[] { args[2], dhSource });

      dh = new BGService().georeference("yale", dhSource);

      FileOutputStream fos = new FileOutputStream(new File(args[1]));
      dh.writeTo(fos);
      fos.flush();
      fos.close();
    } catch (Exception e) {
      System.out.println(e.toString());
    }

  }

  // only for now, this localpath string should be listed in a property file
  // private String localpath = "/home/tomcat/jakarta-tomcat-5.5.8/temp";
  private final String localpath = "c:\\temp";

  public DataHandler georeference(String interpreter, DataHandler indh) {
    String filename = "f" + System.currentTimeMillis();
    File f = new File(localpath);
    File[] fa = f.listFiles();
    if (fa != null) {
      for (int i = 0; i < fa.length; i++) {
        if (fa[i].lastModified() == 0L)
          fa[i].delete();
      }
    }
    if (indh == null)
      System.out.println("no input attachment found");
    File inFile = new File(localpath, filename + ".txt");

    try {
      inFile.createNewFile();
      FileOutputStream fos = new FileOutputStream(inFile);
      indh.writeTo(fos);
      fos.flush();
      fos.close();
    } catch (IOException e) {

      System.out.println("failed to get input file");
    }

    File outFile = new File(localpath, filename + ".xml");

    try {
      RecSet rs = new RecSet(inFile.getCanonicalPath(), "\t");

      GeorefManager gm = new GeorefManager(rs, true);
      gm.newGeoreference(new GeorefPreferences(interpreter));

      Writer out = new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream(outFile), "UTF-8"));
      out.write("<?xml version='1.0' encoding='utf-8'?>" + rs.toMarkup());
      out.flush();
      out.close();
    } catch (Exception e) {

      System.out.println("failed to call bg engine:");
      e.printStackTrace();
    }

    try {
      inFile.setLastModified(0L);
      inFile.delete();
    } catch (Exception e) {
      System.out.println("failed to delete input file");
    }

    DataHandler outdh = null;

    try {
      outdh = new DataHandler(new FileDataSource(outFile));
      outFile.setLastModified(0L);
    } catch (Exception e) {
    }

    return outdh;
  }
}
