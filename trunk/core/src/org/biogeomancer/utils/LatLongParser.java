/**
 *LatLongParser.java
 *
 *The main purpose of this code is to take a String with latitude and
 *longitude or two Strings, one of which is latitude and one of which is longitude,
 *and return an array of LatLong objects with possible parsings.
 *
 *Author: Peter DeVore
 *Email: pdevore AT berkeley dot edu
 */
package org.biogeomancer.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.*;

public abstract class LatLongParser {

	static final String[] LATITUDEMARKERS = { "N", "n", "S", "s" };
	static final String[] LONGITUDEMARKERS = { "E", "e", "W", "w" };
	static final String[] DEGREEMARKERS = { "D", "d", "\u00b0" };
	static final String[] MINUTEMARKERS = { "M", "m", "'" };
	static final String[] SECONDMARKERS = { "S", "s", "\"" };

	public static LatLong[] parse(String s) {
		ArrayList<LatLong> temp = latAndLongParseHelper(s);
		removeDuplicateLatLongs(temp);
		return (LatLong[]) (temp.toArray());
	}

	public static LatLong[] parse(String s1, String s2) {
		ArrayList<LatLong> temp = latAndLongParseHelper(s1, s2);
		removeDuplicateLatLongs(temp);
		return (LatLong[]) (temp.toArray());
	}

	private static ArrayList<LatLong> latAndLongParseHelper(String s) {
		// The task in here is to split up the string into two strings to pass
		// into parse(String, String).
		// It is possible that parse(String) will call parse(String, String)
		// for several different String splitting possibilities.

		// Create the ArrayList<LatLong> to hold candidates
		ArrayList<LatLong> retval = new ArrayList<LatLong>();

		// First attempt: split around spaces
		// Probably a bad idea to split around spaces...
		/*
		 * String[] tempStrArray = s.split(" "); if (tempStrArray.length == 2) {
		 * retval.addAll(latAndLongParseHelper(tempStrArray[0],
		 * tempStrArray[1])); }
		 */

		// Try to split around any sort of direction marking
		// Then send all matches to the parser.
		ArrayList<String[]> tempStrAL = splitByDirection(s);
		for (int i = 0; i < tempStrAL.size(); i++) {
			retval.addAll(latAndLongParseHelper(tempStrAL.get(i)[0], tempStrAL
					.get(i)[1]));
		}

		return retval;
	}

	// Takes a single string that has lat and lon in it
	// and attempts to split it into lat and lon types
	// Then tries to parse the rest of the string
	private static ArrayList<String[]> splitByDirection(String s) {
		String[] temp;
		ArrayList<String[]> retval = new ArrayList<String[]>();
		for (int i = 0; i < LATITUDEMARKERS.length; i++) {
			temp = s.split("(?<=[^A-Za-z]" + LATITUDEMARKERS[i]
					+ ")(?=.*?[0-9])");
			if (temp.length == 2) {
				temp[0] = temp[0].trim();
				temp[1] = temp[1].trim();
				retval.add(temp);
			}
		}
		for (int i = 0; i < LONGITUDEMARKERS.length; i++) {
			temp = s.split("(?<=[^A-Za-z]" + LONGITUDEMARKERS[i]
					+ ")(?=.*?[0-9])");
			if (temp.length == 2) {
				temp[0] = temp[0].trim();
				temp[1] = temp[1].trim();
				retval.add(temp);
			}
		}
		return retval;

		/*
		 * ArrayList<String[]> matches = new ArrayList<String[]>();
		 * 
		 * String[] latitudeSplitters = {"N","n","S","s"}; String[]
		 * longitudeSplitters = {"E","e","W","w"}; TwoDirsAndMags dirsmags = new
		 * TwoDirsAndMags(); String[] temp1; String[] temp2; for (int i = 0; i <
		 * latitudeSplitters.length; i++) { temp1 = s.split("(?<![A-Za-z])" +
		 * latitudeSplitters[i]); if (temp1.length == 2 || temp1.length == 1) {
		 * if (latitudeSplitters[i].equals("N") ||
		 * latitudeSplitters[i].equals("n")) { dirsmags.direction1 =
		 * TwoDirsAndMags.Direction.NORTH; } else if
		 * (latitudeSplitters[i].equals("S") ||
		 * latitudeSplitters[i].equals("s")) { dirsmags.direction1 =
		 * TwoDirsAndMags.Direction.SOUTH; } } for (int j = 0; j <
		 * longitudeSplitters.length; j++) { for (int k = 0; k < temp1.length;
		 * k++) { temp2 = temp1[k].split("(?<![A-Za-z])" +
		 * longitudeSplitters[i]); if (temp2.length == 2 || temp2.length == 1) {
		 * if (latitudeSplitters[i].equals("E") ||
		 * latitudeSplitters[i].equals("e")) { dirsmags.direction2 =
		 * TwoDirsAndMags.Direction.EAST; } else if
		 * (latitudeSplitters[i].equals("W") ||
		 * latitudeSplitters[i].equals("w")) { dirsmags.direction2 =
		 * TwoDirsAndMags.Direction.WEST; }
		 *  } } } } if ((dirsmags.direction1 == TwoDirsAndMags.Direction.NORTH ||
		 * dirsmags.direction1 == TwoDirsAndMags.Direction.SOUTH) &&
		 * (dirsmags.direction2 != TwoDirsAndMags.Direction.EAST &&
		 * dirsmags.direction2 != TwoDirsAndMags.Direction.EAST)) { double temp = }
		 * return dirsmags;
		 */
	}

