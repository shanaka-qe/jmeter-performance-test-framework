// Custom Logger Script
// This Groovy script provides enhanced logging capabilities for debugging test execution
// Usage: Add this as a JSR223 PostProcessor to log request/response details

import java.text.SimpleDateFormat
import java.util.Date

// Get current timestamp for log entries
def timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date())

// Get sampler name
def samplerName = sampler.getName()

// Get request details
def requestUrl = sampler.getUrl()
def requestMethod = sampler.getMethod()
def requestHeaders = sampler.getHeaderManager()?.getHeaders()
def requestBody = sampler.getArguments()

// Get response details
def responseCode = prev.getResponseCode()
def responseMessage = prev.getResponseMessage()
def responseTime = prev.getTime() // in milliseconds
def responseBody = prev.getResponseDataAsString()
def responseHeaders = prev.getResponseHeaders()

// Get response size in bytes
def responseBytes = prev.getBytes()

// Check if request was successful (2xx response codes)
def isSuccess = responseCode?.startsWith("2")

// Determine log level based on response code
def logLevel = isSuccess ? "INFO" : "ERROR"

// Create a detailed log entry
def logEntry = """
========================================
[${timestamp}] ${logLevel}: ${samplerName}
========================================
REQUEST:
  Method: ${requestMethod}
  URL: ${requestUrl}
  Headers: ${requestHeaders?.collect { it.getName() + ": " + it.getValue() }?.join(", ")}

RESPONSE:
  Status: ${responseCode} ${responseMessage}
  Time: ${responseTime} ms
  Size: ${responseBytes} bytes
  Success: ${isSuccess}
========================================
"""

// Log based on level
if (isSuccess) {
    log.info(logEntry)
} else {
    log.error(logEntry)
    // For errors, also log response body for debugging
    log.error("Response Body: " + responseBody)
}

// Store response metrics in variables for later analysis
vars.put("last_response_code", responseCode)
vars.put("last_response_time", responseTime.toString())
vars.put("last_response_success", isSuccess.toString())

// Optional: Implement custom metrics tracking
// Track slow responses (over 1 second)
if (responseTime > 1000) {
    log.warn("SLOW RESPONSE DETECTED: ${samplerName} took ${responseTime}ms")
    // Increment slow response counter
    def slowCount = vars.get("slow_response_count")
    if (slowCount == null) {
        slowCount = "0"
    }
    def newCount = Integer.parseInt(slowCount) + 1
    vars.put("slow_response_count", newCount.toString())
}

// Track error count
if (!isSuccess) {
    def errorCount = vars.get("error_count")
    if (errorCount == null) {
        errorCount = "0"
    }
    def newErrorCount = Integer.parseInt(errorCount) + 1
    vars.put("error_count", newErrorCount.toString())
}

