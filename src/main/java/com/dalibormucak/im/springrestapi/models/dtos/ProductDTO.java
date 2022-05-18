package com.dalibormucak.im.springrestapi.models.dtos;

import com.dalibormucak.im.springrestapi.models.Product;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.math.RoundingMode;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductDTO {
    @JsonProperty("code")
    @Pattern(regexp = "^[A-Z0-9]{10}$", message = "The product code is invalid")
    private String code;

    @JsonProperty("name")
    @Length(max = 32, message = "The product name cannot exceed 32 characters")
    private String name;

    @JsonProperty("price_hrk")
    @Positive(message = "The product price must be positive")
    private BigDecimal price_hrk;

    @JsonProperty("description")
    @Length(max = 128, message = "The product description cannot exceed 128 characters")
    private String description;

    @JsonProperty("is_available")
    @NotNull(message = "Product availability must be defined")
    private Boolean is_available;

    public Product toProduct() {
        return Product.builder()
                .code(code)
                .name(name)
                .price_hrk(price_hrk.setScale(2, RoundingMode.HALF_EVEN))
                .description(description)
                .is_available(is_available)
                .build();
    }
}
