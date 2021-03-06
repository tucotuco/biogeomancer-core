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

public enum ClauseState {
  CLAUSE_CREATED, CLAUSE_CREATION_ERROR, CLAUSE_PARSED, CLAUSE_PARSE_ERROR, CLAUSE_FEATURE_NOT_FOUND_ERROR, CLAUSE_POINT_RADIUS_CREATION_ERROR, CLAUSE_POINT_RADIUS_COMPLETED, CLAUSE_POLYGON_COMPLETED;
}