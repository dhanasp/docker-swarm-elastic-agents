package cd.go.contrib.elasticagents.dockerswarm.elasticagent.executors;

import cd.go.contrib.elasticagents.dockerswarm.elasticagent.*;
import cd.go.contrib.elasticagents.dockerswarm.elasticagent.requests.MigrateConfigurationRequest;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static cd.go.contrib.elasticagents.dockerswarm.elasticagent.DockerPlugin.LOG;

public class MigrateConfigurationRequestExecutor implements RequestExecutor {
    private MigrateConfigurationRequest migrateConfigurationRequest;

    public MigrateConfigurationRequestExecutor(MigrateConfigurationRequest migrateConfigurationRequest) {
        this.migrateConfigurationRequest = migrateConfigurationRequest;
    }

    @Override
    public GoPluginApiResponse execute() throws Exception {
        LOG.info("[Migrate Config] Request for Config Migration Started...");

        PluginSettings pluginSettings = migrateConfigurationRequest.getPluginSettings();
        List<ClusterProfile> existingClusterProfiles = migrateConfigurationRequest.getClusterProfiles();
        List<ElasticAgentProfile> existingElasticAgentProfiles = migrateConfigurationRequest.getElasticAgentProfiles();

        if (!arePluginSettingsConfigured(pluginSettings)) {
            LOG.info("[Migrate Config] No Plugin Settings are configured. Skipping Config Migration...");
            return new DefaultGoPluginApiResponse(200, migrateConfigurationRequest.toJSON());
        }

        if (existingClusterProfiles.size() == 0) {
            LOG.info("[Migrate Config] Did not find any Cluster Profile. Possibly, user just have configured plugin settings and haven't define any elastic agent profiles.");
            String newClusterId = UUID.randomUUID().toString();
            LOG.info("[Migrate Config] Migrating existing plugin settings to new cluster profile '{}'", newClusterId);
            LOG.info("[Migrate Config] Migrating existing plugin settings to new cluster profile with plugin'{}'",Constants.PLUGIN_ID );
            ClusterProfile clusterProfile = new ClusterProfile(newClusterId, Constants.PLUGIN_ID, pluginSettings);

            return getGoPluginApiResponse(pluginSettings, Arrays.asList(clusterProfile), existingElasticAgentProfiles);
        }

        LOG.info("[Migrate Config] Checking to perform migrations on Cluster Profiles '{}'.", existingClusterProfiles.stream().map(ClusterProfile::getId).collect(Collectors.toList()));

        for (ClusterProfile clusterProfile : existingClusterProfiles) {
            List<ElasticAgentProfile> associatedElasticAgentProfiles = findAssociatedElasticAgentProfiles(clusterProfile, existingElasticAgentProfiles);
            if (associatedElasticAgentProfiles.size() == 0) {
                LOG.info("[Migrate Config] Skipping migration for the cluster '{}' as no Elastic Agent Profiles are associated with it.", clusterProfile.getId());
                continue;
            }

            if (!arePluginSettingsConfigured(clusterProfile.getClusterProfileProperties())) {
                List<String> associatedProfileIds = associatedElasticAgentProfiles.stream().map(ElasticAgentProfile::getId).collect(Collectors.toList());
                LOG.info("[Migrate Config] Found an empty cluster profile '{}' associated with '{}' elastic agent profiles.", clusterProfile.getId(), associatedProfileIds);
                migrateConfigForCluster(pluginSettings, associatedElasticAgentProfiles, clusterProfile);
            } else {
                LOG.info("[Migrate Config] Skipping migration for the cluster '{}' as cluster has already been configured.", clusterProfile.getId());
            }
        }

        return new DefaultGoPluginApiResponse(200, migrateConfigurationRequest.toJSON());
    }

    //this is responsible to copy over plugin settings configurations to cluster profile and if required rename no op cluster
    private void migrateConfigForCluster(PluginSettings pluginSettings, List<ElasticAgentProfile> associatedElasticAgentProfiles, ClusterProfile clusterProfile) {
        LOG.info("[Migrate Config] Coping over existing plugin settings configurations to '{}' cluster profile.", clusterProfile.getId());
        clusterProfile.setClusterProfileProperties(pluginSettings);

        if (clusterProfile.getId().equals(String.format("no-op-cluster-for-%s", Constants.PLUGIN_ID))) {
            String newClusterId = UUID.randomUUID().toString();
            LOG.info("[Migrate Config] Renaming dummy cluster profile from '{}' to '{}'.", clusterProfile.getId(), newClusterId);
            clusterProfile.setId(newClusterId);

            LOG.info("[Migrate Config] Changing all elastic agent profiles to point to '{}' cluster profile.", clusterProfile.getId());
            associatedElasticAgentProfiles.forEach(elasticAgentProfile -> elasticAgentProfile.setClusterProfileId(newClusterId));
        }
    }

    private List<ElasticAgentProfile> findAssociatedElasticAgentProfiles(ClusterProfile clusterProfile, List<ElasticAgentProfile> elasticAgentProfiles) {
        return elasticAgentProfiles.stream().filter(profile -> Objects.equals(profile.getClusterProfileId(), clusterProfile.getId())).collect(Collectors.toList());
    }

    private GoPluginApiResponse getGoPluginApiResponse(PluginSettings pluginSettings, List<ClusterProfile> clusterProfiles, List<ElasticAgentProfile> elasticAgentProfiles) {
        MigrateConfigurationRequest response = new MigrateConfigurationRequest();

        response.setPluginSettings(pluginSettings);
        response.setClusterProfiles(clusterProfiles);
        response.setElasticAgentProfiles(elasticAgentProfiles);

        return new DefaultGoPluginApiResponse(200, response.toJSON());
    }

    private boolean arePluginSettingsConfigured(PluginSettings pluginSettings) {
        return !StringUtils.isBlank(pluginSettings.getGoServerUrl());
    }


}
