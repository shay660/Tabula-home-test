package tests;

import categories.CountriesAnalyzer;

import main.ParsedLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class CountriesAnalyzerTest {
    public static final String GEOIP_ERR_MSG = "Failed to open the GeoIP database: ";
    private List<String> IPs;
    private CountriesAnalyzer countriesCounter;

    @BeforeEach
    void setUP() {
        // 6 IPs, 3 US, 2 Germany, 1 Israel
        IPs = Arrays.asList("82.166.148.154", "65.34.248.51", "184.73.21.14", "76.24.130.106", "80.187" +
                ".140.26", "91.0.0.1");

        try {
            countriesCounter = new CountriesAnalyzer("Countries");

            for (String ip : IPs) {
                ParsedLog mockParsedLog = Mockito.mock(ParsedLog.class);
                Mockito.when(mockParsedLog.getIp()).thenReturn(ip);
                countriesCounter.addLogToCounter(mockParsedLog);
            }
        } catch (IOException ioE) {
            System.err.println(GEOIP_ERR_MSG + ioE.getMessage());
            System.exit(1);
        }
    }


    @Test
    @DisplayName("Country Category Works")
    void testCountryCategory() {
        assertNotNull(countriesCounter);
        Map<String, Integer> counter = countriesCounter.getCounter();
        assertNotNull(counter, "Countries counter should not be null.");
        assertEquals(counter.get("United States"), 3);
        assertEquals(counter.get("Germany"), 2);
        assertEquals (counter.get("Israel") ,1);

    }


    @Test
    @DisplayName("Countries Counter handle broken logs.")
    void testCountryCounterInvalidLog() {
        ParsedLog mockBrokenLog = Mockito.mock(ParsedLog.class);
        Mockito.when(mockBrokenLog.getIp()).thenReturn(null);
        countriesCounter.addLogToCounter(mockBrokenLog);
        Map<String, Integer> counter = countriesCounter.getCounter();
        assertNotNull(counter);
        // the count should not change
        assertEquals(3, counter.get("United States"));
        assertEquals(2, counter.get("Germany"));
        assertEquals(1, counter.get("Israel"));
    }
}
