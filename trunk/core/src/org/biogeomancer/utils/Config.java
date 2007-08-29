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
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class Config {
  private static Config instance;

  public static synchronized String get(String property) {
    return instance.p.getProperty(property);
  }

  public synchronized static Config getInstance() {
    // System.out.println("CONFIG GETINSTANCE()");
    if (instance == null) {
      instance = new Config();
    }
    // System.out.println("CONFIG GETINSTANCE() -- EXISTING" + instance.p);
    return instance;
  }

  public synchronized static void load(String filename) {
    try {
      FileInputStream f = new FileInputStream(filename);
      instance.p.load(f);
      f.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    // build logger
    Logger log = Logger.getLogger("bg.utils");
    try {
      FileHandler file_handler = new FileHandler(get("bg.log"), true);
      log.addHandler(file_handler);
    } catch (IOException e) {
      e.printStackTrace();
    }

    // build locales
    HashMap l = new HashMap();
    for (SupportedLanguages lang : SupportedLanguages.values()) {
      l.put(lang, ResourceBundle.getBundle("bg.locale.Concepts_"
          + lang.toString()));
    }
    Config.instance.resources.put("locales", l);
  }

  public static synchronized void set(String name, String value) {
    instance.p.setProperty(name, value);
  }

  public HashMap resources;
  private final Properties p;

  private Config() {
    p = new Properties();
    resources = new HashMap();
  }
}
