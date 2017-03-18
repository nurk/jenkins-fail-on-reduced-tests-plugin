package org.jenkinsci.plugins.failonreducedtests;

import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.test.AggregatedTestResultAction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FailOnReducedTestsBuilderTest {
    @Mock
    private Run build;
    @Mock
    private Run previousBuild;
    @Mock
    private TaskListener listener;
    @Mock
    private AggregatedTestResultAction currentTestResults;
    @Mock
    private AggregatedTestResultAction previousTestResults;
    @Mock
    private PrintStream logger;

    @Before
    public void setUp() throws Exception {
        when(listener.getLogger()).thenReturn(logger);
        doNothing().when(logger).println(anyString());
    }

    @Test
    public void constructor_givenCorrectValues() {
        FailOnReducedTestsBuilder builder = new FailOnReducedTestsBuilder("20.5", "10");

        assertThat(builder.getMinimumAmount()).isEqualTo(10);
        assertThat(builder.getPercentage()).isEqualTo(20.5);
        assertThat(builder.isConfigurationError()).isFalse();
    }

    @Test
    public void constructor_givenWrongValues() {
        FailOnReducedTestsBuilder builder = new FailOnReducedTestsBuilder("t", "10");

        assertThat(builder.isConfigurationError()).isTrue();
    }

    @Test
    public void perform_givenConfigurationResult_thenLog() {
        FailOnReducedTestsBuilder builder = new FailOnReducedTestsBuilder("t", "10");

        builder.perform(build, null, null, listener);

        verify(logger).println("Not configured correctly, skipping");
    }

    @Test
    public void perform_givenNoTestResults_thenUnstable() {
        FailOnReducedTestsBuilder builder = new FailOnReducedTestsBuilder("10", "10");
        when(build.getAction(AggregatedTestResultAction.class)).thenReturn(null);

        builder.perform(build, null, null, listener);

        verify(logger).println("Verifying amount of unit tests.");
        verify(logger).println("percentage: 10.0");
        verify(logger).println("minimum amount: 10");
        verify(logger).println("Verifying amount of unit tests.");
        verify(build).setResult(Result.UNSTABLE);
    }

    @Test
    public void perform_givenCurrentTestResultsButNoPreviousBuiltBuildAndLessThenMinimum_thenUnstable() {
        FailOnReducedTestsBuilder builder = new FailOnReducedTestsBuilder("10", "10");
        when(build.getAction(AggregatedTestResultAction.class)).thenReturn(currentTestResults);
        when(build.getPreviousBuiltBuild()).thenReturn(null);
        when(currentTestResults.getTotalCount()).thenReturn(9);
        when(currentTestResults.getSkipCount()).thenReturn(0);


        builder.perform(build, null, null, listener);

        verify(logger).println("Verifying amount of unit tests.");
        verify(logger).println("percentage: 10.0");
        verify(logger).println("minimum amount: 10");
        verify(logger).println("Current amount of tests: 9");
        verify(logger).println("No previous successful build found, comparing with minimum amount");
        verify(logger).println("Not enough unit tests");
        verify(build).setResult(Result.UNSTABLE);
    }

    @Test
    public void perform_givenCurrentTestResultsButNoPreviousBuiltBuildAndNotLessThenMinimum_thenNotUnstable() {
        FailOnReducedTestsBuilder builder = new FailOnReducedTestsBuilder("10", "10");
        when(build.getAction(AggregatedTestResultAction.class)).thenReturn(currentTestResults);
        when(build.getPreviousBuiltBuild()).thenReturn(null);
        when(currentTestResults.getTotalCount()).thenReturn(10);
        when(currentTestResults.getSkipCount()).thenReturn(0);


        builder.perform(build, null, null, listener);

        verify(logger).println("Verifying amount of unit tests.");
        verify(logger).println("percentage: 10.0");
        verify(logger).println("minimum amount: 10");
        verify(logger).println("Current amount of tests: 10");
        verify(logger).println("No previous successful build found, comparing with minimum amount");
        verify(build, never()).setResult(Result.UNSTABLE);
    }

    @Test
    public void perform_givenCurrentTestResultsAndPreviousBuiltBuildWithoutResultsAndLessThenMinimum_thenUnstable() {
        FailOnReducedTestsBuilder builder = new FailOnReducedTestsBuilder("10", "10");
        when(build.getAction(AggregatedTestResultAction.class)).thenReturn(currentTestResults);
        when(build.getPreviousBuiltBuild()).thenReturn(previousBuild);
        when(previousBuild.getAction(AggregatedTestResultAction.class)).thenReturn(null);
        when(currentTestResults.getTotalCount()).thenReturn(9);
        when(currentTestResults.getSkipCount()).thenReturn(0);


        builder.perform(build, null, null, listener);

        verify(logger).println("Verifying amount of unit tests.");
        verify(logger).println("percentage: 10.0");
        verify(logger).println("minimum amount: 10");
        verify(logger).println("Current amount of tests: 9");
        verify(logger).println("No previous successful build found, comparing with minimum amount");
        verify(logger).println("Not enough unit tests");
        verify(build).setResult(Result.UNSTABLE);
    }

    @Test
    public void perform_givenCurrentTestResultsAndPreviousBuiltBuildWithoutResultsAndNotLessThenMinimum_thenNotUnstable() {
        FailOnReducedTestsBuilder builder = new FailOnReducedTestsBuilder("10", "10");
        when(build.getAction(AggregatedTestResultAction.class)).thenReturn(currentTestResults);
        when(build.getPreviousBuiltBuild()).thenReturn(previousBuild);
        when(previousBuild.getAction(AggregatedTestResultAction.class)).thenReturn(null);
        when(currentTestResults.getTotalCount()).thenReturn(10);
        when(currentTestResults.getSkipCount()).thenReturn(0);


        builder.perform(build, null, null, listener);

        verify(logger).println("Verifying amount of unit tests.");
        verify(logger).println("percentage: 10.0");
        verify(logger).println("minimum amount: 10");
        verify(logger).println("Current amount of tests: 10");
        verify(logger).println("No previous successful build found, comparing with minimum amount");
        verify(build, never()).setResult(Result.UNSTABLE);
    }

    @Test
    public void perform_givenCurrentTestResultsAndPreviousBuiltBuildAndReduced_thenUnstable() {
        FailOnReducedTestsBuilder builder = new FailOnReducedTestsBuilder("10", "10");
        when(build.getAction(AggregatedTestResultAction.class)).thenReturn(currentTestResults);
        when(build.getPreviousBuiltBuild()).thenReturn(previousBuild);
        when(previousBuild.getAction(AggregatedTestResultAction.class)).thenReturn(previousTestResults);
        when(currentTestResults.getTotalCount()).thenReturn(89);
        when(currentTestResults.getSkipCount()).thenReturn(0);
        when(previousTestResults.getTotalCount()).thenReturn(100);
        when(previousTestResults.getSkipCount()).thenReturn(0);


        builder.perform(build, null, null, listener);

        verify(logger).println("Verifying amount of unit tests.");
        verify(logger).println("percentage: 10.0");
        verify(logger).println("minimum amount: 10");
        verify(logger).println("Current amount of tests: 89");
        verify(logger).println("Previous amount of tests: 100");
        verify(logger).println("Amount of tests reduced too much");
        verify(build).setResult(Result.UNSTABLE);
    }

    @Test
    public void perform_givenCurrentTestResultsAndPreviousBuiltBuildAndNotReduced_thenNotUnstable() {
        FailOnReducedTestsBuilder builder = new FailOnReducedTestsBuilder("10", "10");
        when(build.getAction(AggregatedTestResultAction.class)).thenReturn(currentTestResults);
        when(build.getPreviousBuiltBuild()).thenReturn(previousBuild);
        when(previousBuild.getAction(AggregatedTestResultAction.class)).thenReturn(previousTestResults);
        when(currentTestResults.getTotalCount()).thenReturn(90);
        when(currentTestResults.getSkipCount()).thenReturn(0);
        when(previousTestResults.getTotalCount()).thenReturn(100);
        when(previousTestResults.getSkipCount()).thenReturn(0);


        builder.perform(build, null, null, listener);

        verify(logger).println("Verifying amount of unit tests.");
        verify(logger).println("percentage: 10.0");
        verify(logger).println("minimum amount: 10");
        verify(logger).println("Current amount of tests: 90");
        verify(logger).println("Previous amount of tests: 100");
        verify(build, never()).setResult(Result.UNSTABLE);
    }

    @Test
    public void getRequiredMonitorService() {
        FailOnReducedTestsBuilder builder = new FailOnReducedTestsBuilder("10", "10");
        assertThat(builder.getRequiredMonitorService()).isEqualTo(BuildStepMonitor.NONE);
    }
}