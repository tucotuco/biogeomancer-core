/* 
 * This file is part of BioGeomancer.
 *
 *  Biogeomancer is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  any later version.
 *
 *  Biogeomancer is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Biogeomancer; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.biogeomancer.managers;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.biogeomancer.records.FeatureInfo;
import org.biogeomancer.records.Georef;
import org.biogeomancer.records.LocSpec;
import org.biogeomancer.records.LocSpecState;
import org.biogeomancer.utils.Coordinate;
import org.biogeomancer.utils.PointRadius;

/**
 * @author tuco
 *
 * This should be a singleton that is called upon to make shapes 
 * (Point-Radius or polygon) from a LocType, a LocSpec, and up to
 * two FeatureInfos.
 */
public class ShapeManager extends BGManager {
  public static final String PROPS_FILE = "ShapeManager.properties";
  
//	public DatumManager dmanager;
	private static final Logger log = Logger.getLogger(ShapeManager.class);
	private static Properties props;
  
  static {
    props = new Properties();
    initProps(PROPS_FILE, props);
  }

	public ShapeManager() {	
//  dmanager = new DatumManager();
//  log.info("ShapeManager started");
	}

	public static void main(String[] args){ // throws FactoryException { // main
		if(args.length != 7)
			System.out.println("Required args (" + args.length + ") :\nlat lng extent datum coordinateprecision loctype locality");
		int LAT = 0;
		int LNG = 1;
		int EXT = 2;
		int DATUM = 3;
		int COORDPREC = 4;
		int LOCTYPE = 5;
		int LOCALITY = 6;

		ShapeManager sm = new ShapeManager();
		ADLGazetteer gaz = null;
		try {
			gaz = ADLGazetteer.getInstance();
			System.out.println("Logging to: " + props.getProperty("logfile") + "\n");

//			String lat = args[LAT];
//			String lng = args[LNG];
			String loc = args[LOCALITY];
			LocSpec lspec1 = new LocSpec();
			lspec1.featurename = new String(loc);
			lspec1.voffset = new String("9");
			lspec1.voffsetunit = new String("mi");
			lspec1.vheading = new String("North");

			lspec1.voffsetew = new String("9");
			lspec1.voffsetewunit = new String("km.");
			lspec1.vheadingew = new String("w");

			lspec1.voffsetns = new String("1");
			lspec1.voffsetnsunit = new String("mi.");
			lspec1.vheadingns = new String("S");
			ArrayList<FeatureInfo> fis = new ArrayList();
			FeatureInfo finfo = null;
			if( gaz != null ) {
				fis = gaz.featureLookup(gn, args[LOCALITY], "equals-ignore-case", "system");
				finfo = fis.get(0);
			} else {
				finfo = new FeatureInfo();
				finfo.latitude = new Double(args[LAT]).doubleValue();
				finfo.longitude = new Double(args[LNG]).doubleValue();
				finfo.extentInMeters = new Double(args[EXT]).doubleValue();
				finfo.coordPrecision = new Double(args[COORDPREC]).doubleValue();
				finfo.geodeticDatum = DatumManager.getInstance().getDatum(args[DATUM]);
				finfo.name = args[LOCALITY];
			}
			String loctype = args[LOCTYPE];

//			TODO: Figure out over-logging problem before turning this on for testing
//			sm.log.info("Input lat: " + args[LAT] + " lng: " + args[LNG] + " extent: " + args[EXT] + " datum: " + args[DATUM] + " coordprec: " + args[COORDPREC] + " loctype: " + args[LOCTYPE]);
			System.out.println("Input lat: " + args[LAT] + " lng: " + args[LNG] + " extent: " + args[EXT] + " datum: " + args[DATUM] + " coordprec: " + args[COORDPREC] + " loctype: " + args[LOCTYPE]);
			PointRadius pr = sm.getPointRadius(loctype, lspec1, finfo, null);
			if( pr == null ) { 
				sm.log.error("The ShapeManager was unable to construct a PointRadius.\n" + lspec1.toString() );
			} else 
//				TODO: Figure out over-logging problem before turning this on for testing
//				sm.log.info(pr.toString());
				System.out.println(pr.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			gaz.shutdown();
		}
	}
	/*
		sm.log.info("Going in to Polygon...");
		Polygon p = sm.getPolygon(loctype, finfo, null);
		if( p == null ) { 
			am.log.error("The ShapeManager was unable to construct a Polygon.");
		} else 
			sm.log.info("Polygon extent: " + p.extent);
	}
/**/	
	/*
	 *  Using locality type and one or two FeatureInfo objects filled via locality interpretation,
	 *  generate a Point-Radius representation of the loclity. 
	 *  TODO: The Between Features (BF) LocType requires two featurenames. This is currently meant to be accomplished
	 *  with two LocSpecs.
	 */
	public PointRadius getPointRadius(String loctype, LocSpec locspec, FeatureInfo f1, FeatureInfo f2){ // throws FactoryException { // 
		PointRadius pr = null;
		double uncertainty=0.0;

//		TODO: Interpret these loctypes:
//		ADDR, E, FPOH, FS, J, JO, JH, JOH, JOO, JPOH, LL, NJ, NPOM, POM, PS, Q, TRSS, UTM
//		These locTypes interpreted: 
//		BF, BP, F, P, ADM, FOH, POH, NN, UNK, FOO, FH, PH, NF, NP, FO, PO, TRS, 
		if( loctype.equalsIgnoreCase("F") || loctype.equalsIgnoreCase("P") || loctype.equalsIgnoreCase("ADM") ){  
			// --- LocType "F" ---
			// --- LocType "P" ---
			// --- LocType "ADM" ---
			// Feature/Path/Admin locality type. Example: "Bakersfield"
			// This LocType type requires one and only one FeatureInfo.
			if( f1 == null ) { 
				return null;
			}
			// This locType requires nothing of the verbatim attributes of the LocSpec.
			// The sources of uncertainty for this locality type are:
			//   datum
			//   extent
			//   map scale
			//   coordinate precision
			// 1) Sum all uncertainties (datum, extent, distance precision, mapscale, coordinate precision) to get the overall uncertainty.

			// Create variables to work with temporarily
			Coordinate c1 = new Coordinate( f1.longitude, f1.latitude, f1.geodeticDatum, f1.coordPrecision );

			// Uncertainty from unknown datum
			double datumerror = c1.getDatumError();
			// If datum is unknown, change it now to WGS84, uncertainity already accounts for this.
			if( c1.datum==null || c1.datum.getCode() == "unknown" ){
				c1.datum = DatumManager.getInstance().getDatum("WGS84");
			}

			// Uncertainty due to the Extent of the Feature
			double extent = f1.extentInMeters;

			// Uncertainty due to map scale
			double mapscaleerror = f1.mapAccuracyInMeters;

			// Uncertainty due to the Original Coordinate Precision
			double coordprecisionerror = c1.getLatLngPrecisionInMeters();

			uncertainty = datumerror + extent + mapscaleerror + coordprecisionerror;
			pr = new PointRadius( c1, uncertainty );
		} else if( loctype.equalsIgnoreCase("FOH") || loctype.equalsIgnoreCase("POH")){ 
			// --- LocType "FOH" ---
			// --- LocType "POH" ---
			// Offset from a Feature/Path at a heading. Example: "10 mi E (by air) Bakersfield"
			// This locType type requires one and only one FeatureInfo.
			if( f1 == null ) { 
				return null;
			}
			// This LocType requires the following attributes of the LocSpec to be interpretable:
			// feature, offset, heading
			boolean problems = false;
			//LocSpecState locspecstate = locspecmanager.interpretOffset(locspec);
			//if( locspecstate != LocSpecState.LOCSPEC_COMPLETED) {
			//problems = true;
			//log.error("LocSpec interpretation problem: "+locspecstate);
			//}
			//locspecstate = locspecmanager.interpretHeading(locspec);
			//if( locspecstate != LocSpecState.LOCSPEC_COMPLETED) {
			//	problems = true;
			//	log.error("LocSpec interpretation problem: "+locspecstate);
			//}
			if(problems == true ) return null;

			// The sources of uncertainty for this locality type are:
			//   datum
			//   extent
			//   distance precision 
			//   map scale
			//   direction precision 
			//   coordinate precision
			// 1) Sum all distance uncertainties (datum, extent, distance precision, mapscale)
			// 2) Use the offset, heading and distance uncertainties get the net uncertainty due to heading and offset
			// 3) Add the Coordinate Precision uncertainty 
			// 4) Calculate the position of the coordinates at the offset distance from the feature in the direction of the heading.

			if( !locspec.isHeading() ){ // If the heading isn't interpretable as a valid heading, set the locspec.state and end
//				locspec.state = LocSpecState.LOCSPEC_HEADING_ERROR;
				return null;
			}
			// Interpret the offset. We know this will be valid since we already did the isOffset() check.
			double offsetdistance = locspec.getOffsetInMeters(); 

			// Determine the offset distance uncertainty. This, too, should be valid since we already did the isOffset() check.
			double distanceprecisionerror = locspec.getOffsetUncertaintyInMeters(locspec.voffset, locspec.voffsetunit);

			// Create variables to work with temporarily
			Coordinate c1 = new Coordinate( f1.longitude, f1.latitude, f1.geodeticDatum, f1.coordPrecision );

			// Uncertainty from unknown datum
			double datumerror = c1.getDatumError();

			// Uncertainty due to the Extent of the Feature
			double extent = f1.extentInMeters;

			// Uncertainty due to map scale
			double mapscaleerror = f1.mapAccuracyInMeters;

			// Uncertainty due to the Original Coordinate Precision
			double coordprecisionerror = c1.getLatLngPrecisionInMeters();

			// 1) Construct the net distance uncertainty
			double distanceerror = datumerror+extent+distanceprecisionerror+mapscaleerror;

			// Get the direction uncertainty angle. We know this will be valid since we already did the isHeading() check.
			double alpha = locspec.getHeadingUncertaintyInDegrees();

			// 2) Get the uncertainty due to heading and offset.
			double x = offsetdistance*Math.cos( Math.toRadians(alpha) );
			double y = offsetdistance*Math.sin( Math.toRadians(alpha) );
			double xp = offsetdistance+distanceerror;
			uncertainty = Math.sqrt( Math.pow(xp-x,2) + Math.pow(y,2) );

			// 3) Get the net uncertainty by adding the Coordinate Uncertainty to the Offset/Heading Uncertainty.
			uncertainty += coordprecisionerror;

			// 4) Calculate the position of the new coordinate by going the offset distance in the heading direction from the coordinates of FeatureInfo f1.
			// Start at the coordinates for the feature
			double newlat = c1.y;
			double newlng = c1.x;
			double metersnorth = offsetdistance*Math.cos( Math.toRadians(locspec.getHeadingInDegrees()) );
			double meterseast  = offsetdistance*Math.cos( Math.toRadians(locspec.getHeadingInDegrees()-90.0) );
			double latmpd = c1.getLatMetersPerDegree();
			double lngmpd = c1.getLngMetersPerDegree();
			double dlat = metersnorth/latmpd;
			double dlng = meterseast/lngmpd;
			newlat += dlat;
			newlng += dlng;

			pr = new PointRadius( newlng, newlat, f1.geodeticDatum, c1.precision, uncertainty);
		} else if( loctype.equalsIgnoreCase("NN") ){
			// --- LocType "NN" ---
			// Locality recognized as not recorded, as opposed to not understood by the interpretation process.
			locspec.state = LocSpecState.LOCSPEC_COMPLETED;
			return null;
		}else if( loctype.equalsIgnoreCase("UNK") ){
			// --- LocType "UNK" ---
			// Locality not understood by the interpretation process.
			locspec.state = LocSpecState.LOCSPEC_COMPLETED;
			return null;
		} else if( loctype.equalsIgnoreCase("FOO") ){ // Orthogonal offsets from a feature
			// --- LocType "FOO" ---
			// Orthogal Offsets from a Feature locality type. Example: "2 mi E, 4 mi N Bakersfield"
			// This LocType type requires one and only one FeatureInfo.
			if( f1 == null ) { 
				return null;
			}
			// The sources of uncertainty for this locality type are:
			//   datum
			//   extent
			//   distance precision 
			//   map scale
			//   coordinate precision
			// 1) Sum all uncertainties (datum, extent, distance precision, mapscale) except coordinate precision
			// 2) Take the hypotenuse of a triangle with sides equal to the sum of coordinate uncertainties
			// 3) Add the coordinate uncertainty (because this is already a hypotenuse)
			// 4) Determine the new location base on the offset.
			// 	    maxerrordistance += getDistancePrecisionError();
			// 	    maxerrordistance *= Math.sqrt(2.0);
			// 	    maxerrordistance += getDatumError();
			// 	    maxerrordistance += getExtentsError();
			// 	    maxerrordistance += getMapScaleError();
			// 	    maxerrordistance += getCoordPrecisionError();
			if( locspec.isOffset(locspec.ioffsetew, locspec.ioffsetewunit) == false || locspec.isOffset(locspec.ioffsetns, locspec.ioffsetnsunit) == false ){ // If the orthogonal offsets aren't interpretable as valid offsets with understood units, set the locspec.state and end
//				locspec.state = LocSpecState.LOCSPEC_MALFORMED_OFFSET_ERROR;
				return null;
			}
			if( !locspec.areHeadingsOrthogonal() ){  // If the headings aren't interpretable as valid orthogonal headings, set the locspec.state and end
//				locspec.state = LocSpecState.LOCSPEC_HEADING_ERROR;
				return null;
			}
			// Interpret the offset. We know this will be valid since we already did the isOffset() check.
			double offsetdistanceew = locspec.getOffsetEWInMeters(); 
			double offsetdistancens = locspec.getOffsetNSInMeters(); 

			// Determine the offset distance uncertainty. This, too, should be valid since we already did the isOffset() check.
			double distanceprecisionewerror = locspec.getOffsetEWUncertaintyInMeters();
			double distanceprecisionnserror = locspec.getOffsetNSUncertaintyInMeters();
			double distanceprecisionerror = distanceprecisionewerror;
			// Take the lesser of the distance precisions as the indicator of the level of precision for both.
			if( distanceprecisionerror > distanceprecisionnserror) distanceprecisionerror = distanceprecisionnserror;

			// Create variables to work with temporarily, for clarity and debugging.
			Coordinate c1 = new Coordinate( f1.longitude, f1.latitude, f1.geodeticDatum, f1.coordPrecision );

			// Uncertainty from unknown datum
			double datumerror = c1.getDatumError();

			// Uncertainty due to the Extent of the Feature
			double extent = f1.extentInMeters;

			// Uncertainty due to map scale
			double mapscaleerror = f1.mapAccuracyInMeters;

			// Uncertainty due to the Original Coordinate Precision
			double coordprecisionerror = c1.getLatLngPrecisionInMeters();

			// 1) Construct the net distance uncertainty
			double distanceerror = datumerror + extent + mapscaleerror + distanceprecisionerror;

			// 2) Set the uncertainty to the hypotenuse of the triangle with sides equal to the distanceerror.
			uncertainty = distanceerror;

			// 3) Get the net uncertainty by adding the Coordinate Uncertainty.
			uncertainty += coordprecisionerror;

			// 4) Calculate the position of the new coordinate by going the offset distances in the 
			// orthogonal headings from the coordinates of FeatureInfo f1.
			// Start at the coordinates for the feature
			double newlat = c1.y;
			double newlng = c1.x;
			double metersnorth = offsetdistancens;
			double meterseast  = offsetdistanceew;

			newlat += metersnorth/c1.getLatMetersPerDegree();
			newlng += meterseast/c1.getLngMetersPerDegree();

			pr = new PointRadius( newlng, newlat, f1.geodeticDatum, c1.precision, uncertainty);
		} else if( loctype.equalsIgnoreCase("NF") || loctype.equalsIgnoreCase("NP") ){ // Near a feature (path or feature the same for now).
			// --- LocType "NF" or "NP" ---
			// Near Feature/Path locality type. Example: "near Bakersfield", "vicinity of Lake Tahoe"
			// This LocType type requires one and only one FeatureInfo.
			if( f1 == null ) { 
				return null;
			}
			// The sources of uncertainty for this locality type are:
			//   datum
			//   extent
			//   map scale
			//   coordinate precision
			// 1) Sum all uncertainties (datum, extent, distance precision, mapscale, coordinate precision) to get the overall uncertainty.
			//    But double the extent and use a 2 km minimum.

			// Create variables to work with temporarily, for clarity and debugging.
			Coordinate c1 = new Coordinate( f1.longitude, f1.latitude, f1.geodeticDatum, f1.coordPrecision );

			// Uncertainty from unknown datum
			double datumerror = c1.getDatumError();

			// Uncertainty due to the Extent of the Feature
			double extent = 2*f1.extentInMeters;
			if( extent<2000.0) extent=2000.0;

			// Uncertainty due to map scale
			double mapscaleerror = f1.mapAccuracyInMeters;

			// Uncertainty due to the Original Coordinate Precision
			double coordprecisionerror = c1.getLatLngPrecisionInMeters();

			uncertainty = datumerror + extent + mapscaleerror + coordprecisionerror;
			pr = new PointRadius( c1, uncertainty );
		} else if( loctype.equalsIgnoreCase("FH") || loctype.equalsIgnoreCase("PH") ){ 
			// --- LocType "FH" or "PH" ---
			// At a heading from a Feature/Path locality type. Example: "NW of Bakersfield"
			// This LocType type requires one FeatureInfo on input, but may generate a 
			// second featureinfo in the process.
			if( f1 == null ) { 
				return null;
			}
			if( !locspec.isHeading() ){
				return null;
			}

			// The sources of uncertainty for this locality type are:
			//   datum
			//   extent
			//   map scale
			//   coordinate precision
			// 1) Sum all uncertainties (datum, extent, distance precision, mapscale, coordinate precision).
			// 2) Find the nearest feature within the heading uncertainty up to 100km away
			//    but outside of the input feature's extent and uncertainty.

			// Create variables to work with temporarily, for clarity and debugging.
			Coordinate c1 = new Coordinate( f1.longitude, f1.latitude, f1.geodeticDatum, f1.coordPrecision );

			// Uncertainty from unknown datum
			double datumerror = c1.getDatumError();

			// Uncertainty due to the Extent of the Feature
			double extent = f1.extentInMeters;

			// Uncertainty due to map scale
			double mapscaleerror = f1.mapAccuracyInMeters;

			// Uncertainty due to the Original Coordinate Precision
			double coordprecisionerror = c1.getLatLngPrecisionInMeters();

			uncertainty = datumerror + extent + mapscaleerror + coordprecisionerror;
			
			// Calculate the vertices of the cone in the heading direction
			// Get the direction and direction uncertainty angles. 
			// We know this will be valid since we already did the isHeading() check.
			double alpha = locspec.getHeadingInDegrees();
			double delta = locspec.getHeadingUncertaintyInDegrees();
			double latmpd = c1.getLatMetersPerDegree();
			double lngmpd = c1.getLngMetersPerDegree();
//			double mindistindd=uncertainty/lngmpd;
			// Look for features within 100km of the outer extent of the feature.
			double distanceouterlimit = 100000; 

/*
			// Determine the vertices of the heading shape.
			double x1 = c1.x+(distanceouterlimit+extent)*Math.sin( Math.toRadians(alpha-delta) )/lngmpd;
			double y1 = c1.y+(distanceouterlimit+extent)*Math.cos( Math.toRadians(alpha-delta) )/latmpd;
			double x2 = c1.x+(distanceouterlimit+extent)*Math.sin( Math.toRadians(alpha) )/lngmpd;
			double y2 = c1.y+(distanceouterlimit+extent)*Math.cos( Math.toRadians(alpha) )/latmpd;
			double x3 = c1.x+(distanceouterlimit+extent)*Math.sin( Math.toRadians(alpha+delta) )/lngmpd;
			double y3 = c1.y+(distanceouterlimit+extent)*Math.cos( Math.toRadians(alpha+delta) )/latmpd;
			double x4 = c1.x+extent*Math.sin( Math.toRadians(alpha+delta) )/lngmpd;
			double y4 = c1.y+extent*Math.cos( Math.toRadians(alpha+delta) )/latmpd;
			double x5 = c1.x+extent*Math.sin( Math.toRadians(alpha) )/lngmpd;
			double y5 = c1.y+extent*Math.cos( Math.toRadians(alpha) )/latmpd;
			double x6 = c1.x+extent*Math.sin( Math.toRadians(alpha-delta) )/lngmpd;
			double y6 = c1.y+extent*Math.cos( Math.toRadians(alpha-delta) )/latmpd;
*/
			double conecentroidx = c1.x+(uncertainty+distanceouterlimit/2)*Math.sin( Math.toRadians(alpha) )/lngmpd;
			double conecentroidy = c1.y+(uncertainty+distanceouterlimit/2)*Math.cos( Math.toRadians(alpha) )/latmpd;
/*
			// This version of the heading cone starts at the center point of the feature and 
			// goes out to distanceouterlimit.
			String headingcone = new String("'SRID=4326;POLYGON(("+
					f1.longitude+" "+f1.latitude+","+
					x1+" "+y1+","+
					x2+" "+y2+","+
					x3+" "+y3+","+
					f1.longitude+" "+f1.latitude+"))'");

			// This version of the heading cone excludes the part of the cone within the extent 
			// of the feature and goes out to distanceouterlimit.
			String headingcone = new String("'SRID=4326;POLYGON(("+
					x1+" "+y1+","+
					x2+" "+y2+","+
					x3+" "+y3+","+
					x4+" "+y4+","+
					x5+" "+y5+","+
					x6+" "+y6+","+
					x1+" "+y1+"))'");
*/
/* This method turns out to be too slow to be viable.
			FeatureInfo tofi = new FeatureInfo(); // Create a FeatureInfo to hold the nearest Feature
			// TODO: This should be changed to worldplaces.
			Connection gdb = worldplaces;
//			Connection gdb = gnispopulatedplaces;
			ADLGazetteer gaz=null;
			try {
				gaz = ADLGazetteer.getInstance();
				gaz.lookupNearestFeature(gdb, f1, tofi, mindistindd, headingcone);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
//				gaz.shutdown();
			}
*/
//			Coordinate c2 = new Coordinate( tofi.longitude, tofi.latitude, tofi.geodeticDatum, tofi.coordPrecision );
//			double xdist = c2.getLngDistanceInMetersToCoordinate(c1);
//			double ydist = c2.getLatDistanceInMetersToCoordinate(c1);
//			Coordinate midpoint = Georef.getMidPoint(c1, c2);
//			double betweendist = Math.sqrt(Math.pow(xdist, 2) + Math.pow(ydist, 2));
//			double newx = c1.x+betweendist*Math.sin( Math.toRadians(alpha) )/lngmpd;
//			double newy = c1.y+betweendist*Math.cos( Math.toRadians(alpha) )/latmpd;
//			Coordinate newpoint = new Coordinate(newx, newy, tofi.geodeticDatum, tofi.coordPrecision);
			Coordinate newpoint = new Coordinate( conecentroidx, conecentroidy, f1.geodeticDatum, 0 );
			pr = new PointRadius( newpoint, distanceouterlimit/2 );
		} else if( loctype.equalsIgnoreCase("FO") || loctype.equalsIgnoreCase("PO") ){ // Offset from a feature with no heading
			// --- LocType "FO" ---
			// --- LocType "PO" ---
			// Feature Offset locality type. Example: "5 mi from Bakersfield"
			// This LocType type requires one and only one FeatureInfo.
			if( f1 == null ) { 
				return null;
			}
			// This LocType requires the following attributes of the LocSpec to be interpretable:
			// feature, offset
			boolean problems = false;
			//LocSpecState locspecstate = locspecmanager.interpretOffset(locspec);
			//if( locspecstate != LocSpecState.LOCSPEC_COMPLETED) {
			//	problems = true;
			//	log.error("LocSpec interpretation problem: "+locspecstate);
			//}
			if(problems == true ) return null;

			// The sources of uncertainty for this locality type are:
			//   datum
			//   extent
			//   distance precision 
			//   map scale
			//   coordinate precision
			// 1) Sum all uncertainties (datum, extent, distance precision, mapscale, coordinate precision)
			// 2) Add the offset distance

			if( !locspec.isOffset() ){ // If the offset isn't interpretable as a valid offset with understood units, set the locspec.state and end
//				locspec.state = LocSpecState.LOCSPEC_MALFORMED_OFFSET_ERROR;
				return null;
			}
			// Interpret the offset. We know this will be valid since we already did the isOffset() check.
			double offsetdistance = locspec.getOffsetInMeters(); 

			Coordinate c1 = new Coordinate( f1.longitude, f1.latitude, f1.geodeticDatum, f1.coordPrecision );

			// Uncertainty from unknown datum
			double datumerror = c1.getDatumError();

			// Uncertainty due to the Extent of the Feature
			double extent = f1.extentInMeters;

			// Determine the offset distance uncertainty. This, too, should be valid since we already did the isOffset() check.
			double distanceprecisionerror = locspec.getOffsetUncertaintyInMeters(locspec.voffset, locspec.voffsetunit);

			// Uncertainty due to map scale
			double mapscaleerror = f1.mapAccuracyInMeters;

			// Uncertainty due to the Original Coordinate Precision
			double coordprecisionerror = c1.getLatLngPrecisionInMeters();

			uncertainty = datumerror + extent + coordprecisionerror + mapscaleerror + distanceprecisionerror;
			uncertainty += offsetdistance;
			pr = new PointRadius( c1, uncertainty );
		} else if( loctype.equalsIgnoreCase("FS") || loctype.equalsIgnoreCase("PS")){ // Subdivision of a feature
			// --- LocType "FS" ---
			// --- LocType "PS" ---
			// Feature Subdivision locality type. Example: "SE Bakersfield"
			// This LocType type requires one and only one FeatureInfo.
			if( f1 == null ) { 
				return null;
			}
			// This LocType requires the following attributes of the LocSpec to be interpretable:
			// feature, subdivision
			boolean problems = false;
			//LocSpecState locspecstate = locspecmanager.interpretSubdivision(locspec);
			//if( locspecstate != LocSpecState.LOCSPEC_COMPLETED) {
			//	problems = true;
			//	log.error("LocSpec interpretation problem: "+locspecstate);
			//}
			if(problems == true ) return null;

			// The sources of uncertainty for this locality type are:
			//   datum
			//   extent
			//   distance precision 
			//   map scale
			//   coordinate precision
			// 1) Sum all uncertainties (datum, extent, distance precision, mapscale, coordinate precision)
		} else if( loctype.equalsIgnoreCase("BF") || loctype.equalsIgnoreCase("BP") ){ // Near a feature (path or feature the same for now).
			// --- LocType "BF" ---
			// --- LocType "BP" ---
			// Between Features/Between Paths. Example: "between Bakersfield and Fresno"
			// This LocType type requires two FeatureInfos.
			if( f1 == null || f2 == null) { 
				return null;
			}

			// This locType requires nothing of the verbatim attributes of the LocSpec.
			// The sources of uncertainty for this locality type are:
			//   datum
			//   extents of both features (not used)
			//   map scale
			//   coordinate precision
			//   distance from midpoint between features to center of either feature.
			// 1) Sum all uncertainties (datum, extent, distance precision, mapscale, coordinate precision) to get the overall uncertainty.

			// Create variables to work with temporarily
			Coordinate c1 = new Coordinate( f1.longitude, f1.latitude, f1.geodeticDatum, f1.coordPrecision );
			Coordinate c2 = new Coordinate( f2.longitude, f2.latitude, f2.geodeticDatum, f2.coordPrecision );
			double midlat=(f1.longitude+f2.longitude)/2;
			double midlng=(f1.latitude+f2.latitude)/2;
//			Coordinate midpoint = new Coordinate(midlng, midlat); // TODO: Not correct across 180W
			Coordinate midpoint = Georef.getMidPoint(c1, c2);

			// Uncertainty from unknown datum
			double datumerror = c1.getDatumError();
			// If datum is unknown, change it now to WGS84, uncertainity already accounts for this.
			if( c1.datum.getCode() == "unknown" ) c1.datum = DatumManager.getInstance().getDatum("WGS84");

			// Uncertainty due to the Extents of the Features. For conservative uncertainty, use the greater extent.
//			double extent = f1.extentInMeters;
//			if(f2.extentInMeters>f1.extentInMeters) extent = f2.extentInMeters;
			// For sensible uncertainty, use no extent - between is interpreted as between centers.
			double extent = 0;

			// Uncertainty due to map scale
			double mapscaleerror = f1.mapAccuracyInMeters + f2.mapAccuracyInMeters;

			// Uncertainty due to the Original Coordinate Precision
			double coordprecisionerror = c1.getLatLngPrecisionInMeters() + 
			c2.getLatLngPrecisionInMeters();

			uncertainty = datumerror + extent + mapscaleerror + coordprecisionerror;
			uncertainty += midpoint.precision; // add the distance from the midpoint to either center.

			pr = new PointRadius( midpoint, uncertainty );
		}else if( loctype.equalsIgnoreCase("TRS") || loctype.equalsIgnoreCase("TRSS") ){  
			// --- LocType "TRS" --- 
			// --- LocType "TRSS" ---
			// This LocType type requires one and only one FeatureInfo.
			if( f1 == null ) { 
				return null;
			}
			// This locType requires nothing of the verbatim attributes of the LocSpec
			// as long as the featurename has been loaded with standardized form of 
			// TRS (e.g., "Township 23 North Range 17 West")
			// The sources of uncertainty for this locality type are:
			//   datum
			//   extent
			//   map scale
			//   coordinate precision
			// 1) Sum all uncertainties (datum, extent, distance precision, mapscale, coordinate precision) to get the overall uncertainty.

			// Create variables to work with temporarily
			Coordinate c1 = new Coordinate( f1.longitude, f1.latitude, f1.geodeticDatum, f1.coordPrecision );

			// Uncertainty from unknown datum
			double datumerror = c1.getDatumError();
			// If datum is unknown, change it now to WGS84, uncertainity already accounts for this.
			if( c1.datum.getCode() == "unknown" ) c1.datum = DatumManager.getInstance().getDatum("WGS84");

			// Uncertainty due to map scale
			double mapscaleerror = f1.mapAccuracyInMeters;

			// Uncertainty due to the Original Coordinate Precision
			double coordprecisionerror = c1.getLatLngPrecisionInMeters();

			// Uncertainty due to the Extent of the Feature
			double extent = f1.extentInMeters;
			double xoffset = 0, yoffset = 0;
			double sectionwidth=extent*Math.sqrt(2)/2;
			if(locspec.isection!=null){
				int sec = Integer.valueOf(locspec.isection);
				if(sec>0 && sec<37){ // valid section number
					extent=1138; // use standard 1 mi section. 0.707 mi = 1138 m
					switch(sec){ // The following adjustments for sections are based on well-behaved townships.
					case 1:
						xoffset += sectionwidth*5/6;
						yoffset += sectionwidth*5/6;
						break;
					case 2:
						xoffset += sectionwidth*3/6;
						yoffset += sectionwidth*5/6;
						break;
					case 3:
						xoffset += sectionwidth*1/6;
						yoffset += sectionwidth*5/6;
						break;
					case 4:
						xoffset -= sectionwidth*1/6;
						yoffset += sectionwidth*5/6;
						break;
					case 5:
						xoffset -= sectionwidth*3/6;
						yoffset += sectionwidth*5/6;
						break;
					case 6:
						xoffset -= sectionwidth*5/6;
						yoffset += sectionwidth*5/6;
						break;
					case 7:
						xoffset -= sectionwidth*5/6;
						yoffset += sectionwidth*3/6;
						break;
					case 8:
						xoffset -= sectionwidth*3/6;
						yoffset += sectionwidth*3/6;
						break;
					case 9:
						xoffset -= sectionwidth*1/6;
						yoffset += sectionwidth*3/6;
						break;
					case 10:
						xoffset += sectionwidth*1/6;
						yoffset += sectionwidth*3/6;
						break;
					case 11:
						xoffset += sectionwidth*3/6;
						yoffset += sectionwidth*3/6;
						break;
					case 12:
						xoffset += sectionwidth*5/6;
						yoffset += sectionwidth*3/6;
						break;
					case 13:
						xoffset += sectionwidth*5/6;
						yoffset += sectionwidth*1/6;
						break;
					case 14:
						xoffset += sectionwidth*3/6;
						yoffset += sectionwidth*1/6;
						break;
					case 15:
						xoffset += sectionwidth*1/6;
						yoffset += sectionwidth*1/6;
						break;
					case 16:
						xoffset -= sectionwidth*1/6;
						yoffset += sectionwidth*1/6;
						break;
					case 17:
						xoffset -= sectionwidth*3/6;
						yoffset += sectionwidth*1/6;
						break;
					case 18:
						xoffset -= sectionwidth*5/6;
						yoffset += sectionwidth*1/6;
						break;
					case 19:
						xoffset -= sectionwidth*5/6;
						yoffset -= sectionwidth*1/6;
						break;
					case 20:
						xoffset -= sectionwidth*3/6;
						yoffset -= sectionwidth*1/6;
						break;
					case 21:
						xoffset -= sectionwidth*1/6;
						yoffset -= sectionwidth*1/6;
						break;
					case 22:
						xoffset += sectionwidth*1/6;
						yoffset -= sectionwidth*1/6;
						break;
					case 23:
						xoffset += sectionwidth*3/6;
						yoffset -= sectionwidth*1/6;
						break;
					case 24:
						xoffset += sectionwidth*5/6;
						yoffset -= sectionwidth*1/6;
						break;
					case 25:
						xoffset += sectionwidth*5/6;
						yoffset -= sectionwidth*3/6;
						break;
					case 26:
						xoffset += sectionwidth*3/6;
						yoffset -= sectionwidth*3/6;
						break;
					case 27:
						xoffset += sectionwidth*1/6;
						yoffset -= sectionwidth*3/6;
						break;
					case 28:
						xoffset -= sectionwidth*1/6;
						yoffset -= sectionwidth*3/6;
						break;
					case 29:
						xoffset -= sectionwidth*3/6;
						yoffset -= sectionwidth*3/6;
						break;
					case 30:
						xoffset -= sectionwidth*5/6;
						yoffset -= sectionwidth*3/6;
						break;
					case 31:
						xoffset -= sectionwidth*5/6;
						yoffset -= sectionwidth*5/6;
						break;
					case 32:
						xoffset -= sectionwidth*3/6;
						yoffset -= sectionwidth*5/6;
						break;
					case 33:
						xoffset -= sectionwidth*1/6;
						yoffset -= sectionwidth*5/6;
						break;
					case 34:
						xoffset += sectionwidth*1/6;
						yoffset -= sectionwidth*5/6;
						break;
					case 35:
						xoffset += sectionwidth*3/6;
						yoffset -= sectionwidth*5/6;
						break;
					case 36:
						xoffset += sectionwidth*5/6;
						yoffset -= sectionwidth*5/6;
						break;
					}
				}
				// TODO: Determine subsections if locspec.
				if(locspec.isubdivision != null){
					// Assume format of subdivision has been standardized with most 
					// specific subsection first:
					// For example: NW 1/4 of SW 1/4
				}
			} // end if locspec.isection != null
			c1.x += xoffset/c1.getLngMetersPerDegree();
			c1.y += yoffset/c1.getLatMetersPerDegree();

			uncertainty = datumerror + extent + mapscaleerror + coordprecisionerror;
			pr = new PointRadius( c1, uncertainty );
		} else { // didn't process this loctype
//			log.error("LocType "+loctype.toUpperCase()+" processing not yet implemented.");
			return null;
		}            
		return pr; 
	}
}
