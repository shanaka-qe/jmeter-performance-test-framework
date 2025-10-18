#!/usr/bin/env python3
"""
Quality Gates Validation Script for JMeter Performance Tests
This script analyzes JMeter JTL result files and validates against SLA thresholds
It can be used in CI/CD pipelines to enforce performance standards

Usage:
    python quality-gates.py --jtl-file results.jtl --error-rate-threshold 2.0 --p95-threshold 800
"""

import argparse  # For parsing command line arguments
import sys  # For system exit codes
import csv  # For reading JTL CSV files
from typing import Dict, List  # For type hints


def parse_arguments():
    """
    Parse command line arguments for quality gate thresholds
    Returns: Parsed arguments object with all threshold values
    """
    parser = argparse.ArgumentParser(
        description='Validate JMeter test results against quality gates'
    )
    
    # Required argument: path to JTL file
    parser.add_argument(
        '--jtl-file',
        required=True,
        help='Path to JMeter JTL result file'
    )
    
    # Optional argument: error rate threshold percentage
    parser.add_argument(
        '--error-rate-threshold',
        type=float,
        default=2.0,
        help='Maximum acceptable error rate percentage (default: 2.0%%)'
    )
    
    # Optional argument: p95 response time threshold in milliseconds
    parser.add_argument(
        '--p95-threshold',
        type=int,
        default=800,
        help='Maximum acceptable p95 response time in ms (default: 800ms)'
    )
    
    # Optional argument: p99 response time threshold in milliseconds
    parser.add_argument(
        '--p99-threshold',
        type=int,
        default=1200,
        help='Maximum acceptable p99 response time in ms (default: 1200ms)'
    )
    
    # Optional argument: minimum throughput in transactions per second
    parser.add_argument(
        '--throughput-threshold',
        type=float,
        default=10.0,
        help='Minimum acceptable throughput in TPS (default: 10.0)'
    )
    
    # Optional argument: average response time threshold in milliseconds
    parser.add_argument(
        '--avg-response-threshold',
        type=int,
        default=500,
        help='Maximum acceptable average response time in ms (default: 500ms)'
    )
    
    return parser.parse_args()


def read_jtl_file(jtl_file_path: str) -> List[Dict]:
    """
    Read and parse JMeter JTL result file
    JTL files are CSV format with headers in the first row
    
    Args:
        jtl_file_path: Path to the JTL file
        
    Returns:
        List of dictionaries containing test result data
    """
    results = []
    
    try:
        # Open the JTL file for reading
        with open(jtl_file_path, 'r', encoding='utf-8') as file:
            # Use CSV DictReader to parse the file with headers
            csv_reader = csv.DictReader(file)
            
            # Read each row into a list of dictionaries
            for row in csv_reader:
                results.append(row)
        
        print(f"✓ Successfully read {len(results)} samples from {jtl_file_path}")
        return results
        
    except FileNotFoundError:
        # Handle case where JTL file doesn't exist
        print(f"✗ Error: JTL file not found: {jtl_file_path}")
        sys.exit(1)
        
    except Exception as e:
        # Handle any other errors during file reading
        print(f"✗ Error reading JTL file: {str(e)}")
        sys.exit(1)


def calculate_metrics(results: List[Dict]) -> Dict:
    """
    Calculate performance metrics from JTL results
    
    Args:
        results: List of test result dictionaries
        
    Returns:
        Dictionary containing calculated metrics
    """
    # Initialize counters and lists
    total_samples = len(results)
    error_count = 0
    response_times = []
    
    # If no samples, return empty metrics
    if total_samples == 0:
        return {
            'total_samples': 0,
            'error_count': 0,
            'error_rate': 0.0,
            'avg_response_time': 0,
            'p95_response_time': 0,
            'p99_response_time': 0,
            'throughput': 0.0
        }
    
    # Extract start and end times for throughput calculation
    start_time = None
    end_time = None
    
    # Iterate through each result sample
    for result in results:
        # Count errors (success field is "false" for failures)
        if result.get('success', 'true').lower() == 'false':
            error_count += 1
        
        # Collect response times (elapsed time in milliseconds)
        elapsed_time = int(result.get('elapsed', 0))
        response_times.append(elapsed_time)
        
        # Track start and end times for throughput calculation
        timestamp = int(result.get('timeStamp', 0))
        if start_time is None or timestamp < start_time:
            start_time = timestamp
        if end_time is None or timestamp > end_time:
            end_time = timestamp
    
    # Sort response times for percentile calculations
    response_times.sort()
    
    # Calculate error rate percentage
    error_rate = (error_count / total_samples) * 100
    
    # Calculate average response time
    avg_response_time = sum(response_times) / len(response_times)
    
    # Calculate p95 (95th percentile) response time
    p95_index = int(len(response_times) * 0.95)
    p95_response_time = response_times[p95_index] if p95_index < len(response_times) else response_times[-1]
    
    # Calculate p99 (99th percentile) response time
    p99_index = int(len(response_times) * 0.99)
    p99_response_time = response_times[p99_index] if p99_index < len(response_times) else response_times[-1]
    
    # Calculate throughput (transactions per second)
    duration_seconds = (end_time - start_time) / 1000.0 if end_time and start_time else 1
    throughput = total_samples / duration_seconds if duration_seconds > 0 else 0
    
    # Return calculated metrics as a dictionary
    return {
        'total_samples': total_samples,
        'error_count': error_count,
        'error_rate': round(error_rate, 2),
        'avg_response_time': round(avg_response_time, 2),
        'p95_response_time': p95_response_time,
        'p99_response_time': p99_response_time,
        'throughput': round(throughput, 2)
    }


