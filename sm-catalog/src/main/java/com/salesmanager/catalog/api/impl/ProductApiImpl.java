package com.salesmanager.catalog.api.impl;

import com.salesmanager.catalog.api.ProductApi;
import com.salesmanager.catalog.api.dto.product.AvailabilityInformationDTO;
import com.salesmanager.catalog.api.dto.product.DimensionDTO;
import com.salesmanager.catalog.api.dto.product.ProductAttributeDTO;
import com.salesmanager.catalog.business.integration.core.service.LanguageInfoService;
import com.salesmanager.catalog.business.integration.core.service.MerchantStoreInfoService;
import com.salesmanager.catalog.business.service.product.ProductService;
import com.salesmanager.catalog.model.integration.core.LanguageInfo;
import com.salesmanager.catalog.model.integration.core.MerchantStoreInfo;
import com.salesmanager.catalog.model.product.Product;
import com.salesmanager.catalog.model.product.ProductCriteria;
import com.salesmanager.catalog.model.product.ProductList;
import com.salesmanager.catalog.model.product.attribute.ProductAttribute;
import com.salesmanager.catalog.model.product.availability.ProductAvailability;
import com.salesmanager.common.business.exception.ServiceException;
import com.salesmanager.common.presentation.util.DateUtil;
import com.salesmanager.core.integration.language.LanguageDTO;
import com.salesmanager.core.integration.merchant.MerchantStoreDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ProductApiImpl implements ProductApi {

    private ProductService productService;

    private LanguageInfoService languageInfoService;

    private MerchantStoreInfoService merchantStoreInfoService;

    @Autowired
    public ProductApiImpl(ProductService productService, LanguageInfoService languageInfoService, MerchantStoreInfoService merchantStoreInfoService) {
        this.productService = productService;
        this.languageInfoService = languageInfoService;
        this.merchantStoreInfoService = merchantStoreInfoService;
    }

    @Override
    public Product getByCode(String productCode, LanguageDTO language) {
        LanguageInfo languageInfo = this.languageInfoService.findbyCode(language.getCode());
        return this.productService.getByCode(productCode, languageInfo);
    }

    @Override
    public Product getById(Long id) {
        return this.productService.getById(id);
    }

    @Override
    public Product getProductForLocale(long productId, LanguageDTO language, Locale locale) throws ServiceException {
        LanguageInfo languageInfo = this.languageInfoService.findbyCode(language.getCode());
        return this.productService.getProductForLocale(productId, languageInfo, locale);
    }

    @Override
    public ProductList listByStore(MerchantStoreDTO store, LanguageDTO language, ProductCriteria criteria) {
        LanguageInfo languageInfo = this.languageInfoService.findbyCode(language.getCode());
        MerchantStoreInfo merchantStoreInfo = this.merchantStoreInfoService.findbyCode(store.getCode());
        return this.productService.listByStore(merchantStoreInfo, languageInfo, criteria);
    }

    @Override
    public DimensionDTO getProductDimension(Long productId) {
        Product product = this.productService.getById(productId);
        return new DimensionDTO(
                product != null && product.getProductWidth() != null ? product.getProductWidth().doubleValue() : null,
                product != null && product.getProductLength() != null ? product.getProductLength().doubleValue() : null,
                product != null && product.getProductHeight() != null ? product.getProductHeight().doubleValue() : null,
                product != null && product.getProductWeight() != null ? product.getProductWeight().doubleValue() : null
        );
    }

    @Override
    public AvailabilityInformationDTO getProductAvailabilityInformation(Long productId) {
        Product product = this.productService.getById(productId);
        return new AvailabilityInformationDTO(
                product != null && product.isAvailable(),
                product != null && product.isProductShipeable(),
                product != null && product.isProductVirtual());
    }

    @Override
    public Long getProductTaxClassId(Long productId) {
        Product product = this.productService.getById(productId);
        return product != null && product.getTaxClass() != null ? product.getTaxClass().getId() : null;
    }

    @Override
    public Integer getProductMerchantStoreId(Long productId) {
        Product product = this.productService.getById(productId);
        return product != null && product.getMerchantStore() != null ? product.getMerchantStore().getId() : null;
    }

    @Override
    public Set<ProductAttributeDTO> getProductAttributes(Long productId) {
        Product product = this.productService.getById(productId);
        Set<ProductAttributeDTO> attributes = new HashSet<>();
        if (product != null && product.getAttributes() != null) {
            for (ProductAttribute productAttribute : product.getAttributes()) {
                attributes.add(new ProductAttributeDTO(
                        productAttribute.getId(),
                        productAttribute.getProductAttributePrice() != null ? productAttribute.getProductAttributePrice().doubleValue() : null,
                        productAttribute.getProductAttributeIsFree(),
                        productAttribute.getProductAttributeWeight() != null ? productAttribute.getProductAttributeWeight().doubleValue() : null,
                        productAttribute.getProductOption().getId(),
                        productAttribute.getProductOptionValue().getId()));
            }
        }
        return attributes;
    }

    @Override
    public boolean isAvailable(Long productId) {
        Product product = this.productService.getById(productId);
        Set<ProductAvailability> availabilities = product.getAvailabilities();
        if(availabilities == null) {
            return false;
        }

        for(ProductAvailability availability : availabilities) {
            if(availability.getProductQuantity() == null || availability.getProductQuantity().intValue() ==0) {
                return false;
            }
        }

        if(!product.isAvailable()) {
            return false;
        }

        if(!DateUtil.dateBeforeEqualsDate(product.getDateAvailable(), new Date())) {
            return false;
        }
        return true;
    }
}
