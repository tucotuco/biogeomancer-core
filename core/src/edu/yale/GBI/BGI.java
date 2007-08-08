/**
 * BGI.java
 *
 */
package edu.yale.GBI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.biogeomancer.records.*;
import org.w3c.dom.*;

import edu.yale.GBI.interp.*;
import org.biogeomancer.managers.GeorefDictionaryManager;
import org.biogeomancer.utils.SupportedLanguages;
/**
 * BGI class.
 */
public class BGI {

	/**
	 * BGI Exception class.
	 */
	public class BGIException extends Exception {
		public BGIException(Exception e) {
			super(e.toString());
			e.printStackTrace();
		}
	}

	static LocalityRec record;

	static Hashtable<String, String> units;


	static Hashtable<String, String> headings;



	static Hashtable<String, String> path;
	static {
		units = new Hashtable<String, String>();
		units.put("M", "M");
		units.put("FT", "FT");
		units.put("FOOT", "FOOT");
		units.put("FEET", "FEET");
		units.put("YARD", "YARD");
		units.put("YARDS", "YARDS");
		units.put("YD", "YD");
		units.put("YDS", "YDS");
		units.put("M", "M");
		units.put("MI", "MI");
		units.put("MILE", "MILE");
		units.put("MILES", "MILES");
		units.put("KM", "KM");
		units.put("KILOMETER", "KILOMETER");
		units.put("KILOMETERS", "KILOMETERS");
		units.put("NAUTICALMILE", "NAUTICALMILE");
		units.put("FATHOM", "FATHOM");
		
		headings = new Hashtable<String, String>();
		headings.put("NORTH", "NORTH");
		headings.put("NORTHEAST", "NORTHEAST");
		headings.put("EAST", "EAST");
		headings.put("SOUTHEAST", "SOUTHEAST");
		headings.put("SOUTH", "SOUTH");
		headings.put("SOUTHWEST", "SOUTHWEST");
		headings.put("WEST", "WEST");
		headings.put("NORTHWEST", "NORTHWEST");
		headings.put("N", "N");
		headings.put("NNE", "NNE");
		headings.put("NE", "NE");
		headings.put("ENE", "ENE");
		headings.put("E", "E");
		headings.put("ESE", "ESE");
		headings.put("SE", "SE");
		headings.put("SSE", "SSE");
		headings.put("S", "S");
		headings.put("SSW", "SSW");
		headings.put("SW", "SW");
		headings.put("WSW", "WSW");
		headings.put("W", "W");
		headings.put("WNW", "WNW");
		headings.put("NW", "NW");
		headings.put("NNW", "NNW");
		headings.put("UP", "UP");
		headings.put("DOWN", "DOWN");
		
		path = new Hashtable<String, String>();
		path.put("R", "R");
		path.put("RIVER", "RIVER");
		path.put("RIO", "RIO");
		path.put("CREEK", "CREEK");
		path.put("CR", "CR");
		path.put("ROAD", "ROAD");
		path.put("RD", "RD");
		path.put("HWY", "HWY");
		path.put("HIGHWAY", "HIGHWAY");
		path.put("RTE", "RTE");
		path.put("ROUTE", "ROUTE");
		path.put("TRAIL", "TRAIL");
	}

	public void doParsing(RecSet rs) throws BGI.BGIException {
		for (int i = 0; i < rs.recs.size(); i++){
            doParsing((Rec) rs.recs.get(i), "highergeography", true);
            doParsing((Rec) rs.recs.get(i), "continent", true);
            doParsing((Rec) rs.recs.get(i), "waterbody", true);
            doParsing((Rec) rs.recs.get(i), "islandgroup", true);
            doParsing((Rec) rs.recs.get(i), "island", true);
            doParsing((Rec) rs.recs.get(i), "country", true);
            doParsing((Rec) rs.recs.get(i), "stateprovince", true);
            doParsing((Rec) rs.recs.get(i), "county", true);        
            doParsing((Rec) rs.recs.get(i), "locality");
		}
	}

	public void doParsing(RecSet rs, GeorefDictionaryManager gdm, SupportedLanguages lang) throws BGI.BGIException {
		for (int i = 0; i < rs.recs.size(); i++){
            doParsing((Rec) rs.recs.get(i), "highergeography", true);
            doParsing((Rec) rs.recs.get(i), "continent", true);
            doParsing((Rec) rs.recs.get(i), "waterbody", true);
            doParsing((Rec) rs.recs.get(i), "islandgroup", true);
            doParsing((Rec) rs.recs.get(i), "island", true);
            doParsing((Rec) rs.recs.get(i), "country", true);
            doParsing((Rec) rs.recs.get(i), "stateprovince", true);
            doParsing((Rec) rs.recs.get(i), "county", true);        
            doParsing((Rec) rs.recs.get(i), "locality", gdm, lang);
		}
	}

