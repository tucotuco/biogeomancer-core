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

import java.sql.Connection;
import java.sql.SQLException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.biogeomancer.records.*;
import org.biogeomancer.records.RecSet.RecSetException;
import org.biogeomancer.utils.PointRadius;
import org.biogeomancer.managers.ADLGazetteer;
import org.biogeomancer.managers.DatumManager.Datum;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import edu.colorado.sde.*;
import org.biogeomancer.utils.*;

public class SpatialDescriptionManager extends BGManager {
	public static ADLGazetteer gaz;
	public static ShapeManager sm;

	private static Logger log = Logger.getLogger(SpatialDescriptionManager.class);
	private static Properties props = new Properties();
	public static final String PROPS_FILE = "SpatialDescriptionManager.properties";
	static {
		props = new Properties();
		initProps(PROPS_FILE, props);
	}

	public static void main(String[] args) throws MalformedURLException, RecSetException {		
		if(args.length==0) {
			System.out.println("featureid argument required");
			return;
		}
		Integer z = new Integer(args[0]);
		int featureid = z.intValue();

		SpatialDescriptionManager sdm = new SpatialDescriptionManager();
		Rec rec = new Rec();
		sdm.doSpatialDescription(rec, featureid);
		System.out.println(rec);
		sdm.shutdown();
	}

