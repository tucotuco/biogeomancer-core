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
import org.biogeomancer.utils.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import org.biogeomancer.managers.GeorefManager;
import org.biogeomancer.managers.GeorefPreferences;

import com.thoughtworks.xstream.*;
/*
 * The Rec class is a HashMap with DarwinCore column names as keys.
 */
public class Rec extends HashMap<String, String> { 
	public RecState state;              
	public String uFullLocality;  // The concatenation of all verbatim uninterpreted locality input.
	public ArrayList<Clause> clauses;   
	public ArrayList<Georef> georefs;   

	public Map<String, Double> envData; // Environmental data for validation
	public Metadata metadata;

	public Rec() {
		clauses = new ArrayList<Clause>(); 
		georefs = new ArrayList<Georef>(); 
		state = RecState.REC_CREATED;
		envData = new HashMap();
		metadata = new Metadata();
	}

	public static void main(String[] args) throws Exception {
		if(args.length==0) {
			System.out.println("testnum prefs lang arguments required");
			return;
		}
		Integer z = new Integer(args[0]);
		int test = z.intValue();

		Rec r = new Rec();
//		r.put("locality", "5 miles N of Missoula, T20N R14W Sec. 17, Zone 14T 343212N 45332E, Montana");
		r.put("locality", "5 miles N of Missoula");
//		r.put("locality", "T20N R14W Sec. 17");
//		r.put("locality", "Zone 14T 343212N 45332E"); // fails in Yale and UIUC
//		r.put("locality", "Oconomowoc");
//		r.put("verbatimelevation", "4380 m"); // fails in UIUC
//		r.put("verbatimlatitude", "35d15'23\"N"); // fails in Yale
//		r.put("locality", "5 miles North and 6 km East Missoula"); // fails in Yale
//		r.put("stateprovince", "Montana");
//		r.put("country", "USA");
		GeorefManager gm = new GeorefManager();
		GeorefPreferences gp = null;
		if( args[1] == null ) gp = new GeorefPreferences("all");
		else gp = new GeorefPreferences(args[1]);
		gm.georeference(r, gp);
		if(args.length>2 && args[2]!=null && args[2].length()>0){
			SupportedLanguages lang = SupportedLanguages.english;
			if(args[2].equalsIgnoreCase("es")){
				lang = SupportedLanguages.spanish;
			} else if(args[2].equalsIgnoreCase("pt")){
				lang = SupportedLanguages.portuguese;
			} else if (args[2].equalsIgnoreCase("fr")){
				lang = SupportedLanguages.french;
			}
			for(Clause c : r.clauses){
				for(LocSpec l : c.locspecs){
					l.interpretVerbatimAttributes(lang);
				}
			}
		}
		if(r.georefs.size()==0){
			Georef g = Georef.makeOne("POLYGON((36 -122, 36 -120, 34 -120, 34 -122, 36 -122))");
			r.georefs.add(g);
		}

		switch (test) {
		case 1:
			System.out.println("***toXML(showgeom=true) test***");
			System.out.println(r.toXML(false));
			break;
		case 2:
			System.out.println("***toXML(showgeom=false) test***");
			System.out.println(r.toXML(false));
			break;
		default:
			System.out.println("No test number "+test);
		}
	}
	public void clear(){
		clauses.clear();
		georefs.clear();
		envData.clear();
		state=RecState.REC_CLEARED;
	}

