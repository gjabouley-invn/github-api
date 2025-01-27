package org.kohsuke.github.internal;

import org.junit.Assert;
import org.junit.Test;
import org.kohsuke.github.AbstractGitHubWireMockTest;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.HttpConnector;
import org.kohsuke.github.connector.GitHubConnector;
import org.kohsuke.github.connector.GitHubConnectorRequest;
import org.kohsuke.github.connector.GitHubConnectorResponse;
import org.kohsuke.github.extras.okhttp3.OkHttpConnector;
import org.kohsuke.github.extras.okhttp3.OkHttpGitHubConnector;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.*;

public class DefaultGitHubConnectorTest extends AbstractGitHubWireMockTest {

    public DefaultGitHubConnectorTest() {
        useDefaultGitHub = false;
    }

    @Test
    public void testCreate() throws Exception {
        GitHubConnector connector;
        GitHubConnectorHttpConnectorAdapter adapter;

        connector = DefaultGitHubConnector.create("default");
        assertThat(connector, instanceOf(GitHubConnectorHttpConnectorAdapter.class));
        adapter = (GitHubConnectorHttpConnectorAdapter) connector;
        assertThat(adapter.httpConnector, equalTo(HttpConnector.DEFAULT));

        connector = DefaultGitHubConnector.create("urlconnection");
        assertThat(connector, instanceOf(GitHubConnectorHttpConnectorAdapter.class));
        adapter = (GitHubConnectorHttpConnectorAdapter) connector;
        assertThat(adapter.httpConnector, equalTo(HttpConnector.DEFAULT));

        connector = DefaultGitHubConnector.create("okhttpconnector");
        assertThat(connector, instanceOf(GitHubConnectorHttpConnectorAdapter.class));
        adapter = (GitHubConnectorHttpConnectorAdapter) connector;
        assertThat(adapter.httpConnector, instanceOf(OkHttpConnector.class));

        connector = DefaultGitHubConnector.create("okhttp");
        assertThat(connector, instanceOf(OkHttpGitHubConnector.class));

        Assert.assertThrows(IllegalStateException.class, () -> DefaultGitHubConnector.create(""));

        assertThat(GitHubConnectorHttpConnectorAdapter.adapt(HttpConnector.DEFAULT),
                sameInstance(GitHubConnector.DEFAULT));
        assertThat(GitHubConnectorHttpConnectorAdapter.adapt(HttpConnector.OFFLINE),
                sameInstance(GitHubConnector.OFFLINE));

        gitHub = new GitHubBuilder().withConnector(new GitHubConnector() {
            @Override
            public GitHubConnectorResponse send(GitHubConnectorRequest connectorRequest) throws IOException {
                throw new IOException();
            }
        }).build();
        Assert.assertThrows(UnsupportedOperationException.class, () -> gitHub.getConnector());
        gitHub.setConnector((HttpConnector) GitHubConnector.OFFLINE);
        // Doesn't throw when HttpConnect is implemented
        gitHub.getConnector();
    }
}
