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

public class ProcessStep {
  public long starttimestamp;
  public long endtimestamp;
  public String version;
  public String name;
  public String method;

  public ProcessStep() {
    starttimestamp = System.currentTimeMillis();
    name = new String("Begin");
    version = new String("1.0");
    endtimestamp = System.currentTimeMillis();
  }

  public ProcessStep(String name, String version, String pmethod) {
    starttimestamp = System.currentTimeMillis();
    if (name == null || name.length() == 0) {
      this.name = new String("unnamed process");
    } else {
      this.name = new String(name);
    }
    if (version == null || version.length() == 0) {
      this.version = new String("0.0");
    } else {
      this.version = new String(version);
    }
    if (method != null) {
      this.method = new String("");
    } else {
      this.method = new String(pmethod);
    }
    endtimestamp = System.currentTimeMillis();
  }

  public void addMethod(String pmethod) {
    if (method == null) {
      method = new String(pmethod);
    } else {
      method = method.concat(pmethod);
    }
  }

  public void begin() {
    starttimestamp = System.currentTimeMillis();
  }

  public long duration() {
    return endtimestamp - starttimestamp;
  }

  public void end() {
    endtimestamp = System.currentTimeMillis();
  }

  public void setMethod(String pmethod) {
    method = new String(pmethod);
  }

  public String toString() {
    String s = new String(starttimestamp + ": " + name + " (" + version + "): "
        + method);
    return s;
  }

  public String toXML() {
    String s = new String(("<STEP>\n"));
    if (name != null && name.length() > 0) {
      s = s.concat("<NAME>" + name + "</NAME>\n");
    } else {
      s = s.concat("<NAME></NAME>\n");
    }
    if (version != null && version.length() > 0) {
      s = s.concat("<VERSION>" + version + "</VERSION>\n");
    } else {
      s = s.concat("<VERSION></VERSION>\n");
    }
    if (method != null && method.length() > 0) {
      s = s.concat("<METHOD>" + method + "</METHOD>\n");
    } else {
      s = s.concat("<METHOD></METHOD>\n");
    }
    s = s.concat("<START>" + starttimestamp + "</START>\n");
    s = s.concat("<END>" + endtimestamp + "</END>\n");
    s = s.concat("</STEP>\n");
    return s;
  }
}