	public String get(String s){
		if(s==null) return null;
		return super.get( s.toLowerCase().trim() );
	}
	public String toMarkup(){
		String s = new String("<REC>");
		for(Clause c: clauses){
			if(c.locType.equalsIgnoreCase("DEL")==false)
				s=s.concat(c.toMarkup());
		}
		s=s.concat("</REC>");
		return s;
	}
	public String toMarkup(String locType){
		String s = new String("<REC>");
		for(Clause c: clauses){
			if(c.locType.equalsIgnoreCase(locType)==true)
				s=s.concat(c.toMarkup());
		}
		s=s.concat("</REC>");
		return s;
	}
	public String getFullLocality(){
		String s = new String("("+this.get("id")+") ");
		String t = (String) this.get("highergeography");
		if(t!=null)
			s=s.concat("; "+t);

		t = (String) this.get("continent");
		if(t!=null)
			s=s.concat("; "+t);

		t = (String) this.get("waterbody");
		if(t!=null)
			s=s.concat("; "+t);

		t = (String) this.get("islandgroup");
		if(t!=null)
			s=s.concat("; "+t);

		t = (String) this.get("island");
		if(t!=null)
			s=s.concat("; "+t);

		t = (String) this.get("country");
		if(t!=null)
			s=s.concat("; "+t);

		t = (String) this.get("stateprovince");
		if(t!=null)
			s=s.concat("; "+t);

		t = (String) this.get("county");
		if(t!=null)
			s=s.concat("; "+t);

		t = (String) this.get("locality");
		if(t!=null)
			s=s.concat(t);

		t = (String) this.get("minimumelevationinmeters");
		if(t!=null)
			s=s.concat("; "+t);

		t = (String) this.get("maximumelevationinmeters");
		if(t!=null)
			s=s.concat("; "+t);

		t = (String) this.get("verbatimelevation");
		if(t!=null)
			s=s.concat("; "+t);

		t = (String) this.get("verbatimcoordinates");
		if(t!=null)
			s=s.concat("; "+t);

		t = (String) this.get("verbatimcoordinatesystem");
		if(t!=null)
			s=s.concat("; "+t);

		t = (String) this.get("verbatimlatitude");
		if(t!=null)
			s=s.concat("; "+t);

		t = (String) this.get("verbatimlongitude");
		if(t!=null)
			s=s.concat("; "+t);

		t = (String) this.get("decimallatitude");
		if(t!=null)
			s=s.concat("; "+t);

		t = (String) this.get("decimallongitude");
		if(t!=null)
			s=s.concat("; "+t);

		t = (String) this.get("geodeticdatum");
		if(t!=null)
			s=s.concat("; "+t);

		t = (String) this.get("earliestdatecollected");
		if(t!=null)
			s=s.concat("; "+t);

		t = (String) this.get("latestdatecollected");
		if(t!=null)
			s=s.concat("; "+t);

		t = (String) this.get("verbatimcollectingdate");
		if(t!=null)
			s=s.concat("; "+t);
		if(s.length()==0){
			s.concat("no standard interpretable field provided");
		}else if(s.charAt(0)==';'){
			s=s.substring(1);
		}
		uFullLocality=new String(s);
		return uFullLocality;
	}
	public String toString(){
		String s = new String("<REC>\n");
		s=s.concat("Rec state: "+state+ "\n");

		s=s.concat("Original Full Locality: ");
		if( uFullLocality != null && uFullLocality.trim().length()>0) {
			s=s.concat(uFullLocality+ "\n");
		}	else s=s.concat(" not given\n");

		if( this.clauses == null || this.clauses.size() == 0){
			s=s.concat("Clause count: 0\n<CLAUSES>\n");
		}else {
			s=s.concat("Clause count: "+this.clauses.size() + "\n<CLAUSES>\n");
			for( Clause clause: this.clauses) { // print out each clause
				s=s.concat(clause.toString());
			}
		}
		s=s.concat("</CLAUSES>\n");
		if( this.georefs == null || this.georefs.size() == 0){
			s=s.concat("Rec georef count: 0\n<RECGEOREFS>\n");
		}else {
			s=s.concat("Rec georef count: "+this.georefs.size() + "\n<RECGEOREFS>\n");
			for( Georef georef: this.georefs) { // print out each georef
				s=s.concat(georef.toString());
			}
		}
		s=s.concat("</RECGEOREFS>\n");

		// Print out the HashMap key/value pairs.
		Iterator keys = this.keySet().iterator();
		String name, value = null;
		int count = 1;
		while(keys.hasNext()) {
			name = (String) keys.next();
			value = (String) this.get(name);
			s = s.concat(count++ + ") " + name + " = " + value+"\n");	
		}

		s=s.concat("</REC>\n");
		return s;
	}

	public String toXML(boolean showgeom){
//		XStream xstream = new XStream();
//		xstream.alias("GEOREF", Georef.class);
//		xstream.alias("CLAUSE", Clause.class);
//		String xml = xstream.toXML(this);
		String s = new String("<REC>\n<INPUTFIELDS>\n");
		// Print out the HashMap key/value pairs.
		Iterator keys = this.keySet().iterator();
		String name, value = null;
		int count = 1;
		while(keys.hasNext()) {
			name = (String) keys.next();
			value = (String) this.get(name);
			s = s.concat("<FIELD name=\""+name+"\">"+value+"</FIELD>\n");
		}
		s = s.concat("</INPUTFIELDS>\n");

		if( uFullLocality != null && uFullLocality.trim().length()>0) {
			s=s.concat("<FULL_LOCALITY>"+uFullLocality+ "</FULL_LOCALITY>\n");
		} else{
			s=s.concat("<FULL_LOCALITY></FULL_LOCALITY>\n");
		}

		s = s.concat("<CLAUSES>\n");
		for( Clause clause: this.clauses) {
			s=s.concat(clause.toXML(showgeom));
		}
		s=s.concat("</CLAUSES>\n");

		s = s.concat("<REC_GEOREFS>\n");
		for( Georef georef: this.georefs) {
			s=s.concat(georef.toXML(showgeom));
		}
		s=s.concat("</REC_GEOREFS>\n");

		s = s.concat("<REC_PROCESSSTEPS>\n");
		if(metadata!=null){
			s = s.concat(metadata.toXML());
		}
		s = s.concat("</REC_PROCESSSTEPS>\n");

		s=s.concat("<REC_STATE>"+state+"</REC_STATE>"+"\n");
		s=s.concat("</REC>\n");
		return s;
	}
}