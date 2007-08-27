package edu.berkeley.biogeomancer.webservice.server.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.biogeomancer.managers.GeorefManager;
import org.biogeomancer.managers.GeorefPreferences;
import org.biogeomancer.managers.GeorefManager.GeorefManagerException;
import org.biogeomancer.records.Georef;
import org.biogeomancer.records.Rec;
import org.biogeomancer.records.RecSet;
import org.biogeomancer.records.RecSet.RecSetException;

/**
 * Utility class for wrapping BioGeomancer Core API.
 */
public class BgUtil {

	private static Logger log = Logger.getLogger(BgUtil.class);

	/**
	 * 
	 * @param String
	 *            locality
	 * @param String
	 *            higherGeography
	 * @param String
	 *            interpreter
	 * @param String
	 *            out create a list of Georefs from locality, higherGeography,
	 *            and interpreter output xml tag and value for these
	 *            georeferences in format: <dwc:Locality>LocalityName</dwc:Locality>
	 *            <dwc:HigherGeography>HigherGeographyName</dwc:HigherGeography>
	 *            <georeference> <dwc:DecimalLatitude>value</dwc:DecimalLatitude>
	 *            <dwc:DecimalLongitude>value</dwc:DecimalLongitude>
	 *            <dwc:CoordinateUncertaintyInMeters>value</dwc:CoordinateUncertaintyInMeters>
	 *            </georeference> more georeferences here if there are more
	 */
	public static void buildSingleXmlText(String locality,
			String higherGeography, String interpreter, PrintWriter out) {
		log.info("Locality: " + locality + " HigherGeography: "
				+ higherGeography + " Interpreter: " + interpreter);
		out.println("<dwc:Locality>" + locality + "</dwc:Locality>");
		out.println("<dwc:HigherGeography>" + higherGeography
				+ "</dwc:HigherGeography>");
		List<Georef> georefs = georeference(locality, higherGeography,
				interpreter);
		for (Georef g : georefs) {
			out.println("<georeference>");
			out.println("<dwc:DecimalLatitude>" + g.pointRadius.y
					+ "</dwc:DecimalLatitude>");
			out.println("<dwc:DecimalLongitude>" + g.pointRadius.x
					+ "</dwc:DecimalLongitude>");
			out.println("<dwc:CoordinateUncertaintyInMeters>"
					+ g.pointRadius.extent
					+ "</dwc:CoordinateUncertaintyInMeters>");
			out.println("</georeference>");
		}
	}

	/**
	 * @param Rec
	 *            rec
	 * @param String
	 *            interpreter (for georeference)
	 * @param PrintWiter
	 *            out: to output xml text to the browser build xml text base on
	 *            record rec
	 * 
	 */
	public static void buildSingleXmlText(Rec rec, String interpreter,
			PrintWriter out) {
		Iterator<String> georefFields = rec.keySet().iterator();
		// building dwc header for each georeference
		while (georefFields.hasNext()) {
			String georefField = georefFields.next();
			String georefValue = rec.get(georefField);
			log.info(georefField + ": " + georefValue + " ");
			String dwcFieldTag = "dwc:" + georefField + ">";
			out.println("<" + dwcFieldTag + georefValue + "</" + dwcFieldTag);
		}
		List<Georef> georefs = georeference(rec, interpreter);
		// display DecimalLatitude DecimalLongitude
		// CoordinateUncertaintyInMeters
		// values in xml format
		for (Georef g : georefs) {
			out.println("<georeference>");
			out.println("<dwc:DecimalLatitude>" + g.pointRadius.y
					+ "</dwc:DecimalLatitude>");
			out.println("<dwc:DecimalLongitude>" + g.pointRadius.x
					+ "</dwc:DecimalLongitude>");
			out.println("<dwc:CoordinateUncertaintyInMeters>"
					+ g.pointRadius.extent
					+ "</dwc:CoordinateUncertaintyInMeters>");
			out.println("</georeference>");
		}
	}

	/**
	 * 
	 * @param String
	 *            locality
	 * @param String
	 *            higherGeography
	 * @param String
	 *            interpreter
	 * @param StringBuilder
	 *            response generate single georeference and append it to
	 *            response
	 */
	public static void buildSingleGeoreference(String locality,
			String higherGeography, String interpreter, StringBuilder response) {
		log.info("Locality: " + locality + " HigherGeography: "
				+ higherGeography + " Interpreter: " + interpreter);
		List<Georef> georefs = georeference(locality, higherGeography,
				interpreter);
		response.append("Locality: " + locality + " HigherGeography: "
				+ higherGeography + " Interpreter: " + interpreter + "<BR>");
		for (Georef g : georefs) {
			response.append("Georeference: ");
			response.append("DecimalLatitude=" + g.pointRadius.y + "<BR>");
			response.append("DecimalLongitude=" + g.pointRadius.x + "<BR>");
			response.append("CoordinateUncertaintyInMeters="
					+ g.pointRadius.extent + "<P>");
		}
	}

