// Timestamp Generator Script
// This Groovy script generates various timestamp formats for use in API requests
// Usage: Add this as a JSR223 PreProcessor before your HTTP requests

import java.text.SimpleDateFormat
import java.util.Date

// Get current date and time
def currentDate = new Date()

// ISO 8601 format (e.g., 2025-10-18T14:30:00Z)
def iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"))
def timestampISO = iso8601Format.format(currentDate)
vars.put("timestamp_iso", timestampISO)

// Unix epoch timestamp (seconds since 1970-01-01)
def timestampEpoch = (currentDate.getTime() / 1000).toLong().toString()
vars.put("timestamp_epoch", timestampEpoch)

// Unix epoch timestamp in milliseconds
def timestampEpochMs = currentDate.getTime().toString()
vars.put("timestamp_epoch_ms", timestampEpochMs)

// Custom format: YYYYMMDD-HHmmss (useful for file naming)
def customFormat = new SimpleDateFormat("yyyyMMdd-HHmmss")
def timestampCustom = customFormat.format(currentDate)
vars.put("timestamp_custom", timestampCustom)

// Date only: YYYY-MM-DD
def dateOnlyFormat = new SimpleDateFormat("yyyy-MM-dd")
def dateOnly = dateOnlyFormat.format(currentDate)
vars.put("date_only", dateOnly)

// Time only: HH:mm:ss
def timeOnlyFormat = new SimpleDateFormat("HH:mm:ss")
def timeOnly = timeOnlyFormat.format(currentDate)
vars.put("time_only", timeOnly)

// Log all generated timestamps for debugging
log.info("ISO 8601 Timestamp: " + timestampISO)
log.info("Unix Epoch: " + timestampEpoch)
log.info("Unix Epoch (ms): " + timestampEpochMs)
log.info("Custom Format: " + timestampCustom)
log.info("Date Only: " + dateOnly)
log.info("Time Only: " + timeOnly)

// Return the ISO timestamp as default
return timestampISO

