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

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * An HtmlUnit-based {@link Browser} that can open GWT apps by waiting
 * for all async JavaScript to finish.
 * <p/>
 * <p>
 * This class is <em>NOT</em> thread safe.
 * </p>
 *
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class HtmlUnitBrowser implements Browser {

    private static final Logger log = Logger.getLogger(HtmlUnitBrowser.class);

    /**
     * important, need to give the headless browser enough time to execute JavaScript.
     */
    private final Integer backgroundJavascriptWaitingPeriod;

    private final WebClient webClient;

    HtmlUnitBrowser(WebClient webClient, Integer backgroundJavascriptWaitingPeriod) {
        if (webClient == null || backgroundJavascriptWaitingPeriod == null || backgroundJavascriptWaitingPeriod < 0) {
            throw new ExceptionInInitializerError();
        }

        this.webClient = webClient;
        this.backgroundJavascriptWaitingPeriod = backgroundJavascriptWaitingPeriod;
    }

    /**
     * Fetch the page at <code>url</code>, execute any javascript if present, and return the content.
     */
    @Override
    public String fetch(String url) {
        final long start = System.currentTimeMillis();
        final HtmlPage page;
        try {
            page = webClient.getPage(url);
        } catch (IOException e) {
            throw new RuntimeException("failed to open " + url, e);
        }
        //setting NicelyResynchronizingAjaxController doesn't work with GWT, need to wait
        webClient.waitForBackgroundJavaScript(backgroundJavascriptWaitingPeriod);
        //start fresh next time ?
        webClient.closeAllWindows();
        String asXml = page.asXml();

        final long runtime = (System.currentTimeMillis() - start);
        if (log.isDebugEnabled()) {
            log.debug(url + " runtime [ms]: " + runtime);
        }

        return asXml;
    }
}
