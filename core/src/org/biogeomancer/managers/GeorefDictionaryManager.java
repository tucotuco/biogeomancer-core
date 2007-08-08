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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.biogeomancer.utils.Concepts;
import org.biogeomancer.utils.Headings;
import org.biogeomancer.utils.SupportedLanguages;
import org.biogeomancer.utils.Units;

public class GeorefDictionaryManager extends BGManager {
	private HashMap<SupportedLanguages, ResourceBundle> locales;
	private HashMap<SupportedLanguages, HashMap> dictionary;
	private static final Logger log = Logger.getLogger(GeorefDictionaryManager.class);
	private static Properties props = new Properties();
	private static GeorefDictionaryManager instance = null;

	private GeorefDictionaryManager() {
			init();
			initLocales();
	//		log.info("initLocales() completed");
			initDictionary();
	//		log.info("initDictionary() completed");
		}
	public static void main(String[] argv) {
		try {
			GeorefDictionaryManager gdm = new GeorefDictionaryManager();
			System.out.println(gdm.lookupConcept("North", SupportedLanguages.english));
			System.out.println(gdm.lookupConcept("NO", SupportedLanguages.spanish));
			System.out.println(gdm.lookupConcept("km", SupportedLanguages.portuguese));
			System.out.println(gdm.lookupConcept("E.N.E.", SupportedLanguages.french));			
			
			System.out.println(gdm.lookup("m.", SupportedLanguages.english, Concepts.units, true));
			System.out.println(gdm.lookup("pÃ©s", SupportedLanguages.portuguese, Concepts.units, true));
			System.out.println(gdm.lookup("kilÃ´metros", SupportedLanguages.portuguese, Concepts.units, true));
			System.out.println(gdm.lookup("kilÃ³metros", SupportedLanguages.spanish, Concepts.units,true));
			System.out.println(gdm.lookup("SSO", SupportedLanguages.french, Concepts.headings,true));
			System.out.println(gdm.lookup("Sud-sud-ouest", SupportedLanguages.french, Concepts.headings,true));
			System.out.println(gdm.lookup("LÃ©s-nordeste", SupportedLanguages.portuguese, Concepts.headings,true));

			System.out.println(gdm.lookup("m.", SupportedLanguages.english, Concepts.units, false));
			System.out.println(gdm.lookup("pÃ©s", SupportedLanguages.portuguese, Concepts.units, false));
			System.out.println(gdm.lookup("kilÃ´metros", SupportedLanguages.portuguese, Concepts.units, false));
			System.out.println(gdm.lookup("kilÃ³metros", SupportedLanguages.spanish, Concepts.units, false));
			System.out.println(gdm.lookup("SSO", SupportedLanguages.french, Concepts.headings, false));
			System.out.println(gdm.lookup("Sud-sud-ouest", SupportedLanguages.french, Concepts.headings, false));
			System.out.println(gdm.lookup("LÃ©s-nordeste", SupportedLanguages.portuguese, Concepts.headings, false));
		}
		catch (Exception e) {
			log.error(e.toString());
		}
	}
	public void log(String s){
		log.info(s);
	}
	public static GeorefDictionaryManager getInstance(){
		if(instance == null) instance = new GeorefDictionaryManager();
		return instance;
	}
	
	private void initDictionary() {
		dictionary = new HashMap<SupportedLanguages, HashMap>(); 
		for(SupportedLanguages lang : SupportedLanguages.values()) {
			HashMap<String, HashMap> concepts = new HashMap<String, HashMap>();
			HashMap<Headings, LinkedList> heading_patterns = 
				new HashMap<Headings, LinkedList>();	
			HashMap<Units, LinkedList> unit_patterns = 
				new HashMap<Units, LinkedList>();	
			for(Headings heading : Headings.values()) 
				heading_patterns.put(heading, getPatterns((Object)heading, 
						"headings", lang)); 
			concepts.put("headings", heading_patterns); 
			for(Units unit : Units.values()) 
				unit_patterns.put(unit, getPatterns((Object)unit, 
						"units", lang)); 
			concepts.put("units", unit_patterns);
			dictionary.put(lang, concepts); 
		}
	}

	private void init() {
		initProps("GeorefDictionaryManager.properties", props);
//		log.info("GeorefDictionaryManager started");
	}

	/*
	 * Creates hashmap with SupportedLanguages keys mapped to ResourceBundles
	 * for that language.
	 */
	private void initLocales() {
		locales = new HashMap<SupportedLanguages, ResourceBundle>();
		String resource;
		for(SupportedLanguages lang: SupportedLanguages.values()) {
			resource = "org.biogeomancer.locale.Concepts_" + lang.toString();
			ResourceBundle rb = ResourceBundle.getBundle(resource);
			locales.put(lang, rb);
		}
	}

