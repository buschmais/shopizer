package com.salesmanager.core.business.integration.catalog.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FinalPriceDTO implements AbstractCatalogDTO {

    private Boolean discounted;
    private Double finalPrice;
    private Boolean defaultPrice;
    private List<FinalPriceDTO> additionalPrices;
    private ProductPriceDTO productPrice;

}