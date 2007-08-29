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

package org.biogeomancer.records;

import java.util.ArrayList;

public class Metadata {
  public ArrayList<ProcessStep> steps;

  public Metadata() {
    steps = new ArrayList<ProcessStep>();
  }

  public void addStep(ProcessStep step) {
    steps.add(step);
  }

  public String getSteps(String processname) {
    String s = new String();
    for (ProcessStep step : steps) {
      if (step.name.equalsIgnoreCase(processname)) {
        if (s.length() == 0) {
          s = s.concat(step.toString());
        } else {
          s = s.concat("\n" + step.toString());
        }
      }
    }
    return s;
  }

  public String toString() {
    String s = new String();
    for (ProcessStep step : steps) {
      if (s.length() == 0) {
        s = s.concat(step.toString());
      } else {
        s = s.concat("\n" + step.toString());
      }
    }
    return s;
  }

  public String toXML() {
    String s = new String();
    for (ProcessStep step : steps) {
      s = s.concat(step.toXML());
    }
    return s;
  }
}
