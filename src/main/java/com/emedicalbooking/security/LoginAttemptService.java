package com.emedicalbooking.security;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;

@Service
public class LoginAttemptService {

    private final int MAX_ATTEMPT = 5;
    private final int LOCK_TIME_MINUTES = 15;

    // Map lưu số lần thử sai
    private final ConcurrentHashMap<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    // Map lưu thời gian bị khóa
    private final ConcurrentHashMap<String, LocalDateTime> lockCache = new ConcurrentHashMap<>();

    public void loginSucceeded(String key) {
        attemptsCache.remove(key);
        lockCache.remove(key);
    }

    public void loginFailed(String key) {
        int attempts = attemptsCache.getOrDefault(key, 0);
        attempts++;
        attemptsCache.put(key, attempts);
        if (attempts >= MAX_ATTEMPT) {
            lockCache.put(key, LocalDateTime.now().plusMinutes(LOCK_TIME_MINUTES));
        }
    }

    public boolean isBlocked(String key) {
        LocalDateTime lockTime = lockCache.get(key);
        if (lockTime != null) {
            if (LocalDateTime.now().isBefore(lockTime)) {
                return true;
            } else {
                // Đã hết thời gian khóa
                lockCache.remove(key);
                attemptsCache.remove(key);
                return false;
            }
        }
        return false;
    }
}
