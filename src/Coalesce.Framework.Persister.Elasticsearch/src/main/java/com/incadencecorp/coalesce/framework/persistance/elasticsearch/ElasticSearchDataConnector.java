package com.incadencecorp.coalesce.framework.persistance.elasticsearch;

import com.google.common.net.HostAndPort;
import com.incadencecorp.coalesce.common.exceptions.CoalescePersistorException;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;

public class ElasticSearchDataConnector implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchDataConnector.class);

    private RestHighLevelClient client;

    private SSLContext sslContext;

    public RestHighLevelClient getDBConnector(Map<String, String> props)
    {
        Properties properties = new Properties();
        properties.putAll(props);

        return getDBConnector(properties);
    }

    public RestHighLevelClient getDBConnector(Properties props)
    {
        if (client == null)
        {
            boolean isSSLEnabled =
                    props.containsKey(ElasticSearchSettings.PARAM_SSL_ENABLED) && Boolean.parseBoolean((String) props.get(
                            ElasticSearchSettings.PARAM_SSL_ENABLED));

            try
            {
                client = createClient(isSSLEnabled, props);
            }
            catch (CoalescePersistorException e)
            {
                throw new RuntimeException(e);
            }
        }

        return client;
    }

    @Override
    public void close()
    {
        if (client != null)
        {
            try
            {
                client.close();
            }
            catch (IOException e)
            {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    private RestHighLevelClient createClient(boolean isSSLEnabled, Properties props) throws CoalescePersistorException
    {
        try
        {
            List<HttpHost> hostList = new ArrayList<>();

            String hosts = props.getProperty(ElasticSearchSettings.PARAM_HOSTS);
            Stream.of(hosts.split(",")).map(host -> {
                HostAndPort hostAndPort = HostAndPort.fromString(host).withDefaultPort(9200);

                try
                {
                    return new HttpHost(InetAddress.getByName(hostAndPort.getHost()), hostAndPort.getPort(), isSSLEnabled ? "https" : "http");
                }
                catch (UnknownHostException e)
                {
                    LOGGER.warn(e.getMessage(), e);
                    return null;
                }
            }).forEach(hostList::add);

            RestClientBuilder clientBuilder = RestClient.builder(hostList.toArray(new HttpHost[0]));

            if (isSSLEnabled)
            {
                SSLContext context = getSslContext(props);

                clientBuilder.setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder.setSSLContext(
                        context));
            }

            if (!props.getProperty(ElasticSearchSettings.PARAM_USERNAME).isEmpty())
            {
                LOGGER.debug("Adding credentials for: {}", props.getProperty(ElasticSearchSettings.PARAM_USERNAME));
                final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(
                        props.getProperty(ElasticSearchSettings.PARAM_USERNAME),
                        props.getProperty(ElasticSearchSettings.PARAM_PASSWORD))
                );

                clientBuilder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
            }

            return new RestHighLevelClient(clientBuilder);

        }
        catch (ElasticsearchException ex)
        {
            throw new CoalescePersistorException(ex);

        }
    }

    private SSLContext getSslContext(Properties props) throws CoalescePersistorException
    {

        if (sslContext == null)
        {

            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("Keystore = {}", props.getProperty(ElasticSearchSettings.PARAM_KEYSTORE_FILE));
                LOGGER.debug("Truststore = {}", props.getProperty(ElasticSearchSettings.PARAM_TRUSTSTORE_FILE));
            }

            try
            {

                KeyStore truststore = KeyStore.getInstance("jks");
                KeyStore keyStore = KeyStore.getInstance("jks");
                String keyStoreStr = props.getProperty(ElasticSearchSettings.PARAM_KEYSTORE_FILE);
                String trustStoreStr = props.getProperty(ElasticSearchSettings.PARAM_TRUSTSTORE_FILE);

                try (InputStream is = Files.newInputStream(Paths.get(keyStoreStr)))
                {
                    keyStore.load(is, props.getProperty(ElasticSearchSettings.PARAM_KEYSTORE_PASSWORD).toCharArray());
                }
                try (InputStream is = Files.newInputStream(Paths.get(trustStoreStr)))
                {
                    truststore.load(is, props.getProperty(ElasticSearchSettings.PARAM_TRUSTSTORE_PASSWORD).toCharArray());
                }

                SSLContextBuilder sslBuilder = SSLContexts.custom().loadTrustMaterial(truststore, null).loadKeyMaterial(
                        keyStore,
                        props.getProperty(ElasticSearchSettings.PARAM_KEYSTORE_PASSWORD).toCharArray());
                sslContext = sslBuilder.build();

            }
            catch (Exception e)
            {

                throw new CoalescePersistorException(e);

            }

        }

        return sslContext;

    }
}
