// UUID Generator Script
// This Groovy script generates a random UUID and stores it in JMeter variables
// Usage: Add this as a JSR223 PreProcessor or Sampler
// The generated UUID will be stored in variable 'uuid' by default

import java.util.UUID

// Generate a random UUID
def generatedUUID = UUID.randomUUID().toString()

// Store the UUID in JMeter variables
// You can access this later using ${uuid} in your test plan
vars.put("uuid", generatedUUID)

// Optional: Store it with a custom variable name
// Uncomment and modify the line below if you need a different variable name
// vars.put("requestId", generatedUUID)

// Log the generated UUID for debugging purposes
log.info("Generated UUID: " + generatedUUID)

// Return the UUID (optional, for use in assertions or other scripts)
return generatedUUID

