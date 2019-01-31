package com.salesmanager.catalog.api;

import com.salesmanager.catalog.api.dto.product.FinalPriceDTO;
import com.salesmanager.core.integration.merchant.MerchantStoreDTO;

import java.math.BigDecimal;
import java.util.List;

public interface ProductPriceApi {

    String getStoreFormattedAmountWithCurrency(MerchantStoreDTO merchantStoreDTO, BigDecimal amount) throws Exception;

    BigDecimal getAmount(String formattedAmount) throws Exception;

    FinalPriceDTO getProductFinalPrice(Long productId, List<Long> attributeIds);
}
