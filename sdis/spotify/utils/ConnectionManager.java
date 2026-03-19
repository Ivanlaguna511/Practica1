package sdis.spotify.utils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe rate limiter to track login/connection attempts per IP.
 */
public class ConnectionManager {
    private final ConcurrentHashMap<String, Integer> attemptsById = new ConcurrentHashMap<>();
    private final int maxAttempts;

    public ConnectionManager(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public synchronized boolean isIpBanned(String ipAddress) {
        return attemptsById.getOrDefault(ipAddress, 0) >= maxAttempts;
    }

    public synchronized void registerAttempt(String ipAddress) {
        attemptsById.merge(ipAddress, 1, Integer::sum);
    }

    public synchronized void registerDisconnect(String ipAddress) {
        attemptsById.computeIfPresent(ipAddress, (ip, count) -> count > 1 ? count - 1 : null);
    }

    public Integer getAttempts(String ipAddress) {
        return attemptsById.getOrDefault(ipAddress, 0);
    }
}