 /***
Config.java
Created on Mar 24, 2005
***/
package org.biogeomancer.utils;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.Locale;
import java.util.ResourceBundle;
import java.lang.String;
import java.util.logging.*;

import org.biogeomancer.records.*;

public class Config {
    private static Config instance;
    private Properties p;
    public HashMap resources;
    private Config() {
	p = new Properties(); 
	resources = new HashMap(); }

    public static synchronized String get(String property) {
	return instance.p.getProperty(property); }
    public static synchronized void set(String name, String value) {
	instance.p.setProperty(name, value); }
    public synchronized static void load(String filename) {
        try{
	    FileInputStream f = new FileInputStream(filename);
	    instance.p.load(f);
	    f.close(); }
	catch(IOException e) {
	    e.printStackTrace(); } 

	// build logger
	Logger log = Logger.getLogger("bg.utils");
	try {
	    FileHandler file_handler = new FileHandler(get("bg.log"), true);
	    log.addHandler(file_handler); }
	catch(IOException e) {
	    e.printStackTrace(); }

	// build locales
	HashMap l = new HashMap();
	for(SupportedLanguages lang: SupportedLanguages.values()){
	    l.put(lang, ResourceBundle.getBundle("bg.locale.Concepts_" + lang.toString())); }
	Config.instance.resources.put("locales", l);
    }
    public synchronized static Config getInstance() {
	//System.out.println("CONFIG GETINSTANCE()");
	if(instance == null){
	    instance = new Config(); }
	//System.out.println("CONFIG GETINSTANCE() -- EXISTING" + instance.p); 
	return instance; }
}


