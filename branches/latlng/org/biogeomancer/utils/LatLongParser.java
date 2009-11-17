/**
 * Copyright 2007 University of California at Berkeley.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.biogeomancer.utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class LatLongParser {
  static final Pattern DEGREEPATTERN = Pattern.compile("[Dd\u00b0]");
  static final String A_LATLONGSUFFIX = ")(?=.*?[0-9]?)";
  static final String A_LATLONGPREFIX = "(?<=[^A-Za-z]";

  static final Pattern LATITUDEPATTERN = Pattern.compile(A_LATLONGPREFIX
      + "[NnSs]" + A_LATLONGSUFFIX);
  static final Pattern LONGITUDEPATTERN = Pattern.compile(A_LATLONGPREFIX
      + "[EeWW]" + A_LATLONGSUFFIX);

  static final Pattern MINUTEPATTERN = Pattern.compile("[Mm']");
  static final Pattern SECONDPATTERN = Pattern.compile("[Ss\"]");

  public static void main(String args[]) {

  }

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
     * retval.addAll(latAndLongParseHelper(tempStrArray[0], tempStrArray[1])); }
     */

    // Try to split around any sort of direction marking
    // Then send all matches to the parser.
    ArrayList<LatStrLonStr> tempStrAL = splitByDirection(s);
    for (int i = 0; i < tempStrAL.size(); i++) {
      retval.addAll(latThenLongParseHelper(tempStrAL.get(i).latitude, tempStrAL
          .get(i).longitude));
    }

    return retval;
  }

  /**
   * latAndLongParseHelper takes in two Strings, one of which is latitude and
   * the other of which is longitude. However, it is not known at this point
   * which String is latitude and which String is longitude.
   * 
   * @param s1
   *          either a latitude String or a longitude String, but not the same
   *          type as s2.
   * @param s2
   *          either a latitude String or a longitude String, but not the same
   *          type as s1.
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
   * latThenLongParseHelper takes in two Strings, the first of which is latitude
   * and the second of which is longitude. However, it is not known at this
   * point what the numeric values are.
   * 
   * @param s1
   *          either a latitude String or a longitude String, but not the same
   *          type as s2.
   * @param s2
   *          either a latitude String or a longitude String, but not the same
   *          type as s1.
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
   * @param numstr
   *          the String that represents the number to be parsed.
   * 
   * This is done for now, however it will need much fixing.
   * 
   * @return an ArrayList<DegMinSec> containing all possible parsings.
   */
  private static ArrayList<DegMinSec> parseDirection(String numstr) {
    ArrayList<DegMinSec> retval = new ArrayList<DegMinSec>();
    numstr = numstr.trim();
    // first find the location of all number substrings
    Pattern numberPattern = Pattern
        .compile("[([0-9]*[.]?[0-9]+)([0-9]+[.]?[0-9]*)]");
    Matcher numberMatch = numberPattern.matcher(numstr);
    boolean somethingGotSet;
    while (numberMatch.find()) {
      ArrayList<DegMinSec> resultSet = new ArrayList<DegMinSec>();
      DegMinSec temp = new DegMinSec();
      somethingGotSet = false;

      // TODO: Fix this. Can't interate over a Pattern!
      // for (String s : DEGREEPATTERN) {
      // if (s.equals(numstr.substring(numberMatch.end(), numberMatch
      // .end() + 1))) {
      // try {
      // temp.setDeg(Double.parseDouble(numberMatch.group()));
      // temp.isValid = true;
      // somethingGotSet = true;
      // } catch (NumberFormatException e) {
      // }
      // }
      // }

      // // TODO: Fix this. Can't interate over a Pattern!
      // for (String s : MINUTEPATTERN) {
      // if (s.equals(numstr.substring(numberMatch.end(), numberMatch
      // .end() + 1))) {
      // if (somethingGotSet) {
      // // If something did get set beforehand with this number
      // // then this number could represent multiple different
      // // numbers so we must copy this over to more
      // // possibilities
      // }
      // try {
      // temp.setMin(Double.parseDouble(numberMatch.group()));
      // temp.isValid = true;
      // somethingGotSet = true;
      // } catch (NumberFormatException e) {
      // }
      // }
      // }

      // // TODO: Fix this. Can't interate over a Pattern!
      // for (String s : SECONDPATTERN) {
      // if (s.equals(numstr.substring(numberMatch.end(), numberMatch
      // .end() + 1))) {
      // try {
      // temp.setSec(Double.parseDouble(numberMatch.group()));
      // temp.isValid = true;
      // somethingGotSet = true;
      // } catch (NumberFormatException e) {
      // }
      // }
      // }
      if (temp.isValid) {
        resultSet.add(temp);
      }
      if (!somethingGotSet) {

      }
      retval.addAll(resultSet);
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

  // Takes a single string that has lat and lon in it
  // and attempts to split it into lat and lon Strings
  private static ArrayList<LatStrLonStr> splitByDirection(String s) {
    LatStrLonStr temp;
    ArrayList<LatStrLonStr> retval = new ArrayList<LatStrLonStr>();
    Matcher match;
    match = LATITUDEPATTERN.matcher(s);
    while (match.find()) {
      // eventually add in code that only adds temp to retval if each part
      // has a number in it
      temp = new LatStrLonStr();
      temp.latitude = s.substring(0, match.end());
      temp.longitude = s.substring(match.end());
      retval.add(temp);
    }
    match = LONGITUDEPATTERN.matcher(s);
    while (match.find()) {
      temp = new LatStrLonStr();
      temp.longitude = s.substring(0, match.end());
      temp.longitude = s.substring(match.end());
      retval.add(temp);
    }
    return retval;

    /*
     * ArrayList<String[]> matches = new ArrayList<String[]>();
     * 
     * String[] latitudeSplitters = {"N","n","S","s"}; String[]
     * longitudeSplitters = {"E","e","W","w"}; TwoDirsAndMags dirsmags = new
     * TwoDirsAndMags(); String[] temp1; String[] temp2; for (int i = 0; i <
     * latitudeSplitters.length; i++) { temp1 = s.split("(?<![A-Za-z])" +
     * latitudeSplitters[i]); if (temp1.length == 2 || temp1.length == 1) { if
     * (latitudeSplitters[i].equals("N") || latitudeSplitters[i].equals("n")) {
     * dirsmags.direction1 = TwoDirsAndMags.Direction.NORTH; } else if
     * (latitudeSplitters[i].equals("S") || latitudeSplitters[i].equals("s")) {
     * dirsmags.direction1 = TwoDirsAndMags.Direction.SOUTH; } } for (int j = 0;
     * j < longitudeSplitters.length; j++) { for (int k = 0; k < temp1.length;
     * k++) { temp2 = temp1[k].split("(?<![A-Za-z])" + longitudeSplitters[i]);
     * if (temp2.length == 2 || temp2.length == 1) { if
     * (latitudeSplitters[i].equals("E") || latitudeSplitters[i].equals("e")) {
     * dirsmags.direction2 = TwoDirsAndMags.Direction.EAST; } else if
     * (latitudeSplitters[i].equals("W") || latitudeSplitters[i].equals("w")) {
     * dirsmags.direction2 = TwoDirsAndMags.Direction.WEST; } } } } } if
     * ((dirsmags.direction1 == TwoDirsAndMags.Direction.NORTH ||
     * dirsmags.direction1 == TwoDirsAndMags.Direction.SOUTH) &&
     * (dirsmags.direction2 != TwoDirsAndMags.Direction.EAST &&
     * dirsmags.direction2 != TwoDirsAndMags.Direction.EAST)) { double temp = }
     * return dirsmags;
     */
  }
}

class DegMinSec {
  private double degree;
  private double minute;
  private double second;
  boolean isValid;

  DegMinSec() {
    degree = 0;
    minute = 0;
    second = 0;
    isValid = false;
  }

  DegMinSec copy() {
    DegMinSec retval = new DegMinSec();
    retval.degree = this.degree;
    retval.minute = this.minute;
    retval.second = this.second;
    retval.isValid = this.isValid;
    return retval;
    // retval. = this.;
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

  void setDeg(double deg) {
    degree = deg;
  }

  void setMin(double min) {
    minute = min;
  }

  void setSec(double sec) {
    second = sec;
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

  public double getLatDeg() {
    return latitude.degree;
  }

  public double getLatLongUncertainty() {
    // John W. will fill this in.
    return 0.0;
  }

  public double getLatMin() {
    return latitude.minute;
  }

  public double getLatSec() {
    return latitude.second;
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

  public double getLongDeg() {
    return longitude.degree;
  }

  public double getLongMin() {
    return longitude.minute;
  }

  public double getLongSec() {
    return longitude.second;
  }

  boolean equals(LatLong that) {
    if (this.latitude.equals(that.latitude)
        && this.longitude.equals(that.latitude)) {
      return true;
    }
    return false;
  }

}

class LatStrLonStr {
  String latitude;
  String longitude;
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

class TwoDirsAndMags {
  enum Direction {
    EAST, NORTH, SOUTH, WEST
  };

  Direction direction1;
  Direction direction2;
  double magnitude1;
  double magnitude2;
  String magstr1;
  String magstr2;

  TwoDirsAndMags() {
    direction1 = null;
    direction2 = null;
    magstr1 = null;
    magstr2 = null;
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
}