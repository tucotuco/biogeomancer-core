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

package org.biogeomancer.records;

import org.biogeomancer.managers.DatumManager;
import org.biogeomancer.managers.DatumManager.Datum;

/**
 * <p>
 * The structure to contain spatial descriptions and feature attributes required
 * for the determination of footprints and uncertainties.
 * </p>
 */
public class FeatureInfo {
  public static FeatureInfo makeOne() {
    FeatureInfo fi = new FeatureInfo();
    fi.name = new String("Missoula");
    fi.featureID = -9999;
    fi.parentFeatureID = -9999;
    fi.parentFeatureType = -1;
    fi.classificationTerm = new String("populated place");
    fi.latitude = 35.234532;
    fi.longitude = -121.432235;
    fi.geodeticDatum = DatumManager.getInstance().getDatum("WGS84");
    fi.extentInMeters = 3433;
    fi.coordPrecision = 1.0 / 60.0;
    fi.mapAccuracyInMeters = 30;
    fi.coordSource = new String("made up");
    fi.encodedGeometry = new String(
        "POLYGON((588916.4 2432124.4, 588920.7 2432129.9,588928.6 2432123.7,588930.1 2432122.6,588916.4 2432124.4))");
    fi.state = FeatureInfoState.FEATUREINFO_COMPLETED;
    return fi;
  }

  public FeatureInfoState state; // The processing state of the FeatureInfo
  public String name; // The accepted valid name of the feature
  public int featureID; // Unique identifier for the feature
  public int parentFeatureID; // Unique identifier for the containing feature
  public int parentFeatureType; // Type code for containing feature (e.g., 754=country)
  public String classificationTerm; // The Feature Type in which this feature is
  // classified
  public double latitude; // The decimal latitude of the snapped-to centroid of
  // the feature.
  public double longitude; // The decimal longitude of the snapped-to centroid
  // of the feature.
  public Datum geodeticDatum; // The Coordinate Reference System (geodetic
  // datum) of the latitude and longitude.
  public double extentInMeters; // The distance from the snapped-to centroid of
  // the feature to the further point in the
  // feature, in meters.
  public double coordPrecision; // Coordinate precision expressed in decimal
  // degrees (e.g., 0.5 means 30 minutes or half a
  // degree).
  public double mapAccuracyInMeters;// Map accuracy in meters (how far off a
  // coordinate can be from the true value
  // based on the original source).
  public String coordSource; // Required for metadata construction.

  public String encodedGeometry; // An encoded geometry to hold complex

  // representations of the feature (shapes)
  // public MetaData metadata; // metadata object to track processing
  // information, such as timestamps and methods.
  // public ArrayList<MetaData> metadata; // as an ArrayList, metadata could be
  // an event capturing mechanism.

  public FeatureInfo() { // constructor
    this.state = FeatureInfoState.FEATUREINFO_CREATED;
  }

  public FeatureInfo(FeatureInfo f) {
    this.name = new String(f.name);
    this.featureID = f.featureID;
    this.parentFeatureID = f.parentFeatureID;
    this.parentFeatureType = f.parentFeatureType;
    if (f.classificationTerm != null) {
      this.classificationTerm = new String(f.classificationTerm);
    }
    this.latitude = f.latitude;
    this.longitude = f.longitude;
    if (f.geodeticDatum == null) {
      this.geodeticDatum = DatumManager.getInstance().getDatum("unknown");
    } else {
      this.geodeticDatum = DatumManager.getInstance().getDatum(
          f.geodeticDatum.getName());
    }
    this.extentInMeters = f.extentInMeters;
    this.coordPrecision = f.coordPrecision;
    this.mapAccuracyInMeters = f.mapAccuracyInMeters;
    if (f.coordSource != null) {
      this.coordSource = new String(f.coordSource);
    }
    if (f.encodedGeometry != null) {
      this.encodedGeometry = new String(f.encodedGeometry);
    }
    this.state = FeatureInfoState.FEATUREINFO_CREATED;
  }

  public FeatureInfo(String name, int fid, int pid, int ptype, String classificationterm,
      double lat, double lng, String datum, double extent,
      double coordprecision, double mapaccuracyinmeters, String coordsource,
      String encodedgeometry) {
    // this.name = new String(name);
    this.name = new String(name.replaceAll(";", " "));
    this.featureID = fid;
    this.parentFeatureID = pid;
    this.parentFeatureType = ptype;
    this.classificationTerm = new String(classificationterm);
    this.latitude = lat;
    this.longitude = lng;
    this.geodeticDatum = DatumManager.getInstance().getDatum(datum);
    this.extentInMeters = extent;
    this.coordPrecision = coordprecision;
    this.mapAccuracyInMeters = mapaccuracyinmeters;
    this.coordSource = new String(coordsource);
    this.encodedGeometry = new String(encodedgeometry);
    this.state = FeatureInfoState.FEATUREINFO_CREATED;
  }

