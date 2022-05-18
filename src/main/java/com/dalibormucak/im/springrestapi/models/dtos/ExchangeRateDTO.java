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

    /*public ExchangeRateDTO(String excNum, String dateOfAppl, String state, String currencyCode,
                           String currency, String unit, String purchaseRate,
                           String mediumRate, String sellingRate) {
        this.excNum = excNum;
        this.dateOfAppl = dateOfAppl;
        this.state = state;
        this.currencyCode = currencyCode;
        this.currency = currency;
        this.unit = unit;
        this.purchaseRate = purchaseRate;
        this.mediumRate = mediumRate;
        this.sellingRate = sellingRate;
    }*/

    /*public String getExcNum() {
        return excNum;
    }

    public void setExcNum(String excNum) {
        this.excNum = excNum;
    }

    public String getDateOfAppl() {
        return dateOfAppl;
    }

    public void setDateOfAppl(String dateOfAppl) {
        this.dateOfAppl = dateOfAppl;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPurchaseRate() {
        return purchaseRate;
    }

    public void setPurchaseRate(String purchaseRate) {
        this.purchaseRate = purchaseRate;
    }

    public String getMediumRate() {
        return mediumRate;
    }

    public void setMediumRate(String mediumRate) {
        this.mediumRate = mediumRate;
    }

    public String getSellingRate() {
        return sellingRate;
    }

    public void setSellingRate(String sellingRate) {
        this.sellingRate = sellingRate;
    }*/
}