	/**
	 * 
	 * @param rec
	 * @param interp
	 * @param response
	 *            build single georeference base on given Rec, and interpreter
	 *            String representation of the single georeference is return by
	 *            reference as response
	 */
	public static void buildSingleGeoreference(Rec rec, String interp,
			StringBuilder response) {

		Iterator<String> georefFields = rec.keySet().iterator();
		// building dwc header for each georeference
		while (georefFields.hasNext()) {
			String georefField = georefFields.next();
			String georefValue = rec.get(georefField);
			log.info(georefField + ": " + georefValue + " ");
			response.append(georefField + ": " + georefValue + "; ");

		}
		response.append("<BR>");
		List<Georef> georefs = georeference(rec, interp);
		// display DecimalLatitude DecimalLongitude
		// CoordinateUncertaintyInMeters
		// values in xml format
		for (Georef g : georefs) {
			response.append("Georeference: ");
			response.append("DecimalLatitude=" + g.pointRadius.y + "<BR>");
			response.append("DecimalLongitude=" + g.pointRadius.x + "<BR>");
			response.append("CoordinateUncertaintyInMeters="
					+ g.pointRadius.extent + "<P>");
		}
	}

	/**
	 * Georeferences the list of recs using BioGeomancer Core API.
	 * 
	 * @param recs
	 *            the list of Rec objects to georeference
	 * @return the georeferences
	 */
	public static List<Georef> georeference(String FileName, String interpreter) {
		// take FileName argument and interperter name
		// get RecSet from the file and return Georef List
		// has not tested yet
		try {
			RecSet referenceSet = new RecSet(FileName, "\t");
			// Iterator<Rec> recIter = referenceSet.recs.iterator();
			List<Georef> recsList = new ArrayList<Georef>();
			for (Iterator<Rec> recIter = referenceSet.recs.iterator(); recIter
					.hasNext();) {
				Rec currentRec = recIter.next();
				GeorefManager gm;
				try {
					gm = new GeorefManager();
					gm.georeference(currentRec, new GeorefPreferences(
							interpreter));
					recsList.addAll(currentRec.georefs);
				} catch (GeorefManagerException e) {
					// TODO: Logging error to an error log
					e.printStackTrace();
				}

			}
			return recsList;
		} catch (RecSetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Returns a list of Georef objects generated using BioGeomancer Core API.
	 * If there is an error or if no georeferences were generated, returns null.
	 * 
	 * @param locality
	 *            the locality to georeference
	 * @param higherGeography
	 *            the higher geography to georeference
	 * @param interpreter
	 *            the BioGeomancer locality intepreter to use
	 * @return List<Georef> the generated georeferences
	 */
	public static List<Georef> georeference(String locality,
			String higherGeography, String interpreter) {

		// Default interpreter is Yale.
		if (interpreter == null || interpreter.equals("")) {
			interpreter = "yale";
		}

		final Rec rec = new Rec();
		rec.put("locality", locality);
		rec.put("highergeography", higherGeography);
		GeorefManager gm;
		try {
			gm = new GeorefManager();
			gm.georeference(rec, new GeorefPreferences(interpreter));
			return rec.georefs;
		} catch (GeorefManagerException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param rec
	 * @param interpreter
	 * @return list of Georef from given record and interpreter
	 */
	public static List<Georef> georeference(Rec rec, String interpreter) {
		GeorefManager gm;
		try {
			gm = new GeorefManager();
			gm.georeference(rec, new GeorefPreferences(interpreter));
			return rec.georefs;
		} catch (GeorefManagerException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 
	 * @return HashMap of support Parameters url parameters: l,locality =
	 *         locality hg,highergeography = highergeography cy, country =
	 *         country s, stateprovince = stateprovince co, county = county
	 *         vlat, verbatimlatitude = verbatimlatitude vlng, verbatimlongitude =
	 *         verbatimlongitude is, island = island ig, islandgroup =
	 *         islandgroup w, waterbody = waterbody c, continent = continent i,
	 *         interpreter = interpreter (this does not get added to the record)
	 */
	public static HashMap<String, String> supportpParameters() {
		HashMap<String, String> supParameters = new HashMap<String, String>();

		supParameters.put("l", "locality");
		supParameters.put("locality", "locality");

		supParameters.put("hg", "highergeography");
		supParameters.put("highergeography", "highergeography");

		supParameters.put("cy", "country");
		supParameters.put("country", "country");

		supParameters.put("s", "stateprovince");
		supParameters.put("stateprovince", "stateprovince");

		supParameters.put("co", "county");
		supParameters.put("county", "county");

		supParameters.put("vlat", "verbatimlatitude");
		supParameters.put("verbatimlatitude", "verbatimlatitude");

		supParameters.put("vlng", "verbatimlongitude");
		supParameters.put("verbatimlongitude", "verbatimlongitude");

		supParameters.put("is", "island");
		supParameters.put("island", "island");

		supParameters.put("ig", "islandgroup");
		supParameters.put("islandgroup", "islandgroup");

		supParameters.put("w", "waterbody");
		supParameters.put("waterbody", "waterbody");

		supParameters.put("c", "continent");
		supParameters.put("continent", "continent");

		return supParameters;

	}

	/**
	 * 
	 * @param fileName
	 * @param data
	 *            write string of data to a file
	 */
	public static void recordToFile(String fileName, String data) {
		try {
			BufferedWriter buff = new BufferedWriter(new FileWriter(fileName));
			buff.write(data);
			buff.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
