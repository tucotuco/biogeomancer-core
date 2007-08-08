/***
Georef.java
Created 2006-Feb-4
BioGeomancer Project
 ***/
package org.biogeomancer.records;
import java.util.*;

import org.biogeomancer.managers.DatumManager;
import org.biogeomancer.utils.PointRadius;
//import org.biogeomancer.utils.Coordinate;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKTReader;

/**
 * <p>A Georef is an instance of a spatial interpretation. It may be an interpretation for one Clause, or for the whole Rec.</p>
 */

//TODO: make nodecount configurable
//TODO: make PrecisionModel configurable
//TODO: refactor BGCoordinate to subclass JTS Coordinate
//TODO: Assure that SRIDs are used appropriately
//TODO: Implement polar boundary conditions
//TODO: fill out iLocality, uLocality and confidence

public class Georef {
	public GeorefState state;            // The processing state of the Georef
	public Metadata metadata;            // Object to track processing information, such as timestamps and methods.
	public String iLocality;             // The interpreted (and normalized to follow standard names and constructions) locality for a Clause or for a whole Rec. In the latter case all Clause.iLocality's get concatenated with a Clause delimiter.
	public String uLocality;             // The verbatim uninterpreted part of a locality, either for a Clause or a whole Rec, for which all Clause.uLocality's get concatenated with a Clause delimiter.
	public PointRadius pointRadius;      // The Point-Radius spatial interpretation of the Clause or Rec.
	public Geometry geometry;            // The Shape spatial interpretation of the Clause or Rec.
	public int confidence;               // A measure of confidence that the iLocality and its spatial description(s) represent the verbatim information provided in the original Rec.
	public int pointRadiusNodes;         // The number of nodes in the geometry representation of the pointRadius.
	public double pointRadiusSpatialFit; // A measure of how well the Point-Radius matches the original representation of the georeference.
	public double geometrySpatialFit;    // A measure of how well the geometry matches the original representation of the georeference.
	// Confidence can be done in a number of ways. Here are some things to consider: 
	//    1) Exact string matching between verbatim original and interpretation
	//    2) Clauses of information in the LocSpec that were uninterpretable.
	//    3) The number of putative Georefs produced for a Clause or Rec.
	//    4) The level ("equals", "has all words", "has any words", "has phrase", "matches pattern") of the gazetteer lookup required to return a matching feature.
	//    5) In the case of a Georef for a Rec, Whether the Clause were simultaneously true of serially true.
	//    6) If a feature extent has to be guessed, the confidence should be reduced.

//	public ArrayList<MetaData> metadata; // as an ArrayList, metadata could be an event capturing mechanism.
	public Map<String, Double> envData;  // Environmental data for validation

	public ArrayList<FeatureInfo> featureinfos; // a list of featureinfos used in the construction of this georef
	
//	public Georef() {
//	startup();
//	}

