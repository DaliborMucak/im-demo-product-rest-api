package com.dalibormucak.im.springrestapi.services;

import com.dalibormucak.im.springrestapi.models.Product;
import com.dalibormucak.im.springrestapi.models.dtos.ExchangeRateDTO;
import com.dalibormucak.im.springrestapi.models.dtos.ProductDTO;
import com.dalibormucak.im.springrestapi.repositories.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;
    private final ProgrammaticallyValidatingService validatingService;
    @Value("${base.url.hnb.api}")
    private String HNB_API_BASE_URL;

    @Autowired
    public ProductService(ProductRepository productRepository, RestTemplate restTemplate,
                          ProgrammaticallyValidatingService validatingService) {
        this.productRepository = productRepository;
        this.restTemplate = restTemplate;
        this.validatingService = validatingService;
    }

    public Product createProduct(Product product) {
        product.setPrice_eur(calcPriceInEur(product.getPrice_hrk()));
        return productRepository.save(product);
    }

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public Product findProductById(Integer productId) throws NoSuchElementException {
        return productRepository.findById(productId).orElseThrow(() ->
                new NoSuchElementException("Product with id " + productId + " does not exist"));
    }

    public Product updateProduct(Integer productId, ProductDTO productDTO) {
        Product product = findProductById(productId);
        product.setCode(productDTO.getCode());
        product.setName(productDTO.getName());
        product.setPrice_hrk(productDTO.getPrice_hrk().setScale(2, RoundingMode.HALF_EVEN));
        product.setPrice_eur(calcPriceInEur(productDTO.getPrice_hrk()));
        product.setDescription(productDTO.getDescription());
        product.setIs_available(productDTO.getIs_available());
        return productRepository.save(product);
    }

    public Product patchProduct(Integer productId, JsonPatch productPatch) throws JsonPatchException, JsonProcessingException {
        Product product = findProductById(productId);
        ProductDTO productDTO = new ProductDTO(product.getCode(), product.getName(), product.getPrice_hrk(),
                product.getDescription(), product.getIs_available());
        ProductDTO patchedProductDTO = applyPatchToProductDTO(productPatch, productDTO);
        validatingService.validateObject(patchedProductDTO);
        product.setCode(patchedProductDTO.getCode());
        product.setName(patchedProductDTO.getName());
        if (patchedProductDTO.getPrice_hrk().compareTo(productDTO.getPrice_hrk()) != 0){
            product.setPrice_hrk(patchedProductDTO.getPrice_hrk().setScale(2, RoundingMode.HALF_EVEN));
            product.setPrice_eur(calcPriceInEur(patchedProductDTO.getPrice_hrk()));
        }
        product.setDescription(patchedProductDTO.getDescription());
        product.setIs_available(patchedProductDTO.getIs_available());
        return productRepository.save(product);
    }

    public void deleteProduct(Integer productId) {
        Product targetProduct = findProductById(productId);
        productRepository.delete(targetProduct);
    }

//    method intended for applying JsonPatch to ProductDTO object
//    using the com.fasterxml.jackson.databind.JsonNode instance
    private ProductDTO applyPatchToProductDTO(
            JsonPatch patch, ProductDTO productDTO) throws JsonPatchException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode patched = patch.apply(objectMapper.convertValue(productDTO, JsonNode.class));
        return objectMapper.treeToValue(patched, ProductDTO.class);
    }


//    method which calculates the price in EUR calling the HNB API using RestTemplate
//    (as opposed to asynchronous WebClient) and returns the BigDecimal result
    private BigDecimal calcPriceInEur(BigDecimal priceInKn) {
        ResponseEntity<List<ExchangeRateDTO>> exchangeRateData = restTemplate.exchange(
                HNB_API_BASE_URL + "/tecajn/v1?valuta=EUR", HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {
                });
        String exchangeRateString = exchangeRateData.getBody().get(0).getMediumRate().replace(",", ".");
        BigDecimal exchangeRateEurToKn = new BigDecimal(exchangeRateString);
        return priceInKn.divide(exchangeRateEurToKn, 2, RoundingMode.HALF_EVEN);
    }
}
