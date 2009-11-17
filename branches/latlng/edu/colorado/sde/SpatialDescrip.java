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

package edu.colorado.sde;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.WriterAppender;
import org.biogeomancer.managers.DatumManager;
import org.biogeomancer.records.Clause;
import org.biogeomancer.records.FeatureInfo;
import org.biogeomancer.records.LocSpec;
import org.biogeomancer.records.Rec;
import org.biogeomancer.records.RecSet;
import org.xml.sax.InputSource;

import edu.biogeomancer.colorado.greg.parse.query.GazetteerQueryType;
import edu.biogeomancer.colorado.greg.parse.query.NameQueryType;
import edu.biogeomancer.colorado.greg.parse.query.impl.GazetteerQueryTypeImpl;
import edu.biogeomancer.colorado.greg.parse.query.impl.NameQueryTypeImpl;
import edu.colorado.museum.greg.util.Constants;
import edu.colorado.museum.greg.util.FileUtils;
import edu.ucsb.adl.gaz.NonXMLFrontend;

public class SpatialDescrip {
  protected static Logger log = Logger.getLogger(SpatialDescrip.class);
  protected static FileUtils fu = new FileUtils();
  protected static NonXMLFrontend gazetteer = null;
  protected static String host = null;
  protected static String port = null;
  protected static String conn_url = null;

  protected static String conn_owner = null;;
  protected static String conn_pass = null;

  protected static String gazrepu = null;
  protected static String gazseachu = null;
  protected static String adl_conf = null;
  protected static String base_path = null;
  protected static String[] db_urls = new String[2];
  protected static String cmd = null;
  protected static String cmd_flags = null;
  protected static String sh = null;
  protected static String geod = null;
  protected static String[] exec_i = new String[Constants.max_exec_params];
  protected static Boolean inited = false;

  public static void addAppender(Logger log, String backup_path,
      ResourceBundle rp) {

    String log_path = backup_path;
    if (rp != null) {
      try {
        log_path = rp.getString("base_path");
      } catch (MissingResourceException mre) {
        log_path = backup_path;
      }
    }

    File tmpf = new File(log_path);
    if (!tmpf.exists()) {
      log_path = backup_path;
    }

    System.out.println("log path: " + log_path);

    String default_log = "sd.html";
    if (rp != null) {
      try {
        default_log = rp.getString("default_log");
      } catch (MissingResourceException mre) {
        default_log = "sd.html";
      }
    }

    System.out.println("logfile: " + default_log);

    HTMLLayout layout = new HTMLLayout();
    WriterAppender appender = null;

    try {
      FileOutputStream output = new FileOutputStream(fu
          .addTrailingSlash(log_path)
          + default_log, true);
      appender = new WriterAppender(layout, output);
      log.addAppender(appender);
      log.setLevel(Level.DEBUG);
    } catch (Exception e) {
      System.out.println(e.toString());
    }

  }

  /*****************************************************************************
   * this is the spatial description component. it gets jobs from the
   * georeference manager and completes an interpretation for each georeference
   * in each record of the record set.
   ****************************************************************************/

  public RecSet recset = null;
  public Rec r = null;
  public Iterator recs = null, ginterps = null;
  public HashMap resrcs = null, events = null;

  public List sdescrips = null;

  public Set ekeys = null;
  ResourceBundle rb = ResourceBundle.getBundle("bg.MessageResources");

  ResourceBundle rp = ResourceBundle.getBundle("bg.RuntimeResources");

  public SpatialDescrip() {
    if (!inited) {
      init();
      inited = true;
    }
    // gazetteer = new NonXMLFrontend(adl_conf, db_urls, exec_i, true);
    gazetteer = new NonXMLFrontend(adl_conf, db_urls, exec_i);
    addAppender(log, "/home/ghill/bg/logs/", rp);
  }

  public void executeEvents(HashMap events) {
    Iterator eiterator = events.keySet().iterator();
    executeEvents(eiterator);
  }

  public void executeEvents(Iterator eiterator) {

    while (eiterator.hasNext()) {

      r = (Rec) eiterator.next();

    }
    log.debug("SpatialDescrip::executeEvents done" + ": 321");
  }

  public void executeEvents(RecSet recset) {
    List<Rec> recs = recset.recs;
    Iterator eiterator = recs.iterator();
    executeEvents(eiterator);
  }

