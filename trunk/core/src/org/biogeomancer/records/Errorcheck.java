/***
Errorcheck.java
Created 2005-June-14
BioGeomancer Project

This class defines a record for Error checking results


***/

package org.biogeomancer.records;


public class Errorcheck {
// basic alt data
    public int mapalt;                  // altitude of point on map
    public int mapaltmin_uncertainty;   // min alt based on uncertainty radius
    public int mapaltmax_uncertainty;   // max of same
    public int mapaltmin_neighborhoods; // min alt based on neighborhood (9 cells)
    public int mapaltmax_neighborhoods; // max of same

// alt test results
    public float dif;                   // dif between mapalt and user provided alt (alt_collect)
    public boolean inaltrange;          // is alt_colect with range of mapaltmin & mapaltmax

// admin boundaries checking
// all arrays: 0 = country; 1 = adm1; 2 = adm2; 3 = adm3; 4 = adm4 

    public String adm[] = new String[5]; // the adm name observed

    public boolean inadm[] = new boolean[5]; // is the point inside the specified adm?

    public boolean indatabaseatthislevel[] = new boolean[5]; // is the admin name known at this level?
    public float disttoadm[] = new float[5]; // how far from the specified adm? 

    public boolean indatabase[] = new boolean[5]; // is the admin name known at any level?
    public boolean inotherleveladm[] = new boolean[5]; // is the point inside the specified adm (at different level)?

    public float disttootherleveladm[] = new float[5]; // how far from the specified adm (at different level)? 


}
