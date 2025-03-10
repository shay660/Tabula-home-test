package categories;

import categories.CategoryAnalyzer;
import main.ParsedLog;
import ua_parser.Client;

import java.util.HashMap;
import java.util.Map;

/**
* BrowsersAnalyzer counts occurrences of Browsers from log entries.
* It implements the CategoryAnalyzer interface to process logs, extract browsers families,
* and track how many times each browser appears.
*/
public class BrowsersAnalyzer implements CategoryAnalyzer {

    /* A map to store the count of each browser family. */
    private final Map<String, Integer> browsersCounter = new HashMap<>();

    /**
     * The name of the category for this analyzer (in this case, "Browsers").
     */
    private String name;

    /**
     * Constructs a new {@code BrowsersAnalyzer} with the specified category name.
     *
     * @param name the name of the category for this analyzer
     */
    public BrowsersAnalyzer(String name) {
        this.name = name;
    }

    /**
     * Adds a log entry to the browser counter by extracting the browser family from the user agent.
     * If the browser family is already present in the map, its count is incremented;
     * otherwise, it is added with a count of 1.
     *
     * @param log the {@link ParsedLog} object containing the user agent information to be processed
     */
    @Override
    public void addLogToCounter(ParsedLog log) {
        Client client = log.getUserAgentClient();
        String browser = client.userAgent.family;
        if (browser != null) {
            int updatedVal = browsersCounter.getOrDefault(browser, 0) + 1;
            browsersCounter.put(browser, updatedVal);
        }
    }

    /**
     * Retrieves the counter map containing the count of each browser family.
     * The map's keys are browser family names, and the values are the respective counts.
     *
     * @return a map where the keys are browser family names, and the values are their respective counts
     */
    @Override
    public Map<String, Integer> getCounter() {
        return browsersCounter;
    }

    /**
     * Retrieves the name of the category for this analyzer.
     *
     * @return the name of the category (e.g., "Browsers")
     */
    @Override
    public String getCategoryName() {
        return name;
    }
}
