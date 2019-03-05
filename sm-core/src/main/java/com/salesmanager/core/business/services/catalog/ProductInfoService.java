package com.salesmanager.core.business.services.catalog;

import com.salesmanager.common.business.exception.ServiceException;
import com.salesmanager.core.business.integration.catalog.adapter.ProductInfoAdapter;
import com.salesmanager.core.business.repositories.catalog.ProductInfoRepository;
import com.salesmanager.core.model.catalog.*;
import com.salesmanager.core.model.reference.language.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class ProductInfoService {

    private final ProductInfoAdapter productInfoAdapter;

    private final ProductInfoRepository productInfoRepository;

    @Autowired
    public ProductInfoService(ProductInfoAdapter productInfoAdapter, ProductInfoRepository productInfoRepository) {
        this.productInfoAdapter = productInfoAdapter;
        this.productInfoRepository = productInfoRepository;
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
        return productInfoAdapter.requestProductImage(productId);
    }

    public FinalPriceInfo getProductFinalPrice(Long productId, List<ProductAttributeInfo> productAttributes) throws ServiceException {
        return this.productInfoAdapter.requestProductFinalPrice(productId, productAttributes);
    }


}
