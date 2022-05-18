package com.dalibormucak.im.springrestapi.controllers;

import com.dalibormucak.im.springrestapi.models.Product;
import com.dalibormucak.im.springrestapi.models.dtos.ProductDTO;
import com.dalibormucak.im.springrestapi.services.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@Validated
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return new ResponseEntity<>(productService.findAllProducts(), HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProduct(
            @PathVariable("productId") @Positive(message = "Id must be positive integer") Integer productId) {
        return new ResponseEntity<>(productService.findProductById(productId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Product> addProduct(@Valid @RequestBody ProductDTO productDto) {
        Product createdProduct = productService.createProduct(productDto.toProduct());
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateWithPut(
            @PathVariable("productId") @Positive(message = "Id must be positive integer") Integer productId,
            @Valid @RequestBody ProductDTO productDTO) {
        Product updatedProduct = productService.updateProduct(productId, productDTO);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @PatchMapping(path = "/{productId}", consumes = "application/json-patch+json")
    public ResponseEntity<Product> updateWithPatch(
            @PathVariable("productId") @Positive(message = "Id must be positive integer") Integer productId,
            @RequestBody JsonPatch productPatch) throws JsonPatchException, JsonProcessingException {
        Product updatedProduct = productService.patchProduct(productId, productPatch);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> delete(
            @PathVariable("productId") @Positive(message = "Id must be positive integer") Integer productId) {
        productService.deleteProduct(productId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
