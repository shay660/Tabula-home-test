package categories;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;
import main.ParsedLog;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.*;
import java.util.regex.Pattern;

/**
 * The {@code CountriesAnalyzer} class analyzes log entries to determine the countries
 * associated with IP addresses using the MaxMind GeoIP2 database.
 * It implements the {@link CategoryAnalyzer} interface and provides:
 *   - Counting occurrences of countries from log entries</li>
 *   - Tracking IPs that cannot be geolocated</li>
 *
 * The country information is resolved via the GeoLite2 Country database.
 */
public class CountriesAnalyzer implements CategoryAnalyzer {

    /* Path to the GeoLite2 Country database file.*/
    private static final String PATH_TO_DATABASE = "src/main/resources/GeoLite2-Country.mmdb";

    private static final String INVALID_LOG_MSG = "Invalid log: ";

    /* A map to store the count of each country found in the logs. */
    private final Map<String, Integer> countryCounter = new HashMap<>();

    /* A set to store IPs that could not be geolocated (e.g., invalid IPs). */
    private final Set<String> notFoundIps = new HashSet<>();
    private final DatabaseReader reader;
    private String name;

    /**
     * Creates a new CountriesAnalyzer with the given name and loads the GeoIP2 database.
     *
     * @param name the name of the category
     * @throws IOException if the database file can't be loaded
     */
    public CountriesAnalyzer(String name) throws IOException {
        this.name = name;
        File database = new File(PATH_TO_DATABASE);
        reader = new DatabaseReader.Builder(database).build();
    }

    /**
     * Adds a log to the country counter by looking up the country of its IP address.
     * If the IP is invalid or can't be found, it's ignored or stored in notFoundIps.
     *
     * @param log the log to process
     */
    public void addLogToCounter(ParsedLog log) {
        try {
            String ip = log.getIp();
            if (ip == null) {
                return;
            }
            String country = getCountryFromIP(ip, reader);
            if (country != null) {
                int newVal = countryCounter.getOrDefault(country, 0) + 1;
                countryCounter.put(country, newVal);
            }
        } catch (IOException ioE) {
            System.err.println(INVALID_LOG_MSG + ioE.getMessage());
        } catch (GeoIp2Exception g) {
            notFoundIps.add(g.getMessage());
        }

    }

    /**
     * Looks up the country name for a given IP address.
     *
     * @param ip the IP address
     * @return the country name, or null if not found
     * @throws IOException if the lookup fails
     * @throws GeoIp2Exception if the lookup fails
     */
    private String getCountryFromIP(String ip, DatabaseReader reader) throws IOException, GeoIp2Exception {
        InetAddress ipAddress = InetAddress.getByName(ip);
        CountryResponse response = reader.country(ipAddress);
        return response.getCountry().getName();
    }


    /**
     * Returns the map of countries and their respective counts.
     *
     * @return a map where keys are country names and values are their respective counts
     */
    @Override
    public Map<String, Integer> getCounter() {
        return countryCounter;
    }

    /**
     * Returns the name of this category.
     *
     * @return the category name
     */
    @Override
    public String getCategoryName() {
        return name;
    }

    /**
     * Returns the set of IP addresses that could not be geolocated.
     *
     * @return a set of IP addresses that failed GeoIP lookup
     */
    public Set<String> getNotFoundIps() {
        return notFoundIps;
    }
}
