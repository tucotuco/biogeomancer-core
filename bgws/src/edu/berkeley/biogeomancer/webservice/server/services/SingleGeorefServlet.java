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
package edu.berkeley.biogeomancer.webservice.server.services;

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.biogeomancer.records.Rec;

import edu.berkeley.biogeomancer.webservice.server.util.BgUtil;

/**
 * Web service for georeferencing a single locality.
 * 
 */
public class SingleGeorefServlet extends HttpServlet {

	Logger log = Logger.getLogger(SingleGeorefServlet.class);

	private final BgUtil bgUtil = new BgUtil();

	/**
	 * Georeferences a single locality using the BioGeomancer Core API. Returns
	 * all generated georeferences as XML.
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, java.io.IOException {

		response.setContentType("text/xml");
		// String locality = request.getParameter("l");
		// String higherGeography = request.getParameter("hg");
		String interpreter = request.getParameter("i");
		if (interpreter == null)
			interpreter = request.getParameter("interpreter");
		if (interpreter == null || interpreter.equals(""))
			interpreter = "yale";

		Rec recordMap = getParameters(request);

		/*
		 * log.info("Locality: " + locality + " HigherGeography: " +
		 * higherGeography + " Interpreter: " + interpreter);
		 */

		PrintWriter out = response.getWriter();

		// Build the XML header.
		out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		out
				.println("<biogeomancer xmlns:dwc=\"http://rs.tdwg.org/tapir/1.0\">");

		out.println("<interpreter>" + interpreter + "</interpreter>");
		// BgUtil.buildSingleXmlText(locality, higherGeography, interpreter,
		// out);
		BgUtil.buildSingleXmlText(recordMap, interpreter, out);
		out.println("</biogeomancer>");
	}

	/**
	 * 
	 * @param request
	 * @return Rec base on given request helper function for doGet get support
	 *         url parameters: l,locality = locality hg,highergeography =
	 *         highergeography cy, country = country s, stateprovince =
	 *         stateprovince co, county = county vlat, verbatimlatitude =
	 *         verbatimlatitude vlng, verbatimlongitude = verbatimlongitude is,
	 *         island = island ig, islandgroup = islandgroup w, waterbody =
	 *         waterbody c, continent = continent i, interpreter = interpreter
	 *         (this does not get added to the record)
	 */
	@SuppressWarnings("unchecked")
	private Rec getParameters(HttpServletRequest request) {
		Rec rec = new Rec();
		HashMap<String, String> supportParameters = BgUtil.supportpParameters();
		Map<String, String[]> paramValues = request.getParameterMap();
		Enumeration<String> paramNames = request.getParameterNames();
		// paramValues.remove("i");
		// paramValues.remove("interpreter");
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			String supportParam = supportParameters.get(paramName);
			if (supportParam != null) {
				String paramVal = paramValues.get(paramName)[0];
				rec.put(supportParam, paramVal);

			}
		}

		/*
		 * String requestParam = request.getParameter("l"); if (requestParam ==
		 * null) requestParam = request.getParameter("locality"); put(rec,
		 * "locality", requestParam);
		 * 
		 * requestParam = request.getParameter("hg"); if (requestParam == null)
		 * requestParam = request.getParameter("highergeography"); put(rec,
		 * "highergeography", requestParam);
		 * 
		 * requestParam = request.getParameter("cy"); if (requestParam == null)
		 * requestParam = request.getParameter("country"); put(rec, "country",
		 * requestParam);
		 * 
		 * requestParam = request.getParameter("s"); if (requestParam == null)
		 * requestParam = request.getParameter("stateprovince"); put(rec,
		 * "stateprovince", requestParam);
		 * 
		 * requestParam = request.getParameter("co"); if (requestParam == null)
		 * requestParam = request.getParameter("county"); put(rec, "county",
		 * requestParam);
		 * 
		 * requestParam = request.getParameter("vlat"); if (requestParam ==
		 * null) requestParam = request.getParameter("verbatimlatitude");
		 * put(rec, "verbatimlatitude", requestParam);
		 * 
		 * requestParam = request.getParameter("vlng"); if (requestParam ==
		 * null) requestParam = request.getParameter("verbatimlongitude");
		 * put(rec, "verbatimlongitude", requestParam);
		 * 
		 * requestParam = request.getParameter("is"); if (requestParam == null)
		 * requestParam = request.getParameter("island"); put(rec, "island",
		 * requestParam);
		 * 
		 * requestParam = request.getParameter("ig"); if (requestParam == null)
		 * requestParam = request.getParameter("islandgroup"); put(rec,
		 * "islandgroup", requestParam);
		 * 
		 * requestParam = request.getParameter("w"); if (requestParam == null)
		 * requestParam = request.getParameter("waterbody"); put(rec,
		 * "waterbody", requestParam);
		 * 
		 * requestParam = request.getParameter("cn"); if (requestParam == null)
		 * requestParam = request.getParameter("continent"); put(rec,
		 * "continent", requestParam);
		 */

		return rec;
	}

	/**
	 * @param recMapping
	 * @param key
	 * @param value
	 *            helper function for getParamerters: put the value along with
	 *            its key Record if value != null
	 */
	private void put(Rec recMapping, String key, String value) {
		if (value != null)
			recMapping.put(key, value);
	}
}
