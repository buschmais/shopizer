package com.salesmanager.core.business.integration.catalog.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FinalPriceDTO extends AbstractCatalogDTO {

    private Boolean discounted;
    private Double finalPrice;
    private Boolean defaultPrice;
    private List<FinalPriceDTO> additionalPrices;
    private ProductPriceDTO productPrice;

}