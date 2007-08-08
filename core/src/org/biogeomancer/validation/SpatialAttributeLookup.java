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
package org.biogeomancer.validation;

import org.diva.spatial.Grid;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.regex.*;
import java.awt.geom.Point2D;
import org.apache.log4j.*;

// A nice API for writing XML.
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import com.megginson.sax.*;

/**
 * This class is used for spatial attribute lookup operations against vector
 * and grid layers. The attribute and layer definitions are located 
 * in a file named SpatialAttributeLookup.properties in the root src/ directory.
 *
 * @author aaron
 *
 */
/**
 * @author aaron
 *
 */
public class SpatialAttributeLookup
{
	private static Connection db;
	private static Properties props;
	private static Logger log; 
	private HashMap<String, Attribute> attributes;
	private String gridDir;
	private static final int GRID_FILE = 0;
	private static final int GRID_ATTRIBUTE_NAME = 1;
	private static final int VECTOR_TABLE = 1;
	private static final int VECTOR_COLUMN = 2;
	private static final int VECTOR_ATTRIBUTE_NAME = 0;
	
	/**
	 * Super class for vector and grid attribute classes.
	 * 
	 * @author aaron
	 *
	 */
	private class Attribute {
		public String name;
		
		/**
		 * Constructs a new attribute.
		 * 
		 * @param name the human readable name of this attribute
		 */
		public Attribute(String name) {
			this.name = name;
		}
	}
	
	/**
	 * This class is used to store information about a specific grid attribute.
	 * 
	 * @author aaron
	 *
	 */
	private class GridAttribute extends SpatialAttributeLookup.Attribute {
		public String filename;
		
		/**
		 * Constructs a new grid attribute.
		 * 
		 * @param filename the name of the grid file on disk
		 * @param name the human readable name of this attribute
		 */
		GridAttribute(String filename, String name) {
			super(name);
			this.filename = filename;
		}
	}
	
	/**
	 * This class is used to store information about a specific vector attribute.
	 * 
	 * @author aaron
	 *
	 */
	private class VectorAttribute extends SpatialAttributeLookup.Attribute {
		public String table;
		public String column;
		
		/**
		 * Constructs a new vector attribute that is backed by a database. 
		 * Shapefiles not yet supported.
		 * 
		 * TODO: Add shapefile support.
		 * 
		 * @param table database table name where this attribute is defined.
		 * @param column database column name where this attribute is defined.
		 * @param name the human readable name of this attribute.
		 */
		VectorAttribute(String table, String column, String name) {
			super(name);
			this.table = table;
			this.column = column;
		}
	}
	
	/**
	 * A simple test stub that shows lookup() and distance() methods.
	 * 
	 * @param argv
	 */
	public static void main(String[] argv) {
		SpatialAttributeLookup sal = new SpatialAttributeLookup();
		LinkedList<String> list = sal.getAttributeNames();
		String value = null;
		Double x, y = 0.0;

		for (String attribute : list) {
			log.info("lookup(" + argv[0] + ", " + argv[1] + ", " + attribute + ")");
			value = sal.lookup(argv[1], argv[0], attribute);
			log.info("distance(" + argv[0] + ", " + argv[1] + ", " + 
					attribute + ", " + value + ")");
			x = -121 + 1 * (Math.random() - 0.5);
            y = 38 + 1 * (Math.random() - 0.5);
			sal.distance(x.toString() , y.toString(), attribute, value);
		}		
	}	
	
	/**
	 * Constructs the SpatialAttributeLookup object. Starts the log, loads the attribute
	 * properties, and connects to the vector database.
	 *
	 */
	public SpatialAttributeLookup() {
		startlog();
		loadprops();
		dbconnect();
		loadattributes();
		log.info(getAttributeNames());
	}
	
	/*
	 * Returns a list of attribute names that are available for lookup operations.
	 */
	public LinkedList<String> getAttributeNames() {
		LinkedList<String> list = new LinkedList<String>();

		for (Attribute a : attributes.values()) 
			list.add(a.name);

		return list;
	}
	
