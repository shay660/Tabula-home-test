package tests;

import categories.BrowsersAnalyzer;
import main.ParsedLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua_parser.Client;
import ua_parser.Parser;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BrowsersAnalyzerTest {
    private static final String FIREFOX_LOG1 = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:15.0) Gecko/20100101 Firefox/15.0.1";
    private static final String FIREFOX_LOG2 = "\"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.0.6) G" +
            "ecko/20060728 Firefox/1.5.0.6\"";
    private static final String SAFARI_LOG = "\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) " +
            "AppleWebKit/536.26.17 (KHTML, like Gecko) Version/6.0.2 Safari/536.26.17\"";
    private BrowsersAnalyzer browsersAnalyzer;
    Parser parser = new Parser();

    @BeforeEach
    void setUp() {
        browsersAnalyzer = new BrowsersAnalyzer("Browsers");
    }

    @Test
    void testAddLogWithValidOs() {
        // Create a real Client object with a valid OS family
        String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:15.0) Gecko/20100101 Firefox/15.0.1";
        Client client = parser.parse(userAgent);

        ParsedLog parsedLog = mock(ParsedLog.class);
        when(parsedLog.getUserAgentClient()).thenReturn(client);

        browsersAnalyzer.addLogToCounter(parsedLog);
        Map<String, Integer> counter = browsersAnalyzer.getCounter();
        assertEquals(counter.get("Firefox"), 1);
    }

    @Test
    void testAddMultipleLogsToCounter() {
        // Create mock ParsedLogs
        ParsedLog mockLog1 = mock(ParsedLog.class);
        ParsedLog mockLog2 = mock(ParsedLog.class);
        ParsedLog mockLog3 = mock(ParsedLog.class);

        // Create mock Client objects, 2 Firefox (Clients 1 and 3) 1 Safari (Client2)
        Client client1 = parser.parse(FIREFOX_LOG1);
        Client client2 = parser.parse(SAFARI_LOG);
        Client client3 = parser.parse(FIREFOX_LOG2);

        when(mockLog1.getUserAgentClient()).thenReturn(client1);
        when(mockLog2.getUserAgentClient()).thenReturn(client2);
        when(mockLog3.getUserAgentClient()).thenReturn(client3);

        browsersAnalyzer.addLogToCounter(mockLog1);
        browsersAnalyzer.addLogToCounter(mockLog2);
        browsersAnalyzer.addLogToCounter(mockLog3);

        Map<String, Integer> counter = browsersAnalyzer.getCounter();
        assertEquals(2, counter.get("Firefox"));
        assertEquals(1, counter.get("Safari"));
    }

    @Test
    void testGetCategoryName() {
        assertEquals("Browsers", browsersAnalyzer.getCategoryName());
    }
}

