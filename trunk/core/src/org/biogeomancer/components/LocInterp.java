/***
LocInterp.java
created 2005-april-18
biogeomancer project
***/
package org.biogeomancer.components;
//import bg.edu.illinois.*;
//import bg.edu.tulane.*;
//import bg.edu.yale.*;
import java.util.*;

import org.biogeomancer.managers.*;
import org.biogeomancer.records.*;
import org.biogeomancer.utils.*;

public class LocInterp implements ComponentInterface{
    /***
    this is the locality interpretation component. it gets jobs
    from the georeference manager and completes an interpretation
    for each georeference in each record of the record set.
    ***/
    public Sys sys;
    public Rec r;
    //public GeorefInterp gi;
    public List ginterps;
    public Iterator recs;
    public HashMap resrcs, events;
    public Set ekeys;
    public LocInterp(HashMap resrcs){
	sys = new Sys();
	resrcs = resrcs;
        events = (HashMap)resrcs.get("events");}
    public RecSet execute(RecSet recset, ManagerInterface managers){
	/*** CODE DOESN'T WORK... needs an update
	recs = recset.recs.listIterator(0);
	ekeys = events.keySet();
	while(recs.hasNext()){
	    r = (Rec)recs.next();
	    if(ekeys.contains(r)){
		sys.print("SKIP.LI\trecord " + r.locality + " in events dictionary");
		continue;}
	    r.ginterps = new LinkedList();
	    //illinois.parse(r);
	    //tulane.parse(r);
	    //yale.parse(r);
	    gi = new GeorefInterp();
	    r.ginterps.add(gi);}
	***/
	return recset;}
}