	/*
	 * Given a token and a concept, look up the standardized version of that token in the specified language.
	 */
	public String lookup(String token, SupportedLanguages lang, Concepts concept, boolean abbrev) {
		String abbreviation = null;
		HashMap<String, HashMap> concepts = dictionary.get(lang);
		ResourceBundle locale = locales.get(lang);
		switch (concept) {
		case headings:
			for(Headings heading : Headings.values()) {
				HashMap heading_concept = (HashMap)concepts.get(concept.toString());
				LinkedList<Pattern> patterns = (LinkedList<Pattern>) heading_concept.get(heading);
				for(Pattern pattern : patterns) {
					if(pattern.matcher(token.trim()).matches()) {
						if( abbrev == true ){
						abbreviation = locale.getString(concept.toString() + "." + heading + ".abbreviation");
						return abbreviation; 
						}
						else {
							abbreviation = locale.getString(concept.toString() + "." + heading + ".accepted");
							return abbreviation; 
						}
					}
				}
			}
			break;
		case units:
			for(Units unit : Units.values()) {
				HashMap units_concept = (HashMap)concepts.get(concept.toString());
				LinkedList<Pattern> patterns = (LinkedList<Pattern>) units_concept.get(unit);
				for(Pattern pattern : patterns) {
					if(pattern.matcher(token.trim()).matches()) {
						if( abbrev == true ){
							abbreviation = locale.getString(concept.toString() + "." + unit + ".abbreviation");
							return abbreviation; 
							}
							else {
								abbreviation = locale.getString(concept.toString() + "." + unit + ".accepted");
								return abbreviation; 
							}
					}
				}
			}
			break;
		}
		if(abbreviation == null)
			log.info("Lookup failed: lookup(" + token + ", " + lang + ") = null");
		return abbreviation;
	}
	
	/*
	 * Given a token, look up the concept to which it belongs in the specified language.
	 */
	public String lookupConcept(String token, SupportedLanguages lang) {
		HashMap<String, HashMap> concepts = dictionary.get(lang);
		ResourceBundle locale = locales.get(lang);
		Concepts concept = Concepts.headings;
		for(Headings heading : Headings.values()) {
			HashMap heading_concept = (HashMap)concepts.get(concept.toString());
			LinkedList<Pattern> patterns = (LinkedList<Pattern>) heading_concept.get(heading);
			for(Pattern pattern : patterns) {
				if(pattern.matcher(token.trim()).matches()) {
						return "heading"; 
				}
			}
		}
		concept = Concepts.units;
		for(Units unit : Units.values()) {
			HashMap units_concept = (HashMap)concepts.get(concept.toString());
			LinkedList<Pattern> patterns = (LinkedList<Pattern>) units_concept.get(unit);
			for(Pattern pattern : patterns) {
				if(pattern.matcher(token.trim()).matches()) {
						return "unit"; 
				}
			}
		}
		log.info("Concept lookup failed: lookup(" + token + ", " + lang + ") = null");
		return "unknown";
	}
	
	public String lookup(String token, SupportedLanguages lang) 
	{
		//HashMap<String, HashMap> concepts = dictionary.get(lang);
		ResourceBundle locale = locales.get(lang);
		//Concepts concept = Concepts.headings;		
		return locale.getString(token);
	}
	/***
	 Given a concept token and a supported language, this method returns a list of compiled regular expression
	 Pattern objects. The patterns match user defined strings that are accessible in the
	 GeorefDictionary locales HashMap of ResourceBundles. 
	 @param concept Enumeration of concept values. For example, Headings concept has N, E, S, W, etc.
	 @param property A string that describes the concept.
	 @param lang An enumerated lang. For example, SupportedLanguages.english or SupportedLanguages.spanish
	 @return LinkedList<Pattern>
	 ***/
	private LinkedList<Pattern> getPatterns(Object concept, String property, 
			SupportedLanguages lang) {
		if(property == "units") 
			concept = (Units)concept;
		else if(property == "headings")
			concept = (Headings)concept;
		LinkedList<Pattern> patterns = new LinkedList<Pattern>();
		ResourceBundle locale = locales.get(lang);
		Pattern semicolon = Pattern.compile(";");
		String[] matches = semicolon.split(locale.getString(property + "." + 
				concept + ".matches"));
		for(String match : matches) {
			patterns.add(Pattern.compile(match.trim(), Pattern.CANON_EQ | 
					Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)); }
		return patterns; 
	}
}