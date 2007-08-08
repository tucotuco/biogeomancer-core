//PointRadius.java
//created 2006-feb-4
//biogeomancer

package org.biogeomancer.utils;
import java.awt.geom.Point2D;
import java.util.*;

import org.biogeomancer.managers.DatumManager;
import org.biogeomancer.utils.*;


public class PointRadius extends Coordinate { // point radius inner class
	public double extent; // maximum uncertainty radius

//	public PointRadius() {}	// nothing
	private PointRadius(double lng, double lat) { // constructor
		super(lng, lat);
		this.extent = 0.0;
	}
	public PointRadius(double lng, double lat, double extent) { // constructor
		super(lng, lat);
		this.extent = extent;
		TruncateExtent();
	}
	public PointRadius(double lng, double lat, DatumManager.Datum datum) { // constructor
		super(lng, lat, datum);
		this.extent = 0.0;
	}
	public PointRadius(double lng, double lat, DatumManager.Datum datum, double precision) { // constructor
		super(lng, lat, datum, precision);
		this.extent = 0.0;
	} 
	public PointRadius(double lng, double lat, DatumManager.Datum datum, double precision, double extent) { // constructor
		super(lng, lat, datum, precision);
		this.extent = extent; 
		TruncateExtent();
	}
	public PointRadius( Coordinate c ) { // constructor
		super(c.x, c.y);
		this.datum = c.getDatum();
		this.precision = c.precision;
		this.extent = 0.0;
	}
	public PointRadius( Coordinate c, double extent ) { // constructor
		super(c.x, c.y);
		this.datum = c.getDatum();
		this.precision = c.precision;
		this.extent = extent;
		TruncateExtent();
	}
	public void TruncateExtent(){
		extent = (int)Math.floor(extent+0.5);
	}
	public boolean equals(PointRadius pr){
		if(this.x!=pr.x) return false;
		if(this.y!=pr.y) return false;
		if(this.extent!=pr.extent) return false;
		if(this.datum!=pr.datum) return false;
	 	return true;
	}
	public String toString(){
		return "<DecimalLatitude>"+y+"</DecimalLatitude>\n<DecimalLongitude>"+x+"</DecimalLongitude>\n<CoordinateUncertaintyInMeters>"+extent+"</CoordinateUncertaintyInMeters>\n<GeodeticDatum>"+datum.getName()+"</GeodeticDatum>";
	}
	public String toXML(){
		String s = new String("<POINT_RADIUS>\n");
		s=s.concat("<DecimalLatitude>"+y+"</DecimalLatitude>\n");
		s=s.concat("<DecimalLongitude>"+x+"</DecimalLongitude>\n");
		s=s.concat("<CoordinateUncertaintyInMeters>"+extent+"</CoordinateUncertaintyInMeters>\n");
		s=s.concat("<GeodeticDatum>"+datum.getName()+"</GeodeticDatum>\n");
		s=s.concat("</POINT_RADIUS>\n");
		return s;
	}
}