  public boolean equals(Object o) {
    if (!(o instanceof FeatureInfo))
      return false;
    FeatureInfo f = (FeatureInfo) o;
    return f.featureID == this.featureID;
  }

  public int hashCode() {
    return featureID;
  }

  public String toString() {
    String s = new String("FeatureInfoStatus: ");
    s = s.concat(state + "\nFeature Name: ");
    if (name != null) {
      s = s.concat(name + "\n");
    } else
      s = s.concat("name not given\n");
    s = s.concat("FeatureID: " + featureID + "\n");

    s = s.concat("Feature Type: ");
    if (classificationTerm != null) {
      s = s.concat(classificationTerm + "\n");
    } else
      s = s.concat("not given\n");

    s = s.concat("Parent FeatureID: " + parentFeatureID + "\n");
    s = s.concat("Parent Feature Type: " + parentFeatureType + "\n");

    s = s.concat("DecimalLatitude: " + latitude + "\n");
    s = s.concat("DecimalLongitude: " + longitude + "\n");
    s = s.concat("Datum: ");
    if (geodeticDatum != null) {
      s = s.concat(geodeticDatum.getName() + "\n");
    } else
      s = s.concat("null\n");
    s = s.concat("ExtentInMeters: " + extentInMeters + "\n");
    s = s.concat("CoordinatePrecision: " + coordPrecision + "\n");
    s = s.concat("MapAccuracyInMeters: " + mapAccuracyInMeters + "\n");

    s = s.concat("CoordinateSource: ");
    if (coordSource != null) {
      s = s.concat(coordSource + "\n");
    } else
      s = s.concat("not given\n");

    s = s.concat("EncodedGeometry: ");
    if (encodedGeometry != null) {
      s = s.concat(encodedGeometry + "\n");
    } else
      s = s.concat("not given\n");

    return s;
  }

  public String toXML(boolean showgeom) {
    String s = new String("<FEATUREINFO>\n");
    if (name != null && name.length() > 0) {
      s = s.concat("<FEATURENAME>" + name + "</FEATURENAME>\n");
    } else {
      s = s.concat("<FEATURENAME></FEATURENAME>\n");
    }
    s = s.concat("<FEATUREID>" + featureID + "</FEATUREID>\n");

    if (classificationTerm != null && classificationTerm.length() > 0) {
    } else {
      s = s.concat("<FEATURETYPE>not recorded</FEATURETYPE>\n");
    }

    s = s.concat("<PARENTFEATUREID>" + parentFeatureID + "</PARENTFEATUREID>\n");
    s = s.concat("<PARENTFEATURETYPE>" + parentFeatureType + "</PARENTFEATUREID>\n");

    s = s.concat("<FEATURE_LATITUDE>" + latitude + "</FEATURE_LATITUDE>\n");
    s = s.concat("<FEATURE_LONGITUDE>" + longitude + "</FEATURE_LONGITUDE>\n");
    if (geodeticDatum != null && geodeticDatum.getName() != null
        && geodeticDatum.getName().length() > 0) {
      s = s.concat("<FEATURE_DATUM>" + geodeticDatum.getName()
          + "</FEATURE_DATUM>\n");
    } else {
      s = s.concat("<FEATURE_DATUM>not recorded</FEATURE_DATUM>\n");
    }
    s = s.concat("<EXTENTINMETERS>" + extentInMeters + "</EXTENTINMETERS>\n");
    s = s.concat("<COORDINATEPRECISION>" + coordPrecision
        + "</COORDINATEPRECISION>\n");
    s = s.concat("<MAPACCURACYINMETERS>" + mapAccuracyInMeters
        + "</MAPACCURACYINMETERS>\n");

    if (coordSource != null && coordSource.length() > 0) {
      s = s
          .concat("<COORDINATESOURCE>" + coordSource + "</COORDINATESOURCE>\n");
    } else {
      s = s.concat("<COORDINATESOURCE>not recorded</COORDINATESOURCE>\n");
    }

    if (showgeom == true && encodedGeometry != null
        && encodedGeometry.length() > 0) {
      s = s.concat("<ENCODEDGEOMETRY>" + encodedGeometry
          + "</ENCODEDGEOMETRY>\n");
    } else {
      s = s.concat("<ENCODEDGEOMETRY></ENCODEDGEOMETRY>\n");
    }

    s = s.concat("<FEATUREINFO_STATE>" + state + "</FEATUREINFO_STATE>\n");
    s = s.concat("</FEATUREINFO>\n");

    return s;
  }
}