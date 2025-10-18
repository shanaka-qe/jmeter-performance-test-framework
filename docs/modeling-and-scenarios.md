# 📐 Load Modeling and Test Scenarios

This document provides guidance on modeling realistic load patterns and designing effective test scenarios for performance testing.

---

## Table of Contents

1. [Load Modeling Principles](#load-modeling-principles)
2. [Arrival Rate Models](#arrival-rate-models)
3. [Ramp-up Strategies](#ramp-up-strategies)
4. [User Journey Scenarios](#user-journey-scenarios)
5. [Think Time](#think-time)
6. [Pacing and Throughput](#pacing-and-throughput)

---

## Load Modeling Principles

### Understanding Your Users

Before designing load tests, understand:

1. **User Demographics**: Who uses your application?
2. **Usage Patterns**: When and how do they use it?
3. **Peak Times**: What are the busiest hours/days?
4. **Geographic Distribution**: Where are users located?
5. **Device Mix**: What devices do they use?

### Production Analysis

Gather metrics from production:

```
Metric                  | How to Get It
------------------------|------------------
Concurrent Users        | APM tools, analytics
Requests per Second     | Server logs, load balancer
Peak Load Times         | Analytics, monitoring
User Session Duration   | Analytics
Page Views per Session  | Analytics
API Call Distribution   | API gateway logs
```

### Load Calculation Example

```
Given:
- Peak hour: 10,000 unique users
- Average session: 15 minutes
- Average think time: 5 seconds between requests
- Average requests per session: 20

Calculate concurrent users:
- Session rate = 10,000 users / 60 min = ~167 sessions/min
- Concurrent users = 167 × 15 = ~2,500 concurrent users

Calculate TPS:
- Total requests = 10,000 × 20 = 200,000 requests/hour
- TPS = 200,000 / 3600 = ~56 TPS
```

---

## Arrival Rate Models

### 1. Constant Load Model

**Description**: Fixed number of users for the entire test duration

**Use Case**: Baseline performance testing

**JMeter Configuration**:
```properties
threads=50
ramp.up=60
duration=1800
```

**Load Pattern**:
```
Users
  │
50├────────────────────────────────────────
  │
  │
 0├─────┬─────────────────────────────────
  0    60s                           1800s
       Time
```

**When to Use**:
- Baseline performance validation
- Steady-state stability testing
- Comparing releases

---

### 2. Step Load Model

**Description**: Incrementally increase load in steps

**Use Case**: Finding capacity limits

**Implementation**:
```
Phase 1: 10 users for 10 minutes
Phase 2: 25 users for 10 minutes
Phase 3: 50 users for 10 minutes
Phase 4: 100 users for 10 minutes
```

**Load Pattern**:
```
Users
   │
100├──────────────────────────
   │                 
 50├─────────────
   │       
 25├──────
   │
 10├
   │
  0└──────┬────────┬────────┬────────
        10m      20m      30m      40m
                 Time
```

**When to Use**:
- Capacity planning
- Stress testing
- Finding breaking points

---

### 3. Spike Load Model

**Description**: Sudden burst of traffic from baseline

**Use Case**: Testing resilience to traffic spikes

**Implementation**:
```properties
# Normal load
threads=20
duration=600

# Spike (separate thread group)
threads=200
ramp.up=30
duration=180
delay=300
```

**Load Pattern**:
```
Users
   │
200├───────
   │       │
   │       │
 20├───────┴──────────────────
   │
   │
  0└───────┬─────┬───────────
         5min  8min
            Time
```

**When to Use**:
- Flash sales preparation
- Breaking news scenarios
- Auto-scaling validation

---

### 4. Wave Pattern Model

**Description**: Cyclical load pattern mimicking daily usage

**Use Case**: Realistic production simulation

**Load Pattern**:
```
Users
   │    ╱╲    ╱╲
100├───╱  ╲──╱  ╲───
   │  ╱    ╲╱    ╲
 50├─╱            ╲─
   │╱              ╲
  0└─────────────────
        Time (24h)
```

**When to Use**:
- Soak testing
- Production-like simulation
- Long-duration tests

---

## Ramp-up Strategies

### Linear Ramp-up

**Description**: Users increase at a constant rate

```
users_per_second = total_users / ramp_up_time
```

**Example**:
```properties
threads=100
ramp.up=300  # 5 minutes
# Rate: 100/300 = 0.33 users/second
```

**Visual**:
```
Users
100 ├                      ╱
    │                    ╱
 50 ├                  ╱
    │                ╱
  0 └──────────────╱
    0            300s
```

**Best For**: Most general scenarios

---

### Fast Ramp-up

**Description**: Quickly reach target load

```properties
threads=100
ramp.up=30  # 30 seconds
# Rate: 100/30 = 3.33 users/second
```

**Visual**:
```
Users
100 ├          ╱
    │        ╱
 50 ├      ╱
    │    ╱
  0 └───╱
    0  30s
```

**Best For**:
- Spike tests
- Stress tests
- Rapid scale-up scenarios

---

### Gradual Ramp-up

**Description**: Slow increase to target load

```properties
threads=100
ramp.up=600  # 10 minutes
# Rate: 100/600 = 0.17 users/second
```

**Best For**:
- Soak tests
- Avoiding warm-up artifacts
- Realistic user growth

---

### Stair-step Ramp-up

**Description**: Multiple thread groups with delayed starts

```
Thread Group 1: 20 users, delay 0s
Thread Group 2: 20 users, delay 300s
Thread Group 3: 20 users, delay 600s
Thread Group 4: 20 users, delay 900s
Thread Group 5: 20 users, delay 1200s
```

**Best For**:
- Step load testing
- Capacity analysis
- Controlled load increase

---

## User Journey Scenarios

### Scenario Design Principles

1. **Realistic**: Mirror actual user behavior
2. **Representative**: Cover common use cases
3. **Varied**: Include different user types
4. **Weighted**: Match production distribution

### Example: E-commerce Application

#### Scenario 1: Browser (60% of users)

```
1. Visit homepage (2s think time)
2. Search for product (3s)
3. View product details (5s)
4. View another product (4s)
5. Exit
```

**Duration**: ~20 seconds per iteration

#### Scenario 2: Buyer (30% of users)

```
1. Visit homepage (2s think time)
2. Login (3s)
3. Search for product (3s)
4. View product details (5s)
5. Add to cart (2s)
6. View cart (3s)
7. Checkout (10s)
8. Complete purchase (5s)
9. Logout (1s)
```

**Duration**: ~40 seconds per iteration

#### Scenario 3: Account Manager (10% of users)

```
1. Visit homepage (2s think time)
2. Login (3s)
3. View profile (3s)
4. Update profile (10s)
5. View order history (4s)
6. Logout (1s)
```

**Duration**: ~25 seconds per iteration

### Implementing in JMeter

Use **Throughput Controllers** to distribute load:

```
Thread Group: 100 users
├── Throughput Controller: 60% (Browser scenario)
├── Throughput Controller: 30% (Buyer scenario)
└── Throughput Controller: 10% (Account manager scenario)
```

---

## Think Time

### What is Think Time?

Time a user spends reading, deciding, or performing non-test activities between requests.

### Why It Matters

Without think time:
- Unrealistic load patterns
- Inflated throughput
- Server overwhelmed
- Skewed metrics

### Calculating Think Time

From production data:
```
think_time = (session_duration - total_request_time) / number_of_requests

Example:
- Session: 300 seconds
- Requests: 20
- Request time: 100 seconds total
- Think time: (300 - 100) / 20 = 10 seconds average
```

### Implementing Think Time

#### Fixed Think Time

```xml
<ConstantTimer>
  <stringProp name="ConstantTimer.delay">3000</stringProp>  <!-- 3 seconds -->
</ConstantTimer>
```

#### Random Think Time

```xml
<UniformRandomTimer>
  <stringProp name="ConstantTimer.delay">1000</stringProp>  <!-- Base: 1s -->
  <stringProp name="RandomTimer.range">2000</stringProp>   <!-- Range: 0-2s -->
  <!-- Total: 1-3 seconds random -->
</UniformRandomTimer>
```

#### Property-based Think Time

```xml
<ConstantTimer>
  <stringProp name="ConstantTimer.delay">
    ${__Random(${__P(think.time.min,1000)},${__P(think.time.max,3000)})}
  </stringProp>
</ConstantTimer>
```

### Think Time Recommendations

| Scenario Type | Min | Max | Typical |
|---------------|-----|-----|---------|
| API Testing | 0.5s | 2s | 1s |
| Web Browsing | 2s | 10s | 5s |
| Data Entry | 5s | 30s | 15s |
| Reading Content | 10s | 60s | 20s |

---

## Pacing and Throughput

### Pacing

**Definition**: Time between iterations of a user scenario

**Purpose**: Control request rate independently of think time

### Throughput Control

#### Constant Throughput Timer

Maintains specific throughput (TPS):

```xml
<ConstantThroughputTimer>
  <intProp name="calcMode">0</intProp>  <!-- This thread only -->
  <stringProp name="throughput">${__P(throughput,60)}</stringProp>  <!-- 60 per minute = 1 TPS -->
</ConstantThroughputTimer>
```

**Calculate Mode Options**:
- `0`: This thread only
- `1`: All active threads
- `2`: All active threads (shared)
- `3`: All active threads in current thread group
- `4`: All active threads (shared, considering each thread group)

### Calculating Target Throughput

```
Example Requirements:
- 100,000 requests per hour during peak
- Test duration: 1 hour

Target TPS = 100,000 / 3600 = ~28 TPS

JMeter Configuration:
throughput=1680  # 28 TPS × 60 seconds = 1680 per minute
```

### Throughput vs. Think Time

**Scenario 1: Fixed Think Time**
```
threads=10
think.time=5000ms (5s)
requests_per_iteration=5

Throughput = (10 threads × 5 requests) / (5s × 5 requests) = 2 TPS
```

**Scenario 2: Throughput Controller**
```
threads=10
throughput=60 (per minute) = 1 TPS
requests_per_iteration=5

Pacing = (10 threads × 5 requests) / 1 TPS = 50 seconds per iteration
```

---

## Load Modeling Best Practices

### Do's ✅

1. **Base models on production data** whenever possible
2. **Use realistic think times** to avoid artificial load
3. **Implement varied user scenarios** representing actual behavior
4. **Ramp up gradually** to avoid startup artifacts
5. **Monitor system resources** during tests
6. **Run tests multiple times** for consistency
7. **Document assumptions** in your load model

### Don'ts ❌

1. **Don't skip think time** for "faster" results
2. **Don't use unrealistic load patterns** that don't match production
3. **Don't ignore ramp-up periods** in result analysis
4. **Don't test with only one scenario** when users have varied behaviors
5. **Don't forget to consider data variety**
6. **Don't assume linear scalability** without testing
7. **Don't neglect geographic distribution** if applicable

---

## Quick Reference

### Load Pattern Selection

| Goal | Pattern | Ramp-up | Duration |
|------|---------|---------|----------|
| Baseline | Constant | Gradual | 30-60 min |
| Capacity | Step | Per step | 60-90 min |
| Stability | Constant | Gradual | 2-8 hours |
| Resilience | Spike | Fast | 15-30 min |
| Reality | Wave | Gradual | 4-24 hours |

### Think Time Ranges

| Activity | Minimum | Maximum | Average |
|----------|---------|---------|---------|
| API calls | 0.5s | 2s | 1s |
| Page view | 2s | 10s | 5s |
| Form fill | 5s | 30s | 15s |
| Reading | 10s | 60s | 20s |
| Idle | 30s | 180s | 60s |

---

**Document Version**: 1.0  
**Last Updated**: 2025-10-18  
**Maintained By**: Performance Engineering Team

