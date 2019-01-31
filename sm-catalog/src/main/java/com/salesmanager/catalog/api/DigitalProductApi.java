package com.salesmanager.catalog.api;

import com.salesmanager.common.business.exception.ServiceException;
import com.salesmanager.core.integration.merchant.MerchantStoreDTO;

public interface DigitalProductApi {

    String getFileNameByProduct(MerchantStoreDTO store, Long productId) throws ServiceException;

}
