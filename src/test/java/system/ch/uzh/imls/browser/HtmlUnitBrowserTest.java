package system.ch.uzh.imls.browser;


import ch.uzh.imls.browser.HtmlUnitBrowser;
import ch.uzh.imls.browser.HtmlUnitBrowserBuilder;
import ch.uzh.imls.browser.URLFilter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Milan Simonovic <milan.simonovic@imls.uzh.ch>
 */
public class HtmlUnitBrowserTest {
    private HttpServer server;
    private final String index_html = "<html>\n" +
            "  <head>\n" +
            "   <title>Demo page</title>\n" +
            "   <script type=\"text/javascript\" src=\"/test/paxdb.nocache.js\"></script>" +
            "  </head>\n" +
            "<body>\n" +
            "   <div>some text</div>\n" +
            "   an image <img src=\"/test/some_img.gif\"/>\n" +
            "</body>\n" +
            "</html>\n";

    @Before
    public void startHttpServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", 10101), 2);
        server.setExecutor(null); // creates a default executor
        server.createContext("/test", new HttpHandler() {
            @Override
            public void handle(HttpExchange t) throws IOException {
                t.sendResponseHeaders(HTTP_OK, index_html.length());
                final OutputStream os = t.getResponseBody();
                os.write(index_html.getBytes());
                os.close();
                t.close();
            }
        });
        server.start();
    }

    @Test
    public void test_url_filter() throws Exception {
        final AtomicBoolean jsRequested = new AtomicBoolean(false);
        final HtmlUnitBrowser browser = new HtmlUnitBrowserBuilder().withUrlFilter(new URLFilter() {
            @Override
            public boolean accept(URL url) {
                if (url.getPath().endsWith("paxdb.nocache.js")) {
                    jsRequested.set(true);
                    return false;
                }
                return true;
            }
        }).build();
//        for some reason it's not trying to load the image...
        assertFalse(jsRequested.get());
        final String s = browser.fetch("http://localhost:10101/test/index.html");
        assertTrue(s, jsRequested.get());
    }

    @After
    public void stopHttpServer() {
        server.stop(0);
    }

}
