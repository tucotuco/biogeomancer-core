/***
Metadata.java
Created 2005-april-15
BioGeomancer Project
 ***/
package org.biogeomancer.records;
import java.util.*;

import org.biogeomancer.records.*;

public class Metadata {
	public ArrayList<ProcessStep> steps;

	public Metadata(){
		steps=new ArrayList<ProcessStep>();
	}
	public void addStep(ProcessStep step){
		steps.add(step);
	}
	public String getSteps(String processname){
		String s=new String();
		for(ProcessStep step: steps){
			if(step.name.equalsIgnoreCase(processname)){
				if(s.length()==0){
					s=s.concat(step.toString());
				} else{
					s=s.concat("\n"+step.toString());
				}
			}
		}
		return s;
	}
	public String toString(){
		String s = new String();
		for(ProcessStep step: steps){
			if(s.length()==0){
				s=s.concat(step.toString());
			} else{
				s=s.concat("\n"+step.toString());
			}
		}
		return s;
	}
	public String toXML(){
		String s = new String();
		for(ProcessStep step: steps){
			s = s.concat(step.toXML());
		}
		return s;
	}
}
