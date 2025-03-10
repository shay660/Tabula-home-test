package categories;

import main.ParsedLog;
import ua_parser.Client;

import java.util.HashMap;
import java.util.Map;

/**
 * OSAnalyzer counts occurrences of operating systems (OS) from log entries.
 * It implements the CategoryAnalyzer interface to process logs, extract OS families,
 * and track how many times each OS appears.
 */
public class OSAnalyzer implements CategoryAnalyzer {

    /** A map to store the count of each OS family. */
    private final HashMap<String, Integer> osCounter = new HashMap<>();
    private final String name;

    /**
     * Creates an OSAnalyzer with the specified category name.
     *
     * @param name the name of the category
     */
    public OSAnalyzer(String name) {
        this.name = name;
    }

    /**
     * Adds a parsed log entry to the OS counter.
     * Extracts the OS family from the log's User-Agent and updates the count.
     *
     * @param log the parsed log entry
     */
    @Override
    public void addLogToCounter(ParsedLog log) {
        Client client = log.getUserAgentClient();
        if (client != null) {
            String os = client.os.family;
            if (os != null) {
                int newVal = osCounter.getOrDefault(os, 0) + 1;
                osCounter.put(os, newVal);
            }
        }
    }

    /**
     * Returns the map of OS families and their respective counts.
     *
     * @return a map where keys are OS family names and values are their counts
     */
    @Override
    public Map<String, Integer> getCounter() {
        return osCounter;
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
}
