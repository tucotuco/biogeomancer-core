/**
 * BioGeomancer.java is a SOAP service provider.
 * Copyright (C) 2006 BioGeomancer
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.biogeomancer.ws;

import java.io.*;
import java.util.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;
import com.megginson.sax.*;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.biogeomancer.validation.SpatialAttributeLookup;
import org.biogeomancer.managers.*;
import org.biogeomancer.records.*;
import org.diva.spatial.Grid;

public class BioGeomancer {
	private static Properties props;
	private static Logger log;
	
	/**
	 * Constructs a BioGeomancer object.
	 */
	public BioGeomancer() {
		loadprops();
		startlog();
	}
	
	/** 
	 * Loads the properties file.
	 */
	private void loadprops() {
		try {
			ClassLoader loader = SpatialAttributeLookup.class.getClassLoader();
			InputStream inputStream = 
				loader.getResourceAsStream("BioGeomancer.properties");
			props = new Properties();
			props.load(inputStream);
		}
		catch (Exception e) {
			log.error("could not read properties file: " + e.toString());
		}
	}
	
	/**
	 * Starts the log.
	 */
	private void startlog() {
		ConsoleAppender console = 
			new ConsoleAppender(new SimpleLayout(), ConsoleAppender.SYSTEM_OUT);
		log = Logger.getLogger(BioGeomancer.class);    
		//log.addAppender(console);
	}
	

	public String spatialAttributeLookup(String lng, String lat,
			String attribute) {
		SpatialAttributeLookup sal = new SpatialAttributeLookup();
		return sal.lookup(lng, lat, attribute);
	}

	public String spatialAttributeList() {
		SpatialAttributeLookup sal = new SpatialAttributeLookup();
		return "test"; //sal.getattributes();
	}

	public String Georeference(String dataurl, String datadelineator,
			String interpreter) {
		try {
			String uploadPath = props.getProperty("uploadpath");

			String delineator = null;
			if (datadelineator.equalsIgnoreCase("tab"))
				delineator = "\t";
			if (datadelineator.equalsIgnoreCase("comma"))
				delineator = ",";

			// download data and create the recset
			//HttpClient client = new HttpClient();
			String tofile = uploadPath + "/bg_datafile_"
					+ System.currentTimeMillis();
			//client.download(dataurl, tofile);
			RecSet recset = new RecSet(dataurl, datadelineator, tofile);

			// georeference the recset
			GeorefPreferences prefs = new GeorefPreferences();
			prefs.locinterp = interpreter;
			GeorefManager georefManager = new GeorefManager(recset);
			georefManager.georeference(prefs);
			if (recset != null)
				return outputResults(recset);
			return "Could not download RecSet from " + dataurl;
		}

		catch (GeorefManager.GeorefManagerException e) {
			e.printStackTrace();
			// TODO
		} catch (RecSet.RecSetException e) { // unable to create recset from file.
			e.printStackTrace();
		} catch (Exception e) { // unable to process uploaded file.
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * outputResults()
	 *
	 * Outputs results.
	 *
	 * @param response the http response object
	 * @param output the request's output parameter value.
	 * @param recset the recset generated with this request.
	 */
	private String outputResults(RecSet recset) {
		try {
			StringWriter xml = new StringWriter();
			DataWriter w = new DataWriter(xml);
			w.setIndentStep(2);
			w.startDocument();
			w.startElement("BioGeomancer");
			for (Rec rec : recset.recs) {
				w.startElement("Record");
				
				// Print out the rec hashmap properties.
				Iterator keys = rec.keySet().iterator();
				String name, value = null;
				while(keys.hasNext()) {
					name = (String) keys.next();
					value = (String) rec.get(name);
					w.dataElement(name, value);
				}

				w.endElement("Record");
			}
			w.endElement("BioGeomancer");
			w.endDocument();
			String result = xml.toString();
			if (result == null)
				return "Problem parsing RecSet";
			return result;
		} catch (Exception e) {
			System.out.println("Problem in outputResults() --> ");
			e.printStackTrace();
			return "Problem parsing RecSet";
		}
	}
}
