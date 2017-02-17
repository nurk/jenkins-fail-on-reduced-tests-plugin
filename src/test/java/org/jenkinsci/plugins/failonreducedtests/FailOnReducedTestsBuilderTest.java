package org.jenkinsci.plugins.failonreducedtests;

import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.test.AggregatedTestResultAction;
import hudson.util.FormValidation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FailOnReducedTestsBuilderTest {

    private FailOnReducedTestsBuilder.DescriptorImpl descriptorImpl = new FailOnReducedTestsBuilder.DescriptorImpl(false);

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
    public void constructor_givenValidValues_thenValuesSet() {
        FailOnReducedTestsBuilder builder = new FailOnReducedTestsBuilder("10.5", "10");

        assertThat(builder.getPercentage()).isEqualTo(10.5);
        assertThat(builder.getMinimumAmount()).isEqualTo(10);
    }

    @Test
    public void constructor_givenWrongPercentageValue_thenDefaultValues() {
        FailOnReducedTestsBuilder builder = new FailOnReducedTestsBuilder("10.5b", "10");

        assertThat(builder.getPercentage()).isEqualTo(100);
        assertThat(builder.getMinimumAmount()).isEqualTo(0);
    }

    @Test
    public void constructor_givenWrongMinimumAmountValue_thenDefaultValues() {
        FailOnReducedTestsBuilder builder = new FailOnReducedTestsBuilder("10.5", "10b");

        assertThat(builder.getPercentage()).isEqualTo(100);
        assertThat(builder.getMinimumAmount()).isEqualTo(0);
    }

    @Test
    public void doCheckPercentage_givenEmptyString_thenFormValidationError() throws IOException, ServletException {
        FormValidation actual = descriptorImpl.doCheckPercentage("");

        assertThat(actual.getMessage()).isEqualTo("Please set a percentage");
    }

    @Test
    public void doCheckPercentage_givenIllegalValueString_thenFormValidationError() throws IOException, ServletException {
        FormValidation actual = descriptorImpl.doCheckPercentage("abc");

        assertThat(actual.getMessage()).isEqualTo("Please enter a valid number");
    }

    @Test
    public void doCheckPercentage_givenValidString_thenFormValidation() throws IOException, ServletException {
        FormValidation actual = descriptorImpl.doCheckPercentage("10.5");

        assertThat(actual.getMessage()).isNull();
    }

    @Test
    public void doCheckMinimumAmount_givenEmptyString_thenFormValidationError() throws IOException, ServletException {
        FormValidation actual = descriptorImpl.doCheckMinimumAmount("");

        assertThat(actual.getMessage()).isEqualTo("Please set a minimum amount");
    }

    @Test
    public void doCheckMinimumAmount_givenIllegalValueString_thenFormValidationError() throws IOException, ServletException {
        FormValidation actual = descriptorImpl.doCheckMinimumAmount("abc");

        assertThat(actual.getMessage()).isEqualTo("Please enter a valid number");
    }

    @Test
    public void doCheckMinimumAmount_givenValidString_thenFormValidation() throws IOException, ServletException {
        FormValidation actual = descriptorImpl.doCheckMinimumAmount("10");

        assertThat(actual.getMessage()).isNull();
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