// Dynamic Correlation Script
// This Groovy script extracts dynamic values from responses for correlation
// Useful for session IDs, CSRF tokens, transaction IDs, etc.
// Usage: Add as JSR223 PostProcessor after requests that return dynamic values

import groovy.json.JsonSlurper
import java.util.regex.Pattern
import java.util.regex.Matcher

// Get the response body from the previous HTTP request
def responseBody = prev.getResponseDataAsString()

// Check if response is not empty
if (responseBody == null || responseBody.isEmpty()) {
    log.warn("Response body is empty. No correlation possible.")
    return
}

// ===== JSON-based correlation =====
try {
    // Attempt to parse as JSON
    def jsonSlurper = new JsonSlurper()
    def jsonResponse = jsonSlurper.parseText(responseBody)
    
    // Extract common dynamic values (customize based on your API)
    
    // Session ID
    def sessionId = jsonResponse?.sessionId ?: jsonResponse?.session_id ?: jsonResponse?.data?.sessionId
    if (sessionId != null) {
        vars.put("session_id", sessionId.toString())
        log.info("Session ID extracted: " + sessionId)
    }
    
    // Transaction ID
    def transactionId = jsonResponse?.transactionId ?: jsonResponse?.transaction_id ?: jsonResponse?.txId
    if (transactionId != null) {
        vars.put("transaction_id", transactionId.toString())
        log.info("Transaction ID extracted: " + transactionId)
    }
    
    // CSRF Token
    def csrfToken = jsonResponse?.csrfToken ?: jsonResponse?.csrf_token ?: jsonResponse?._csrf
    if (csrfToken != null) {
        vars.put("csrf_token", csrfToken.toString())
        log.info("CSRF Token extracted: [HIDDEN]")
    }
    
    // User ID
    def userId = jsonResponse?.userId ?: jsonResponse?.user_id ?: jsonResponse?.data?.user?.id
    if (userId != null) {
        vars.put("user_id", userId.toString())
        log.info("User ID extracted: " + userId)
    }
    
    // Request ID (for tracing)
    def requestId = jsonResponse?.requestId ?: jsonResponse?.request_id ?: jsonResponse?.trace_id
    if (requestId != null) {
        vars.put("request_id", requestId.toString())
        log.info("Request ID extracted: " + requestId)
    }
    
    // Correlation ID (for distributed tracing)
    def correlationId = jsonResponse?.correlationId ?: jsonResponse?.correlation_id ?: jsonResponse?.x_correlation_id
    if (correlationId != null) {
        vars.put("correlation_id", correlationId.toString())
        log.info("Correlation ID extracted: " + correlationId)
    }
    
} catch (Exception e) {
    log.debug("Response is not JSON or JSON parsing failed: " + e.getMessage())
}

// ===== Regex-based correlation (for HTML or non-JSON responses) =====

// Extract CSRF token from HTML forms
def csrfPattern = Pattern.compile('name=["\']csrf[_-]?token["\']\\s+value=["\']([^"\']+)["\']', Pattern.CASE_INSENSITIVE)
def csrfMatcher = csrfPattern.matcher(responseBody)
if (csrfMatcher.find()) {
    def csrfToken = csrfMatcher.group(1)
    vars.put("csrf_token", csrfToken)
    log.info("CSRF Token extracted via regex: [HIDDEN]")
}

// Extract session ID from cookies (if present in response headers)
def responseHeaders = prev.getResponseHeaders()
if (responseHeaders != null) {
    def cookiePattern = Pattern.compile('Set-Cookie:\\s*JSESSIONID=([^;]+)', Pattern.CASE_INSENSITIVE)
    def cookieMatcher = cookiePattern.matcher(responseHeaders)
    if (cookieMatcher.find()) {
        def jsessionId = cookieMatcher.group(1)
        vars.put("jsession_id", jsessionId)
        log.info("JSESSIONID extracted from cookies: " + jsessionId)
    }
}

// Extract generic ID patterns (customize regex as needed)
def idPattern = Pattern.compile('"id"\\s*:\\s*"?([a-zA-Z0-9-]+)"?')
def idMatcher = idPattern.matcher(responseBody)
if (idMatcher.find()) {
    def extractedId = idMatcher.group(1)
    vars.put("extracted_id", extractedId)
    log.info("Generic ID extracted: " + extractedId)
}

log.info("Dynamic correlation completed for sampler: " + prev.getSampleLabel())