  public void init() {

    try {
      base_path = rp.getString("base_path");
    } catch (MissingResourceException mre) {
      base_path = "C:/Apache/Apache2/data/cumuseum/";
    } finally {
      base_path = fu.addTrailingSlash(base_path);
      adl_conf = fu.addTrailingSlash(base_path) + "etc/adl/";
    }

    try {
      host = rp.getString("host");
    } catch (MissingResourceException mre) {
      host = "localhost";
    }

    try {
      port = rp.getString("port");
    } catch (MissingResourceException mre) {
      port = "5432";
    }

    try {
      conn_url = rp.getString("conn.url");
    } catch (MissingResourceException mre) {
      conn_url = "jdbc:postgresql://";
    }

    try {
      conn_owner = rp.getString("conn.owner");
    } catch (MissingResourceException mre) {
      conn_owner = "Owner";
    }

    try {
      conn_pass = rp.getString("conn.pass");
    } catch (MissingResourceException mre) {
      conn_pass = "Owner";
    }

    try {
      gazrepu = rp.getString("gazrepu");
    } catch (MissingResourceException mre) {
      gazrepu = "gazrepu";
    }

    try {
      gazseachu = rp.getString("gazseachu");
    } catch (MissingResourceException mre) {
      gazseachu = "gazseachu";
    }

    db_urls[0] = conn_url + host + ":" + port + "/" + gazrepu + "?" + "user="
        + conn_owner + "&" + "password=" + conn_pass;

    db_urls[1] = conn_url + host + ":" + port + "/" + gazseachu + "?" + "user="
        + conn_owner + "&" + "password=" + conn_pass;

    try {
      cmd = rp.getString("cmd");
      cmd_flags = rp.getString("cmd_flags");
      sh = rp.getString("sh");
      geod = rp.getString("geod");
    } catch (MissingResourceException mre) {
      cmd = "cmd.exe";
      cmd_flags = "/C";
      sh = "sh.exe";
      geod = "geod.sh";
    } finally {
      exec_i[0] = cmd;
      exec_i[1] = cmd_flags;
      exec_i[2] = fu.addTrailingSlash(base_path);
      exec_i[3] = sh;
      exec_i[4] = geod;
      exec_i[Constants.max_exec_params - 2] = ""; // username not needed for
                                                  // gazetteer
      exec_i[Constants.max_exec_params - 1] = ""; // password not needed for
                                                  // gazetteer
    }
  }

  public FeatureInfo[] lookupFeature(Rec rec) {
    Clause c = null;
    LocSpec[] locs = null;
    ArrayList locs_a = null;
    LocSpec loc = null;
    ArrayList<Clause> clauses = null;
    Iterator citerator = null;
    Iterator it = null;
    Iterator it2 = null;
    clauses = rec.clauses;
    FeatureInfo[] fifos = null;

    for (it = clauses.iterator(); it.hasNext();) {
      c = (Clause) it.next();
      if (c == null) {
        continue;
      }

      locs_a = c.locspecs;
      locs = new LocSpec[locs_a.size()];
      locs_a.toArray(locs);
      for (Integer ii = 0; ii < locs.length; ii++) {
        loc = locs[ii];
        if (loc == null || loc.featurename == null) {
          continue;
        }
        fifos = lookupFeature(loc.featurename);
      }
    }
    return fifos;
  }