	public SpatialDescriptionManager() {
		startup();
	}
	public void startup() {
		try {
			gaz = ADLGazetteer.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		sm = new ShapeManager();
	}
	public void shutdown(){
		gaz.shutdown();
	}

	public void addUserFeature(Rec r, String user, int scheme_term_id){
		if(r.georefs.size()!=1){
			// There can be only one Georef for a user Feature 
			// to be loaded into the gazetteer. If there is more than 
			// one, something isn't a it should be.
			return;
		}
		Clause featureclause = null;
		for( Clause c : r.clauses){
			if(c.locType.equalsIgnoreCase("f")){
				featureclause = c;
			}
		}
		if(featureclause==null) return;
		try {
			gaz.insertFeature(userplaces, r.georefs.get(0), user, featureclause.uLocality, scheme_term_id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void doSpatialDescription(Rec rec, int featureid){
		String version = new String("doSpatialDescription(Rec, int):20070513");
		String process = new String("SpatialDescriptionManager)");
		// In this method we are going to add a single georeference 
		// to the rec based solely on the feature referenced by featureid 
		// with locType F
		Clause clause = new Clause();
		clause.locType = new String("F");
		LocSpec locspec = new LocSpec();
		FeatureInfo fi = gaz.featureLookup(userplaces, featureid);
		locspec.featurename = new String(fi.name);
		locspec.featureinfos.add(fi);
		clause.locspecs.add(locspec);
		PointRadius pr = null;
		pr = sm.getPointRadius( clause.locType, clause.locspecs.get(0), fi, null );
		// Point-radius was successfully created, now add it to the clause's georefs list.
		if( pr != null ) {
			Georef g = new Georef(pr);
			g.confidence=0;
			g.uLocality=clause.uLocality;
			if(g.iLocality==null || g.iLocality.trim().length()==0){
				g.iLocality=clause.makeInterpretedLocality(fi, null);
			}else{
				g.iLocality=g.iLocality.concat(clause.makeInterpretedLocality(fi, null));
			}
			g.uLocality=clause.uLocality;
			g.state=GeorefState.GEOREF_COMPLETED;
			rec.georefs.add(g);
		}
	}
	public void doSpatialDescription(Rec r){
		String version = new String("doSpatialDescription(Rec):20070916");
		String process = new String("SpatialDescriptionManager");
		if( r == null ) return;
		if( r.clauses == null || r.clauses.size() == 0) {
			r.state = RecState.REC_NO_CLAUSES_ERROR;
			return;
		}
		/*
		 * For every Clause
		 * Fill in the georefs list with georefs generated for 
		 * every combination of FeatureInfos resulting from
		 * lookupFeature on the one or two LocSpecs.
		 */
		Connection gdb = null;
		String dbname = new String("not specified");
		for( Clause clause : r.clauses) { // do feature lookups for all locspecs for every clause based on locType
			gdb = null;
			dbname=new String("not specified");
			if(clause.locType.equalsIgnoreCase("ADM")){
				gdb=gadm;
				dbname=new String("gadm");
				ProcessStep ps = new ProcessStep(process, version, "");
				clause.locspecs.get(0).featureinfos = gaz.featureQuickLookup(gdb, clause.locspecs.get(0).featurename, "equals-ignore-case", null);
				gaz.addFeatures(userplaces, clause.locspecs.get(0).featureinfos, clause.locspecs.get(0).featurename, "equals-ignore-case");
				if(clause.locspecs.get(0).featureinfos==null || clause.locspecs.get(0).featureinfos.size()==0){
					// TODO: other kinds of lookups
					ps.method=ps.method.concat("Found no features matching "+clause.locspecs.get(0).featurename+" in "+dbname+" using query type equals-ignore-case.");
				} else{
					ps.method=ps.method.concat("Found "+clause.locspecs.get(0).featureinfos.size()+" features matching "+clause.locspecs.get(0).featurename+" in "+dbname+" using query type equals-ignore-case.");
				}
				ps.endtimestamp=System.currentTimeMillis();
				r.metadata.addStep(ps);
			} else if(clause.locType.equalsIgnoreCase("F")){
				gdb=worldplaces;
				dbname=new String("worldplaces");
				ProcessStep ps = new ProcessStep(process, version, "");
				clause.locspecs.get(0).featureinfos = gaz.featureQuickLookup(gdb, clause.locspecs.get(0).featurename, "equals-ignore-case", null);
				gaz.addFeatures(userplaces, clause.locspecs.get(0).featureinfos, clause.locspecs.get(0).featurename, "equals-ignore-case");
				if(clause.locspecs.get(0).featureinfos==null || clause.locspecs.get(0).featureinfos.size()==0){
					// TODO: other kinds of lookups
					ps.method=ps.method.concat("Found no features matching "+clause.locspecs.get(0).featurename+" in "+dbname+" using query type equals-ignore-case.");
				} else{
					ps.method=ps.method.concat("Found "+clause.locspecs.get(0).featureinfos.size()+" features matching "+clause.locspecs.get(0).featurename+" in "+dbname+" using query type equals-ignore-case.");
				}
				ps.endtimestamp=System.currentTimeMillis();
				r.metadata.addStep(ps);
			} else if(clause.locType.equalsIgnoreCase("FOH")){
				gdb=worldplaces;
				dbname=new String("worldplaces");
				if(clause.locspecs.size()>0){
					clause.locspecs.get(0).interpretOffset(SupportedLanguages.english);
					clause.locspecs.get(0).interpretHeading(SupportedLanguages.english);
				}
				ProcessStep ps = new ProcessStep(process, version, "");
				clause.locspecs.get(0).featureinfos = gaz.featureQuickLookup(gdb, clause.locspecs.get(0).featurename, "equals-ignore-case", null);
				gaz.addFeatures(userplaces, clause.locspecs.get(0).featureinfos, clause.locspecs.get(0).featurename, "equals-ignore-case");
				if(clause.locspecs.get(0).featureinfos==null || clause.locspecs.get(0).featureinfos.size()==0){
					// TODO: other kinds of lookups
					ps.method=ps.method.concat("Found no features matching "+clause.locspecs.get(0).featurename+" in "+dbname+" using query type equals-ignore-case.");
				} else{
					ps.method=ps.method.concat("Found "+clause.locspecs.get(0).featureinfos.size()+" features matching "+clause.locspecs.get(0).featurename+" in "+dbname+" using query type equals-ignore-case.");
				}
				ps.endtimestamp=System.currentTimeMillis();
				r.metadata.addStep(ps);
			} else if(clause.locType.equalsIgnoreCase("FO")){
				gdb=worldplaces;
				dbname=new String("worldplaces");
				if(clause.locspecs.size()>0){
					clause.locspecs.get(0).interpretOffset(SupportedLanguages.english);
				}
				ProcessStep ps = new ProcessStep(process, version, "");
				clause.locspecs.get(0).featureinfos = gaz.featureQuickLookup(gdb, clause.locspecs.get(0).featurename, "equals-ignore-case", null);
				gaz.addFeatures(userplaces, clause.locspecs.get(0).featureinfos, clause.locspecs.get(0).featurename, "equals-ignore-case");
				if(clause.locspecs.get(0).featureinfos==null || clause.locspecs.get(0).featureinfos.size()==0){
					// TODO: other kinds of lookups
					ps.method=ps.method.concat("Found no features matching "+clause.locspecs.get(0).featurename+" in "+dbname+" using query type equals-ignore-case.");
				} else{
					ps.method=ps.method.concat("Found "+clause.locspecs.get(0).featureinfos.size()+" features matching "+clause.locspecs.get(0).featurename+" in "+dbname+" using query type equals-ignore-case.");
				}
				ps.endtimestamp=System.currentTimeMillis();
				r.metadata.addStep(ps);
			} else if(clause.locType.equalsIgnoreCase("FS")){
				gdb=worldplaces;
				dbname=new String("worldplaces");
				if(clause.locspecs.size()>0){
					clause.locspecs.get(0).interpretSubdivision(SupportedLanguages.english);
				}
				ProcessStep ps = new ProcessStep(process, version, "");
				clause.locspecs.get(0).featureinfos = gaz.featureQuickLookup(gdb, clause.locspecs.get(0).featurename, "equals-ignore-case", null);
				gaz.addFeatures(userplaces, clause.locspecs.get(0).featureinfos, clause.locspecs.get(0).featurename, "equals-ignore-case");
				if(clause.locspecs.get(0).featureinfos==null || clause.locspecs.get(0).featureinfos.size()==0){
					// TODO: other kinds of lookups
					ps.method=ps.method.concat("Found no features matching "+clause.locspecs.get(0).featurename+" in "+dbname+" using query type equals-ignore-case.");
				} else{
					ps.method=ps.method.concat("Found "+clause.locspecs.get(0).featureinfos.size()+" features matching "+clause.locspecs.get(0).featurename+" in "+dbname+" using query type equals-ignore-case.");
				}
				ps.endtimestamp=System.currentTimeMillis();
				r.metadata.addStep(ps);
			} else if(clause.locType.equalsIgnoreCase("NF")){
				gdb=worldplaces;
				dbname=new String("worldplaces");
				ProcessStep ps = new ProcessStep(process, version, "");
				clause.locspecs.get(0).featureinfos = gaz.featureQuickLookup(gdb, clause.locspecs.get(0).featurename, "equals-ignore-case", null);
				gaz.addFeatures(userplaces, clause.locspecs.get(0).featureinfos, clause.locspecs.get(0).featurename, "equals-ignore-case");
				if(clause.locspecs.get(0).featureinfos==null || clause.locspecs.get(0).featureinfos.size()==0){
					// TODO: other kinds of lookups
					ps.method=ps.method.concat("Found no features matching "+clause.locspecs.get(0).featurename+" in "+dbname+" using query type equals-ignore-case.");
				} else{
					ps.method=ps.method.concat("Found "+clause.locspecs.get(0).featureinfos.size()+" features matching "+clause.locspecs.get(0).featurename+" in "+dbname+" using query type equals-ignore-case.");
				}
				ps.endtimestamp=System.currentTimeMillis();
				r.metadata.addStep(ps);
			} else if(clause.locType.equalsIgnoreCase("FOO")){
				gdb=worldplaces;
				dbname=new String("worldplaces");
				if(clause.locspecs.size()>0){
					clause.locspecs.get(0).interpretOffsetEW(SupportedLanguages.english);
					clause.locspecs.get(0).interpretOffsetNS(SupportedLanguages.english);
					clause.locspecs.get(0).interpretHeadingEW(SupportedLanguages.english);
					clause.locspecs.get(0).interpretHeadingNS(SupportedLanguages.english);
				}
				ProcessStep ps = new ProcessStep(process, version, "");
				clause.locspecs.get(0).featureinfos = gaz.featureQuickLookup(gdb, clause.locspecs.get(0).featurename, "equals-ignore-case", null);
				gaz.addFeatures(userplaces, clause.locspecs.get(0).featureinfos, clause.locspecs.get(0).featurename, "equals-ignore-case");
				if(clause.locspecs.get(0).featureinfos==null || clause.locspecs.get(0).featureinfos.size()==0){
					// TODO: other kinds of lookups
					ps.method=ps.method.concat("Found no features matching "+clause.locspecs.get(0).featurename+" in "+dbname+" using query type equals-ignore-case.");
				} else{
					ps.method=ps.method.concat("Found "+clause.locspecs.get(0).featureinfos.size()+" features matching "+clause.locspecs.get(0).featurename+" in "+dbname+" using query type equals-ignore-case.");
				}
				ps.endtimestamp=System.currentTimeMillis();
				r.metadata.addStep(ps);
			} else if(clause.locType.equalsIgnoreCase("UNK")){
				// It might be that clauses of UNK locType are actually a feature. This happens,
				// for example, if the Yale interpreter encounters a lower-case feature name. In
				// this case try to lookup the contents of the UNK clause as if it was a Feature.
				// If it is found, change the locType to F, populate the feature name and proceed 
				// accordingly.
				gdb=worldplaces;
				dbname=new String("worldplaces");
				if(clause.locspecs.size()>0){
					ProcessStep ps = new ProcessStep(process, version, "");
					clause.locspecs.get(0).featureinfos = gaz.featureQuickLookup(gdb, clause.uLocality, "equals-ignore-case", null);
					gaz.addFeatures(userplaces, clause.locspecs.get(0).featureinfos, clause.uLocality, "equals-ignore-case");
					if(clause.locspecs.get(0).featureinfos==null || clause.locspecs.get(0).featureinfos.size()==0){
						// TODO: other kinds of lookups
						ps.method=ps.method.concat("Found no features matching "+clause.uLocality+" in "+dbname+" using query type equals-ignore-case. LocType UNK remains changed.");
					} else{
						// Features found matching clause.uLocality. Change the locType and associated variables.
						clause.locType="F";
						clause.locspecs.get(0).featurename = new String(clause.uLocality);
						ps.method=ps.method.concat("Changed locType UNK to locType F. Found "+clause.locspecs.get(0).featureinfos.size()+" features matching "+clause.locspecs.get(0).featurename+" in "+dbname+" using query type equals-ignore-case.");
					}
					ps.endtimestamp=System.currentTimeMillis();
					r.metadata.addStep(ps);
				}
			} else if(clause.locType.equalsIgnoreCase("FH") || clause.locType.equalsIgnoreCase("PH")){
				// The FH and PH locTypes are often misinterpreted F or P (e.g., "North Haven" is a feature,
				// not a heading from a feature, such as "North of Haven").
				// Test to see if a Feature can be found when the locType is FH or PH.
				gdb=worldplaces;
				dbname=new String("worldplaces");
				if(clause.locspecs.size()>0){
					ProcessStep ps = new ProcessStep(process, version, "");
					String testname = new String(clause.locspecs.get(0).vheading+" "+clause.locspecs.get(0).featurename);
					if( clause.uLocality.equalsIgnoreCase(testname)){
						// The uninterpreted clause contains the putative feature name. In other words, proceed with 
						// cases such as uLocality="North Haven", but not with uLocality="North of Haven".
						clause.locspecs.get(0).featureinfos = gaz.featureQuickLookup(gdb, testname, "equals-ignore-case", null);
						gaz.addFeatures(userplaces, clause.locspecs.get(0).featureinfos, testname, "equals-ignore-case");
						if(clause.locspecs.get(0).featureinfos==null || clause.locspecs.get(0).featureinfos.size()==0){
							// TODO: other kinds of lookups
							ps.method=ps.method.concat("Found no features matching "+testname+" in "+dbname+" using query type equals-ignore-case. LocType FH remains changed.");
						} else{
							// Features found matching testname. Change the locType and associated variables.
							if( clause.locType.equalsIgnoreCase("fh") ){
								clause.locType="F";
								ps.method=ps.method.concat("Found feature matching "+testname+" in "+dbname+" using query type equals-ignore-case. Changed locType FH to F.");
							} else if( clause.locType.equalsIgnoreCase("ph") ){
								clause.locType="P";
								ps.method=ps.method.concat("Found feature matching "+testname+" in "+dbname+" using query type equals-ignore-case. Changed locType PH to P.");
							}
							clause.locspecs.get(0).featurename = new String(testname);
							clause.locspecs.get(0).vheading = null;
							clause.locspecs.get(0).iheading = null;
						}
						ps.endtimestamp=System.currentTimeMillis();
						r.metadata.addStep(ps);
					}
				}
			} else if(clause.locType.equalsIgnoreCase("BF")){
				gdb=worldplaces;
				dbname=new String("worldplaces");
				ProcessStep ps = new ProcessStep(process, version, "");
				if(clause.locspecs.size()>1){
					clause.locspecs.get(0).featureinfos = gaz.featureQuickLookup(gdb, clause.locspecs.get(0).featurename, "equals-ignore-case", null);
					gaz.addFeatures(userplaces, clause.locspecs.get(0).featureinfos, clause.locspecs.get(0).featurename, "equals-ignore-case");
					if(clause.locspecs.get(0).featureinfos==null || clause.locspecs.get(0).featureinfos.size()==0){
						// TODO: other kinds of lookups
						ps.method=ps.method.concat("Found no features matching "+clause.locspecs.get(0).featurename+" in "+dbname+" using query type equals-ignore-case.");
					} else{
						ps.method=ps.method.concat("Found "+clause.locspecs.get(0).featureinfos.size()+" features matching "+clause.locspecs.get(0).featurename+" in "+dbname+" using query type equals-ignore-case.");
					}
					clause.locspecs.get(1).featureinfos = gaz.featureQuickLookup(gdb, clause.locspecs.get(1).featurename, "equals-ignore-case", null);
					gaz.addFeatures(userplaces, clause.locspecs.get(1).featureinfos, clause.locspecs.get(1).featurename, "equals-ignore-case");
					if(clause.locspecs.get(1).featureinfos==null || clause.locspecs.get(1).featureinfos.size()==0){
						// TODO: other kinds of lookups
						ps.method=ps.method.concat("Found no features matching "+clause.locspecs.get(1).featurename+" in "+dbname+" using query type equals-ignore-case.");
					} else{
						ps.method=ps.method.concat("Found "+clause.locspecs.get(1).featureinfos.size()+" features matching "+clause.locspecs.get(1).featurename+" in "+dbname+" using query type equals-ignore-case.");
					}
				}
				ps.endtimestamp=System.currentTimeMillis();
				r.metadata.addStep(ps);
			} else if (clause.locType.equalsIgnoreCase("P") || 
					clause.locType.equalsIgnoreCase("POH") ||
					clause.locType.equalsIgnoreCase("PO") || 
					clause.locType.equalsIgnoreCase("PH") ||
					clause.locType.equalsIgnoreCase("J") || 
					clause.locType.equalsIgnoreCase("NJ") ||
					clause.locType.equalsIgnoreCase("JOH") ||
					clause.locType.equalsIgnoreCase("JO") ||
					clause.locType.equalsIgnoreCase("JH") ||
					clause.locType.equalsIgnoreCase("JOO") ||
					clause.locType.equalsIgnoreCase("POM") ||
					clause.locType.equalsIgnoreCase("NPOM") ||
					clause.locType.equalsIgnoreCase("PS") || 
					clause.locType.equalsIgnoreCase("BP") || 
					clause.locType.equalsIgnoreCase("ADDR") || 
					clause.locType.equalsIgnoreCase("NP")){
				gdb = null; // until roads and rivers layers are installed
			} else if(clause.locType.equalsIgnoreCase("TRS") || clause.locType.equalsIgnoreCase("TRSS")){
				gdb=plss;
				dbname=new String("plss");
				ProcessStep ps = new ProcessStep(process, version, "");
				if(clause.locspecs.size()>0){
					clause.locspecs.get(0).interpretTRS();
				}
				clause.locspecs.get(0).featureinfos = gaz.featureQuickLookup(gdb, clause.locspecs.get(0).featurename, "equals-ignore-case", null);
				gaz.addFeatures(userplaces, clause.locspecs.get(0).featureinfos, clause.locspecs.get(0).featurename, "equals-ignore-case");
				if(clause.locspecs.get(0).featureinfos==null || clause.locspecs.get(0).featureinfos.size()==0){
					// TODO: other kinds of lookups
					ps.method=ps.method.concat("Found no features matching "+clause.locspecs.get(0).featurename+" in "+dbname+" using query type equals-ignore-case.");
				} else{
					ps.method=ps.method.concat("Found "+clause.locspecs.get(0).featureinfos.size()+" features matching "+clause.locspecs.get(0).featurename+" in "+dbname+" using query type equals-ignore-case.");
				}
				ps.endtimestamp=System.currentTimeMillis();
				r.metadata.addStep(ps);
			} else if(clause.locType.equalsIgnoreCase("LL") || clause.locType.equalsIgnoreCase("TRSS")){
				if(clause.locspecs.size()>0){
					clause.locspecs.get(0).interpretLatLng(clause.locspecs.get(0));
					gdb=null;
				}
			} else if(clause.locType.equalsIgnoreCase("UTM") || clause.locType.equalsIgnoreCase("TRSS")){
				if(clause.locspecs.size()>0){
					clause.locspecs.get(0).interpretUTM();
					gdb=null;
				}
			} else if(clause.locType.equalsIgnoreCase("FPOH")){
				gdb = null; // until roads and rivers layers are installed
			} else if(clause.locType.equalsIgnoreCase("JPOH")){
				gdb = null; // until roads and rivers layers are installed
			} else if(clause.locType.equalsIgnoreCase("Q")){
				gdb = null; // don't yet have data for this
			}

			if(gdb!=null){ // do other lookup types if the database is defined
				for(LocSpec locspec:clause.locspecs) { 
					/*
					if(locspec.featureinfos==null || locspec.featureinfos.size()==0){
						ProcessStep ps = new ProcessStep(process, version, "");
						locspec.featureinfos = gaz.featureQuickLookup(gdb, locspec.featurename, "contains-all-words", null);
						gaz.addFeatures(userplaces, locspec.featureinfos, locspec.featurename, "contains-all-words");
						if(locspec.featureinfos==null || locspec.featureinfos.size()==0){
							// TODO: other kinds of lookups
							ps.method=ps.method.concat("Found no features matching "+clause.locspecs.get(0).featurename+" in "+dbname+" using query type contains-all-words.");
						} else{
							ps.method=ps.method.concat("Found "+locspec.featureinfos.size()+" features matching "+locspec.featurename+" in "+dbname+" using query type contains-all-words.");
						}
						ps.endtimestamp=System.currentTimeMillis();
						r.metadata.addStep(ps);
					}
					// contains-all-words didn't work, try other method,
/*
					if(locspec.featureinfos==null || locspec.featureinfos.size()==0){
						ProcessStep ps = new ProcessStep(process, version, "");
						locspec.featureinfos = gaz.featureQuickLookup(gdb, locspec.featurename, "contains-any-words", null);
						gaz.addFeatures(userplaces, locspec.featureinfos, locspec.featurename, "contains-any-words");
						if(locspec.featureinfos==null || locspec.featureinfos.size()==0){
							// TODO: other kinds of lookups
							ps.method=ps.method.concat("Found no features matching "+clause.locspecs.get(0).featurename+" in "+dbname+" using query type contains-any-words.");
						} else{
							ps.method=ps.method.concat("Found "+locspec.featureinfos.size()+" features matching "+locspec.featurename+" in "+dbname+" using query type contains-any-words.");
						}
						ps.endtimestamp=System.currentTimeMillis();
						r.metadata.addStep(ps);
					}
/*					
					// contains-any-words didn't work, try other method,
					if(locspec.featureinfos==null || locspec.featureinfos.size()==0){
						ProcessStep ps = new ProcessStep(process, version, "");
						locspec.featureinfos = gaz.featureQuickLookup(gdb, locspec.featurename, "contains-phrase", null);
						gaz.addFeatures(userplaces, locspec.featureinfos, locspec.featurename, "contains-phrase");
						if(locspec.featureinfos==null || locspec.featureinfos.size()==0){
							// TODO: other kinds of lookups
							ps.method=ps.method.concat("Found no features matching "+clause.locspecs.get(0).featurename+" in "+dbname+" using query type contains-phrase.");
						} else{
							ps.method=ps.method.concat("Found "+locspec.featureinfos.size()+" features matching "+locspec.featurename+" in "+dbname+" using query type contains-phrase.");
						}
						ps.endtimestamp=System.currentTimeMillis();
						r.metadata.addStep(ps);
					}
					 */
					// contains phrase didn't work, try other method,
					// such as a pattern query replacing vowels with wildcard characters
					if(locspec.featureinfos==null || locspec.featureinfos.size()==0){
						// At this point, if the locspec still doesn't have any features, the lookup failed.
						clause.state = ClauseState.CLAUSE_FEATURE_NOT_FOUND_ERROR;
						locspec.state = LocSpecState.LOCSPEC_ERROR_FEATURE_NOT_FOUND;
					}
				}
			}

			// LocSpec Feature lookups are done. Make Shapes for this Clause.
			if(clause.locspecs.get(0).featureinfos != null){
				for(int i=0;i<clause.locspecs.get(0).featureinfos.size();i++) { // for every featureinfo in LocSpec1
					FeatureInfo featureinfo1 = clause.locspecs.get(0).featureinfos.get(i);
					FeatureInfo featureinfo2 = null;
					PointRadius pr = null;
					if(clause.locspecs.size() > 1 && clause.locspecs.get(1).featureinfos.size() > 0) { // there are features for the second locspec
						for(int j=0;j<clause.locspecs.get(1).featureinfos.size();j++) { // for every feature info in LocSpec2
							featureinfo2 = clause.locspecs.get(1).featureinfos.get(j);
							pr = sm.getPointRadius( clause.locType, clause.locspecs.get(0), featureinfo1, featureinfo2 );
							// Point-radius was successfully created, now add it to the clause's georefs list.
							if( pr != null) {
								Georef g = new Georef(pr);
								g.confidence=0;
								if(g.iLocality==null || g.iLocality.trim().length()==0){
									g.iLocality=clause.makeInterpretedLocality(featureinfo1, featureinfo2);
								}else{
									g.iLocality=g.iLocality.concat(clause.makeInterpretedLocality(featureinfo1, featureinfo2));
								}
								g.uLocality=clause.uLocality;
								g.addFeatureInfo(featureinfo1);
								g.addFeatureInfo(featureinfo2);
								clause.state=ClauseState.CLAUSE_POINT_RADIUS_COMPLETED;
								clause.georefs.add(g);
							}
						}
					}else { // there is no second locspec
						try{
							pr = sm.getPointRadius( clause.locType, clause.locspecs.get(0), featureinfo1, featureinfo2 );
						} catch (Exception e) {
							e.printStackTrace();
						}
						// Point-radius was successfully created, now add it to the clause's georefs list.

						if( pr != null ) {
							Georef g = new Georef(pr);
							g.confidence=0;
							g.uLocality=clause.uLocality;
							if(g.iLocality==null || g.iLocality.trim().length()==0){
								g.iLocality=clause.makeInterpretedLocality(featureinfo1, featureinfo2);
							}else{
								g.iLocality=g.iLocality.concat(clause.makeInterpretedLocality(featureinfo1, featureinfo2));
							}
							g.uLocality=clause.uLocality;
							g.addFeatureInfo(featureinfo1);
							g.state=GeorefState.GEOREF_COMPLETED;
							clause.state=ClauseState.CLAUSE_POINT_RADIUS_COMPLETED;
							clause.georefs.add(g);
						}
					}
				}
			}
		} 
		/*
		 * Now that georefs have been generated for the clauses, remove any duplicate georefs within a given clause. 
		 * TODO: Determined that this is probably not necessary.
		 */
		
		for( Clause clause : r.clauses) { // do feature lookups for all locspecs for every clause
			for(int i=0;i<clause.georefs.size();i++){
				for(int j=0;j<clause.georefs.size();j++){
					if(i!=j){
						if(clause.georefs.get(i).equals(clause.georefs.get(j))){
							clause.georefs.remove(j);
							j--;
						}
					}
				}
			}
		}
		
		/*
		 * Point-radius creation has been attempted for all Clauses in the Rec. 
		 * Now do a spatial intersection on all combinations of georefs across clauses. 
		 * The number of possible georefs arising from the intersections of clauses 
		 * is the product of the number of successfully created georefs for each clause.
		 */
		if(r.getGeorefedClauseCount()==0){
			r.state = RecState.REC_NO_GEOREFERENCE_ERROR;
			return;
		}
		int clausecount=r.clauses.size(); // Total number of Clauses in the Rec.
//		int viableclausecount = r.getGeorefedClauseCount(); // Number of Clauses that have at least one viable georef.
		int combos=0; // Total number of combinations of viable Clauses.
		int[] gcounts=null; // Number of Georefs for the Clause at each index
		for(int n=0;n<clausecount;n++) {
			int viablegeorefcount = r.clauses.get(n).viableGeorefCount();
			if( viablegeorefcount>0 ){
				if(combos==0) combos=viablegeorefcount;
				else combos*=viablegeorefcount;
			}
		}
		gcounts=new int[clausecount];
		for(int j=0;j<clausecount;j++){
			int viablegeorefs = r.clauses.get(j).viableGeorefCount(); 
			gcounts[j] = viablegeorefs;
		}

		// find the max number of combos
		int size = 1;
		for (int k = 0; k < gcounts.length; k++) {
			if(gcounts[k] != 0)
				size = size * gcounts[k];
		}

		// Create an array to hold the combinations of georef indexes to do intersections on.
		int[][] geoCombos = new int[size][gcounts.length];

		// create a array to store the current combo
		int[] curr = new int[ gcounts.length];

		// populate the new array with 0's for the beginning
		for (int k = 0; k < gcounts.length; k++) {
			if(gcounts[k] == 0){
				curr[k] = -1;
				continue;
			}
			curr[k] = 0;
		}

		// loop through each combo
		for (int x = 0; x < geoCombos.length; x++) {

			geoCombos[x] = curr.clone(); // add the new combo to the list

			// loop through each location in the current combo
			// essentially works like a backwards mileage counter with each number
			// place having a different base
			for (int j = 0; j < curr.length; j++) {
				//check for invalid clauses
				if(curr[j] == -1){
					continue;
				}
				curr[j] = curr[j] + 1;// increase the value of current location
				if (curr[j] == gcounts[j]) {
					curr[j] = 0; // if current location is too big, drop it to zero and
					// move to next
					continue;
				}
				break;// otherwise break and add this new unique combo
			}

		}


		GeometryFactory gf = new GeometryFactory();
		WKTReader wktreader = new WKTReader(gf);	
		Georef newGeoref = null;
//		Georef g;
		Geometry geom;
		String encodedG = null;

		Georef g1 = null, intersection = null;
		double distancebetweencenters=0, sumofradii=0;
		boolean foundFirstValid = false;
//		ArrayList<FeatureInfo> tempFeatureinfos;
		String loctype;
		int featureid;
		for(int m=0;m<combos;m++) { // For every combo of Clause Georefs
			g1=intersection=null;
			for(int i=0;i<clausecount;i++) { // For every Clause in the Rec
				if(i == 0){
					foundFirstValid = false;
				}

				if(geoCombos[m][i] == -1){
					continue;
				}
				g1=r.clauses.get(i).georefs.get(geoCombos[m][i]);
				loctype = r.clauses.get(i).locType;
				if(!foundFirstValid){ 
					// first VALID clause, intersection is just that clause or
					// the geometry for the feature if the loctype is one of the
					// feature-only loctypes.
					foundFirstValid=true;
					if(loctype.equalsIgnoreCase("F") || loctype.equalsIgnoreCase("ADM") ||
							loctype.equalsIgnoreCase("P") || loctype.equalsIgnoreCase("TRS")){
						// Use the actual shape for the intersection instead of the point-radius.
						featureid = g1.featureinfos.get(0).featureID;
						String csource = new String(g1.featureinfos.get(0).coordSource);
						if(csource != null && csource.equalsIgnoreCase("usersdb")){
							encodedG = new String(gaz.lookupFootprint(userplaces, featureid));
						} 
						else if(loctype.equalsIgnoreCase("ADM")){
							encodedG = new String(gaz.lookupFootprint(gadm, featureid));
						}
						else if(loctype.equalsIgnoreCase("F")){
							encodedG = new String(gaz.lookupFootprint(worldplaces, featureid));
						}
						//TODO change this to be roads or rivers when they get added to the Gazetteer
						else if(loctype.equalsIgnoreCase("P")){
							encodedG = new String(gaz.lookupFootprint(worldplaces, featureid));
						}
						else if(loctype.equalsIgnoreCase("TRS")){
							encodedG = new String(gaz.lookupFootprint(plss, featureid));
						}
						try {
							geom = wktreader.read(encodedG);
							if(geom.getDimension()==0){ 
								// feature is a point in the gazetteer
								intersection = g1;
							} else {
								// feature has a footprint in the gazetteer
								intersection = new Georef(geom, DatumManager.getInstance().getDatum("WGS84"));
								intersection.iLocality=new String(g1.iLocality);
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}
					} 
					else{
						intersection=g1;
					}
				} else{ 
					// clause beyond the first, may have real intersection
					// Check to see if the distances between points is greater
					// than the sum of the radii of the features. If so, there is no overlap. 
					distancebetweencenters = g1.getDistanceToGeorefCentroid(intersection);
					sumofradii = g1.pointRadius.extent+intersection.pointRadius.extent;
					if(distancebetweencenters<=sumofradii){
						// there is a non-point intersection 
						// For any of the feature-only loctypes
						if(loctype.equalsIgnoreCase("F") || loctype.equalsIgnoreCase("ADM") ||
								loctype.equalsIgnoreCase("P") || loctype.equalsIgnoreCase("TRS")){
							// Use the actual shape for the intersection instead of the point-radius.
							featureid = g1.featureinfos.get(0).featureID;
							String csource = new String(g1.featureinfos.get(0).coordSource);
							if(csource != null && csource.equalsIgnoreCase("usersdb")){
								encodedG = new String(gaz.lookupFootprint(userplaces, featureid));
							} 
							else if(loctype.equalsIgnoreCase("ADM")){
								encodedG = new String(gaz.lookupFootprint(gadm, featureid));
							}
							else if(loctype.equalsIgnoreCase("F")){
								encodedG = new String(gaz.lookupFootprint(worldplaces, featureid));
							}
							//TODO change this to be roads or rivers when they get added to the Gazetteer
							else if(loctype.equalsIgnoreCase("P")){
								encodedG = new String(gaz.lookupFootprint(worldplaces, featureid));
							}
							else if(loctype.equalsIgnoreCase("TRS")){
								encodedG = new String(gaz.lookupFootprint(plss, featureid));
							}
							try {
								// Test the old intersection against the real geometries
								geom = wktreader.read(encodedG);
								if(geom.getDimension()==0){ 
									// feature is a point in the gazetteer, use the point-radius
									newGeoref = g1;
								} else {
									// feature has a footprint in the gazetteer
									newGeoref = new Georef(geom, DatumManager.getInstance().getDatum("WGS84"));
									newGeoref.iLocality=new String(g1.iLocality);
								}
							} catch (ParseException e) {
								e.printStackTrace();
							}
							intersection=intersection.intersect(newGeoref);
						} 
						else{
							intersection=intersection.intersect(g1);
						}
						// copy the featureinfos used in the intersecting georef
						// to the georef for the resulting intersection
						for(FeatureInfo f: g1.featureinfos){
							try{
								if(intersection!=null){
									intersection.addFeatureInfo(f);
//									featureid = f.featureID;
//									intersection.addFeatureLoctype(f.featureID,loctype);
								}
							} catch (Exception e) {
								System.out.println("Problem with addFeatureInfo to intersection for the following feature:\n"+f.toXML(true));
								System.out.println("from the following intersection:\n"+intersection.toXML(true));
								e.printStackTrace();
							}
						}
					} else { 
						// there is no intersection between g1 and g2 based on distance between centers
						// and point-radiuses
						intersection=null;
						i=clausecount; // no reason to even try other clauses, because there is already an inconsistency
					}
				}
			}
			if( intersection != null) {
				r.georefs.add(intersection);
			}
		}
		// Remove duplicate georeferences, if any
		for(int i=0;i<r.georefs.size();i++){
			for(int j=0;j<r.georefs.size();j++){
				if(i!=j){
					if(r.georefs.get(i).equals(r.georefs.get(j))){
						r.georefs.remove(j);
						j--;
					}
				}
			}
		}
	}
}
//		***
/*
		//Double check the intersection based on actual geometry
		String locType;
		Georef newIntersection = null;
		ArrayList<Georef> newIntersections = new ArrayList<Georef>();
		boolean invalidLocType = false;
		Hashtable<Integer, Georef> featureIdGeometry = new Hashtable<Integer, Georef>(); 


		//Go through each found intersection from previous section
		for(int i = 0; i < r.georefs.size(); i++){
			g = r.georefs.get(i);
			newIntersection = g; //set it to the new intersection
			for(FeatureInfo f : g.featureinfos){
				if(featureIdGeometry.contains(f.featureID)){
					newGeoref = featureIdGeometry.get(f.featureID);
				}
				else{
					//Query for the real geometry
					locType = g.getFeatureLoctype(f.featureID);
					if(locType.equalsIgnoreCase("ADM")){
						encodedG = new String(gaz.lookupFootprint(gadm, f.featureID));
					}
					else if(locType.equalsIgnoreCase("F")){
						encodedG = new String(gaz.lookupFootprint(worldplaces, f.featureID));
					}
					//TODO change this to be roads or rivers when they get added to the Gazetteer
					else if(locType.equalsIgnoreCase("P")){
						encodedG = new String(gaz.lookupFootprint(worldplaces, f.featureID));
					}
					else if(locType.equalsIgnoreCase("TRS")){
						encodedG = new String(gaz.lookupFootprint(plss, f.featureID));
					}
					else{
						invalidLocType = true;
						break;
					}

					try {
						//Test the old intersection against the real geometries
						geom = wktreader.read(encodedG);
						if(geom.getDimension()==0){ // feature is a point in the gazetteer
							newGeoref = g;
						} else {
							newGeoref = new Georef(geom, DatumManager.getInstance().getDatum("WGS84"));
						}
						featureIdGeometry.put(f.featureID, newGeoref);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(newIntersection==null){
					newIntersection=newGeoref;
				} else{
					newIntersection = newGeoref.intersect(newIntersection);			
				}//if the new intersection becomes null, just break
				//since it's no longer valid
				if(newIntersection == null){
					break;
				}
			}

			if(invalidLocType){
				invalidLocType = false;
				newIntersections.add(r.georefs.get(i));
				continue;
			}
			//If there is a new valid intersection, replace the old one,
			//otherwise do nothing since there was no intersection
			if(newIntersection != null){
				newIntersections.add(r.georefs.get(i));
			}
		}
		r.georefs = newIntersections;
	}
//	***
}
*/
/*
for(int i=0;i<locspec.featureinfos.size();i++){
	FeatureInfo f = locspec.featureinfos.get(i);
	if(f.extentInMeters < 1){ 
		// TODO: This same work is being done by the gazetteer. Doesn't
		// hurt to keep it here for now.
		// Try making a PointRadius from the encoded Geometry in the event
		// that the radius given in the database is less than a meter.
//		log.error("SpatialDescriptionManager found feature extent < 1. Check database for "+f.featureID);
		GeometryFactory gf = new GeometryFactory();
		WKTReader wktreader = new WKTReader(gf);
		Geometry g=null;
		try {
			g = wktreader.read(f.encodedGeometry);
			Georef georef = new Georef(g, DatumManager.getInstance().getDatum("WGS84"));
			PointRadius pr = georef.makePointRadius(g, DatumManager.getInstance().getDatum("WGS84"));
			if(pr.extent>=1) f.extentInMeters=pr.extent;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(f.extentInMeters < 1){
			// Try getting the best guess uncertainty for the feature based
			// on its feature type.
			double d =gaz.lookupBestGuessUncertainty(gdb, f.featureID); 
			if(d>=1) f.extentInMeters=d;
		}
		if(f.extentInMeters < 1){
			// Try getting the i_feature_footprint.radius of this feature
//			log.error("Should never have to look up i_feature_footprint.radius. Feature_id: "+f.featureID);
			// on its feature type.
			double d =gaz.lookupRadius(gdb, f.featureID); 
			if(d>=1) f.extentInMeters=d;
		}
		if(f.extentInMeters < 1){
			// The extent is still less than a meter. This feature is a candidate
			// for correction, supplementation, or removal from the database.
			log.error("Feature removed for lack of extent: "+f.name+"(FeatureID="+f.featureID+", ExtentInMeters="+f.extentInMeters+") ");
			locspec.featureinfos.remove(f);
			i--;
		}
	}
}
 */
