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

package org.biogeomancer.records;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
//import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

//import org.biogeomancer.ws.HttpClient;

import com.thoughtworks.xstream.XStream;


public class RecSet {
	public ArrayList<Rec> recs; // The list of records for processing in this RecSet.
	private File file;
	public String filename;     // The file name where the RecSet input is stored.
	public String outfilename;  // The file name where the RecSet output is stored.
	public String delineator;	// user defined delineator for rec file
	public RecSetState state;   // The processing state of the RecSet
	public String[] originalheader;     // The ordered list of columns in the input data.
	//public int priority;      // A relative value used to assess the priority for processing this RecSet.
	//public String language;   // The ISO 639 three-letter language code for the language in which the localities are in.

	/*
	 * @gwt.typeArgs <org.biogeomancer.records.Rec>
	 */

	public static void main(String[] args) { // loads a data file and prints out each record
		final int TOMARKUP = 1;
		final int TOXML = 2;
		if(args.length < 3){
			System.out.println("Usage: main inputfile delimiter testtorun\nExample: main /Users/tuco/Documents/MaNISGeorefUploadTest1-500.txt tab 2");
			return;
		}
		String filename = new String(args[0]);
		String delimiter = new String(args[1]);
		Integer z = new Integer(args[2]);
		int test = z.intValue();
		try {
			RecSet recset = new RecSet(filename, delimiter); 
//			RecSet recset = new RecSet("http://bg.berkeley.edu/MaNISGeorefUploadTest1-500.txt", delimiter, "lastloadedrecset.txt"); 
			switch(test){
			case TOMARKUP:
				System.out.println("filename = " + recset.file.getAbsolutePath());
				System.out.println("RecSet header line:");
				for(int i=0;i<recset.originalheader.length;i++){
					if(i==0) System.out.print(recset.originalheader[i]);
					else System.out.print(delimiter+recset.originalheader[i]);
					System.out.println(recset.toMarkup());
				}
				break;
			case TOXML:
				System.out.println(recset.toXML());
				break;
			}
		}
		catch(RecSet.RecSetException e) {
			System.out.println(e);
		}
	}


	public class RecSetException extends Exception { // recset exception
		public RecSetException(Exception exception) {
			super(exception); 
		}
		public RecSetException(String errormsg, Exception exception) {
			super(errormsg, exception); 
		}
		public RecSetException(String errormsg) {
			super(errormsg); 
		}
	}

	/*
	 * RecSet()
	 *
	 * Constructs a recset given data in the local filesystem.
	 *
	 * @param filename the recset filename in the local filesystem
	 * @param delineator the recset delineator character
	 */
	public RecSet(String filename, String delineator) 
	throws RecSet.RecSetException {  // construct recset from rec file on disk

			this.recs = new ArrayList();
			this.filename = new String(filename);
			this.file = new File(filename);
			this.delineator = delineator;
			this.state=RecSetState.RECSET_CREATED;
			this.load();
	}