	/**
	 * latAndLongParseHelper takes in two Strings, one of which is latitude and
	 * the other of which is longitude. However, it is not known at this point
	 * which String is latitude and which String is longitude.
	 * 
	 * @param s1
	 *            either a latitude String or a longitude String, but not the
	 *            same type as s2.
	 * @param s2
	 *            either a latitude String or a longitude String, but not the
	 *            same type as s1.
	 * @return an ArrayList<LatLong> of possible parsings.
	 */
	private static ArrayList<LatLong> latAndLongParseHelper(String s1, String s2) {
		// Create the ArrayList<LatLong> to hold candidates
		ArrayList<LatLong> retval = new ArrayList<LatLong>();

		// Try to figure out which is lat and which is long, then pass info to
		// latThenLongParseHelper

		return retval;
	}

	/**
	 * latThenLongParseHelper takes in two Strings, the first of which is
	 * latitude and the second of which is longitude. However, it is not known
	 * at this point what the numeric values are.
	 * 
	 * @param s1
	 *            either a latitude String or a longitude String, but not the
	 *            same type as s2.
	 * @param s2
	 *            either a latitude String or a longitude String, but not the
	 *            same type as s1.
	 * @return an ArrayList<LatLong> of possible parsings.
	 */
	private static ArrayList<LatLong> latThenLongParseHelper(String lat,
			String lon) {

		// Create the ArrayList<LatLong> to hold candidates
		ArrayList<LatLong> retval = new ArrayList<LatLong>();

		return retval;
	}

	/**
	 * parseNumber parses a String representing an angle.
	 * 
	 * @param numstr the String that represents the number to be parsed.
	 * 
	 * @return an ArrayList<DegMinSec> containing all possible parsings.
	 */
	private static ArrayList<DegMinSec> parseDirection(String numstr) {
		ArrayList<DegMinSec> retval = new ArrayList<DegMinSec>();
		numstr = numstr.trim();
		//first find the location of all number substrings
		Pattern numberPattern = 
				Pattern.compile("[([0-9]*[.]?[0-9]+)([0-9]+[.]?[0-9]*)]");
		Matcher numberMatch = numberPattern.matcher(numstr);
		boolean somethingGotSet;
		while (numberMatch.find()) {
			ArrayList<DegMinSec> blah;
			somethingGotSet = false;
			for (String s: DEGREEMARKERS) {
				if (s.equals(
						numstr.substring(numberMatch.end(), numberMatch.end()+1))) {
					if (somethingGotSet) {
						//If something did get set beforehand with this number
						//then this number could represent multiple different numbers
						//and we can't be sure what the heck is going on!
					}
					try {
						retval.setDeg(Double.parseDouble(numberMatch.group()));
						retval.isValid = true;
						somethingGotSet = true;
					} catch (NumberFormatException e) {}
				}
			}
			for (String s: MINUTEMARKERS) {
				if (s.equals(
						numstr.substring(numberMatch.end(), numberMatch.end()+1))) {
					try {
						retval.setMin(Double.parseDouble(numberMatch.group()));
						retval.isValid = true;
						somethingGotSet = true;
					} catch (NumberFormatException e) {}
				}
			}
			for (String s: SECONDMARKERS) {
				if (s.equals(
						numstr.substring(numberMatch.end(), numberMatch.end()+1))) {
					try {
						retval.setSec(Double.parseDouble(numberMatch.group()));
						retval.isValid = true;
						somethingGotSet = true;
					} catch (NumberFormatException e) {}
				}
			}
			
		}	
		
		
		return retval;
	}

