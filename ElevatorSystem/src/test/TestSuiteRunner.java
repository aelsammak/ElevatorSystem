package test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Test runner for the application. Runs all test cases created within the application. JUNIT 4 has been used.
 * @author Ben Herriott
 * @version 1.0
 */
public class TestSuiteRunner {
	
	 public static void main(String[] args) {
	        String resultsOfFailedTests = "";

	        //Runs all tests in TestSuite
	        Result result = JUnitCore.runClasses(InitialTest.class, ElevatorTest.class);

	        //Collect all failures to print out for feedback
	        for (Failure failure : result.getFailures()) {
	            resultsOfFailedTests += "\nTest: " + failure.getTestHeader() + "\n" + failure.toString();
	        }

	        System.out.println("\nAll Test Passed: " + result.wasSuccessful()
	                + "\nTests Passed: " + (result.getRunCount() - result.getFailureCount())
	                + "\nTests Failed: " + result.getFailureCount()
	                + "\nFailed Tests Feedback: " + resultsOfFailedTests);

	        System.exit(0);
	    }

}
