/**
 * Copyright (C) 2012 Swiss Institute of Bioinformatics (www.isb-sib.ch)
 * and Institute of Molecular Life Sciences, University of Zürich.

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.uzh.imls.browser;

import java.net.URL;

/**
 * This filter can be used to provide control over which resources are fetched.
 *
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public interface URLFilter {
    boolean accept(URL resource);
}
