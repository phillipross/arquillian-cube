package org.arquillian.cube.impl.client;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.arquillian.cube.impl.client.CubeConfiguration;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class CubeConfigurationTest {

    private static final String CONTENT = "tomcat:\n" +
            "  image: tutum/tomcat:7.0\n" +
            "  exposedPorts: [8089/tcp]\n" +
            "  await:\n" +
            "    strategy: static\n" +
            "    ip: localhost\n" +
            "    ports: [8080, 8089]";

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void should_parse_and_load_configuration_file() {

        Map<String, String> parameters = new HashMap<String, String>();

        parameters.put("serverVersion", "1.13");
        parameters.put("serverUri", "http://localhost:25123");
        parameters.put("dockerContainers", CONTENT);

        CubeConfiguration cubeConfiguration = CubeConfiguration.fromMap(parameters);
        assertThat(cubeConfiguration.getDockerServerUri(), is("http://localhost:25123"));
        assertThat(cubeConfiguration.getDockerServerVersion(), is("1.13"));

        Map<String, Object> dockerContainersContent = cubeConfiguration.getDockerContainersContent();
        @SuppressWarnings("unchecked")
        Map<String, Object> actualTomcat = (Map<String, Object>) dockerContainersContent.get("tomcat");
        assertThat(actualTomcat, is(notNullValue()));

        String image = (String) actualTomcat.get("image");
        assertThat(image, is("tutum/tomcat:7.0"));
    }

    @Test
    public void should_parse_and_load_configuration_file_from_container_configuration_file() throws IOException {

        File newFile = testFolder.newFile("config.yaml");
        Files.write(Paths.get(newFile.toURI()), CONTENT.getBytes());

        Map<String, String> parameters = new HashMap<String, String>();

        parameters.put("serverVersion", "1.13");
        parameters.put("serverUri", "http://localhost:25123");
        parameters.put("dockerContainersFile", newFile.getAbsolutePath());

        CubeConfiguration cubeConfiguration = CubeConfiguration.fromMap(parameters);
        assertThat(cubeConfiguration.getDockerServerUri(), is("http://localhost:25123"));
        assertThat(cubeConfiguration.getDockerServerVersion(), is("1.13"));

        Map<String, Object> dockerContainersContent = cubeConfiguration.getDockerContainersContent();
        @SuppressWarnings("unchecked")
        Map<String, Object> actualTomcat = (Map<String, Object>) dockerContainersContent.get("tomcat");
        assertThat(actualTomcat, is(notNullValue()));

        String image = (String) actualTomcat.get("image");
        assertThat(image, is("tutum/tomcat:7.0"));
    }

    @Test
    public void should_parse_empty_autostart() throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("autoStartContainers", "");

        CubeConfiguration cubeConfiguration = CubeConfiguration.fromMap(parameters);
        Assert.assertNotNull(cubeConfiguration.getAutoStartContainers());
        Assert.assertEquals(0, cubeConfiguration.getAutoStartContainers().length);
    }

    @Test
    public void should_parse_empty_values_autostart() throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("autoStartContainers", "  ,   ");

        CubeConfiguration cubeConfiguration = CubeConfiguration.fromMap(parameters);
        Assert.assertNotNull(cubeConfiguration.getAutoStartContainers());
        Assert.assertEquals(0, cubeConfiguration.getAutoStartContainers().length);
    }

    @Test
    public void should_parse_trim_autostart() throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("autoStartContainers", "a , b ");

        CubeConfiguration cubeConfiguration = CubeConfiguration.fromMap(parameters);
        Assert.assertNotNull(cubeConfiguration.getAutoStartContainers());
        Assert.assertEquals(2, cubeConfiguration.getAutoStartContainers().length);
        Assert.assertEquals("a", cubeConfiguration.getAutoStartContainers()[0]);
        Assert.assertEquals("b", cubeConfiguration.getAutoStartContainers()[1]);
    }
}
