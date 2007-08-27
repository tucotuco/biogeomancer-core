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

import java.io.StringReader;
import java.util.List;

import org.apache.log4j.Logger;
import org.biogeomancer.records.Rec;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

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
	 * @param String
	 *            locality
	 * @param String
	 *            higherGeog
	 * @param String
	 *            interp return a string representation of a single georeference
	 */
	public String singleGeoreference(String locality, String higherGeog,
			String interp) {
		StringBuilder response = new StringBuilder();
		BgUtil.buildSingleGeoreference(locality, higherGeog, interp, response);
		return response.toString();
	}

	/**
	 * @param georef
	 *            return a string representation of a single georeference
	 *            reference by the vertical panel georef
	 * 
	 */
	public String singleGeoreference(VerticalPanel georef) {
		int total_ref = georef.getWidgetCount();
		Rec rec = new Rec();
		String interp = null;
		for (int i = 0; i < total_ref / 2; i++) {
			String georefName = ((Label) georef.getWidget(i)).getText();
			String georefVal = ((TextBox) georef.getWidget(i + 1)).getText();
			if (georefName.equals("interpreter"))
				interp = georefVal;
			else if (georefVal != null || georefVal != "")
				rec.put(georefName.toLowerCase(), georefVal);

		}
		StringBuilder response = new StringBuilder();
		BgUtil.buildSingleGeoreference(rec, interp, response);
		return response.toString();
	}

	/**
	 * @param String
	 *            xmlRequest: contain string presentation of an xml Request loop
	 *            each single georeference and return a string representation of
	 *            a batch georeference
	 */
	public String batchGeoreference(String xmlRequest) {
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
				BgUtil.buildSingleGeoreference(locality, higherGeography,
						interpreter, response);
			}
			return response.toString();
		} catch (DocumentException e) {
			e.printStackTrace();
			return null;
		}

	}
}
