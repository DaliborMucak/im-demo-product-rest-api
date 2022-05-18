package com.dalibormucak.im.springrestapi;

import com.dalibormucak.im.springrestapi.models.Product;
import com.dalibormucak.im.springrestapi.models.dtos.ProductDTO;
import com.dalibormucak.im.springrestapi.repositories.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;

@SpringBootTest(classes = SpringRestApi.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SpringRestApiProductControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProductRepository productRepository;

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void beforeAll() {
        wireMockServer = new WireMockServer(
                new WireMockConfiguration()
                        .options()
                        .usingFilesUnderDirectory("src/test/resources")
                        .port(7070));
        wireMockServer.start();
        configureFor("localhost", 7070);

        wireMockServer.stubFor(get(urlEqualTo("/tecajn/v1?valuta=EUR"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.CREATED.value())
                        .withHeader("Content-Type", "application/json;charset=UTF-8")
                        .withBodyFile("hnb-api-response.json")));
    }

    @AfterEach
    void afterEach() {
        wireMockServer.resetRequests();
    }

    @AfterAll
    static void afterAll() {
        wireMockServer.stop();
    }

    @Test
    void testCreateProduct() throws Exception {

        //given
        BDDMockito.willAnswer((Answer) invocation -> {
            Product passedProduct = invocation.getArgument(0);
            return passedProduct;
        }).given(productRepository).save(any(Product.class));

        ProductDTO productDTO = new ProductDTO("203V5LSB26", "Monitor Philips", BigDecimal.valueOf(1000.00),
                "Enjoy vivid LED pictures with this attractive, glossy design display.", true);
        String productDTO_AsJson = new ObjectMapper().writeValueAsString(productDTO);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/products")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productDTO_AsJson);

        //when
        MvcResult result = mvc.perform(requestBuilder).andReturn();

        //then
        wireMockServer.verify(getRequestedFor(urlEqualTo("/tecajn/v1?valuta=EUR")));
        Mockito.verify(productRepository, times(1)).save(any(Product.class));

        assertThat(result.getResponse().getStatus()).isEqualTo(201);

        DocumentContext jsonResponse = JsonPath.parse(result.getResponse().getContentAsString());
        String actualCode = jsonResponse.read("code");
        BigDecimal actualPriceKn = jsonResponse.read("price_hrk", BigDecimal.class);
        BigDecimal actualPriceEur = jsonResponse.read("price_eur", BigDecimal.class);
        assertThat(actualCode).isEqualTo("203V5LSB26");
        assertThat(actualPriceKn.compareTo(BigDecimal.valueOf(1000.00)) == 0).isTrue();
        assertThat(actualPriceEur.compareTo(BigDecimal.valueOf(132.87)) == 0).isTrue();
    }

    @Test
    void testCreateProduct_withInvalidCode() throws Exception {

        //given
        BDDMockito.willAnswer((Answer) invocation -> {
            Product passedProduct = invocation.getArgument(0);
            return passedProduct;
        }).given(productRepository).save(any(Product.class));

        ProductDTO productDTO = new ProductDTO("203.V5@B20", "Monitor Philips", BigDecimal.valueOf(1000.00),
                "Enjoy vivid LED pictures with this attractive, glossy design display.", true);
        String productDTO_AsJson = new ObjectMapper().writeValueAsString(productDTO);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/products")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productDTO_AsJson);

        //when
        MvcResult result = mvc.perform(requestBuilder).andReturn();

        //then
        assertThat(result.getResponse().getStatus()).isEqualTo(400);

        DocumentContext jsonResponse = JsonPath.parse(result.getResponse().getContentAsString());
        List<String> errors = jsonResponse.read("errors");
        String errorMsg = errors.get(0);
        assertThat(errorMsg).isEqualTo("The product code is invalid");
    }

    @Test
    void testGetProduct() throws Exception {

        //given
        BDDMockito.willAnswer((Answer) invocation -> {
            Integer passedId = invocation.getArgument(0);
            Product product = Product.builder()
                    .id(passedId)
                    .code("485T5LSB26")
                    .name("Monitor Asus")
                    .price_hrk(BigDecimal.valueOf(1200.00))
                    .price_eur(BigDecimal.valueOf(159.44))
                    .description("HDR technology delivers exceptional onscreen clarity and detail.")
                    .is_available(false).build();
            return Optional.of(product);
        }).given(productRepository).findById(anyInt());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/products/9");

        //when
        MvcResult result = mvc.perform(requestBuilder).andReturn();

        //then
        Mockito.verify(productRepository, times(1)).findById(anyInt());

        assertThat(result.getResponse().getStatus()).isEqualTo(200);

        DocumentContext jsonResponse = JsonPath.parse(result.getResponse().getContentAsString());
        Integer actualId = jsonResponse.read("id");
        Boolean actualIsAvailable = jsonResponse.read("is_available");
        assertThat(actualId).isEqualTo(9);
        assertThat(actualIsAvailable).isFalse();
    }

    @Test
    void testGet_nonExistentProduct() throws Exception {

        //given
        BDDMockito.willAnswer((Answer) invocation -> {
            return Optional.empty();
        }).given(productRepository).findById(anyInt());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/products/10");

        //when
        MvcResult result = mvc.perform(requestBuilder).andReturn();

        //then
        Mockito.verify(productRepository, times(1)).findById(anyInt());

        assertThat(result.getResponse().getStatus()).isEqualTo(404);
    }

    @Test
    void testGetAllProducts() throws Exception {

        //given
        BDDMockito.willAnswer((Answer) invocation -> {
            List<Product> allProducts = new ArrayList<>();
            Product product1 = Product.builder()
                    .id(0)
                    .code("485T5LSB26")
                    .name("Monitor Asus")
                    .price_hrk(BigDecimal.valueOf(1200.00))
                    .price_eur(BigDecimal.valueOf(159.44))
                    .description("HDR technology delivers exceptional onscreen clarity and detail.")
                    .is_available(false)
                    .build();
            Product product2 = Product.builder()
                    .id(1).code("203V5LSB26")
                    .name("Monitor Philips")
                    .price_hrk(BigDecimal.valueOf(1000.00))
                    .price_eur(BigDecimal.valueOf(132.87))
                    .description("Enjoy vivid LED pictures with this attractive, glossy design display.")
                    .is_available(true)
                    .build();
            allProducts.add(product1);
            allProducts.add(product2);
            return allProducts;
        }).given(productRepository).findAll();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/products");

        //when
        MvcResult result = mvc.perform(requestBuilder).andReturn();

        //then
        Mockito.verify(productRepository, times(1)).findAll();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);

        DocumentContext jsonResponse = JsonPath.parse(result.getResponse().getContentAsString());
        int numOfProducts = jsonResponse.read("$.length()");
        assertThat(numOfProducts).isEqualTo(2);
    }

    @Test
    void testUpdateProduct() throws Exception {

        //given
        BDDMockito.willAnswer((Answer) invocation -> {
            Integer passedId = invocation.getArgument(0);
            Product product = Product.builder()
                    .id(passedId)
                    .code("203V5LSB26")
                    .name("Monitor Philips")
                    .price_hrk(BigDecimal.valueOf(1000.00))
                    .price_eur(BigDecimal.valueOf(132.87))
                    .description("Enjoy vivid LED pictures with this attractive, glossy design display.")
                    .is_available(true)
                    .build();
            return Optional.of(product);
        }).given(productRepository).findById(anyInt());

        BDDMockito.willAnswer((Answer) invocation -> {
            Product passedProduct = invocation.getArgument(0);
            return passedProduct;
        }).given(productRepository).save(any(Product.class));

        ProductDTO productDTO = new ProductDTO("AEZAKMI007", "Monitor Philips 20inch", BigDecimal.valueOf(900.00),
                "Enjoy vivid LED pictures with this attractive design display.", true);
        String productDTO_AsJson = new ObjectMapper().writeValueAsString(productDTO);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/products/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productDTO_AsJson);

        //when
        MvcResult result = mvc.perform(requestBuilder).andReturn();

        //then
        wireMockServer.verify(getRequestedFor(urlEqualTo("/tecajn/v1?valuta=EUR")));
        Mockito.verify(productRepository, times(1)).findById(anyInt());
        Mockito.verify(productRepository, times(1)).save(any(Product.class));

        assertThat(result.getResponse().getStatus()).isEqualTo(200);

        DocumentContext jsonResponse = JsonPath.parse(result.getResponse().getContentAsString());
        String actualCode = jsonResponse.read("code");
        BigDecimal actualPriceKn = jsonResponse.read("price_hrk", BigDecimal.class);
        BigDecimal actualPriceEur = jsonResponse.read("price_eur", BigDecimal.class);
        assertThat(actualCode).isEqualTo("AEZAKMI007");
        assertThat(actualPriceKn.compareTo(BigDecimal.valueOf(900.00)) == 0).isTrue();
        assertThat(actualPriceEur.compareTo(BigDecimal.valueOf(119.58)) == 0).isTrue();
    }

    @Test
    void testUpdateProduct_withNonUniqueCode() throws Exception {

        //given
        BDDMockito.willAnswer((Answer) invocation -> {
            Integer passedId = invocation.getArgument(0);
            Product product = Product.builder()
                    .id(passedId)
                    .code("203V5LSB26")
                    .name("Monitor Philips")
                    .price_hrk(BigDecimal.valueOf(1000.00))
                    .price_eur(BigDecimal.valueOf(132.87))
                    .description("Enjoy vivid LED pictures with this attractive, glossy design display.")
                    .is_available(true)
                    .build();
            return Optional.of(product);
        }).given(productRepository).findById(anyInt());

        BDDMockito.willAnswer((Answer) invocation -> {
            throw new PSQLException(new ServerErrorMessage("ERROR: duplicate key value violates unique constraint"));
        }).given(productRepository).save(any(Product.class));

        ProductDTO productDTO = new ProductDTO("485T5LSB26", "Monitor Philips 20inch", BigDecimal.valueOf(1100.00),
                "Enjoy vivid LED pictures with this attractive design display.", true);
        String productDTO_AsJson = new ObjectMapper().writeValueAsString(productDTO);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/products/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productDTO_AsJson);

        //when
        MvcResult result = mvc.perform(requestBuilder).andReturn();

        //then
        assertThat(result.getResponse().getStatus()).isEqualTo(500);

        DocumentContext jsonResponse = JsonPath.parse(result.getResponse().getContentAsString());
        List<String> errors = jsonResponse.read("errors");
        String errorMsg = errors.get(0);
        assertThat(errorMsg).isEqualTo("Database error");
    }

    @Test
    void testPatchProduct_changePrice() throws Exception {

        //given
        BDDMockito.willAnswer((Answer) invocation -> {
            Integer passedId = invocation.getArgument(0);
            Product product = Product.builder()
                    .id(passedId)
                    .code("203V5LSB26")
                    .name("Monitor Philips")
                    .price_hrk(BigDecimal.valueOf(1000.00))
                    .price_eur(BigDecimal.valueOf(132.87))
                    .description("Enjoy vivid LED pictures with this attractive, glossy design display.")
                    .is_available(true)
                    .build();
            return Optional.of(product);
        }).given(productRepository).findById(anyInt());

        BDDMockito.willAnswer((Answer) invocation -> {
            Product passedProduct = invocation.getArgument(0);
            return passedProduct;
        }).given(productRepository).save(any(Product.class));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/products/1")
                .accept("application/json-patch+json")
                .contentType("application/json-patch+json")
                .content("[{ \"op\": \"replace\", \"path\": \"/price_hrk\", \"value\": 800.00 }]");

        //when
        MvcResult result = mvc.perform(requestBuilder).andReturn();

        //then
        wireMockServer.verify(getRequestedFor(urlEqualTo("/tecajn/v1?valuta=EUR")));
        Mockito.verify(productRepository, times(1)).findById(anyInt());
        Mockito.verify(productRepository, times(1)).save(any(Product.class));

        assertThat(result.getResponse().getStatus()).isEqualTo(200);

        DocumentContext jsonResponse = JsonPath.parse(result.getResponse().getContentAsString());
        BigDecimal actualPriceKn = jsonResponse.read("price_hrk", BigDecimal.class);
        BigDecimal actualPriceEur = jsonResponse.read("price_eur", BigDecimal.class);
        assertThat(actualPriceKn.compareTo(BigDecimal.valueOf(800.00)) == 0).isTrue();
        assertThat(actualPriceEur.compareTo(BigDecimal.valueOf(106.30)) == 0).isTrue();
    }

    @Test
    void testPatchProduct_withInvalidPrice() throws Exception {

        //given
        BDDMockito.willAnswer((Answer) invocation -> {
            Integer passedId = invocation.getArgument(0);
            Product product = Product.builder()
                    .id(passedId)
                    .code("203V5LSB26")
                    .name("Monitor Philips")
                    .price_hrk(BigDecimal.valueOf(1000.00))
                    .price_eur(BigDecimal.valueOf(132.87))
                    .description("Enjoy vivid LED pictures with this attractive, glossy design display.")
                    .is_available(true)
                    .build();
            return Optional.of(product);
        }).given(productRepository).findById(anyInt());

        BDDMockito.willAnswer((Answer) invocation -> {
            Product passedProduct = invocation.getArgument(0);
            return passedProduct;
        }).given(productRepository).save(any(Product.class));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/products/1")
                .accept("application/json-patch+json")
                .contentType("application/json-patch+json")
                .content("[{ \"op\": \"replace\", \"path\": \"/price_hrk\", \"value\": -200.00 }]");

        //when
        MvcResult result = mvc.perform(requestBuilder).andReturn();

        //then
        Mockito.verify(productRepository, times(1)).findById(anyInt());

        assertThat(result.getResponse().getStatus()).isEqualTo(400);

        DocumentContext jsonResponse = JsonPath.parse(result.getResponse().getContentAsString());
        List<String> errors = jsonResponse.read("errors");
        String errorMsg = errors.get(0);
        assertThat(errorMsg).isEqualTo("The product price must be positive");
    }

    @Test
    void testDeleteProduct() throws Exception {

        //given
        BDDMockito.willAnswer((Answer) invocation -> {
            Integer passedId = invocation.getArgument(0);
            Product product = Product.builder()
                    .id(passedId)
                    .code("203V5LSB26")
                    .name("Monitor Philips")
                    .price_hrk(BigDecimal.valueOf(1000.00))
                    .price_eur(BigDecimal.valueOf(132.87))
                    .description("Enjoy vivid LED pictures with this attractive, glossy design display.")
                    .is_available(true)
                    .build();
            return Optional.of(product);
        }).given(productRepository).findById(anyInt());

        BDDMockito.willDoNothing().given(productRepository).delete(any(Product.class));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/products/9");

        //when
        MvcResult result = mvc.perform(requestBuilder).andReturn();

        //then
        Mockito.verify(productRepository, times(1)).findById(anyInt());
        Mockito.verify(productRepository, times(1)).delete(any(Product.class));

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
    }

    @Test
    void testDelete_nonExistentProduct() throws Exception {

        //given
        BDDMockito.willAnswer((Answer) invocation -> {
            return Optional.empty();
        }).given(productRepository).findById(anyInt());

        BDDMockito.willDoNothing().given(productRepository).delete(any(Product.class));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/products/11");

        //when
        MvcResult result = mvc.perform(requestBuilder).andReturn();

        //then
        Mockito.verify(productRepository, times(1)).findById(anyInt());

        assertThat(result.getResponse().getStatus()).isEqualTo(404);
    }


}
