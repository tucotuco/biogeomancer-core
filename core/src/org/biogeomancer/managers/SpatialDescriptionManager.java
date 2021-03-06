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

import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.biogeomancer.records.Clause;
import org.biogeomancer.records.ClauseState;
import org.biogeomancer.records.FeatureInfo;
import org.biogeomancer.records.Georef;
import org.biogeomancer.records.GeorefState;
import org.biogeomancer.records.LocSpec;
import org.biogeomancer.records.LocSpecState;
import org.biogeomancer.records.ProcessStep;
import org.biogeomancer.records.Rec;
import org.biogeomancer.records.RecState;
import org.biogeomancer.records.RecSet.RecSetException;
import org.biogeomancer.utils.Concepts;
import org.biogeomancer.utils.PointRadius;
import org.biogeomancer.utils.SupportedLanguages;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

public class SpatialDescriptionManager extends BGManager {
  public static ADLGazetteer gaz;
  public static final String PROPS_FILE = "SpatialDescriptionManager.properties";

  public static ShapeManager sm;
  private static Logger log = Logger.getLogger(SpatialDescriptionManager.class);
  private static Properties props = new Properties();
  static {
    props = new Properties();
    initProps(PROPS_FILE, props);
  }

  public static void main(String[] args) throws MalformedURLException,
      RecSetException {
    if (args.length == 0) {
      System.out.println("featureid argument required");
      return;
    }
    Integer z = new Integer(args[0]);
    int featureid = z.intValue();

    SpatialDescriptionManager sdm = new SpatialDescriptionManager();
    Rec rec = new Rec();
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
    pr = sm.getPointRadius(clause.locType, clause.locspecs.get(0), fi, null);
    // Point-radius was successfully created, now add it to the clause's georefs
    // list.
    if (pr != null) {
      Georef g = new Georef(pr);
      g.confidence = 0;
      g.uLocality = clause.uLocality;
      if (g.iLocality == null || g.iLocality.trim().length() == 0) {
        g.iLocality = clause.makeInterpretedLocality(fi, null, true, true);
      } else {
        g.iLocality = g.iLocality.concat(clause.makeInterpretedLocality(fi,
            null, true, true));
      }
      g.uLocality = clause.uLocality;
      g.state = GeorefState.GEOREF_COMPLETED;
      rec.georefs.add(g);
    }

    sdm.doNewSpatialDescription(rec, true, true);
    System.out.println(rec);
    sdm.shutdown();
  }

  public SpatialDescriptionManager() {
    startup();
  }

