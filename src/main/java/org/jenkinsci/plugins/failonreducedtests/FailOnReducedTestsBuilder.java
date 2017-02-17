package org.jenkinsci.plugins.failonreducedtests;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.tasks.test.AggregatedTestResultAction;
import hudson.util.FormValidation;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.IOException;

public class FailOnReducedTestsBuilder extends Recorder implements SimpleBuildStep {

    private Double percentage = 100d;
    private Integer minimumAmount = 0;

    @DataBoundConstructor
    public FailOnReducedTestsBuilder(String percentage, String minimumAmount) {
        try {
            this.percentage = Double.valueOf(percentage);
            this.minimumAmount = Integer.valueOf(minimumAmount);
        } catch (Exception e) {
            this.percentage = 100d;
            this.minimumAmount = 0;
        }
    }

    public Double getPercentage() {
        return percentage;
    }

    public Integer getMinimumAmount() {
        return minimumAmount;
    }

    @Override
    public void perform(Run<?, ?> build, FilePath workspace, Launcher launcher, TaskListener listener) {
        listener.getLogger().println("Verifying amount of unit tests.");
        listener.getLogger().println("percentage: " + getPercentage());
        listener.getLogger().println("minimum amount: " + getMinimumAmount());

        AggregatedTestResultAction currentTestResults = build.getAction(AggregatedTestResultAction.class);
        if (currentTestResults == null) {
            listener.getLogger().println("No Test Results in current build");
            build.setResult(Result.UNSTABLE);
            return;
        }
        int currentTestTotal = currentTestResults.getTotalCount() - currentTestResults.getSkipCount();
        listener.getLogger().println("Current amount of tests: " + currentTestResults);

        Run<?, ?> previousBuiltBuild = build.getPreviousBuiltBuild();
        if (previousBuiltBuild != null) {
            AggregatedTestResultAction previousTestResults = previousBuiltBuild.getAction(AggregatedTestResultAction.class);
            if (previousTestResults != null) {
                int previousTestTotal = previousTestResults.getTotalCount() - previousTestResults.getSkipCount();
                listener.getLogger().println("Previous amount of tests: " + previousTestTotal);
                listener.getLogger().println("Comparing with percentage");
                if (currentTestTotal < (previousTestTotal - (previousTestTotal * percentage) / 100)) {
                    listener.getLogger().println("Amount of tests reduced too much");
                    build.setResult(Result.UNSTABLE);
                    return;
                }
            }
        }

        listener.getLogger().println("No previous successful build found, comparing with minimum amount");
        if (currentTestResults.getTotalCount() - currentTestResults.getSkipCount() < minimumAmount) {
            listener.getLogger().print("Not enough unit tests");
            build.setResult(Result.UNSTABLE);
            return;
        }
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        public DescriptorImpl() {
            load();
        }

        public DescriptorImpl(boolean load){
            if(load){
                load();
            }
        }

        public FormValidation doCheckPercentage(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please set a percentage");
            try {
                Double.valueOf(value);
            } catch (Exception e) {
                return FormValidation.error("Please enter a valid number");
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckMinimumAmount(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please set a minimum amount");
            try {
                Integer.valueOf(value);
            } catch (Exception e) {
                return FormValidation.error("Please enter a valid number");
            }
            return FormValidation.ok();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        public String getDisplayName() {
            return "Fail on reduced tests";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req, formData);
        }
    }
}

