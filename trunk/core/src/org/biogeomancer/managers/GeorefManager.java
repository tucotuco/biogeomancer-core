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

package org.biogeomancer.managers;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.biogeomancer.managers.DatumManager.Datum;
import org.biogeomancer.records.Clause;
import org.biogeomancer.records.Georef;
import org.biogeomancer.records.Rec;
import org.biogeomancer.records.RecSet;
import org.biogeomancer.utils.PointRadius;
import org.biogeomancer.utils.SupportedLanguages;

import edu.tulane.bg_geolocate;
import edu.tulane.bg_geolocate.bg_geolocate_Exception;
import edu.uiuc.BGI.Hmm.BGIHmm;
import edu.uiuc.BGI.Hmm.BGIHmm.BGIHmmException;
import edu.yale.GBI.BGI;
import edu.yale.GBI.BGI.BGIException;

public class GeorefManager extends BGManager {

	public class GeorefManagerException extends Exception {
		GeorefManagerException(Exception exception) {
			super(exception);
		}

		GeorefManagerException(String errormsg) {
			super(errormsg);
		}

		GeorefManagerException(String errormsg, Exception exception) {
			super(errormsg, exception);
		}
	}

	public static String PROPS_FILE = "GeorefManager.properties";
	private static final Logger log = Logger.getLogger(GeorefManager.class);
	private static Properties props = new Properties();

	static {
		initProps(PROPS_FILE, props);
	}

	public static void main(String[] args) throws Exception {
		int test = 0;
		String arg1 = null;
		String arg2 = null;

		// choose test to run by one of the following integers in the first argument
		final int INTERP = 1; // no args
		final int DIFF_IO = 2; // no args
		final int DIFF_INTERP = 3; // no args
		final int INTERP_TULANE = 4; // no args
		final int INTERP_YALE = 5; // fieldname
		final int INTERP_UIUC = 6; // fieldname
		final int YALE_FIRST = 7; // no args
		final int UIUC_FIRST = 8; // no args
		final int TULANE_FIRST = 9; // no args
		final int SAME_INTERP = 10; // whatever, "showorig"
		final int FIND_LOCTYPE = 11; // LocType
		final int COUNT_LOCTYPE = 12; // interpreter ("yale", "uiuc", "tulane"),
		// fieldname
		final int TO_MARKUP = 13; // no args
		final int NEW_GEOREF = 14; // interpreter ("yale", "uiuc", "tulane")
		final int SINGLE_GEOREF = 15; // interpreter ("yale", "uiuc", "tulane")
		final int INTERP_NEWYALE = 16; // fieldname or "all", language
		final int INTERP_FORFEATURENAMES = 17; // interpreter
		final int CREATE_USERFEATURES = 18; // interpreter
		final int REMOVE_USERFEATURES = 19; // feature_id (-1 for all features)
		if (args.length > 0) {
			Integer z = new Integer(args[0]);
			test = z.intValue();
		}
		if (args.length > 1) {
			arg1 = new String(args[1]);
		}
		if (args.length > 2) {
			arg2 = new String(args[2]);
		}

		// test the georef manager
		GeorefManager gm = new GeorefManager();
		RecSet rs = null;

		// Grab test data from bg.config and create RecSet from it.
		String localData, remoteData, downloadData = null;
		localData = gm.props.getProperty("testdata.local.georefmanager");
		remoteData = gm.props.getProperty("testdata.remote.georefmanager");
		downloadData = gm.props.getProperty("downloads");
		if (localData != null) {
			rs = new RecSet(localData, "tab");
		} else if (remoteData != null) {
			if (downloadData != null)
				rs = new RecSet(remoteData, "tab", downloadData);
			else
				System.out
				.println("No download location configured in "
						+ System.getProperty("user.home")
						+ "/bg.config... Please define downloads as a path to where these files should be stored.");
		} else
			System.out
			.println("No test data configured in "
					+ System.getProperty("user.home")
					+ "/bg.config... Please define testdata.local.georefmanager or testdata.remote.georefmanager.");

		gm.setRecSet(rs);
		GeorefPreferences gp = null;
		if (arg1 == null)
			gp = new GeorefPreferences("all");
		else
			gp = new GeorefPreferences(arg1);

		long starttime = System.currentTimeMillis();
		switch (test) {
		case INTERP:
			System.out.println("***INTERPRETATION test***");
			gm.testInterpretation();
			break;
		case DIFF_IO:
			System.out.println("***INPUT/OUTPUT DIFFERENCE test***");
			gm.diffInputOutput();
			break;
		case DIFF_INTERP:
			System.out.println("***DIFFERENT INTERPRETATIONS test***");
			gm.diffInterpretations();
			break;
		case SAME_INTERP:
			System.out.println("***SAME INTERPRETATIONS test***");
			if (arg2 != null && arg2.equalsIgnoreCase("showorig"))
				gm.sameInterpretations(true);
			else
				gm.sameInterpretations(false);
			break;
		case YALE_FIRST:
			System.out.println("***GEOREFERENCE YALE FIRST test***");
			gm.georeferenceAllYaleFirst();
			break;
		case UIUC_FIRST:
			System.out.println("***GEOREFERENCE UIUC FIRST test***");
			gm.georeferenceAllUIUCFirst();
			break;
		case TULANE_FIRST:
			System.out.println("***GEOREFERENCE TULANE FIRST test***");
			gm.georeferenceAllTulaneFirst();
			break;
		case INTERP_YALE:
			System.out.println("***YALE INTERPRETATION test***");
			gm.interpretYale(arg1); // parsefieldname or "all"
			break;
		case INTERP_NEWYALE:
			System.out.println("***NEW YALE INTERPRETATION test***");
			gm.newInterpretYale(arg1, arg2); // parsefieldname or "all", language of
			// original text ("english", spanish",
			// "portuguese", "french")
			break;
		case INTERP_UIUC:
			System.out.println("***UIUC INTERPRETATION test***");
			gm.interpretUIUC(arg1);
			break;
		case INTERP_TULANE:
			System.out.println("***TULANE INTERPRETATION test***");
			gm.interpretTulane();
			break;
		case FIND_LOCTYPE:
			System.out.println("***LOCTYPE SEARCH test: LocType " + arg1 + " ***");
			gm.findLocTypeInterpretations(arg1);
			break;
		case COUNT_LOCTYPE:
			System.out.println("***LOCTYPE COUNT test***");
			gm.locTypeCounts(arg1, args[2]); // arg1 (e.g., "yale"),
			// args[2]=interpfield (e.g.,
			// "Locality")
			break;
		case TO_MARKUP:
			System.out
			.println("***GEOREFERENCE MARKUP test: " + gp.locinterp + "***");
			gm.georeference(gp);
			String recsetstring = new String(gm.recset.toMarkup());
			System.out.println(recsetstring);
			break;
		case NEW_GEOREF:
			System.out.println("***NEWGEOREFERENCE test***");
			gp.setLanguage(arg2);
			gm.newGeoreference(gp);
			for(Rec r : gm.recset.recs){
//				System.out.println(r.toString());
				System.out.println(r.getSummary("  "));
			}
			break;
		case SINGLE_GEOREF:
			System.out.println("***SINGLE GEOREFERENCE test***");
			for (Rec r : gm.recset.recs) {
				gm.georeference(r, gp);
				System.out.println(r.toXML(false));
			}
			break;
		case INTERP_FORFEATURENAMES:
			System.out.println("***FEATURENAMES test***");
			gm.getFeatureNames(gp);
			break;
		case CREATE_USERFEATURES:
			System.out.println("***CREATE_USERFEATURE test***");
			gm.newGeoreference(gp);
			for(Rec r : gm.recset.recs){
				gm.loadUserFeatures(r);
			}
			break;
		case REMOVE_USERFEATURES:
			if (arg1 == null)
				System.out.println("REMOVE_USERFEATURE requires feature_id as an input parameter (-1 to delete all user features from the BG Gazetteer.");
			else{
				Integer fid = new Integer(arg1);
				int featureid = fid.intValue();
				gm.removeUserFeature(featureid);
				System.out.println("***REMOVE_USERFEATURE test***");
			}
			break;
		default:
			System.out.println("***GEOREFERENCE test***");
		gm.georeference(gp);
		break;
		}

		long endtime = System.currentTimeMillis();
		log.info("\nElapsed test time: " + (endtime - starttime) + " (ms)\n");
		gm.shutdown();
	}

