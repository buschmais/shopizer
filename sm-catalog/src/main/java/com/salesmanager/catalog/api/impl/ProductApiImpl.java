package com.salesmanager.catalog.api.impl;

import com.salesmanager.catalog.api.ProductApi;
import com.salesmanager.catalog.api.dto.product.AvailabilityInformationDTO;
import com.salesmanager.catalog.api.dto.product.DimensionDTO;
import com.salesmanager.catalog.business.integration.core.service.LanguageInfoService;
import com.salesmanager.catalog.business.integration.core.service.MerchantStoreInfoService;
import com.salesmanager.catalog.business.integration.core.service.TaxClassInfoService;
import com.salesmanager.catalog.business.service.product.ProductService;
import com.salesmanager.catalog.model.integration.core.LanguageInfo;
import com.salesmanager.catalog.model.integration.core.MerchantStoreInfo;
import com.salesmanager.catalog.model.integration.core.TaxClassInfo;
import com.salesmanager.catalog.model.product.Product;
import com.salesmanager.catalog.model.product.ProductCriteria;
import com.salesmanager.catalog.model.product.ProductList;
import com.salesmanager.common.business.exception.ServiceException;
import com.salesmanager.core.integration.language.LanguageDTO;
import com.salesmanager.core.integration.merchant.MerchantStoreDTO;
import com.salesmanager.core.integration.tax.TaxClassDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class ProductApiImpl implements ProductApi {

    private ProductService productService;

    private LanguageInfoService languageInfoService;

    private TaxClassInfoService taxClassInfoService;

    private MerchantStoreInfoService merchantStoreInfoService;

    @Autowired
    public ProductApiImpl(ProductService productService, LanguageInfoService languageInfoService, TaxClassInfoService taxClassInfoService, MerchantStoreInfoService merchantStoreInfoService) {
        this.productService = productService;
        this.languageInfoService = languageInfoService;
        this.taxClassInfoService = taxClassInfoService;
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
    public List<Product> listByTaxClass(TaxClassDTO taxClass) {
        TaxClassInfo taxClassInfo = this.taxClassInfoService.findById(taxClass.getId());
        return this.productService.listByTaxClass(taxClassInfo);
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
}
