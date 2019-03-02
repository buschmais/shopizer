package com.salesmanager.core.business.services.catalog;

import com.salesmanager.catalog.api.ProductApi;
import com.salesmanager.catalog.api.ProductPriceApi;
import com.salesmanager.catalog.api.dto.product.*;
import com.salesmanager.core.business.repositories.catalog.ProductInfoRepository;
import com.salesmanager.core.business.repositories.merchant.MerchantRepository;
import com.salesmanager.core.business.repositories.tax.TaxClassRepository;
import com.salesmanager.core.model.catalog.*;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.tax.taxclass.TaxClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class ProductInfoService {

    private final ProductApi productApi;

    private final ProductInfoRepository productInfoRepository;

    private final MerchantRepository merchantRepository;
    private final TaxClassRepository taxClassRepository;
    private final ProductPriceApi productPriceApi;

    @Autowired
    public ProductInfoService(ProductApi productApi, ProductInfoRepository productInfoRepository, MerchantRepository merchantRepository, TaxClassRepository taxClassRepository, ProductPriceApi productPriceApi) {
        this.productApi = productApi;
        this.productInfoRepository = productInfoRepository;
        this.merchantRepository = merchantRepository;
        this.taxClassRepository = taxClassRepository;
        this.productPriceApi = productPriceApi;
    }

    public ProductInfo save(ProductInfo productInfo) {
        return this.productInfoRepository.save(productInfo);
    }

    public void delete(Long productId) {
        this.productInfoRepository.delete(productId);
    }

    public List<ProductInfo> listByTaxClass(Long taxClassId) {
        return this.productInfoRepository.listByTaxClass(taxClassId);
    }

    public TaxClass enrichTaxClassForProduct(Long productId) {
        Long taxClassId = productApi.getProductTaxClassId(productId);
        if (taxClassId != null) {
            return this.taxClassRepository.findOne(taxClassId);
        }
        return null;
    }

    public MerchantStore enrichMerchantForProduct(Long productId) {
        Integer merchantStoreId = productApi.getProductMerchantStoreId(productId);
        if (merchantStoreId != null) {
            return this.merchantRepository.findOne(merchantStoreId);
        }
        return null;
    }

    public Set<ProductDescriptionInfo> enrichProductDescriptionsForProduct(Long productId) {
        Set<ProductDescriptionDTO> productDescriptionDTOs = productApi.getProductDescriptions(productId);
        Set<ProductDescriptionInfo> productDescriptionInfos = new HashSet<>();
        if (productDescriptionDTOs != null) {
            for (ProductDescriptionDTO dto : productDescriptionDTOs) {
                productDescriptionInfos.add(new ProductDescriptionInfo(dto.getId(), dto.getName(), dto.getSeUrl(), dto.getLanguageId()));
            }
        }
        return productDescriptionInfos;
    }

    public ProductInfo getProductForLocale(String sku, Language language) {
        ProductInfo productInfo = this.productInfoRepository.findOneBySku(sku);
        return productInfo != null ? getProductForLocale(productInfo, language) : null;
    }

    public ProductInfo getProductForLocale(ProductInfo productInfo, Language language) {
        ProductDescriptionInfo productDescription = null;
        for (ProductDescriptionInfo description : productInfo.getDescriptions()) {
            if (description.getLanguageId() == language.getId().longValue()) {
                productDescription = description;
            }
        }
        ProductInfo product = new ProductInfo(
                productInfo.getId(),
                productInfo.getSku(),
                productDescription.getName(),
                productInfo.getManufacturerCode()
        );
        product.setMerchantStore(productInfo.getMerchantStore());
        product.setTaxClass(productInfo.getTaxClass());
        product.setDimension(productInfo.getDimension());
        product.setAvailabilityInformation(productInfo.getAvailabilityInformation());
        product.getDescriptions().add(productDescription);
        product.getAttributes().addAll(productInfo.getAttributes());

        return product;
    }

    public ProductImageInfo getDefaultImage(Long productId) {
        ProductImageDTO productImageDTO = productApi.getDefaultImage(productId);
        if (productImageDTO != null) {
            return new ProductImageInfo(productImageDTO.getId(), productImageDTO.getImageName(), productImageDTO.isDefaultImage(), productImageDTO.getImageUrl());
        }
        return null;
    }

    public FinalPriceInfo getProductFinalPrice(Long productId, List<ProductAttributeInfo> productAttributes) {
        List<Long> productAttributeIds = productAttributes != null ? productAttributes.stream().map(ProductAttributeInfo::getId).collect(Collectors.toList()) : null;
        FinalPriceDTO finalPriceDTO = productPriceApi.getProductFinalPrice(productId, productAttributeIds);
        if (finalPriceDTO != null) {
            return toFinalPriceInfo(finalPriceDTO);
        }
        return null;
    }

    private FinalPriceInfo toFinalPriceInfo(FinalPriceDTO finalPriceDTO) {
        List<FinalPriceInfo> additionalPrices = finalPriceDTO.getAdditionalPrices() != null ?
                finalPriceDTO.getAdditionalPrices().stream().map(this::toFinalPriceInfo).collect(Collectors.toList()) :
                new ArrayList<>();
        ProductPriceDTO productPriceDTO = finalPriceDTO.getProductPrice();
        ProductPriceInfo productPriceInfo = productPriceDTO != null ?
                new ProductPriceInfo(
                        productPriceDTO.getCode(), productPriceDTO.getProductPriceType(),
                        productPriceDTO.getDefaultPrice(), productPriceDTO.getName(),
                        productPriceDTO.getProductPriceSpecialAmount(), productPriceDTO.getProductPriceSpecialStartDate(),
                        productPriceDTO.getProductPriceSpecialEndDate()
                ) : null;
        return new FinalPriceInfo(
                finalPriceDTO.getDiscounted(),
                finalPriceDTO.getFinalPrice(),
                finalPriceDTO.getDefaultPrice(),
                additionalPrices,
                productPriceInfo
        );
    }
}