	private RecSet recset;
	private BGI yaleLocInterp;
	private BGIHmm uiucLocInterp;
	private SpatialDescriptionManager spatialDescriptionManager;

	bg_geolocate TULocInterp;

	public GeorefManager() throws GeorefManager.GeorefManagerException {
		init();
	}

	public GeorefManager(RecSet recset) // Remember to call shutdown() when
	// finished with a GeorefManager.
	throws GeorefManager.GeorefManagerException {
		this.recset = recset;
		init();
	}

	public void diffInputOutput() {
		for (int i = 0; i < this.recset.recs.size(); i++) {
			Rec rec = this.recset.recs.get(i);
			System.out.println(this.recset.recs.get(i).georefs.size()
					+ " georefs for\t" + this.recset.recs.get(i).uFullLocality);
			for (int j = 0; j < this.recset.recs.get(i).georefs.size(); j++) {
				Georef g = this.recset.recs.get(i).georefs.get(j);
				System.out.println(i + ":" + j + "\t"
						+ this.recset.recs.get(i).georefs.get(j).iLocality);
				if (this.recset.recs.get(i).georefs.get(j).pointRadius != null) {
					System.out.println("\t"
							+ this.recset.recs.get(i).georefs.get(j).pointRadius.toString());

				}
			}
		}
	}

