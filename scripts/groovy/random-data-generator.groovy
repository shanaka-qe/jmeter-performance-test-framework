// Random Data Generator Script
// This Groovy script generates random test data for various use cases
// Usage: Add as JSR223 PreProcessor before requests that need randomized data

import java.util.Random
import java.security.SecureRandom

// Initialize random number generator
def random = new Random()
def secureRandom = new SecureRandom()

// ===== Random String Generation =====

// Generate random alphanumeric string
def generateRandomString(int length) {
    def chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    def sb = new StringBuilder(length)
    for (int i = 0; i < length; i++) {
        sb.append(chars.charAt(random.nextInt(chars.length())))
    }
    return sb.toString()
}

// Generate random email address
def generateRandomEmail() {
    def username = generateRandomString(10).toLowerCase()
    def domains = ["example.com", "test.com", "demo.com", "qa.com"]
    return username + "@" + domains[random.nextInt(domains.size())]
}

// Generate random phone number (format: +1-XXX-XXX-XXXX)
def generateRandomPhone() {
    def areaCode = 200 + random.nextInt(800) // 200-999
    def prefix = 200 + random.nextInt(800)   // 200-999
    def lineNumber = random.nextInt(10000)   // 0-9999
    return String.format("+1-%03d-%03d-%04d", areaCode, prefix, lineNumber)
}

// ===== Random Number Generation =====

// Generate random integer between min and max (inclusive)
def generateRandomInt(int min, int max) {
    return min + random.nextInt(max - min + 1)
}

// Generate random price (between 10.00 and 999.99)
def generateRandomPrice() {
    def price = 10.0 + (random.nextDouble() * 989.99)
    return String.format("%.2f", price)
}

// ===== Random Date Generation =====

// Generate random date in the past year
def generateRandomPastDate() {
    def now = System.currentTimeMillis()
    def oneYearInMs = 365L * 24 * 60 * 60 * 1000
    def randomTime = now - random.nextLong().abs() % oneYearInMs
    return new Date(randomTime).format("yyyy-MM-dd")
}

// ===== Random Selection from Lists =====

// Select random item from array
def selectRandom(items) {
    return items[random.nextInt(items.size())]
}

// ===== Generate and Store Variables =====

// Random username
def randomUsername = "user_" + generateRandomString(8).toLowerCase()
vars.put("random_username", randomUsername)

// Random email
def randomEmail = generateRandomEmail()
vars.put("random_email", randomEmail)

// Random phone
def randomPhone = generateRandomPhone()
vars.put("random_phone", randomPhone)

// Random first name
def firstNames = ["John", "Jane", "Michael", "Sarah", "David", "Emily", "Robert", "Lisa"]
def randomFirstName = selectRandom(firstNames)
vars.put("random_first_name", randomFirstName)

// Random last name
def lastNames = ["Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis"]
def randomLastName = selectRandom(lastNames)
vars.put("random_last_name", randomLastName)

// Random age (18-80)
def randomAge = generateRandomInt(18, 80).toString()
vars.put("random_age", randomAge)

// Random product quantity (1-10)
def randomQuantity = generateRandomInt(1, 10).toString()
vars.put("random_quantity", randomQuantity)

// Random price
def randomPrice = generateRandomPrice()
vars.put("random_price", randomPrice)

// Random past date
def randomDate = generateRandomPastDate()
vars.put("random_date", randomDate)

// Random street address
def streetNumbers = generateRandomInt(100, 9999)
def streetNames = ["Main St", "Oak Ave", "Maple Dr", "Park Blvd", "Lake Rd"]
def randomAddress = streetNumbers + " " + selectRandom(streetNames)
vars.put("random_address", randomAddress)

// Random city
def cities = ["New York", "Los Angeles", "Chicago", "Houston", "Phoenix", "Philadelphia"]
def randomCity = selectRandom(cities)
vars.put("random_city", randomCity)

// Random ZIP code
def randomZip = String.format("%05d", generateRandomInt(10000, 99999))
vars.put("random_zip", randomZip)

// Random alphanumeric ID (e.g., for order numbers)
def randomOrderId = "ORD-" + generateRandomString(12).toUpperCase()
vars.put("random_order_id", randomOrderId)

// Random boolean
def randomBoolean = random.nextBoolean().toString()
vars.put("random_boolean", randomBoolean)

// Log generated values for debugging
log.info("Generated random test data:")
log.info("  Username: " + randomUsername)
log.info("  Email: " + randomEmail)
log.info("  Name: " + randomFirstName + " " + randomLastName)
log.info("  Phone: " + randomPhone)
log.info("  Order ID: " + randomOrderId)

