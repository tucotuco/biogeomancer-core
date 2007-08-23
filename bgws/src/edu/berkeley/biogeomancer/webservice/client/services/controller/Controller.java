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
package edu.berkeley.biogeomancer.webservice.client.services.controller;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * The <code>ControllerAsync<code> class is a singleton that is used to get an
 * instance of the asynchronous controller. All asynchronous services are called 
 * using this class. Typical use case:
 * 
 * ControllerAsync.getInstance().someMethod(new AsyncCallback() {
 *   public void onFailure(Throwable caught) {
 *     // handle failure...
 *   }
 *   public void onSuccess(Object result) {
 *     // handle success...
 *   }
 * });
 *
 */
public class Controller {
  private static ControllerServiceAsync controller;

  /**
   * Private constructor that initializes the controller singleton.
   */
  private Controller() {
    init();
  }

  /**
   * 
   */
  private static void init() {
    controller = (ControllerServiceAsync) GWT.create(ControllerService.class);
    ServiceDefTarget target = (ServiceDefTarget) controller;
    String url = GWT.getModuleBaseURL();
    url += "/controller";
    target.setServiceEntryPoint(url);
  }

  /**
   * Returns the asynchronous controller instance.
   * 
   * @return ControllerServiceAsync
   */
  public static ControllerServiceAsync getInstance() {
    if (controller == null) {
      init();
    }
    return controller;
  }
}