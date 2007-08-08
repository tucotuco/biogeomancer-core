package org.biogeomancer.utils;

import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
//import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.io.InputStream;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.TTCCLayout;
import org.biogeomancer.managers.BGManager;

public class TGNConverter extends BGManager{
	private File infile, outfile;
	public String infilename;   // The file name where the TGN input is stored.
	public String outfilename;  // The file name where the TGN output is stored.
	public String delineator;	// User defined delineator for TGN file.
	private static Properties props = new Properties();

	public static void main(String[] args){
		if(args.length < 3){
			System.out.println("Usage: main inputfile delimiter outputfile\nExample: main /Users/tuco/Documents/TGNTerms.txt tab TGNTermsUTF8.txt");
			return;
		}
		TGNConverter tgn = new TGNConverter(args[0], args[1], args[2]);
		tgn.load();
		
	}
	public TGNConverter(String in, String delim, String out){
		startup("TGNConverter.properties", in, delim, out);
	}
	public TGNConverter(String propsfile, String in, String delim, String out) {	
		startup(propsfile, in, delim, out);
	}
	public void startup(String propsfile, String in, String delim, String out){
		infilename = new String(in);
		delineator = new String(delim);
		outfilename = new String(out);
		infile = new File(infilename);
		outfile = new File(outfilename);

		// Load the user's properties file.
		String userHomeDir = System.getProperty("user.home");		
		
		InputStream userProps;
		try {
			userProps = new FileInputStream(userHomeDir + "/" + propsfile);
			try {
				props.loadFromXML(userProps);
			} catch (InvalidPropertiesFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		initProps(propsfile, props);
	}

	private boolean load() { // load a file from disk 
		if (delineator.equalsIgnoreCase("tab") || 
				delineator.equalsIgnoreCase("\t"))
			delineator = "\t";
		else if (delineator.equalsIgnoreCase("comma"))
			delineator = ",";
		String line = null;
		try { 			// opening file
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.infile), "UTF-8"));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.outfile), "UTF-8"));
			Pattern delineation = Pattern.compile(this.delineator);
			Pattern lineTerminators = Pattern.compile("(?m)$^|[\\r\\n]+\\z");
			line = new String(reader.readLine()); // read header definition from file
			
			if (lineTerminators.matcher(line.subSequence(0,line.length())).matches()) {	// get rid of control characters 
				line = lineTerminators.matcher(line.subSequence(0,line.length())).replaceAll("");
			}

			writer.write(line+"\n");

			int rowcount=0, subcount=0;
			String code;
			String sub = null;
			String A=null, B=null;
			boolean missing=false;
			while ((line = reader.readLine()) != null) { // load each row of file data
				if (lineTerminators.matcher(line.subSequence(0,line.length())).matches()) { // get rid of control characters
					line = lineTerminators.matcher(line.subSequence(0,line.length())).replaceAll("");
				}
				rowcount++;
				missing=false;
				if(rowcount==42645){
					rowcount=42645;
				}
				while(line.indexOf("$") != -1 && missing==false){
					code = line.substring(line.indexOf("$"),line.indexOf("$")+3);
					sub=props.getProperty(code);
					if(sub==null){
						code = line.substring(line.indexOf("$"),line.indexOf("$")+4);
					}
					sub=props.getProperty(code);
					if(sub!=null){
						A = line.substring(0,line.indexOf("$"));
						B = line.substring(line.indexOf("$")+code.length(),line.length());
						line = new String(A+sub+B);
						subcount++;
					} else{
						missing=true;
						System.out.println("Missing: "+code);
					}
				}
//				System.out.println(rowcount+": "+line);
				writer.write(line+"\n");
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
}