package categories;

import main.ParsedLog;
import java.util.Map;

/**
 * The {@code CategoryAnalyzer} interface defines the contract for analyzing log data
 * based on specific categories (such as countries, operating systems, or browsers).
 * Implementations of this interface are responsible for extracting relevant data from
 * log entries and maintaining a count of occurrences per category value.
 */
public interface CategoryAnalyzer {

    /**
     * Processes a parsed log entry and updates the internal counter for the relevant category.
     *
     * @param log the {@link ParsedLog} object representing a single log entry.
     */
    void addLogToCounter(ParsedLog log);

    /**
     * Returns the counter that maps category values to their occurrence counts.
     *
     * @return a {@link Map} where keys are category names (e.g., country names, browser types)
     * and values are the counts of occurrences.
     */
    Map<String, Integer> getCounter();

    /**
     * Returns the name of the category that this analyzer represents.
     *
     * @return the category name as a {@link String} (e.g., "Countries", "Operating Systems").
     */
    String getCategoryName();
}
