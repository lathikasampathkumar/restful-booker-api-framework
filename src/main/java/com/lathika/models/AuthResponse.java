package com.lathika.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Response body of {@code POST /auth}.
 * <pre>
 * success: { "token": "abc123" }
 * failure: { "reason": "Bad credentials" }   (Restful Booker returns this with status 200)
 * </pre>
 *
 * @author Lathika Sampathkumar
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthResponse {
    private String token;
    private String reason;

    /** Required by Jackson for deserialization. */
    public AuthResponse() {}

    /** @return the auth token, or {@code null} if login failed */
    public String getToken() { return token; }
    /** @param token the auth token */
    public void setToken(String token) { this.token = token; }

    /** @return the failure reason, or {@code null} if login succeeded */
    public String getReason() { return reason; }
    /** @param reason the failure reason */
    public void setReason(String reason) { this.reason = reason; }
}
