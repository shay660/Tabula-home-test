package main;

import categories.BrowsersAnalyzer;
import categories.CategoryAnalyzer;
import categories.CountriesAnalyzer;
import categories.OSAnalyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * The LogAnalyzer class processes and analyzes log files to provide statistics
 * on various categories such as countries, operating systems, and browsers.
 * The analysis is performed using different {@link CategoryAnalyzer} implementations,
 * which extract and count relevant data from each log entry.
 */
public class LogAnalyzer {

    // === Constants ===
    private static final String MISSING_ARGUMENT_ERR = "Please provide the path to the logs file as a " +
            "command-line argument.";
    private static final String COUNTRIES_CATEGORY_NAME = "Countries";
    private static final String OPERATING_SYSTEMS_CATEGORY_NAME = "Operating systems";
    private static final String BROWSERS_CATEGORY_NAME = "Browsers";
    private static final String ERROR_READING_LOGS = "Error reading logs: ";
    private static final String ERROR_GEOIP = "Failed to open the GeoIP File: ";
    private static final double PERCENTAGE_MULTIPLIER = 100.0;

    // === Fields ===
    private final List<String> allLogs;
    private final int totalLogs;
    private final List<CategoryAnalyzer> analyzers;
    private boolean calculated = false;

    /**
     * Constructs a {@code LogAnalyzer} with the provided logs and analyzers.
     *
     * @param logs      the list of log entries to analyze.
     * @param analyzers the list of {@link CategoryAnalyzer} objects to use.
     */
    public LogAnalyzer(List<String> logs, List<CategoryAnalyzer> analyzers) {
        this.allLogs = logs;
        this.totalLogs = logs.size();
        this.analyzers = analyzers;
    }


    /**
     * Processes all log entries and updates the analyzers' counters.
     */
    public void calculateParameters() {
        calculated = true;
        for (String log : allLogs) {
            ParsedLog parsedLog = new ParsedLog(log);
            for (CategoryAnalyzer analyzer : analyzers) {
                analyzer.addLogToCounter(parsedLog);
            }
        }
    }

    /**
     * Prints the results of all category analyzers, sorted by their occurrence percentages.
     * If not already calculated, this method will trigger the calculation first.
     */
    public void printAllCategories() {
        if (!calculated) {
            calculateParameters();
        }
        for (CategoryAnalyzer analyzer : analyzers) {
            printCategory(analyzer.getCounter(), analyzer.getCategoryName());
        }
    }


    /**
     * Prints the percentage distribution of a category in descending order.
     *
     * @param counter  a map containing category counts.
     * @param category the headline to display before the category.
     */
    private void printCategory(Map<String, Integer> counter, String category) {
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(counter.entrySet());
        sortedEntries.sort(Map.Entry.<String, Integer>comparingByValue().reversed());
//        int totalLogs = 0;
//        for (int val : counter.values()) {
//            totalLogs += val;
//        }
        System.out.println(category + ":");
        for (var entry : sortedEntries) {
            double percentage = (entry.getValue() * PERCENTAGE_MULTIPLIER) / totalLogs;
            System.out.printf("%s - %.2f%%%n", entry.getKey(), percentage);
        }
        System.out.println();
    }

    /**
     * Adds a new analyzer to the log analyzer.
     * If calculation was already performed, it updates the new analyzer immediately.
     *
     * @param analyzer the {@link CategoryAnalyzer} to add.
     */
    public void addAnalyzer(CategoryAnalyzer analyzer) {
        if (calculated) {
            for (String log : allLogs) {
                ParsedLog parsedLog = new ParsedLog(log);
                analyzer.addLogToCounter(parsedLog);
            }
        }
        analyzers.add(analyzer);
    }

    public List<CategoryAnalyzer> getAnalyzers() {
        return analyzers;
    }

    /**
     * Main method to execute the log analysis.
     *
     * @param args command line arguments. Expects the first argument to be the path to the log file.
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println(MISSING_ARGUMENT_ERR);
            return;
        }
        try {
            String logPathFile = args[0];
            Path logPath = Paths.get(logPathFile);
            List<String> allLogs = Files.readAllLines(logPath);
            CategoryAnalyzer countryAnalyzer = null;
            CategoryAnalyzer osAnalyzer = new OSAnalyzer(OPERATING_SYSTEMS_CATEGORY_NAME);
            CategoryAnalyzer browsersAnalyzer = new BrowsersAnalyzer(BROWSERS_CATEGORY_NAME);
            try {
                countryAnalyzer = new CountriesAnalyzer(COUNTRIES_CATEGORY_NAME);
            } catch (IOException ioE) {
                System.err.println(ERROR_GEOIP + ioE.getMessage());
            }
            List<CategoryAnalyzer> analyzersList = new ArrayList<>(Arrays.asList(osAnalyzer,
                    browsersAnalyzer));
            if (countryAnalyzer != null) {
                analyzersList.add(countryAnalyzer);
            }

            LogAnalyzer logAnalyzer = new LogAnalyzer(allLogs, analyzersList);
            logAnalyzer.calculateParameters();
            logAnalyzer.printAllCategories();

        } catch (IOException ioException) {
            System.err.println(ERROR_READING_LOGS + ioException.getMessage());
        }
    }
}

