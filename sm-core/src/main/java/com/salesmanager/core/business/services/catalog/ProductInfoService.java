package com.salesmanager.core.business.services.catalog;

import com.salesmanager.catalog.api.ProductApi;
import com.salesmanager.catalog.api.dto.product.AvailabilityInformationDTO;
import com.salesmanager.catalog.api.dto.product.DimensionDTO;
import com.salesmanager.catalog.api.dto.product.ProductAttributeDTO;
import com.salesmanager.core.business.repositories.catalog.ProductInfoRepository;
import com.salesmanager.core.business.repositories.catalog.ProductOptionInfoRepository;
import com.salesmanager.core.business.repositories.catalog.ProductOptionValueInfoRepository;
import com.salesmanager.core.business.repositories.merchant.MerchantRepository;
import com.salesmanager.core.business.repositories.tax.TaxClassRepository;
import com.salesmanager.core.model.catalog.ProductAttributeInfo;
import com.salesmanager.core.model.catalog.ProductInfo;
import com.salesmanager.core.model.catalog.ProductOptionInfo;
import com.salesmanager.core.model.catalog.ProductOptionValueInfo;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.tax.taxclass.TaxClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;


@Component
public class ProductInfoService {

    private final ProductApi productApi;

    private final ProductInfoRepository productInfoRepository;

    private final MerchantRepository merchantRepository;
    private final TaxClassRepository taxClassRepository;
    private final ProductOptionInfoRepository productOptionInfoRepository;
    private final ProductOptionValueInfoRepository productOptionValueInfoRepository;


    @Autowired
    public ProductInfoService(ProductApi productApi, ProductInfoRepository productInfoRepository, MerchantRepository merchantRepository, TaxClassRepository taxClassRepository, ProductOptionInfoRepository productOptionInfoRepository, ProductOptionValueInfoRepository productOptionValueInfoRepository) {
        this.productApi = productApi;
        this.productInfoRepository = productInfoRepository;
        this.merchantRepository = merchantRepository;
        this.taxClassRepository = taxClassRepository;
        this.productOptionInfoRepository = productOptionInfoRepository;
        this.productOptionValueInfoRepository = productOptionValueInfoRepository;
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

    public Set<ProductAttributeInfo> enrichProductAttributesForProduct(Long productId) {
        Set<ProductAttributeDTO> productAttributeDTOs = productApi.getProductAttributes(productId);
        Set<ProductAttributeInfo> productAttributeInfos = new HashSet<>();
        if (productAttributeDTOs != null) {
            for (ProductAttributeDTO dto : productAttributeDTOs) {
                ProductOptionInfo optionInfo = productOptionInfoRepository.findOne(dto.getProductOptionId());
                ProductOptionValueInfo valueInfo = productOptionValueInfoRepository.findOne(dto.getProductOptionValueId());
                productAttributeInfos.add(new ProductAttributeInfo(dto.getId(), dto.getPrice(), dto.getFree(), dto.getWeight(), optionInfo, valueInfo));
            }
        }
        return productAttributeInfos;
    }
}