	public void addFeatureInfo(FeatureInfo f){
		FeatureInfo newfeature = new FeatureInfo(f);
		featureinfos.add(newfeature);
	}
	public static Georef makeOne(String geom){
		Georef g = null;
		try
		{
			WKTReader r = new WKTReader();
//			Geometry g1 = r.read("POLYGON((36 -122, 36 -120, 34 -120, 34 -122, 36 -122))");
			Geometry g1 = r.read(geom);
			g = new Georef(g1, DatumManager.getInstance().getDatum("WGS84"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return g;
	}
	public static void main(String[] args) {
		try {
/*
			Georef xyrg = new Georef(0,0,100);
			System.out.println("Georef from x,y,radius:\n"+xyrg.toString()+"\n");

			PointRadius pr = new PointRadius(180,0,100); // 100 meter circle around lng=-180, lat=0
			Georef prg = new Georef(pr);
			System.out.println("Georef from PointRadius:\n"+prg.toString()+"\n");

			GeometryFactory gf = new GeometryFactory();
			WKTReader wktreader = new WKTReader(gf);

//	        Geometry bbg = wktreader.read("POLYGON ((-179 1, 179 1, 179 -1, -179 -1, -179 1))");
//	    	Georef bbgg = new Georef(bbg);
//	    	System.out.println("Georef from BB Geometry:\n"+bbgg.toString()+"\n");
//			
//	        Geometry bbg = wktreader.read("POLYGON ((180 1, 179 1, 179 -1, 180 -1, 180 1))");
//	    	Geometry cg = wktreader.read("POLYGON ((-178 2, 178 2, 178 -2, -178 -2, -178 2))");
//	    	Geometry diff = gf.createGeometry(bbg.difference(cg));
//	    	Georef diffg = new Georef(diff);
//	    	System.out.println("Georef Geometry difference:\n"+diffg.toString()+"\n");
//	    	Coordinate c = diffg.getOverlappingCentroid(diff);
//	    	System.out.println("Centroid: "+c);

			Geometry concaveg = wktreader.read("POLYGON ((-178 2, 178 2, 180 1, -178.9 1, -178 0, -179 -1, 180 -1, 178 -2, -178 -2, -178 2))");
			Georef concavegg = new Georef(concaveg, DatumManager.getInstance().getDatum("WGS84"));
			System.out.println("Georef from concave Geometry:\n"+concavegg.toString()+"\n");
			org.biogeomancer.utils.Coordinate c = concavegg.getOverlappingCentroid(concaveg);
			System.out.println("Centroid: "+concaveg.getCentroid());
			System.out.println("Centroid (overlapping): "+c);

			org.biogeomancer.utils.Coordinate c1 = new org.biogeomancer.utils.Coordinate(-179.7, 0);
			org.biogeomancer.utils.Coordinate c2 = new org.biogeomancer.utils.Coordinate(179.9, 0);
			org.biogeomancer.utils.Coordinate mid = Georef.getMidPoint(c1, c2);
			System.out.println("Midpoint: "+mid.toString());

//	    	Geometry lsg = wktreader.read("LINESTRING (0 0, -1 1, 1 1, 1 -1, -1 -1)");
//	    	Georef lsgg = new Georef(lsg);
//	    	System.out.println("Georef from Line Geometry:\n"+lsgg.toString()+"\n");
*/
			try
			{
				WKTReader r = new WKTReader();
// This one doesn't work
				Geometry g1 = r.read("POLYGON((588912.7 2432104.5, 588927.7 2432124, 588928.9 2432123.6, 588923.7 2432132.3,588912.7 2432104.5))");
// This one works
//				Geometry g1 = r.read("POLYGON((588912.7 2432104.5, 588927.6 2432124, 588928.9 2432123.6, 588923.7 2432132.3,588912.7 2432104.5))");
				Geometry g2 = r.read("POLYGON((588916.4 2432124.4, 588920.7 2432129.9,588928.6 2432123.7,588930.1 2432122.6,588916.4 2432124.4))");
				System.out.println("isvalid(g1) = "+ g1.isValid());
				System.out.println("isvalid(g2) = "+ g2.isValid());
				Geometry g1ch = g1.convexHull();
				Geometry g4 = g2.intersection(g1ch);
				System.out.println("g4="+g4.toText());

				Geometry g3 = g2.intersection(g1);
//				Geometry g3 = g1.intersection(g2);
				System.out.println("g3="+g3.toText());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Georef(PointRadius pr, int nodes) {
		startup();
		this.pointRadius = makePointRadius(pr);
		this.geometry = makeGeometry(this.pointRadius, nodes);
		if( this.pointRadius != null){
			this.pointRadiusSpatialFit=1.0;
			if( this.geometry != null){
				double geometryarea = getGeometryArea(this.geometry);
				if( geometryarea > 0){
					double radiusinmeters = this.pointRadius.extent/this.pointRadius.getLatMetersPerDegree();
					double pointradiusarea = Math.PI*Math.pow(radiusinmeters, 2);
					this.geometrySpatialFit = geometryarea/pointradiusarea;
//					System.out.println("Radius: "+radiusinmeters+" Area: "+pointradiusarea+" G Area: "+geometryarea);
				}
			}
		}
		setState();
	}

	public Georef(PointRadius pr) {
		startup();
		this.pointRadius = makePointRadius(pr);
		this.geometry = makeGeometry(this.pointRadius, this.pointRadiusNodes);
		if( this.pointRadius != null){
			this.pointRadiusSpatialFit=1.0;
			if( this.geometry != null){
				double geometryarea = getGeometryArea(this.geometry);
				if( geometryarea > 0){
					double radiusinmeters = this.pointRadius.extent/this.pointRadius.getLatMetersPerDegree();
					double pointradiusarea = Math.PI*Math.pow(radiusinmeters, 2);
					this.geometrySpatialFit = geometryarea/pointradiusarea;
				}
			}
		}
		setState();
	}

	public Georef(double x, double y, double r){
		startup();
		this.pointRadius = makePointRadius(x,y,r);
		this.geometry = makeGeometry(this.pointRadius, this.pointRadiusNodes);
		if( this.pointRadius != null){
			this.pointRadiusSpatialFit=1.0;
			if( this.geometry != null){
				double geometryarea = getGeometryArea(this.geometry);
				if( geometryarea > 0){
					double radiusinmeters = this.pointRadius.extent/this.pointRadius.getLatMetersPerDegree();
					double pointradiusarea = Math.PI*Math.pow(radiusinmeters, 2);
					this.geometrySpatialFit = geometryarea/pointradiusarea;
				}
			}
		}
		setState();
	}

	public Georef(Geometry g, DatumManager.Datum datum){
		startup();
		this.geometry=makeGeometry(g);
		this.pointRadius=makePointRadius(g, datum);
		if( this.geometry != null){
			this.geometrySpatialFit = 1.0;
			double geometryarea = getGeometryArea(this.geometry);
			if( this.pointRadius != null){
				double radiusindegrees = this.pointRadius.extent/this.pointRadius.getLatMetersPerDegree();
				double pointradiusarea = Math.PI*Math.pow(radiusindegrees, 2);
				this.pointRadiusSpatialFit = pointradiusarea/geometryarea;
			}
		}
		setState();
	}
	public void startup(){
		this.state = GeorefState.GEOREF_CREATED;
		this.envData = new HashMap();
		this.featureinfos = new ArrayList<FeatureInfo>();
		this.confidence = -1;
		this.pointRadiusNodes = 32;
		this.pointRadiusSpatialFit = -1; // Use -1 for undefined
		this.geometrySpatialFit = -1; // Use -1 for undefined
	}
	public void setState(){
		if( this.pointRadius != null && this.geometry != null){
			this.state = GeorefState.GEOREF_COMPLETED;
			return;
		}
		if( this.pointRadius == null){
			this.state = GeorefState.GEOREF_POINTRADIUS_CREATION_ERROR;			
		} else {
			this.state = GeorefState.GEOREF_POINTRADIUS_CREATED;
		}
		if( geometry == null){
			this.state = GeorefState.GEOREF_GEOMETRY_CREATION_ERROR;			
		} else {
			this.state = GeorefState.GEOREF_GEOMETRY_CREATED;
		}
	}

	public PointRadius makePointRadius(PointRadius pr){
		PointRadius npr = (PointRadius) pr.clone();
		return npr;
	}
	public org.biogeomancer.utils.Coordinate getOverlappingCentroid(Geometry g){
		if( g == null ) return null;
		GeometryFactory gf = new GeometryFactory(g.getPrecisionModel(),g.getSRID());
		Geometry ng = (Geometry) g.clone();
		Coordinate[] coordinates = ng.getCoordinates();
		int nc = coordinates.length;
		double maxx = -180.0;
		double minx = 180.0;
		for(int i=0;i<nc;i++){
			if(coordinates[i].x > maxx) maxx = coordinates[i].x;
			if(coordinates[i].x < minx) minx = coordinates[i].x;
		}
		if( maxx > 90 && minx < -90){ // geometry crosses longitude = 180
			Georef.shiftLongitude(ng, 360.0);
		}
		Point p = ng.getCentroid();
		if(p==null) return null; 
		Point newp = (Point) p.clone();
		double mindist = 9E12;
		double maxdist = 0;
		if( newp.distance(ng) > 0 ){ // The centroid is not in the original geometry
			for(int i=0;i<nc;i++){
				Point tp = gf.createPoint(coordinates[i]);
				double ndist = tp.distance(p);
				if(ndist < mindist){
					mindist= ndist;
					newp = (Point) tp.clone(); // set newp to the Point in g nearest centroid of g.
				}
			}
			if( maxx > 90 && minx < -90){ // geometry crosses longitude = 180
				newp.getCoordinate().x=newp.getX()-360;
				if( newp.getCoordinate().x == -180) newp.getCoordinate().x = 180;
				if( newp.getCoordinate().y == -0 ) newp.getCoordinate().y = 0;
			}
		}
		// TODO: The following will make a coordinate with datum WGS84. Should test that the g.getSRID is WGS84 and transform it if it isn't.
		org.biogeomancer.utils.Coordinate c = new org.biogeomancer.utils.Coordinate(newp.getX(), newp.getY()); // Note that this will make coordinate with datum WGS84
		return c;
	}
	public static org.biogeomancer.utils.Coordinate getMidPoint(org.biogeomancer.utils.Coordinate g1, org.biogeomancer.utils.Coordinate g2){
		if( g1 == null || g2 == null) return null;
		double maxx = -180.0;
		double minx = 180.0;
		if(g1.x > maxx) maxx = g1.x;
		if(g2.x > maxx) maxx = g2.x;
		if(g1.x < minx) minx = g1.x;
		if(g2.x < minx) minx = g2.x;
		if( maxx > 90 && minx < -90){ // coordinates on opposite sides of longitude = 180
			if(g1.x<g2.x) g1.x+=360;
			else g2.x+=360;
		}
		double meanx=(g1.x+g2.x)/2;
		double meany=(g1.y+g2.y)/2;

		org.biogeomancer.utils.Coordinate midpointcoord = new org.biogeomancer.utils.Coordinate(meanx, meany, g1.datum);
		double latdist = midpointcoord.getLatDistanceInMetersToCoordinate(g1);
		double lngdist = midpointcoord.getLngDistanceInMetersToCoordinate(g1);
		double dist = Math.sqrt(Math.pow(latdist, 2) + Math.pow(lngdist, 2));
		midpointcoord.precision=dist;
		if( maxx > 90 && minx < -90){ // geometry crosses longitude = 180
			if(g1.x>g2.x) g1.x-=360;
			else g2.x-=360;
			if(midpointcoord.x>180) midpointcoord.x-=360;
		}
		return midpointcoord;
	}
	public double getGeometryArea(Geometry g){
		if( g == null ) return -1;
		Geometry ng = (Geometry) g.clone();
		Coordinate[] coordinates = ng.getCoordinates();
		int nc = coordinates.length;
		double maxx = -180.0;
		double minx = 180.0;
		for(int i=0;i<nc;i++){
			if(coordinates[i].x > maxx) maxx = coordinates[i].x;
			if(coordinates[i].x < minx) minx = coordinates[i].x;
		}
		if( maxx > 90 && minx < -90){ // geometry crosses longitude = 180
			Georef.shiftLongitude(ng, 360.0);
			return ng.getArea();
		}
		return g.getArea();
	}
	public PointRadius makePointRadius(Geometry g, DatumManager.Datum datum){
		if( g == null ) return null;
		if( g.getEnvelope() == null) return null;
		GeometryFactory gf = new GeometryFactory(g.getPrecisionModel(),g.getSRID());
		Geometry ng = (Geometry) g.clone();
		Coordinate[] coordinates = ng.getCoordinates();
		int nc = coordinates.length;
		double maxx = -180.0;
		double minx = 180.0;
		for(int i=0;i<nc;i++){
			if(coordinates[i].x > maxx) maxx = coordinates[i].x;
			if(coordinates[i].x < minx) minx = coordinates[i].x;
		}
		if( maxx > 90 && minx < -90){ // geometry crosses longitude = 180
			Georef.shiftLongitude(ng, 360.0);
		}
		Point p = ng.getCentroid();
		if(p==null){
			return null; // Can't make a point-radius if there is no centroid.
		}
		Point newp = (Point) p.clone();
		double mindist = 9E12;
		double maxdist = 0;
		if( newp.distance(ng) > 0 ){ // The centroid is not in the original geometry
			for(int i=0;i<nc;i++){
				Point tp = gf.createPoint(coordinates[i]);
				double ndist = tp.distance(p);
				if(ndist < mindist){
					mindist= ndist;
					newp = (Point) tp.clone(); // set newp to the Point in g nearest centroid of g.
				}
			}
		}
		PointRadius p1 = new PointRadius(newp.getX(), newp.getY(),1);
		for(int i=0;i<nc;i++){
			Point np = gf.createPoint(coordinates[i]);
			PointRadius p2 = new PointRadius(np.getX(), np.getY(),1);
			double longdist=p1.getLngDistanceInMetersToCoordinate(p2);
			double latdist=p1.getLatDistanceInMetersToCoordinate(p2);
			double distanceToNode = Math.sqrt(Math.pow(longdist,2)+Math.pow(latdist,2)); 
			if( distanceToNode > maxdist) maxdist = distanceToNode;
		}
		if(maxdist < 1){
			// TODO: Make sure the database has an i_feature_footprint.radius value >=1 for every feature
			// The value should be from the footprint if not a point
			// else it should be the best_guessuncert based on feature type
			// else it should be halfway to the nearest neighbor > 1000m away. 
//			return null; // can't make a point-radius without a radius
		}
		PointRadius pr = null;
		if( maxx > 90 && minx < -90){ // geometry crosses longitude = 180
			pr = new PointRadius(newp.getX()-360, newp.getY(), maxdist);
			if( pr.x == -180) pr.x = 180;
			if( pr.y == -0 ) pr.y = 0;
		} else{
			pr = new PointRadius(newp.getX(), newp.getY(), maxdist);
		}
		pr.datum=datum;
		return pr;
	}

	public PointRadius makePointRadius(double x, double y, double r){
		PointRadius npr = new PointRadius(x,y,r);
		return npr;
	}
	public Geometry makeGeometry(PointRadius pr, int nodecount){
		if( pr == null || nodecount < 4){
			this.geometry = null;
			this.pointRadiusNodes = 0;
			return null;
		}
		this.pointRadiusNodes = nodecount;
		double x = this.pointRadius.x;
		double y = this.pointRadius.y;
		double e = this.pointRadius.extent/Math.cos(Math.PI/nodecount); // extend the extent so that the geometric is a circumscription of the pointradius.
		Coordinate[] coordinates = new Coordinate[nodecount+1];
		coordinates[nodecount]=new Coordinate(x+e/pointRadius.getLatMetersPerDegree(), y);
		for(int i=0;i<nodecount;i++){
			double a = i*2*Math.PI/nodecount;
			coordinates[i]=new Coordinate(x+e*Math.cos(a)/pointRadius.getLatMetersPerDegree(), y+e*Math.sin(a)/pointRadius.getLatMetersPerDegree());
		} // now there are coordinates for a complete closed LinerRing approximating a circle
		LinearRing lr = new GeometryFactory().createLinearRing(coordinates);
		Polygon p = new GeometryFactory().createPolygon(lr,null);
		this.geometry = p;
		return this.geometry;
	}
	public Geometry makeGeometry(Geometry g){
		Geometry ng = (Geometry) g.clone();
		return ng;
	}

	public Georef intersect(Georef g) { // TODO: Figure out how confidence is affected by missing intersections
		// TODO: look at effect of PrecisionModels
		if( g == null || g.geometry == null) return this; // when intersecting with an unknown geometry, return the original
		if( g.geometry.isValid() == false) return this;
		// Try to make both Point-Radius and Polygon intersections and determine their spatialFits.
		// TODO: Can't do this unless the coordinates are in the same projection. If they aren't reproject.
		if(this.pointRadius.datum.getName().equalsIgnoreCase(g.pointRadius.datum.getName())==false){
			// transform both to WGS84
			// Lat/Longs and shapes from Gazetteer are all supposed to be projected in WGS84,
			// so this isn't supposed to be necessary.
		}
		Geometry g1 = (Geometry) this.geometry.clone();
		Geometry g2 = (Geometry) g.geometry.clone();
//		TODO: Check what happens if the geometries cross the meridian at longitude=180
		Georef.shiftLongitude(g1,360.0);
		Georef.shiftLongitude(g2,360.0);
		Geometry intersection = null;
		try{ 
			// There appears to be a bug in JTS having to do with precision robustness.
			// If intersect gives one of these rare exceptions, use the convex hull of the 
			// geometry for the intersection instead.
			intersection = g1.intersection(g2);
		} catch (Exception e){
			intersection = g1.convexHull().intersection(g2);
		}
		Georef.shiftLongitude(intersection, -360.0);
		// create a new Georef (including PR and SFs) and return that
		Georef ng = new Georef(intersection, this.pointRadius.datum);
		if(ng.pointRadius==null) return null;
		// TODO: It could be that we want to keep one or the other of the input georef if 
		// they don't intersect. Which one?

		// Make an interpreted textual locality description for the intersection. 
		if(ng!=null){
			ng.iLocality=new String(this.iLocality+"; "+g.iLocality);
		}
		return ng;
	}

	public static void shiftLongitude(Geometry g, double shiftby){
		// shift to remove effects of crossing longitude discontinuity
		int nodes = g.getNumPoints();
		Coordinate[] c = g.getCoordinates();
		for(int i=0;i<nodes;i++){
			c[i].x+=shiftby;
			if(c[i].x>360.0) c[i].x-=360.0;
			if(c[i].x<-180.0) c[i].x+=360.0;
		}
	}
	public boolean equals(Georef g){
		// May want to get more rigorous than calling georefs equal if their PointRadiuses are equal.
		return this.pointRadius.equals(g.pointRadius);
	}
	public String toString(){
		return this.toString(true);
	}
	public String toString(boolean showFeatureInfo){
		String s = new String("<GEOREF>\n>");
		s=s.concat("Georef state: "+state+"\n");
		s=s.concat("Original Locality: ");
		if( uLocality != null && uLocality.trim().length()>0) {
			s=s.concat(uLocality+ "\n");
		}	else s=s.concat("not given\n");

		s=s.concat("Interpreted Locality: ");
		if( iLocality != null && iLocality.trim().length()>0) {
			s=s.concat(iLocality+ "\n");
		}	else s=s.concat("not given\n");

		s=s.concat("Confidence: ");
		if( confidence > 0 ) {
			s=s.concat(confidence+ "\n");
		}	else s=s.concat("not given\n");

		s=s.concat("<POINT-RADIUS>\n");
		if( pointRadius != null ) {
			s=s.concat(pointRadius.toString() + "\n");
		}	else s=s.concat("not given\n");
		s=s.concat("</POINT-RADIUS>\n");

		s=s.concat("Point-radius spatial fit: ");
		if( pointRadiusSpatialFit <= 0 || geometry.getArea() <= 0) {
			s=s.concat("undefined\n");
		}	else s=s.concat(pointRadiusSpatialFit + "\n");

		s=s.concat("<GEOMETRY>\n");
		if( geometry != null ) {
			s=s.concat(geometry.toString() + "\n");
		}	else s=s.concat("not given\n");
		s=s.concat("</GEOMETRY>\n");

		s=s.concat("Geometry spatial fit: ");
		if( geometrySpatialFit > 0 ) {
			s=s.concat(geometrySpatialFit + "\n");
		}	else s=s.concat("undefined\n");
		if(showFeatureInfo==true){
			s=s.concat("<FEATUREINFOS>\n");
			for(FeatureInfo f: featureinfos){
				s=s.concat("<FEATUREINFO>\n");
				s=s.concat(f.toString());
				s=s.concat("</FEATUREINFO>\n");
			}
			s=s.concat("</FEATUREINFOS>\n");
		}
		s=s.concat("</GEOREF>\n");

		return s;
	}
	public String toXML(boolean showgeom){
		String s = new String("<GEOREF>\n");
		if( uLocality != null && uLocality.trim().length()>0) {
			s=s.concat("<UNINTERPRETED_LOCALITY>"+uLocality+"</UNINTERPRETED_LOCALITY>\n");
		} else{
			s=s.concat("<UNINTERPRETED_LOCALITY></UNINTERPRETED_LOCALITY>\n");
		}

		if( iLocality != null && iLocality.trim().length()>0) {
			s=s.concat("<INTERPRETED_LOCALITY>"+iLocality+"</INTERPRETED_LOCALITY>\n");
		} else{
			s=s.concat("<INTERPRETED_LOCALITY></INTERPRETED_LOCALITY>\n");
		}

		s=s.concat("<CONFIDENCE>"+confidence+"</CONFIDENCE>\n");

		if( pointRadius != null ) {
			s=s.concat(pointRadius.toXML());
		} else{
			s=s.concat("<POINT_RADIUS></POINT-RADIUS>\n");
		}

		if( pointRadiusSpatialFit <= 0 || geometry.getArea() <= 0) {
			s=s.concat("<PointRadiusSpatialFit>undefined</PointRadiusSpatialFit>\n");
		} else{
			s=s.concat("<PointRadiusSpatialFit>"+pointRadiusSpatialFit+"</PointRadiusSpatialFit>\n");
		}

		if( showgeom==true && geometry != null ) {
			s=s.concat("<GEOMETRY>"+geometry.toString()+"</GEOMETRY>\n");
		} else{
			s=s.concat("<GEOMETRY></GEOMETRY>\n");
		}

		if( geometrySpatialFit > 0 ) {
			s=s.concat("<GeometrySpatialFit>undefined</GeometrySpatialFit>\n");
		} else{
			s=s.concat("<GeometrySpatialFit>"+geometrySpatialFit+"</GeometrySpatialFit>\n");
		}

			s=s.concat("<GEOREF_FEATUREINFOS>\n");
			for(FeatureInfo f: featureinfos){
				s=s.concat(f.toXML(showgeom));
			}
			s=s.concat("</GEOREF_FEATUREINFOS>\n");

			s=s.concat("<GEOREF_STATE>"+state+"</GEOREF_STATE>\n");
		s=s.concat("</GEOREF>\n");

		return s;
	}
}