/*
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

package edu.berkeley.biogeomancer.webservice.server.util;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.XPath;

/**
 * The CustomNamespaceDocument class provides support for Document objects with
 * a custom XML namespace.
 * 
 */
public class CustomNamespaceDocument {

  protected static final Logger log = Logger
      .getLogger(CustomNamespaceDocument.class);

  protected final Document doc;

  protected Map<String, String> namespaceMap;

  public CustomNamespaceDocument(Document d) throws NullPointerException {
    if (d == null) {
      log.error("Document was null");
      throw new NullPointerException();
    }
    doc = d;
  }

  /**
   * Returns the underlying Document XML.
   * 
   * @return
   */
  public String asXml() {
    return doc.asXML();
  }

  /**
   * @return the doc
   */
  public Document getDoc() {
    return doc;
  }

  /**
   * Returns the declared xml namespace.
   * 
   * @return the namespace
   */
  public Map<String, String> getNamespaceMap() {
    if (namespaceMap == null) {
      namespaceMap = new HashMap<String, String>();
    }
    namespaceMap.put("xmlns", doc.getRootElement().getNamespace().getText());
    List<Namespace> list = doc.getRootElement().additionalNamespaces();
    for (Namespace n : list) {
      String prefix = n.getPrefix();
      if (n.getPrefix() == " " || n.getPrefix() == null) {
        continue;
      }
      namespaceMap.put(n.getPrefix(), n.getText());
    }
    return namespaceMap;
  }

  /**
   * Returns a List of child Node objects given a parent Node and a element
   * name. The algorithm here is:
   * 
   * <pre>
   * 1) For each namespace, select the nodes
   * 2) If there are no nodes, try the next namespace
   * 3) If there are still no nodes, return null
   * </pre>
   * 
   * @param parent root Node
   * @param elementName the element name
   * @return List<Node>
   */
  public List<Node> getNodes(Node parent, String elementName) {
    String prefix = "";
    if (parent == getRootElement()) {
      prefix = "//";
    }
    XPath xpath;
    List<Node> nodes = null;
    for (String ns : getNamespaceMap().keySet()) {
      xpath = DocumentHelper.createXPath(prefix + ns + ":" + elementName);
      xpath.setNamespaceURIs(getNamespaceMap());
      nodes = xpath.selectNodes(parent);
      if (nodes != null && nodes.isEmpty()) {
        continue;
      }
      return nodes;
    }
    return nodes;
  }

  /**
   * Returns a List of child Node objects given the path.
   * 
   * @param path
   * @return
   */
  public List<Node> getNodes(String path) {
    return getNodes(doc.getRootElement(), path);
  }

  /**
   * Returns the root document node.
   * 
   */
  public Node getRootElement() {
    return doc.getRootElement();

  }
}
