package com.salesmanager.catalog.api.impl;

import com.salesmanager.catalog.api.ProductApi;
import com.salesmanager.catalog.api.dto.product.*;
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
import com.salesmanager.catalog.model.product.description.ProductDescription;
import com.salesmanager.catalog.model.product.image.ProductImage;
import com.salesmanager.catalog.presentation.util.CatalogImageFilePathUtils;
import com.salesmanager.common.business.exception.ServiceException;
import com.salesmanager.common.presentation.model.BreadcrumbItem;
import com.salesmanager.common.presentation.model.BreadcrumbItemType;
import com.salesmanager.common.presentation.util.DateUtil;
import com.salesmanager.core.integration.language.LanguageDTO;
import com.salesmanager.core.integration.merchant.MerchantStoreDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Component
public class ProductApiImpl implements ProductApi {

    private ProductService productService;

    private LanguageInfoService languageInfoService;

    private MerchantStoreInfoService merchantStoreInfoService;

    private CatalogImageFilePathUtils catalogImageFilePathUtils;

    @Autowired
    public ProductApiImpl(ProductService productService, LanguageInfoService languageInfoService, MerchantStoreInfoService merchantStoreInfoService, CatalogImageFilePathUtils catalogImageFilePathUtils) {
        this.productService = productService;
        this.languageInfoService = languageInfoService;
        this.merchantStoreInfoService = merchantStoreInfoService;
        this.catalogImageFilePathUtils = catalogImageFilePathUtils;
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
    public BreadcrumbItem getBreadcrumbItemForLocale(long productId, LanguageDTO languageDTO, Locale locale) throws ServiceException {
        LanguageInfo languageInfo = this.languageInfoService.findbyCode(languageDTO.getCode());
        Product product = this.productService.getProductForLocale(productId, languageInfo, locale);
        if(product!=null) {
            BreadcrumbItem productItem = new  BreadcrumbItem();
            productItem.setId(product.getId());
            productItem.setItemType(BreadcrumbItemType.PRODUCT);
            productItem.setLabel(product.getProductDescription().getName());
            productItem.setUrl(product.getProductDescription().getSeUrl());
            return productItem;
        }
        return null;
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

    @Override
    public Set<ProductDescriptionDTO> getProductDescriptions(Long productId) {
        Product product = this.productService.getById(productId);
        Set<ProductDescriptionDTO> descriptions= new HashSet<>();
        if (product != null && product.getDescriptions() != null) {
            for (ProductDescription productDescription : product.getDescriptions()) {
                descriptions.add(new ProductDescriptionDTO(
                        productDescription.getId(),
                        productDescription.getName(),
                        productDescription.getSeUrl(),
                        productDescription.getLanguage() != null ? productDescription.getLanguage().getId().longValue() : null
                ));
            }
        }
        return descriptions;
    }

    @Override
    public ProductImageDTO getDefaultImage(Long productId) {
        Product product = this.productService.getById(productId);
        if (product != null && product.getProductImage() != null) {
            ProductImage image = product.getProductImage();
            String imageUrl = catalogImageFilePathUtils.buildProductImageUtils(product.getMerchantStore(), product.getSku(), image.getProductImage());
            String contextPath = catalogImageFilePathUtils.getContextPath();
            if (contextPath != null) {
                imageUrl = contextPath + imageUrl;
            }
            return new ProductImageDTO(image.getId(),
                    image.getProductImage(),
                    image.isDefaultImage(),
                    imageUrl);
        }
        return null;
    }

    @Override
    public Integer getAvailabilityForRegion(Long productId, String region) {
        Product product = this.productService.getById(productId);
        if (product != null && product.getAvailabilities() != null) {
            for (ProductAvailability productAvailability : product.getAvailabilities()) {
                if (productAvailability.getRegion().equals(region)) {
                    return productAvailability.getProductQuantity();
                }
            }
        }
        return 0;
    }
}
