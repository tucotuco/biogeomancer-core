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

package org.biogeomancer.components;

// import bg.edu.illinois.*;
// import bg.edu.tulane.*;
// import bg.edu.yale.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.biogeomancer.managers.ManagerInterface;
import org.biogeomancer.records.Rec;
import org.biogeomancer.records.RecSet;
import org.biogeomancer.utils.Sys;

public class LocInterp implements ComponentInterface {
  /*****************************************************************************
   * this is the locality interpretation component. it gets jobs from the
   * georeference manager and completes an interpretation for each georeference
   * in each record of the record set.
   ****************************************************************************/
  public Sys sys;
  public Rec r;
  // public GeorefInterp gi;
  public List ginterps;
  public Iterator recs;
  public HashMap resrcs, events;
  public Set ekeys;

  public LocInterp(HashMap resrcs) {
    sys = new Sys();
    resrcs = resrcs;
    events = (HashMap) resrcs.get("events");
  }

  public RecSet execute(RecSet recset, ManagerInterface managers) {
    /***************************************************************************
     * * CODE DOESN'T WORK... needs an update recs =
     * recset.recs.listIterator(0); ekeys = events.keySet();
     * while(recs.hasNext()){ r = (Rec)recs.next(); if(ekeys.contains(r)){
     * sys.print("SKIP.LI\trecord " + r.locality + " in events dictionary");
     * continue;} r.ginterps = new LinkedList(); //illinois.parse(r);
     * //tulane.parse(r); //yale.parse(r); gi = new GeorefInterp();
     * r.ginterps.add(gi);}
     **************************************************************************/
    return recset;
  }
}
