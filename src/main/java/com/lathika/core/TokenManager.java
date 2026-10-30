package com.lathika.core;

import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lathika.api.AuthApi;
import com.lathika.config.ConfigReader;

/**
 * Caches the auth token for the whole suite and refreshes it shortly before it expires.
 * <p>
 * Instead of logging in before every test, tests call {@link #getToken()}, which returns
 * the cached token and only calls {@code POST /auth} when there is no token yet or when
 * the token is within the refresh buffer of its lifetime:
 * <pre>
 * refresh when: now &gt;= fetchedAt + token.ttl.seconds - token.refresh.buffer.seconds
 * </pre>
 * Both values come from config and can be overridden in CI with {@code -D}.
 * </p>
 * <p>
 * {@code synchronized} ensures parallel tests never log in at the same time or read a
 * half-updated token.
 * </p>
 *
 * @author Lathika Sampathkumar
 */
public class TokenManager {

    private static final Logger log = LogManager.getLogger(TokenManager.class);

    /** The cached token, shared by all tests in the run. */
    private static String token;

    /** When the cached token was fetched. */
    private static Instant fetchedAt;

    /**
     * Returns a valid auth token, fetching a new one only when needed.
     *
     * @return the current token
     */
    public static synchronized String getToken() {
        if (token == null || isAboutToExpire()) {
            log.info("Fetching a new auth token");
            token = new AuthApi().getToken();
            fetchedAt = Instant.now();
        }
        return token;
    }

    /**
     * Checks whether the cached token has reached its refresh point
     * (lifetime minus buffer, e.g. 600 s - 60 s = refresh after 9 minutes).
     *
     * @return {@code true} if a new token should be fetched
     */
    private static boolean isAboutToExpire() {
        long ttl    = ConfigReader.getInt("token.ttl.seconds");
        long buffer = ConfigReader.getInt("token.refresh.buffer.seconds");
        Instant refreshAt = fetchedAt.plusSeconds(ttl - buffer);
        return !Instant.now().isBefore(refreshAt);
    }
}
