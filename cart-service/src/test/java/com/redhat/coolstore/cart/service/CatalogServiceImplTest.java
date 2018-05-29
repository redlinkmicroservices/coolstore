package com.redhat.coolstore.cart.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.redhat.coolstore.cart.model.Product;


public class CatalogServiceImplTest {

	@Rule
    public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.wireMockConfig().dynamicPort());

	private CatalogServiceImpl catalogService;

	@Before
	public void setupCatalogService () {
		catalogService = new CatalogServiceImpl();
        ReflectionTestUtils.setField(catalogService, "catalogServiceUrl", "http://localhost:" + wireMockRule.port(), null);		
	}
	
	
	@Test
	public void getProduct() throws IOException {
		InputStream is = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("catalog-response.json");
		stubFor(WireMock.get(WireMock.urlEqualTo("/product/111111"))
				.willReturn(aResponse()
						.withStatus(200)
						.withHeader("Content-type", "application/json")
						.withBody(IOUtils.toString(is, Charset.defaultCharset()))));

        Product p = catalogService.getProduct("111111");
        assertThat(p, notNullValue());
        assertThat(p.getItemId(), notNullValue());
        assertThat(p.getItemId(), equalTo("111111"));
        assertThat(p.getPrice(), equalTo(new Double(100.0)));
        verify(getRequestedFor(urlEqualTo("/product/111111")));
	}
	
	@Test
    public void getProductWhenCatalogServerRespondsWithNotFound() throws Exception {

        stubFor(get(urlEqualTo("/product/111111"))
                .willReturn(aResponse()
                		.withStatus(404)));

        Product product = catalogService.getProduct("111111");

        assertThat(product, nullValue());
        verify(getRequestedFor(urlEqualTo("/product/111111")));
    }

    @Test
    public void getProductWhenCatalogServerRespondsWithError() throws Exception {

        stubFor(get(urlEqualTo("/product/111111"))
                .willReturn(aResponse()
                		.withStatus(503).withHeader("Content-type", "text/plain").withBody("{}")));

        try {
            catalogService.getProduct("111111");
            fail();
        }
        catch (HttpStatusCodeException e) {
            assertThat(e.getRawStatusCode(), equalTo(503));
        }

        verify(getRequestedFor(urlEqualTo("/product/111111")));
    }

    @Test
    public void getProductWhenCatalogServerIsDown() throws Exception {

        wireMockRule.shutdownServer();
        Thread.sleep(1000);

        try {
            catalogService.getProduct("111111");
            fail();
        }
        catch (RestClientException e) {
            assertThat(e.getMessage(), containsString("I/O"));
        }
    }
}
