package project.tests;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherFactory;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage;

import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.junit.platform.launcher.listeners.TestExecutionSummary.Failure;

import java.util.List;

/**
 * Discovers and executes all tests and prints the results.
 *
 * @author Paul Roode
 * @version Iteration 5
 */
public class TestRunner {

    public static void main(String[] args) {

        final Launcher launcher = LauncherFactory.create();
        final SummaryGeneratingListener summaryGeneratingListener = new SummaryGeneratingListener();
        final LauncherDiscoveryRequest launcherDiscoveryRequest = LauncherDiscoveryRequestBuilder.request()
                .selectors(
                        selectPackage("project.tests")
                )
                .build();

        launcher.registerTestExecutionListeners(summaryGeneratingListener);
        launcher.execute(launcherDiscoveryRequest);

        // print test results
        TestExecutionSummary testExecutionSummary = summaryGeneratingListener.getSummary();
        System.out.println("TestRunner says:");
        System.out.println("Tests found - " + testExecutionSummary.getTestsFoundCount());
        System.out.println("Tests passed - " + testExecutionSummary.getTestsSucceededCount());
        List<Failure> failures = testExecutionSummary.getFailures();
        failures.forEach(failure -> System.out.println("Failure - " + failure.getException()));

    }

    /**
     * Sanity check.
     */
    @Test
    public void testSanity() {
        String message = "I am sane";
        Throwable throwable = assertThrows(IllegalArgumentException.class, () -> {
            throw new IllegalArgumentException(message);
        });
        assertEquals(message, throwable.getMessage());
    }

}