	public void diffInterpretations() {
		try {
			Writer out = new BufferedWriter(new OutputStreamWriter(System.out,
					"UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		// Set up output stream
		long starttime = 0, endtime = 0, diffcount = 0;
		String oInterp, yInterp, uInterp;
		for (int i = 0; i < this.recset.recs.size(); i++) {
			String recid = null;
			if (this.recset.recs.get(i).get("Id") != null) {
				recid = new String(this.recset.recs.get(i).get("Id"));
			} else
				recid = new String("" + i);
			oInterp = new String(recid + "\torig\t"
					+ this.recset.recs.get(i).get("locality"));
			starttime = System.currentTimeMillis();
			try {
				this.yaleLocInterp.doParsing(this.recset.recs.get(i), "Locality");
			} catch (BGIException e) {
				e.printStackTrace();
			}
			endtime = System.currentTimeMillis();
			yInterp = new String(this.recset.recs.get(i).toMarkup());

			this.recset.recs.get(i).clear();
			starttime = System.currentTimeMillis();
			try {
				this.uiucLocInterp.doParsing(this.recset.recs.get(i), "Locality");
			} catch (BGIHmmException e) {
				e.printStackTrace();
			}
			endtime = System.currentTimeMillis();
			uInterp = new String(this.recset.recs.get(i).toMarkup());
			if (yInterp.equalsIgnoreCase(uInterp) == false) {
				diffcount++;
				System.out.println(oInterp + "\n" + recid + "\tyale\t" + yInterp + "\n"
						+ recid + "\tuiuc\t" + uInterp);
			} else {
				System.out.println(oInterp + "\n" + recid + "\tboth\t" + yInterp);
			}
		}
		double percentdiffs = (double) diffcount / (double) this.recset.recs.size();
		System.out.println(diffcount + " diffs of " + this.recset.recs.size()
				+ " records (" + 100 * percentdiffs + "%)");
	}

	public void findLocTypeInterpretations(String loctype) {
		if (loctype == null)
			return;
		try {
			Writer out = new BufferedWriter(new OutputStreamWriter(System.out,
					"UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		// Set up output stream
		long starttime = 0, endtime = 0, diffcount = 0;
		String oInterp, yInterp, uInterp;
		for (int i = 0; i < this.recset.recs.size(); i++) {
			String recid = null;
			if (this.recset.recs.get(i).get("Id") != null) {
				recid = new String(this.recset.recs.get(i).get("Id"));
			} else
				recid = new String("" + i);
			oInterp = new String(recid + "\torig\t"
					+ this.recset.recs.get(i).get("locality"));
			starttime = System.currentTimeMillis();
			try {
				this.yaleLocInterp.doParsing(this.recset.recs.get(i), "Locality");
			} catch (BGIException e) {
				e.printStackTrace();
			}
			endtime = System.currentTimeMillis();
			yInterp = new String(this.recset.recs.get(i).toMarkup(loctype));

			this.recset.recs.get(i).clear();
			starttime = System.currentTimeMillis();
			try {
				this.uiucLocInterp.doParsing(this.recset.recs.get(i), "Locality");
			} catch (BGIHmmException e) {
				e.printStackTrace();
			}
			endtime = System.currentTimeMillis();
			uInterp = new String(this.recset.recs.get(i).toMarkup(loctype));
			if (yInterp.indexOf(loctype) > 0 || uInterp.indexOf(loctype) > 0) {
				diffcount++;
				System.out.println(oInterp + "\n" + recid + "\tyale\t" + yInterp + "\n"
						+ recid + "\tuiuc\t" + uInterp);
			}
		}
		double percentdiffs = (double) diffcount / (double) this.recset.recs.size();
		System.out.println(diffcount + " records with LocType <" + loctype
				+ "> of " + this.recset.recs.size() + " records (" + 100 * percentdiffs
				+ "%)");
	}

	public boolean georeference(GeorefPreferences prefs)
	throws GeorefManager.GeorefManagerException {
		if (prefs.locinterp == null || prefs.locinterp.equalsIgnoreCase("all")) {
			// I think this won't work: Interpreters will just add more clauses to the
			// same Rec.
			// Need to consider a redesign to allow multiple interpreters' georefs.
			// ClauseSets, but for a new reason.
			// this.yaleLocInterp.doParsing(this.recset);
			// this.uiucLocInterp.doParsing(this.recset);
			// this.TULocInterp.doParsing(this.recset, "Locality", "HigherGeography",
			// "Country", "State", "County");
		} else if (prefs.locinterp.equalsIgnoreCase("uiuc")) {
			try {
				this.uiucLocInterp.doParsing(this.recset);
			} catch (BGIHmm.BGIHmmException e) {
				e.printStackTrace();
				throw this.new GeorefManagerException(e.toString(), e);
			}
			for (Rec rec : this.recset.recs) {
				if(geoPredoneCheck(rec)){
					if(populateGeoref(rec)){
						return true;
					}
				}

				spatialDescriptionManager.doSpatialDescription(rec);
			}
		} else if (prefs.locinterp.equalsIgnoreCase("yale")) {
			try {
				this.yaleLocInterp.doParsing(this.recset);
			} catch (BGI.BGIException e) {
				e.printStackTrace();
				throw this.new GeorefManagerException(e.toString(), e);
			}
			for (Rec rec : this.recset.recs) {
				if(geoPredoneCheck(rec)){
					if(populateGeoref(rec)){
						return true;
					}
				}
				spatialDescriptionManager.doSpatialDescription(rec);
			}
		} else if (prefs.locinterp.equalsIgnoreCase("tulane")) {
			try {
				this.TULocInterp.doParsing(this.recset, "Locality", "HigherGeography",
						"Country", "State", "County");
			} catch (Exception e) {
				System.out.println("Error in GeorefManager.georeference()");
			}
			for (Rec rec : this.recset.recs) {
				rec.toString();
			}
		}
		return true;
	}

	public boolean geoPredoneCheck(Rec rec){
		//Check if these headers already have values:

		//DecimalLatitude, DecimalLongitude, 
		//GeodeticDatum, CoordinateUncertaintyInMeters
		if(rec.containsKey("decimallatitude") &&
				rec.containsKey("decimallongitude") &&
//				rec.containsKey("geodeticdatum") &&
				rec.containsKey("coordinateuncertaintyinmeters")){
			if((rec.get("decimallatitude") != null) &&
					(rec.get("decimallongitude") != null) &&
					(rec.get("coordinateuncertaintyinmeters")) != null){
				if((!rec.get("decimallatitude").equals("")) &&
						(!rec.get("decimallongitude").equals("")) &&
						(!rec.get("coordinateuncertaintyinmeters").equals(""))){
					return true;
				}
				return false;
			}
			return false;
		}
		return false;

	}

	public boolean populateGeoref(Rec rec){
		String lat = rec.get("decimallatitude");
		String lng = rec.get("decimallongitude");
		String extent = rec.get("coordinateuncertaintyinmeters");
		Datum datum = null;
		if( rec.get("geodeticdatum")!=null ){
			// Get the datum form the string provided in the geodeticdatum
			// field in the Rec, if it exists.
			datum=DatumManager.getInstance().getDatum(rec.get("geodeticdatum"));
		}
		else {
			// Assume the datum is WGS84 if not provided
			datum = DatumManager.getInstance().getDatum("WGS84"); 
		}
		double dlat = 0;
		double dlng = 0;
		double dextent = 0;

		try{
			dlat = Double.parseDouble(lat);
		}catch(NumberFormatException e){
			return false;
		}try{
			dlng = Double.parseDouble(lng);
		}catch(NumberFormatException e){
			return false;
		}try{
			dextent = Double.parseDouble(extent);
		}catch(NumberFormatException e){
			return false;
		}
		PointRadius pr = new PointRadius(dlng, dlat, datum, -1, dextent);
		Georef g = new Georef(pr);

		String uLocality = "";
		String iLocality = "";

		for(Clause c : rec.clauses){
			uLocality = uLocality + c.uLocality + "; ";  
		}
		iLocality = uLocality + lat + "; " + lng + "; " + extent + " m";
		g.iLocality = iLocality;
		g.uLocality = uLocality;
		rec.georefs.add(g);

		return true;
	}


	public boolean georeference(Rec rec, GeorefPreferences prefs) {
		
		GeorefDictionaryManager gdm = GeorefDictionaryManager.getInstance();
		if (rec == null)
			return false;
		// String s = new String("RecSet: ");
		try {
			// long interpstarttime = 0, interpendtime = 0;
			if (prefs.locinterp == null || prefs.locinterp.equalsIgnoreCase("yale")) {
				// interpstarttime = System.currentTimeMillis();
				this.yaleLocInterp.doParsing(rec, "highergeography", true);
				this.yaleLocInterp.doParsing(rec, "continent", true);
				this.yaleLocInterp.doParsing(rec, "waterbody", true);
				this.yaleLocInterp.doParsing(rec, "islandgroup", true);
				this.yaleLocInterp.doParsing(rec, "island", true);
				this.yaleLocInterp.doParsing(rec, "country", true);
				this.yaleLocInterp.doParsing(rec, "stateprovince", true);
				this.yaleLocInterp.doParsing(rec, "county", true);
				this.yaleLocInterp.doParsing(rec, "locality",gdm,prefs.language);
				this.yaleLocInterp.doParsing(rec, "verbatimlatitude",gdm,prefs.language);
				this.yaleLocInterp.doParsing(rec, "verbatimlongitude",gdm,prefs.language);
				this.yaleLocInterp.doParsing(rec, "verbatimcoordinates",gdm,prefs.language);
				this.yaleLocInterp.doParsing(rec, "verbatimelevation",gdm,prefs.language);

				// interpendtime = System.currentTimeMillis();
				// s = s.concat(" (default) Yale interpreter elapsed time: "
				// + (interpendtime - interpstarttime) + "(ms)");
			} else if (prefs.locinterp.equalsIgnoreCase("uiuc")) {
				// interpstarttime = System.currentTimeMillis();
				this.uiucLocInterp.doParsing(rec, "Locality");
				this.uiucLocInterp.doParsing(rec, "HigherGeography");
				this.uiucLocInterp.doParsing(rec, "Continent");
				this.uiucLocInterp.doParsing(rec, "WaterBody");
				this.uiucLocInterp.doParsing(rec, "IslandGroup");
				this.uiucLocInterp.doParsing(rec, "Island");
				this.uiucLocInterp.doParsing(rec, "Country");
				this.uiucLocInterp.doParsing(rec, "StateProvince");
				this.uiucLocInterp.doParsing(rec, "County");
				this.uiucLocInterp.doParsing(rec, "VerbatimLatitude");
				this.uiucLocInterp.doParsing(rec, "VerbatimLongitude");
				this.uiucLocInterp.doParsing(rec, "VerbatimCoordinates");
				this.uiucLocInterp.doParsing(rec, "VerbatimElevation");
				// interpendtime = System.currentTimeMillis();
				// s = s.concat(" UIUC interpreter elapsed time: "
				// + (interpendtime - interpstarttime) + "(ms)");
			} else if (prefs.locinterp.equalsIgnoreCase("tulane")) {
				// interpstarttime = System.currentTimeMillis();
				this.TULocInterp.doParsing(rec, "Locality", "Highergeography",
						"Country", "State", "County");
				System.out.println(rec);
				// interpendtime = System.currentTimeMillis();
				// s = s.concat(" Tulane interpreter elapsed time: "
				// + (interpendtime - interpstarttime) + "(ms)");
			} else
				return false;
		} catch (BGIHmm.BGIHmmException e) {
			e.printStackTrace();
		} catch (BGI.BGIException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Error in GeorefManager.georeference()");
		}

		if(geoPredoneCheck(rec)){
			if(populateGeoref(rec)){
				return true;
			}
		}

		// long sdstarttime = System.currentTimeMillis();
		// log.info("Doing Spatial Description for rec.");
		// *** Comment next line for testing while not connected
		spatialDescriptionManager.doSpatialDescription(rec);
		// long sdendtime = System.currentTimeMillis();
		// s = s.concat(" Spatial Description Elapsed Time: "
		// + (sdendtime - sdstarttime) + "(ms)");
		// log.info(s);
		return true;
	}
	public void loadUserFeatures(Rec rec){
		if(rec==null) return;
		spatialDescriptionManager.addUserFeature(rec, "tuco (tuco@berkeley.edu)", -1);
	}

	public void removeUserFeature(int featureid){
		spatialDescriptionManager.removeUserFeature(featureid);
	}

	public boolean georeference(Rec rec, int featureid) {
		// In this method we are going to add a single georeference
		// to the rec based solely on the feature referenced by featureid
		// with locType F. This assumes the the rest of the Rec has already been
		// georeferenced.
		spatialDescriptionManager.doSpatialDescription(rec, featureid);
		return false;
	}

	public boolean georeferenceAllTulaneFirst()
	throws GeorefManager.GeorefManagerException, BGIException,
	BGIHmmException {
		return false;
	}

	public boolean georeferenceAllUIUCFirst()
	throws GeorefManager.GeorefManagerException, BGIException,
	BGIHmmException {

		if (this.recset == null)
			return false;
		String s = new String("RecSet: ");
		if (recset.filename == null)
			s = s.concat("[no filename]; ");
		else
			s = s.concat(recset.filename + "; ");
		s = s.concat("Count=" + recset.recs.size());
		log.info(s);
		for (int i = 0; i < this.recset.recs.size(); i++) {
			Rec r = this.recset.recs.get(i);
			String s1 = new String("Rec:\t" + r.get("id"));
			this.uiucLocInterp.doParsing(r, "Locality");
			spatialDescriptionManager.doSpatialDescription(r);
			if (r.georefs == null || r.georefs.size() == 0) {
				r.clear();
				s1 = s1.concat(";\tUIUC failed");
				this.yaleLocInterp.doParsing(r, "locality");
				spatialDescriptionManager.doSpatialDescription(r);
				if (r.georefs == null || r.georefs.size() == 0) {
					s1 = s1.concat(";\tYale failed");
				} else {
					s1 = s1.concat(";\tYale succeeded");
				}
			} else {
				s1 = s1.concat(";\tUIUC succeeded\tYale not attempted");
			}
			log.info(s1);
		}
		return true;
	}

	public boolean georeferenceAllYaleFirst()
	throws GeorefManager.GeorefManagerException, BGIException,
	BGIHmmException {
		if (this.recset == null)
			return false;
		String s = new String("RecSet: ");
		if (recset.filename == null)
			s = s.concat("[no filename]; ");
		else
			s = s.concat(recset.filename + "; ");
		s = s.concat("Count=" + recset.recs.size());
		log.info(s);
		for (int i = 0; i < this.recset.recs.size(); i++) {
			Rec r = this.recset.recs.get(i);
			String s1 = new String("Rec:\t" + r.get("id"));
			this.yaleLocInterp.doParsing(r, "locality");
			spatialDescriptionManager.doSpatialDescription(r);
			if (r.georefs == null || r.georefs.size() == 0) {
				r.clear();
				s1 = s1.concat(";\tYale failed");
				this.uiucLocInterp.doParsing(r, "Locality");
				spatialDescriptionManager.doSpatialDescription(r);
				if (r.georefs == null || r.georefs.size() == 0) {
					s1 = s1.concat(";\tUIUC failed");
				} else {
					s1 = s1.concat(";\tUIUC succeeded");
				}
			} else {
				s1 = s1.concat(";\tYale succeeded\tUIUC not attempted");
			}
			log.info(s1);
		}
		return true;
	}

	public String getProperty(String p){
		return this.props.getProperty(p);
	}

	public void interpretTulane() throws bg_geolocate_Exception {
		if (this.recset == null)
			return;
		System.out.println("Output from interpretTulane() using as input: "
				+ recset.filename);
		String recid = null;
		for (int i = 0; i < this.recset.recs.size(); i++) {
			if (this.recset.recs.get(i).get("Id") != null) {
				recid = this.recset.recs.get(i).get("Id");
			} else
				recid = new String("" + i);
			this.TULocInterp.doParsing(this.recset.recs.get(i), "Locality",
					"Highergeography", "Country", "State", "County");
			System.out.println(recid + "\t" + this.recset.recs.get(i).toMarkup());
		}
	}

	public void interpretUIUC(String parsefield)
	throws GeorefManager.GeorefManagerException, BGIHmmException {
		if (this.recset == null)
			return;
		System.out.println("Output from interpretUIUC() using as input: "
				+ recset.filename);
		String recid = null;
		if (parsefield == null || parsefield.equalsIgnoreCase("all")) {
			this.uiucLocInterp.doParsing(this.recset);
			for (int i = 0; i < this.recset.recs.size(); i++) {
				if (this.recset.recs.get(i).get("Id") != null) {
					recid = this.recset.recs.get(i).get("Id");
				} else
					recid = new String("" + i);
				System.out.println(recid + "\t" + this.recset.recs.get(i).toMarkup());
			}
		} else {
			for (int i = 0; i < this.recset.recs.size(); i++) {
				if (this.recset.recs.get(i).get("Id") != null) {
					recid = this.recset.recs.get(i).get("Id");
				} else
					recid = new String("" + i);
				this.uiucLocInterp.doParsing(this.recset.recs.get(i), parsefield);
				System.out.println(recid + "\t" + this.recset.recs.get(i).toMarkup());
			}
		}
	}

	public void interpretYale(String parsefield)
	throws GeorefManager.GeorefManagerException, BGIException {
		if (this.recset == null)
			return;
		System.out.println("Output from interpretYale() using as input: "
				+ recset.filename);
		String recid = null;
		if (parsefield == null || parsefield.equalsIgnoreCase("all")) {
			this.yaleLocInterp.doParsing(this.recset);
			for (int i = 0; i < this.recset.recs.size(); i++) {
				if (this.recset.recs.get(i).get("Id") != null) {
					recid = this.recset.recs.get(i).get("Id");
				} else
					recid = new String("" + i);
				System.out.println(recid + "\t" + this.recset.recs.get(i).toMarkup());
			}
		} else {
			for (int i = 0; i < this.recset.recs.size(); i++) {
				if (this.recset.recs.get(i).get("Id") != null) {
					recid = this.recset.recs.get(i).get("Id");
				} else
					recid = new String("" + i);
				this.yaleLocInterp.doParsing(this.recset.recs.get(i), parsefield);
				System.out.println(recid + "\t" + this.recset.recs.get(i).toMarkup());
			}
		}
	}

	public void locTypeCounts(String arg1, String interpfield) {
		String[] loctypes = { "addr", "adm", "bf", "bp", "e", "f", "fh", "fo",
				"foh", "foo", "fpoh", "fs", "j", "jo", "jh", "joh", "joo", "jpoh",
				"ll", "nf", "nj", "nn", "np", "npom", "p", "ph", "po", "poh", "pom",
				"ps", "q", "trs", "trss", "unk", "utm" };
		int[] loccounts = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int clausecount = 0;
		for (int i = 0; i < this.recset.recs.size(); i++) {
			if (arg1.equalsIgnoreCase("yale")) {
				try {
					this.yaleLocInterp.doParsing(this.recset.recs.get(i), interpfield);
				} catch (BGIException e) {
					e.printStackTrace();
				}
			}
			if (arg1.equalsIgnoreCase("uiuc")) {
				try {
					this.uiucLocInterp.doParsing(this.recset.recs.get(i), interpfield);
				} catch (BGIHmmException e) {
					e.printStackTrace();
				}
			}
			for (int j = 0; j < this.recset.recs.get(i).clauses.size(); j++) {
				clausecount++;
				int k = 0;
				while (k < loctypes.length
						&& loctypes[k].equalsIgnoreCase(this.recset.recs.get(i).clauses
								.get(j).locType) == false)
					k++;
				loccounts[k]++;
			}
		}
		for (int i = 0; i < loctypes.length; i++) {
			System.out.println(loctypes[i] + ":  \t" + loccounts[i] + "\t(" + 100.0
					* loccounts[i] / clausecount + "%)");
		}
		System.out.println("Recs: " + this.recset.recs.size() + " Clauses: "
				+ clausecount);
	}

	public boolean newGeoreference(GeorefPreferences prefs)
	throws GeorefManager.GeorefManagerException {
		for (Rec rec : this.recset.recs) {
			georeference(rec, prefs);
		}
		return true;
	}

	public void newInterpretYale(String parsefield, String language)
	throws GeorefManager.GeorefManagerException, BGIException {
		GeorefDictionaryManager gdm = GeorefDictionaryManager.getInstance();
		SupportedLanguages interplang = SupportedLanguages.english;
		if (language.equalsIgnoreCase("español")
				|| language.equalsIgnoreCase("spanish")) {
			interplang = SupportedLanguages.spanish;
		} else if (language.equalsIgnoreCase("português")
				|| language.equalsIgnoreCase("portuguese")) {
			interplang = SupportedLanguages.portuguese;
		} else if (language.equalsIgnoreCase("français")
				|| language.equalsIgnoreCase("french")) {
			interplang = SupportedLanguages.french;
		}

		if (this.recset == null)
			return;
		System.out.println("Output from newInterpretYale() using as input: "
				+ recset.filename);
		String recid = null;
		if (parsefield == null || parsefield.equalsIgnoreCase("all")) {
			this.yaleLocInterp.doParsing(this.recset, gdm, interplang);
			for (int i = 0; i < this.recset.recs.size(); i++) {
				if (this.recset.recs.get(i).get("Id") != null) {
					recid = this.recset.recs.get(i).get("Id");
				} else
					recid = new String("" + i);
				System.out.println(recid + "\t" + this.recset.recs.get(i).toMarkup());
			}
		} else {
			for (int i = 0; i < this.recset.recs.size(); i++) {
				if (this.recset.recs.get(i).get("Id") != null) {
					recid = this.recset.recs.get(i).get("Id");
				} else
					recid = new String("" + i);
				this.yaleLocInterp.doParsing(this.recset.recs.get(i), parsefield, gdm,
						interplang);
				System.out.println(recid + "\t" + this.recset.recs.get(i).toMarkup());
			}
		}
	}

	public void sameInterpretations(boolean showorig) {
		try {
			Writer out = new BufferedWriter(new OutputStreamWriter(System.out,
					"UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		// Set up output stream
		long starttime = 0, endtime = 0, diffcount = 0;
		String yInterp, uInterp;
		for (int i = 0; i < this.recset.recs.size(); i++) {
			String recid = null;
			if (this.recset.recs.get(i).get("Id") != null) {
				recid = new String(this.recset.recs.get(i).get("Id"));
			} else
				recid = new String("" + i);
			starttime = System.currentTimeMillis();
			try {
				this.yaleLocInterp.doParsing(this.recset.recs.get(i), "Locality");
			} catch (BGIException e) {
				e.printStackTrace();
			}
			endtime = System.currentTimeMillis();
			yInterp = new String(this.recset.recs.get(i).toMarkup());

			this.recset.recs.get(i).clear();
			starttime = System.currentTimeMillis();
			try {
				this.uiucLocInterp.doParsing(this.recset.recs.get(i), "Locality");
			} catch (BGIHmmException e) {
				e.printStackTrace();
			}
			endtime = System.currentTimeMillis();
			uInterp = new String(this.recset.recs.get(i).toMarkup());
			if (yInterp.equalsIgnoreCase(uInterp) == true) {
				diffcount++;
				System.out.println(recid + "\torig\t"
						+ this.recset.recs.get(i).get("locality"));
				System.out.println(recid + "\tboth\t" + yInterp);
			}
		}
		double percentdiffs = (double) diffcount / (double) this.recset.recs.size();
		System.out.println(diffcount + " the same of " + this.recset.recs.size()
				+ " records (" + 100 * percentdiffs + "%)");
	}

	public void setRecSet(RecSet recset) {
		this.recset = recset;
	}

	public void shutdown() {
		if (spatialDescriptionManager != null)
			spatialDescriptionManager.shutdown();
	}

	public void testInterpretation() {
		long starttime = 0, endtime = 0;
		for (int i = 0; i < this.recset.recs.size(); i++) {
			if (this.recset.recs.get(i).get("Id") == null) {
				System.out.println("Orig\tRow: " + i + ";\tTime: 0;\tMarkup: "
						+ this.recset.recs.get(i).get("locality"));
			} else {
				System.out.println("Orig\tRec: " + i + ";\tTime: 0;\tMarkup: "
						+ this.recset.recs.get(i).get("locality"));
			}
			starttime = System.currentTimeMillis();
			try {
				this.yaleLocInterp.doParsing(this.recset.recs.get(i), "Locality");
			} catch (BGIException e) {
				e.printStackTrace();
			}
			endtime = System.currentTimeMillis();
			if (this.recset.recs.get(i).get("Id") == null) {
				System.out.println("Yale\tRow: " + i + ";\tTime: "
						+ (endtime - starttime) + ";\tMarkup: "
						+ this.recset.recs.get(i).toMarkup());
			} else {
				System.out.println("Yale\tRec: " + this.recset.recs.get(i).get("Id")
						+ ";\tTime: " + (endtime - starttime) + ";\tMarkup: "
						+ this.recset.recs.get(i).toMarkup());
			}

			this.recset.recs.get(i).clear();
			starttime = System.currentTimeMillis();
			try {
				this.uiucLocInterp.doParsing(this.recset.recs.get(i), "Locality");
			} catch (BGIHmmException e) {
				e.printStackTrace();
			}
			endtime = System.currentTimeMillis();
			if (this.recset.recs.get(i).get("Id") == null) {
				System.out.println("UIUC\tRow: " + i + ";\tTime: "
						+ (endtime - starttime) + ";\tMarkup: "
						+ this.recset.recs.get(i).toMarkup());
			} else {
				System.out.println("UIUC\tRec: " + this.recset.recs.get(i).get("Id")
						+ ";\tTime: " + (endtime - starttime) + ";\tMarkup: "
						+ this.recset.recs.get(i).toMarkup());
			}
		}
	}

	public void testXML() {
		System.out.print(this.recset.toXML());
	}

	private void init() throws GeorefManagerException {
		try {
			log.info("GeorefManager started");
			yaleLocInterp = new BGI();
			// log.info("after BGI()");
			uiucLocInterp = new BGIHmm();
			// log.info("after BGIHmm()");
			TULocInterp = new bg_geolocate();
			// log.info("after BGIHmm()");

			// Comment out the spatialDescriptionManager to test interpreters without
			// connecting to the gazetteer.
			spatialDescriptionManager = new SpatialDescriptionManager();
			// log.info("after SpatialDescriptionManager()");
		} catch (Throwable t) {
			log.error("Problem in GeorefManager.init()! " + t.toString());
			// throw new GeorefManager.GeorefManagerException(e.toString(), e);
		}
	}
	public boolean getFeatureNames(GeorefPreferences prefs)
	throws GeorefManager.GeorefManagerException {
		for (Rec rec : this.recset.recs) {
			interpretForFeatureNames(rec, prefs);
		}
		return true;
	}

	public void interpretForFeatureNames(Rec rec, GeorefPreferences prefs){	
		try {
//		long interpstarttime = 0, interpendtime = 0;
		if (prefs.locinterp == null || prefs.locinterp.equalsIgnoreCase("yale")) {
//			interpstarttime = System.currentTimeMillis();
			this.yaleLocInterp.doParsing(rec, "locality");
			this.yaleLocInterp.doParsing(rec, "highergeography", true);
			this.yaleLocInterp.doParsing(rec, "continent", true);
			this.yaleLocInterp.doParsing(rec, "waterbody", true);
			this.yaleLocInterp.doParsing(rec, "islandgroup", true);
			this.yaleLocInterp.doParsing(rec, "island", true);
			this.yaleLocInterp.doParsing(rec, "country", true);
			this.yaleLocInterp.doParsing(rec, "stateprovince", true);
			this.yaleLocInterp.doParsing(rec, "county", true);   
			this.yaleLocInterp.doParsing(rec, "verbatimlatitude");   
			this.yaleLocInterp.doParsing(rec, "verbatimlongitude");   
			this.yaleLocInterp.doParsing(rec, "verbatimcoordinates");
			this.yaleLocInterp.doParsing(rec, "verbatimelevation");

//			interpendtime = System.currentTimeMillis();
//			s = s.concat(" (default) Yale interpreter elapsed time: "
//					+ (interpendtime - interpstarttime) + "(ms)");
		} else if (prefs.locinterp.equalsIgnoreCase("uiuc")) {
//			interpstarttime = System.currentTimeMillis();
			this.uiucLocInterp.doParsing(rec, "Locality");
			this.uiucLocInterp.doParsing(rec, "HigherGeography");
			this.uiucLocInterp.doParsing(rec, "Continent");
			this.uiucLocInterp.doParsing(rec, "WaterBody");
			this.uiucLocInterp.doParsing(rec, "IslandGroup");
			this.uiucLocInterp.doParsing(rec, "Island");
			this.uiucLocInterp.doParsing(rec, "Country");
			this.uiucLocInterp.doParsing(rec, "StateProvince");
			this.uiucLocInterp.doParsing(rec, "County");
			this.uiucLocInterp.doParsing(rec, "VerbatimLatitude");
			this.uiucLocInterp.doParsing(rec, "VerbatimLongitude");
			this.uiucLocInterp.doParsing(rec, "VerbatimCoordinates");
			this.uiucLocInterp.doParsing(rec, "VerbatimElevation");
//			interpendtime = System.currentTimeMillis();
//			s = s.concat(" UIUC interpreter elapsed time: "
//					+ (interpendtime - interpstarttime) + "(ms)");
		} else if (prefs.locinterp.equalsIgnoreCase("tulane")) {
//			interpstarttime = System.currentTimeMillis();
			this.TULocInterp.doParsing(rec, "Locality", "Highergeography", "Country", "State", "County");
			System.out.println(rec);
//			interpendtime = System.currentTimeMillis();
//			s = s.concat(" Tulane interpreter elapsed time: "
//					+ (interpendtime - interpstarttime) + "(ms)");
		}
	} catch (BGIHmm.BGIHmmException e) {
		e.printStackTrace();
	} catch (BGI.BGIException e) {
		e.printStackTrace();
	} catch (Exception e) {
		System.out.println("Error in GeorefManager.georeference()");
	}
	System.out.print(rec.getFeatures());
	}
	
}
/*
 * public boolean georeference(GeorefPreferences prefs) throws
 * GeorefManager.GeorefManagerException { if (this.recset == null) return false;
 * String s = new String("RecSet: "); if (recset.filename == null) s =
 * s.concat("[no filename]; "); else s = s.concat(recset.filename + "; "); s =
 * s.concat("Count=" + recset.recs.size()); try { long interpstarttime = 0,
 * interpendtime = 0; if (prefs.locinterp == null) { interpstarttime =
 * System.currentTimeMillis(); this.yaleLocInterp.doParsing(this.recset);
 * interpendtime = System.currentTimeMillis(); s = s.concat(" (default) Yale
 * interpreter elapsed time: " + (interpendtime - interpstarttime) + "(ms)"); }
 * else if (prefs.locinterp.equalsIgnoreCase("uiuc")) { interpstarttime =
 * System.currentTimeMillis(); this.uiucLocInterp.doParsing(this.recset);
 * interpendtime = System.currentTimeMillis(); s = s.concat(" UIUC interpreter
 * elapsed time: " + (interpendtime - interpstarttime) + "(ms)"); } else if
 * (prefs.locinterp.equalsIgnoreCase("yale")) { interpstarttime =
 * System.currentTimeMillis(); this.yaleLocInterp.doParsing(this.recset);
 * System.out .println("georeference() returning from yale.doParsing()");
 * 
 * interpendtime = System.currentTimeMillis(); s = s.concat(" Yale interpreter
 * elapsed time: " + (interpendtime - interpstarttime) + "(ms)"); } } catch
 * (BGIHmm.BGIHmmException e) { e.printStackTrace(); throw this.new
 * GeorefManagerException(e.toString(), e); } catch (BGI.BGIException e) {
 * e.printStackTrace(); throw this.new GeorefManagerException(e.toString(), e); }
 * catch (Exception e) { System.out.println("Error in
 * GeorefManager.georeference()"); } long sdstarttime =
 * System.currentTimeMillis(); for (Rec rec : this.recset.recs) { // print out
 * each record log.info("Doing Spatial Description for rec ID =
 * "+rec.get("id")); spatialDescriptionManager.doSpatialDescription(rec); } long
 * sdendtime = System.currentTimeMillis(); s = s.concat(" Spatial Description
 * Elapsed Time: " + (sdendtime - sdstarttime) + "(ms)"); log.info(s); return
 * true; }
 */