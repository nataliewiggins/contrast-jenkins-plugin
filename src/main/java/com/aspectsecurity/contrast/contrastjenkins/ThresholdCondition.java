package com.aspectsecurity.contrast.contrastjenkins;


import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * ThresholdCondition class contains the variables and logic to populate the conditions when verifying for vulnerabilities.
 */
public class ThresholdCondition extends AbstractDescribableImpl<ThresholdCondition> {

    private String thresholdCount;

    private String thresholdSeverity;

    private String thresholdVulnType;

    @DataBoundConstructor
    public ThresholdCondition(String thresholdCount, String thresholdSeverity, String thresholdVulnType) {
        this.thresholdCount = thresholdCount;
        this.thresholdSeverity = thresholdSeverity;
        this.thresholdVulnType = thresholdVulnType;
    }

    public String getThresholdCount() {
        return thresholdCount;
    }

    public String getThresholdSeverity() {
        return thresholdSeverity;
    }

    public String getThresholdVulnType() {
        return thresholdVulnType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("count is " + thresholdCount);

        if (thresholdSeverity != null) {
            sb.append(", severity is " + thresholdSeverity);
        }

        if (thresholdVulnType != null) {
            sb.append(", rule type is " + thresholdVulnType);
        }

        sb.append(".");

        return sb.toString();
    }

    /**
     * Descriptor for {@link ThresholdCondition}.
     */
    @Extension
    public static class DescriptorImpl extends Descriptor<ThresholdCondition> {

        /**
         * Validation of the 'thresholdCount' form Field.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         */
        public FormValidation doCheckThresholdCount(@QueryParameter String value) {

            if (value.length() > 0) {
                try {
                    int temp = Integer.parseInt(value);

                    if (temp < 0) {
                        return FormValidation.error("Please enter a positive integer.");
                    }

                } catch (NumberFormatException e) {
                    return FormValidation.error("Please enter a valid integer.");
                }
            }

            return FormValidation.ok();
        }

        /**
         * Validation of the 'thresholdSeverity' form Field.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         */
        public FormValidation doCheckThresholdSeverity(@QueryParameter String value) {
            return FormValidation.ok();
        }

        /**
         * Validation of the 'thresholdCategory' form Field.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         */
        public FormValidation doCheckThresholdVulnType(@QueryParameter String value) {
            return FormValidation.ok();
        }


        public List<String> getSeverities() {
            return SEVERITIES;
        }

        /**
         * Fills the Threshold Category select drop down with vulnerability types for the configured application.
         * These are read in from the static rules.properties file then sorted based on name.
         *
         * @return ListBoxModel filled with vulnerability types.
         */
        public ListBoxModel doFillThresholdVulnTypeItems() throws IOException {
            ListBoxModel items = new ListBoxModel();
            Properties rules = new Properties() {
                @Override
                public synchronized Enumeration<Object> keys() {
                    return Collections.enumeration(new TreeSet<>(super.keySet()));
                }
            };

            InputStream rulesInputStream = getClass().getResourceAsStream("rules.properties");

            try {
                rules.load(rulesInputStream);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                rulesInputStream.close();
            }

            items.add(EMPTY_SELECT, null);

            for (Object rule : rules.keySet()) {
                items.add((String) rule, (String) rule);
            }

            return items;
        }

        /**
         * Fills the Threshold Severity select drop down with severities for the configured application.
         *
         * @return ListBoxModel filled with severities.
         */
        public ListBoxModel doFillThresholdSeverityItems() {
            ListBoxModel items = new ListBoxModel();
            items.add(EMPTY_SELECT, null);

            for (String severity : SEVERITIES) {
                items.add(severity, severity);
            }

            return items;
        }

        /**
         * Display name in the Build Action dropdown.
         *
         * @return String
         */
        public String getDisplayName() {
            return "Threshold Condition";
        }
    }

    private static final List<String> SEVERITIES = Arrays.asList("Note", "Low", "Medium", "High", "Critical");
    private static final String EMPTY_SELECT = "None";
}
