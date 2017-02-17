package org.jenkinsci.plugins.failonreducedtests;

import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.test.AggregatedTestResultAction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.PrintStream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FailOnReducedTestsBuilderTest {
    @Mock
    private Run build;
    @Mock
    private Run previousBuild;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
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
    public void perform_givenNoTestResults_thenUnstable() {
        FailOnReducedTestsBuilder builder = new FailOnReducedTestsBuilder("10", "10");
        when(build.getAction(AggregatedTestResultAction.class)).thenReturn(null);

        builder.perform(build, null, null, listener);

        verify(logger).println("Verifying amount of unit tests.");
        verify(build).setResult(Result.UNSTABLE);
    }
}