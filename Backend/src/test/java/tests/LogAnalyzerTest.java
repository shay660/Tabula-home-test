package tests;

import categories.CategoryAnalyzer;
import categories.CountriesAnalyzer;
import categories.BrowsersAnalyzer;
import categories.OSAnalyzer;
import main.LogAnalyzer;
import main.ParsedLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class LogAnalyzerTest {
    private static final String COUNTRIES_CATEGORY_NAME = "Countries";
    private static final String OPERATING_SYSTEMS_CATEGORY_NAME = "Operating systems";
    private static final String BROWSERS_CATEGORY_NAME = "Browsers";
    private LogAnalyzer logAnalyzer;


    @BeforeEach
    void setUP() {
        // 6 logs, 3 US, 2 Germany, 1 Israel
        // 4 Windows, 1 iOS, 1 Android
        // 4 firefox, 1 Safari, 1 Android
        List<String> sampleLogs = Arrays.asList(
                "82.166.148.154 - - [20/Jan/2013:06:56:32 -0600] \"GET" +
                        "/wp-content/themes/twentytwelve/js/navigation.js?ver=1.0 HTTP/1.1\" 200 863" +
                        "\"http://creditcardandloanoffers.com/?utm_source=Contextin&utm_term=_\" \"Mozilla/5.0 " +
                        "(Windows NT 6.1; WOW64; rv:15.0) Gecko/20100101 Firefox/15.0.1\" 361 1133 - 5625", // Israel, Windows, Firefox
                "65.34.248.51 - - [20/Jan/2013:04:33:29 -0600] " +
                        "\"GET /?utm_source=Contextin&utm_term=E4AFE73EA95769781618402254&utm_campaign=19056&utm_content=&utm_medium=0" +
                        " HTTP/1.1\" 200 9983 \"-\" \"Mozilla/5.0 (iPad; CPU OS 6_0_1 like Mac OS X) AppleWebKit/536.26" +
                        " (KHTML, like Gecko) Version/6.0 Mobile/10A523 Safari/8536.25\" 625 10246 - " +
                        "233945", //US, iOS, Safari
                "184.73.21.14 - - [20/Jan/2013:05:10:21 -0600] \"GET / HTTP/1.0\" 200 9983 \"-\" \"Mozilla/5.0" +
                        " (compatible; Windows; U; Windows NT 6.2; WOW64; en-US; rv:12.0) Gecko/20120403211507" +
                        " Firefox/12.0\" 308 10198 - 664093", // US,  Windows, Firefox
                "76.24.130.106 - - [27/Jan/2013:18:08:13 -0600] \"GET " +
                        "/?utm_source=Contextin&utm_term=RMX_UI&utm_campaign=21096&utm_content=1&utm_medium=RMX_UI HTTP/1.1\"" +
                        " 200 9983 \"http://ad.yieldmanager.com/st?ad_type=iframe&ad_size=300x250&section=3850335&pub_url=${PUB_URL}\"" +
                        " \"Mozilla/5.0 (Linux; U; Android 4.0.3; en-us; Transformer TF101 Build/IML74K)" +
                        " AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Safari/534.30\" 606 10246 - " +
                        "126870",  // US, Android, Android Browser
                "80.187.140.26 - - [20/Jan/2013:05:10:21 -0600] \"GET / HTTP/1.0\" 200 9983 \"-\" \"Mozilla/5.0" +
                        " (compatible; Windows; U; Windows NT 6.2; WOW64; en-US; rv:12.0) Gecko/20120403211507" +
                        " Firefox/12.0\" 308 10198 - 664093", // Germany,  Windows, Firefox
                "91.0.0.1 - - [20/Jan/2013:05:10:21 -0600] \"GET / HTTP/1.0\" 200 9983 \"-\" \"Mozilla/5.0" +
                        " (compatible; Windows; U; Windows NT 6.2; WOW64; en-US; rv:12.0) Gecko/20120403211507" +
                        " Firefox/12.0\" 308 10198 - 664093" // Germany,  Windows, Firefox
        );

        CategoryAnalyzer countryAnalyzer = Mockito.mock(CountriesAnalyzer.class);
        CategoryAnalyzer osAnalyzer = Mockito.mock(OSAnalyzer.class);
        CategoryAnalyzer browsersAnalyzer = Mockito.mock(BrowsersAnalyzer.class);
        List<CategoryAnalyzer> analyzersList = new ArrayList<>(Arrays.asList(countryAnalyzer, osAnalyzer,
                browsersAnalyzer));

        Mockito.when(countryAnalyzer.getCategoryName()).thenReturn(COUNTRIES_CATEGORY_NAME);
        Mockito.when(osAnalyzer.getCategoryName()).thenReturn(OPERATING_SYSTEMS_CATEGORY_NAME);
        Mockito.when(browsersAnalyzer.getCategoryName()).thenReturn(BROWSERS_CATEGORY_NAME);


        Map<String, Integer> mockCountryCounter = new HashMap<>();
        mockCountryCounter.put("United States", 3);
        mockCountryCounter.put("Israel", 1);
        mockCountryCounter.put("Germany", 2);
        Mockito.when(countryAnalyzer.getCounter()).thenReturn(mockCountryCounter);

        Map<String, Integer> mockOSCounter = new HashMap<>();
        mockOSCounter.put("Windows", 4);
        mockOSCounter.put("iOS", 1);
        mockOSCounter.put("Android", 1);
        Mockito.when(osAnalyzer.getCounter()).thenReturn(mockOSCounter);

        Map<String, Integer> mockBrowserCounter = new HashMap<>();
        mockBrowserCounter.put("Firefox", 4);
        mockBrowserCounter.put("Safari", 1);
        mockBrowserCounter.put("Android", 1);
        Mockito.when(browsersAnalyzer.getCounter()).thenReturn(mockBrowserCounter);

        logAnalyzer = new LogAnalyzer(sampleLogs, analyzersList);
    }

    @Test
    void testAddAnalyzerBeforeCalculation() {
        CategoryAnalyzer mockAnalyzer = mock(CategoryAnalyzer.class);
        logAnalyzer.addAnalyzer(mockAnalyzer);

        // Ensure the analyzer is added to the analyzers list
        List<CategoryAnalyzer> analyzers = logAnalyzer.getAnalyzers();
        assertTrue(analyzers.contains(mockAnalyzer), "Analyzer should be added.");

        // Verify that addLogToCounter was NOT called, since calculation hasn't been performed yet
        verify(mockAnalyzer, never()).addLogToCounter(ArgumentMatchers.any());
    }

    @Test
    void testAddAnalyzerAfterCalculation() {

        CategoryAnalyzer mockAnalyzer = mock(CategoryAnalyzer.class);

        Mockito.verify(mockAnalyzer, times(0)).addLogToCounter(ArgumentMatchers.any(ParsedLog.class));

        logAnalyzer.calculateParameters();

        logAnalyzer.addAnalyzer(mockAnalyzer);

        // Verify that addLogToCounter was called for each log after calculation
        verify(mockAnalyzer, Mockito.times(6)).addLogToCounter(ArgumentMatchers.any(ParsedLog.class));
    }

    @Test
    void testPrintAllCategories() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        try {
            System.setOut(new PrintStream(outContent));

            logAnalyzer.printAllCategories();
            String output = outContent.toString();

            // check countries print
            assertTrue(output.contains("Countries:"), output);
            assertTrue(output.contains("United States - 50.00%"), output);
            assertTrue(output.contains("Germany - 33.33%"), output);
            assertTrue(output.contains("Israel - 16.67%"), output);

            // check Operating system print
            assertTrue(output.contains("Operating systems:"), output);
            assertTrue(output.contains("Windows - 66.67%"), output);
            assertTrue(output.contains("iOS - 16.67%"), output);
            assertTrue(output.contains("Android - 16.67%"), output);

            // check browsers print
            assertTrue(output.contains("Browsers:"), output);
            assertTrue(output.contains("Firefox - 66.67%"), output);
            assertTrue(output.contains("Safari - 16.67%"), output);
            assertTrue(output.contains("Android - 16.67%"), output);

        } finally {
            System.setOut(originalOut);
        }
    }

}