	private static void removeDuplicateLatLongs(ArrayList<LatLong> al) {
		for (int i = 0; i < al.size(); i++) {
			int j = i + 1;
			while (j < al.size()) {
				if (al.get(i).equals(al.get(j))) {
					al.remove(j);
				} else {
					j++;
				}
			}
		}
	}
}

class DegMinSec {
	private double degree;
	private double minute;
	private double second;
	
	DegMinSec () {
		degree = 0;
		minute = 0;
		second = 0;
	}
	
	DegMinSec copy() {
		DegMinSec retval = new DegMinSec();
		retval.degree = this.degree;
		retval.minute = this.minute;
		retval.second = this.second;
		return retval;
		//retval. = this.;
	}
	
	void setDeg(double deg) {
		degree = deg;
	}
	
	void setMin(double min) {
		minute = min;
	}
	
	void setSec(double sec) {
		second = sec;
	}
	
	double getDeg() {
		return degree;
	}
	
	double getMin() {
		return minute;
	}
	
	double getSec() {
		return second;
	}
}

class TwoDirsAndMags {
	enum Direction {
		NORTH, SOUTH, EAST, WEST
	};

	Direction direction1;
	String magstr1;
	double magnitude1;
	Direction direction2;
	String magstr2;
	double magnitude2;

	TwoDirsAndMags() {
		direction1 = null;
		direction2 = null;
		magstr1 = null;
		magstr2 = null;
	}

	boolean setDirection1(String s) {
		if (s.equals("n")) {
			direction1 = Direction.NORTH;
		} else if (s.equals("s")) {
			direction1 = Direction.SOUTH;
		} else if (s.equals("e")) {
			direction1 = Direction.EAST;
		} else if (s.equals("w")) {
			direction1 = Direction.WEST;
		} else {
			return false;
		}
		return true;
	}

	String getDirection1() {
		if (direction1 == Direction.NORTH) {
			return "n";
		} else if (direction1 == Direction.SOUTH) {
			return "s";
		} else if (direction1 == Direction.EAST) {
			return "e";
		} else if (direction1 == Direction.WEST) {
			return "w";
		} else {
			return null;
		}
	}

	boolean setDirection2(String s) {
		if (s.equals("n")) {
			direction2 = Direction.NORTH;
		} else if (s.equals("s")) {
			direction2 = Direction.SOUTH;
		} else if (s.equals("e")) {
			direction2 = Direction.EAST;
		} else if (s.equals("w")) {
			direction2 = Direction.WEST;
		} else {
			return false;
		}
		return true;
	}

	String getDirection2() {
		if (direction2 == Direction.NORTH) {
			return "n";
		} else if (direction2 == Direction.SOUTH) {
			return "s";
		} else if (direction2 == Direction.EAST) {
			return "e";
		} else if (direction2 == Direction.WEST) {
			return "w";
		} else {
			return null;
		}
	}
}

class LatLong {

	Latitude latitude;
	Longitude longitude;

	/**
	 * 
	 * 
	 * 
	 */

