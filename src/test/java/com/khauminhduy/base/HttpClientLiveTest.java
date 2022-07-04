package com.khauminhduy.base;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.khauminhduy.ResponseUtil;

public class HttpClientLiveTest {

	private static final String SAMPLE_URL = "http://www.github.com";
	private CloseableHttpClient instance;
	private CloseableHttpResponse response;

	@Before
	public void init() {
		instance = HttpClients.createDefault();
	}

	@Test(expected = ConnectTimeoutException.class)
	public void givenLowTimeout_whenExecutingRequestWithTimeout_thenException()
			throws ClientProtocolException, IOException {
		// @formatter:off
		final RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(1)
				.setConnectTimeout(1)
				.setSocketTimeout(1)
				.build();
		final HttpGet request = new HttpGet(SAMPLE_URL);
		request.setConfig(requestConfig);
		response = instance.execute(request);
		// @formatter:on

	}

	@Test
	public void givenHttpClientIsConfiguredWithCustomConnectionManager_whenExecutingRequest_thenNoExceptions()
			throws ClientProtocolException, IOException {
		instance = HttpClientBuilder.create().setConnectionManager(new BasicHttpClientConnectionManager()).build();
		response = instance.execute(new HttpGet(SAMPLE_URL));
	}

	@Test
	public void whenRequestIsCanceled_thenCorrect() throws ClientProtocolException, IOException {
		instance = HttpClients.createDefault();
		final HttpGet request = new HttpGet(SAMPLE_URL);
		response = instance.execute(request);

		try {
			final HttpEntity entity = response.getEntity();

			System.out.println("----------------------------------------");
			System.out.println(response.getStatusLine());
			if (entity != null) {
				System.out.println("Response content length: " + entity.getContentLength());
			}
			System.out.println("----------------------------------------");

			// Do not feel like reading the response body
			// Call abort on the request object
			request.abort();
		} finally {
			response.close();
		}

	}

	@After
	public void tearDown() throws IOException {
		ResponseUtil.closeResponse(response);
	}
}
