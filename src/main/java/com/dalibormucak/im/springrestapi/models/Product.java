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


    /*public Integer getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice_hrk() {
        return price_hrk;
    }

    public void setPrice_hrk(BigDecimal price_hrk) {
        this.price_hrk = price_hrk;
    }

    public BigDecimal getPrice_eur() {
        return price_eur;
    }

    public void setPrice_eur(BigDecimal price_eur) {
        this.price_eur = price_eur;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIs_available() {
        return is_available;
    }

    public void setIs_available(Boolean is_available) {
        this.is_available = is_available;
    }*/
}
