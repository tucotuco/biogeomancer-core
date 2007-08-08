package edu.colorado.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.biogeomancer.records.Rec;
import org.biogeomancer.records.RecSet;


public class FileFormat {

	public FileFormat() {

	}
    public ArrayList getPreview(String filename, String tokenizer, boolean skipfirst){
    	/***
    	loads headers from a file.
    	***/

    	String line;
    	String[] recElems = null;
    	ArrayList recs = new ArrayList();


    	// Dataset schema for testing GeorefEngine
    	// http://128.32.146.140/bgdev/?q=node/481
    	Pattern p = Pattern.compile(tokenizer);  // for example tab, comma, etc. "\t", ","
        try 
    	{ 
    	    File recfile = new File(filename);
    	    BufferedReader reader = new BufferedReader(new FileReader(recfile));
    	    line = reader.readLine();
    	    if (skipfirst) line = reader.readLine();
    	    
    	    int i=0;
    	    while((line != null) && (i < 10)) 
    	    {

    	      if (line != null) {
    		    recElems = p.split(line);
    	      }
    	      recs.add(recElems);
    	      i++;
    	      line = reader.readLine();
    	    }
    	    reader.close();
    	      

    	    return recs;
    	} catch(NullPointerException e)
    	{ 
    		e.printStackTrace();
    		System.err.println("filename is null");    		
    	    return null;
    	}
    	catch(FileNotFoundException e)
    	{
    		e.printStackTrace();
    		System.err.println("file not found");    	    
    	    return null;
    	}
    	catch(IOException e)
    	{
    		e.printStackTrace();
    		System.err.println("error reading file");
    		return null;
    	}
    	    
    }

}


