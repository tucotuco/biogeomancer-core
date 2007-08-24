/*
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
package edu.berkeley.biogeomancer.webservice.server.services.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.StringReader;
import java.io.Reader;
import java.util.List;

import org.apache.log4j.Logger;
import org.biogeomancer.records.Georef;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
//import com.sun.org.apache.xerces.internal.parsers.XMLParser;
//import com.google.gwt.xml.client.Document;
//import com.google.gwt.xml.client.XMLParser;

import edu.berkeley.biogeomancer.webservice.client.services.controller.ControllerService;
import edu.berkeley.biogeomancer.webservice.server.util.BgUtil;
import edu.berkeley.biogeomancer.webservice.server.util.CustomNamespaceDocument;

/**
 * Implements the server side asynchronous controller service specification.
 * 
 * @see edu.berkeley.cache.client.services.controller.ControllerService
 */

/**
 * @author aaron
 * 
 */
public class ControllerServiceImpl extends RemoteServiceServlet implements
    ControllerService {

  private static final Logger log = Logger
      .getLogger(ControllerServiceImpl.class);

  /**
   * @param String locality
   * @param String higherGeog
   * @param String interp
   * return a string representation of a single georeference
   */
  public String georeference(String locality, String higherGeog, String interp) {
    StringBuilder response = new StringBuilder();    
    BgUtil.buildSingleGeoreference(locality, higherGeog, interp, response);
    return response.toString();
  }
  /**
   * @param String xmlRequest: contain string presentation of an xml Request
   * loop each single georeference and return a string representation of a batch georeference
   */
  public String batchGeoreference(String xmlRequest)
  {
	  StringBuilder response = new StringBuilder();
	  SAXReader reader = new SAXReader();
	    try {
	      Document xmlDoc = reader.read(new StringReader(xmlRequest));
	      CustomNamespaceDocument cnd = new CustomNamespaceDocument(xmlDoc);
	      List<Node> recordNodes = cnd.getNodes("record");
	      Element requestElement = recordNodes.get(0).getParent();

	      String interpreter = requestElement.attributeValue("interpreter");
	      for (Node recordNode : recordNodes) {
	        Node localityNode = recordNode.selectSingleNode("dwc:Locality");
	        String locality = localityNode.getStringValue();
	        Node higherGeogNode = recordNode
	            .selectSingleNode("dwc:HigherGeography");
	        String higherGeography = higherGeogNode.getStringValue();
	        BgUtil.buildSingleGeoreference(locality, higherGeography, interpreter, response);
	      }
	      return response.toString();
	    } catch (DocumentException e) {
	      e.printStackTrace();
	      return null;
	    }
	 
  }
}
