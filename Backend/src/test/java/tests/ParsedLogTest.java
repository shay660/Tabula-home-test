package tests;

import main.ParsedLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua_parser.Client;
import ua_parser.Parser;

import static org.junit.jupiter.api.Assertions.*;

class ParsedLogTest {

    private Parser parser;

    @BeforeEach
    void setUp() {
        // Initialize the parser before each test case
        parser = new Parser();
    }

    @Test
    void testExtractIp() {
        // Test a valid log with an IP address
        String log = "82.166.148.154 - - [01/Mar/2025:12:34:56 +0000] \"GET / HTTP/1.1\" 200 1024";
        ParsedLog parsedLog = new ParsedLog(log);
        assertEquals("82.166.148.154", parsedLog.getIp());
    }

    @Test
    void testExtractUserAgent() {
        // Test a valid log with a User-Agent string
        String log = "82.166.148.154 - - [20/Jan/2013:06:56:32 -0600] \"GET" +
                "/wp-content/themes/twentytwelve/js/navigation.js?ver=1.0 HTTP/1.1\" 200 863" +
                "\"http://creditcardandloanoffers.com/?utm_source=Contextin&utm_term=_\" \"Mozilla/5.0 " +
                "(Windows NT 6.1; WOW64; rv:15.0) Gecko/20100101 Firefox/15.0.1\" 361 1133 - 5625";
        ParsedLog parsedLog = new ParsedLog(log);
        assertNotNull(parsedLog.getUserAgent());
        assertEquals("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:15.0) Gecko/20100101 Firefox/15.0.1",
                parsedLog.getUserAgent());
    }

    @Test
    void testParseUserAgent() {
        // Test that the User-Agent string is correctly parsed into a Client object
        String log = "82.166.148.154 - - [20/Jan/2013:06:56:32 -0600] \"GET\n" +
                "/wp-content/themes/twentytwelve/js/navigation.js?ver=1.0 HTTP/1.1\" 200 863\n" +
                "\"http://creditcardandloanoffers.com/?utm_source=Contextin&utm_term=_\" \"Mozilla/5.0 (Windows\n" +
                "NT 6.1; WOW64; rv:15.0) Gecko/20100101 Firefox/15.0.1\" 361 1133 - 5625";
        ParsedLog parsedLog = new ParsedLog(log);
        Client client = parsedLog.getUserAgentClient();
        assertNotNull(client);
        assertEquals("Firefox", client.userAgent.family);
        assertEquals("15", client.userAgent.major);
    }

    @Test
    void testMissingUserAgent() {
        // Test a log with no User-Agent string
        String log = "82.166.148.154 - - [01/Mar/2025:12:34:56 +0000] \"GET / HTTP/1.1\" 200 1024";
        ParsedLog parsedLog = new ParsedLog(log);
        assertEquals("Other", parsedLog.getUserAgentClient().userAgent.family);
    }

    @Test
    void testMissingIp() {
        // Test a log with no IP address
        String log = "- - - [01/Mar/2025:12:34:56 +0000] \"GET / HTTP/1.1\" 200 1024 \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36\"";
        ParsedLog parsedLog = new ParsedLog(log);
        assertNull(parsedLog.getIp());
    }

    @Test
    void testCacheUserAgentParsing() {
        // Ensure that the same User-Agent is parsed only once (cache behavior)
        String log1 = "82.166.148.154 - - [01/Mar/2025:12:34:56 +0000] \"GET / HTTP/1.1\" 200 1024 \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36\"";
        String log2 = "65.34.248.51 - - [01/Mar/2025:12:34:56 +0000] \"GET / HTTP/1.1\" 200 1024 \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36\"";

        ParsedLog parsedLog1 = new ParsedLog(log1);
        ParsedLog parsedLog2 = new ParsedLog(log2);

        assertSame(parsedLog1.getUserAgentClient(), parsedLog2.getUserAgentClient(), "The same User-Agent should not be parsed twice.");
    }
}
