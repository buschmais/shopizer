package com.salesmanager.catalog.api.impl;

import com.salesmanager.catalog.api.ProductPriceApi;
import com.salesmanager.catalog.business.integration.core.service.MerchantStoreInfoService;
import com.salesmanager.catalog.business.service.product.PricingService;
import com.salesmanager.catalog.business.service.product.ProductService;
import com.salesmanager.catalog.business.service.product.attribute.ProductAttributeService;
import com.salesmanager.catalog.business.util.ProductPriceUtils;
import com.salesmanager.catalog.model.integration.core.MerchantStoreInfo;
import com.salesmanager.catalog.model.product.Product;
import com.salesmanager.catalog.model.product.attribute.ProductAttribute;
import com.salesmanager.catalog.model.product.price.FinalPrice;
import com.salesmanager.common.business.exception.ServiceException;
import com.salesmanager.core.integration.merchant.MerchantStoreDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductPriceApiImpl implements ProductPriceApi {

    private ProductPriceUtils productPriceUtils;

    private MerchantStoreInfoService merchantStoreInfoService;

    private PricingService pricingService;

    private ProductService productService;

    private ProductAttributeService productAttributeService;

    @Autowired
    public ProductPriceApiImpl(ProductPriceUtils productPriceUtils, MerchantStoreInfoService merchantStoreInfoService, PricingService pricingService, ProductService productService, ProductAttributeService productAttributeService) {
        this.productPriceUtils = productPriceUtils;
        this.merchantStoreInfoService = merchantStoreInfoService;
        this.pricingService = pricingService;
        this.productService = productService;
        this.productAttributeService = productAttributeService;
    }

    @Override
    public String getAdminFormattedAmountWithCurrency(MerchantStoreDTO merchantStoreDTO, BigDecimal amount) throws Exception {
        MerchantStoreInfo storeInfo = this.merchantStoreInfoService.findbyCode(merchantStoreDTO.getCode());
        return this.productPriceUtils.getAdminFormatedAmountWithCurrency(storeInfo, amount);
    }

    @Override
    public String getAdminFormattedAmount(MerchantStoreDTO merchantStoreDTO, BigDecimal amount) throws Exception {
        MerchantStoreInfo storeInfo = this.merchantStoreInfoService.findbyCode(merchantStoreDTO.getCode());
        return this.productPriceUtils.getAdminFormatedAmount(storeInfo, amount);
    }

    @Override
    public String getStoreFormattedAmountWithCurrency(MerchantStoreDTO merchantStoreDTO, BigDecimal amount) throws Exception {
        MerchantStoreInfo storeInfo = this.merchantStoreInfoService.findbyCode(merchantStoreDTO.getCode());
        return this.productPriceUtils.getStoreFormatedAmountWithCurrency(storeInfo, amount);
    }

    @Override
    public BigDecimal getAmount(String formattedAmount) throws Exception {
        return this.productPriceUtils.getAmount(formattedAmount);
    }

    @Override
    public FinalPrice getFinalProductPrice(Long productId, List<Long> productAttributeIds) throws ServiceException {
        Product product = this.productService.getById(productId);
        List<ProductAttribute> attributes = this.productAttributeService.getByAttributeIds(product.getMerchantStore(), product, productAttributeIds);
        return this.productPriceUtils.getFinalProductPrice(product, attributes);
    }

    @Override
    public FinalPrice calculateProductPrice(Long productId) throws ServiceException {
        Product product = this.productService.getById(productId);
        return pricingService.calculateProductPrice(product);
    }

}
