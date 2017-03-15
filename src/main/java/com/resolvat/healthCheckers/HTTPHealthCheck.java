package com.resolvat.healthCheckers;

import com.resolvat.model.StatusRepo;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;

/**
 * Created by korteke on 11/03/17.
 */
@Service
public class HTTPHealthCheck extends HealthChecker {

    private static final String PORT_DELIMETER = ":";
    private static final String SCHEME_DELIMETER = "://";
    private static final String DELIMETER = "/";

    @Autowired
    private StatusRepo statusRepo;

    @Value("${http.address}")
    private String httpAddress;

    @Value("${http.port}")
    private int httpPort;

    @Value("${http.scheme}")
    private String httpScheme;

    @Value("${http.timeout}")
    private int httpTimeout;

    @Value("${http.index}")
    private int index;

    @Value("${http.enabled}")
    private String enabled;

    @Value("${http.validateContent}")
    private String validateContent;

    @Value("${http.contentMatch}")
    private String contentMatch;

    @Value("${http.authentication}")
    private String authentication;

    @Value("${http.username}")
    private String httpUsername;

    @Value("${http.password}")
    private String httpPassword;

    @Value("${http.method}")
    private String httpMethod;

    @Value("${http.uri}")
    private String httpUri;

    @Value("${http.okResponseCode}")
    private String okResponseCode;

    /**
     * Default constructor
     */
    public HTTPHealthCheck() {
    }

    @Override
    public void runHealthCheck() {

        logger.debug("httpAddress: " + httpAddress);
        logger.debug("Http Index: " + index);

        StringBuilder serverUrl = new StringBuilder();

        boolean isAlive = true;

        CloseableHttpClient httpClient = createHttpClient(Boolean.valueOf(authentication));

        serverUrl.append(httpScheme);
        serverUrl.append(SCHEME_DELIMETER);
        serverUrl.append(httpAddress);
        serverUrl.append(PORT_DELIMETER);
        serverUrl.append(httpPort);
        serverUrl.append(DELIMETER);
        serverUrl.append(httpUri);
        logger.debug("HTTPS server URL: " + serverUrl.toString());

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(httpTimeout)
                .setConnectTimeout(httpTimeout)
                .setSocketTimeout(httpTimeout)
                .build();

        HttpGet request = new HttpGet(serverUrl.toString());
        request.setConfig(requestConfig);

        try {
            HttpResponse response = httpClient.execute(request);
            int responseCode = response.getStatusLine().getStatusCode();

            if (responseCode != Integer.valueOf(okResponseCode)) {
                isAlive = false;
                logger.error("HTTP Connection error! Response Code: {}", responseCode);
            }

            //TODO: Need to implement content matching / validation
            if (Boolean.valueOf(validateContent)) {
                HttpEntity entity = response.getEntity();
                String content = EntityUtils.toString(entity);
                EntityUtils.consume(entity);
                logger.debug(content);
            }

            logger.debug("HTTP connection OK! Response code: {}", responseCode);

        } catch (IOException e) {
            isAlive = false;
            logger.error("HTTP IO Connection error!");
        } finally {
            logger.debug("Release HTTP connection");
            request.releaseConnection();
            statusRepo.setStatus(isAlive, index);
        }
    }

    /**
     * Create {@link CloseableHttpClient} with or without authentication
     * @param auth authentication enabled
     * @return httpClient
     */
    private CloseableHttpClient createHttpClient(boolean auth) {

        logger.debug("Creating httpClient");
        CloseableHttpClient httpClient = null;

        logger.debug("Authentication: " + Boolean.valueOf(auth));
        if (auth) {
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            logger.debug("username [{}] password [{}]", httpUsername, httpPassword);
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(httpUsername, httpPassword);
            credsProvider.setCredentials(AuthScope.ANY, creds);

            httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(credsProvider).build();
        } else {
            httpClient = HttpClientBuilder.create().build();
        }

        return httpClient;
    }

    /**
     * Parse HTTP response body
     * @param conn {@link HttpURLConnection}
     * @return
     */
    private String httpConnResponseBody(HttpURLConnection conn) {
        String result = null;
        StringBuffer sb = new StringBuffer();
        InputStream is = null;

        try {
            is = new BufferedInputStream(conn.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            result = sb.toString();
        } catch (Exception e) {
            logger.error("HTTP - Error reading InputStream");
            result = null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    logger.debug("HTTP - Error closing InputStream");
                }
            }
        }
        return result;
    }
}