	/**
	 * RecSet()
	 *
	 * Constructs a recset given remote data.
	 *
	 * @param fileurl the remote recset file url
	 * @param delineator the recset delineator character
	 */
	public RecSet(String fileurl, String delineator, String destination) 
	throws RecSet.RecSetException {  // construct recset from rec file at URL
		try {
			destination = destination + "/bg_recset_." + System.currentTimeMillis();
			URL url = new URL(fileurl);
			InputStream data = url.openStream();
			FileOutputStream file = new FileOutputStream(destination);

			byte[] buffer = new byte[4096];
			int chunkSize;
			while ((chunkSize = data.read(buffer)) != -1)
				file.write(buffer, 0, chunkSize);
			file.close();
			this.file = new File(destination);
			this.delineator = delineator;
			this.recs = new ArrayList<Rec>();
			this.filename = new File(destination).getName();
			this.load();			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * getFile()
	 *
	 * Downloads and saves recset data from a url, then returns it as a new file.
	 */
	private File getFile(URL fileurl, String destination) {
		try {
			String filedata = (String)fileurl.getContent().toString();
			String filename = destination + fileurl.getFile();
			System.out.println("DATA FILE NAME = " + filename);
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			writer.write(filedata);
			writer.close();
			return new File(filename);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * load()
	 *
	 * Loads a remote recset file.
	 *
	 * @param fileurl the remote recset file url
	 * @param delineator the recset delineator character
	 */
	public boolean load(URL fileurl, String delineator, String destination) 
	throws RecSet.RecSetException {
		this.file = this.getFile(fileurl, destination);
		this.delineator = delineator;
		return this.load();
	}	    

	/**
	 * load()
	 *
	 * Loads a recset file from the local filesystem.
	 *
	 * @param filename the recset filename
	 * @param delineator the recset delineator character
	 */
	public boolean load(String filename, String delineator) 
	throws RecSet.RecSetException {
		this.file = new File(filename);
		return this.load();
	}

	private boolean load() 
	throws RecSet.RecSetException { // load a file from disk 
		if (delineator.equalsIgnoreCase("tab") || 
				delineator.equalsIgnoreCase("\t"))
			delineator = "\t";
		else if (delineator.equalsIgnoreCase("comma"))
			delineator = ",";

		try { 			// opening file
//			BufferedReader reader = new BufferedReader(new FileReader(this.file));
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.file), "UTF-8"));
			Pattern delineation = Pattern.compile(this.delineator);
			Pattern lineTerminators = Pattern.compile("(?m)$^|[\\r\\n]+\\z");
			String line = reader.readLine(); // read header definition from file

			if (lineTerminators.matcher(line.subSequence(0,line.length())).matches()) {	// get rid of control characters 
				line = lineTerminators.matcher(line.subSequence(0,line.length())).replaceAll("");
			}

			line = line + "\n";

			String[] header = delineation.split(line); // create header definition
			this.originalheader = delineation.split(line);
//			for (String name : header)
//			System.out.println("Header: " + name);
			for (int i=0; i<header.length; i++){ // remove leading/trailing white space
				header[i] = header[i].toLowerCase().trim(); 
				this.originalheader[i] = this.originalheader[i].trim();
			}
			String[] row;
			Rec rec;
			int rowcount=0;
			while ((line = reader.readLine()) != null) { // load each row of file data
				if (lineTerminators.matcher(line.subSequence(0,line.length())).matches()) { // get rid of control characters
					line = lineTerminators.matcher(line.subSequence(0,line.length())).replaceAll("");
					line = line + "\n";
				}

				row = delineation.split(line);
				rec = new Rec();
				for (int i=0; i<header.length; i++) {
					if(i<row.length){
						// remove leading/trailing white space
						row[i] = row[i].trim(); 
						if (row[i].startsWith("\"") && row[i].endsWith("\"")) // row value is surrounded by double quotes
							rec.put(header[i], row[i].substring(1, row[i].length() - 1)); // remove double quotes
						else
							rec.put(header[i], row[i]);
					}else
						rec.put(header[i], null);
				}
				// Provide an id if no id record is given in the input.
				if(rec.get("id")==null){
					rowcount++;
					rec.put("id", String.valueOf(rowcount));
				}
				rec.getFullLocality();
				this.recs.add(rec);
			}
		}
		catch(Exception e) {
			this.state=RecSetState.RECSET_LOADED;
			throw this.new RecSetException("Problem loading the data file: " + e.toString(), e);
		}
		this.state=RecSetState.RECSET_LOADED;
		return true;
	}

	public void printRecs() { 	// just print the rec values
		String name, value;
		for(Rec rec: this.recs) { // print out each record
			Iterator keys = rec.keySet().iterator();
			while(keys.hasNext()) {
				name = (String) keys.next();
				value = (String) rec.get(name);
				System.out.println(name + " = " + value);	
			}
//			System.out.println("...................................");
		}

	}
	public String toMarkup(){
		String s = new String("<RECSET>\n");
		for(Rec r: recs){
			s=s.concat(r.toMarkup());
		}
		s=s.concat("</RECSET>");
		return s;
	}
	public String toXML(){
		XStream xstream = new XStream();
		xstream.alias("RECSET", RecSet.class);
		xstream.alias("REC", Rec.class);
		xstream.alias("CLAUSE", Clause.class);
		xstream.alias("LOCSPEC", LocSpec.class);
		xstream.alias("GEOREF", Georef.class);
		String xml = xstream.toXML(this);
		return xml;
	}
	public String toString(){
		String s = new String("<RECSET>\n");
		s=s.concat("RecSet state: "+state+"\n");
		if( filename != null ) {
			s=s.concat("FileName: "+filename+ "\n");
		}	else s=s.concat("FileName not given\n");
		if( delineator != null ) {
			if(delineator.equals("\t")){
				s=s.concat("Delineator: \\t\n");
			} else{
				s=s.concat("Delineator: "+delineator+ "\n");
			}
		}	else s=s.concat("Delineator not given\n");

		if( this.recs == null || this.recs.size() == 0){
			s=s.concat("Record count: 0\n<RECORDS>\n");
		} else {
			s=s.concat("Record count: "+this.recs.size() + "\n<RECORDS>\n");
		}
		String name, value;
		for(Rec rec: this.recs) { // print out each record
			Iterator keys = rec.keySet().iterator();
			while(keys.hasNext()) {
				name = (String) keys.next();
				value = (String) rec.get(name);
			}
			s=s.concat(rec.toString());
		}
		s=s.concat("</RECORDS>\n</RECSET>\n");

		return s;
	}
}