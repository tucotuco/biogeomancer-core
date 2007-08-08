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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;

/*
 * BGManager is the base class for managers. It provides database, logging, 
 * and properties support. Note that a manager can have many property files
 * associated with it. That's why we use the propertyMap.
 */
public class BGManager
{
	protected static Connection gadm; // GADM Administrative boundaries
	protected static Connection plss; // PLSS township geometries
	protected static Connection gn; // GeonetNames gazetteer data
	protected static Connection conustigerplaces; // Tiger Census shapes for S populated places
	protected static Connection gnispopulatedplaces; // GNIS populated place gazetteer data
	protected static Connection worldplaces; // Combined gadm, gn, conustigerplaces, gnispopulatedplaces gazetteer data
	protected static Connection userplaces; // Features entered by users through the BioGeomancer Workbench
	
	protected HashMap <String,Properties> propertyMap = new HashMap<String,Properties>();
	protected static Properties properties;

	/** 
	 * Loads the properties file.
	 **/
	protected static void initProps(String propsfile, Properties props) {
		InputStream inputStream;
		try {
			// Load the properties file
			ClassLoader loader = BGManager.class.getClassLoader();
			inputStream = loader.getResourceAsStream(propsfile);
			props.load(inputStream);
			
			// Load the user's properties file.
			String userHomeDir = System.getProperty("user.home");
			File userConfigFile = new File(userHomeDir + "/bg.config");
			if (userConfigFile.exists()) {
				InputStream userProps;
				userProps = new FileInputStream(userConfigFile);
				props.load(userProps);
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads and returns a new properties object.
	 * 
	 * @param propsFilename
	 * @return
	 */
	protected void getProps(String propsFilename, Properties props) {
		File propsFile = new File(propsFilename);
		InputStream input;
		try {
			input = new FileInputStream(propsFile);
			props.load(input);
		} catch (FileNotFoundException e1) {
			ClassLoader loader = this.getClass().getClassLoader();
			input = loader.getResourceAsStream(propsFilename);
			try {
				props.load(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Connect to a search database (JDBC is threadsafe).
	protected Connection gazdbconnect(String driver, String server, String host, 
			String name, String user, String pass) {
		Connection gazdb = null;
		try {
			Class.forName("org.postgresql.Driver");
			String url = driver + ":" + server + "://" + host + "/" + name;
			Properties props = new Properties();
			props.setProperty("user", user);
			props.setProperty("password", pass);
			gazdb = DriverManager.getConnection(url, props);
		}
		catch (Exception e) {
			System.out.println(e.toString());
			gazdb = null;
		}
		return gazdb;
	}
	
	/**
	 * Starts the log.
	 */
//	protected static Logger getLog(Class theClass, String logfile) {
//		Logger log;
//		try {
//			log = Logger.getLogger(theClass);    
//			startlogfile(log, logfile);
//			return log;
//		}
//		catch(Exception e){
//			System.out.println(e.toString());
//		}
//		return null;
//	}
	
	/**
	 * Add log file appender.
	 */
//	private static void startlogfile(Logger log, String logfile) {
//		try {
//			TTCCLayout layout = new TTCCLayout();
//			layout.setCategoryPrefixing(true);
//			layout.setContextPrinting(true);
//			layout.setThreadPrinting(true);
//			FileAppender appender = new FileAppender(layout, logfile, true);
//			appender.setEncoding("UTF-8");
//			appender.setName("bgmanagerLogger");
//			log.addAppender(appender);
//		}
//		catch (Exception e) {
//			System.out.println("could not create log file: " + e.toString());
//		}
//	}
}