package org.jenkinsci.plugins.failonreducedtests;

import hudson.util.FormValidation;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import javax.servlet.ServletException;
import java.io.IOException;

public class DescriptorImplTest {
    private FailOnReducedTestsBuilder.DescriptorImpl descriptorImpl = new FailOnReducedTestsBuilder.DescriptorImpl(false);

    @Test
    public void constructor_givenValidValues_thenValuesSet() {
        FailOnReducedTestsBuilder builder = new FailOnReducedTestsBuilder("10.5", "10");

        Assertions.assertThat(builder.getPercentage()).isEqualTo(10.5);
        Assertions.assertThat(builder.getMinimumAmount()).isEqualTo(10);
    }

    @Test
    public void constructor_givenWrongPercentageValue_thenDefaultValues() {
        FailOnReducedTestsBuilder builder = new FailOnReducedTestsBuilder("10.5b", "10");

        Assertions.assertThat(builder.getPercentage()).isEqualTo(100);
        Assertions.assertThat(builder.getMinimumAmount()).isEqualTo(0);
    }

    @Test
    public void constructor_givenWrongMinimumAmountValue_thenDefaultValues() {
        FailOnReducedTestsBuilder builder = new FailOnReducedTestsBuilder("10.5", "10b");

        Assertions.assertThat(builder.getPercentage()).isEqualTo(100);
        Assertions.assertThat(builder.getMinimumAmount()).isEqualTo(0);
    }

    @Test
    public void doCheckPercentage_givenEmptyString_thenFormValidationError() throws IOException, ServletException {
        FormValidation actual = descriptorImpl.doCheckPercentage("");

        Assertions.assertThat(actual.getMessage()).isEqualTo("Please set a percentage");
    }

    @Test
    public void doCheckPercentage_givenIllegalValueString_thenFormValidationError() throws IOException, ServletException {
        FormValidation actual = descriptorImpl.doCheckPercentage("abc");

        Assertions.assertThat(actual.getMessage()).isEqualTo("Please enter a valid number");
    }

    @Test
    public void doCheckPercentage_givenValidString_thenFormValidation() throws IOException, ServletException {
        FormValidation actual = descriptorImpl.doCheckPercentage("10.5");

        Assertions.assertThat(actual.getMessage()).isNull();
    }

    @Test
    public void doCheckMinimumAmount_givenEmptyString_thenFormValidationError() throws IOException, ServletException {
        FormValidation actual = descriptorImpl.doCheckMinimumAmount("");

        Assertions.assertThat(actual.getMessage()).isEqualTo("Please set a minimum amount");
    }

    @Test
    public void doCheckMinimumAmount_givenIllegalValueString_thenFormValidationError() throws IOException, ServletException {
        FormValidation actual = descriptorImpl.doCheckMinimumAmount("abc");

        Assertions.assertThat(actual.getMessage()).isEqualTo("Please enter a valid number");
    }

    @Test
    public void doCheckMinimumAmount_givenValidString_thenFormValidation() throws IOException, ServletException {
        FormValidation actual = descriptorImpl.doCheckMinimumAmount("10");

        Assertions.assertThat(actual.getMessage()).isNull();
    }
}