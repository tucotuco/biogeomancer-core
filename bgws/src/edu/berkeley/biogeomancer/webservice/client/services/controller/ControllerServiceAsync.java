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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The corresponding asynchronous interface for the controller service.
 * 
 * 
 */
public interface ControllerServiceAsync {

  public void georeference(String locality, String higherGeog, String interp,
      AsyncCallback cb);
  public void batchGeoreference(String xmlRequest, AsyncCallback cb);
}
