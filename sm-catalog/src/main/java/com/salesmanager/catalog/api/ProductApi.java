package com.salesmanager.catalog.api;

import com.salesmanager.catalog.api.dto.product.AvailabilityInformationDTO;
import com.salesmanager.catalog.api.dto.product.DimensionDTO;
import com.salesmanager.catalog.api.dto.product.ProductAttributeDTO;
import com.salesmanager.catalog.api.dto.product.ProductDescriptionDTO;
import com.salesmanager.catalog.model.product.Product;
import com.salesmanager.catalog.model.product.ProductCriteria;
import com.salesmanager.catalog.model.product.ProductList;
import com.salesmanager.common.business.exception.ServiceException;
import com.salesmanager.common.presentation.model.BreadcrumbItem;
import com.salesmanager.core.integration.language.LanguageDTO;
import com.salesmanager.core.integration.merchant.MerchantStoreDTO;
import com.salesmanager.core.integration.tax.TaxClassDTO;

import java.util.List;
import java.util.Locale;
import java.util.Set;

public interface ProductApi {

    Product getByCode(String productCode, LanguageDTO language);

    Product getById(Long id);

    Product getProductForLocale(long productId, LanguageDTO language, Locale locale) throws ServiceException;

    BreadcrumbItem getBreadcrumbItemForLocale(long productId, LanguageDTO languageDTO, Locale locale) throws ServiceException;

    ProductList listByStore(MerchantStoreDTO store, LanguageDTO language,
                            ProductCriteria criteria);

    DimensionDTO getProductDimension(Long productId);

    AvailabilityInformationDTO getProductAvailabilityInformation(Long productId);

    Long getProductTaxClassId(Long productId);

    Integer getProductMerchantStoreId(Long productId);

    Set<ProductAttributeDTO> getProductAttributes(Long productId);

    boolean isAvailable(Long productId);

    Set<ProductDescriptionDTO> getProductDescriptions(Long productId);
}
