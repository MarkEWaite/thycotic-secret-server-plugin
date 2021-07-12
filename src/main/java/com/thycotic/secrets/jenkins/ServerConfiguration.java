package com.thycotic.secrets.jenkins;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;

import com.cloudbees.plugins.credentials.common.StandardListBoxModel;

import org.apache.commons.lang.StringUtils;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.Item;
import hudson.security.ACL;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.GlobalConfiguration;

@Extension
@Symbol("secretServer")
public class ServerConfiguration extends GlobalConfiguration {
    public static final String DEFAULT_API_PATH_URI = "/api/v1";
    public static final String DEFAULT_TOKEN_PATH_URI = "/oauth2/token";
    public static final String DEFAULT_ENVIRONMENT_VARIABLE_PREFIX = "TSS_";

    /**
     * Calls hudson.ExtensionList#lookupSingleton(ServerConfiguration.class)
     * to get the singleton instance of this class which is how the Jenkins
     * documentation recommends that it be accessed.
     *
     * @return the singleton instance of this class
     */
    public static ServerConfiguration get() {
        return ExtensionList.lookupSingleton(ServerConfiguration.class);
    }

    /**
     * Exposes the Base URL validation logic to {@link ServerSecret}
     *
     * @param value - the base URL to be validated
     * @return {@link hudson.util.FormValidation#ok()} or
     *         {@link hudson.util.FormValidation#error(String)}
     */
    static FormValidation checkBaseUrl(@QueryParameter final String value) {
        try {
            new URL(value);
            return FormValidation.ok();
        } catch (final MalformedURLException e) {
            return FormValidation.error("Invalid URL");
        }
    }

    private String credentialId, baseUrl, apiPathUri = DEFAULT_API_PATH_URI, tokenPathUri = DEFAULT_TOKEN_PATH_URI,
            environmentVariablePrefix = DEFAULT_ENVIRONMENT_VARIABLE_PREFIX;

    /**
     * Convenience method for {@link ServerBuildWrapper}
     *
     * @return the composition of {@link #getBaseUrl()} and {@link #getApiPathUri()}
     */
    String getAPIUrl() {
        return getBaseUrl() + getApiPathUri();
    }

    /**
     * Convenience method for {@link ServerBuildWrapper}
     *
     * @return the composition of {@link #getBaseUrl()} and
     *         {@link #getTokenPathUri()}
     */
    String getTokenUrl() {
        return getBaseUrl() + getTokenPathUri();
    }

    public ServerConfiguration() {
        load();
    }

    public FormValidation doCheckBaseUrl(@QueryParameter final String value) throws IOException, ServletException {
        return checkBaseUrl(value);
    }

    public ListBoxModel doFillCredentialIdItems(@AncestorInPath final Item item) {
        return new StandardListBoxModel().includeEmptyValue().includeAs(ACL.SYSTEM, item, UserCredentials.class);
    }

    public String getCredentialId() {
        return credentialId;
    }

    @DataBoundSetter
    public void setCredentialId(final String credentialId) {
        this.credentialId = credentialId;
        save();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    @DataBoundSetter
    public void setBaseUrl(final String baseUrl) {
        this.baseUrl = StringUtils.removeEnd(baseUrl, "/");
        save();
    }

    public String getEnvironmentVariablePrefix() {
        return environmentVariablePrefix;
    }

    @DataBoundSetter
    public void setEnvironmentVariablePrefix(final String environmentVariablePrefix) {
        this.environmentVariablePrefix = environmentVariablePrefix;
        save();
    }

    public String getApiPathUri() {
        return apiPathUri;
    }

    @DataBoundSetter
    public void setApiPathUri(final String apiPathUri) {
        this.apiPathUri = "/" + StringUtils.strip(apiPathUri, "/");
        save();
    }

    public String getTokenPathUri() {
        return tokenPathUri;
    }

    @DataBoundSetter
    public void setTokenPathUri(final String tokenPathUri) {
        this.tokenPathUri = StringUtils.strip(tokenPathUri);
        save();
    }
}
