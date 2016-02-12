# Introduction #

We wish to parse latitude and longitude from String format to decimal format. There are two starting cases. One is two strings, one of which is Latitude, the other is Longitude (in some order). The other case is a string containing both Latitude and Longitude.

In order to make these two tasks into one task, we must join them together as they reach common cases.  However, we don't wish to waste the work we already did in finding some info out, so it should pass the extra information it found (perhaps we SHOULDN'T do that, for simplification's sake).

# Cases (conquer each case one at a time style) (easier!) #
In order to even start thing, you must do a regex matching to figure if it matches this case.  Thus, for each case, I will specify the regex necessary to match the case.
## Common cases! ##
### 10D  36M N, 95D  39M E ###
Regex:
```
"\d+[Dd\u00b0]\s*\d+[Mm']\s*[NnSsEeWw],\s*
\d+[Dd\u00b0]\s*\d+[Mm']\s*[NnSsEeWw]"
```
How to solve:
  * parse by "," into two directions.
  * search for direction marker in each one, i.e.
```
direction = regexFind("[NnSsEeWw]",string);
```
  * then you know what direction each on is.
  * then determine the degrees, i.e.
```
degrees = regexFind("beforeD",string);
```
  * determine the minutes, i.e.
```
minutes = regexFind("beforeM",string);
```
  * fill in the values
  * return them



# Cases (conquer all cases at once style) #

## C1 (Starting case) One string ##
```
public static LatLong[] parse(String s);
```

Goal: _determine how to separate the string_
  * PROBLEM: where to separate?
  * SOLUTION #1: separate by direction marker
    * even after separating by direction marker, check to make sure each String contains a number
      * we desire that the function that does the separating only return parsings where each string contains a number
      * since it determined which is latitude, which is longitude, it can then return what direction each is in
      * if it determines that, for example, latitude is negative, then it would return "South" for that direction
        * we could only parse that if the other direction was magnitude over 90 or had "east" or "west" direction
      * however, the direction would still be negative, so would it reverse the direction later? i say "no"
    * if successful, then goto: C3 with extra info: directions
  * Case: no direction markers: SOLUTION #2: use other separators
    * this technique will produce a success if:
      1. This results in two Strings ONLY
      1. Each string has at least one valid number in it
    * try comma first
    * MAYBE try space. problem is space can be fairly arbitrary.
    * if successful, then goto: C2.

## C2 (Starting case) Separate strings, latitude string is unknown ##
```
public static LatLong[] parse(String s1, String s2);
```


Goal: _determine which is which_
  * PROBLEM: these two strings are COUPLED, i.e. there is no function that can take in only one of the string and determine which is lat,which is long
  * SOLUTION #1: we use a function that only takes in one anyway and try to determine which is lat. this function will also return the direction and the original string.
    * this will search for direction markers, i.e. "NnSs" for latitude, "EeWw" for longitude
      * however, this must be the first or last character in the string, because we could find "e" in the middle of "west".
  * Case: cannot determine each one independently: SOLTUION #2: try solutions that involve both
    1. if one has magnitude greater than 90, then it must be longitude
    1. we default to the first string being latitude(?)
  * then we know: latitude, longitude, directions
  * goto: C3 with info: directions

## C3 Separate strings, latitude string is known ##
```
private static ArrayList<LatLong> latThenLongParseHelper(String lat, String lon);
```

Goal: _determine the magnitudes of each string_
  * PROBLEM: there are several different formats for the strings, which would lead to different numbers in the different parsings
    * SOLUTION #1: create a function that sorts out the different formats, using boolean functions, and applies the correct parser to each format
      * for example:
```
if (isDMS(string)) {
  parsings.addAll(parseDMSDirection(string));
}
if (isDecimal(string)) {
  parsings.addAll(parseDecimalDirection(string));
}
```

