  public FeatureInfo[] lookupFeature(String locality) {

    GazetteerQueryType gqt = null;
    NameQueryType nqt = null;
    QueryHandler qh = new QueryHandler();

    java.util.List cs_l = null;

    Object[][][][] ars = null;

    String query_string = null;
    Object[][][][] results = null;

    FeatureInfo[] fifos = null;
    FeatureInfo fifo = null;

    Double upper = null;
    Double lower = null;
    Double lefter = null;
    Double righter = null;

    Integer ii = null;

    try {
      // gbh defaulting to NameQuery. Other query types implemented and ready to
      // use
      // (Footprint, Classification, Offset ...)
      gqt = new GazetteerQueryTypeImpl();
      nqt = new NameQueryTypeImpl();
      nqt.setOperator("contains-phrase");
      nqt.setText(locality);
      gqt.setNameQuery(nqt);
      query_string = qh.getGazetteerQueryAsString(gqt);
      String prefix = "<gazetteer-service"
          + " xmlns=\"http://www.alexandria.ucsb.edu/gazetteer\""
          + " xmlns:gml=\"http://www.opengis.net/gml\"" + " version=\"1.2\">"
          + "<query-request>" + "<gazetteer-query>";
      query_string = query_string.replaceFirst("[\n]", "\n" + prefix);
      String suffix = "</gazetteer-query>"
          + "<report-format>standard</report-format>" + "</query-request>"
          + "</gazetteer-service>";
      query_string = query_string.replace("\n\n", "\n" + suffix + "\n\n");
      query_string = query_string.replace(
          "xmlns=\"http://query.parse.greg.colorado.biogeomancer.edu\"", "");

      results = gazetteer.processQuery(query_string);
      fifos = new FeatureInfo[results.length];
      for (ii = 0; ii < results.length; ii++) {
        fifo = new FeatureInfo();
        fifo.name = (String) results[ii][Constants.F_NAMES][0][1];
        upper = (Double) results[ii][Constants.F_BOUNDING_BOX][0][Constants.north];
        lower = (Double) results[ii][Constants.F_BOUNDING_BOX][0][Constants.south];
        lefter = (Double) results[ii][Constants.F_BOUNDING_BOX][0][Constants.west];
        righter = (Double) results[ii][Constants.F_BOUNDING_BOX][0][Constants.east];
        fifo.latitude = (upper + lower) / 2;
        fifo.longitude = (lefter + righter) / 2;
        fifo.classificationTerm = (String) results[ii][Constants.F_CLASSES][0][2];
        fifo.geodeticDatum = DatumManager.getInstance().getDatum("WGS84"); // !!!!this
                                                                            // is
                                                                            // correct
                                                                            // because
                                                                            // specified
                                                                            // that
                                                                            // all
        // data in bg-adl is WGS-84 but should add to report
        fifo.encodedGeometry = (String) results[ii][Constants.F_FOOTPRINT][0][3];
        fifo.extentInMeters = (Double) results[ii][Constants.F_FOOTPRINT][0][5];
        fifos[ii] = fifo;
      }
    } catch (Exception e) {
      log.debug(e.toString());
    }
    return fifos;
  }

  public FeatureInfo[][] lookupFeatures(RecSet recset) {
    FeatureInfo[][] fifos = null;
    ArrayList fifos_a = new ArrayList();
    if (fu.isEmpty(recset)) {
      return null;
    }
    ArrayList<Rec> recs = recset.recs;
    Iterator it = recs.iterator();
    fifos = new FeatureInfo[recs.size()][];
    for (Integer ii = 0; ii < recs.size(); ii++) {
      try {
        fifos[ii] = lookupFeature(recs.get(ii).get("vlocality"));
      } catch (Exception e) {
        continue;
      }
    }
    return fifos;
  }

}

class QueryHandler {

  public GazetteerQueryType getGazetteerQuery(InputSource pSource)
      throws JAXBException {
    JAXBContext context = JAXBContext.newInstance(
        "edu.biogeomancer.colorado.greg.parse.query", this.getClass()
            .getClassLoader());
    Unmarshaller unmarshaller = context.createUnmarshaller();
    return (GazetteerQueryType) unmarshaller.unmarshal(pSource);
  }

  public String getGazetteerQueryAsString(GazetteerQueryType query)
      throws JAXBException {
    StringWriter sw = new StringWriter();
    JAXBContext context = JAXBContext.newInstance(
        "edu.biogeomancer.colorado.greg.parse.query", this.getClass()
            .getClassLoader());
    Marshaller marshaller = context.createMarshaller();
    marshaller.marshal(query, sw);
    return sw.toString();
  }

  // results returned as Object[dim1][dim2][dim3][dim4].
  // dim1 iterates over reports returned (max 100, see gaz.properties to
  // change).
  // dim2 - facet of the report (names, classifications, footprints, etc.). See
  // Constants.java.
  // dim3 - 0..n-1 repetitions of the facet. For example if the facet is names
  // and there are n
  // names, dim 3 will be n-1.
  // dim4 - 0..i-1 for the i categories of the facet. ex. classification facet
  // has a term, a term id,
  // and a thesaurus for each classification (so in this case i is 3).
  // lon/lat facet is an array of 2 doubles, 0 - longitude
  // 1 - latitude
  //
  // bounding box facet is an array of 4 doubles, 0 - x coord of upper left
  // 1 - y coord of upper left
  // 2 - x coord of lower right
  // 3 - y coord of lower right
  //
  // no form of results ranking is being done here. adl has a hook for it in a
  // user-supplied
  // comparison function that the gazetteer uses, although there is a
  // significant impact on
  // performance to doing so from within the adl server.

  // takes a list of reports and navigates the in-memory JAXB datastructure,
  // puts desired information
  // into the more straightforwardly accessed datastructure Object[][][]
  // described above.
}
