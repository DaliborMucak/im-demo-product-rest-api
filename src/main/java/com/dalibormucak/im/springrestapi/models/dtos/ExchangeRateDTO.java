package com.dalibormucak.im.springrestapi.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ExchangeRateDTO {

    @JsonProperty("Broj tečajnice")
    private String excNum;

    @JsonProperty("Datum primjene")
    private String dateOfAppl;

    @JsonProperty("Država")
    private String state;

    @JsonProperty("Šifra valute")
    private String currencyCode;

    @JsonProperty("Valuta")
    private String currency;

    @JsonProperty("Jedinica")
    private String unit;

    @JsonProperty("Kupovni za devize")
    private String purchaseRate;

    @JsonProperty("Srednji za devize")
    private String mediumRate;

    @JsonProperty("Prodajni za devize")
    private String sellingRate;
}
