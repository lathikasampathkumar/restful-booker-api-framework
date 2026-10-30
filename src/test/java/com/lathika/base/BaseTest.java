package com.lathika.base;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import com.lathika.api.AuthApi;
import com.lathika.api.BookingApi;
import com.lathika.core.TokenManager;
import com.lathika.listener.TestListener;

/**
 * Parent class of every test class.
 * <p>
 * Provides the API clients and a valid auth token, and registers {@link TestListener}
 * so every test gets reporting and logging. It holds only what every test class needs;
 * test-specific data setup and cleanup live in each test class.
 * </p>
 * <p>
 * {@code alwaysRun = true} makes these setup methods run even when tests are filtered
 * by group (for example {@code smoke} only).
 * </p>
 *
 * @author Lathika Sampathkumar
 */
@Listeners(TestListener.class)
public class BaseTest {

    /** Client for /booking endpoints. */
    protected BookingApi bookingApi;

    /** Client for /auth, for tests that check login behaviour. */
    protected AuthApi authApi;

    /** Valid auth token for update, patch and delete calls. */
    protected String token;

    /**
     * Creates the API clients once per test class.
     */
    @BeforeClass(alwaysRun = true)
    public void baseSetUp() {
        authApi = new AuthApi();
        bookingApi = new BookingApi();
    }

    /**
     * Before every test, gets a valid token from {@link TokenManager}: the cached one,
     * or a fresh one if the cached token is about to expire.
     */
    @BeforeMethod(alwaysRun = true)
    public void refreshToken() {
        token = TokenManager.getToken();
    }
}
