/*
 * Copyright 2016 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cd.go.contrib.elasticagents.dockerswarm.elasticagent;

import cd.go.contrib.elasticagents.dockerswarm.elasticagent.utils.Util;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.spotify.docker.client.messages.RegistryAuth;
import org.joda.time.Period;

import java.util.Collection;

public class PluginSettings {
    public static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    @Expose
    @SerializedName("go_server_url")
    private String goServerUrl;

    @Expose
    @SerializedName("environment_variables")
    private String environmentVariables;

    @Expose
    @SerializedName("max_docker_containers")
    private String maxDockerContainers;

    @Expose
    @SerializedName("docker_uri")
    private String dockerURI;

    @Expose
    @SerializedName("auto_register_timeout")
    private String autoRegisterTimeout;

    @Expose
    @SerializedName("docker_ca_cert")
    private String dockerCACert;

    @Expose
    @SerializedName("docker_client_cert")
    private String dockerClientCert;

    @Expose
    @SerializedName("docker_client_key")
    private String dockerClientKey;

    @Expose
    @SerializedName("private_registry_server")
    private String privateRegistryServer;

    @Expose
    @SerializedName("private_registry_username")
    private String privateRegistryUsername;

    @Expose
    @SerializedName("private_registry_password")
    private String privateRegistryPassword;

    @Expose
    @SerializedName("enable_private_registry_authentication")
    private boolean useDockerAuthInfo;

    private Period autoRegisterPeriod;

    public static PluginSettings fromJSON(String json) {
        return GSON.fromJson(json, PluginSettings.class);
    }

    public Period getAutoRegisterPeriod() {
        if (this.autoRegisterPeriod == null) {
            this.autoRegisterPeriod = new Period().withMinutes(Integer.parseInt(getAutoRegisterTimeout()));
        }
        return this.autoRegisterPeriod;
    }

    private String getAutoRegisterTimeout() {
        if (autoRegisterTimeout == null) {
            autoRegisterTimeout = "10";
        }
        return autoRegisterTimeout;
    }

    public Collection<String> getEnvironmentVariables() {
        return Util.splitIntoLinesAndTrimSpaces(environmentVariables);
    }

    public Integer getMaxDockerContainers() {
        return Integer.valueOf(maxDockerContainers);
    }

    public String getGoServerUrl() {
        return goServerUrl;
    }

    public String getDockerURI() {
        return dockerURI;
    }

    public String getDockerCACert() {
        return dockerCACert;
    }

    public String getDockerClientCert() {
        return dockerClientCert;
    }

    public String getDockerClientKey() {
        return dockerClientKey;
    }

    public void setDockerCACert(String dockerCACert) {
        this.dockerCACert = dockerCACert;
    }

    public void setDockerClientCert(String dockerClientCert) {
        this.dockerClientCert = dockerClientCert;
    }

    public void setDockerClientKey(String dockerClientKey) {
        this.dockerClientKey = dockerClientKey;
    }

    public void setDockerURI(String dockerURI) {
        this.dockerURI = dockerURI;
    }

    public void setEnvironmentVariables(String environmentVariables) {
        this.environmentVariables = environmentVariables;
    }

    public void setMaxDockerContainers(Integer maxDockerContainers) {
        this.maxDockerContainers = String.valueOf(maxDockerContainers);
    }

    public boolean useDockerAuthInfo() {
        return Boolean.valueOf(useDockerAuthInfo);
    }

    public RegistryAuth registryAuth() {
        return RegistryAuth.builder()
                .serverAddress(privateRegistryServer)
                .username(privateRegistryUsername)
                .password(privateRegistryPassword)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PluginSettings that = (PluginSettings) o;

        if (useDockerAuthInfo != that.useDockerAuthInfo) return false;
        if (goServerUrl != null ? !goServerUrl.equals(that.goServerUrl) : that.goServerUrl != null) return false;
        if (environmentVariables != null ? !environmentVariables.equals(that.environmentVariables) : that.environmentVariables != null)
            return false;
        if (maxDockerContainers != null ? !maxDockerContainers.equals(that.maxDockerContainers) : that.maxDockerContainers != null)
            return false;
        if (dockerURI != null ? !dockerURI.equals(that.dockerURI) : that.dockerURI != null) return false;
        if (autoRegisterTimeout != null ? !autoRegisterTimeout.equals(that.autoRegisterTimeout) : that.autoRegisterTimeout != null)
            return false;
        if (dockerCACert != null ? !dockerCACert.equals(that.dockerCACert) : that.dockerCACert != null) return false;
        if (dockerClientCert != null ? !dockerClientCert.equals(that.dockerClientCert) : that.dockerClientCert != null)
            return false;
        if (dockerClientKey != null ? !dockerClientKey.equals(that.dockerClientKey) : that.dockerClientKey != null)
            return false;
        if (privateRegistryServer != null ? !privateRegistryServer.equals(that.privateRegistryServer) : that.privateRegistryServer != null)
            return false;
        if (privateRegistryUsername != null ? !privateRegistryUsername.equals(that.privateRegistryUsername) : that.privateRegistryUsername != null)
            return false;
        if (privateRegistryPassword != null ? !privateRegistryPassword.equals(that.privateRegistryPassword) : that.privateRegistryPassword != null)
            return false;
        return autoRegisterPeriod != null ? autoRegisterPeriod.equals(that.autoRegisterPeriod) : that.autoRegisterPeriod == null;
    }

    @Override
    public int hashCode() {
        int result = goServerUrl != null ? goServerUrl.hashCode() : 0;
        result = 31 * result + (environmentVariables != null ? environmentVariables.hashCode() : 0);
        result = 31 * result + (maxDockerContainers != null ? maxDockerContainers.hashCode() : 0);
        result = 31 * result + (dockerURI != null ? dockerURI.hashCode() : 0);
        result = 31 * result + (autoRegisterTimeout != null ? autoRegisterTimeout.hashCode() : 0);
        result = 31 * result + (dockerCACert != null ? dockerCACert.hashCode() : 0);
        result = 31 * result + (dockerClientCert != null ? dockerClientCert.hashCode() : 0);
        result = 31 * result + (dockerClientKey != null ? dockerClientKey.hashCode() : 0);
        result = 31 * result + (privateRegistryServer != null ? privateRegistryServer.hashCode() : 0);
        result = 31 * result + (privateRegistryUsername != null ? privateRegistryUsername.hashCode() : 0);
        result = 31 * result + (privateRegistryPassword != null ? privateRegistryPassword.hashCode() : 0);
        result = 31 * result + (useDockerAuthInfo ? 1 : 0);
        result = 31 * result + (autoRegisterPeriod != null ? autoRegisterPeriod.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PluginSettings{" +
                "goServerUrl='" + goServerUrl + '\'' +
                ", environmentVariables='" + environmentVariables + '\'' +
                ", maxDockerContainers='" + maxDockerContainers + '\'' +
                ", dockerURI='" + dockerURI + '\'' +
                ", autoRegisterTimeout='" + autoRegisterTimeout + '\'' +
                ", dockerCACert='" + dockerCACert + '\'' +
                ", dockerClientCert='" + dockerClientCert + '\'' +
                ", dockerClientKey='" + dockerClientKey + '\'' +
                ", privateRegistryServer='" + privateRegistryServer + '\'' +
                ", privateRegistryUsername='" + privateRegistryUsername + '\'' +
                ", privateRegistryPassword='" + privateRegistryPassword + '\'' +
                ", useDockerAuthInfo=" + useDockerAuthInfo +
                ", autoRegisterPeriod=" + autoRegisterPeriod +
                '}';
    }

    public void setGoServerUrl(String goServerUrl) {
        this.goServerUrl = goServerUrl;
    }

    public void setAutoRegisterTimeout(String autoRegisterTimeout) {
        this.autoRegisterTimeout = autoRegisterTimeout;
    }
}
