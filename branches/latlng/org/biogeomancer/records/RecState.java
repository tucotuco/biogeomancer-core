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

public enum RecState {
  REC_CREATED, REC_CREATION_ERROR, REC_LOCALITY_EXISTS, REC_NO_LOCALITY_ERROR, REC_CLAUSES_CREATED, REC_NO_CLAUSES_ERROR, REC_LOCTYPES_FOUND, REC_NO_LOCTYPES_ERROR, REC_PARSED, REC_PARSE_ERROR, REC_GEOREFERENCE_COMPLETED, REC_NO_GEOREFERENCE_ERROR, REC_CLEARED;
}