	LatLong() {
		latitude = new Latitude();
		longitude = new Longitude();
	}

	LatLong(Latitude lat, Longitude lon) {
		latitude = lat;
		longitude = lon;
	}

	LatLong(Longitude lon, Latitude lat) {
		longitude = lon;
		latitude = lat;
	}

	boolean equals(LatLong that) {
		if (this.latitude.equals(that.latitude)
				&& this.longitude.equals(that.latitude)) {
			return true;
		}
		return false;
	}

	public double getLatDeg() {
		return latitude.degree;
	}

	public double getLatMin() {
		return latitude.minute;
	}

	public double getLatSec() {
		return latitude.second;
	}

	public double getLongDeg() {
		return longitude.degree;
	}

	public double getLongMin() {
		return longitude.minute;
	}

	public double getLongSec() {
		return longitude.second;
	}

	// using all info from latitude, return decimal degree
	// this assumes at least degree is defined
	public double getLatDec() {
		double retval = latitude.degree;
		if (latitude.minuteDefined) {
			retval += (latitude.minute / 60);
			if (latitude.secondDefined) {
				retval += (latitude.second / 3600);
			}
		}
		return retval;
	}

	// using all info from longitude, return decimal degree
	// this assumes at least degree is defined
	public double getLongDec() {
		double retval = longitude.degree;
		if (longitude.minuteDefined) {
			retval += (longitude.minute / 60);
			if (longitude.secondDefined) {
				retval += (longitude.second / 3600);
			}
		}
		return retval;
	}

	public double getLatLongUncertainty() {
		// John W. will fill this in.
		return 0.0;
	}

}

class Latitude {

	// Latitude variables
	double degree;
	boolean degreeDefined;
	double minute;
	boolean minuteDefined;
	double second;
	boolean secondDefined;

	Latitude() {
		degreeDefined = false;
		minuteDefined = false;
		secondDefined = false;
	}

	Latitude(double d) {
		degree = d;
		degreeDefined = true;
		minuteDefined = false;
		secondDefined = false;
	}

	Latitude(double d, double m) {
		this(d);
		minute = m;
		minuteDefined = true;
		secondDefined = false;
	}

	Latitude(double d, double m, double s) {
		this(d, m);
		second = s;
		secondDefined = true;
	}

	boolean equals(Latitude that) {
		if (this.degreeDefined != that.degreeDefined) {
			return false;
		}
		if (this.degreeDefined) {
			if (this.degree != that.degree) {
				return false;
			}
		}
		if (this.minuteDefined != that.minuteDefined) {
			return false;
		}
		if (this.minuteDefined) {
			if (this.minute != that.minute) {
				return false;
			}
		}
		if (this.secondDefined != that.secondDefined) {
			return false;
		}
		if (this.secondDefined) {
			if (this.second != that.second) {
				return false;
			}
		}
		return true;
	}

}

class Longitude {

	// Longitude variables
	double degree;
	boolean degreeDefined;
	double minute;
	boolean minuteDefined;
	double second;
	boolean secondDefined;

	Longitude() {
		degreeDefined = false;
		minuteDefined = false;
		secondDefined = false;
	}

	Longitude(double d) {
		degree = d;
		degreeDefined = true;
		minuteDefined = false;
		secondDefined = false;
	}

	Longitude(double d, double m) {
		this(d);
		minute = m;
		minuteDefined = true;
		secondDefined = false;
	}

	Longitude(double d, double m, double s) {
		this(d, m);
		second = s;
		secondDefined = true;
	}

	boolean equals(Longitude that) {
		if (this.degreeDefined != that.degreeDefined) {
			return false;
		}
		if (this.degreeDefined) {
			if (this.degree != that.degree) {
				return false;
			}
		}
		if (this.minuteDefined != that.minuteDefined) {
			return false;
		}
		if (this.minuteDefined) {
			if (this.minute != that.minute) {
				return false;
			}
		}
		if (this.secondDefined != that.secondDefined) {
			return false;
		}
		if (this.secondDefined) {
			if (this.second != that.second) {
				return false;
			}
		}
		return true;
	}

}