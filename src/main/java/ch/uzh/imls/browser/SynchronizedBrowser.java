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

/**
 * An adapter to make any {@link Browser} thread-safe by serializing all requests to
 * {@link #fetch(String)} so that only one thread a time can use it (others will block).
 * <p/>
 * Thread-safe. No client-side locking required. To guarantee thread-safety, the given
 * browser instance should be exclusively owned by this class.
 * <p/>
 * <p>
 * The reason for using this class is memory leak when HtmlUnit's WebClient is used
 * via ThreadLocal in a thread-pool where threads start and stop.
 * </p>
 *
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public final class SynchronizedBrowser implements Browser {

    /**
     * Guards access to <code>#getPage</code> so that only one thread at a time can use it.
     * Rationale: htmlunit spawns a thread per webWindow and when multiple webclients are
     * used from multiple threads (each thread local), it creates a memory leak even
     * with closeAllWindow() calls.
     */
    private final Object lock = new Object();

    private final Browser browser;

    public SynchronizedBrowser(Browser browser) {
        this.browser = browser;
    }

    @Override
    public String fetch(String url) {
        synchronized (lock) {
            return browser.fetch(url);
        }
    }
}
