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
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.biogeomancer.records.FeatureInfo;
import org.biogeomancer.records.FeatureInfoState;
import org.biogeomancer.records.Georef;
import org.biogeomancer.utils.PointRadius;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * @author asteele
 * 
 */
public class ADLGazetteer extends BGManager {
	private class IFeatureName {
		private final static String SEPARATORS = " ;";
//		private final static String AND = "&";
//		private final static String OR = "|";

		public boolean addFeaturesByName(Connection gdb,
				ArrayList<FeatureInfo> features, String feature, String querytype) {
			if (gdb == null || features == null || feature == null
					|| querytype == null)
				return false;
			String searchname = new String(feature.replace("'", "\\'"));
			FeatureInfo fi = null;
			Statement st = null;
			boolean featureadded = false;

			String query = "SELECT"
				+ " i_feature_footprint.feature_id,"
				+ " geom_y,"
				+ " geom_x,"
				+ " radius,"
				+ " displayname,"
				+ " g_feature_name.name,"
				+ " i_scheme_term.term,"
				+ " g_collection.name,"
				+ " g_collection.mapaccuracyinmeters,"
				+ " g_collection.coordprecision"
//				+ " g_collection.coordprecision,"
//				+ " asText(footprint)"
				+ " FROM"
				+ " i_feature_footprint,"
				+ " g_feature,"
				+ " g_feature_name,"
				+ " g_feature_displayname,"
				+ " i_scheme_term,"
				+ " i_classification,"
				+ " g_collection"
				+ " WHERE"
				+ " i_feature_footprint.feature_id=g_feature_displayname.feature_id"
				+ " AND i_feature_footprint.feature_id=g_feature.feature_id"
				+ " AND i_feature_footprint.feature_id=g_feature_name.feature_id"
				+ " AND i_feature_footprint.feature_id=i_classification.feature_id"
				+ " AND i_classification.classification_term_id=i_scheme_term.scheme_term_id"
				+ " AND g_feature.collection_id=g_collection.collection_id";

			if (querytype.equalsIgnoreCase("equals-ignore-case")) {
				// Make sure database is indexed on lower(name)
				query = query.concat(" AND lower(g_feature_name.name) = '"
						+ searchname.toLowerCase().trim() + "';");
				try {
					st = gdb.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (querytype.equalsIgnoreCase("contains-phrase")) {
				// Make sure database is indexed properly for contains phrase
				query = query.concat(" AND lower(g_feature_name.name) LIKE'%"
						+ searchname.toLowerCase().trim() + "%';");
				log.info("Contains-phrase query required for feature name: "
						+ searchname.toLowerCase().trim() + ". " + query);
				try {
					st = gdb.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (querytype.equalsIgnoreCase("contains-all-words")) {
				// Make sure database is indexed properly for contains words queries
				query = query.concat(" AND idxfti@@to_tsquery('default','"
						+ searchname.toLowerCase().trim().replace(" ", "&") + "');");
				// query =
				// "SELECT " +
				// " feature_id " +
				// " FROM " +
				// " g_feature_name " +
				// " WHERE " +
				// " idxfti@@to_tsquery('default','" +
				// searchname.toLowerCase().trim().replace(" ", "&") +
				// "');";
				// select feature_id,name,idxfti from g_feature_name where
				// idxfti@@to_tsquery('default','santa&ROSA');
				log.info("Contains-all-words query required for feature name: "
						+ searchname.toLowerCase().trim() + ". " + query);
				try {
					st = gdb.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (querytype.equalsIgnoreCase("contains-any-words")) {
				// Make sure database is indexed properly for contains words
				query = query.concat(" AND idxfti@@to_tsquery('default','"
						+ searchname.toLowerCase().trim().replace(" ", "|") + "');");
				// query =
				// "SELECT " +
				// " feature_id " +
				// " FROM " +
				// " g_feature_name " +
				// " WHERE " +
				// " idxfti@@to_tsquery('default','" +
				// searchname.toLowerCase().trim().replace(" ", "|") +
				// "');";
				// select feature_id,name,idxfti from g_feature_name where
				// idxfti@@to_tsquery('default','santa|ROSA');
				log.info("Contains-any-words query required for feature name: "
						+ searchname.toLowerCase().trim() + ". " + query);
				try {
					st = gdb.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			try {
				ResultSet rs = st.executeQuery(query);
				String namefound = null;

				while (rs.next()) {
					fi = new FeatureInfo();
					fi.featureID = new Integer(rs.getString(1)).intValue();
					fi.latitude = rs.getDouble(2);
					fi.longitude = rs.getDouble(3);
					fi.extentInMeters = rs.getDouble(4);
					fi.geodeticDatum = DatumManager.getInstance().getDatum("WGS84");
					fi.name = new String(rs.getString(5));
					namefound = new String(rs.getString(6));
					if (namefound.equalsIgnoreCase(fi.name) == false) {
						fi.name = new String(namefound + " (=" + rs.getString(5) + ")");
					}
					fi.classificationTerm = new String(rs.getString(7));
					fi.coordSource = new String(rs.getString(8));
					fi.mapAccuracyInMeters = rs.getDouble(9);
					fi.coordPrecision = rs.getDouble(10);
//					fi.encodedGeometry = new String(rs.getString(11));
					features.add(fi);
					featureadded = true;
				}
				rs.close();
				st.close();
			} catch (SQLException e) {
				log.error(e.toString());
			}
			return featureadded;
		}

		public boolean addUserFeaturesByName(ArrayList<FeatureInfo> features,
				String feature, String querytype) {
			if (feature == null || querytype == null)
				return false;
			String searchname = new String(feature.replace("'", "\\'"));
			FeatureInfo fi = null;
			Statement st = null;
			boolean featureadded = false;

			String query = "SELECT"
				+ " i_feature_footprint.feature_id,"
				+ " geom_y,"
				+ " geom_x,"
				+ " radius,"
				+ " displayname,"
				+ " i_scheme_term.term,"
				+ " g_collection.name,"
				+ " g_collection.mapaccuracyinmeters,"
				+ " g_collection.coordprecision"
//				+ " g_collection.coordprecision,"
//				+ " asText(footprint)"
				+ " FROM"
				+ " i_feature_footprint,"
				+ " g_feature,"
				+ " g_feature_displayname,"
				+ " i_scheme_term,"
				+ " i_classification,"
				+ " g_collection"
				+ " WHERE"
				+ " i_feature_footprint.feature_id=g_feature_displayname.feature_id"
				+ " AND i_feature_footprint.feature_id=g_feature.feature_id"
				+ " AND i_feature_footprint.feature_id=i_classification.feature_id"
				+ " AND i_classification.classification_term_id=i_scheme_term.scheme_term_id"
				+ " AND g_feature.collection_id=g_collection.collection_id";

			if (querytype.equalsIgnoreCase("equals-ignore-case")) {
				// Make sure database is indexed on lower(name)
				query = query.concat(" AND lower(displayname) = '"
						+ searchname.toLowerCase().trim() + "';");
				try {
					st = userplaces.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (querytype.equalsIgnoreCase("contains-phrase")) {
				// Make sure database is indexed properly for contains phrase
				query = query.concat(" AND lower(displayname) LIKE'%"
						+ searchname.toLowerCase().trim() + "%';");
				log.info("Contains-phrase query required for feature name: "
						+ searchname.toLowerCase().trim() + ". " + query);
				try {
					st = userplaces.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (querytype.equalsIgnoreCase("contains-all-words")) {
				// Make sure database is indexed properly for contains words queries
				query = query.concat(" AND idxfti@@to_tsquery('default','"
						+ searchname.toLowerCase().trim().replace(" ", "&") + "');");
				// query =
				// "SELECT " +
				// " feature_id " +
				// " FROM " +
				// " g_feature_name " +
				// " WHERE " +
				// " idxfti@@to_tsquery('default','" +
				// searchname.toLowerCase().trim().replace(" ", "&") +
				// "');";
				// select feature_id,name,idxfti from g_feature_name where
				// idxfti@@to_tsquery('default','santa&ROSA');
				log.info("Contains-all-words query required for feature name: "
						+ searchname.toLowerCase().trim() + ". " + query);
				try {
					st = userplaces.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (querytype.equalsIgnoreCase("contains-any-words")) {
				// Make sure database is indexed properly for contains words
				query = query.concat(" AND idxfti@@to_tsquery('default','"
						+ searchname.toLowerCase().trim().replace(" ", "|") + "');");
				// query =
				// "SELECT " +
				// " feature_id " +
				// " FROM " +
				// " g_feature_name " +
				// " WHERE " +
				// " idxfti@@to_tsquery('default','" +
				// searchname.toLowerCase().trim().replace(" ", "|") +
				// "');";
				// select feature_id,name,idxfti from g_feature_name where
				// idxfti@@to_tsquery('default','santa|ROSA');
				log.info("Contains-any-worrds query required for feature name: "
						+ searchname.toLowerCase().trim() + ". " + query);
				try {
					st = userplaces.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			try {
				ResultSet rs = st.executeQuery(query);

				while (rs.next()) {
					fi = new FeatureInfo();
					fi.featureID = new Integer(rs.getString(1)).intValue();
					fi.latitude = rs.getDouble(2);
					fi.longitude = rs.getDouble(3);
					fi.extentInMeters = rs.getDouble(4);
					fi.geodeticDatum = DatumManager.getInstance().getDatum("WGS84");
					fi.name = new String(rs.getString(5));
					fi.classificationTerm = new String(rs.getString(6));
					fi.coordSource = new String(rs.getString(7));
					fi.mapAccuracyInMeters = rs.getDouble(8);
					fi.coordPrecision = rs.getDouble(9);
//					fi.encodedGeometry = new String(rs.getString(10));
					features.add(fi);
					featureadded = true;
				}
				rs.close();
				st.close();
			} catch (SQLException e) {
				log.error(e.toString());
			}
			return featureadded;
		}

		// Tokenizes feature string to be proper form for fulltext query
		public String ftiQueryString(String line, String argument) {
			String result = "";
			StringTokenizer st = new StringTokenizer(line, SEPARATORS);
			while (st.hasMoreTokens()) {
				result += st.nextToken();
				if (st.hasMoreTokens())
					result += argument;
			}
			return result;
		}

		public ArrayList<FeatureInfo> selectDistinctFeaturesByName(Connection gdb,
				String feature, String querytype) {
			if (gdb == null || feature == null || querytype == null)
				return null;
			String searchname = new String(feature.replace("'", "\\'"));
			String query = "empty";
			int fid;
			ArrayList<FeatureInfo> features = new ArrayList<FeatureInfo>();
			FeatureInfo fi = null;
			Statement st = null;

			// Potential query types in order of most useful execution:
			// (all assume case is ignored and diacritical equivalence)
			// equals
			// contains-phrase
			// contains-all-words
			// contains-any-words
			// contains-potential-misspelling
			if (querytype.equalsIgnoreCase("equals-ignore-case")) {
				// Make sure database is indexed on lower(name)
				query = "SELECT DISTINCT " + " feature_id " + " FROM "
				+ " g_feature_name " + " WHERE " + " lower(name) = '"
				+ searchname.toLowerCase().trim() + "';";
				try {
					st = gdb.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (querytype.equalsIgnoreCase("contains-phrase")) {
				// Make sure database is indexed properly for contains phrase
				query = "SELECT DISTINCT " + " feature_id " + " FROM "
				+ " g_feature_name " + " WHERE " + " lower(name) LIKE'%"
				+ searchname.toLowerCase().trim() + "%';";
				// select feature_id,name from g_feature_name where lower(name) LIKE
				// '%santa rosa%';
				log.info("Contains-phrase query required for feature name: "
						+ searchname.toLowerCase().trim() + ". " + query);
				try {
					st = gdb.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (querytype.equalsIgnoreCase("contains-all-words")) {
				// Make sure database is indexed properly for contains words queries
				query = "SELECT DISTINCT" + " feature_id " + " FROM "
				+ " g_feature_name " + " WHERE "
				+ " idxfti@@to_tsquery('default','"
				+ searchname.toLowerCase().trim().replace(" ", "&") + "');";
				// select feature_id,name,idxfti from g_feature_name where
				// idxfti@@to_tsquery('default','santa&ROSA');
				log.info("Contains-all-words query required for feature name: "
						+ searchname.toLowerCase().trim() + ". " + query);
				try {
					st = gdb.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (querytype.equalsIgnoreCase("contains-any-words")) {
				// Make sure database is indexed properly for contains words
				query = "SELECT DISTINCT" + " feature_id " + " FROM "
				+ " g_feature_name " + " WHERE "
				+ " idxfti@@to_tsquery('default','"
				+ searchname.toLowerCase().trim().replace(" ", "|") + "');";
				// select feature_id,name,idxfti from g_feature_name where
				// idxfti@@to_tsquery('default','santa|ROSA');
				log.info("Contains-any-worrds query required for feature name: "
						+ searchname.toLowerCase().trim() + ". " + query);
				try {
					st = gdb.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			try {
				ResultSet rs = st.executeQuery(query);

				while (rs.next()) {
					fid = new Integer(rs.getString(1)).intValue();
					fi = new FeatureInfo();
					fi.featureID = fid;
					features.add(fi);
				}
				rs.close();
				st.close();
				return features;
			} catch (SQLException e) {
				log.error(e.toString());
			}
			return null;
		}

		public FeatureInfo selectFeatureById(Connection gdb, int featureid) {
			FeatureInfo fi = null;
			Statement st = null;
			String query = "SELECT"
				+ " i_feature_footprint.feature_id,"
				+ " geom_y,"
				+ " geom_x,"
				+ " radius,"
				+ " displayname,"
				+ " i_scheme_term.term,"
				+ " g_collection.name,"
				+ " g_collection.mapaccuracyinmeters,"
				+ " g_collection.coordprecision"
//				+ " g_collection.coordprecision,"
//				+ " asText(footprint)"
				+ " FROM"
				+ " i_feature_footprint,"
				+ " g_feature,"
				+ " g_feature_displayname,"
				+ " i_scheme_term,"
				+ " i_classification,"
				+ " g_collection"
				+ " WHERE"
				+ " i_feature_footprint.feature_id=g_feature_displayname.feature_id"
				+ " AND i_feature_footprint.feature_id=g_feature.feature_id"
				+ " AND i_feature_footprint.feature_id=i_classification.feature_id"
				+ " AND i_classification.classification_term_id=i_scheme_term.scheme_term_id"
				+ " AND g_feature.collection_id=g_collection.collection_id"
				+ " AND g_feature.feature_id = " + featureid;
			try {
				st = gdb.createStatement();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				ResultSet rs = st.executeQuery(query);

				while (rs.next()) {
					fi = new FeatureInfo();
					fi.featureID = featureid;
					fi.latitude = rs.getDouble(2);
					fi.longitude = rs.getDouble(3);
					fi.extentInMeters = rs.getDouble(4);
					fi.geodeticDatum = DatumManager.getInstance().getDatum("WGS84");
					fi.name = new String(rs.getString(5));
					fi.classificationTerm = new String(rs.getString(6));
					fi.coordSource = new String(rs.getString(7));
					fi.mapAccuracyInMeters = rs.getDouble(8);
					fi.coordPrecision = rs.getDouble(9);
//					fi.encodedGeometry = new String(rs.getString(10));
				}
				rs.close();
				st.close();
				return fi;
			} catch (SQLException e) {
				log.error(e.toString() + "\n" + query);
			}
			return null;
		}

		public ArrayList<FeatureInfo> selectFeaturesByName(Connection gdb,
				String feature, String querytype) {
			if (gdb == null || feature == null || querytype == null)
				return null;
			String searchname = new String(feature.replace("'", "\\'"));
			String query = "empty";
			int fid;
			ArrayList<FeatureInfo> features = new ArrayList<FeatureInfo>();
			FeatureInfo fi = null;
			Statement st = null;

			// Potential query types in order of most useful execution:
			// (all assume case is ignored and diacritical equivalence)
			// equals
			// contains-phrase
			// contains-all-words
			// contains-potential-misspelling
			if (querytype.equalsIgnoreCase("equals-ignore-case")) {
				// Make sure database is indexed on lower(name)
				query = "SELECT " + " feature_id " + " FROM " + " g_feature_name "
				+ " WHERE " + " lower(name) = '" + searchname.toLowerCase().trim()
				+ "';";
				try {
					st = gdb.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (querytype.equalsIgnoreCase("contains-phrase")) {
				// Make sure database is indexed properly for contains phrase
				query = "SELECT " + " feature_id " + " FROM " + " g_feature_name "
				+ " WHERE " + " lower(name) LIKE'%"
				+ searchname.toLowerCase().trim() + "%';";
				// select feature_id,name from g_feature_name where lower(name) LIKE
				// '%santa rosa%';
				log.info("Contains-phrase query required for feature name: "
						+ searchname.toLowerCase().trim() + ". " + query);
				try {
					st = gdb.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (querytype.equalsIgnoreCase("contains-all-words")) {
				// Make sure database is indexed properly for contains words queries
				query = "SELECT " + " feature_id " + " FROM " + " g_feature_name "
				+ " WHERE " + " idxfti@@to_tsquery('default','"
				+ searchname.toLowerCase().trim().replace(" ", "&") + "');";
				// select feature_id,name,idxfti from g_feature_name where
				// idxfti@@to_tsquery('default','santa&ROSA');
				log.info("Contains-all-words query required for feature name: "
						+ searchname.toLowerCase().trim() + ". " + query);
				try {
					st = gdb.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (querytype.equalsIgnoreCase("contains-any-words")) {
				// Make sure database is indexed properly for contains words
				query = "SELECT " + " feature_id " + " FROM " + " g_feature_name "
				+ " WHERE " + " idxfti@@to_tsquery('default','"
				+ searchname.toLowerCase().trim().replace(" ", "|") + "');";
				// select feature_id,name,idxfti from g_feature_name where
				// idxfti@@to_tsquery('default','santa|ROSA');
				log.info("Contains-any-worrds query required for feature name: "
						+ searchname.toLowerCase().trim() + ". " + query);
				try {
					st = gdb.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			try {
				ResultSet rs = st.executeQuery(query);

				while (rs.next()) {
					fid = new Integer(rs.getString(1)).intValue();
					fi = new FeatureInfo();
					fi.featureID = fid;
					features.add(fi);
				}
				rs.close();
				st.close();
				return features;
			} catch (SQLException e) {
				log.error(e.toString());
			}
			return null;
		}

		public ArrayList<FeatureInfo> selectQuickFeaturesByName(Connection gdb,
				String feature, String querytype) {
			if (gdb == null || feature == null || querytype == null)
				return null;
			String searchname = new String(feature.replace("'", "\\'"));
//			double lat = 90, lng = 0;
//			double radius = -1;
//			String displayname = null;
			String query =
				/*
				 * "SELECT" + " i_feature_footprint.feature_id," + " geom_y,"+ " geom_x,"+ "
				 * radius,"+ " displayname"+ " FROM" + " i_feature_footprint," + "
				 * g_feature," + " g_feature_displayname," + " g_feature_name" + " WHERE" + "
				 * i_feature_footprint.feature_id=g_feature_displayname.feature_id" + "
				 * AND i_feature_footprint.feature_id=g_feature.feature_id" + " AND
				 * i_feature_footprint.feature_id=g_feature_name.feature_id";
				 */
				"SELECT"
				+ " i_feature_footprint.feature_id,"
				+ " geom_y,"
				+ " geom_x,"
				+ " radius,"
				+ " displayname,"
				+ " g_collection.name,"
				+ " g_collection.coordprecision,"
				+ " g_collection.mapaccuracyinmeters,"
				+ " i_scheme_term.term"
//				+ " i_scheme_term.term,"
//				+ " asText(footprint)"
				+ " FROM"
				+ " i_feature_footprint,"
				+ " g_feature,"
				+ " g_feature_displayname,"
				+ " g_feature_name,"
				+ " i_scheme_term,"
				+ " i_classification,"
				+ " g_collection"
				+ " WHERE"
				+ " i_feature_footprint.feature_id=g_feature_displayname.feature_id"
				+ " AND i_feature_footprint.feature_id=g_feature.feature_id"
				+ " AND i_feature_footprint.feature_id=i_classification.feature_id"
				+ " AND i_classification.classification_term_id=i_scheme_term.scheme_term_id"
				+ " AND g_feature.collection_id=g_collection.collection_id"
				+ " AND i_feature_footprint.feature_id=g_feature_name.feature_id";

			// int fid;
			ArrayList<FeatureInfo> features = new ArrayList<FeatureInfo>();
			FeatureInfo fi = null;
			Statement st = null;

			// Potential query types in order of most useful execution:
			// (all assume case is ignored and diacritical equivalence)
			// equals
			// contains-phrase
			// contains-all-words
			// contains-potential-misspelling
			if (querytype.equalsIgnoreCase("equals-ignore-case")) {
				// Make sure database is indexed on lower(name)
				query = query.concat(" AND lower(g_feature_name.name) = '"
						+ searchname.toLowerCase().trim() + "';");
				// select feature_id, name from g_feature_name where lower(name) ='santa
				// rosa';
				try {
					st = gdb.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println(query);
				}
			} else if (querytype.equalsIgnoreCase("contains-phrase") /*
			 * &&
			 * searchname.length() >
			 * 3
			 */) {
				// Make sure database is indexed properly for contains phrase
				query = query.concat(" AND lower(g_feature_name.name) LIKE'%"
						+ searchname.toLowerCase().trim() + "%';");
				// select feature_id, name from g_feature_name where lower(name) LIKE
				// '%santa rosa%';
				log.info("Contains-phrase query required for feature name: "
						+ searchname.toLowerCase().trim() + ". " + query);
				try {
					st = gdb.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println(query);
				}
			} else if (querytype.equalsIgnoreCase("contains-all-words")) {
				// Make sure database is indexed properly for contains words queries
				query = query.concat(" AND idxfti@@to_tsquery('default','"
						+ searchname.toLowerCase().trim().replace(" ", "&") + "');");
				// select feature_id, name, idxfti from g_feature_name where
				// idxfti@@to_tsquery('default','santa&ROSA');
				log.info("Contains-all-words query required for feature name: "
						+ searchname.toLowerCase().trim() + ". " + query);
				try {
					st = gdb.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println(query);
				}
			} else if (querytype.equalsIgnoreCase("contains-any-words")) {
				// Make sure database is indexed properly for contains words
				query = query.concat(" AND idxfti@@to_tsquery('default','"
						+ searchname.toLowerCase().trim().replace(" ", "|") + "');");
				// select feature_id, name, idxfti from g_feature_name where
				// idxfti@@to_tsquery('default','santa|ROSA');
				log.info("Contains-any-words query required for feature name: "
						+ searchname.toLowerCase().trim() + ". " + query);
				try {
					st = gdb.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println(query);
				}
			}
			try {
				ResultSet rs = st.executeQuery(query);
				while (rs.next()) {
					fi = new FeatureInfo();
					fi.featureID = new Integer(rs.getString(1)).intValue();
					fi.latitude = rs.getDouble(2);
					fi.longitude = rs.getDouble(3);
					fi.extentInMeters = rs.getDouble(4);
					fi.name = new String(rs.getString(5));
					fi.coordSource = new String(rs.getString(6));
					fi.coordPrecision = rs.getDouble(7);
					fi.mapAccuracyInMeters = rs.getDouble(8);
					fi.classificationTerm = new String(rs.getString(9));
//					fi.encodedGeometry = new String(rs.getString(10));
					fi.geodeticDatum = DatumManager.getInstance().getDatum("WGS84");
					features.add(fi);
				}
				rs.close();
				st.close();
				return features;
			} catch (SQLException e) {
				log.error(e.toString() + "\n" + query);
			}
			return null;
		}
	}

	private static ADLGazetteer instance;
	private static final Logger log = Logger.getLogger(ADLGazetteer.class);
	private static Properties props = new Properties();

	public static ADLGazetteer getInstance() throws Exception {
		if (instance == null)
			instance = new ADLGazetteer();
		return instance;
	}

	public static void main(String[] argv) {
		if (argv.length < 2) {
			System.out.println("arguments are datasource featureid (-1 for all)");
			return;
		}
		Integer z = new Integer(argv[1]);
		int featureid = z.intValue();
		String db = argv[0];

		ADLGazetteer adl = null;
		try {
			adl = new ADLGazetteer();
			Connection con = worldplaces;
			if (argv[0].equalsIgnoreCase("userplaces")) {
				con = userplaces;
			} else if (argv[0].equalsIgnoreCase("gadm")) {
				con = gadm;
			} else if (argv[0].equalsIgnoreCase("plss")) {
				con = plss;
			} else if (argv[0].equalsIgnoreCase("gn")) {
				con = gn;
			} else if (argv[0].equalsIgnoreCase("conustigerplaces")) {
				con = conustigerplaces;
			} else if (argv[0].equalsIgnoreCase("gnispopulatedplaces")) {
				con = gnispopulatedplaces;
			} else if (argv[0].equalsIgnoreCase("protectedplaces")) {
				con = protectedplaces;
			}
			adl.selectIFeatureFootprint(con, featureid);
//			adl.setIFeatureFootprintRadii(con, featureid);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			adl.shutdown();
		}


		// Potential query types in order of most useful execution:
		// (all assume case is ignored and diacritical equivalence)
		// equals
		// contains-phrase
		// contains-all-words
		// contains-any-words
		// contains-potential-misspelling


		/*	  
    if (argv.length < 3) {
      System.out.println("arguments are featurename querytype datasource");
      return;
    }

    String feature = new String(argv[0]);
    String querytype = new String(argv[1]);
    // String querytype = new String("equals-ignore-case");
    String logtype = new String("system");
    ADLGazetteer adl = null;

    try {
      adl = new ADLGazetteer();
      Connection con = worldplaces;
      if (argv[2].equalsIgnoreCase("userplaces")) {
        con = userplaces;
        FeatureInfo f = adl.featureLookup(con, 69);
        System.out.println(f);
      } else if (argv[2].equalsIgnoreCase("gadm")) {
        con = gadm;
      } else if (argv[2].equalsIgnoreCase("plss")) {
        con = plss;
      } else if (argv[2].equalsIgnoreCase("gn")) {
        con = gn;
      } else if (argv[2].equalsIgnoreCase("conustigerplaces")) {
        con = conustigerplaces;
      } else if (argv[2].equalsIgnoreCase("gnispopulatedplaces")) {
        con = gnispopulatedplaces;
      }
      ArrayList<FeatureInfo> results = adl.featureLookup(con, feature,
          querytype, logtype);
      adl.addFeatures(userplaces, results, feature, querytype);
      for (FeatureInfo fi : results) {
        System.out.println(fi);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      adl.shutdown();
    }
		 */
	}

	private ADLGazetteer.IFeatureName iFeatureName;

	private ADLGazetteer() throws SQLException {
		initProps("ADLGazetteer.properties", props);

		try {
			gadm = gazdbconnect(props.getProperty("adl.gadm.driver"), props
					.getProperty("adl.gadm.server"), props.getProperty("adl.gadm.host"),
					props.getProperty("adl.gadm.name"), props
					.getProperty("adl.gadm.user"), props.getProperty("adl.gadm.pass"));

			gn = gazdbconnect(props.getProperty("adl.gn.driver"), props
					.getProperty("adl.gn.server"), props.getProperty("adl.gn.host"),
					props.getProperty("adl.gn.name"), props.getProperty("adl.gn.user"),
					props.getProperty("adl.gn.pass"));

			plss = gazdbconnect(props.getProperty("adl.plss.driver"), props
					.getProperty("adl.plss.server"), props.getProperty("adl.plss.host"),
					props.getProperty("adl.plss.name"), props
					.getProperty("adl.plss.user"), props.getProperty("adl.plss.pass"));

			conustigerplaces = gazdbconnect(props.getProperty("adl.conus.driver"),
					props.getProperty("adl.conus.server"), props
					.getProperty("adl.conus.host"), props
					.getProperty("adl.conus.name"), props
					.getProperty("adl.conus.user"), props
					.getProperty("adl.conus.pass"));

			gnispopulatedplaces = gazdbconnect(props.getProperty("adl.gnis.driver"),
					props.getProperty("adl.gnis.server"), props
					.getProperty("adl.gnis.host"),
					props.getProperty("adl.gnis.name"), props
					.getProperty("adl.gnis.user"), props.getProperty("adl.gnis.pass"));

			worldplaces = gazdbconnect(props.getProperty("adl.worldplaces.driver"),
					props.getProperty("adl.worldplaces.server"), props
					.getProperty("adl.worldplaces.host"), props
					.getProperty("adl.worldplaces.name"), props
					.getProperty("adl.worldplaces.user"), props
					.getProperty("adl.worldplaces.pass"));

			protectedplaces = gazdbconnect(props.getProperty("adl.protected.driver"), props
					.getProperty("adl.protected.server"), props.getProperty("adl.protected.host"),
					props.getProperty("adl.protected.name"), props.getProperty("adl.protected.user"),
					props.getProperty("adl.protected.pass"));

			userplaces = gazdbconnect(props.getProperty("adl.userplaces.driver"),
					props.getProperty("adl.userplaces.server"), props
					.getProperty("adl.userplaces.host"), props
					.getProperty("adl.userplaces.name"), props
					.getProperty("adl.userplaces.user"), props
					.getProperty("adl.userplaces.pass"));

			iFeatureName = this.new IFeatureName();
		} finally { // closing here doesn't work, because this gets executed when
			// the try clause finishes, no matter what
			/*
			 * if( gadm!=null ) gadm.close(); if( gn!=null ) gn.close(); if(
			 * plss!=null ) plss.close(); if( conustigerplaces!=null )
			 * conustigerplaces.close(); if( gnispopulatedplaces!=null )
			 * gnispopulatedplaces.close(); if( worldplaces!=null )
			 * worldplaces.close();
			 */
		}
	}

	public boolean addFeatures(Connection gdb,
			ArrayList<FeatureInfo> featurelist, String featurename) {
		if (gdb == null || featurelist == null)
			return false;
		if (featurename == null || featurename.length() == 0)
			return false;
		boolean featureadded = iFeatureName.addFeaturesByName(gdb, featurelist,
				featurename, "equals-ignore-case");
		if (featurelist.isEmpty()) {
			featureadded = iFeatureName.addFeaturesByName(gdb, featurelist,
					featurename, "contains-phrase");
		}
		if (featurelist.isEmpty()) {
			featureadded = iFeatureName.addFeaturesByName(gdb, featurelist,
					featurename, "contains-all-words");
		}
		return featureadded;
	}

	public boolean addFeatures(Connection gdb,
			ArrayList<FeatureInfo> featurelist, String featurename, String querytype) {
		if (gdb == null || featurelist == null)
			return false;
		if (featurename == null || featurename.length() == 0)
			return false;
		boolean featureadded = iFeatureName.addFeaturesByName(gdb, featurelist,
				featurename, querytype);
		return featureadded;
	}

	public FeatureInfo featureLookup(Connection gdb, int featureid) {
		FeatureInfo fi = iFeatureName.selectFeatureById(gdb, featureid);
		return fi;
	}

	// Perform a feature lookup against the ADL gazetteer database.
	// @param gdb the gazetteer database in which to perform lookup
	// @param feature the feature name to lookup
	// @param type the type of lookup (equals-ignore-case, contains, etc.)
	// @return array of FeatureInfo objects
	public ArrayList<FeatureInfo> featureLookup(Connection gdb, String feature,
			String querytype, String logtype) {
		if (gdb == null || feature == null || querytype == null
				|| feature.length() == 0 || querytype.length() == 0)
			return null;
		ArrayList<FeatureInfo> fis = null;
		if (logtype == null) {
			// fis = iFeatureName.selectFeaturesByName(gdb, feature, querytype);
			fis = iFeatureName.selectDistinctFeaturesByName(gdb, feature, querytype);
			lookupFeatureDetails(gdb, fis);
			return fis;
		}
		if (logtype.equals("log")) {
			long starttime = System.currentTimeMillis();
			// fis = iFeatureName.selectFeaturesByName(gdb, feature, querytype);
			fis = iFeatureName.selectDistinctFeaturesByName(gdb, feature, querytype);
			lookupFeatureDetails(gdb, fis);
			long endtime = System.currentTimeMillis();
			if (fis == null || fis.isEmpty()) {
				log.info("Feature:\t" + feature + ";\tQuery type: " + querytype
						+ ";\tCount: 0;\tElapsed Time: " + (endtime - starttime) + "(ms)");
			} else {
				log.info("Feature:\t" + feature + ";\tQuery type: " + querytype
						+ ";\tCount: " + fis.size() + ";\tElapsed Time: "
						+ (endtime - starttime) + "(ms)");
			}
		} else if (logtype.equals("system")) {
			long starttime = System.currentTimeMillis();
			// fis = iFeatureName.selectFeaturesByName(gdb, feature, querytype);
			fis = iFeatureName.selectDistinctFeaturesByName(gdb, feature, querytype);
			long endtime = System.currentTimeMillis();
			if (fis == null || fis.isEmpty()) {
				System.out.println("Feature:\t" + feature + ";\tQuery type: "
						+ querytype + ";\tCount: 0;\tLookup Time: " + (endtime - starttime)
						+ "(ms)");
			} else {
				System.out.println("Feature:\t" + feature + ";\tQuery type: "
						+ querytype + ";\tCount: " + fis.size() + ";\tLookup Time: "
						+ (endtime - starttime) + "(ms)");
			}
			starttime = System.currentTimeMillis();
			lookupFeatureDetails(gdb, fis);
			endtime = System.currentTimeMillis();
			if (fis == null || fis.isEmpty()) {
				System.out.println("Feature:\t" + feature + ";\tQuery type: "
						+ querytype + ";\tCount: 0;\tDetail Lookup Time: "
						+ (endtime - starttime) + "(ms)");
			} else {
				System.out.println("Feature:\t" + feature + ";\tQuery type: "
						+ querytype + ";\tCount: " + fis.size() + ";\tDetail Lookup Time: "
						+ (endtime - starttime) + "(ms)");
			}
		} else {
			// fis = iFeatureName.selectFeaturesByName(gdb, feature, querytype);
			fis = iFeatureName.selectDistinctFeaturesByName(gdb, feature, querytype);
			lookupFeatureDetails(gdb, fis);
		}
		return fis;
	}

	public ArrayList<FeatureInfo> featureQuickLookup(Connection gdb,
			String feature, String querytype, String logtype) {
		if (gdb == null || feature == null || querytype == null
				|| feature.length() == 0 || querytype.length() == 0)
			return null;
		ArrayList<FeatureInfo> fis = null;
		if (logtype == null) {
			fis = iFeatureName.selectQuickFeaturesByName(gdb, feature, querytype);
			// lookupQuickFeatureDetails(gdb, fis);
		} else if (logtype.equals("log")) {
			long starttime = System.currentTimeMillis();
			fis = iFeatureName.selectQuickFeaturesByName(gdb, feature, querytype);
			// lookupQuickFeatureDetails(gdb, fis);
			long endtime = System.currentTimeMillis();
			if (fis == null || fis.isEmpty()) {
				log.info("Feature:\t" + feature + ";\tQuery type: " + querytype
						+ ";\tCount: 0;\tElapsed Time: " + (endtime - starttime) + "(ms)");
			} else {
				log.info("Feature:\t" + feature + ";\tQuery type: " + querytype
						+ ";\tCount: " + fis.size() + ";\tElapsed Time: "
						+ (endtime - starttime) + "(ms)");
			}
		} else if (logtype.equals("system")) {
			long starttime = System.currentTimeMillis();
			// fis = iFeatureName.selectFeaturesByName(gdb, feature, querytype);
			// fis = iFeatureName.selectDistinctFeaturesByName(gdb, feature,
			// querytype);
			fis = iFeatureName.selectQuickFeaturesByName(gdb, feature, querytype);
			long endtime = System.currentTimeMillis();
			if (fis == null || fis.isEmpty()) {
				System.out.println("Feature:\t" + feature + ";\tQuery type: "
						+ querytype + ";\tCount: 0;\tLookup Time: " + (endtime - starttime)
						+ "(ms)");
			} else {
				System.out.println("Feature:\t" + feature + ";\tQuery type: "
						+ querytype + ";\tCount: " + fis.size() + ";\tLookup Time: "
						+ (endtime - starttime) + "(ms)");
			}
			// starttime = System.currentTimeMillis();
			// lookupQuickFeatureDetails(gdb, fis);
			// endtime = System.currentTimeMillis();
			// if (fis==null || fis.isEmpty()){
			// System.out.println("Feature:\t"+feature+";\tQuery type:
			// "+querytype+";\tCount: 0;\tDetail Lookup Time:
			// "+(endtime-starttime)+"(ms)");
			// }else{
			// System.out.println("Feature:\t"+feature+";\tQuery type:
			// "+querytype+";\tCount: "+fis.size()+";\tDetail Lookup Time:
			// "+(endtime-starttime)+"(ms)");
			// }
		} else {
			// fis = iFeatureName.selectDistinctFeaturesByName(gdb, feature,
			// querytype);
			// lookupQuickFeatureDetails(gdb, fis);
		}
		// Remove duplicate features.
		for (int i = 0; i < fis.size(); i++) {
			for (int j = 0; j < fis.size(); j++) {
				if (i != j) {
					if (fis.get(i).featureID == fis.get(j).featureID) {
						// Two features may have the same id if they came from different
						// databases (e.g., userplaces and worldplaces), so check if the
						// names are also the same.
						if (fis.get(i).name.equalsIgnoreCase(fis.get(j).name)) {
							fis.remove(j);
							j--;
						}
					}
				}
			}
		}
		return fis;
	}

	public double lookupBestGuessUncertainty(Connection gdb, int featureID) {
		String query = "SELECT"
			+ " i_scheme_term.bestguess_uncert, "
			+ " i_scheme_term.term"
			+ " FROM i_scheme_term,"
			+ " i_classification"
			+ " WHERE i_classification.classification_term_id=i_scheme_term.scheme_term_id"
			+ " AND i_classification.feature_id=" + featureID + ";";
		try {
			double extent = 0;
			String name = null;

			Statement st = gdb.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			if (rs.getRow() == 0) { // no rows
				log
				.info("BestGuessUncertainty: i_scheme_term not linked to feature_id "
						+ featureID);
			} else {
				extent = rs.getDouble(1);
				if (extent == 0) {
					name = rs.getString(2);
					log.info("No best guess extent found for feature_id:\t" + featureID
							+ " feature_type:\t" + name);
					return 0;
				}
			}

			rs.close();
			st.close();
			return extent;
		} catch (SQLException e) {
			log.error(e.toString());
		}

		return 0;
	}

	public void lookupCollectionAttributes(Connection gdb, FeatureInfo fi) {
		String src = null; // Name of the coordinate source data set.
		double acc = 1000; // Use default 1000 meter map accuracy if not given
		// explictly.
		double prec = 0; // Use 0 coordinate precision if not explicitly given.

		String query = " SELECT" + " g_collection.name,"
		+ " g_collection.coordprecision," + " g_collection.mapaccuracyinmeters"
		+ " FROM" + " g_feature," + " g_collection" + " WHERE"
		+ " g_feature.collection_id=g_collection.collection_id"
		+ " AND g_feature.feature_id=" + fi.featureID + ";";
		try {
			Statement st = gdb.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			if (rs.getRow() == 0) { // no rows
				src = new String("feature source not recorded");
			} else {
				src = new String(rs.getString(1));
				prec = rs.getDouble(2);
				acc = rs.getDouble(3);
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			log.error(e.toString());
		}
		fi.coordSource = src;
		fi.coordPrecision = prec;
		fi.mapAccuracyInMeters = acc;
	}

	public String lookupConvexHull(Connection gdb, int featureID) {
		String wktGeometry = null;
		String query = " SELECT asText(geom)" + " FROM i_feature_footprint "
		+ " WHERE feature_id=" + featureID + ";";
		try {
			Statement st = gdb.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			if (rs.getRow() != 0) { // no rows
				wktGeometry = new String(rs.getString(1));
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			log.error(e.toString());
		}
		return wktGeometry;
	}

	public double lookupCoordinatePrecision(Connection gdb, int featureID) {
		double d = 0;
		String query = "SELECT" + " g_collection.coordprecision" + " FROM"
		+ " g_feature," + " g_collection" + " WHERE"
		+ " g_feature.collection_id=g_collection.collection_id"
		+ " AND g_feature.feature_id=" + featureID + ";";
		try {
			Statement st = gdb.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			if (rs.getRow() == 0) { // no rows
				log.info("CoordinatePrecision: g_collection not linked to feature_id "
						+ featureID);
			} else {
				d = rs.getDouble(1);
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			log.error(e.toString());
		}
		return d;
	}

	public String lookupCoordinateSource(Connection gdb, int featureID) {
		String s = null;
		String query = "SELECT" + " g_collection.name" + " FROM" + " g_feature,"
		+ " g_collection" + " WHERE "
		+ " g_feature.collection_id=g_collection.collection_id"
		+ " AND g_feature.feature_id=" + featureID + ";";
		try {
			Statement st = gdb.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			if (rs.getRow() == 0) { // no rows
				s = new String("no g_collection record for feature_id " + featureID);
			} else {
				s = new String(rs.getString(1));
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			log.error(e.toString());
		}
		return s;
	}

	public String lookupDisplayName(Connection gdb, int featureID) {
		String s = null;
		String query = "SELECT displayname" + " FROM g_feature_displayname"
		+ " WHERE feature_id=" + featureID + ";";
		try {
			Statement st = gdb.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			if (rs.getRow() == 0) { // no rows
				s = new String("no g_feature_displayname for feature_id " + featureID);
			} else {
				s = new String(rs.getString(1));
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			log.error(e.toString());
		}
		return s;
	}

	public void lookupFeatureDetails(Connection gdb, ArrayList<FeatureInfo> fis) {
		// if( gdb == null || fis == null || fis.isEmpty()) return;
		// Get the rest of the information for the featureinfos.
		GeometryFactory gf = new GeometryFactory();
		WKTReader wktreader = null;
		Geometry g = null;
		Georef georef = null;
		PointRadius pr = null;
		FeatureInfo fi = null;
		for (int i = 0; i < fis.size(); i++) {
			fi = fis.get(i);
			try {
				// Get the extent from footprint if it exists.
				lookupFootprintAttributes(gdb, fi);
				if (fi.encodedGeometry == null
						|| fi.encodedGeometry.toLowerCase().contains("empty")) {
					// Otherwise get it from geom, which is a convex hull.
					log.info("Convex hull lookup required for feature_id: "
							+ fi.featureID);
					fi.encodedGeometry = lookupConvexHull(gdb, fi.featureID);
					fi.geodeticDatum = DatumManager.getInstance().getDatum("WGS84");
				}
				if (fi.encodedGeometry != null) {
					wktreader = new WKTReader(gf);
					// System.out.println(fi.featureID+":"+fi.encodedGeometry);
					g = wktreader.read(fi.encodedGeometry);
					georef = new Georef(g, fi.geodeticDatum);
					pr = georef.makePointRadius(g, fi.geodeticDatum);
					if (pr != null) {
						fi.latitude = pr.y;
						fi.longitude = pr.x;
						fi.extentInMeters = pr.extent;
						if (fi.extentInMeters < 1) {
							// The PointRadius representation of the geometry has a miniscule
							// radius.
							fi.extentInMeters = lookupBestGuessUncertainty(gdb, fi.featureID);
						}
						lookupFeatureTypeAttributes(gdb, fi);
						lookupCollectionAttributes(gdb, fi);
						fi.name = lookupDisplayName(gdb, fi.featureID);
						fi.state = FeatureInfoState.FEATUREINFO_COMPLETED;
					} else {
						fis.remove(fi);
						i--;
					}
				} else {
					fis.remove(fi);
					i--;
				}
			} catch (ParseException e) {
				fi.state = FeatureInfoState.FEATUREINFO_CREATION_ERROR;
				e.printStackTrace();
			}
		}
	}

	public String lookupFeatureType(Connection gdb, int featureID) {
		String s = null;
		String query = "SELECT"
			+ " i_scheme_term.term"
			+ " FROM"
			+ " i_feature_footprint,"
			+ " i_scheme_term,"
			+ " i_classification"
			+ " WHERE"
			+ " i_feature_footprint.feature_id=i_classification.feature_id"
			+ " AND i_classification.classification_term_id=i_scheme_term.scheme_term_id"
			+ " AND i_feature_footprint.feature_id=" + featureID + ";";

		try {
			Statement st = gdb.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			if (rs.getRow() == 0) { // no rows
				s = new String("FeatureType: i_scheme_term not linked to feature_id "
						+ featureID);
			} else {
				s = new String(rs.getString(1));
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			log.error(e.toString());
		}
		return s;
	}

	public void lookupFeatureTypeAttributes(Connection gdb, FeatureInfo fi) {
		String featuretype = null;
		double uncertainty = 0; // Use 0 as best guess uncertainty if not given
		// explicitly.
		String query = "SELECT "
			+ " i_scheme_term.bestguess_uncert,"
			+ " i_scheme_term.term"
			+ " FROM"
			+ " i_feature_footprint,"
			+ " i_scheme_term,"
			+ " i_classification"
			+ " WHERE"
			+ " i_feature_footprint.feature_id=i_classification.feature_id"
			+ " AND i_classification.classification_term_id=i_scheme_term.scheme_term_id"
			+ " AND i_feature_footprint.feature_id=" + fi.featureID + ";";

		try {
			Statement st = gdb.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			if (rs.getRow() == 0) { // no rows
				featuretype = new String("feature type not recorded");
			} else {
				uncertainty = rs.getDouble(1);
				featuretype = new String(rs.getString(2));
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			log.error(e.toString());
		}
		fi.classificationTerm = featuretype;
		if (fi.extentInMeters < 1)
			fi.extentInMeters = uncertainty;
	}

	public String lookupFootprint(Connection gdb, int featureID) {
		String wktGeometry = null;
		String query = "SELECT asText(footprint) " + "FROM i_feature_footprint "
		+ "WHERE feature_id=" + featureID + ";";
		try {
			Statement st = gdb.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			if (rs.getRow() != 0) { // no rows
				wktGeometry = new String(rs.getString(1));
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			log.error(e.toString());
		}
		return wktGeometry;
	}

	public void lookupFootprintAttributes(Connection gdb, FeatureInfo fi) {
		String wktGeometry = null;
		double lat = 90, lng = 0, radius = 0;
		String query = " SELECT" + " asText(footprint)," + " geom_y," + " geom_x,"
		+ " radius" + " FROM i_feature_footprint " + " WHERE feature_id="
		+ fi.featureID + ";";
		try {
			Statement st = gdb.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			if (rs.getRow() != 0) { // has rows
				if (rs.getString(1) == null) { // feature has no footprint
					rs.close();
					st.close();
					return;
				}
				wktGeometry = new String(rs.getString(1));
				lat = rs.getDouble(2);
				lng = rs.getDouble(3);
				radius = rs.getDouble(4);
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			log.error(e.toString());
		}
		fi.encodedGeometry = wktGeometry;
		fi.latitude = lat;
		fi.longitude = lng;
		fi.extentInMeters = radius;
		fi.geodeticDatum = DatumManager.getInstance().getDatum("WGS84");
	}

	public double lookupMapAccuracyInMeters(Connection gdb, int featureID) {
		double d = 1000; // use default 1000 meter map accuracy if not given
		// explictly
		String query = "SELECT" + " g_collection.mapaccuracyinmeters" + " FROM"
		+ " g_feature," + " g_collection" + " WHERE"
		+ " g_feature.collection_id=g_collection.collection_id"
		+ " AND g_feature.feature_id=" + featureID + ";";
		try {
			Statement st = gdb.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			if (rs.getRow() == 0) { // no rows
				log.info("MapAccuracy: g_collection not linked to feature_id "
						+ featureID);
			} else {
				d = rs.getDouble(1);
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			log.error(e.toString());
		}
		if (d < 0)
			d = 0;
		return d;
	}

	public void lookupNearestFeature(Connection gdb, FeatureInfo fromfi,
			FeatureInfo tofi, double ddlimit, String withingeomEWKT) {
		// finds the feature (tofi) nearest to fromfi within a geometry specified
		// by withingeomEWKT, but further from fromfi than the distance in decimal
		// degrees specified by ddlimit. Unfortunately, this search is too slow to
		// be practical.
		int featureid = -1;
		double lat = 90, lng = 0, radius = 0;
		double distanceindd = 0, withindist = 0;
		String query = " SELECT" + " feature_id," + " geom_y," + " geom_x,"
		+ " radius,"
		+ " Distance(centroid(geom),GeomFromEWKT('SRID=4326;POINT("
		+ fromfi.longitude + " " + fromfi.latitude + ")')) as distanceindd,"
		+ " Distance(centroid(geom),GeomFromEWKT(" + withingeomEWKT
		+ ")) as withindist" + " FROM i_feature_footprint " + " WHERE"
		+ " Distance(centroid(geom),GeomFromEWKT(" + withingeomEWKT + ")) = 0"
		+ " AND Distance(centroid(geom),GeomFromEWKT('SRID=4326;POINT("
		+ fromfi.longitude + " " + fromfi.latitude + ")')) >= " + ddlimit
		+ " ORDER BY distanceindd ASC LIMIT 1";

		/*
		 * Example: SELECT feature_id, geom_y, geom_x, radius,
		 * Distance(centroid(geom),GeomFromEWKT('SRID=4326;POINT(-113.9930596
		 * 46.8722197)')) as distanceindd,
		 * Distance(centroid(geom),GeomFromEWKT('SRID=4326;POLYGON((-113.9930596
		 * 46.8722197,-114.92056926599541 47.5082881399381,-115.30475634888285
		 * 46.8722197,-114.92056926599541 46.2361512600619,-113.9930596
		 * 46.8722197))')) as withindist FROM i_feature_footprint WHERE
		 * Distance(centroid(geom),GeomFromEWKT('SRID=4326;POINT(-113.9930596
		 * 46.8722197)')) >= 0.04530465685560272 AND
		 * Distance(centroid(geom),GeomFromEWKT('SRID=4326;POLYGON((-113.9930596
		 * 46.8722197,-114.92056926599541 47.5082881399381,-115.30475634888285
		 * 46.8722197,-114.92056926599541 46.2361512600619,-113.9930596
		 * 46.8722197))')) = 0 ORDER BY distanceindd ASC LIMIT 1;
		 */
		try {
			Statement st = gdb.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			if (rs.getRow() != 0) { // has rows
				if (rs.getString(1) == null) { // feature has no footprint
					rs.close();
					st.close();
					return;
				}
				featureid = rs.getInt(1);
				lat = rs.getDouble(2);
				lng = rs.getDouble(3);
				radius = rs.getDouble(4);
				distanceindd = rs.getDouble(5);
				withindist = rs.getDouble(6);
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			log.error(e.toString());
		}
		tofi.featureID = featureid;
		tofi.latitude = lat;
		tofi.longitude = lng;
		tofi.extentInMeters = radius;
		tofi.geodeticDatum = DatumManager.getInstance().getDatum("WGS84");
	}

	public void lookupPointRadiusAttributes(Connection gdb, FeatureInfo fi) {
		double lat = 90, lng = 0, radius = 0;
		String query = " SELECT" + " geom_y," + " geom_x," + " radius"
		+ " FROM i_feature_footprint " + " WHERE feature_id=" + fi.featureID
		+ ";";
		try {
			Statement st = gdb.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			if (rs.getRow() != 0) { // has rows
				lat = rs.getDouble(1);
				lng = rs.getDouble(2);
				radius = rs.getDouble(3);
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			log.error(e.toString());
		}
		fi.latitude = lat;
		fi.longitude = lng;
		fi.extentInMeters = radius;
		fi.geodeticDatum = DatumManager.getInstance().getDatum("WGS84");
	}

	public void lookupQuickAttributes(Connection gdb, FeatureInfo fi) {
		double lat = 90, lng = 0, radius = 0;
//		double acc = 1000; // Use default 1000 meter map accuracy if not given
		// explictly.
//		double prec = 0; // Use 0 coordinate precision if not explicitly given.
		String s = null;
//		String src = null;

		String query = " SELECT"
			+ " geom_y,"
			+ " geom_x,"
			+ " radius,"
			+ " displayname"
			+
			// TODO: There is no assurrance that a collection record exists for the
			// feature.
			// Should check that the database does have this, but also avoid the
			// problem by not doing the join here.
			// " coordprecision, " +
			// " mapaccuracyinmeters," +
			// " g_collection.name," +
			" FROM i_feature_footprint, "
			+ " g_feature, "
			+
			// " g_collection," +
			" g_feature_displayname"
			+ " WHERE i_feature_footprint.feature_id=g_feature_displayname.feature_id"
			+
			// " AND g_feature.collection_id=g_collection.collection_id" +
			" AND g_feature.feature_id=i_feature_footprint.feature_id"
			+ " AND i_feature_footprint.feature_id=" + fi.featureID + ";";
		/*
		 * SELECT geom_y, geom_x, radius, coordprecision, mapaccuracyinmeters,
		 * g_collection.name, displayname FROM i_feature_footprint, g_feature,
		 * g_collection, g_feature_displayname WHERE
		 * i_feature_footprint.feature_id=g_feature_displayname.feature_id AND
		 * g_feature.collection_id=g_collection.collection_id AND
		 * g_feature.feature_id=i_feature_footprint.feature_id AND
		 * i_feature_footprint.feature_id=76000707;
		 */
		try {
			Statement st = gdb.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			if (rs.getRow() != 0) { // has rows
				lat = rs.getDouble(1);
				lng = rs.getDouble(2);
				radius = rs.getDouble(3);
				s = new String(rs.getString(4));
				// prec=rs.getDouble(5);
				// acc=rs.getDouble(6);
				// src = new String(rs.getString(7));
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			log.error(e.toString());
		}
		fi.latitude = lat;
		fi.longitude = lng;
		fi.extentInMeters = radius;
		fi.name = s;
		// fi.coordSource=src;
		// fi.coordPrecision=prec;
		// fi.mapAccuracyInMeters=acc;
		fi.geodeticDatum = DatumManager.getInstance().getDatum("WGS84");
	}

	public void lookupQuickFeatureDetails(Connection gdb,
			ArrayList<FeatureInfo> fis) {
		FeatureInfo fi = null;
		for (int i = 0; i < fis.size(); i++) {
			fi = fis.get(i);
			// try {
			lookupQuickAttributes(gdb, fi);
		}
	}

	public double lookupRadius(Connection gdb, int featureID) {
		double r = 0;
		String query = "SELECT radius " + "FROM i_feature_footprint "
		+ "WHERE feature_id=" + featureID + ";";
		try {
			Statement st = gdb.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			if (rs.getRow() != 0) { // no rows
				r = rs.getDouble(1);
				if (r <= 1) {
					log.error("Invalid i_feature_footprint.radius (" + r
							+ ") for feature_id:\t" + featureID);
					return 0;
				}
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			log.error(e.toString());
		}
		return r;
	}

	public void shutdown() {
		try {
			if (gadm != null)
				gadm.close();
			if (gn != null)
				gn.close();
			if (plss != null)
				plss.close();
			if (conustigerplaces != null)
				conustigerplaces.close();
			if (gnispopulatedplaces != null)
				gnispopulatedplaces.close();
			if (worldplaces != null)
				worldplaces.close();
			if (userplaces != null)
				userplaces.close();
			if (protectedplaces != null)
				protectedplaces.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	/*
	 * Need to insert details into: g_feature g_feature_name g_feature_displayname
	 * i_feature_footprint
	 * 
	 */
	public void insertFeature(Connection gdb, Georef g, String user, String featurename, int scheme_term_id) throws SQLException {
		if(g==null) return;
		int featureId = insertGFeature(gdb, user, g);
		insertGFeatureDisplayName(gdb, featurename, featureId);
		insertGFeatureName(gdb, featurename, featureId);
		insertIFeatureFootprint(gdb, g, featureId);
		insertIClassification(gdb, featureId, scheme_term_id);
	}

	private int insertGFeature(Connection gdb, String user, Georef g) throws SQLException {
		PreparedStatement ps;
		String q;
		try {
			q = "INSERT INTO g_feature VALUES (nextval('public.g_feature_feature_id_seq'), ?, ?, ?, ?, ?, ?)";
			ps = gdb.prepareStatement(q);
			ps.setInt(1, -1);
			ps.setBoolean(2, false);
			ps.setInt(3, -1);
			ps.setDate(4, new Date(new java.util.Date().getTime()));
			ps.setNull(5, Types.DATE);
			ps.setString(6, user);
//			ps.setString(6, user == null ? "-1" : user.getNickName() + " ("
//			+ user.getEmail() + ")");
//			System.out.println(ps.toString());
			ps.execute();
			ps.close();

			// Obtain a feature_id from generating a g_feature record
			q = "SELECT last_value FROM g_feature_feature_id_seq";
			ResultSet rs = gdb.createStatement().executeQuery(q);
			rs.next();
			int featureId = rs.getInt("last_value");
			return featureId;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	private void insertGFeatureDisplayName(Connection gdb, String name, int featureId)
	throws SQLException {
		PreparedStatement ps;
		String q;
		try {
			q = "INSERT INTO g_feature_displayname VALUES ( ?, ?, ?)";
			ps = gdb.prepareStatement(q);
			ps.setInt(1, featureId);
			ps.setString(2, name.trim());
			ps.setString(3, "null");
			System.out.println(ps.toString());
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	private int insertGFeatureName(Connection gdb, String name, int featureId)
	throws SQLException {
		PreparedStatement ps;
		String q;
		try {
			q = "INSERT INTO g_feature_name VALUES ( ?, true, ?, null, null, null)";
			ps = gdb.prepareStatement(q);
			ps.setInt(1, featureId);
			ps.setString(2, name.trim());
//			System.out.println(ps.toString());
			ps.execute();
			ps.close();

			// Generate a g_feature_name_id and return it
			q = "SELECT nextval('g_feature_name_feature_name_id_seq')";
			ResultSet rs = gdb.createStatement().executeQuery(q);
			rs.next();
			int featureNameId = rs.getInt("nextval");
			return featureNameId;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	private void insertIClassification(Connection gdb, int featureId, int scheme_term_id) throws SQLException {
		PreparedStatement ps;
		String q;
		try {
			q = "INSERT INTO i_classification VALUES ( ?, ?)";
			ps = gdb.prepareStatement(q);
			ps.setInt(1, featureId);
			ps.setInt(2, scheme_term_id);
//			System.out.println(ps.toString());
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	private void insertIFeatureFootprint(Connection gdb, Georef g, int featureId)
	throws SQLException {
		PreparedStatement ps;
		String q;
		try {
			/*
			 * Arguments to insert a row into i_feature_footprint 
			 * 1. feature_id 
			 * 2. footprint (MULTIPOLYGON) 
			 * 3. min x 
			 * 4. min y 
			 * 5. max y 
			 * 6. max x 
			 * 7. radius
			 * 8. x center 
			 * 9. y center 
			 * 10. envelope (null) 
			 * 11. geom (POLYGON)
			 */
			String fp = new String(makeFootprintEwkt(g));
			String gp = new String(makeGeomEwkt(g));
			q = "INSERT INTO i_feature_footprint VALUES (?, " + makeFootprintEwkt(g)
			+ ", ?, ?, ?, ?, 'user', ?, 2, ?, ?, null, " + makeGeomEwkt(g) + ")";
//			q = "INSERT INTO i_feature_footprint VALUES (?, " + makeFootprintEwkt(g)
//			+ ", ?, ?, ?, ?, 'user', ?, 2, ?, ?, null, " + makeGeomEwkt(g) + ")";
			ps = gdb.prepareStatement(q);
			ps.setInt(1, featureId);

			ps.setDouble(2, g.getMinLng());
			ps.setDouble(3, g.getMinLat());
			ps.setDouble(4, g.getMaxLat());
			ps.setDouble(5, g.getMinLng());

			ps.setDouble(6, g.pointRadius.extent);
			ps.setDouble(7, g.pointRadius.x);
			ps.setDouble(8, g.pointRadius.y);

			System.out.println(ps.toString());
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	/*
	 * Produces a MULTIPOLYGON of the uncertainty radius
	 * 
	 */
	private String makeFootprintEwkt(Georef g) {
		return "GeomFromEWKT('SRID=4326;MULTIPOLYGON(((" + makeRadiusEwkt(g)
		+ ")))')";
	}

	/*
	 * Produces a POLYGON for use in the Geom field
	 * 
	 */
	private String makeGeomEwkt(Georef g) {
		return "GeomFromEWKT('SRID=4326;POLYGON((" + makeRadiusEwkt(g) + "))')";
	}

	/*
	 * Creates an uncertainty radius in Ewkt string format
	 * 
	 */
	private String makeRadiusEwkt(Georef g) {
		String result = "";
		double x = g.pointRadius.x;
		double y = g.pointRadius.y;
		double e = g.pointRadius.extent/Math.cos(Math.PI/g.pointRadiusNodes); // extend the extent so that the geometric is a circumscription of the pointradius.
		double ix=0, iy=0, a, cx, cy;
		for(int i=0;i<g.pointRadiusNodes;i++){
			a = i*2*Math.PI/g.pointRadiusNodes;
			cx = x+e*Math.cos(a)/g.pointRadius.getLngMetersPerDegree();
			cy = y+e*Math.sin(a)/g.pointRadius.getLatMetersPerDegree();
			if(i==0){
				ix=cx; iy=cy;
				result = result + cx + " " + cy;
			} else {
				result = result + ", " + cx + " " + cy;
			}
		}
		result = result + ", " + ix + " " + iy;
		return result;
	}
	public void removeUserFeature(int featureid) throws SQLException {
		PreparedStatement ps;
		String q = null;
		if(featureid==-1){
			// Use -1 to clear the whole user database
			try {
				q = new String("DELETE FROM i_classification");
				ps = userplaces.prepareStatement(q);
				System.out.println(ps.toString());
				ps.execute();
				ps.close();

				q = new String("DELETE FROM i_feature_footprint");
				ps = userplaces.prepareStatement(q);
				System.out.println(ps.toString());
				ps.execute();
				ps.close();

				q = new String("DELETE FROM g_feature_name");
				ps = userplaces.prepareStatement(q);
				System.out.println(ps.toString());
				ps.execute();
				ps.close();

				q = new String("DELETE FROM g_feature_displayname");
				ps = userplaces.prepareStatement(q);
				System.out.println(ps.toString());
				ps.execute();
				ps.close();

				q = new String("DELETE FROM g_feature");
				ps = userplaces.prepareStatement(q);
				System.out.println(ps.toString());
				ps.execute();
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw e;
			}
		}
		else{
			// clear all of the tables of the record with the feature_id provided.
			try {
				q = new String("DELETE FROM i_classification WHERE feature_id="+featureid);
				ps = userplaces.prepareStatement(q);
				System.out.println(ps.toString());
				ps.execute();
				ps.close();

				q = new String("DELETE FROM i_feature_footprint WHERE feature_id="+featureid);
				ps = userplaces.prepareStatement(q);
				System.out.println(ps.toString());
				ps.execute();
				ps.close();

				q = new String("DELETE FROM g_feature_name WHERE feature_id="+featureid);
				ps = userplaces.prepareStatement(q);
				System.out.println(ps.toString());
				ps.execute();
				ps.close();

				q = new String("DELETE FROM g_feature_displayname WHERE feature_id="+featureid);
				ps = userplaces.prepareStatement(q);
				System.out.println(ps.toString());
				ps.execute();
				ps.close();

				q = new String("DELETE FROM g_feature WHERE feature_id="+featureid);
				ps = userplaces.prepareStatement(q);
				System.out.println(ps.toString());
				ps.execute();
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw e;
			}
		}
	}

	private void setIFeatureFootprintRadii(Connection gdb, int featureid) throws SQLException {
		if(gdb==null) return;
		GeometryFactory gf = new GeometryFactory();
		WKTReader wktreader = new WKTReader(gf);	
		Geometry geom = null;
		String encodedG = null;
		PreparedStatement ps = null;
		String q = null;
		PointRadius pr = null;
		double radius;
		try {
			if(featureid==-1){
				// Use -1 to set radii for the whole database
				int min_fid=0, max_fid=0;
				Statement st = gdb.createStatement();
				ResultSet rs = st.executeQuery("SELECT MAX(feature_id) FROM i_feature_footprint");
				rs.next();
				if (rs.getRow() == 0){
					rs.close();
					st.close();
					return;
				}
				else{
					max_fid = new Integer(rs.getString(1)).intValue();
				}
				if(max_fid<1){
					rs.close();
					st.close();
					return;
				}
				st = gdb.createStatement();
				rs = st.executeQuery("SELECT MIN(feature_id) FROM i_feature_footprint");
				rs.next();
				if (rs.getRow() == 0){
					rs.close();
					st.close();
					return;
				}
				else{
					min_fid = new Integer(rs.getString(1)).intValue();
					rs.close();
					st.close();
				}

//				for(int i=min_fid;i<=max_fid;i++){
					for(int i=832;i<=max_fid;i++){
					encodedG = lookupFootprint(gdb, i);
					if(encodedG!=null){
						try {
							geom = wktreader.read(encodedG);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						if(geom!=null){
							pr = getPointRadiusFromGeometry(geom);
							if(pr!=null){
								radius=pr.extent;
								q = new String("UPDATE i_feature_footprint SET radius="+radius+" WHERE feature_id="+i);
								ps = gdb.prepareStatement(q);
								System.out.println(ps.toString());
								ps.execute();
							}
							ps.close();
						}
					}
				}
			}
			else {
				// set the radius for the selected feature_id
				encodedG = lookupFootprint(gdb, featureid);
				try {
					geom = wktreader.read(encodedG);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if(geom!=null){
					pr = getPointRadiusFromGeometry(geom);
					if(pr!=null){
						radius=pr.extent;
						q = new String("UPDATE i_feature_footprint SET radius="+radius+"WHERE feature_id="+featureid);
						ps = gdb.prepareStatement(q);
						System.out.println(ps.toString());
						ps.execute();
						ps.close();
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}
	private PointRadius getPointRadiusFromGeometry(Geometry g){		
		if (g == null)
			return null;
		if (g.getEnvelope() == null)
			return null;
		GeometryFactory gf = new GeometryFactory(g.getPrecisionModel(), g.getSRID());
		Geometry ng = (Geometry) g.clone();
		Coordinate[] coordinates = ng.getCoordinates();
		int nc = coordinates.length;
		double maxx = -180.0;
		double minx = 180.0;
		double miny = 90.0;
		double maxy = -90.0;
		for (int i = 0; i < nc; i++) {
			if (coordinates[i].x > maxx)
				maxx = coordinates[i].x;
			if (coordinates[i].x < minx)
				minx = coordinates[i].x;
			if (coordinates[i].y > maxy)
				maxy = coordinates[i].y;
			if (coordinates[i].y < miny)
				miny = coordinates[i].y;
		}
		if (maxx > 90 && minx < -90) { // geometry crosses longitude = 180
			Georef.shiftLongitude(ng, 360.0);
		}
//		Geometry.getCentroid() returns a weighted mean centroid, not a geographic one.
//		Point p = ng.getCentroid();
		Coordinate c = new Coordinate((minx+maxx)/2, (miny+maxy)/2);
		Point p = gf.createPoint(c);
		if (p == null) {
			return null; // Can't make a point-radius if there is no centroid.
		}
		Point newp = (Point) p.clone();
		double mindist = 9E12;
		double maxdist = 0;
		if (newp.distance(ng) > 0) { 
			// The centroid is not in the original geometry
			for (int i = 0; i < nc; i++) {
				Point tp = gf.createPoint(coordinates[i]);
				double ndist = tp.distance(p);
				if (ndist < mindist) {
					mindist = ndist;
					// set newp to the Point in g nearest centroid of g.
					newp = (Point) tp.clone(); 
				}
			}
		}
		PointRadius p1 = new PointRadius(newp.getX(), newp.getY(), 1);
		for (int i = 0; i < nc; i++) {
			Point np = gf.createPoint(coordinates[i]);
			PointRadius p2 = new PointRadius(np.getX(), np.getY(), 1);
			double longdist = p1.getLngDistanceInMetersToCoordinate(p2);
			double latdist = p1.getLatDistanceInMetersToCoordinate(p2);
			double distanceToNode = Math.sqrt(Math.pow(longdist, 2)
					+ Math.pow(latdist, 2));
			if (distanceToNode > maxdist)
				maxdist = distanceToNode;
		}
		if (maxdist < 1) {
			// TODO: Make sure the database has an i_feature_footprint.radius value
			// >=1 for every feature
			// The value should be from the footprint if not a point
			// else it should be the best_guessuncert based on feature type
			// else it should be halfway to the nearest neighbor > 1000m away.
			// return null; // can't make a point-radius without a radius
		}
		PointRadius pr = null;
		if (maxx > 90 && minx < -90) { // geometry crosses longitude = 180
			pr = new PointRadius(newp.getX() - 360, newp.getY(), maxdist);
			if (pr.x == -180)
				pr.x = 180;
			if (pr.y == -0)
				pr.y = 0;
		} else {
			pr = new PointRadius(newp.getX(), newp.getY(), maxdist);
		}
		return pr;
	}
	private void selectIFeatureFootprint(Connection gdb, int featureid) throws SQLException {
		if(gdb==null) return;
		GeometryFactory gf = new GeometryFactory();
		WKTReader wktreader = new WKTReader(gf);	
		Geometry geom = null;
		String encodedG = null;
		PreparedStatement ps = null;
		String q = null;
		PointRadius pr = null;
		FeatureInfo fi=null;
		double radius;
		fi = iFeatureName.selectFeatureById(gdb, featureid);
//		lookupQuickAttributes(gdb,fi);
		encodedG = lookupFootprint(gdb, featureid);
		try {
			geom = wktreader.read(encodedG);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(geom!=null){
			pr = getPointRadiusFromGeometry(geom);
			if(pr!=null){
					System.out.println(pr.toString()+"\n"+fi.toString());
			}
		}
	}
}