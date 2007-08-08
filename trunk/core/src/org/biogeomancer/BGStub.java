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
package org.biogeomancer;

import java.io.FileInputStream;
import java.util.Properties;
import java.net.URL;

import org.biogeomancer.managers.GeorefManager;
import org.biogeomancer.managers.GeorefPreferences;
import org.biogeomancer.validation.ValidationManager;
import org.biogeomancer.records.Clause;
import org.biogeomancer.records.Georef;
import org.biogeomancer.records.Rec;
import org.biogeomancer.records.RecSet;

public class BGStub {
	public static void main(String args[]) {
		System.out.println("BGStub running...");
		String configfile = args[0];
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(configfile));
			String datafile = props.getProperty("datafile");
			RecSet recset = null;
			if (isUrl(datafile)) {
				recset = new RecSet(datafile, 
					props.getProperty("delineator"), 
					props.getProperty("destination"));
			}
			else {
				recset = new RecSet(datafile, 
					props.getProperty("delineator"));
			}
			String enableGeorefStr = props.getProperty("GeorefEngine.enabled");
			if (enableGeorefStr == null || (enableGeorefStr != null && Boolean.parseBoolean(enableGeorefStr))) {
				GeorefManager georefmanager = new GeorefManager(recset);
				GeorefPreferences prefs = new GeorefPreferences();
				prefs.locinterp = props.getProperty("interpreter");
				georefmanager.georeference(prefs);
			}
			ValidationManager vm = new ValidationManager(props, recset);
			if (vm.enabled()) {
				vm.validate();
			}
			int recIndex = -1;
			int georefIndex;
			for (Rec rec: recset.recs) {
				++recIndex;
				System.out.println("Rec [id =\"" + rec.get("id") + "\", " +
							"scientific name =\"" + rec.get("scientificname") + "\", " +
							"country =\"" + rec.get("country") + "\", " +
							"locality =\"" + rec.get("locality") + "\", " +
							"declat = \"" + rec.get("declat") + "\", " +
							"declong = \"" + rec.get("declong") + "\"]");
				System.out.println("\tClauses: ");
				for (Clause clause: rec.clauses) {
					System.out.println("\t\tLocType: " + clause.locType);
				}
				System.out.println("\tInterpretations: ");
				georefIndex = -1;
				for (Georef georef: rec.georefs) {
					++georefIndex;
					if (georef.pointRadius == null) { 
					    System.out.println("\t\t--> NULL!");
					}
					else { 
					    System.out.println("\t\t--> y: " + 
					        georef.pointRadius.getY() +
					        " x: " + 
					        georef.pointRadius.getX());
					}
					vm.displayResults( recIndex, georefIndex );
				}
				vm.displayResults( recIndex );
			}
			vm.displaySummary();
		}
		catch (Exception e) {
			System.out.println(e.toString());
                        e.printStackTrace();
		}
	}

	public static boolean isUrl(String resource) {
		boolean ret = true;
		try {
			URL url = new URL(resource);
		} catch (Exception e) {
			ret = false;
		}
		return ret;
	}
}



