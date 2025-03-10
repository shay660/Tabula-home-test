package tests;

import categories.OSAnalyzer;
import main.ParsedLog;
import org.mockito.Mockito;
import ua_parser.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua_parser.Parser;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


class OSAnalyzerTest {
    private OSAnalyzer osAnalyzer;
    Parser parser = new Parser();

    @BeforeEach
    void setUp() {
        osAnalyzer = new OSAnalyzer("Operating Systems");
    }

    @Test
    void testAddLogWithValidOs() {
        // Create a real Client object with a valid OS family
        String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:15.0) Gecko/20100101 Firefox/15.0.1";
        Client client = parser.parse(userAgent);

        ParsedLog parsedLog = Mockito.mock(ParsedLog.class);
        Mockito.when(parsedLog.getUserAgentClient()).thenReturn(client);

        osAnalyzer.addLogToCounter(parsedLog);
        Map<String, Integer> counter = osAnalyzer.getCounter();
        assertEquals(1, counter.get("Windows"), "Windows OS should be counted once");
    }

    @Test
    void testAddMultipleLogsToCounter() {
        // Create mock ParsedLogs
        ParsedLog mockLog1 = Mockito.mock(ParsedLog.class);
        ParsedLog mockLog2 = Mockito.mock(ParsedLog.class);
        ParsedLog mockLog3 = Mockito.mock(ParsedLog.class);

        // Create mock Client objects, 2 Windows (1, 3) 1 iOS (2)
        Client client1 = parser.parse("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:15.0) Gecko/20100101 Firefox/15.0.1");
        Client client2 = parser.parse(  "65.34.248.51 - - [20/Jan/2013:04:33:29 -0600] " +
                        "\"GET /?utm_source=Contextin&utm_term=E4AFE73EA95769781618402254&utm_campaign=19056&utm_content=&utm_medium=0" +
                        " HTTP/1.1\" 200 9983 \"-\" \"Mozilla/5.0 (iPad; CPU OS 6_0_1 like Mac OS X) AppleWebKit/536.26" +
                        " (KHTML, like Gecko) Version/6.0 Mobile/10A523 Safari/8536.25\" 625 10246 - " +
                        "233945");
        Client client3 = parser.parse("173.217.189.152 - - [27/Jan/2013:18:19:51 -0600] \"GET " +
                "/?utm_source=Contextin&utm_term=RMX_UINoClickTrack&utm_campaign=21096&utm_content=" +
                "1&utm_medium=RMX_UINoClickTrack HTTP/1.1\" 200 9983 \"http://ads.creafi-online-media.com" +
                "/st?ad_type=iframe&ad_size=300x250&section=3923930&pub_url=${PUB_URL}\" \"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.56 Safari/537.17\" 615 10246 - 126652");

        Mockito.when(mockLog1.getUserAgentClient()).thenReturn(client1);
        Mockito.when(mockLog2.getUserAgentClient()).thenReturn(client2);
        Mockito.when(mockLog3.getUserAgentClient()).thenReturn(client3);

        osAnalyzer.addLogToCounter(mockLog1);
        osAnalyzer.addLogToCounter(mockLog2);
        osAnalyzer.addLogToCounter(mockLog3);

        Map<String, Integer> counter = osAnalyzer.getCounter();
        assertEquals(2, counter.get("Windows"));
        assertEquals(1, counter.get("iOS"));
    }

    @Test
    void testGetCategoryName() {
        assertEquals("Operating Systems", osAnalyzer.getCategoryName());
    }
}
