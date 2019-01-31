package com.salesmanager.catalog.api;

import com.salesmanager.core.integration.merchant.MerchantStoreDTO;

import java.util.List;

public interface ProductRelationshipApi {

    List<String> getGroups(MerchantStoreDTO store);

}