	public void doParsing(Rec r, String fieldName) throws BGI.BGIException {
		if (fieldName == null) {
			// do we want to log this?
			return;
		}
		String verbatimLocality = r.get(fieldName);
		if(verbatimLocality==null){
			// TODO: This field name isn't among those in the input RecSet.
			// The user must comply with the accepted column names, which
			// are enumerated on the BG web site.
			return;
		}
		try {
			LocalityRec rc = new LocalityRec();
//			System.out.println(fieldName+"= " + verbatimLocality);
			rc.localityString = verbatimLocality.trim().replaceAll("\"", "");
			BGMUtil.parseLocality(rc);
			for (int i = 0; i < rc.results.length; i++) {
				Clause cl = new Clause();
				r.clauses.add(cl);
				cl.locType = rc.results[i].locType;
				cl.uLocality=rc.clauseSet[i]; // Added by JRW 060813 to capture original clause.
				if (cl.locType.length() > 0 && cl.locType.equals("nn"))
					cl.state = ClauseState.CLAUSE_PARSE_ERROR;
				else
					cl.state = ClauseState.CLAUSE_PARSED;
				LocSpec ls = new LocSpec();
				cl.locspecs.add(ls);
				if (rc.results[i].feature1.length() > 0)
					ls.state = LocSpecState.LOCSPEC_COMPLETED;
				ls.featurename = rc.results[i].feature1;
				ls.vheading = rc.results[i].heading;
				if (rc.results[i].headingEW.length()>0)
					ls.vheadingew = rc.results[i].headingEW;
				if (rc.results[i].headingNS.length()>0)
					ls.vheadingns = rc.results[i].headingNS;				
				if (rc.results[i].offset != null
						&& rc.results[i].offset.length() > 0) {
					ls.voffset = rc.results[i].offset;
					ls.voffsetunit = rc.results[i].unit;
				}
				if (rc.results[i].offsetEW != null
						&& rc.results[i].offsetEW.length() > 0) {
					ls.voffsetew = rc.results[i].offsetEW;
					ls.voffsetewunit = rc.results[i].unit;
				}
				if (rc.results[i].offsetNS != null
						&& rc.results[i].offsetNS.length() > 0) {
					ls.voffsetns = rc.results[i].offsetNS;
					ls.voffsetnsunit = rc.results[i].unit;
				}
				ls.vsubdivision = rc.results[i].subdivision;
// TODO: rc.results[i].unit is being added to velevation regardless
// This results in elevation units even when there is no elevation.
// It's unclear what happens when the clause is an elevation. JRW 2006-08-27
				if(rc.results[i].evelation != null 
						&& rc.results[i].evelation.length()>0){
					ls.velevation = rc.results[i].evelation;
					if(rc.results[i].unit != null 
							&& rc.results[i].unit.length()>0){
						ls.velevationunits =  rc.results[i].unit;
					}
				}
// JRW commented out the following line in favor f the code block above
//				ls.velevation = rc.results[i].evelation + rc.results[i].unit;
				ls.vlat = rc.results[i].lat;
				ls.vlng = rc.results[i].lng;
				ls.vutmzone = rc.results[i].utmz;
				ls.vutme = rc.results[i].utme;
				ls.vutmn = rc.results[i].utmn;
				ls.vtownship = rc.results[i].town;
				ls.vtownshipdir = rc.results[i].towndir;
				ls.vrange = rc.results[i].range;
				ls.vrangedir = rc.results[i].rangedir;
				ls.vsection = rc.results[i].section;
				if (rc.results[i].feature2 != null
						&& rc.results[i].feature2.length() > 0) {
					LocSpec ls2 = new LocSpec();
					cl.locspecs.add(ls2);
					ls2.featurename = rc.results[i].feature2;
					ls2.state = LocSpecState.LOCSPEC_COMPLETED;
				}
				if (rc.results[i].feature3 != null
						&& rc.results[i].feature3.length() > 0) {
					LocSpec ls3 = new LocSpec();
					cl.locspecs.add(ls3);
					ls3.featurename = rc.results[i].feature3;
					ls3.state = LocSpecState.LOCSPEC_COMPLETED;
				}
			}
		} catch (Exception e) {
			throw this.new BGIException(e);
		}
	}
	
