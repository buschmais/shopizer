package com.salesmanager.catalog.api;

import com.salesmanager.catalog.api.dto.product.FinalPriceDTO;

import java.util.List;

public interface ProductPriceApi {

    FinalPriceDTO getProductFinalPrice(Long productId, List<Long> attributeIds);

}
