package com.dalibormucak.im.springrestapi.models;


import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Product {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PROTECTED)
    private Integer id;

    @Column(name = "code", columnDefinition = "VARCHAR(10) UNIQUE CHECK (CHAR_LENGTH(code) = 10)")
    private String code;

    @Column(name = "name", length = 32)
    private String name;

    @Column(name = "price_hrk", columnDefinition = "DECIMAL(12, 2) CHECK (price_hrk >= 0)", precision = 12, scale = 2)
    private BigDecimal price_hrk;

    @Column(name = "price_eur", columnDefinition = "DECIMAL(12, 2) CHECK (price_eur >= 0)", precision = 12, scale = 2)
    private BigDecimal price_eur;

    @Column(name = "description", length = 128)
    private String description;

    @Column(name = "is_available", nullable = false)
    private Boolean is_available;
}
