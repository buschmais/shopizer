package com.salesmanager.core.business.services.catalog;

import com.salesmanager.catalog.api.ProductApi;
import com.salesmanager.catalog.api.dto.product.AvailabilityInformationDTO;
import com.salesmanager.catalog.api.dto.product.DimensionDTO;
import com.salesmanager.core.business.repositories.catalog.ProductInfoRepository;
import com.salesmanager.core.business.repositories.merchant.MerchantRepository;
import com.salesmanager.core.business.repositories.tax.TaxClassRepository;
import com.salesmanager.core.model.catalog.ProductInfo;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.tax.taxclass.TaxClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ProductInfoService {

    private final ProductApi productApi;

    private final ProductInfoRepository productInfoRepository;

    private final MerchantRepository merchantRepository;
    private final TaxClassRepository taxClassRepository;


    @Autowired
    public ProductInfoService(ProductApi productApi, ProductInfoRepository productInfoRepository, MerchantRepository merchantRepository, TaxClassRepository taxClassRepository) {
        this.productApi = productApi;
        this.productInfoRepository = productInfoRepository;
        this.merchantRepository = merchantRepository;
        this.taxClassRepository = taxClassRepository;
    }

    public ProductInfo save(ProductInfo productInfo) {
        return this.productInfoRepository.save(productInfo);
    }

    public void delete(Long productId) {
        this.productInfoRepository.delete(productId);
    }

    public ProductInfo.Dimension enrichDimensionsForProduct(Long productId) {
        DimensionDTO dimensionDTO = productApi.getProductDimension(productId);
        if (dimensionDTO != null) {
            return new ProductInfo.Dimension(dimensionDTO.getWidth(), dimensionDTO.getLength(), dimensionDTO.getHeight(), dimensionDTO.getWeight());
        }
        return null;
    }

    public ProductInfo.AvailabilityInformation enrichAvailabilityInfoForProduct(Long productId) {
        AvailabilityInformationDTO availabilityDTO = productApi.getProductAvailabilityInformation(productId);
        if (availabilityDTO != null) {
            return new ProductInfo.AvailabilityInformation(availabilityDTO.isAvailable(), availabilityDTO.isShippable(), availabilityDTO.isVirtual());
        }
        return null;
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
}
