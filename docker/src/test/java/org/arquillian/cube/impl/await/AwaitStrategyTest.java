package org.arquillian.cube.impl.await;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.arquillian.cube.impl.await.AwaitStrategy;
import org.arquillian.cube.impl.await.AwaitStrategyFactory;
import org.arquillian.cube.impl.await.NativeAwaitStrategy;
import org.arquillian.cube.impl.await.PollingAwaitStrategy;
import org.arquillian.cube.impl.await.StaticAwaitStrategy;
import org.arquillian.cube.spi.Cube;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.yaml.snakeyaml.Yaml;

@RunWith(MockitoJUnitRunner.class)
public class AwaitStrategyTest {

    private static final String CONTENT_WITH_STATIC_STRATEGY = "tomcat:\n" +
            "  image: tutum/tomcat:7.0\n" +
            "  exposedPorts: [8089/tcp]\n" +
            "  await:\n" +
            "    strategy: static\n" +
            "    ip: localhost\n" +
            "    ports: [8080,8089]";

    private static final String CONTENT_WITH_NO_STRATEGY = "tomcat:\n" +
            "  image: tutum/tomcat:7.0\n" +
            "  exposedPorts: [8089/tcp]\n";

    private static final String CONTENT_WITH_POLLING_STRATEGY = "tomcat:\n" +
            "  image: tutum/tomcat:7.0\n" +
            "  exposedPorts: [8089/tcp]\n" +
            "  await:\n" +
            "    strategy: polling";

    private static final String CONTENT_WITH_NATIVE_STRATEGY = "tomcat:\n" +
            "  image: tutum/tomcat:7.0\n" +
            "  exposedPorts: [8089/tcp]\n" +
            "  await:\n" +
            "    strategy: native";

    @Mock
    private Cube cube;

    @Test
    public void should_create_static_await_strategy() {

        @SuppressWarnings("unchecked")
        Map<String, Object> content = (Map<String, Object>) new Yaml().load(CONTENT_WITH_STATIC_STRATEGY);
        @SuppressWarnings("unchecked")
        Map<String, Object> tomcatConfig = (Map<String, Object>) content.get("tomcat");

        AwaitStrategy strategy = AwaitStrategyFactory.create(null, cube, tomcatConfig);

        assertThat(strategy, instanceOf(StaticAwaitStrategy.class));
        StaticAwaitStrategy staticAwaitStrategy = (StaticAwaitStrategy)strategy;

        assertThat(staticAwaitStrategy.getIp(), is("localhost"));
        assertThat(staticAwaitStrategy.getPorts().get(0), is(8080));
        assertThat(staticAwaitStrategy.getPorts().get(1), is(8089));
    }

    @Test
    public void should_create_native_await_strategy_if_no_strategy_is_provided() {

        @SuppressWarnings("unchecked")
        Map<String, Object> content = (Map<String, Object>) new Yaml().load(CONTENT_WITH_NO_STRATEGY);
        @SuppressWarnings("unchecked")
        Map<String, Object> tomcatConfig = (Map<String, Object>) content.get("tomcat");

        AwaitStrategy strategy = AwaitStrategyFactory.create(null, cube, tomcatConfig);

        assertThat(strategy, instanceOf(NativeAwaitStrategy.class));
    }

    @Test
    public void should_create_polling_await_strategy() {

        @SuppressWarnings("unchecked")
        Map<String, Object> content = (Map<String, Object>) new Yaml().load(CONTENT_WITH_POLLING_STRATEGY);
        @SuppressWarnings("unchecked")
        Map<String, Object> tomcatConfig = (Map<String, Object>) content.get("tomcat");

        AwaitStrategy strategy = AwaitStrategyFactory.create(null, cube, tomcatConfig);

        assertThat(strategy, instanceOf(PollingAwaitStrategy.class));
    }

    @Test
    public void should_create_native_await_strategy() {

        @SuppressWarnings("unchecked")
        Map<String, Object> content = (Map<String, Object>) new Yaml().load(CONTENT_WITH_NATIVE_STRATEGY);
        @SuppressWarnings("unchecked")
        Map<String, Object> tomcatConfig = (Map<String, Object>) content.get("tomcat");

        AwaitStrategy strategy = AwaitStrategyFactory.create(null, cube, tomcatConfig);

        assertThat(strategy, instanceOf(NativeAwaitStrategy.class));
    }
}