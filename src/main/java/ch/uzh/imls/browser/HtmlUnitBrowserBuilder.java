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

import com.gargoylesoftware.htmlunit.*;

import java.io.IOException;
import java.net.URL;

/**
 * A builder for {@link HtmlUnitBrowser}. The underlying webClient can be
 * configured by overriding {@link #configureWebClient(com.gargoylesoftware.htmlunit.WebClient)}.
 *
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public final class HtmlUnitBrowserBuilder {
    private static final int DEFAULT_BACKGROUND_JAVASCRIPT_WAITING_PERIOD = 500;
    private static final BrowserVersion DEFAULT_BROWSER_VERSION = BrowserVersion.FIREFOX_3_6;
    private static final URLFilter ACCEPT_EVERYTHING_FILTER = new URLFilter() {
        @Override
        public boolean accept(URL url) {
            return true;
        }
    };
    private URLFilter urlFilter = ACCEPT_EVERYTHING_FILTER;
    private BrowserVersion browserVersion = DEFAULT_BROWSER_VERSION;
    private Integer backgroundJavascriptWaitingPeriod = DEFAULT_BACKGROUND_JAVASCRIPT_WAITING_PERIOD;

    public HtmlUnitBrowser build() {
        return new HtmlUnitBrowser(makeWebClient(), backgroundJavascriptWaitingPeriod);
    }

    public HtmlUnitBrowserBuilder withBrowserVersion(BrowserVersion version) {
        if (version != null) {
            this.browserVersion = version;
        }
        return this;
    }

    /**
     * @param urlFilter defines which resources to fetch and which not
     * @return builder itself
     */
    public HtmlUnitBrowserBuilder withUrlFilter(URLFilter urlFilter) {
        if (urlFilter != null) {
            this.urlFilter = urlFilter;
        }
        return this;
    }

    public HtmlUnitBrowserBuilder withBackgroundJavascriptWaitingPeriod(Integer backgroundJavascriptWaitingPeriod) {
        if (backgroundJavascriptWaitingPeriod != null && backgroundJavascriptWaitingPeriod <= 0) {
            throw new IllegalArgumentException("backgroundJavascriptWaitingPeriod can't be negative");
        }
        this.backgroundJavascriptWaitingPeriod = backgroundJavascriptWaitingPeriod;
        return this;
    }

    /**
     * Extension point, override to configure webClient.
     *
     * @param webClient the client to be configured
     */
    protected void configureWebClient(WebClient webClient) {
    }

    private WebClient makeWebClient() {
        WebClient webClient = new WebClient(browserVersion) {
            @Override
            public WebResponse loadWebResponse(WebRequest request) throws IOException {
                try {
                    if (urlFilter.accept(request.getUrl())) {
                        return super.loadWebResponse(request);
                    }
                    return new StringWebResponse("", request.getUrl());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        webClient.setJavaScriptEnabled(true);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());

        webClient.setThrowExceptionOnScriptError(false);
        webClient.setRedirectEnabled(true);
        webClient.setCssEnabled(false);
        webClient.setActiveXNative(false);
        webClient.setAppletEnabled(false);

        configureWebClient(webClient);
        return webClient;
    }


}
