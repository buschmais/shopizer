package com.salesmanager.catalog.api;

import com.salesmanager.catalog.model.product.price.FinalPrice;
import com.salesmanager.common.business.exception.ServiceException;
import com.salesmanager.core.integration.merchant.MerchantStoreDTO;

import java.math.BigDecimal;
import java.util.List;

public interface ProductPriceApi {

    String getAdminFormattedAmountWithCurrency(MerchantStoreDTO merchantStoreDTO, BigDecimal amount) throws Exception;

    String getAdminFormattedAmount(MerchantStoreDTO merchantStoreDTO, BigDecimal amount) throws Exception;

    String getStoreFormattedAmountWithCurrency(MerchantStoreDTO merchantStoreDTO, BigDecimal amount) throws Exception;

    BigDecimal getAmount(String formattedAmount) throws Exception;

    FinalPrice getFinalProductPrice(Long productId, List<Long> productAttributeIds) throws ServiceException;

    FinalPrice calculateProductPrice(Long productId) throws ServiceException;
}
