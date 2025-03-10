package main;

import ua_parser.Client;
import ua_parser.Parser;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@code ParsedLog} class is responsible for parsing individual log entries.
 * It extracts important components such as the IP address and the User-Agent string,
 * and uses the {@link ua_parser.Parser} to parse the User-Agent into a {@link Client} object.
 * A static cache is maintained to avoid redundant parsing of duplicate User-Agent strings.
 */
public class ParsedLog {

    /* Regex pattern to extract the IP address from the beginning of a log line. */
    private static final Pattern IP_PATTERN = Pattern.compile("^(\\d+\\.\\d+\\.\\d+\\.\\d+)");

    /* Regex pattern to extract the User-Agent string from a log line. */
    private static final Pattern USER_AGENT_PATTERN = Pattern.compile(".*\"([^\"]+)\"[^\"]*$");

    /* Cache to store parsed User-Agent strings and their corresponding {@link Client} objects. */
    private static final Map<String, Client> userAgentsToClient = new ConcurrentHashMap<>();

    /* The User-Agent parser instance used to parse User-Agent strings. */
    private static final Parser parser = new Parser();

    /* The extracted IP address from the log entry. */
    private final String ip;

    /* The extracted raw User-Agent string from the log entry. */
    private final String userAgent;

    /* The parsed User-Agent Client object, or ode null if no User-Agent was found. */
    private final Client userAgentClient;

    /**
     * Constructs a {@code ParsedLog} by parsing the provided log entry.
     *
     * @param log the log entry to parse
     */
    public ParsedLog(String log) {
        ip = extractIP(log);
        userAgent = extractClient(log);
        userAgentClient = userAgent != null
                ? userAgentsToClient.computeIfAbsent(userAgent, parser::parse)
                : null;
    }

    /**
     * Extracts the IP address from the given log entry.
     *
     * @param log the log entry containing the IP address
     * @return the extracted IP address, or {@code null} if not found
     */
    private String extractIP(String log) {
        Matcher matcher = IP_PATTERN.matcher(log);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * Extracts the User-Agent string from the given log entry.
     *
     * @param log the log entry to extract from
     * @return the extracted User-Agent string, or {@code null} if not found
     */
    private String extractClient(String log) {
        Matcher matcher = USER_AGENT_PATTERN.matcher(log);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * Returns the extracted IP address of the log entry.
     *
     * @return the IP address as a {@link String}, or {@code null} if not present
     */
    public String getIp() {
        return ip;
    }

    /**
     * Returns the parsed {@link Client} object of the User-Agent.
     *
     * @return the parsed User-Agent {@link Client}, or {@code null} if no User-Agent was found
     */
    public Client getUserAgentClient() {
        return userAgentClient;
    }

    /**
     * Returns the raw User-Agent string from the log entry.
     *
     * @return the User-Agent string, or {@code null} if not present
     */
    public String getUserAgent() {
        return userAgent;
    }
}
