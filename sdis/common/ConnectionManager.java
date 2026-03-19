package sdis.common;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe manager to track and limit connection or login attempts per IP.
 * Protects the server against brute-force attacks.
 */
public class ConnectionManager {
    private final ConcurrentHashMap<String, Integer> attemptsByIp = new ConcurrentHashMap<>();
    private final int maxAttempts;

    public ConnectionManager(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    /**
     * Checks if a specific IP has exceeded the maximum allowed attempts.
     */
    public synchronized boolean isIpBanned(String ipAddress) {
        return attemptsByIp.getOrDefault(ipAddress, 0) >= maxAttempts;
    }

    /**
     * Increments the attempt counter for a given IP address.
     */
    public synchronized void registerAttempt(String ipAddress) {
        attemptsByIp.merge(ipAddress, 1, Integer::sum);
    }

    public int getAttempts(String ipAddress) {
        return attemptsByIp.getOrDefault(ipAddress, 0);
    }
}