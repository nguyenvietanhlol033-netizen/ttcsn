package com.melia.hotel.dto.response;

public class JwtAuthResponse {
    private String accessToken;
    private String tokenType;
    private Long expiresInMs;
    private String userId;
    private String username;
    private String fullName;
    private String role;

    public JwtAuthResponse() {}

    public JwtAuthResponse(String accessToken, String tokenType, Long expiresInMs, String userId, String username, String fullName, String role) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresInMs = expiresInMs;
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String accessToken;
        private String tokenType;
        private Long expiresInMs;
        private String userId;
        private String username;
        private String fullName;
        private String role;

        public Builder accessToken(String accessToken) { this.accessToken = accessToken; return this; }
        public Builder tokenType(String tokenType) { this.tokenType = tokenType; return this; }
        public Builder expiresInMs(Long expiresInMs) { this.expiresInMs = expiresInMs; return this; }
        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder role(String role) { this.role = role; return this; }
        public JwtAuthResponse build() {
            return new JwtAuthResponse(accessToken, tokenType, expiresInMs, userId, username, fullName, role);
        }
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public Long getExpiresInMs() { return expiresInMs; }
    public void setExpiresInMs(Long expiresInMs) { this.expiresInMs = expiresInMs; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
