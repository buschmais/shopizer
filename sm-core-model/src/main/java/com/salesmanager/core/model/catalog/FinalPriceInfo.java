package com.salesmanager.core.model.catalog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class FinalPriceInfo {

    private Boolean discounted;
    private Double finalPrice;
    private Boolean defaultPrice;
    private List<FinalPriceInfo> additionalPrices;
    private ProductPriceInfo productPrice;

}