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

package org.biogeomancer.managers;

import org.biogeomancer.utils.SupportedLanguages;


public class GeorefPreferences {
  public String locinterp;
  public SupportedLanguages language;

  public GeorefPreferences() {
  }

  public GeorefPreferences(String interpprefs) {
	    this.locinterp = new String(interpprefs);
	    this.language = SupportedLanguages.english;
	  }
  
  public GeorefPreferences(String interpprefs,SupportedLanguages language) {
	    this.locinterp = new String(interpprefs);
	    this.language = language;
	  }
  
  public void setLanguage(String lang){

	  if(lang != null){
		  if(lang.equals("dutch")){
			  this.language = SupportedLanguages.dutch;
		  }
		  else if(lang.equals("french")){
			  this.language = SupportedLanguages.french;
		  }
		  else if(lang.equals("spanish")){
			  this.language = SupportedLanguages.spanish;
		  }
		  else if(lang.equals("portuguese")){
			  this.language = SupportedLanguages.portuguese;
		  }
		  else{
			  this.language = SupportedLanguages.english;
		  }
	  }
  }

}