	public void doParsing(Rec r, String fieldName, GeorefDictionaryManager gdm, SupportedLanguages lang) throws BGI.BGIException {
		if (fieldName == null) {
			// do we want to log this?
			return;
		}
		String verbatimLocality = r.get(fieldName);
		if(verbatimLocality==null){
			// TODO: This field name isn't among those in the input RecSet.
			// The user must comply with the accepted column names, which
			// are enumerated on the BG web site.
			return;
		}
		try {
			LocalityRec rc = new LocalityRec();
//			System.out.println(fieldName+"= " + verbatimLocality);
			rc.localityString = verbatimLocality.trim().replaceAll("\"", "");
			Parser.getInstance(gdm,lang).process(rc);
			for (int i = 0; i < rc.results.length; i++) {
				Clause cl = new Clause();
				r.clauses.add(cl);
				cl.locType = rc.results[i].locType;
				cl.uLocality=rc.clauseSet[i]; // Added by JRW 060813 to capture original clause.
				if (cl.locType.length() > 0 && cl.locType.equals("nn"))
					cl.state = ClauseState.CLAUSE_PARSE_ERROR;
				else
					cl.state = ClauseState.CLAUSE_PARSED;
				LocSpec ls = new LocSpec();
				cl.locspecs.add(ls);
				if (rc.results[i].feature1.length() > 0)
					ls.state = LocSpecState.LOCSPEC_COMPLETED;
				ls.featurename = rc.results[i].feature1;
				ls.vheading = rc.results[i].heading;
				if (rc.results[i].headingEW.length()>0)
					ls.vheadingew = rc.results[i].headingEW;
				if (rc.results[i].headingNS.length()>0)
					ls.vheadingns = rc.results[i].headingNS;				
				if (rc.results[i].offset != null
						&& rc.results[i].offset.length() > 0) {
					ls.voffset = rc.results[i].offset;
					ls.voffsetunit = rc.results[i].unit;
				}
				if (rc.results[i].offsetEW != null
						&& rc.results[i].offsetEW.length() > 0) {
					ls.voffsetew = rc.results[i].offsetEW;
					ls.voffsetewunit = rc.results[i].unit;
				}
				if (rc.results[i].offsetNS != null
						&& rc.results[i].offsetNS.length() > 0) {
					ls.voffsetns = rc.results[i].offsetNS;
					ls.voffsetnsunit = rc.results[i].unit;
				}
				ls.vsubdivision = rc.results[i].subdivision;
// TODO: rc.results[i].unit is being added to velevation regardless
// This results in elevation units even when there is no elevation.
// It's unclear what happens when the clause is an elevation. JRW 2006-08-27
				if(rc.results[i].evelation != null 
						&& rc.results[i].evelation.length()>0){
					ls.velevation = rc.results[i].evelation;
					if(rc.results[i].unit != null 
							&& rc.results[i].unit.length()>0){
						ls.velevationunits =  rc.results[i].unit;
					}
				}
// JRW commented out the following line in favor of the code block above
//				ls.velevation = rc.results[i].evelation + rc.results[i].unit;
				ls.vlat = rc.results[i].lat;
				ls.vlng = rc.results[i].lng;
				ls.vutmzone = rc.results[i].utmz;
				ls.vutme = rc.results[i].utme;
				ls.vutmn = rc.results[i].utmn;
				ls.vtownship = rc.results[i].town;
				ls.vtownshipdir = rc.results[i].towndir;
				ls.vrange = rc.results[i].range;
				ls.vrangedir = rc.results[i].rangedir;
				ls.vsection = rc.results[i].section;
				if (rc.results[i].feature2 != null
						&& rc.results[i].feature2.length() > 0) {
					LocSpec ls2 = new LocSpec();
					cl.locspecs.add(ls2);
					ls2.featurename = rc.results[i].feature2;
					ls2.state = LocSpecState.LOCSPEC_COMPLETED;
				}
				if (rc.results[i].feature3 != null
						&& rc.results[i].feature3.length() > 0) {
					LocSpec ls3 = new LocSpec();
					cl.locspecs.add(ls3);
					ls3.featurename = rc.results[i].feature3;
					ls3.state = LocSpecState.LOCSPEC_COMPLETED;
				}
			}
		} catch (Exception e) {
			throw this.new BGIException(e);
		}
	}
		
	public void doParsing(Rec r, String fieldName, boolean isAdm) throws BGI.BGIException {
		if (fieldName == null) {
			// do we want to log this?
			return;
		}
		String verbatimLocality = r.get(fieldName);
		if(!isAdm || verbatimLocality==null || verbatimLocality.length()<1){
			// TODO: This field name isn't among those in the input RecSet.
			// The user must comply with the accepted column names, which
			// are enumerated on the BG web site.
			return;
		}
		try {
			String []adm=verbatimLocality.split(",|;|:");
			for (int i = 0; i < adm.length; i++) {
				if(adm[i].length()>0){
				    Clause cl = new Clause();
				    r.clauses.add(cl);
				    cl.locType = "ADM";
				    cl.uLocality=adm[i].trim(); // Added by JRW 060813 to capture original clause.				
				    cl.state = ClauseState.CLAUSE_PARSED;
				    LocSpec ls = new LocSpec();
				    cl.locspecs.add(ls);				
				    ls.state = LocSpecState.LOCSPEC_COMPLETED;
				    ls.featurename = adm[i].trim();
			    }
			}
		} catch (Exception e) {
			throw this.new BGIException(e);
		}
	}
}