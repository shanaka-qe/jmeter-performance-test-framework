// Token Extractor Script
// This Groovy script extracts authentication tokens from JSON responses
// Usage: Add this as a JSR223 PostProcessor after your authentication/login request

import groovy.json.JsonSlurper

// Get the response body from the previous HTTP request
def responseBody = prev.getResponseDataAsString()

// Check if response is not empty
if (responseBody == null || responseBody.isEmpty()) {
    log.error("Response body is empty. Cannot extract token.")
    return
}

try {
    // Parse the JSON response
    def jsonSlurper = new JsonSlurper()
    def jsonResponse = jsonSlurper.parseText(responseBody)
    
    // Extract access token (adjust path based on your API response structure)
    def accessToken = jsonResponse?.access_token ?: jsonResponse?.data?.token ?: jsonResponse?.token
    
    if (accessToken != null) {
        // Store the access token in JMeter variables
        vars.put("access_token", accessToken.toString())
        log.info("Access token extracted successfully")
        
        // Optional: Extract refresh token if available
        def refreshToken = jsonResponse?.refresh_token ?: jsonResponse?.data?.refresh_token
        if (refreshToken != null) {
            vars.put("refresh_token", refreshToken.toString())
            log.info("Refresh token extracted successfully")
        }
        
        // Optional: Extract token expiry time
        def expiresIn = jsonResponse?.expires_in ?: jsonResponse?.data?.expires_in
        if (expiresIn != null) {
            vars.put("token_expires_in", expiresIn.toString())
            log.info("Token expiry: " + expiresIn + " seconds")
        }
        
        // Optional: Extract token type (Bearer, etc.)
        def tokenType = jsonResponse?.token_type ?: "Bearer"
        vars.put("token_type", tokenType.toString())
        
        // Store the full authorization header value
        def authHeader = tokenType + " " + accessToken
        vars.put("authorization_header", authHeader)
        log.info("Authorization header prepared: " + tokenType + " [TOKEN]")
        
    } else {
        log.error("Token not found in response. Check API response structure.")
        log.debug("Response body: " + responseBody)
    }
    
} catch (Exception e) {
    log.error("Error parsing JSON response: " + e.getMessage())
    log.debug("Response body: " + responseBody)
}

