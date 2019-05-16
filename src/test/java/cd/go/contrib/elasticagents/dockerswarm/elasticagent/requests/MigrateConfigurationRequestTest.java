package cd.go.contrib.elasticagents.dockerswarm.elasticagent.requests;

import cd.go.contrib.elasticagents.dockerswarm.elasticagent.ClusterProfile;
import cd.go.contrib.elasticagents.dockerswarm.elasticagent.ElasticAgentProfile;
import cd.go.contrib.elasticagents.dockerswarm.elasticagent.PluginSettings;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Arrays;
import java.util.HashMap;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class MigrateConfigurationRequestTest {
    @Test
    public void shouldCreateMigrationConfigRequestFromRequestBody() {
        String requestBody = "{" +
                "    \"plugin_settings\":{" +
                "        \"go_server_url\":\"https://127.0.0.1:8154/go\", " +
                "        \"auto_register_timeout\":\"20\"" +
                "    }," +
                "    \"cluster_profiles\":[" +
                "        {" +
                "            \"id\":\"cluster_profile_id\"," +
                "            \"plugin_id\":\"plugin_id\"," +
                "            \"properties\":{" +
                "                \"go_server_url\":\"https://127.0.0.1:8154/go\", " +
                "                \"auto_register_timeout\":\"20\"" +
                "            }" +
                "         }" +
                "    ]," +
                "    \"elastic_agent_profiles\":[" +
                "        {" +
                "            \"id\":\"profile_id\"," +
                "            \"plugin_id\":\"plugin_id\"," +
                "            \"cluster_profile_id\":\"cluster_profile_id\"," +
                "            \"properties\":{" +
                "                \"some_key\":\"some_value\"," +
                "                \"some_key2\":\"some_value2\"" +
                "            }" +
                "        }" +
                "    ]" +
                "}\n";

        MigrateConfigurationRequest request = MigrateConfigurationRequest.fromJSON(requestBody);

        PluginSettings pluginSettings = new PluginSettings();
        pluginSettings.setGoServerUrl("https://127.0.0.1:8154/go");
        pluginSettings.setAutoRegisterTimeout("20");

        ClusterProfile clusterProfile = new ClusterProfile();
        clusterProfile.setId("cluster_profile_id");
        clusterProfile.setPluginId("plugin_id");
        clusterProfile.setClusterProfileProperties(pluginSettings);

        ElasticAgentProfile elasticAgentProfile = new ElasticAgentProfile();
        elasticAgentProfile.setId("profile_id");
        elasticAgentProfile.setPluginId("plugin_id");
        elasticAgentProfile.setClusterProfileId("cluster_profile_id");
        HashMap<String, String> properties = new HashMap<>();
        properties.put("some_key", "some_value");
        properties.put("some_key2", "some_value2");
        elasticAgentProfile.setProperties(properties);

        assertThat(pluginSettings, is(request.getPluginSettings()));
        assertThat(Arrays.asList(clusterProfile), is(request.getClusterProfiles()));
        assertThat(Arrays.asList(elasticAgentProfile), is(request.getElasticAgentProfiles()));
    }

    @Test
    public void shouldCreateMigrationConfigRequestWhenNoConfigurationsAreSpecified() {
        String requestBody = "{" +
                "    \"plugin_settings\":{}," +
                "    \"cluster_profiles\":[]," +
                "    \"elastic_agent_profiles\":[]" +
                "}\n";

        MigrateConfigurationRequest request = MigrateConfigurationRequest.fromJSON(requestBody);

        assertThat(new PluginSettings(), is(request.getPluginSettings()));
        assertThat(Arrays.asList(), is(request.getClusterProfiles()));
        assertThat(Arrays.asList(), is(request.getElasticAgentProfiles()));
    }

    @Test
    public void shouldSerializeToJSONFromMigrationConfigRequest() throws JSONException {
        PluginSettings pluginSettings = new PluginSettings();
        pluginSettings.setGoServerUrl("https://127.0.0.1:8154/go");
        pluginSettings.setAutoRegisterTimeout("20");

        ClusterProfile clusterProfile = new ClusterProfile();
        clusterProfile.setId("cluster_profile_id");
        clusterProfile.setPluginId("plugin_id");
        clusterProfile.setClusterProfileProperties(pluginSettings);

        ElasticAgentProfile elasticAgentProfile = new ElasticAgentProfile();
        elasticAgentProfile.setId("profile_id");
        elasticAgentProfile.setPluginId("plugin_id");
        elasticAgentProfile.setClusterProfileId("cluster_profile_id");
        HashMap<String, String> properties = new HashMap<>();
        properties.put("some_key", "some_value");
        properties.put("some_key2", "some_value2");
        elasticAgentProfile.setProperties(properties);

        MigrateConfigurationRequest request = new MigrateConfigurationRequest(pluginSettings, Arrays.asList(clusterProfile), Arrays.asList(elasticAgentProfile));

        String actual = request.toJSON();

        String expected = "{" +
                "    \"plugin_settings\":{" +
                "        \"go_server_url\":\"https://127.0.0.1:8154/go\", " +
                "        \"auto_register_timeout\":\"20\"" +
                "    }," +
                "    \"cluster_profiles\":[" +
                "        {" +
                "            \"id\":\"cluster_profile_id\"," +
                "            \"plugin_id\":\"plugin_id\"," +
                "            \"properties\":{" +
                "                \"go_server_url\":\"https://127.0.0.1:8154/go\", " +
                "                \"auto_register_timeout\":\"20\"" +
                "            }" +
                "         }" +
                "    ]," +
                "    \"elastic_agent_profiles\":[" +
                "        {" +
                "            \"id\":\"profile_id\"," +
                "            \"plugin_id\":\"plugin_id\"," +
                "            \"cluster_profile_id\":\"cluster_profile_id\"," +
                "            \"properties\":{" +
                "                \"some_key\":\"some_value\"," +
                "                \"some_key2\":\"some_value2\"" +
                "            }" +
                "        }" +
                "    ]" +
                "}\n";

        JSONAssert.assertEquals(expected, actual, false);
    }

}