  public void addUserFeature(Rec r, String user, int scheme_term_id) {
    if (r.georefs.size() != 1) {
      // There can be only one Georef for a user Feature
      // to be loaded into the gazetteer. If there is more than
      // one, something isn't a it should be.
      return;
    }
    Clause featureclause = null;
    for (Clause c : r.clauses) {
      if (c.locType.equalsIgnoreCase("f")) {
        featureclause = c;
      }
    }
    if (featureclause == null)
      return;
    try {
      gaz.insertFeature(userplaces, r.georefs.get(0), user,
          featureclause.uLocality, scheme_term_id);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void doNewSpatialDescription(Rec r, boolean showSource,
      boolean showType) {
    String version = new String("doNewSpatialDescription(Rec):20080202");
    String process = new String("SpatialDescriptionManager");
    if (r == null)
      return;
    if (r.clauses == null || r.clauses.size() == 0) {
      r.state = RecState.REC_NO_CLAUSES_ERROR;
      return;
    }
    long starttime = System.currentTimeMillis();

    // Check for ADM subtypes within clauses in the Rec.
    reassessClauseLocTypes(r);
    long endtime = System.currentTimeMillis();
    System.out.println("reassessClauseLocTypes: " + (endtime - starttime)
        + " ms");
    System.out.println(r.getCounts("  "));

    // Load features for all clauses in the Rec.
    starttime = System.currentTimeMillis();
    getPutativeFeatures(r);
    endtime = System.currentTimeMillis();
    System.out.println("getPutativeFeatures: " + (endtime - starttime) + " ms");
    System.out.println(r.getCounts("  "));

    // Remove irrelevant features - ones that don't have BB overlap with
    // features in any other clause.
    starttime = System.currentTimeMillis();
    // removeNonmatchingFeatures(r);
    removeNonoverlappingFeatures(r);
    endtime = System.currentTimeMillis();
    System.out.println("removeNonoverlappingFeatures: " + (endtime - starttime)
        + " ms");
    System.out.println(r.getCounts("  "));

    // Metadata capture is slow - move this until after BB filter? Flatten
    // gazetteer?
    // Get feature metadata for the final candidate features
    // starttime = System.currentTimeMillis();
    // getFeatureMetadata(r);
    // endtime = System.currentTimeMillis();
    // System.out.println("getFeatureMetadata: "+(endtime-starttime)+" ms");

    // Make Georefs for all clauses in the Rec.
    starttime = System.currentTimeMillis();
    makeClauseGeorefs(r, showSource, showType);
    endtime = System.currentTimeMillis();
    System.out.println("makeClauseGeorefs: " + (endtime - starttime) + " ms");

    // Make Georefs for the Rec
    starttime = System.currentTimeMillis();
    makeRecGeorefs(r);
    endtime = System.currentTimeMillis();
    System.out.println("makeRecGeorefs: " + (endtime - starttime) + " ms");
  }

  public Geometry getGeomSubdivision(Geometry geom, String subdivision) {
    if (subdivision == null || subdivision.length() == 0)
      return geom;
    String remainder = new String(subdivision);
    while (remainder.trim().length() > 0) {

      if (remainder.lastIndexOf(" 1/") > remainder.indexOf(" 1/")) {
        geom = subdivide(geom, remainder.substring(
            remainder.lastIndexOf(" 1/") - 2).trim());
        remainder = new String(remainder.substring(0, remainder
            .lastIndexOf(" 1/") - 2));
      } else {
        geom = subdivide(geom, remainder.trim());
        remainder = new String("");
      }
    }
    return geom;
  }

  /*
   * public void getFeatureMetadata(Rec r){ Connection gdb = null; for(Clause
   * clause : r.clauses){ gdb = null;
   * if(clause.locType.toUpperCase().contains("ADM")){ gdb=gadm; } else
   * if(clause.locType.equalsIgnoreCase("TRS")){ gdb=plss; } else {
   * gdb=worldplaces; } for(LocSpec locspec : clause.locspecs){ for(FeatureInfo
   * f : locspec.featureinfos){ String csource = f.coordSource; if(csource !=
   * null && csource.equalsIgnoreCase("usersdb")){
   * gaz.lookupFeatureMetadata(userplaces, f); } else {
   * gaz.lookupFeatureMetadata(gdb, f); } } } } }
   */
  public void getPutativeFeatures(Rec r) {
    String version = new String("getPutativeFeatures(Rec):20080202");
    String process = new String("SpatialDescriptionManager");
    if (r == null)
      return;
    if (r.clauses == null || r.clauses.size() == 0) {
      r.state = RecState.REC_NO_CLAUSES_ERROR;
      return;
    }

    // For Every LocSpec in every Clause, populate the featureinfos

    Connection gdb = null;
    String dbname = new String("not specified");
    for (Clause clause : r.clauses) { // do feature lookups for all locspecs for
      // every clause based on locType
      gdb = null;
      dbname = new String("not specified");
      if (clause.locType.toUpperCase().contains("ADM")) {
        int featuretype = 0;
        if (clause.locType.equalsIgnoreCase("ADM0")) {
          featuretype = 754; // countries
        } else if (clause.locType.equalsIgnoreCase("ADM1")) {
          featuretype = 755; // countries, 1st order divisions
        } else if (clause.locType.equalsIgnoreCase("ADM2")) {
          featuretype = 756;
        }
        gdb = gadm;
        dbname = new String("gadm");
        ProcessStep ps = new ProcessStep(process, version, "");
        clause.locspecs.get(0).featureinfos = gaz.featureInfoLookup(gdb,
            clause.locspecs.get(0).featurename, featuretype,
            "equals-ignore-case", null);
        gaz.addFeatures(userplaces, clause.locspecs.get(0).featureinfos,
            clause.locspecs.get(0).featurename, "equals-ignore-case");
        if (clause.locspecs.get(0).featureinfos == null
            || clause.locspecs.get(0).featureinfos.size() == 0) {
          // TODO: other kinds of lookups
          ps.method = ps.method.concat("Found no features matching "
              + clause.locspecs.get(0).featurename + " in " + dbname
              + " using query type equals-ignore-case.");
        } else {
          ps.method = ps.method.concat("Found "
              + clause.locspecs.get(0).featureinfos.size()
              + " features matching " + clause.locspecs.get(0).featurename
              + " in " + dbname + " using query type equals-ignore-case.");
        }
        ps.endtimestamp = System.currentTimeMillis();
        r.metadata.addStep(ps);
      } else if (clause.locType.equalsIgnoreCase("F")) {
        gdb = worldplaces;
        dbname = new String("worldplaces");
        ProcessStep ps = new ProcessStep(process, version, "");
        clause.locspecs.get(0).featureinfos = gaz.featureInfoLookup(gdb,
            clause.locspecs.get(0).featurename, 0, "equals-ignore-case", null);
        // The following could be made into a parentlookup as well, but not
        // currently worth the trouble
        gaz.addFeatures(userplaces, clause.locspecs.get(0).featureinfos,
            clause.locspecs.get(0).featurename, "equals-ignore-case");
        if (clause.locspecs.get(0).featureinfos == null
            || clause.locspecs.get(0).featureinfos.size() == 0) {
          // TODO: other kinds of lookups
          ps.method = ps.method.concat("Found no features matching "
              + clause.locspecs.get(0).featurename + " in " + dbname
              + " using query type equals-ignore-case.");
        } else {
          ps.method = ps.method.concat("Found "
              + clause.locspecs.get(0).featureinfos.size()
              + " features matching " + clause.locspecs.get(0).featurename
              + " in " + dbname + " using query type equals-ignore-case.");
        }
        ps.endtimestamp = System.currentTimeMillis();
        r.metadata.addStep(ps);
      } else if (clause.locType.equalsIgnoreCase("FOH")) {
        gdb = worldplaces;
        dbname = new String("worldplaces");
        if (clause.locspecs.size() > 0) {
          clause.locspecs.get(0).interpretOffset(SupportedLanguages.english);
          clause.locspecs.get(0).interpretHeading(SupportedLanguages.english);
        }
        ProcessStep ps = new ProcessStep(process, version, "");
        clause.locspecs.get(0).featureinfos = gaz.featureInfoLookup(gdb,
            clause.locspecs.get(0).featurename, 0, "equals-ignore-case", null);
        // The following could be made into a parentlookup as well, but not
        // currently worth the trouble
        gaz.addFeatures(userplaces, clause.locspecs.get(0).featureinfos,
            clause.locspecs.get(0).featurename, "equals-ignore-case");
        if (clause.locspecs.get(0).featureinfos == null
            || clause.locspecs.get(0).featureinfos.size() == 0) {
          // TODO: other kinds of lookups
          ps.method = ps.method.concat("Found no features matching "
              + clause.locspecs.get(0).featurename + " in " + dbname
              + " using query type equals-ignore-case.");
        } else {
          ps.method = ps.method.concat("Found "
              + clause.locspecs.get(0).featureinfos.size()
              + " features matching " + clause.locspecs.get(0).featurename
              + " in " + dbname + " using query type equals-ignore-case.");
        }
        ps.endtimestamp = System.currentTimeMillis();
        r.metadata.addStep(ps);
      } else if (clause.locType.equalsIgnoreCase("FO")) {
        gdb = worldplaces;
        dbname = new String("worldplaces");
        if (clause.locspecs.size() > 0) {
          clause.locspecs.get(0).interpretOffset(SupportedLanguages.english);
        }
        ProcessStep ps = new ProcessStep(process, version, "");
        clause.locspecs.get(0).featureinfos = gaz.featureInfoLookup(gdb,
            clause.locspecs.get(0).featurename, 0, "equals-ignore-case", null);
        // The following could be made into a parentlookup as well, but not
        // currently worth the trouble
        gaz.addFeatures(userplaces, clause.locspecs.get(0).featureinfos,
            clause.locspecs.get(0).featurename, "equals-ignore-case");
        if (clause.locspecs.get(0).featureinfos == null
            || clause.locspecs.get(0).featureinfos.size() == 0) {
          // TODO: other kinds of lookups
          ps.method = ps.method.concat("Found no features matching "
              + clause.locspecs.get(0).featurename + " in " + dbname
              + " using query type equals-ignore-case.");
        } else {
          ps.method = ps.method.concat("Found "
              + clause.locspecs.get(0).featureinfos.size()
              + " features matching " + clause.locspecs.get(0).featurename
              + " in " + dbname + " using query type equals-ignore-case.");
        }
        ps.endtimestamp = System.currentTimeMillis();
        r.metadata.addStep(ps);
      } else if (clause.locType.equalsIgnoreCase("FS")) {
        gdb = worldplaces;
        dbname = new String("worldplaces");
        if (clause.locspecs.size() > 0) {
          clause.locspecs.get(0).interpretSubdivision(
              SupportedLanguages.english);
        }
        ProcessStep ps = new ProcessStep(process, version, "");
        clause.locspecs.get(0).featureinfos = gaz.featureInfoLookup(gdb,
            clause.locspecs.get(0).featurename, 0, "equals-ignore-case", null);
        // The following could be made into a parentlookup as well, but not
        // currently worth the trouble
        gaz.addFeatures(userplaces, clause.locspecs.get(0).featureinfos,
            clause.locspecs.get(0).featurename, "equals-ignore-case");
        if (clause.locspecs.get(0).featureinfos == null
            || clause.locspecs.get(0).featureinfos.size() == 0) {
          // TODO: other kinds of lookups
          ps.method = ps.method.concat("Found no features matching "
              + clause.locspecs.get(0).featurename + " in " + dbname
              + " using query type equals-ignore-case.");
        } else {
          ps.method = ps.method.concat("Found "
              + clause.locspecs.get(0).featureinfos.size()
              + " features matching " + clause.locspecs.get(0).featurename
              + " in " + dbname + " using query type equals-ignore-case.");
        }
        ps.endtimestamp = System.currentTimeMillis();
        r.metadata.addStep(ps);
      } else if (clause.locType.equalsIgnoreCase("NF")) {
        gdb = worldplaces;
        dbname = new String("worldplaces");
        ProcessStep ps = new ProcessStep(process, version, "");
        clause.locspecs.get(0).featureinfos = gaz.featureInfoLookup(gdb,
            clause.locspecs.get(0).featurename, 0, "equals-ignore-case", null);
        // The following could be made into a parentlookup as well, but not
        // currently worth the trouble
        gaz.addFeatures(userplaces, clause.locspecs.get(0).featureinfos,
            clause.locspecs.get(0).featurename, "equals-ignore-case");
        if (clause.locspecs.get(0).featureinfos == null
            || clause.locspecs.get(0).featureinfos.size() == 0) {
          // TODO: other kinds of lookups
          ps.method = ps.method.concat("Found no features matching "
              + clause.locspecs.get(0).featurename + " in " + dbname
              + " using query type equals-ignore-case.");
        } else {
          ps.method = ps.method.concat("Found "
              + clause.locspecs.get(0).featureinfos.size()
              + " features matching " + clause.locspecs.get(0).featurename
              + " in " + dbname + " using query type equals-ignore-case.");
        }
        ps.endtimestamp = System.currentTimeMillis();
        r.metadata.addStep(ps);
      } else if (clause.locType.equalsIgnoreCase("FOO")) {
        gdb = worldplaces;
        dbname = new String("worldplaces");
        if (clause.locspecs.size() > 0) {
          clause.locspecs.get(0).interpretOffsetEW(SupportedLanguages.english);
          clause.locspecs.get(0).interpretOffsetNS(SupportedLanguages.english);
          clause.locspecs.get(0).interpretHeadingEW(SupportedLanguages.english);
          clause.locspecs.get(0).interpretHeadingNS(SupportedLanguages.english);
        }
        ProcessStep ps = new ProcessStep(process, version, "");
        clause.locspecs.get(0).featureinfos = gaz.featureInfoLookup(gdb,
            clause.locspecs.get(0).featurename, 0, "equals-ignore-case", null);
        // The following could be made into a parentlookup as well, but not
        // currently worth the trouble
        gaz.addFeatures(userplaces, clause.locspecs.get(0).featureinfos,
            clause.locspecs.get(0).featurename, "equals-ignore-case");
        if (clause.locspecs.get(0).featureinfos == null
            || clause.locspecs.get(0).featureinfos.size() == 0) {
          // TODO: other kinds of lookups
          ps.method = ps.method.concat("Found no features matching "
              + clause.locspecs.get(0).featurename + " in " + dbname
              + " using query type equals-ignore-case.");
        } else {
          ps.method = ps.method.concat("Found "
              + clause.locspecs.get(0).featureinfos.size()
              + " features matching " + clause.locspecs.get(0).featurename
              + " in " + dbname + " using query type equals-ignore-case.");
        }
        ps.endtimestamp = System.currentTimeMillis();
        r.metadata.addStep(ps);
      } else if (clause.locType.equalsIgnoreCase("UNK")) {
        // It might be that clauses of UNK locType are actually a feature. This
        // happens,
        // for example, if the Yale interpreter encounters a lower-case feature
        // name. In
        // this case try to lookup the contents of the UNK clause as if it was a
        // Feature.
        // If it is found, change the locType to F, populate the feature name
        // and proceed
        // accordingly.
        gdb = worldplaces;
        dbname = new String("worldplaces");
        if (clause.locspecs.size() > 0) {
          ProcessStep ps = new ProcessStep(process, version, "");
          clause.locspecs.get(0).featureinfos = gaz.featureInfoLookup(gdb,
              clause.uLocality, 0, "equals-ignore-case", null);
          // The following could be made into a parentlookup as well, but not
          // currently worth the trouble
          gaz.addFeatures(userplaces, clause.locspecs.get(0).featureinfos,
              clause.uLocality, "equals-ignore-case");
          if (clause.locspecs.get(0).featureinfos == null
              || clause.locspecs.get(0).featureinfos.size() == 0) {
            // TODO: other kinds of lookups
            ps.method = ps.method
                .concat("Found no features matching "
                    + clause.uLocality
                    + " in "
                    + dbname
                    + " using query type equals-ignore-case. LocType UNK remains changed.");
          } else {
            // Features found matching clause.uLocality. Change the locType and
            // associated variables.
            clause.locType = "F";
            clause.locspecs.get(0).featurename = new String(clause.uLocality);
            ps.method = ps.method
                .concat("Changed locType UNK to locType F. Found "
                    + clause.locspecs.get(0).featureinfos.size()
                    + " features matching "
                    + clause.locspecs.get(0).featurename + " in " + dbname
                    + " using query type equals-ignore-case.");
          }
          ps.endtimestamp = System.currentTimeMillis();
          r.metadata.addStep(ps);
        }
      } else if (clause.locType.equalsIgnoreCase("FH")
          || clause.locType.equalsIgnoreCase("PH")) {
        // The FH and PH locTypes are often misinterpreted F or P (e.g., "North
        // Haven" is a feature,
        // not a heading from a feature, such as "North of Haven").
        // Test to see if a Feature can be found when the locType is FH or PH.
        gdb = worldplaces;
        dbname = new String("worldplaces");
        if (clause.locspecs.size() > 0) {
          ProcessStep ps = new ProcessStep(process, version, "");
          String testname = new String(clause.locspecs.get(0).vheading + " "
              + clause.locspecs.get(0).featurename);
          if (clause.uLocality.equalsIgnoreCase(testname)) {
            // The uninterpreted clause contains the putative feature name. In
            // other words, proceed with
            // cases such as uLocality="North Haven", but not with
            // uLocality="North of Haven".
            clause.locspecs.get(0).featureinfos = gaz.featureInfoLookup(gdb,
                testname, 0, "equals-ignore-case", null);
            // The following could be made into a parentlookup as well, but not
            // currently worth the trouble
            gaz.addFeatures(userplaces, clause.locspecs.get(0).featureinfos,
                testname, "equals-ignore-case");
            if (clause.locspecs.get(0).featureinfos == null
                || clause.locspecs.get(0).featureinfos.size() == 0) {
              // TODO: other kinds of lookups
              ps.method = ps.method
                  .concat("Found no features matching "
                      + testname
                      + " in "
                      + dbname
                      + " using query type equals-ignore-case. LocType FH remains changed.");
            } else {
              // Features found matching testname. Change the locType and
              // associated variables.
              if (clause.locType.equalsIgnoreCase("fh")) {
                clause.locType = "F";
                ps.method = ps.method
                    .concat("Found feature matching "
                        + testname
                        + " in "
                        + dbname
                        + " using query type equals-ignore-case. Changed locType FH to F.");
              } else if (clause.locType.equalsIgnoreCase("ph")) {
                clause.locType = "P";
                ps.method = ps.method
                    .concat("Found feature matching "
                        + testname
                        + " in "
                        + dbname
                        + " using query type equals-ignore-case. Changed locType PH to P.");
              }
              clause.locspecs.get(0).featurename = new String(testname);
              clause.locspecs.get(0).vheading = null;
              clause.locspecs.get(0).iheading = null;
            }
            ps.endtimestamp = System.currentTimeMillis();
            r.metadata.addStep(ps);
          }
        }
      } else if (clause.locType.equalsIgnoreCase("BF")) {
        gdb = worldplaces;
        dbname = new String("worldplaces");
        ProcessStep ps = new ProcessStep(process, version, "");
        if (clause.locspecs.size() > 1) {
          clause.locspecs.get(0).featureinfos = gaz
              .featureInfoLookup(gdb, clause.locspecs.get(0).featurename, 0,
                  "equals-ignore-case", null);
          // The following could be made into a parentlookup as well, but not
          // currently worth the trouble
          gaz.addFeatures(userplaces, clause.locspecs.get(0).featureinfos,
              clause.locspecs.get(0).featurename, "equals-ignore-case");
          if (clause.locspecs.get(0).featureinfos == null
              || clause.locspecs.get(0).featureinfos.size() == 0) {
            // TODO: other kinds of lookups
            ps.method = ps.method.concat("Found no features matching "
                + clause.locspecs.get(0).featurename + " in " + dbname
                + " using query type equals-ignore-case.");
          } else {
            ps.method = ps.method.concat("Found "
                + clause.locspecs.get(0).featureinfos.size()
                + " features matching " + clause.locspecs.get(0).featurename
                + " in " + dbname + " using query type equals-ignore-case.");
          }
          clause.locspecs.get(1).featureinfos = gaz
              .featureInfoLookup(gdb, clause.locspecs.get(1).featurename, 0,
                  "equals-ignore-case", null);
          // The following could be made into a parentlookup as well, but not
          // currently worth the trouble
          gaz.addFeatures(userplaces, clause.locspecs.get(1).featureinfos,
              clause.locspecs.get(1).featurename, "equals-ignore-case");
          if (clause.locspecs.get(1).featureinfos == null
              || clause.locspecs.get(1).featureinfos.size() == 0) {
            // TODO: other kinds of lookups
            ps.method = ps.method.concat("Found no features matching "
                + clause.locspecs.get(1).featurename + " in " + dbname
                + " using query type equals-ignore-case.");
          } else {
            ps.method = ps.method.concat("Found "
                + clause.locspecs.get(1).featureinfos.size()
                + " features matching " + clause.locspecs.get(1).featurename
                + " in " + dbname + " using query type equals-ignore-case.");
          }
        }
        ps.endtimestamp = System.currentTimeMillis();
        r.metadata.addStep(ps);
      } else if (clause.locType.equalsIgnoreCase("P")
          || clause.locType.equalsIgnoreCase("POH")
          || clause.locType.equalsIgnoreCase("PO")
          || clause.locType.equalsIgnoreCase("PH")
          || clause.locType.equalsIgnoreCase("J")
          || clause.locType.equalsIgnoreCase("NJ")
          || clause.locType.equalsIgnoreCase("JOH")
          || clause.locType.equalsIgnoreCase("JO")
          || clause.locType.equalsIgnoreCase("JH")
          || clause.locType.equalsIgnoreCase("JOO")
          || clause.locType.equalsIgnoreCase("POM")
          || clause.locType.equalsIgnoreCase("NPOM")
          || clause.locType.equalsIgnoreCase("PS")
          || clause.locType.equalsIgnoreCase("BP")
          || clause.locType.equalsIgnoreCase("ADDR")
          || clause.locType.equalsIgnoreCase("NP")) {
        gdb = null; // until roads and rivers layers are installed
      } else if (clause.locType.equalsIgnoreCase("TRS")
          || clause.locType.equalsIgnoreCase("TRSS")) {
        gdb = plss;
        dbname = new String("plss");
        ProcessStep ps = new ProcessStep(process, version, "");
        if (clause.locspecs.size() > 0) {
          clause.locspecs.get(0).interpretTRS();
        }
        clause.locspecs.get(0).featureinfos = gaz.featureInfoLookup(gdb,
            clause.locspecs.get(0).featurename, 0, "equals-ignore-case", null);
        // The following could be made into a parentlookup as well, but not
        // currently worth the trouble
        gaz.addFeatures(userplaces, clause.locspecs.get(0).featureinfos,
            clause.locspecs.get(0).featurename, "equals-ignore-case");
        if (clause.locspecs.get(0).featureinfos == null
            || clause.locspecs.get(0).featureinfos.size() == 0) {
          // TODO: other kinds of lookups
          ps.method = ps.method.concat("Found no features matching "
              + clause.locspecs.get(0).featurename + " in " + dbname
              + " using query type equals-ignore-case.");
        } else {
          ps.method = ps.method.concat("Found "
              + clause.locspecs.get(0).featureinfos.size()
              + " features matching " + clause.locspecs.get(0).featurename
              + " in " + dbname + " using query type equals-ignore-case.");
        }
        ps.endtimestamp = System.currentTimeMillis();
        r.metadata.addStep(ps);
      } else if (clause.locType.equalsIgnoreCase("LL")) {
        if (clause.locspecs.size() > 0) {
          clause.locspecs.get(0).interpretLatLng(clause.locspecs.get(0));
          gdb = null;
        }
      } else if (clause.locType.equalsIgnoreCase("UTM")) {
        if (clause.locspecs.size() > 0) {
          clause.locspecs.get(0).interpretUTM();
          gdb = null;
        }
      } else if (clause.locType.equalsIgnoreCase("FPOH")) {
        gdb = null; // until roads and rivers layers are installed
      } else if (clause.locType.equalsIgnoreCase("JPOH")) {
        gdb = null; // until roads and rivers layers are installed
      } else if (clause.locType.equalsIgnoreCase("Q")) {
        gdb = null; // don't yet have data for this
      }

      if (gdb != null) { // do other lookup types if the database is defined
        for (LocSpec locspec : clause.locspecs) {
          // if(locspec.featureinfos==null || locspec.featureinfos.size()==0){
          // ProcessStep ps = new ProcessStep(process, version, "");
          // locspec.featureinfos = gaz.featureQuickLookup(gdb,
          // locspec.featurename, "contains-all-words", null);
          // gaz.addFeatures(userplaces, locspec.featureinfos,
          // locspec.featurename, "contains-all-words");
          // if(locspec.featureinfos==null || locspec.featureinfos.size()==0){
          // // TODO: other kinds of lookups
          // ps.method=ps.method.concat("Found no features matching
          // "+clause.locspecs.get(0).featurename+" in "+dbname+" using query
          // type contains-all-words.");
          // } else{
          // ps.method=ps.method.concat("Found "+locspec.featureinfos.size()+"
          // features matching "+locspec.featurename+" in "+dbname+" using query
          // type contains-all-words.");
          // }
          // ps.endtimestamp=System.currentTimeMillis();
          // r.metadata.addStep(ps);
          // }
          // // contains-all-words didn't work, try other method,
          //
          // if(locspec.featureinfos==null || locspec.featureinfos.size()==0){
          // ProcessStep ps = new ProcessStep(process, version, "");
          // locspec.featureinfos = gaz.featureQuickLookup(gdb,
          // locspec.featurename, "contains-any-words", null);
          // gaz.addFeatures(userplaces, locspec.featureinfos,
          // locspec.featurename, "contains-any-words");
          // if(locspec.featureinfos==null || locspec.featureinfos.size()==0){
          // // TODO: other kinds of lookups
          // ps.method=ps.method.concat("Found no features matching
          // "+clause.locspecs.get(0).featurename+" in "+dbname+" using query
          // type contains-any-words.");
          // } else{
          // ps.method=ps.method.concat("Found "+locspec.featureinfos.size()+"
          // features matching "+locspec.featurename+" in "+dbname+" using query
          // type contains-any-words.");
          // }
          // ps.endtimestamp=System.currentTimeMillis();
          // r.metadata.addStep(ps);
          // }
          //					
          // // contains-any-words didn't work, try other method,
          // if(locspec.featureinfos==null || locspec.featureinfos.size()==0){
          // ProcessStep ps = new ProcessStep(process, version, "");
          // locspec.featureinfos = gaz.featureQuickLookup(gdb,
          // locspec.featurename, "contains-phrase", null);
          // gaz.addFeatures(userplaces, locspec.featureinfos,
          // locspec.featurename, "contains-phrase");
          // if(locspec.featureinfos==null || locspec.featureinfos.size()==0){
          // // TODO: other kinds of lookups
          // ps.method=ps.method.concat("Found no features matching
          // "+clause.locspecs.get(0).featurename+" in "+dbname+" using query
          // type contains-phrase.");
          // } else{
          // ps.method=ps.method.concat("Found "+locspec.featureinfos.size()+"
          // features matching "+locspec.featurename+" in "+dbname+" using query
          // type contains-phrase.");
          // }
          // ps.endtimestamp=System.currentTimeMillis();
          // r.metadata.addStep(ps);
          // }

          // contains phrase didn't work, try other method,
          // such as a pattern query replacing vowels with wildcard characters
          if (locspec.featureinfos == null || locspec.featureinfos.size() == 0) {
            // At this point, if the locspec still doesn't have any features,
            // the lookup failed.
            clause.state = ClauseState.CLAUSE_FEATURE_NOT_FOUND_ERROR;
            locspec.state = LocSpecState.LOCSPEC_ERROR_FEATURE_NOT_FOUND;
          }
        }
      }
    }
  }

  public Geometry getTRSectionGeometry(Geometry trgeom, int section) {
    if (section < 1 || section > 36)
      return null;
    Coordinate[] coordinates = trgeom.getEnvelope().getCoordinates();
    int nc = coordinates.length;
    double maxx = -180.0;
    double minx = 180.0;
    double miny = 90.0;
    double maxy = -90.0;
    for (int k = 0; k < nc; k++) {
      if (coordinates[k].x > maxx)
        maxx = coordinates[k].x;
      if (coordinates[k].x < minx)
        minx = coordinates[k].x;
      if (coordinates[k].y > maxy)
        maxy = coordinates[k].y;
      if (coordinates[k].y < miny)
        miny = coordinates[k].y;
    }
    // Now have the bounding coordinates for the Township
    // Find the point-radius for the section. Assume well-
    // behaved townships - sections of equal size, evenly
    // distributed across the township
    double xincrement = (maxx - minx) / 6;
    double yincrement = (maxy - miny) / 6;
    long row = Math.round(Math.floor((section - 1) / 6));
    long evenrow = row % 2;
    long column = -1;
    if (evenrow == 0) {
      column = (section - 1) % 6;
    } else {
      column = 5 - (section - 1) % 6;
    }
    double rx = maxx - xincrement * column;
    double uy = maxy - yincrement * row;
    double lx = maxx - xincrement * (column + 1);
    double ly = maxy - yincrement * (row + 1);
    Coordinate urcorner = new Coordinate(rx, uy);
    Coordinate lrcorner = new Coordinate(rx, ly);
    Coordinate llcorner = new Coordinate(lx, ly);
    Coordinate ulcorner = new Coordinate(lx, uy);
    Coordinate[] sectioncoords = new Coordinate[5];
    sectioncoords[0] = urcorner;
    sectioncoords[1] = lrcorner;
    sectioncoords[2] = llcorner;
    sectioncoords[3] = ulcorner;
    sectioncoords[4] = urcorner;
    LinearRing lr = new GeometryFactory().createLinearRing(sectioncoords);
    Polygon p = new GeometryFactory().createPolygon(lr, null);
    return p;
  }

  public void makeClauseGeorefs(Rec r, boolean showSource, boolean showType) {
    for (Clause clause : r.clauses) {
      if (clause.locType.equalsIgnoreCase("UNK"))
        continue;
      // if Clause locType is LatLong (LL)
      if (clause.locType.equalsIgnoreCase("LL")) {
        LocSpec ls = clause.locspecs.get(0);
        if (ls != null && ls.isCoordinate()) {
          PointRadius pr = sm.getPointRadius(clause.locType, ls, null, null);
          // Point-radius was successfully created, now add it to the
          // clause's georefs list.
          if (pr != null) {
            Georef g = new Georef(pr);
            g.confidence = 0;
            if (g.iLocality == null || g.iLocality.trim().length() == 0) {
              g.iLocality = ls.ilat + " " + ls.ilng + " WGS84, uncertainty: "
                  + Math.ceil(pr.extent) + "m";
              // g.iLocality = clause.makeInterpretedLocality(null, null,
              // showSource, showType);
            } else {
              g.iLocality = g.iLocality + ", " + ls.ilat + " " + ls.ilng
                  + " WGS84, uncertainty: " + Math.ceil(pr.extent) + "m";
              // g.iLocality =
              // g.iLocality.concat(clause.makeInterpretedLocality(
              // null, null, showSource, showType));
            }
            g.uLocality = clause.uLocality;
            clause.state = ClauseState.CLAUSE_POINT_RADIUS_COMPLETED;
            clause.georefs.add(g);
          }
        }
      } else {
        // first locSpec in the Clause has a featureinfo list
        if (clause.locspecs.get(0).featureinfos != null) {

          // for every featureinfo in LocSpec1
          for (int i = 0; i < clause.locspecs.get(0).featureinfos.size(); i++) {
            FeatureInfo featureinfo1 = clause.locspecs.get(0).featureinfos
                .get(i);
            FeatureInfo featureinfo2 = null;
            PointRadius pr = null;
            // If there are features for the second locspec
            if (clause.locspecs.size() > 1
                && clause.locspecs.get(1).featureinfos.size() > 0) {
              for (int j = 0; j < clause.locspecs.get(1).featureinfos.size(); j++) {
                // for every featureinfo in LocSpec2
                featureinfo2 = clause.locspecs.get(1).featureinfos.get(j);
                pr = sm.getPointRadius(clause.locType, clause.locspecs.get(0),
                    featureinfo1, featureinfo2);
                // Point-radius was successfully created, now add it to the
                // clause's georefs list.
                if (pr != null) {
                  Georef g = new Georef(pr);
                  g.confidence = 0;
                  if (g.iLocality == null || g.iLocality.trim().length() == 0) {
                    g.iLocality = clause.makeInterpretedLocality(featureinfo1,
                        featureinfo2, showSource, showType);
                  } else {
                    g.iLocality = g.iLocality.concat(clause
                        .makeInterpretedLocality(featureinfo1, featureinfo2,
                            showSource, showType));
                  }
                  g.uLocality = clause.uLocality;
                  g.addFeatureInfo(featureinfo1);
                  g.addFeatureInfo(featureinfo2);
                  clause.state = ClauseState.CLAUSE_POINT_RADIUS_COMPLETED;
                  clause.georefs.add(g);
                }
              }
            } else { // there is no second locspec
              try {
                pr = sm.getPointRadius(clause.locType, clause.locspecs.get(0),
                    featureinfo1, featureinfo2);
              } catch (Exception e) {
                e.printStackTrace();
              }
              // Point-radius was successfully created, now add it to the
              // clause's
              // georefs list.

              if (pr != null) {
                Georef g = new Georef(pr);
                g.confidence = 0;
                g.uLocality = clause.uLocality;
                if (g.iLocality == null || g.iLocality.trim().length() == 0) {
                  g.iLocality = clause.makeInterpretedLocality(featureinfo1,
                      featureinfo2, showSource, showType);
                } else {
                  g.iLocality = g.iLocality.concat(clause
                      .makeInterpretedLocality(featureinfo1, featureinfo2,
                          showSource, showType));
                }
                g.uLocality = clause.uLocality;
                g.addFeatureInfo(featureinfo1);
                g.state = GeorefState.GEOREF_COMPLETED;
                clause.state = ClauseState.CLAUSE_POINT_RADIUS_COMPLETED;
                clause.georefs.add(g);
              }
            }
          }
        }
      }
    }

    // Now that georefs have been generated for the clauses, remove any
    // duplicate georefs within a given clause.
    for (Clause clause : r.clauses) {
      for (int i = 0; i < clause.georefs.size(); i++) {
        for (int j = 0; j < clause.georefs.size(); j++) {
          if (i != j) {
            if (clause.georefs.get(i).equals(clause.georefs.get(j))) {
              clause.georefs.remove(j);
              j--;
            }
          }
        }
      }
    }
  }

  public String makeEncodedGeometry(FeatureInfo fi) {
    if (fi == null)
      return null;
    String encodedG = null;
    DatumManager dm = DatumManager.getInstance();
    PointRadius pr = new PointRadius(fi.longitude, fi.latitude, dm
        .getDatum("WGS84"), fi.coordPrecision, fi.extentInMeters);
    Georef g = new Georef(pr);
    Geometry geom = g.makeGeometry(pr, 32);
    WKTWriter wktw = new WKTWriter();
    encodedG = new String(wktw.write(geom));
    return encodedG;
  }

  public void makeRecGeorefs(Rec r) {
    // Point-radius creation has been attempted for all Clauses in the Rec.
    // Now do a spatial intersection on all combinations of georefs across
    // clauses.
    // The number of possible georefs arising from the intersections of clauses
    // is the product of the number of successfully created georefs for each
    // clause.

    if (r.getGeorefedClauseCount() == 0) {
      r.state = RecState.REC_NO_GEOREFERENCE_ERROR;
      return;
    }
    int clausecount = r.clauses.size(); // Total number of Clauses in the Rec.
    int combos = 0; // Total number of combinations of viable Clauses.
    int[] gcounts = null; // Number of Georefs for the Clause at each index
    for (int n = 0; n < clausecount; n++) {
      int viablegeorefcount = r.clauses.get(n).viableGeorefCount();
      if (viablegeorefcount > 0) {
        if (combos == 0)
          combos = viablegeorefcount;
        else
          combos *= viablegeorefcount;
      }
    }

    gcounts = new int[clausecount];
    for (int j = 0; j < clausecount; j++) {
      int viablegeorefs = r.clauses.get(j).viableGeorefCount();
      gcounts[j] = viablegeorefs;
    }

    // find the max number of combos
    int size = 1;
    for (int k = 0; k < gcounts.length; k++) {
      if (gcounts[k] != 0)
        size = size * gcounts[k];
    }

    // Create an array to hold the combinations of georef indexes to do
    // intersections on.
    int[][] geoCombos = new int[size][gcounts.length];

    // create an array to store the current combo
    int[] curr = new int[gcounts.length];

    // populate the new array with 0's to begin
    for (int k = 0; k < gcounts.length; k++) {
      if (gcounts[k] == 0) {
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
        // check for invalid clauses
        if (curr[j] == -1) {
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
    Geometry geom;
    String encodedG = null;

    Georef g1 = null, intersection = null;
    double distancebetweencenters = 0, sumofradii = 0;
    boolean foundFirstValid = false;
    String loctype;
    int featureid;
    int geometrythreshhold = 1300;
    for (int m = 0; m < combos; m++) { // For every combo of Clause Georefs
      g1 = intersection = null;
      for (int i = 0; i < clausecount; i++) { // For every Clause in the Rec
        if (i == 0) {
          foundFirstValid = false;
        }

        if (geoCombos[m][i] == -1) {
          continue;
        }
        g1 = r.clauses.get(i).georefs.get(geoCombos[m][i]);
        loctype = r.clauses.get(i).locType;
        if (!foundFirstValid) {
          // first VALID clause, intersection is just that clause or
          // the geometry for the feature if the loctype is one of the
          // feature-only loctypes.
          foundFirstValid = true;
          if (loctype.equalsIgnoreCase("F")
              || loctype.toUpperCase().contains("ADM")
              || loctype.equalsIgnoreCase("P")
              || loctype.equalsIgnoreCase("FS")
              || loctype.equalsIgnoreCase("PS")
              || loctype.equalsIgnoreCase("TRS")
              || loctype.equalsIgnoreCase("TRSS")) {
            // Use the actual shape for the intersection instead of the
            // point-radius.
            featureid = g1.featureinfos.get(0).featureID;

            String csource = new String(g1.featureinfos.get(0).coordSource);
            if (csource != null && csource.equalsIgnoreCase("usersdb")) {
              encodedG = new String(gaz.lookupFootprint(userplaces, featureid));
            } else if (loctype.toUpperCase().contains("ADM")) {
              if (g1.featureinfos.get(0).geomminx <= -180) {
                // This is a temporary fix to overcome problems in the gazetteer
                // features for features crossing longitude 180.
                encodedG = makeEncodedGeometry(g1.featureinfos.get(0));
              } else {
                int geomsize = gaz
                    .lookupFootprintGeometryCount(gadm, featureid);
                if (gaz.lookupFootprintGeometryCount(gadm, featureid) > geometrythreshhold) {
                  // This is a temporary fix to overcome exceedingly complex
                  // geometries.
                  encodedG = makeEncodedGeometry(g1.featureinfos.get(0));
                  // encodedG = new String(gaz.lookupConvexHull(gadm,
                  // featureid));
                } else {
                  encodedG = new String(gaz.lookupFootprint(gadm, featureid));
                }
              }
            } else if (loctype.equalsIgnoreCase("F")
                || loctype.equalsIgnoreCase("FS")) {
              if (g1.featureinfos.get(0).geomminx <= -180) {
                // This is a temporary fix to overcome problems in the gazetteer
                // features for features crossing longitude 180.
                encodedG = makeEncodedGeometry(g1.featureinfos.get(0));
              } else {
                if (gaz.lookupFootprintGeometryCount(worldplaces, featureid) > geometrythreshhold) {
                  // This is a temporary fix to overcome exceedingly complex
                  // geometries.
                  encodedG = makeEncodedGeometry(g1.featureinfos.get(0));
                  // encodedG = new String(gaz.lookupConvexHull(worldplaces,
                  // featureid));
                } else {
                  encodedG = new String(gaz.lookupFootprint(worldplaces,
                      featureid));
                }
              }
            }
            // TODO change this to be roads or rivers when they get added to the
            // Gazetteer
            else if (loctype.equalsIgnoreCase("P")
                || loctype.equalsIgnoreCase("PS")) {
              encodedG = new String(gaz.lookupFootprint(worldplaces, featureid));
            } else if (loctype.equalsIgnoreCase("TRS")
                || loctype.equalsIgnoreCase("TRSS")) {
              encodedG = new String(gaz.lookupFootprint(plss, featureid));
            }
            try {
              geom = wktreader.read(encodedG);
              if (geom.getDimension() == 0) {
                // feature is a point in the gazetteer
                geom = g1.geometry;
                intersection = g1; // not sure why this was commented out. It
                // seems to omit all features based on
                // single points.
              } else {
                // feature has a footprint in the gazetteer
                if (loctype.equalsIgnoreCase("TRS")
                    || loctype.equalsIgnoreCase("TRSS")) {
                  int sec = r.clauses.get(i).locspecs.get(0).finishTRSSection();
                  if (sec > 0) {
                    // g1.iLocality = g1.iLocality.concat(" Section " + sec);
                    geom = getTRSectionGeometry(geom, sec);
                  }
                  if (loctype.equalsIgnoreCase("TRSS")) {
                    if (r.clauses.get(i).locspecs.get(0).isubdivision != null
                        && r.clauses.get(i).locspecs.get(0).isubdivision
                            .length() > 0) {
                      geom = getGeomSubdivision(geom, r.clauses.get(i).locspecs
                          .get(0).isubdivision);
                    }
                  }
                }
                intersection = new Georef(geom, DatumManager.getInstance()
                    .getDatum("WGS84"));
                intersection.iLocality = new String(g1.iLocality);
              }
              // Regardless of point-radius or shape, if FS or PS, make
              // subdivision
              if (loctype.equalsIgnoreCase("FS")
                  || loctype.equalsIgnoreCase("PS")) {
                geom = getGeomSubdivision(geom, r.clauses.get(i).locspecs
                    .get(0).isubdivision);
                intersection = new Georef(geom, DatumManager.getInstance()
                    .getDatum("WGS84"));
                intersection.iLocality = new String(g1.iLocality);
              }
            } catch (ParseException e) {
              e.printStackTrace();
            }
          } else {
            intersection = g1;
          }
        } else {
          // Clause beyond the first, may have real intersection
          // Check to see if the distances between points is greater
          // than the sum of the radii of the features. If so, there is no
          // overlap.
          distancebetweencenters = g1.getDistanceToGeorefCentroid(intersection);
          sumofradii = g1.pointRadius.extent + intersection.pointRadius.extent;
          if (distancebetweencenters <= sumofradii) {
            // there is a non-point intersection
            // For any of the feature-only loctypes
            if (loctype.equalsIgnoreCase("F")
                || loctype.toUpperCase().contains("ADM")
                || loctype.equalsIgnoreCase("P")
                || loctype.equalsIgnoreCase("FS")
                || loctype.equalsIgnoreCase("PS")
                || loctype.equalsIgnoreCase("TRS")
                || loctype.equalsIgnoreCase("TRSS")) {
              // Use the actual shape for the intersection instead of the
              // point-radius.
              featureid = g1.featureinfos.get(0).featureID;

              String csource = new String(g1.featureinfos.get(0).coordSource);
              if (csource != null && csource.equalsIgnoreCase("usersdb")) {
                encodedG = new String(gaz
                    .lookupFootprint(userplaces, featureid));
              } else if (loctype.toUpperCase().contains("ADM")) {
                if (g1.featureinfos.get(0).geomminx <= -180) {
                  // This is a temporary fix to overcome problems in the
                  // gazetteer
                  // features for features crossing longitude 180.
                  encodedG = makeEncodedGeometry(g1.featureinfos.get(0));
                } else {
                  if (gaz.lookupFootprintGeometryCount(gadm, featureid) > geometrythreshhold) {
                    // This is a temporary fix to overcome
                    // exceedingly complex geometries.
                    encodedG = makeEncodedGeometry(g1.featureinfos.get(0));
                    // encodedG = new String(gaz.lookupConvexHull(gadm,
                    // featureid));
                  } else {
                    encodedG = new String(gaz.lookupFootprint(gadm, featureid));
                  }
                }
              } else if (loctype.equalsIgnoreCase("F")
                  || loctype.equalsIgnoreCase("FS")) {
                if (g1.featureinfos.get(0).geomminx <= -180) {
                  // This is a temporary fix to overcome problems in the
                  // gazetteer
                  // features for features crossing longitude 180.
                  encodedG = makeEncodedGeometry(g1.featureinfos.get(0));
                } else {
                  if (gaz.lookupFootprintGeometryCount(worldplaces, featureid) > geometrythreshhold) {
                    // This is a temporary fix to overcome
                    // exceedingly complex geometries.
                    encodedG = makeEncodedGeometry(g1.featureinfos.get(0));
                    // encodedG = new String(gaz.lookupConvexHull(worldplaces,
                    // featureid));
                  } else {
                    encodedG = new String(gaz.lookupFootprint(worldplaces,
                        featureid));
                  }
                }
              }
              // TODO change this to be roads or rivers when they get added to
              // the Gazetteer
              else if (loctype.equalsIgnoreCase("P")
                  || loctype.equalsIgnoreCase("PS")) {
                encodedG = new String(gaz.lookupFootprint(worldplaces,
                    featureid));
              } else if (loctype.equalsIgnoreCase("TRS")
                  || loctype.equalsIgnoreCase("TRSS")) {
                encodedG = new String(gaz.lookupFootprint(plss, featureid));
              }
              try {
                // Test the old intersection against the real geometries
                geom = wktreader.read(encodedG);
                if (geom.getDimension() == 0) {
                  // feature is a point in the gazetteer, use the point-radius
                  newGeoref = g1;
                } else {
                  // feature has a footprint in the gazetteer
                  if (loctype.equalsIgnoreCase("TRS")
                      || loctype.equalsIgnoreCase("TRSS")) {
                    int sec = r.clauses.get(i).locspecs.get(0)
                        .finishTRSSection();
                    if (sec > 0) {
                      // g1.iLocality = g1.iLocality.concat(" Section " + sec);
                      geom = getTRSectionGeometry(geom, sec);
                    }
                    if (loctype.equalsIgnoreCase("TRSS")) {
                      if (r.clauses.get(i).locspecs.get(0).isubdivision != null
                          && r.clauses.get(i).locspecs.get(0).isubdivision
                              .length() > 0) {
                        geom = getGeomSubdivision(geom,
                            r.clauses.get(i).locspecs.get(0).isubdivision);
                      }
                    }
                  }

                  newGeoref = new Georef(geom, DatumManager.getInstance()
                      .getDatum("WGS84"));
                  newGeoref.iLocality = new String(g1.iLocality);
                }
                // Regardless of point-radius or shape, if FS or PS, make
                // subdivision
                if (loctype.equalsIgnoreCase("FS")
                    || loctype.equalsIgnoreCase("PS")) {

                  geom = getGeomSubdivision(newGeoref.geometry, r.clauses
                      .get(i).locspecs.get(0).isubdivision);
                  // geom = getGeomSubdivision(geom, r.clauses.get(i).locspecs
                  // .get(0).isubdivision);
                  newGeoref = new Georef(geom, DatumManager.getInstance()
                      .getDatum("WGS84"));
                  newGeoref.iLocality = new String(g1.iLocality);
                }
              } catch (ParseException e) {
                e.printStackTrace();
              }
              intersection = intersection.intersect(newGeoref);
            } else {
              intersection = intersection.intersect(g1);
            }
            if (intersection != null && !intersection.geometry.isEmpty()) {
              // copy the featureinfos used in the intersecting georef
              // to the georef for the resulting intersection
              for (FeatureInfo f : g1.featureinfos) {
                try {
                  if (intersection != null) {
                    intersection.addFeatureInfo(f);
                  }
                } catch (Exception e) {
                  System.out
                      .println("Problem with addFeatureInfo to intersection for the following feature:\n"
                          + f.toXML(true));
                  System.out.println("from the following intersection:\n"
                      + intersection.toXML(true));
                  e.printStackTrace();
                }
              }
            } else {
              // there is no intersection between g1 and the previously
              // calculated intersection based on geometry
              intersection = null;
              i = clausecount; // no reason to even try other clauses, because
              // there is already an inconsistency
            }
          } else {
            // there is no intersection between g1 and g2 based on distance
            // between centers
            // and point-radiuses
            intersection = null;
            i = clausecount; // no reason to even try other clauses, because
            // there is already an inconsistency
          }
        }
      }
      if (intersection != null) {
        r.georefs.add(intersection);
      }
    }
    // Remove duplicate georeferences, if any
    for (int i = 0; i < r.georefs.size(); i++) {
      for (int j = 0; j < r.georefs.size(); j++) {
        if (i != j) {
          if (r.georefs.get(i).equals(r.georefs.get(j))) {
            r.georefs.remove(j);
            j--;
          }
        }
      }
    }
  }

  public void reassessClauseLocTypes(Rec r) {
    if (r == null)
      return;
    if (r.clauses == null || r.clauses.size() == 0) {
      r.state = RecState.REC_NO_CLAUSES_ERROR;
      return;
    }
    for (Clause clause : r.clauses) {
      // do feature lookups for all locspecs for every clause based on locType
      // Hack! TODO: Proper handling of misinterpreted features in Interpreters.
      // Due diligence
      // would suggest that all feature names should be tested
      if (clause.locType.toUpperCase().equals("FS")
          && (clause.locspecs.get(0).vsubdivision == null || clause.locspecs
              .get(0).vsubdivision.length() == 0)) {
        clause.locType = new String("F");
        for (LocSpec ls : clause.locspecs) {
          ls.clear();
          ls.featurename = new String(clause.uLocality);
        }
      }
      if (clause.locType.toUpperCase().equals("F") == false
          && (clause.uLocality.equalsIgnoreCase("NM")
              || clause.uLocality.equalsIgnoreCase("NE") || clause.uLocality
              .equalsIgnoreCase("MI"))) {
        clause.locType = new String("F");
        for (LocSpec ls : clause.locspecs) {
          ls.clear();
          ls.featurename = new String(clause.uLocality);
        }
      }
      if (clause.locType.toUpperCase().contains("ADM")) {
        if (clause.sourceField.equalsIgnoreCase("country")) {
          clause.locType = new String("ADM0");
        } else if (clause.sourceField.equalsIgnoreCase("stateprovince")) {
          clause.locType = new String("ADM1");
        } else if (clause.sourceField.equalsIgnoreCase("county")) {
          clause.locType = new String("ADM2");
        }
      } else if (clause.locType.equalsIgnoreCase("F")) {
        if (clause.uLocality.toLowerCase().endsWith(" county")
            || clause.uLocality.toLowerCase().endsWith(" co.")
            || clause.uLocality.toLowerCase().endsWith(" parish")) {
          clause.locType = new String("ADM2");
        }
      }
    }
  }

  public void removeNonoverlappingFeatures(Rec r) {
    if (r == null)
      return;
    if (r.clauses == null)
      return;
    // Find and remove features for which no other features in
    // other clauses that have overlapping bounding boxes.
    // Basically, this method takes
    // advantage of preprocessing into the flatfeatureinfo table
    // to reduce the number of features that need to be compared
    // for geometry intersections.

    // No point in trying this if there isn't more than one clause.
    if (r.clauses.size() < 2)
      return;
    for (Clause c1 : r.clauses) { // do feature lookups for all locspecs for
      // every clause based on locType
      for (LocSpec locspec : c1.locspecs) {
        int fcount = 0;
        if (locspec.featureinfos != null)
          fcount = locspec.featureinfos.size();
        if (fcount > 0) {
          boolean[] fparents = new boolean[fcount];
          int i = 0;
          for (FeatureInfo f : locspec.featureinfos) {
            fparents[i] = false;
            for (Clause c2 : r.clauses) {
              if (c1.equals(c2) == false) {
                if (c2.overlapsFeature(f) == true) {
                  fparents[i] = true;
                  break;
                }
              }
            }
            i++;
          }
          for (i = fcount - 1; i > 0; i--) {
            if (fparents[i] == false) {
              locspec.featureinfos.remove(i);
            }
          }
        }
      }
    }
  }

  public void removeUserFeature(int featureid) {
    try {
      gaz.removeUserFeature(featureid);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public void shutdown() {
    gaz.shutdown();
  }

  public void startup() {
    try {
      gaz = ADLGazetteer.getInstance();
    } catch (Exception e) {
      e.printStackTrace();
    }
    sm = new ShapeManager();
  }

  public Geometry subdivide(Geometry geom, String subdivision) {
    // Translate subdivision string to English before calling subdivide
    if (subdivision == null || subdivision.length() == 0)
      return geom;
    String translatedSubdivision = GeorefDictionaryManager.getInstance()
        .lookup(subdivision, SupportedLanguages.english, Concepts.headings,
            true);

    // subdivision not found in the dictionary
    if (translatedSubdivision == null)
      return geom;

    Coordinate[] coordinates = geom.getEnvelope().getCoordinates();
    int nc = coordinates.length;
    double maxx = -180.0;
    double minx = 180.0;
    double miny = 90.0;
    double maxy = -90.0;
    double xincrement = 1.0;
    double yincrement = 1.0;
    Coordinate[] cuttercoords = new Coordinate[5];
    for (int k = 0; k < nc; k++) {
      if (coordinates[k].x > maxx)
        maxx = coordinates[k].x;
      if (coordinates[k].x < minx)
        minx = coordinates[k].x;
      if (coordinates[k].y > maxy)
        maxy = coordinates[k].y;
      if (coordinates[k].y < miny)
        miny = coordinates[k].y;
    }
    double rx = maxx;
    double uy = maxy;
    double lx = minx;
    double ly = miny;
    // Now we have the bounding coordinates for the geometry
    // Find the geometry for the subdivision.
    if (translatedSubdivision.equalsIgnoreCase("NW")
    // || subdivision.equalsIgnoreCase("NW")
        || subdivision.equalsIgnoreCase("NW 1/4")) {
      xincrement = (maxx - minx) / 2;
      yincrement = (maxy - miny) / 2;
      rx = minx + xincrement;
      lx = minx;
      uy = maxy;
      ly = maxy - yincrement;
    } else if (subdivision.equalsIgnoreCase("SW")
        || subdivision.equalsIgnoreCase("SW 1/4")) {
      xincrement = (maxx - minx) / 2;
      yincrement = (maxy - miny) / 2;
      rx = minx + xincrement;
      lx = minx;
      uy = miny + yincrement;
      ly = miny;
    } else if (subdivision.equalsIgnoreCase("SE")
        || subdivision.equalsIgnoreCase("SE 1/4")) {
      xincrement = (maxx - minx) / 2;
      yincrement = (maxy - miny) / 2;
      rx = maxx;
      lx = maxx - xincrement;
      uy = miny + yincrement;
      ly = miny;
    } else if (subdivision.equalsIgnoreCase("NE")
        || subdivision.equalsIgnoreCase("NE 1/4")) {
      xincrement = (maxx - minx) / 2;
      yincrement = (maxy - miny) / 2;
      rx = maxx;
      lx = maxx - xincrement;
      uy = maxy;
      ly = maxy - yincrement;
    } else if (subdivision.equalsIgnoreCase("N")
        || subdivision.equalsIgnoreCase("N 1/2")) {
      yincrement = (maxy - miny) / 2;
      rx = maxx;
      lx = minx;
      uy = maxy;
      ly = maxy - yincrement;
    } else if (subdivision.equalsIgnoreCase("S")
        || subdivision.equalsIgnoreCase("S 1/2")) {
      yincrement = (maxy - miny) / 2;
      rx = maxx;
      lx = minx;
      uy = miny + yincrement;
      ly = miny;
    } else if (subdivision.equalsIgnoreCase("W")
        || subdivision.equalsIgnoreCase("W 1/2")) {
      xincrement = (maxx - minx) / 2;
      rx = minx + xincrement;
      lx = minx;
      uy = maxy;
      ly = miny;
    } else if (subdivision.equalsIgnoreCase("E")
        || subdivision.equalsIgnoreCase("E 1/2")) {
      xincrement = (maxx - minx) / 2;
      rx = maxx;
      lx = maxx - xincrement;
      uy = maxy;
      ly = miny;
    }
    cuttercoords[0] = new Coordinate(rx, uy);
    cuttercoords[1] = new Coordinate(rx, ly);
    cuttercoords[2] = new Coordinate(lx, ly);
    cuttercoords[3] = new Coordinate(lx, uy);
    cuttercoords[4] = new Coordinate(rx, uy);
    LinearRing cutter = new GeometryFactory().createLinearRing(cuttercoords);
    Polygon p = new GeometryFactory().createPolygon(cutter, null);
    Geometry newgeom = geom.intersection(p);
    return newgeom;
  }
}