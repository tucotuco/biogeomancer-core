/***
UncertCalc.java
Created 2005-april-14
BioGeomancer Project
***/
package org.biogeomancer.components;
//import bg.edu.berkeley.*;
import java.util.*;

import org.biogeomancer.managers.*;
import org.biogeomancer.records.*;
import org.biogeomancer.utils.*;

public class UncertCalc implements ComponentInterface{
    /***
    this is the uncert calculation component. it's managed 
    by the georef manager.
    ***/
    public Sys sys;
    Rec r;
    UncertCalc u;
    Iterator recs, ginterps, sdescrips;
    //GeorefInterp gi;
    HashMap resrcs, events;
    Set ekeys;

/* JRW adding local variables for uncertainty calculations. Uncertainty calculations will be made based on LocalityTypes.
   The LocalityTypes for which uncertainties can currently be be calculated are marked with a preceeding asterix in the list
   below. The latest full set of locality types can be found on the Developer's Portal in the Locality Types forum.

LocalityType Dictionary
------------------
Code	Description
+2P	orthogonal offsets from two paths
ADDR	street address
*ADM	administrative unit
BF	between features
BP	between paths
E	elevation
*F	feature
FH	heading from a feature, no offset
*FO	offset from a feature, no heading
*FO+	orthogonal offsets from a feature
*FOH	offset from a feature at a heading
FPH	heading from a feature along a path, no offset
FPO	offset from feature along a path, no heading
*FPOH	offset from feature at a heading along a path
FS	subdivision of a feature
J	junction
JOH	offset at a heading from a junction
*LL	latitude and longitude coordinates
NF	near a feature
NJ	near a junction
NN	specific locality not known
NP	near a path
OGS	other grid system
P	path or linear feature
PBF	on a path between features
PH	at a heading from a path
PO	offset along a path, no heading or feature
POH	offset at a heading from a path
POM	offset marker
PS	subdivision of a path
TRS	PLSS descriptions, Township Range Section
TRSS	subdivision of a PLSS section
UNK	undefined
*UTM	Universal Transverse Mercator coordinates   

The following parameters (the "basic set") are needed for all uncertainty calculations:
LocalityType
CoordinateSourceType
MapSourceName
OriginalCoordinateSystem
DecimalLatitude
DecimalLongitude
OriginalLatitude
OriginalLongitude
Datum
CoordinatePrecision
DistanceUnits

The list of parameters needed in addition to the basic set for each of the supported locality types is given below:
ADM:  Extent
F:    Extent
FO:   Extent, Offset, OffsetPrecision
FO+:  Extent, OffsetNorS, OffsetEorW, HeadingNS, HeadingEW, OffsetPrecision
FOH:  Extent, Offset, OffsetPrecision, Heading
FPOH: Extent, DistancePrecision
LL:   basic set only
UTM:  basic set only

Some of the parameters must comply with dictionary entries. If a value is not found in a dictionary, it must be added.
Following are dictionary values for parameters that need them.

CoordinateSourceType Dictionary
---------------------------
locality description
map
GPS

MapSourceName Dictionary
-------------------------------
This dictionary will have map names and their mapscale uncertainties in meters. An administrative interface to manage
map information will be useful for discovering new maps and entering them in the dictionary.

OriginalCoordinateSystem Dictionary
-----------------------------------
decimal degrees
degrees minutes seconds
degrees decimal minutes
UTM

Datum Dictionary
----------------
Get this from libproj4

CoordinatePrecision Dictionary
------------------------------
system, precision

DistanceUnits Dictionary
------------------------
km
m
mi
yds
ft
(alternates for the above)

Heading Dictionary
------------------
N, E, S, W, NE, NW, SE, SW, NNE, NNW, ENE, WNW, SSE, SSW, ESE, WSW or number between 0 and 360.

OffsetPrecision needs to be calculated from an algorithm based on the values in the DistanceUnits and the Offset.

*/
  
    

    public UncertCalc(HashMap resrcs){
	sys = new Sys();
	resrcs = resrcs;
	events = (HashMap)resrcs.get("events");}
    public RecSet execute(RecSet recset, ManagerInterface managers){
	/***
	This code doesn't work... need to update.
	calculate uncertainty for each record's spatial description.
	
	recs = recset.recs.listIterator(0);
	ekeys = events.keySet();
	while(recs.hasNext()){
	    r = (Rec)recs.next();
		    if(ekeys.contains(r)){
		sys.print("SKIP.UC\trecord " + r.locality + " in events dictionary");
		continue;}
	    ginterps = r.ginterps.listIterator(0);
	    while(ginterps.hasNext()){
		gi = (GeorefInterp)ginterps.next();
		sdescrips = gi.sdescrips.listIterator(0);
		while(sdescrips.hasNext()){
		    d = (Description)sdescrips.next();
		    d.uncertainty = new Uncertainty();
		    // berkeley.calculate(d);
		    d.uncertainty.value = 77.0;}}}
	***/
	return recset;}
}
