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
package edu.berkeley.biogeomancer.webservice;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.biogeomancer.records.Georef;

import edu.berkeley.biogeomancer.webservice.util.BgUtil;
import edu.berkeley.biogeomancer.webservice.util.CustomNamespaceDocument;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.net.MalformedURLException;
import java.net.URL;
/**
 * Web service for batch georeferencing using the BioGeomancer Core API.
 * 
 */
public class BatchGeoreferenceWebService extends HttpServlet {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

Logger log = Logger.getLogger(BatchGeoreferenceWebService.class);

//  private final BgUtil bgUtil = new BgUtil();
	/**
	 * Georeferences a batch locality using the BioGeomancer Core API. Returns
	 * all generated georeferences as XML.
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, java.io.IOException {
    System.out.println("doPost...");
    response.setContentType("text/html");

    BufferedReader reader = request.getReader();
    PrintWriter out = response.getWriter();
    
    out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    out.println("<biogeomancer xmlns:dwc=\"http://rs.tdwg.org/tapir/1.0\">");
    
    parseXml(reader, out);
    out.println("</biogeomancer>");
    out.close();
	}
  
  @SuppressWarnings("unchecked")
  /**
   * @param Reader read
   * @param PrintWriter out
   * read xml text from Reader read and output (using PrintWriter out) just as like
   * Batch output at http://code.google.com/p/biogeomancer-core/wiki/WebServicesRequirements
   */
  private void parseXml(Reader read, PrintWriter out)
  {
	  SAXReader reader = new SAXReader();
	  try {
		Document xmlDoc  = reader.read(read);
		CustomNamespaceDocument cnd = new CustomNamespaceDocument(xmlDoc);
		List<Node> recordNodes = cnd.getNodes("record");
		Element requestElement = recordNodes.get(0).getParent();
		
		String interpreter = requestElement.attributeValue("interpreter");
		out.println("<records interpreter=\"" + interpreter + "\">");
		for (Node recordNode : recordNodes) {
			out.println("<record>");
			Node localityNode = recordNode.selectSingleNode("dwc:Locality");
			String locality= localityNode.getStringValue();
			Node higherGeogNode = recordNode.selectSingleNode("dwc:HigherGeography");
			String higherGeography = higherGeogNode.getStringValue();
			BgUtil.buildSingleXmlText(locality, higherGeography, interpreter, out);
			out.println("</record>");
		}
		out.println("</records>");
	} catch (DocumentException e) {
		e.printStackTrace();
	}
	
  }
}
