package com.lathika.models;

/**
 * Request body of {@code POST /auth}.
 * <pre>
 * { "username": "admin", "password": "password123" }
 * </pre>
 *
 * @author Lathika Sampathkumar
 */
public class AuthRequest {
    private String username;
    private String password;

    /** Required by Jackson. */
    public AuthRequest() {}

    /**
     * @param username login username
     * @param password login password
     */
    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /** @return login username */
    public String getUsername() { return username; }
    /** @param username login username */
    public void setUsername(String username) { this.username = username; }

    /** @return login password */
    public String getPassword() { return password; }
    /** @param password login password */
    public void setPassword(String password) { this.password = password; }
}