	/**
	 * Starts the log.
	 */
	private void startlog() {
		ConsoleAppender console = 
			new ConsoleAppender(new SimpleLayout(), ConsoleAppender.SYSTEM_OUT);
		log = Logger.getLogger(SpatialAttributeLookup.class);    
		//log.addAppender(console);
	}
	
	/**
	 * Loads layer defs from props file.
	 */
	private void loadattributes() {
		attributes = new HashMap<String, Attribute>();
		Pattern comma = Pattern.compile(",");
		Pattern colon = Pattern.compile(";");
		
		// Load vector attributes.
		String[] vectors = colon.split(props.getProperty("attributes.vector"));
		if (vectors != null) {
			for (String layer : vectors) {
				log.info(layer);
				String[] data = comma.split(layer);
				attributes.put(data[VECTOR_ATTRIBUTE_NAME].trim(), 
						this.new VectorAttribute(data[VECTOR_TABLE].trim(), 
								data[VECTOR_COLUMN].trim(), 
								data[VECTOR_ATTRIBUTE_NAME].trim()));
			}
		}
		
		// Load grid attributes.
		gridDir = props.getProperty("grids.dir"); 
		if (gridDir == null)
			return;
		String[] grid = colon.split(props.getProperty("attributes.grid"));
		if (grid != null) {
			for (String layer : grid) {
				String[] data = comma.split(layer);
				attributes.put(data[GRID_ATTRIBUTE_NAME].trim(),
						this.new GridAttribute(data[GRID_FILE].trim(),
								data[GRID_ATTRIBUTE_NAME].trim()));
			}
		}
	}
	
	/**
	 * Connect to the database (JDBC is threadsafe).
	 *
	 */
	private void dbconnect() {
		log.info("trying to connect to db");
		
		try {
			Class.forName("org.postgresql.Driver");
			String driver, server, host, name, user, pass;
			driver = props.getProperty("db.driver");
			server = props.getProperty("db.server");
			host = props.getProperty("db.host");
			name = props.getProperty("db.name");
			user = props.getProperty("db.user");
			pass = props.getProperty("db.pass");
			String url = driver + ":" + server + "://" + host + "/" + name;
			log.info("URL = " + url);
			props.setProperty("user", user);
			props.setProperty("password", pass);
			db = DriverManager.getConnection(url, props);
		}
		catch (Exception e) {
			log.error(e.toString());
			db = null;
		}
	}
	
	/** 
	 * Loads the properties file.
	 */
	private void loadprops() {
		try {
			ClassLoader loader = SpatialAttributeLookup.class.getClassLoader();
			InputStream inputStream = 
				loader.getResourceAsStream("SpatialAttributeLookup.properties");
			props = new Properties();
			props.load(inputStream);
		}
		catch (Exception e) {
			log.error("could not read properties file: " + e.toString());
		}
	}
	
	/** 
	 *	This class implements the FilenameFilter interface to accept .grd files.
	 */
	class GrdFilter implements FilenameFilter 
	{
		public boolean accept(File dir, String s) {
			if (s.endsWith(".grd"))
				return true;
			return false;
		}
	}
	
	
	/**
	 * Return a list of grid file names that match the request.
	 *
	 * @param layers -- the layer names in the request
	 * @return the arraylist of matching grid file names
	 */
	private ArrayList<String> gridnames(String[] layers) {
		try {
			String[] gridfilenames = new File(props.getProperty("grids.dir")).list(new GrdFilter());
			ArrayList<String> match = new ArrayList<String>();
			String prefix;
			Pattern period = Pattern.compile("\\.");
			for (String fname : gridfilenames) {
				prefix = period.split(fname)[0];
				for (String layername : layers) {
					if (layername.matches(prefix)) {
						match.add(layername);
					}
				}
			}
			return match;
		}
		catch (Exception e) {
			log.error(e.toString());
		}
		return null;
	}
	
