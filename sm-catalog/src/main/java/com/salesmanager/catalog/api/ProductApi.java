package com.salesmanager.catalog.api;

import com.salesmanager.catalog.api.dto.product.*;
import com.salesmanager.common.business.exception.ServiceException;
import com.salesmanager.common.presentation.model.BreadcrumbItem;
import com.salesmanager.core.integration.language.LanguageDTO;

import java.util.Locale;
import java.util.Set;

public interface ProductApi {

    BreadcrumbItem getBreadcrumbItemForLocale(long productId, LanguageDTO languageDTO, Locale locale) throws ServiceException;

    Long getProductTaxClassId(Long productId);

    Integer getProductMerchantStoreId(Long productId);

    Set<ProductAttributeDTO> getProductAttributes(Long productId);

    boolean isAvailable(Long productId);

    Set<ProductDescriptionDTO> getProductDescriptions(Long productId);

    ProductImageDTO getDefaultImage(Long productId);

    Integer getAvailabilityForRegion(Long productId, String region);

}
