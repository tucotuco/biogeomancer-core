/**
 * Config.java
 * Created on Mar 24, 2005
 */
package org.biogeomancer.utils;
import java.io.*;
import java.util.*;

public class Configs {
    private Properties props;

    /**
     * Config constructor.
     *
     * Creates a Config object given a filename to a properties file.
     *
     * @param filename path to a properties file.
     */
    public Configs(String filename) {
	this.props = new Properties(); 
	if (filename != null)
	    this.load(filename);
    }

    /**
     * get()
     *
     * Gets a config property given a property name.
     *
     * @param name the name of the property value to get.
     */
    public String get(String property) {
	return this.props.getProperty(property); 
    }

    /**
     * set()
     *
     * Sets a config property given a name value pair.
     *
     * @param name the name of the property value to set.
     * @param value the value of the property name to set.
     */
    public synchronized void set(String name, String value) {
	this.props.setProperty(name, value); 
    }

    /** 
     * load()
     *
     * Loads properties from the filename properties file into this Config object.
     *
     * @param filename path to a properties file on a local disk.
     */
    private void load(String filename) {
        try {
	    FileInputStream f = new FileInputStream(filename);
	    props.load(f);
	    f.close(); 
	}
	catch(IOException e) {
	    e.printStackTrace(); 
	} 
    }
}


