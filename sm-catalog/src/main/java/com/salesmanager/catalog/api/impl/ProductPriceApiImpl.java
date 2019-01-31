package com.salesmanager.catalog.api.impl;

import com.salesmanager.catalog.api.ProductPriceApi;
import com.salesmanager.catalog.api.dto.product.FinalPriceDTO;
import com.salesmanager.catalog.api.dto.product.ProductPriceDTO;
import com.salesmanager.catalog.business.integration.core.service.MerchantStoreInfoService;
import com.salesmanager.catalog.business.service.product.ProductService;
import com.salesmanager.catalog.business.service.product.attribute.ProductAttributeService;
import com.salesmanager.catalog.business.util.ProductPriceUtils;
import com.salesmanager.catalog.model.integration.core.MerchantStoreInfo;
import com.salesmanager.catalog.model.product.Product;
import com.salesmanager.catalog.model.product.attribute.ProductAttribute;
import com.salesmanager.catalog.model.product.price.FinalPrice;
import com.salesmanager.catalog.model.product.price.ProductPrice;
import com.salesmanager.core.integration.merchant.MerchantStoreDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductPriceApiImpl implements ProductPriceApi {

    private ProductPriceUtils productPriceUtils;

    private MerchantStoreInfoService merchantStoreInfoService;

    private ProductService productService;

    private ProductAttributeService productAttributeService;

    @Autowired
    public ProductPriceApiImpl(ProductPriceUtils productPriceUtils, MerchantStoreInfoService merchantStoreInfoService, ProductService productService, ProductAttributeService productAttributeService) {
        this.productPriceUtils = productPriceUtils;
        this.merchantStoreInfoService = merchantStoreInfoService;
        this.productService = productService;
        this.productAttributeService = productAttributeService;
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
    public FinalPriceDTO getProductFinalPrice(Long productId, List<Long> attributeIds) {
        Product product = this.productService.getById(productId);
        FinalPrice finalPrice = null;
        if (attributeIds != null && !attributeIds.isEmpty()) {
            List<ProductAttribute> productAttributes = new ArrayList<>();
            for (Long attributeId : attributeIds) {
                productAttributes.add(this.productAttributeService.getById(attributeId));
            }
            finalPrice = this.productPriceUtils.getFinalProductPrice(product, productAttributes);
        } else {
            finalPrice = this.productPriceUtils.getFinalPrice(product);
        }
        return toDTO(finalPrice);
    }

    private FinalPriceDTO toDTO(FinalPrice finalPrice) {
        if (finalPrice != null) {
            ProductPrice productPrice = finalPrice.getProductPrice();
            ProductPriceDTO productPriceDTO = null;
            if (productPrice != null) {
                productPriceDTO = new ProductPriceDTO(
                        productPrice.getCode(),
                        productPrice.getProductPriceType().name(),
                        productPrice.isDefaultPrice(),
                        productPrice.getDescriptions() != null && productPrice.getDescriptions().size() > 0 ? productPrice.getDescriptions().iterator().next().getName() : null,
                        productPrice.getProductPriceSpecialAmount() != null ? productPrice.getProductPriceSpecialAmount().doubleValue() : null,
                        productPrice.getProductPriceSpecialStartDate(),
                        productPrice.getProductPriceSpecialEndDate()
                );
            }
            return new FinalPriceDTO(
                    finalPrice.isDiscounted(),
                    finalPrice.getFinalPrice().doubleValue(),
                    finalPrice.isDefaultPrice(),
                    finalPrice.getAdditionalPrices() != null ? finalPrice.getAdditionalPrices().stream().map(this::toDTO).collect(Collectors.toList()) : null,
                    productPriceDTO
            );
        }
        return null;
    }

}
