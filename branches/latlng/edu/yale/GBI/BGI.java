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

package edu.yale.GBI;

import java.util.Hashtable;

import org.biogeomancer.managers.DatumManager;
import org.biogeomancer.managers.GeorefDictionaryManager;
import org.biogeomancer.managers.LocSpecManager;
import org.biogeomancer.records.Clause;
import org.biogeomancer.records.ClauseState;
import org.biogeomancer.records.Georef;
import org.biogeomancer.records.LocSpec;
import org.biogeomancer.records.LocSpecState;
import org.biogeomancer.records.Rec;
import org.biogeomancer.records.RecSet;
import org.biogeomancer.utils.SupportedLanguages;

import edu.yale.GBI.interp.Parser;

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
  public String language;

  public void doParsing(Rec r, String fieldName) throws BGI.BGIException {
    if (fieldName == null) {
      // do we want to log this?
      return;
    }
    String verbatimLocality = r.get(fieldName);
    if (verbatimLocality == null) {
      // This field name isn't among those in the input RecSet.
      // The user must comply with the accepted column names, which
      // are enumerated on the BG web site.
      return;
    }
    try {
      LocalityRec rc = new LocalityRec();
      rc.localityString = verbatimLocality.trim().replaceAll("\"", "");

      // set the language of the parser
      Parser.getInstance(GeorefDictionaryManager.getInstance(),
          SupportedLanguages.english).process(rc);

      for (int i = 0; i < rc.results.length; i++) {
        Clause cl = new Clause();
        r.clauses.add(cl);
        cl.locType = rc.results[i].locType;
        cl.sourceField = new String(fieldName);
        cl.uLocality = rc.clauseSet[i];
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
        if (rc.results[i].headingEW.length() > 0)
          ls.vheadingew = rc.results[i].headingEW;
        if (rc.results[i].headingNS.length() > 0)
          ls.vheadingns = rc.results[i].headingNS;
        if (rc.results[i].offset != null && rc.results[i].offset.length() > 0) {
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
        // It's unclear what happens when the clause is an elevation. JRW
        // 2006-08-27
        if (rc.results[i].evelation != null
            && rc.results[i].evelation.length() > 0) {
          ls.velevation = rc.results[i].evelation;
          if (rc.results[i].unit != null && rc.results[i].unit.length() > 0) {
            ls.velevationunits = rc.results[i].unit;
          }
        }
        // JRW commented out the following line in favor of the code block above
        // ls.velevation = rc.results[i].evelation + rc.results[i].unit;
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

  public void doParsing(Rec r, String fieldName, boolean isAdm)
      throws BGI.BGIException {
    if (fieldName == null) {
      // do we want to log this?
      return;
    }
    String verbatimLocality = r.get(fieldName);
    if (!isAdm || verbatimLocality == null || verbatimLocality.length() < 1) {
      // This field name isn't among those in the input RecSet.
      // The user must comply with the accepted column names, which
      // are enumerated on the BG web site.
      return;
    }
    try {
      String[] adm = verbatimLocality.split(",|;|:");
      for (int i = 0; i < adm.length; i++) {
        if (adm[i].length() > 0) {
          Clause cl = new Clause();
          r.clauses.add(cl);
          cl.locType = "ADM";
          cl.sourceField = new String(fieldName);
          cl.uLocality = adm[i].trim();
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

  public void doParsing(Rec r, String fieldName, GeorefDictionaryManager gdm,
      SupportedLanguages lang) throws BGI.BGIException {
    if (fieldName == null) {
      // do we want to log this?
      return;
    }
    String verbatimLocality = r.get(fieldName);
    if (verbatimLocality == null) {
      // This field name isn't among those in the input RecSet.
      // The user must comply with the accepted column names, which
      // are enumerated on the BG web site.
      return;
    }
    // Treat incoming coordinates as a special case.
    if (fieldName.equalsIgnoreCase("decimallatitude")
        || fieldName.equalsIgnoreCase("decimallongitude")
        || fieldName.equalsIgnoreCase("verbatimlatitude")
        || fieldName.equalsIgnoreCase("verbatimlongitude")
        || fieldName.equalsIgnoreCase("coordinateuncertaintyinmeters")
        || fieldName.equalsIgnoreCase("geodeticdatum")) {
      Clause c = r.hasLatLongClause();
      if (c == null) {
        c = new Clause();
        LocSpec ls = new LocSpec();
        c.locspecs.add(ls);
        c.state = ClauseState.CLAUSE_CREATED;
        c.locType = "ll";
        c.interpretedInLanguage = lang;
        r.clauses.add(c);
      }
      if (fieldName.equalsIgnoreCase("decimallatitude")
          || fieldName.equalsIgnoreCase("verbatimlatitude")) {
        if (c.uLocality == null) {
          c.uLocality = ("lat: " + verbatimLocality).trim();
          c.sourceField = fieldName;
        } else {
          c.uLocality = ("lat: " + verbatimLocality).trim() + " " + c.uLocality;
          c.sourceField = fieldName + ", " + c.sourceField;
        }
        c.locspecs.get(0).vlat = verbatimLocality;
      } else if (fieldName.equalsIgnoreCase("decimallongitude")
          || fieldName.equalsIgnoreCase("verbatimlongitude")) {
        c.uLocality.concat(" long: " + verbatimLocality).trim();
        c.sourceField = fieldName + ", " + c.sourceField;
        c.locspecs.get(0).vlng = verbatimLocality;
      } else if (fieldName.equalsIgnoreCase("geodeticDatum")) {
        c.uLocality.concat(" datum: " + verbatimLocality).trim();
        c.sourceField = fieldName + ", " + c.sourceField;
        c.locspecs.get(0).vdatum = verbatimLocality;
      } else if (fieldName.equalsIgnoreCase("coordinateuncertaintyinmeters")) {
        c.uLocality.concat(" uncertainty: " + verbatimLocality).trim();
        c.locspecs.get(0).vuncertainty = verbatimLocality;
        c.sourceField = fieldName + ", " + c.sourceField;
      }
      try {
        LocSpecManager.getInstance().interpretLatLng(c.locspecs.get(0));
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return;
    }

    try {
      LocalityRec rc = new LocalityRec();
      rc.localityString = verbatimLocality.trim().replaceAll("\"", "");
      Parser.getInstance(gdm, lang).process(rc);
      for (int i = 0; i < rc.results.length; i++) {
        Clause cl = new Clause();
        r.clauses.add(cl);
        cl.locType = rc.results[i].locType;
        cl.sourceField = new String(fieldName);
        cl.uLocality = rc.clauseSet[i];
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
        if (rc.results[i].headingEW.length() > 0)
          ls.vheadingew = rc.results[i].headingEW;
        if (rc.results[i].headingNS.length() > 0)
          ls.vheadingns = rc.results[i].headingNS;
        if (rc.results[i].offset != null && rc.results[i].offset.length() > 0) {
          ls.voffset = rc.results[i].offset;
          ls.voffsetunit = rc.results[i].unit;
        }
        if (rc.results[i].offsetEW != null
            && rc.results[i].offsetEW.length() > 0) {
          ls.voffsetew = rc.results[i].offsetEW;
          ls.voffsetewunit = rc.results[i].unit2;
        }
        if (rc.results[i].offsetNS != null
            && rc.results[i].offsetNS.length() > 0) {
          ls.voffsetns = rc.results[i].offsetNS;
          ls.voffsetnsunit = rc.results[i].unit;
        }
        ls.vsubdivision = rc.results[i].subdivision;
        // TODO: rc.results[i].unit is being added to velevation regardless
        // This results in elevation units even when there is no elevation.
        // It's unclear what happens when the clause is an elevation. JRW
        // 2006-08-27
        if (rc.results[i].evelation != null
            && rc.results[i].evelation.length() > 0) {
          ls.velevation = rc.results[i].evelation;
          if (rc.results[i].unit != null && rc.results[i].unit.length() > 0) {
            ls.velevationunits = rc.results[i].unit;
          }
        }
        // JRW commented out the following line in favor of the code block above
        // ls.velevation = rc.results[i].evelation + rc.results[i].unit;
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

  public void doParsing(RecSet rs) throws BGI.BGIException {
    for (int i = 0; i < rs.recs.size(); i++) {
      hasGeoref(rs.recs.get(i), null); // Make a LL clause if it's feasible
      doParsing(rs.recs.get(i), "highergeography", true);
      doParsing(rs.recs.get(i), "continent", true);
      doParsing(rs.recs.get(i), "waterbody", true);
      doParsing(rs.recs.get(i), "islandgroup", true);
      doParsing(rs.recs.get(i), "island", true);
      doParsing(rs.recs.get(i), "country", true);
      doParsing(rs.recs.get(i), "stateprovince", true);
      doParsing(rs.recs.get(i), "county", true);
      doParsing(rs.recs.get(i), "locality");
    }
  }

  public void doParsing(RecSet rs, GeorefDictionaryManager gdm,
      SupportedLanguages lang) throws BGI.BGIException {
    for (int i = 0; i < rs.recs.size(); i++) {
      hasGeoref(rs.recs.get(i), lang); // Make a LL clause if it's feasible
      doParsing(rs.recs.get(i), "highergeography", true);
      doParsing(rs.recs.get(i), "continent", true);
      doParsing(rs.recs.get(i), "waterbody", true);
      doParsing(rs.recs.get(i), "islandgroup", true);
      doParsing(rs.recs.get(i), "island", true);
      doParsing(rs.recs.get(i), "country", true);
      doParsing(rs.recs.get(i), "stateprovince", true);
      doParsing(rs.recs.get(i), "county", true);
      doParsing(rs.recs.get(i), "locality", gdm, lang);
    }
  }

  public boolean hasGeoref(Rec r, SupportedLanguages lang)
      throws BGI.BGIException {
    if (r.get("decimallatitude") == null)
      return false;
    if (r.get("decimallongitude") == null)
      return false;
    if (r.get("coordinateuncertaintyinmeters") == null)
      return false;
    if (r.get("geodeticdatum") == null)
      return false;
    if (DatumManager.getInstance().getDatum(r.get("geodeticdatum")).getCode()
        .equalsIgnoreCase("unknown"))
      return false;

    double lat, lon, uncertainty;
    if (lang == SupportedLanguages.english) {
      if (isDouble(r.get("decimallatitude"))) {
        lat = Double.valueOf(r.get("decimallatitude"));
      } else
        return false;
      if (isDouble(r.get("decimallongitude"))) {
        lon = Double.valueOf(r.get("decimallongitude"));
      } else
        return false;
      if (isDouble(r.get("coordinateuncertaintyinmeters"))) {
        uncertainty = Double.valueOf(r.get("coordinateuncertaintyinmeters"));
      } else
        return false;
    } else { // not english, check for comma as decimal indicator
      if (isDouble(r.get("decimallatitude"))) {
        lat = Double.valueOf(r.get("decimallatitude"));
      } else if (isDouble(r.get("decimallatitude").replace(',', '.'))) {
        lat = Double.valueOf(r.get("decimallatitude").replace(',', '.'));
      } else
        return false;
      if (isDouble(r.get("decimallongitude"))) {
        lon = Double.valueOf(r.get("decimallongitude"));
      } else if (isDouble(r.get("decimallongitude").replace(',', '.'))) {
        lon = Double.valueOf(r.get("decimallongitude").replace(',', '.'));
      } else
        return false;
      if (isDouble(r.get("coordinateuncertaintyinmeters"))) {
        uncertainty = Double.valueOf(r.get("coordinateuncertaintyinmeters"));
      } else if (isDouble(r.get("coordinateuncertaintyinmeters").replace(',',
          '.'))) {
        uncertainty = Double.valueOf(r.get("coordinateuncertaintyinmeters")
            .replace(',', '.'));
      } else
        return false;
    }
    // There are values for the required fields
    // Note: Check LocSpec.interpretLatLong()
    Clause cl = new Clause();
    LocSpec ls = new LocSpec();
    Georef g = new Georef(lat, lon, uncertainty, DatumManager.getInstance()
        .getDatum(r.get("geodeticdatum")));
    g.confidence = 1;
    g.iLocality = String.valueOf(lat) + " " + String.valueOf(lon) + " "
        + String.valueOf(uncertainty) + " "
        + DatumManager.getInstance().getDatum(r.get("geodeticdatum")).getCode();
    g.uLocality = r.get("decimallatitude") + " " + r.get("decimallongitude")
        + " " + r.get("coordinateuncertaintyinmeters") + " "
        + r.get("geodeticdatum");
    cl.georefs.add(g);
    r.georefs.add(g);
    ls.vdatum = r.get("geodeticdatum");
    ls.vlat = r.get("decimallatitude");
    ls.vlng = r.get("decimallongitude");
    ls.vuncertainty = r.get("coordinateuncertaintyinmeters");
    ls.ilat = String.valueOf(lat);
    ls.ilng = String.valueOf(lon);
    ls.iuncertainty = String.valueOf(uncertainty);
    ls.idatum = DatumManager.getInstance().getDatum(r.get("geodeticdatum"))
        .getCode();
    cl.locspecs.add(ls);

    cl.iLocality = String.valueOf(lat) + " " + String.valueOf(lon) + " "
        + String.valueOf(uncertainty) + " "
        + DatumManager.getInstance().getDatum(r.get("geodeticdatum")).getCode();
    cl.interpretedInLanguage = lang;
    cl.locType = "LL";
    cl.sourceField = "DecimalLatitude, DecimalLongitude, CoordinateUncertaintyInMeters, GeodeticDatum";
    cl.state = ClauseState.CLAUSE_PARSED;
    cl.uLocality = r.get("decimallatitude") + " " + r.get("decimallongitude")
        + " " + r.get("coordinateuncertaintyinmeters") + " "
        + r.get("geodeticdatum");
    r.clauses.add(cl);
    return true;
  }

  public boolean isDouble(String s) {
    if (s == null)
      return false;
    Double d = null;
    try { // Try to make a double out of the value of s.
      d = new Double(s);
    } catch (Exception e) { // It isn't a valid double value.
      return false;
    }
    return true;
  }
}