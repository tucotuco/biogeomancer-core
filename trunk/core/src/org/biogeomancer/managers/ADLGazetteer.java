/* 
 * This file is part of BioGeomancer.
 *
 *  Biogeomancer is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  any later version.
 *
 *  Biogeomancer is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Biogeomancer; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.biogeomancer.managers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.biogeomancer.records.FeatureInfo;
import org.biogeomancer.records.FeatureInfoState;
import org.biogeomancer.records.Georef;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.biogeomancer.utils.*;
//import edu.colorado.sde.*;

/**
 * @author asteele
 *
 */
public class ADLGazetteer extends BGManager {
	private ADLGazetteer.IFeatureName iFeatureName;
	private static ADLGazetteer instance;
	private static final Logger log = Logger.getLogger(ADLGazetteer.class);
	private static Properties props = new Properties();

	private class IFeatureName {
		private final static String SEPARATORS = " ;";
		private final static String AND = "&";
		private final static String OR = "|";

		public ArrayList<FeatureInfo> selectDistinctFeaturesByName(Connection gdb, String feature, String querytype) {
			if (gdb == null || feature == null || querytype == null) return null;
			String searchname = new String(feature.replace("'", "\\'"));
			String query = "empty";
			int fid;
			ArrayList<FeatureInfo> features = new ArrayList<FeatureInfo>();
			FeatureInfo fi = null;
			Statement st = null;

			// Potential query types in order of most useful execution: 
			// (all assume case is ignored and diacritical equivalence)
			//  equals
			//  contains-phrase
			//  contains-all-words
			//  contains-any-words
			//  contains-potential-misspelling
			if (querytype.equalsIgnoreCase("equals-ignore-case")){
				// Make sure database is indexed on lower(name)
				query = 
					"SELECT DISTINCT " +
					" feature_id " +
					" FROM " +
					" g_feature_name " +
					" WHERE " +
					" lower(name) = '" + searchname.toLowerCase().trim() + 
					"';";
				try {
					st = gdb.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (querytype.equalsIgnoreCase("contains-phrase")){
				// Make sure database is indexed properly for contains phrase
				query = 
					"SELECT DISTINCT " +
					" feature_id " +
					" FROM " +
					" g_feature_name " +
					" WHERE " +
					" lower(name) LIKE'%" + searchname.toLowerCase().trim() + 
					"%';";
//				select feature_id,name from g_feature_name where lower(name) LIKE '%santa rosa%';
				log.info("Contains-phrase query required for feature name: "+searchname.toLowerCase().trim()+". "+query);
				try {
					st = gdb.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (querytype.equalsIgnoreCase("contains-all-words")){
				// Make sure database is indexed properly for contains words queries
				query = 
					"SELECT DISTINCT" +
					" feature_id " +
					" FROM " +
					" g_feature_name " +
					" WHERE " +
					" idxfti@@to_tsquery('default','" + searchname.toLowerCase().trim().replace(" ", "&") + 
					"');";
//				select feature_id,name,idxfti from g_feature_name where idxfti@@to_tsquery('default','santa&ROSA');
				log.info("Contains-all-words query required for feature name: "+searchname.toLowerCase().trim()+". "+query);
				try {
					st = gdb.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (querytype.equalsIgnoreCase("contains-any-words")){
				// Make sure database is indexed properly for contains words
				query = 
					"SELECT DISTINCT" +
					" feature_id " +
					" FROM " +
					" g_feature_name " +
					" WHERE " +
					" idxfti@@to_tsquery('default','" + searchname.toLowerCase().trim().replace(" ", "|") + 
					"');";
//				select feature_id,name,idxfti from g_feature_name where idxfti@@to_tsquery('default','santa|ROSA');
				log.info("Contains-any-worrds query required for feature name: "+searchname.toLowerCase().trim()+". "+query);
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
					fi.featureID=fid;
					features.add(fi);
				}
				rs.close();
				st.close();
				return features;
			} catch(SQLException e) {
				log.error(e.toString());
			}
			return null;
		}

		public ArrayList<FeatureInfo> selectQuickFeaturesByName(Connection gdb, String feature, String querytype) {
			if (gdb == null || feature == null || querytype == null) return null;
			String searchname = new String(feature.replace("'", "\\'"));
			double lat=90, lng=0, radius=-1;
			String displayname = null;
			String query = 
/*
				"SELECT" +
				" i_feature_footprint.feature_id," +
				" geom_y,"+
				" geom_x,"+
				" radius,"+
				" displayname"+
				" FROM" +
				" i_feature_footprint," +
				" g_feature," +
				" g_feature_displayname," +
				" g_feature_name" +
				" WHERE" +
				" i_feature_footprint.feature_id=g_feature_displayname.feature_id" +
				" AND i_feature_footprint.feature_id=g_feature.feature_id" +
				" AND i_feature_footprint.feature_id=g_feature_name.feature_id";
*/
				"SELECT" +
				" i_feature_footprint.feature_id," +
				" geom_y,"+
				" geom_x,"+
				" radius,"+
				" displayname,"+
				" g_collection.name," +
				" g_collection.coordprecision," +
				" g_collection.mapaccuracyinmeters," +
				" i_scheme_term.term," +
				" asText(footprint)" +
				" FROM" +
				" i_feature_footprint," +
				" g_feature," +
				" g_feature_displayname," +
				" g_feature_name," +
				" i_scheme_term," +
				" i_classification," +
				" g_collection" +
				" WHERE" +
				" i_feature_footprint.feature_id=g_feature_displayname.feature_id" +
				" AND i_feature_footprint.feature_id=g_feature.feature_id" +
				" AND i_feature_footprint.feature_id=i_classification.feature_id" +
				" AND i_classification.classification_term_id=i_scheme_term.scheme_term_id" +
				" AND g_feature.collection_id=g_collection.collection_id" +
				" AND i_feature_footprint.feature_id=g_feature_name.feature_id";

//			int fid;
			ArrayList<FeatureInfo> features = new ArrayList<FeatureInfo>();
			FeatureInfo fi = null;
			Statement st = null;

			// Potential query types in order of most useful execution: 
			// (all assume case is ignored and diacritical equivalence)
			//  equals
			//  contains-phrase
			//  contains-all-words
			//  contains-potential-misspelling
			if (querytype.equalsIgnoreCase("equals-ignore-case")){
				// Make sure database is indexed on lower(name)
				query = query.concat(" AND lower(g_feature_name.name) = '" + searchname.toLowerCase().trim() + "';");
//				select feature_id, name from g_feature_name where lower(name) ='santa rosa';
				try {
					st = gdb.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println(query);
				}
			} else if (querytype.equalsIgnoreCase("contains-phrase") /*&& searchname.length() > 3*/){
				// Make sure database is indexed properly for contains phrase
				query = query.concat(" AND lower(g_feature_name.name) LIKE'%" + searchname.toLowerCase().trim() + "%';");
//				select feature_id, name from g_feature_name where lower(name) LIKE '%santa rosa%';
				log.info("Contains-phrase query required for feature name: "+searchname.toLowerCase().trim()+". "+query);
				try {
					st = gdb.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println(query);
				}
			} else if (querytype.equalsIgnoreCase("contains-all-words")){
				// Make sure database is indexed properly for contains words queries
				query = query.concat(" AND idxfti@@to_tsquery('default','" + searchname.toLowerCase().trim().replace(" ", "&") + "');");
//				select feature_id, name, idxfti from g_feature_name where idxfti@@to_tsquery('default','santa&ROSA');
				log.info("Contains-all-words query required for feature name: "+searchname.toLowerCase().trim()+". "+query);
				try {
					st = gdb.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println(query);
				}
			} else if (querytype.equalsIgnoreCase("contains-any-words")){
				// Make sure database is indexed properly for contains words
				query = query.concat(" AND idxfti@@to_tsquery('default','" + searchname.toLowerCase().trim().replace(" ", "|") + "');");
//				select feature_id, name, idxfti from g_feature_name where idxfti@@to_tsquery('default','santa|ROSA');
				log.info("Contains-any-words query required for feature name: "+searchname.toLowerCase().trim()+". "+query);
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
					fi.encodedGeometry = new String(rs.getString(10));
					fi.geodeticDatum = DatumManager.getInstance().getDatum("WGS84");
					features.add(fi);
				}
				rs.close();
				st.close();
				return features;
			} catch(SQLException e) {
				log.error(e.toString() + "\n"+query);
			}
			return null;
		}

		public FeatureInfo selectFeatureById(Connection gdb, int featureid) {
			FeatureInfo fi = null;
			Statement st = null;
			String query = 
				"SELECT" +
				" i_feature_footprint.feature_id," +
				" geom_y,"+
				" geom_x,"+
				" radius,"+
				" displayname,"+
				" asText(footprint)," +
				" i_scheme_term.term," +
				" g_collection.name," +
				" g_collection.mapaccuracyinmeters," +
				" g_collection.coordprecision" +
				" FROM" +
				" i_feature_footprint," +
				" g_feature," +
				" g_feature_displayname," +
				" i_scheme_term," +
				" i_classification," +
				" g_collection" +
				" WHERE" +
				" i_feature_footprint.feature_id=g_feature_displayname.feature_id" +
				" AND i_feature_footprint.feature_id=g_feature.feature_id" +
				" AND i_feature_footprint.feature_id=i_classification.feature_id" +
				" AND i_classification.classification_term_id=i_scheme_term.scheme_term_id" +
				" AND g_feature.collection_id=g_collection.collection_id" +
				" AND g_feature.feature_id = " + featureid;
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
					fi.encodedGeometry = new String(rs.getString(6));
					fi.classificationTerm = new String(rs.getString(7));
					fi.coordSource = new String(rs.getString(8));
					fi.mapAccuracyInMeters = rs.getDouble(9);
					fi.coordPrecision = rs.getDouble(10);
				}
				rs.close();
				st.close();
				return fi;
			} catch(SQLException e) {
				log.error(e.toString() + "\n"+query);
			}
			return null;
		}

		public boolean addUserFeaturesByName(ArrayList<FeatureInfo> features, String feature, String querytype) {
			if (feature == null || querytype == null) return false;
			String searchname = new String(feature.replace("'", "\\'"));
			FeatureInfo fi = null;
			Statement st = null;
			boolean featureadded = false;
			
			String query = 
			"SELECT" +
			" i_feature_footprint.feature_id," +
			" geom_y,"+
			" geom_x,"+
			" radius,"+
			" displayname,"+
			" asText(footprint)," +
			" i_scheme_term.term," +
			" g_collection.name," +
			" g_collection.mapaccuracyinmeters," +
			" g_collection.coordprecision" +
			" FROM" +
			" i_feature_footprint," +
			" g_feature," +
			" g_feature_displayname," +
			" i_scheme_term," +
			" i_classification," +
			" g_collection" +
			" WHERE" +
			" i_feature_footprint.feature_id=g_feature_displayname.feature_id" +
			" AND i_feature_footprint.feature_id=g_feature.feature_id" +
			" AND i_feature_footprint.feature_id=i_classification.feature_id" +
			" AND i_classification.classification_term_id=i_scheme_term.scheme_term_id" +
			" AND g_feature.collection_id=g_collection.collection_id";
//			" AND g_feature.feature_id = " + featureid;


			if (querytype.equalsIgnoreCase("equals-ignore-case")){
				// Make sure database is indexed on lower(name)
				query = query.concat(" AND lower(displayname) = '" + searchname.toLowerCase().trim() + "';");
				try {
					st = userplaces.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (querytype.equalsIgnoreCase("contains-phrase")){
				// Make sure database is indexed properly for contains phrase
				query = query.concat(" AND lower(displayname) LIKE'%" + searchname.toLowerCase().trim() + "%';");
				log.info("Contains-phrase query required for feature name: "+searchname.toLowerCase().trim()+". "+query);
				try {
					st = userplaces.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (querytype.equalsIgnoreCase("contains-all-words")){
				// Make sure database is indexed properly for contains words queries
				query = query.concat(" AND idxfti@@to_tsquery('default','" + searchname.toLowerCase().trim().replace(" ", "&") + "');");
//				query = 
//					"SELECT " +
//					" feature_id " +
//					" FROM " +
//					" g_feature_name " +
//					" WHERE " +
//					" idxfti@@to_tsquery('default','" + searchname.toLowerCase().trim().replace(" ", "&") +
//					"');";
//				select feature_id,name,idxfti from g_feature_name where idxfti@@to_tsquery('default','santa&ROSA');
				log.info("Contains-all-words query required for feature name: "+searchname.toLowerCase().trim()+". "+query);
				try {
					st = userplaces.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (querytype.equalsIgnoreCase("contains-any-words")){
				// Make sure database is indexed properly for contains words
				query = query.concat(" AND idxfti@@to_tsquery('default','" + searchname.toLowerCase().trim().replace(" ", "|") + "');");
//				query = 
//					"SELECT " +
//					" feature_id " +
//					" FROM " +
//					" g_feature_name " +
//					" WHERE " +
//					" idxfti@@to_tsquery('default','" + searchname.toLowerCase().trim().replace(" ", "|") + 
//					"');";
//				select feature_id,name,idxfti from g_feature_name where idxfti@@to_tsquery('default','santa|ROSA');
				log.info("Contains-any-worrds query required for feature name: "+searchname.toLowerCase().trim()+". "+query);
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
					fi.encodedGeometry = new String(rs.getString(6));
					fi.classificationTerm = new String(rs.getString(7));
					fi.coordSource = new String(rs.getString(8));
					fi.mapAccuracyInMeters = rs.getDouble(9);
					fi.coordPrecision = rs.getDouble(10);
					features.add(fi);
					featureadded=true;
				}
				rs.close();
				st.close();
			} catch(SQLException e) {
				log.error(e.toString());
			}
			return featureadded;
		}

		public boolean addFeaturesByName(Connection gdb, ArrayList<FeatureInfo> features, String feature, String querytype) {
			if (gdb==null || features==null || feature == null || querytype == null) return false;
			String searchname = new String(feature.replace("'", "\\'"));
			FeatureInfo fi = null;
			Statement st = null;
			boolean featureadded = false;
			
			String query = 
			"SELECT" +
			" i_feature_footprint.feature_id," +
			" geom_y,"+
			" geom_x,"+
			" radius,"+
			" displayname,"+
			" g_feature_name.name,"+
			" asText(footprint)," +
			" i_scheme_term.term," +
			" g_collection.name," +
			" g_collection.mapaccuracyinmeters," +
			" g_collection.coordprecision" +
			" FROM" +
			" i_feature_footprint," +
			" g_feature," +
			" g_feature_name," +
			" g_feature_displayname," +
			" i_scheme_term," +
			" i_classification," +
			" g_collection" +
			" WHERE" +
			" i_feature_footprint.feature_id=g_feature_displayname.feature_id" +
			" AND i_feature_footprint.feature_id=g_feature.feature_id" +
			" AND i_feature_footprint.feature_id=g_feature_name.feature_id" +
			" AND i_feature_footprint.feature_id=i_classification.feature_id" +
			" AND i_classification.classification_term_id=i_scheme_term.scheme_term_id" +
			" AND g_feature.collection_id=g_collection.collection_id";

			if (querytype.equalsIgnoreCase("equals-ignore-case")){
				// Make sure database is indexed on lower(name)
				query = query.concat(" AND lower(g_feature_name.name) = '" + searchname.toLowerCase().trim() + "';");
				try {
					st = gdb.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (querytype.equalsIgnoreCase("contains-phrase")){
				// Make sure database is indexed properly for contains phrase
				query = query.concat(" AND lower(g_feature_name.name) LIKE'%" + searchname.toLowerCase().trim() + "%';");
				log.info("Contains-phrase query required for feature name: "+searchname.toLowerCase().trim()+". "+query);
				try {
					st = gdb.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (querytype.equalsIgnoreCase("contains-all-words")){
				// Make sure database is indexed properly for contains words queries
				query = query.concat(" AND idxfti@@to_tsquery('default','" + searchname.toLowerCase().trim().replace(" ", "&") + "');");
//				query = 
//					"SELECT " +
//					" feature_id " +
//					" FROM " +
//					" g_feature_name " +
//					" WHERE " +
//					" idxfti@@to_tsquery('default','" + searchname.toLowerCase().trim().replace(" ", "&") +
//					"');";
//				select feature_id,name,idxfti from g_feature_name where idxfti@@to_tsquery('default','santa&ROSA');
				log.info("Contains-all-words query required for feature name: "+searchname.toLowerCase().trim()+". "+query);
				try {
					st = gdb.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (querytype.equalsIgnoreCase("contains-any-words")){
				// Make sure database is indexed properly for contains words
				query = query.concat(" AND idxfti@@to_tsquery('default','" + searchname.toLowerCase().trim().replace(" ", "|") + "');");
//				query = 
//					"SELECT " +
//					" feature_id " +
//					" FROM " +
//					" g_feature_name " +
//					" WHERE " +
//					" idxfti@@to_tsquery('default','" + searchname.toLowerCase().trim().replace(" ", "|") + 
//					"');";
//				select feature_id,name,idxfti from g_feature_name where idxfti@@to_tsquery('default','santa|ROSA');
				log.info("Contains-any-words query required for feature name: "+searchname.toLowerCase().trim()+". "+query);
				try {
					st = gdb.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			try {
				ResultSet rs = st.executeQuery(query);
				String namefound=null;

				while (rs.next()) {
					fi = new FeatureInfo();
					fi.featureID = new Integer(rs.getString(1)).intValue();
					fi.latitude = rs.getDouble(2);
					fi.longitude = rs.getDouble(3);
					fi.extentInMeters = rs.getDouble(4);
					fi.geodeticDatum = DatumManager.getInstance().getDatum("WGS84");
					fi.name = new String(rs.getString(5));
					namefound = new String(rs.getString(6));
					if(namefound.equalsIgnoreCase(fi.name)==false){
						fi.name = new String(namefound+" (="+rs.getString(5)+")");
					}
					fi.encodedGeometry = new String(rs.getString(7));
					fi.classificationTerm = new String(rs.getString(8));
					fi.coordSource = new String(rs.getString(9));
					fi.mapAccuracyInMeters = rs.getDouble(10);
					fi.coordPrecision = rs.getDouble(11);
					features.add(fi);
					featureadded = true;
				}
				rs.close();
				st.close();
			} catch(SQLException e) {
				log.error(e.toString());
			}
			return featureadded;
		}

		public ArrayList<FeatureInfo> selectFeaturesByName(Connection gdb, String feature, String querytype) {
			if (gdb == null || feature == null || querytype == null) return null;
			String searchname = new String(feature.replace("'", "\\'"));
			String query = "empty";
			int fid;
			ArrayList<FeatureInfo> features = new ArrayList<FeatureInfo>();
			FeatureInfo fi = null;
			Statement st = null;

			// Potential query types in order of most useful execution: 
			// (all assume case is ignored and diacritical equivalence)
			//  equals
			//  contains-phrase
			//  contains-all-words
			//  contains-potential-misspelling
			if (querytype.equalsIgnoreCase("equals-ignore-case")){
				// Make sure database is indexed on lower(name)
				query = 
					"SELECT " +
					" feature_id " +
					" FROM " +
					" g_feature_name " +
					" WHERE " +
					" lower(name) = '" + searchname.toLowerCase().trim() + 
					"';";
				try {
					st = gdb.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (querytype.equalsIgnoreCase("contains-phrase")){
				// Make sure database is indexed properly for contains phrase
				query = 
					"SELECT " +
					" feature_id " +
					" FROM " +
					" g_feature_name " +
					" WHERE " +
					" lower(name) LIKE'%" + searchname.toLowerCase().trim() + 
					"%';";
//				select feature_id,name from g_feature_name where lower(name) LIKE '%santa rosa%';
				log.info("Contains-phrase query required for feature name: "+searchname.toLowerCase().trim()+". "+query);
				try {
					st = gdb.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (querytype.equalsIgnoreCase("contains-all-words")){
				// Make sure database is indexed properly for contains words queries
				query = 
					"SELECT " +
					" feature_id " +
					" FROM " +
					" g_feature_name " +
					" WHERE " +
					" idxfti@@to_tsquery('default','" + searchname.toLowerCase().trim().replace(" ", "&") + 
					"');";
//				select feature_id,name,idxfti from g_feature_name where idxfti@@to_tsquery('default','santa&ROSA');
				log.info("Contains-all-words query required for feature name: "+searchname.toLowerCase().trim()+". "+query);
				try {
					st = gdb.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (querytype.equalsIgnoreCase("contains-any-words")){
				// Make sure database is indexed properly for contains words
				query = 
					"SELECT " +
					" feature_id " +
					" FROM " +
					" g_feature_name " +
					" WHERE " +
					" idxfti@@to_tsquery('default','" + searchname.toLowerCase().trim().replace(" ", "|") + 
					"');";
//				select feature_id,name,idxfti from g_feature_name where idxfti@@to_tsquery('default','santa|ROSA');
				log.info("Contains-any-worrds query required for feature name: "+searchname.toLowerCase().trim()+". "+query);
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
					fi.featureID=fid;
					features.add(fi);
				}
				rs.close();
				st.close();
				return features;
			} catch(SQLException e) {
				log.error(e.toString());
			}
			return null;
		}

		// Tokenizes feature string to be proper form for fulltext query
		public String ftiQueryString(String line,String argument) {
			String result="";
			StringTokenizer st = new StringTokenizer(line, SEPARATORS);
			while (st.hasMoreTokens()) {
				result+=st.nextToken();
				if (st.hasMoreTokens()) result+=argument;
			}
			return result;
		}
	}

	public static void main(String[] argv){
		// Potential query types in order of most useful execution: 
		// (all assume case is ignored and diacritical equivalence)
		//  equals
		//  contains-phrase
		//  contains-all-words
		//  contains-any-words
		//  contains-potential-misspelling
		if(argv.length < 3){
			System.out.println("arguments are featurename querytype datasource");
			return;
		}

		String feature = new String(argv[0]);
		String querytype = new String(argv[1]);
//		String querytype = new String("equals-ignore-case");
		String logtype = new String("system");
		ADLGazetteer adl = null;

		try {
			adl = new ADLGazetteer();
			Connection con = worldplaces;
			if(argv[2].equalsIgnoreCase("userplaces")){
				con=userplaces;
				FeatureInfo f = adl.featureLookup(con, 69);
				System.out.println(f);
			} else if(argv[2].equalsIgnoreCase("gadm")){
				con=gadm;
			} else if(argv[2].equalsIgnoreCase("plss")){
				con=plss;
			} else if(argv[2].equalsIgnoreCase("gn")){
				con=gn;
			} else if(argv[2].equalsIgnoreCase("conustigerplaces")){
				con=conustigerplaces;
			} else if(argv[2].equalsIgnoreCase("gnispopulatedplaces")){
				con=gnispopulatedplaces;
			} 
			ArrayList<FeatureInfo> results = adl.featureLookup(con, feature, querytype, logtype);
			adl.addFeatures(userplaces, results, feature, querytype);
			for (FeatureInfo fi : results) {
				System.out.println(fi);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			adl.shutdown();
		}
	}

	public static ADLGazetteer getInstance() throws Exception {
		if (instance == null)
			instance = new ADLGazetteer();
		return instance;
	}
	private ADLGazetteer() throws SQLException {
		initProps("ADLGazetteer.properties", props);

		try{
			gadm=gazdbconnect(props.getProperty("adl.gadm.driver"), 
					props.getProperty("adl.gadm.server"),
					props.getProperty("adl.gadm.host"),
					props.getProperty("adl.gadm.name"),
					props.getProperty("adl.gadm.user"),
					props.getProperty("adl.gadm.pass"));

			gn=gazdbconnect(props.getProperty("adl.gn.driver"), 
					props.getProperty("adl.gn.server"),
					props.getProperty("adl.gn.host"),
					props.getProperty("adl.gn.name"),
					props.getProperty("adl.gn.user"),
					props.getProperty("adl.gn.pass"));

			plss=gazdbconnect(props.getProperty("adl.plss.driver"), 
					props.getProperty("adl.plss.server"),
					props.getProperty("adl.plss.host"),
					props.getProperty("adl.plss.name"),
					props.getProperty("adl.plss.user"),
					props.getProperty("adl.plss.pass"));

			conustigerplaces=gazdbconnect(props.getProperty("adl.conus.driver"), 
					props.getProperty("adl.conus.server"),
					props.getProperty("adl.conus.host"),
					props.getProperty("adl.conus.name"),
					props.getProperty("adl.conus.user"),
					props.getProperty("adl.conus.pass"));

			gnispopulatedplaces=gazdbconnect(props.getProperty("adl.gnis.driver"), 
					props.getProperty("adl.gnis.server"),
					props.getProperty("adl.gnis.host"),
					props.getProperty("adl.gnis.name"),
					props.getProperty("adl.gnis.user"),
					props.getProperty("adl.gnis.pass"));

			worldplaces=gazdbconnect(props.getProperty("adl.worldplaces.driver"), 
					props.getProperty("adl.worldplaces.server"),
					props.getProperty("adl.worldplaces.host"),
					props.getProperty("adl.worldplaces.name"),
					props.getProperty("adl.worldplaces.user"),
					props.getProperty("adl.worldplaces.pass"));

			userplaces=gazdbconnect(props.getProperty("adl.userplaces.driver"), 
					props.getProperty("adl.userplaces.server"),
					props.getProperty("adl.userplaces.host"),
					props.getProperty("adl.userplaces.name"),
					props.getProperty("adl.userplaces.user"),
					props.getProperty("adl.userplaces.pass"));

			iFeatureName = this.new IFeatureName();
		}finally{ // closing here doesn't work, because this gets executed when the try clause finishes, no matter what
			/*
			if( gadm!=null ) gadm.close();
			if( gn!=null ) gn.close();
			if( plss!=null ) plss.close();
			if( conustigerplaces!=null ) conustigerplaces.close();
			if( gnispopulatedplaces!=null ) gnispopulatedplaces.close();
			if( worldplaces!=null ) worldplaces.close();
			 */			
		}
	}
	public void shutdown(){
		try {
			if( gadm!=null ) gadm.close();
			if( gn!=null ) gn.close();
			if( plss!=null ) plss.close();
			if( conustigerplaces!=null ) conustigerplaces.close();
			if( gnispopulatedplaces!=null ) gnispopulatedplaces.close();
			if( worldplaces!=null ) worldplaces.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
	public ArrayList<FeatureInfo> featureLookup(Connection gdb, String feature, String querytype, String logtype) {
		if(gdb == null || feature == null || querytype == null || feature.length()==0 || querytype.length()==0) return null;
		ArrayList<FeatureInfo> fis = null;
		if(logtype==null){
//			fis = iFeatureName.selectFeaturesByName(gdb, feature, querytype);
			fis = iFeatureName.selectDistinctFeaturesByName(gdb, feature, querytype);
			lookupFeatureDetails(gdb, fis);
			return fis;
		}
		if(logtype.equals("log")){
			long starttime = System.currentTimeMillis();
//			fis = iFeatureName.selectFeaturesByName(gdb, feature, querytype);
			fis = iFeatureName.selectDistinctFeaturesByName(gdb, feature, querytype);
			lookupFeatureDetails(gdb, fis);
			long endtime = System.currentTimeMillis();
			if (fis==null || fis.isEmpty()){
				log.info("Feature:\t"+feature+";\tQuery type: "+querytype+";\tCount: 0;\tElapsed Time: "+(endtime-starttime)+"(ms)");
			}else{
				log.info("Feature:\t"+feature+";\tQuery type: "+querytype+";\tCount: "+fis.size()+";\tElapsed Time: "+(endtime-starttime)+"(ms)");
			}
		}else if(logtype.equals("system")){
			long starttime = System.currentTimeMillis();
//			fis = iFeatureName.selectFeaturesByName(gdb, feature, querytype);
			fis = iFeatureName.selectDistinctFeaturesByName(gdb, feature, querytype);
			long endtime = System.currentTimeMillis();
			if (fis==null || fis.isEmpty()){
				System.out.println("Feature:\t"+feature+";\tQuery type: "+querytype+";\tCount: 0;\tLookup Time: "+(endtime-starttime)+"(ms)");
			}else{
				System.out.println("Feature:\t"+feature+";\tQuery type: "+querytype+";\tCount: "+fis.size()+";\tLookup Time: "+(endtime-starttime)+"(ms)");
			}
			starttime = System.currentTimeMillis();
			lookupFeatureDetails(gdb, fis);
			endtime = System.currentTimeMillis();
			if (fis==null || fis.isEmpty()){
				System.out.println("Feature:\t"+feature+";\tQuery type: "+querytype+";\tCount: 0;\tDetail Lookup Time: "+(endtime-starttime)+"(ms)");
			}else{
				System.out.println("Feature:\t"+feature+";\tQuery type: "+querytype+";\tCount: "+fis.size()+";\tDetail Lookup Time: "+(endtime-starttime)+"(ms)");
			}
		}else{
//			fis = iFeatureName.selectFeaturesByName(gdb, feature, querytype);
			fis = iFeatureName.selectDistinctFeaturesByName(gdb, feature, querytype);
			lookupFeatureDetails(gdb, fis);
		}
		return fis;
	}

	public boolean addFeatures(Connection gdb, ArrayList<FeatureInfo> featurelist, String featurename, String querytype){
		if( gdb==null || featurelist==null) return false;
		if( featurename == null || featurename.length()==0 ) return false;
		boolean featureadded = iFeatureName.addFeaturesByName(gdb, featurelist, featurename, querytype);
		return featureadded;
	}
	
	public boolean addFeatures(Connection gdb, ArrayList<FeatureInfo> featurelist, String featurename){
		if( gdb==null || featurelist==null) return false;
		if( featurename == null || featurename.length()==0 ) return false;
		boolean featureadded = iFeatureName.addFeaturesByName(gdb, featurelist, featurename, "equals-ignore-case");
		if(featurelist.isEmpty()){
			featureadded = iFeatureName.addFeaturesByName(gdb, featurelist, featurename, "contains-phrase");
		}
		if(featurelist.isEmpty()){
			featureadded = iFeatureName.addFeaturesByName(gdb, featurelist, featurename, "contains-all-words");
		}
		return featureadded;
	}
	
	public ArrayList<FeatureInfo> featureQuickLookup(Connection gdb, String feature, String querytype, String logtype) {
		if(gdb == null || feature == null || querytype == null || feature.length()==0 || querytype.length()==0) return null;
		ArrayList<FeatureInfo> fis = null;
		if(logtype==null){
			fis = iFeatureName.selectQuickFeaturesByName(gdb, feature, querytype);
//			lookupQuickFeatureDetails(gdb, fis);
		}else if(logtype.equals("log")){
			long starttime = System.currentTimeMillis();
			fis = iFeatureName.selectQuickFeaturesByName(gdb, feature, querytype);
//			lookupQuickFeatureDetails(gdb, fis);
			long endtime = System.currentTimeMillis();
			if (fis==null || fis.isEmpty()){
				log.info("Feature:\t"+feature+";\tQuery type: "+querytype+";\tCount: 0;\tElapsed Time: "+(endtime-starttime)+"(ms)");
			}else{
				log.info("Feature:\t"+feature+";\tQuery type: "+querytype+";\tCount: "+fis.size()+";\tElapsed Time: "+(endtime-starttime)+"(ms)");
			}
		}else if(logtype.equals("system")){
			long starttime = System.currentTimeMillis();
//			fis = iFeatureName.selectFeaturesByName(gdb, feature, querytype);
//			fis = iFeatureName.selectDistinctFeaturesByName(gdb, feature, querytype);
			fis = iFeatureName.selectQuickFeaturesByName(gdb, feature, querytype);
			long endtime = System.currentTimeMillis();
			if (fis==null || fis.isEmpty()){
				System.out.println("Feature:\t"+feature+";\tQuery type: "+querytype+";\tCount: 0;\tLookup Time: "+(endtime-starttime)+"(ms)");
			}else{
				System.out.println("Feature:\t"+feature+";\tQuery type: "+querytype+";\tCount: "+fis.size()+";\tLookup Time: "+(endtime-starttime)+"(ms)");
			}
//			starttime = System.currentTimeMillis();
//			lookupQuickFeatureDetails(gdb, fis);
//			endtime = System.currentTimeMillis();
//			if (fis==null || fis.isEmpty()){
//			System.out.println("Feature:\t"+feature+";\tQuery type: "+querytype+";\tCount: 0;\tDetail Lookup Time: "+(endtime-starttime)+"(ms)");
//			}else{
//			System.out.println("Feature:\t"+feature+";\tQuery type: "+querytype+";\tCount: "+fis.size()+";\tDetail Lookup Time: "+(endtime-starttime)+"(ms)");
//			}
		}else{
//			fis = iFeatureName.selectDistinctFeaturesByName(gdb, feature, querytype);
//			lookupQuickFeatureDetails(gdb, fis);
		}
		// Remove duplicate features.
		for(int i=0;i<fis.size();i++){
			for(int j=0;j<fis.size();j++){
				if(i!=j){
					if(fis.get(i).featureID==fis.get(j).featureID){
						// Two features may have the same id if they came from different
						// databases (e.g., userplaces and worldplaces), so check if the names
						// are also the same.
						if(fis.get(i).name.equalsIgnoreCase(fis.get(j).name)){
							fis.remove(j);
							j--;
						}
					}
				}
			}
		}
		return fis;
	}

	public void lookupFeatureDetails(Connection gdb, ArrayList<FeatureInfo> fis){
//		if( gdb == null || fis == null || fis.isEmpty()) return;
		// Get the rest of the information for the featureinfos.
		GeometryFactory gf = new GeometryFactory();
		WKTReader wktreader = null;
		Geometry g = null;
		Georef georef = null;
		PointRadius pr = null;
		FeatureInfo fi = null;
		for(int i=0;i<fis.size();i++){
			fi=fis.get(i);
			try {
				// Get the extent from footprint if it exists.
				lookupFootprintAttributes(gdb, fi);
				if(fi.encodedGeometry==null || fi.encodedGeometry.toLowerCase().contains("empty")){
					// Otherwise get it from geom, which is a convex hull.
					log.info("Convex hull lookup required for feature_id: "+fi.featureID);
					fi.encodedGeometry=lookupConvexHull(gdb, fi.featureID);
					fi.geodeticDatum=DatumManager.getInstance().getDatum("WGS84");
				}
				if( fi.encodedGeometry!=null){
					wktreader = new WKTReader(gf);
//					System.out.println(fi.featureID+":"+fi.encodedGeometry);
					g = wktreader.read(fi.encodedGeometry);
					georef = new Georef(g, fi.geodeticDatum);
					pr = georef.makePointRadius(g, fi.geodeticDatum);
					if(pr!=null){
						fi.latitude=pr.y;
						fi.longitude=pr.x;
						fi.extentInMeters=pr.extent;
						if(fi.extentInMeters<1){ 
							// The PointRadius representation of the geometry has a miniscule radius.
							fi.extentInMeters=lookupBestGuessUncertainty(gdb, fi.featureID);
						}
						lookupFeatureTypeAttributes(gdb, fi);
						lookupCollectionAttributes(gdb, fi);
						fi.name=lookupDisplayName(gdb, fi.featureID);
						fi.state=FeatureInfoState.FEATUREINFO_COMPLETED;
					}else{
						fis.remove(fi);
						i--;
					}
				}else{
					fis.remove(fi);
					i--;
				}
			} catch (ParseException e) {
				fi.state=FeatureInfoState.FEATUREINFO_CREATION_ERROR;
				e.printStackTrace();
			}
		}
	}

	public void lookupQuickFeatureDetails(Connection gdb, ArrayList<FeatureInfo> fis){
//		if( gdb == null || fis == null || fis.isEmpty()) return;
		// Get the rest of the information for the featureinfos.
		GeometryFactory gf = new GeometryFactory();
		WKTReader wktreader = null;
		Geometry g = null;
		Georef georef = null;
		PointRadius pr = null;
		FeatureInfo fi = null;
		for(int i=0;i<fis.size();i++){
			fi=fis.get(i);
//			try {
			lookupQuickAttributes(gdb, fi);
			pr = new PointRadius(fi.longitude, fi.latitude, DatumManager.getInstance().getDatum("WGS84"), fi.extentInMeters);
			/*
				if(fi.encodedGeometry==null || fi.encodedGeometry.toLowerCase().contains("empty")){
					// Get the extent from convex hull if it exists.
					fi.encodedGeometry=lookupConvexHull(gdb, fi.featureID);
					fi.geodeticDatum=DatumManager.getInstance().getDatum("WGS84");
				}
				if( fi.encodedGeometry!=null){
					wktreader = new WKTReader(gf);
//					System.out.println(fi.featureID+":"+fi.encodedGeometry);
					g = wktreader.read(fi.encodedGeometry);
					georef = new Georef(g, fi.geodeticDatum);
					pr = georef.makePointRadius(g, fi.geodeticDatum);
					if(pr!=null){
						fi.latitude=pr.y;
						fi.longitude=pr.x;
						fi.extentInMeters=pr.extent;
						if(fi.extentInMeters<1){ 
							// The PointRadius representation of the geometry has a miniscule radius.
							fi.extentInMeters=lookupBestGuessUncertainty(gdb, fi.featureID);
						}
						lookupFeatureTypeAttributes(gdb, fi);
						lookupCollectionAttributes(gdb, fi);
						fi.state=FeatureInfoState.FEATUREINFO_COMPLETED;
					}else{
						fis.remove(fi);
						i--;
					}
				}else{
					fis.remove(fi);
					i--;
				}
				} catch (ParseException e) {
				fi.state=FeatureInfoState.FEATUREINFO_CREATION_ERROR;
				e.printStackTrace();
			}
			 */		}
	}

	public String lookupDisplayName(Connection gdb, int featureID) {
		String s = null;
		String query =
			"SELECT displayname" +
			" FROM g_feature_displayname" +
			" WHERE feature_id="+featureID+";";
		try {
			Statement st = gdb.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			if(rs.getRow()==0){ // no rows
				s = new String("no g_feature_displayname for feature_id "+featureID);
			} else{
				s = new String(rs.getString(1));
			}
			rs.close();
			st.close();
		} catch(SQLException e) {
			log.error(e.toString());
		}
		return s;
	}
	public void lookupQuickAttributes(Connection gdb, FeatureInfo fi) {
		String wktGeometry = null;
		double lat=90, lng=0, radius=0;
		double acc = 1000; // Use default 1000 meter map accuracy if not given explictly.
		double prec=0;     // Use 0 coordinate precision if not explicitly given.
		String s = null;
		String src = null;

		String query =
			" SELECT"+
			" geom_y,"+
			" geom_x,"+
			" radius,"+
			" displayname"+
//			TODO: There is no assurrance that a collection record exists for the feature. 
//			Should check that the database does have this, but also avoid the problem by not doing the jopin here. 
//			" coordprecision, " +
//			" mapaccuracyinmeters," +
//			" g_collection.name," +
			" FROM i_feature_footprint, " +
			" g_feature, " +
//			" g_collection," +
			" g_feature_displayname" +
			" WHERE i_feature_footprint.feature_id=g_feature_displayname.feature_id" +
//			" AND g_feature.collection_id=g_collection.collection_id" +
			" AND g_feature.feature_id=i_feature_footprint.feature_id" +
			" AND i_feature_footprint.feature_id="+fi.featureID+";";
		/*
SELECT geom_y, geom_x, radius, coordprecision, mapaccuracyinmeters, g_collection.name, displayname
FROM i_feature_footprint, g_feature, g_collection, g_feature_displayname
WHERE i_feature_footprint.feature_id=g_feature_displayname.feature_id
AND g_feature.collection_id=g_collection.collection_id
AND g_feature.feature_id=i_feature_footprint.feature_id
AND i_feature_footprint.feature_id=76000707;
		 */
		try {
			Statement st = gdb.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			if(rs.getRow()!=0){ // has rows
				lat=rs.getDouble(1);
				lng=rs.getDouble(2);
				radius=rs.getDouble(3);
				s = new String(rs.getString(4));
//				prec=rs.getDouble(5);
//				acc=rs.getDouble(6);
//				src = new String(rs.getString(7));
			}
			rs.close();
			st.close();
		} catch(SQLException e) {
			log.error(e.toString());
		}
		fi.latitude=lat;
		fi.longitude=lng;
		fi.extentInMeters=radius;
		fi.name=s;
//		fi.coordSource=src;
//		fi.coordPrecision=prec;
//		fi.mapAccuracyInMeters=acc;
		fi.geodeticDatum=DatumManager.getInstance().getDatum("WGS84");
	}

	public void lookupFeatureTypeAttributes(Connection gdb, FeatureInfo fi) {
		String featuretype = null;
		double uncertainty = 0; // Use 0 as best guess uncertainty if not given explicitly.
		String query =
			"SELECT "+
			" i_scheme_term.bestguess_uncert," +
			" i_scheme_term.term" +
			" FROM"+
			" i_feature_footprint," +
			" i_scheme_term," +
			" i_classification" +
			" WHERE"+
			" i_feature_footprint.feature_id=i_classification.feature_id" +
			" AND i_classification.classification_term_id=i_scheme_term.scheme_term_id"+
			" AND i_feature_footprint.feature_id="+fi.featureID+
			";";

		try {
			Statement st = gdb.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			if(rs.getRow()==0){ // no rows
				featuretype = new String("feature type not recorded");
			} else{
				uncertainty=rs.getDouble(1);
				featuretype = new String(rs.getString(2));
			}
			rs.close();
			st.close();
		} catch(SQLException e) {
			log.error(e.toString());
		}
		fi.classificationTerm=featuretype;
		if(fi.extentInMeters<1) fi.extentInMeters=uncertainty;
	}

	public void lookupCollectionAttributes(Connection gdb, FeatureInfo fi) {
		String src = null; // Name of the coordinate source data set.
		double acc = 1000; // Use default 1000 meter map accuracy if not given explictly.
		double prec=0;     // Use 0 coordinate precision if not explicitly given.

		String query =
			" SELECT"+
			" g_collection.name," +
			" g_collection.coordprecision," +
			" g_collection.mapaccuracyinmeters" +
			" FROM"+
			" g_feature," +
			" g_collection" +
			" WHERE"+
			" g_feature.collection_id=g_collection.collection_id" +
			" AND g_feature.feature_id="+fi.featureID+
			";";
		try {
			Statement st = gdb.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			if(rs.getRow()==0){ // no rows
				src = new String("feature source not recorded");
			} else{
				src = new String(rs.getString(1));
				prec = rs.getDouble(2);
				acc = rs.getDouble(3);
			}
			rs.close();
			st.close();
		} catch(SQLException e) {
			log.error(e.toString());
		}
		fi.coordSource=src;
		fi.coordPrecision=prec;
		fi.mapAccuracyInMeters=acc;
	}

	public void lookupFootprintAttributes(Connection gdb, FeatureInfo fi) {
		String wktGeometry = null;
		double lat=90, lng=0, radius=0;
		String query =
			" SELECT"+
			" asText(footprint)," +
			" geom_y,"+
			" geom_x,"+
			" radius"+
			" FROM i_feature_footprint " +
			" WHERE feature_id="+fi.featureID+";";
		try {
			Statement st = gdb.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			if(rs.getRow()!=0){ // has rows
				if(rs.getString(1)==null){ // feature has no footprint
					rs.close();
					st.close();
					return;
				}
				wktGeometry = new String(rs.getString(1));
				lat=rs.getDouble(2);
				lng=rs.getDouble(3);
				radius=rs.getDouble(4);
			}
			rs.close();
			st.close();
		} catch(SQLException e) {
			log.error(e.toString());
		}
		fi.encodedGeometry=wktGeometry;
		fi.latitude=lat;
		fi.longitude=lng;
		fi.extentInMeters=radius;
		fi.geodeticDatum=DatumManager.getInstance().getDatum("WGS84");
	}
	public void lookupPointRadiusAttributes(Connection gdb, FeatureInfo fi) {
		String wktGeometry = null;
		double lat=90, lng=0, radius=0;
		String query =
			" SELECT"+
			" geom_y,"+
			" geom_x,"+
			" radius"+
			" FROM i_feature_footprint " +
			" WHERE feature_id="+fi.featureID+";";
		try {
			Statement st = gdb.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			if(rs.getRow()!=0){ // has rows
				lat=rs.getDouble(1);
				lng=rs.getDouble(2);
				radius=rs.getDouble(3);
			}
			rs.close();
			st.close();
		} catch(SQLException e) {
			log.error(e.toString());
		}
		fi.latitude=lat;
		fi.longitude=lng;
		fi.extentInMeters=radius;
		fi.geodeticDatum=DatumManager.getInstance().getDatum("WGS84");
	}
	public String lookupCoordinateSource(Connection gdb, int featureID) {
		String s = null;
		String query =
			"SELECT"+
			" g_collection.name" +
			" FROM"+
			" g_feature," +
			" g_collection" +
			" WHERE "+
			" g_feature.collection_id=g_collection.collection_id" +
			" AND g_feature.feature_id="+featureID+
			";";
		try {
			Statement st = gdb.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			if(rs.getRow()==0){ // no rows
				s = new String("no g_collection record for feature_id "+featureID);
			} else{
				s = new String(rs.getString(1));
			}
			rs.close();
			st.close();
		} catch(SQLException e) {
			log.error(e.toString());
		}
		return s;
	}

	public double lookupMapAccuracyInMeters(Connection gdb, int featureID) {
		double d = 1000; // use default 1000 meter map accuracy if not given explictly
		String query =
			"SELECT"+
			" g_collection.mapaccuracyinmeters" +
			" FROM"+
			" g_feature," +
			" g_collection" +
			" WHERE"+
			" g_feature.collection_id=g_collection.collection_id" +
			" AND g_feature.feature_id="+featureID+
			";";
		try {
			Statement st = gdb.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			if(rs.getRow()==0){ // no rows
				log.info("MapAccuracy: g_collection not linked to feature_id "+featureID);
			}else{
				d = rs.getDouble(1);
			}
			rs.close();
			st.close();
		} catch(SQLException e) {
			log.error(e.toString());
		}
		if(d<0) d=0;
		return d;
	}

	public double lookupCoordinatePrecision(Connection gdb, int featureID) {
		double d = 0; 
		String query =
			"SELECT"+
			" g_collection.coordprecision" +
			" FROM"+
			" g_feature," +
			" g_collection" +
			" WHERE"+
			" g_feature.collection_id=g_collection.collection_id" +
			" AND g_feature.feature_id="+featureID+
			";";
		try {
			Statement st = gdb.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			if(rs.getRow()==0){ // no rows
				log.info("CoordinatePrecision: g_collection not linked to feature_id "+featureID);
			}else{
				d = rs.getDouble(1);
			}
			rs.close();
			st.close();
		} catch(SQLException e) {
			log.error(e.toString());
		}
		return d;
	}

	public String lookupFeatureType(Connection gdb, int featureID) {
		String s = null;
		String query =
			"SELECT"+
			" i_scheme_term.term" +
			" FROM"+
			" i_feature_footprint," +
			" i_scheme_term," +
			" i_classification" +
			" WHERE"+
			" i_feature_footprint.feature_id=i_classification.feature_id" +
			" AND i_classification.classification_term_id=i_scheme_term.scheme_term_id"+
			" AND i_feature_footprint.feature_id="+featureID+
			";";

		try {
			Statement st = gdb.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			if(rs.getRow()==0){ // no rows
				s = new String("FeatureType: i_scheme_term not linked to feature_id "+featureID);
			} else{
				s = new String(rs.getString(1));
			}
			rs.close();
			st.close();
		} catch(SQLException e) {
			log.error(e.toString());
		}
		return s;
	}

	public String lookupConvexHull(Connection gdb, int featureID) {
		String wktGeometry = null;
		String query =
			" SELECT asText(geom)" +
			" FROM i_feature_footprint " +
			" WHERE feature_id="+featureID+";";
		try {
			Statement st = gdb.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			if(rs.getRow()!=0){ // no rows
				wktGeometry = new String(rs.getString(1));
			}
			rs.close();
			st.close();
		} catch(SQLException e) {
			log.error(e.toString());
		}
		return wktGeometry;
	}

	public String lookupFootprint(Connection gdb, int featureID) {
		String wktGeometry = null;
		String query =
			"SELECT asText(footprint) " +
			"FROM i_feature_footprint " +
			"WHERE feature_id="+featureID+";";
		try {
			Statement st = gdb.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			if(rs.getRow()!=0){ // no rows
				wktGeometry = new String(rs.getString(1));
			}
			rs.close();
			st.close();
		} catch(SQLException e) {
			log.error(e.toString());
		}
		return wktGeometry;
	}

	public double lookupRadius(Connection gdb, int featureID) {
		double r=0;
		String query =
			"SELECT radius " +
			"FROM i_feature_footprint " +
			"WHERE feature_id="+featureID+";";
		try {
			Statement st = gdb.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			if(rs.getRow()!=0){ // no rows
				r = rs.getDouble(1);
				if(r<=1){
					log.error("Invalid i_feature_footprint.radius ("+r+") for feature_id:\t"+featureID);
					return 0;
				}
			}
			rs.close();
			st.close();
		} catch(SQLException e) {
			log.error(e.toString());
		}
		return r;
	}

	public double lookupBestGuessUncertainty(Connection gdb, int featureID) {
		String query =
			"SELECT" +
			" i_scheme_term.bestguess_uncert, " +
			" i_scheme_term.term" +
			" FROM i_scheme_term," +
			" i_classification" +
			" WHERE i_classification.classification_term_id=i_scheme_term.scheme_term_id" + 
			" AND i_classification.feature_id="+featureID+
			";";
		try {
			double extent = 0;
			String name = null;

			Statement st = gdb.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			if(rs.getRow()==0){ // no rows
				log.info("BestGuessUncertainty: i_scheme_term not linked to feature_id "+featureID);
			} else{
				extent = rs.getDouble(1);
				if(extent==0){
					name = rs.getString(2);
					log.info("No best guess extent found for feature_id:\t"+featureID+" feature_type:\t"+name);
					return 0;
				}
			}

			rs.close();
			st.close();
			return extent;
		} catch(SQLException e) {
			log.error(e.toString());
		}

		return 0;
	}
	public void lookupNearestFeature(Connection gdb, FeatureInfo fromfi, FeatureInfo tofi, double ddlimit, String withingeomEWKT) {
		// finds the feature (tofi) nearest to fromfi within a geometry specified 
		// by withingeomEWKT, but further from fromfi than the distance in decimal 
		// degrees specified by ddlimit. Unfortunately, this search is too slow to be practical.
		int featureid=-1;
		double lat=90, lng=0, radius=0, distanceindd=0, withindist=0;
		String query =
			" SELECT"+
			" feature_id,"+
			" geom_y,"+
			" geom_x,"+
			" radius,"+
			" Distance(centroid(geom),GeomFromEWKT('SRID=4326;POINT("+fromfi.longitude+" "+fromfi.latitude+")')) as distanceindd,"+
			" Distance(centroid(geom),GeomFromEWKT("+withingeomEWKT+")) as withindist"+
			" FROM i_feature_footprint "+
			" WHERE"+
			" Distance(centroid(geom),GeomFromEWKT("+withingeomEWKT+")) = 0"+
			" AND Distance(centroid(geom),GeomFromEWKT('SRID=4326;POINT("+fromfi.longitude+" "+fromfi.latitude+")')) >= "+ddlimit+
			" ORDER BY distanceindd ASC LIMIT 1";

//		" SELECT"+
//		" feature_id,"+
//		" geom_y,"+
//		" geom_x,"+
//		" radius,"+
//		" Distance(centroid(geom),GeomFromEWKT('SRID=4326;POINT("+fromfi.longitude+" "+fromfi.latitude+")')) as distanceindd,"+
//		" Distance(centroid(geom),GeomFromEWKT("+withingeomEWKT+")) as withindist"+
//		" FROM i_feature_footprint "+
//		" WHERE"+
//		" Distance(centroid(geom),GeomFromEWKT("+withingeomEWKT+")) = 0"+
//		" AND Distance(centroid(geom),GeomFromEWKT('SRID=4326;POINT("+fromfi.longitude+" "+fromfi.latitude+")')) >= "+ddlimit+
//		" ORDER BY distanceindd ASC LIMIT 1";
		/* Example:
SELECT feature_id, geom_y, geom_x, radius, Distance(centroid(geom),GeomFromEWKT('SRID=4326;POINT(-113.9930596 46.8722197)')) as distanceindd, Distance(centroid(geom),GeomFromEWKT('SRID=4326;POLYGON((-113.9930596 46.8722197,-114.92056926599541 47.5082881399381,-115.30475634888285 46.8722197,-114.92056926599541 46.2361512600619,-113.9930596 46.8722197))')) as withindist FROM i_feature_footprint  WHERE Distance(centroid(geom),GeomFromEWKT('SRID=4326;POINT(-113.9930596 46.8722197)')) >= 0.04530465685560272 AND Distance(centroid(geom),GeomFromEWKT('SRID=4326;POLYGON((-113.9930596 46.8722197,-114.92056926599541 47.5082881399381,-115.30475634888285 46.8722197,-114.92056926599541 46.2361512600619,-113.9930596 46.8722197))')) = 0 ORDER BY distanceindd ASC LIMIT 1; 
		 */
		try {
			Statement st = gdb.createStatement();
			ResultSet rs = st.executeQuery(query);
			rs.next();
			if(rs.getRow()!=0){ // has rows
				if(rs.getString(1)==null){ // feature has no footprint
					rs.close();
					st.close();
					return;
				}
				featureid = rs.getInt(1);
				lat=rs.getDouble(2);
				lng=rs.getDouble(3);
				radius=rs.getDouble(4);
				distanceindd=rs.getDouble(5);
				withindist=rs.getDouble(6);
			}
			rs.close();
			st.close();
		} catch(SQLException e) {
			log.error(e.toString());
		}
		tofi.featureID=featureid;
		tofi.latitude=lat;
		tofi.longitude=lng;
		tofi.extentInMeters=radius;
		tofi.geodeticDatum=DatumManager.getInstance().getDatum("WGS84");
	}
}