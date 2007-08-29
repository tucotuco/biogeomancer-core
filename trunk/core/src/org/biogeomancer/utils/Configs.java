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

package org.biogeomancer.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configs {
  private final Properties props;

  /**
   * Config constructor.
   * 
   * Creates a Config object given a filename to a properties file.
   * 
   * @param filename
   *          path to a properties file.
   */
  public Configs(String filename) {
    this.props = new Properties();
    if (filename != null)
      this.load(filename);
  }

  /**
   * get()
   * 
   * Gets a config property given a property name.
   * 
   * @param name
   *          the name of the property value to get.
   */
  public String get(String property) {
    return this.props.getProperty(property);
  }

  /**
   * set()
   * 
   * Sets a config property given a name value pair.
   * 
   * @param name
   *          the name of the property value to set.
   * @param value
   *          the value of the property name to set.
   */
  public synchronized void set(String name, String value) {
    this.props.setProperty(name, value);
  }

  /**
   * load()
   * 
   * Loads properties from the filename properties file into this Config
   * object.
   * 
   * @param filename
   *          path to a properties file on a local disk.
   */
  private void load(String filename) {
    try {
      FileInputStream f = new FileInputStream(filename);
      props.load(f);
      f.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