	/**
	 * Calculates the distance between x,y point and a polygon associated with 
	 * an attribute name/value. 
	 * 
	 * TODO: Make this work with grid files.
	 * 
	 * @param lng longitude (x)
	 * @param lat latitude (y)
	 * @param attribute the attribute name
	 * @param value the attribute value
	 * 
	 * @return kilometers. 0 if x,y in polygon associated with attribute name/value, -1 if error
	 * or if polygon not found, else the distance in kilometers.
	 */
	public double distance(String lng, String lat, String attribute, String value) {
		double distance = -1;
		int SRID = new Integer(props.getProperty("attributes.srid")).intValue();
		
		try {
			log.info("Attribute name: " + attribute.trim());
			Attribute a = attributes.get(attribute.trim());
			log.info("Attribute object: " + a);

			if (a instanceof VectorAttribute) {
				log.info("Vector distance lookup...");
				String column = ((VectorAttribute) a).column;
				String table = ((VectorAttribute) a).table;
				Statement st = db.createStatement();
				StringBuilder query = new StringBuilder();
				query.append("SELECT " +
						"DISTANCE(TRANSFORM(the_geom, 23033), TRANSFORM(GEOMFROMTEXT(" +
						"'POINT(" + lng + " " + lat + ")', " + SRID + "), 23033)) / 1000 " + 
						"FROM " + table + " " +
						"WHERE " + column + " = '" + value + "'");
				log.info(query.toString());
				ResultSet rs = st.executeQuery(query.toString());
				while (rs.next()) {
					distance = new Double(rs.getString(1)).doubleValue();
					log.info("sal.distance(" + a.name + ") = " + distance + "\n\n");
				}
				rs.close();
				st.close();
				return distance;
			}

			else if (a instanceof GridAttribute) {
				log.info("Distance not defined for grids");
				return distance;
			}
		}
		catch (Exception e) {
			log.error(e.toString());
		}	
		return distance;
	}	
	
	
	/**
	 * Lookup the value at point in attribute.
	 * 
	 * @param lng
	 * @param lat
	 * @param attribute name of the attribute from which we want the value from
	 * @return
	 */
	public String lookup(String lng, String lat, String attribute) {
		String result = "No attribute found";
		int SRID = new Integer(props.getProperty("attributes.srid")).intValue();
		
		try {
			log.info("Attribute name: " + attribute.trim());
			Attribute a = attributes.get(attribute.trim());
			log.info("Attribute object: " + a);
			
			if (a instanceof VectorAttribute) {
				log.info("Vector lookup...");
				String column = ((VectorAttribute) a).column;
				String table = ((VectorAttribute) a).table;
				Statement st = db.createStatement();
				StringBuilder query = new StringBuilder();
				query.append("SELECT " + column + " ");
				query.append("FROM " + table + " ");
				query.append("WHERE WITHIN(GEOMFROMTEXT('POINT(" + lng + " " + lat + " )'," + SRID + "), the_geom)");
				log.info("Query: " + query.toString());
				ResultSet rs = st.executeQuery(query.toString());
				while (rs.next()) {
					result = rs.getString(1);
					log.info("sal.lookup("+a.name+") = " + result + "\n\n");
				}
				rs.close();
				st.close();
				return result;
			}
			
			else if (a instanceof GridAttribute) {
				log.info(a + " is a grid attribute");
				String filename = ((GridAttribute) a).filename;
				Grid grid = new Grid(gridDir + filename);
				Point2D.Double point = new Point2D.Double();
				point.setLocation(new Double(lng).doubleValue(), 
						new Double(lat).doubleValue());
				result = grid.getstringvalue(point, true);
				log.info("getstringvalue(" +point+ ") = " + result);
				return result;
			}
		}
		catch (Exception e) {
			log.error(e.toString());
		}	
		return result;
	}
}