def validate_quality_gates(metrics: Dict, thresholds: argparse.Namespace) -> bool:
    """
    Validate calculated metrics against quality gate thresholds
    
    Args:
        metrics: Dictionary of calculated performance metrics
        thresholds: Namespace object containing threshold values
        
    Returns:
        True if all quality gates pass, False otherwise
    """
    print("\n" + "="*60)
    print("QUALITY GATES VALIDATION")
    print("="*60)
    
    # Track whether all gates pass
    all_gates_passed = True
    
    # Print metrics summary
    print("\n📊 Performance Metrics:")
    print(f"  Total Samples:       {metrics['total_samples']}")
    print(f"  Error Count:         {metrics['error_count']}")
    print(f"  Error Rate:          {metrics['error_rate']}%")
    print(f"  Avg Response Time:   {metrics['avg_response_time']} ms")
    print(f"  P95 Response Time:   {metrics['p95_response_time']} ms")
    print(f"  P99 Response Time:   {metrics['p99_response_time']} ms")
    print(f"  Throughput:          {metrics['throughput']} TPS")
    
    print("\n🚦 Quality Gate Results:")
    
    # Gate 1: Error Rate
    if metrics['error_rate'] <= thresholds.error_rate_threshold:
        print(f"  ✓ Error Rate:        {metrics['error_rate']}% <= {thresholds.error_rate_threshold}% [PASS]")
    else:
        print(f"  ✗ Error Rate:        {metrics['error_rate']}% > {thresholds.error_rate_threshold}% [FAIL]")
        all_gates_passed = False
    
    # Gate 2: Average Response Time
    if metrics['avg_response_time'] <= thresholds.avg_response_threshold:
        print(f"  ✓ Avg Response Time: {metrics['avg_response_time']} ms <= {thresholds.avg_response_threshold} ms [PASS]")
    else:
        print(f"  ✗ Avg Response Time: {metrics['avg_response_time']} ms > {thresholds.avg_response_threshold} ms [FAIL]")
        all_gates_passed = False
    
    # Gate 3: P95 Response Time
    if metrics['p95_response_time'] <= thresholds.p95_threshold:
        print(f"  ✓ P95 Response Time: {metrics['p95_response_time']} ms <= {thresholds.p95_threshold} ms [PASS]")
    else:
        print(f"  ✗ P95 Response Time: {metrics['p95_response_time']} ms > {thresholds.p95_threshold} ms [FAIL]")
        all_gates_passed = False
    
    # Gate 4: P99 Response Time
    if metrics['p99_response_time'] <= thresholds.p99_threshold:
        print(f"  ✓ P99 Response Time: {metrics['p99_response_time']} ms <= {thresholds.p99_threshold} ms [PASS]")
    else:
        print(f"  ✗ P99 Response Time: {metrics['p99_response_time']} ms > {thresholds.p99_threshold} ms [FAIL]")
        all_gates_passed = False
    
    # Gate 5: Throughput
    if metrics['throughput'] >= thresholds.throughput_threshold:
        print(f"  ✓ Throughput:        {metrics['throughput']} TPS >= {thresholds.throughput_threshold} TPS [PASS]")
    else:
        print(f"  ✗ Throughput:        {metrics['throughput']} TPS < {thresholds.throughput_threshold} TPS [FAIL]")
        all_gates_passed = False
    
    print("\n" + "="*60)
    
    return all_gates_passed


def main():
    """
    Main function to orchestrate quality gates validation
    """
    # Parse command line arguments
    args = parse_arguments()
    
    print("\n🔍 Starting Quality Gates Validation...")
    print(f"JTL File: {args.jtl_file}")
    
    # Read JTL result file
    results = read_jtl_file(args.jtl_file)
    
    # Calculate performance metrics from results
    metrics = calculate_metrics(results)
    
    # Validate metrics against quality gate thresholds
    gates_passed = validate_quality_gates(metrics, args)
    
    # Print final result and exit with appropriate code
    if gates_passed:
        print("\n✅ All quality gates PASSED!")
        print("="*60 + "\n")
        sys.exit(0)  # Exit code 0 indicates success
    else:
        print("\n❌ Some quality gates FAILED!")
        print("="*60 + "\n")
        sys.exit(1)  # Exit code 1 indicates failure


# Entry point of the script
if __name__ == '__main__':
    main()

