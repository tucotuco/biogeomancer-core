// /**
// *@ LatLongParser.java
// *
// *The main purpose of this code is to take a String with latitude and
// *longitude or two Strings, one of which is latitude and one of which is
// longitude,
// *and return an array of LatLong objects with possible parsings.
// *
// *@version alpha, untested
// *
// *@author Peter DeVore
// *@email pdevore AT berkeley dot edu
// */
// package org.biogeomancer.utils.latlong;
//
// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.regex.*;
//
// public abstract class LatLongParser {
//
// private static final String LATITUDEMARKERS = "NnSs";
// private static final String LONGITUDEMARKERS = "EeWW";
// private static final String LATLONGMARKERS = LATITUDEMARKERS
// + LONGITUDEMARKERS;
// private static final Pattern LATITUDEPATTERN = Pattern.compile("["
// + LATITUDEMARKERS + "]");
// private static final Pattern LONGITUDEPATTERN = Pattern.compile("["
// + LONGITUDEMARKERS + "]");
// private static final Pattern LATLONGPATTERN = Pattern.compile("["
// + LATLONGMARKERS + "]");
// private static final Pattern DEGREEPATTERN = Pattern.compile("[Dd\u00b0]");
// private static final Pattern MINUTEPATTERN = Pattern.compile("[Mm']");
// private static final Pattern SECONDPATTERN = Pattern.compile("[Ss\"]");
// private static final Pattern NUMBERPATTERN = Pattern
// .compile("[([0-9]+([.][0-9]+)?)([0-9]*[.][0-9]+)]");
//
// public static LatAndLong[] parse(String s) {
// ArrayList<LatAndLong> temp = latAndLongParseHelper(s);
// removeDuplicateLatLongs(temp);
// return (LatAndLong[]) (temp.toArray());
// }
//
// public static LatAndLong[] parse(String s1, String s2) {
// ArrayList<LatAndLong> temp = latAndLongParseHelper(s1, s2);
// removeDuplicateLatLongs(temp);
// return (LatAndLong[]) (temp.toArray());
// }
//
// private static ArrayList<LatAndLong> latAndLongParseHelper(String s) {
// // The task in here is to split up the string into two strings to pass
// // into parse(String, String).
// // It is possible that parse(String) will call parse(String, String)
// // for several different String splitting possibilities.
//
// // Create the ArrayList<LatLong> to hold candidates
// ArrayList<LatAndLong> retval = new ArrayList<LatAndLong>();
//		
// // Now we try our first test case.
// // 10D 36M N, 95D 39M E
// Pattern p1 =
// Pattern.compile("[0-9]+[Dd\u00b0]\\s*[0-9]+[Mm']\\s*[NnSsEeWw],[0-9]+[Dd\u00b0]\\s*[0-9]+[Mm']\\s*[NnSsEeWw]");
// Matcher m1 = p1.matcher(s);
// Information info = new Information();
// LatAndLong latlong = new LatAndLong();
// String[] latlongstr;
// if (m1.matches()) {
// latlongstr = s.split(",");
// // search for direction marker in each one
// String direction0 =
// Pattern.compile("[NnSsEeWw]").matcher(latlongstr[0]).group();
// String direction1 =
// Pattern.compile("[NnSsEeWw]").matcher(latlongstr[1]).group();
// if (direction0 != null && direction1 != null) {
// if (direction0.equals("w") || direction0.equals("W")
// || direction0.equals("e") || direction0.equals("E") &&
// direction1.equals("n") || direction1.equals("N")
// || direction1.equals("s") || direction0.equals("S")) {
// info.direction0 = direction0;
// info.direction0defined = true;
// info.latlong0 = "longitude";
// info.latlong0defined = true;
// // find degrees
// info.degmag0 =
// Integer.parseInt(Pattern.compile("[0-9]+(?=[Dd\u00b0])").matcher(latlongstr[0]).group());
// // find minutes
// info.minmag0 =
// Integer.parseInt(Pattern.compile("[0-9]+(?=[Mm'])").matcher(latlongstr[0]).group());
// info.direction1 = direction1;
// info.direction1defined = true;
// info.latlong1 = "latitude";
// info.latlong1defined = true;
// // find degrees
// info.degmag1 =
// Integer.parseInt(Pattern.compile("[0-9]+(?=[Dd\u00b0])").matcher(latlongstr[1]).group());
// // find minutes
// info.minmag1 =
// Integer.parseInt(Pattern.compile("[0-9]+(?=[Mm'])").matcher(latlongstr[1]).group());
// } else if (direction0.equals("n") || direction0.equals("N")
// || direction0.equals("s") || direction0.equals("S") &&
// direction0.equals("w") || direction0.equals("W")
// || direction0.equals("e") || direction0.equals("E")) {
// info.direction0 = direction0;
// info.direction0defined = true;
// info.latlong0 = "latitude";
// info.latlong0defined = true;
// // find degrees
// info.degmag0 =
// Double.parseDouble(Pattern.compile("[0-9]+(?=[Dd\u00b0])").matcher(latlongstr[0]).group());
// // find minutes
// info.minmag0 =
// Double.parseDouble(Pattern.compile("[0-9]+(?=[Mm'])").matcher(latlongstr[0]).group());
// info.direction1 = direction1;
// info.direction1defined = true;
// info.latlong1 = "longitude";
// info.latlong1defined = true;
// // find degrees
// info.degmag1 =
// Double.parseDouble(Pattern.compile("[0-9]+(?=[Dd\u00b0])").matcher(latlongstr[1]).group());
// // find minutes
// info.minmag1 =
// Double.parseDouble(Pattern.compile("[0-9]+(?=[Mm'])").matcher(latlongstr[1]).group());
// }
// }
// if (info.latlong0.equals("latitude") && info.latlong1.equals("longitude")) {
// latlong.setLongDeg(info.degmag1);
// latlong.setLatDeg(info.degmag0);
// latlong.setLongMin(info.minmag1);
// latlong.setLatMin(info.minmag0);
// } else if (info.latlong1.equals("latitude") &&
// info.latlong0.equals("longitude")) {
// latlong.setLongDeg(info.degmag0);
// latlong.setLatDeg(info.degmag1);
// latlong.setLongMin(info.minmag0);
// latlong.setLatMin(info.minmag1);
// }
// retval.add(latlong);
// }
//	
// // Probably a bad idea to split around spaces...
// /*
// * String[] tempStrArray = s.split(" "); if (tempStrArray.length == 2) {
// * retval.addAll(latAndLongParseHelper(tempStrArray[0],
// * tempStrArray[1])); }
// */
//
// // Try to split around any sort of direction marking
// // Then send all matches to the parser.
// /*ArrayList<LatStrLonStr> tempStrAL = splitByDirection(s);
// for (int i = 0; i < tempStrAL.size(); i++) {
// retval.addAll(latThenLongParseHelper(tempStrAL.get(i).latitude,
// tempStrAL.get(i).longitude));
// }*/
//
// return retval;
// }
//
// // Takes a single string that has lat and lon in it
// // and attempts to split it into lat and lon Strings
// private static ArrayList<LatStrLonStr> splitByDirection(String s) {
// LatStrLonStr temp;
// ArrayList<LatStrLonStr> retval = new ArrayList<LatStrLonStr>();
// Matcher match;
// match = LATITUDEPATTERN.matcher(s);
// while (match.find()) {
// temp = new LatStrLonStr();
// temp.latitude = s.substring(0, match.end());
// temp.longitude = s.substring(match.end());
// if (containsNum(temp.latitude) && containsNum(temp.longitude)) {
// retval.add(temp);
// }
// }
// match = LONGITUDEPATTERN.matcher(s);
// while (match.find()) {
// temp = new LatStrLonStr();
// temp.longitude = s.substring(0, match.end());
// temp.longitude = s.substring(match.end());
// if (containsNum(temp.latitude) && containsNum(temp.longitude)) {
// retval.add(temp);
// }
// }
// return retval;
// /*
// * ArrayList<String[]> matches = new ArrayList<String[]>();
// *
// * String[] latitudeSplitters = {"N","n","S","s"}; String[]
// * longitudeSplitters = {"E","e","W","w"}; TwoDirsAndMags dirsmags = new
// * TwoDirsAndMags(); String[] temp1; String[] temp2; for (int i = 0; i <
// * latitudeSplitters.length; i++) { temp1 = s.split("(?<![A-Za-z])" +
// * latitudeSplitters[i]); if (temp1.length == 2 || temp1.length == 1) {
// * if (latitudeSplitters[i].equals("N") ||
// * latitudeSplitters[i].equals("n")) { dirsmags.direction1 =
// * TwoDirsAndMags.Direction.NORTH; } else if
// * (latitudeSplitters[i].equals("S") ||
// * latitudeSplitters[i].equals("s")) { dirsmags.direction1 =
// * TwoDirsAndMags.Direction.SOUTH; } } for (int j = 0; j <
// * longitudeSplitters.length; j++) { for (int k = 0; k < temp1.length;
// * k++) { temp2 = temp1[k].split("(?<![A-Za-z])" +
// * longitudeSplitters[i]); if (temp2.length == 2 || temp2.length == 1) {
// * if (latitudeSplitters[i].equals("E") ||
// * latitudeSplitters[i].equals("e")) { dirsmags.direction2 =
// * TwoDirsAndMags.Direction.EAST; } else if
// * (latitudeSplitters[i].equals("W") ||
// * latitudeSplitters[i].equals("w")) { dirsmags.direction2 =
// * TwoDirsAndMags.Direction.WEST; } } } } } if ((dirsmags.direction1 ==
// * TwoDirsAndMags.Direction.NORTH || dirsmags.direction1 ==
// * TwoDirsAndMags.Direction.SOUTH) && (dirsmags.direction2 !=
// * TwoDirsAndMags.Direction.EAST && dirsmags.direction2 !=
// * TwoDirsAndMags.Direction.EAST)) { double temp = } return dirsmags;
// */
// }
//
// private static boolean containsNum(String s) {
// Matcher match = NUMBERPATTERN.matcher(s);
// if (match.find()) {
// return true;
// }
// return false;
// }
//
// /**
// * latAndLongParseHelper takes in two Strings, one of which is latitude and
// * the other of which is longitude. However, it is not known at this point
// * which String is latitude and which String is longitude.
// *
// * @param s1
// * either a latitude String or a longitude String, but not the
// * same type as s2.
// * @param s2
// * either a latitude String or a longitude String, but not the
// * same type as s1.
// * @return an ArrayList<LatLong> of possible parsings.
// */
// private static ArrayList<LatAndLong> latAndLongParseHelper(String s1, String
// s2) {
// // Create the ArrayList<LatLong> to hold candidates
// ArrayList<LatAndLong> retval = new ArrayList<LatAndLong>();
// // Try to figure out which is lat and which is long, then pass info to
// // latThenLongParseHelper
//
// return retval;
// }
//
// /**
// * latThenLongParseHelper takes in two Strings, the first of which is
// * latitude and the second of which is longitude. However, it is not known
// * at this point what the numeric values are.
// *
// * @param s1
// * either a latitude String or a longitude String, but not the
// * same type as s2.
// * @param s2
// * either a latitude String or a longitude String, but not the
// * same type as s1.
// * @return an ArrayList<LatLong> of possible parsings.
// */
// private static ArrayList<LatAndLong> latThenLongParseHelper(String lat,
// String lon) {
// // Create the ArrayList<LatLong> to hold candidates
// ArrayList<LatAndLong> retval = new ArrayList<LatAndLong>();
//
// return retval;
// }
//
// /**
// * parseDirection parses a String representing an angle with optional
// * direction marker.
// *
// * @param numstr
// * the String that represents the number to be parsed.
// *
// * This is done for now, however it will need much fixing.
// *
// * @return an ArrayList<DegMinSec> containing all possible parsings.
// */
// private static ArrayList<DegMinSec> parseLatOrLong(String numstr) {
// ArrayList<DegMinSec> retval = new ArrayList<DegMinSec>();
// numstr = numstr.trim();
// // first find a direction marker. if it exists, shed it off and
// // store the info.
//
// // first find the location of all number substrings
// // must match at least one digit or it isn't a number
// Matcher numberMatch = NUMBERPATTERN.matcher(numstr);
//		
// // new try: copy the string
// // try to match a number with optional marker to the copied string and
// // delete the used portion
// // do that for degree, minute, second
// boolean somethingGotSet;
// boolean areDone = false;
// /*
// * while (!areDone) { //ArrayList<DegMinSec> resultSet = new
// ArrayList<DegMinSec>();
// * DegMinSec temp = new DegMinSec(); somethingGotSet = false; Matcher
// * typeMatch = DEGREEPATTERN.matcher(numstr); while (typeMatch.find()) {
// *
// * if (s.equals(numstr.substring(numberMatch.end(), numberMatch .end() +
// * 1))) { try { temp.setDeg(Double.parseDouble(numberMatch.group()));
// * temp.isValid = true; somethingGotSet = true; } catch
// * (NumberFormatException e) { } } } for (String s : MINUTEPATTERN) { if
// * (s.equals(numstr.substring(numberMatch.end(), numberMatch .end() +
// * 1))) { if (somethingGotSet) { // If something did get set beforehand
// * with this number // then this number could represent multiple
// * different // numbers so we must copy this over to more //
// * possibilities } try {
// * temp.setMin(Double.parseDouble(numberMatch.group())); temp.isValid =
// * true; somethingGotSet = true; } catch (NumberFormatException e) { } } }
// * for (String s : SECONDPATTERN) { if
// * (s.equals(numstr.substring(numberMatch.end(), numberMatch .end() +
// * 1))) { try { temp.setSec(Double.parseDouble(numberMatch.group()));
// * temp.isValid = true; somethingGotSet = true; } catch
// * (NumberFormatException e) { } } } if (!somethingGotSet) { // Assume
// * by default its degrees, then parse it that way } if (temp.isValid) {
// * resultSet.add(temp); } retval.addAll(resultSet); }
// */
// return retval;
// }
//
// private static void removeDuplicateLatLongs(ArrayList<LatAndLong> al) {
// for (int i = 0; i < al.size(); i++) {
// int j = i + 1;
// while (j < al.size()) {
// if (al.get(i).equals(al.get(j))) {
// al.remove(j);
// } else {
// j++;
// }
// }
// }
// }
//
// public static void main(String args[]) {
// System.out.println("filler");
// }
//
// static class LatStrLonStr {
// String latitude;
// String longitude;
// }
//
// class DegMinSec {
// private double degree;
// private double minute;
// private double second;
// boolean degreeValid;
// boolean minuteValid;
// boolean secondValid;
// boolean isValid;
//
// DegMinSec() {
// degree = 0;
// minute = 0;
// second = 0;
// degreeValid = false;
// minuteValid = false;
// secondValid = false;
// isValid = false;
// }
//
// DegMinSec copy() {
// DegMinSec retval = new DegMinSec();
// retval.degree = this.degree;
// retval.minute = this.minute;
// retval.second = this.second;
// retval.degreeValid = this.degreeValid;
// retval.minuteValid = this.minuteValid;
// retval.secondValid = this.secondValid;
// retval.isValid = this.isValid;
// return retval;
// // retval. = this.;
// }
//
// void setDeg(double deg) {
// degree = deg;
// }
//
// void setMin(double min) {
// minute = min;
// }
//
// void setSec(double sec) {
// second = sec;
// }
//
// double getDeg() {
// return degree;
// }
//
// double getMin() {
// return minute;
// }
//
// double getSec() {
// return second;
// }
// }
//
// static class TwoDirsAndMags {
// enum Direction {
// NORTH, SOUTH, EAST, WEST
// };
//
// Direction direction1;
// String magstr1;
// double magnitude1;
// Direction direction2;
// String magstr2;
// double magnitude2;
//
// TwoDirsAndMags() {
// direction1 = null;
// direction2 = null;
// magstr1 = null;
// magstr2 = null;
// }
//
// boolean setDirection1(String s) {
// if (s.equals("n")) {
// direction1 = Direction.NORTH;
// } else if (s.equals("s")) {
// direction1 = Direction.SOUTH;
// } else if (s.equals("e")) {
// direction1 = Direction.EAST;
// } else if (s.equals("w")) {
// direction1 = Direction.WEST;
// } else {
// return false;
// }
// return true;
// }
//
// String getDirection1() {
// if (direction1 == Direction.NORTH) {
// return "n";
// } else if (direction1 == Direction.SOUTH) {
// return "s";
// } else if (direction1 == Direction.EAST) {
// return "e";
// } else if (direction1 == Direction.WEST) {
// return "w";
// } else {
// return null;
// }
// }
//
// boolean setDirection2(String s) {
// if (s.equals("n")) {
// direction2 = Direction.NORTH;
// } else if (s.equals("s")) {
// direction2 = Direction.SOUTH;
// } else if (s.equals("e")) {
// direction2 = Direction.EAST;
// } else if (s.equals("w")) {
// direction2 = Direction.WEST;
// } else {
// return false;
// }
// return true;
// }
//
// String getDirection2() {
// if (direction2 == Direction.NORTH) {
// return "n";
// } else if (direction2 == Direction.SOUTH) {
// return "s";
// } else if (direction2 == Direction.EAST) {
// return "e";
// } else if (direction2 == Direction.WEST) {
// return "w";
// } else {
// return null;
// }
// }
// }
//
// }
