package com.salesmanager.catalog.api.dto.product;

import com.salesmanager.catalog.api.dto.AbstractCatalogDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class FinalPriceDTO extends AbstractCatalogDTO {

    private Boolean discounted;
    private Double finalPrice;
    private Boolean defaultPrice;
    private List<FinalPriceDTO> additionalPrices;
    private ProductPriceDTO productPrice;

}
