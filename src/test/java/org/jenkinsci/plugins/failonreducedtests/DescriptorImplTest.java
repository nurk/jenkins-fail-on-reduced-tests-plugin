package org.jenkinsci.plugins.failonreducedtests;

import hudson.util.FormValidation;
import org.junit.Test;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class DescriptorImplTest {
    private FailOnReducedTestsBuilder.DescriptorImpl descriptorImpl = new FailOnReducedTestsBuilder.DescriptorImpl(false);

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
}