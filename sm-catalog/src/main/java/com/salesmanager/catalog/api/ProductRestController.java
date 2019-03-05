package com.salesmanager.catalog.api;

import com.salesmanager.catalog.api.dto.product.*;
import com.salesmanager.catalog.business.integration.core.service.LanguageInfoService;
import com.salesmanager.catalog.business.integration.core.service.MerchantStoreInfoService;
import com.salesmanager.catalog.business.service.product.ProductService;
import com.salesmanager.catalog.business.service.product.attribute.ProductAttributeService;
import com.salesmanager.catalog.business.service.product.file.DigitalProductService;
import com.salesmanager.catalog.business.service.product.relationship.ProductRelationshipService;
import com.salesmanager.catalog.business.util.ProductPriceUtils;
import com.salesmanager.catalog.model.integration.core.LanguageInfo;
import com.salesmanager.catalog.model.integration.core.MerchantStoreInfo;
import com.salesmanager.catalog.model.product.Product;
import com.salesmanager.catalog.model.product.attribute.ProductAttribute;
import com.salesmanager.catalog.model.product.availability.ProductAvailability;
import com.salesmanager.catalog.model.product.description.ProductDescription;
import com.salesmanager.catalog.model.product.file.DigitalProduct;
import com.salesmanager.catalog.model.product.image.ProductImage;
import com.salesmanager.catalog.model.product.price.FinalPrice;
import com.salesmanager.catalog.model.product.price.ProductPrice;
import com.salesmanager.catalog.model.product.relationship.ProductRelationship;
import com.salesmanager.catalog.presentation.util.CatalogImageFilePathUtils;
import com.salesmanager.common.business.exception.ServiceException;
import com.salesmanager.common.presentation.model.BreadcrumbItem;
import com.salesmanager.common.presentation.model.BreadcrumbItemType;
import com.salesmanager.common.presentation.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/catalog/product")
public class ProductRestController {

    private final ProductAttributeService productAttributeService;
    private final ProductPriceUtils productPriceUtils;
    private final ProductService productService;
    private final CatalogImageFilePathUtils catalogImageFilePathUtils;
    private final MerchantStoreInfoService merchantStoreInfoService;
    private final DigitalProductService digitalProductService;
    private final ProductRelationshipService productRelationshipService;
    private final LanguageInfoService languageInfoService;

    @Autowired
    public ProductRestController(ProductAttributeService productAttributeService, ProductPriceUtils productPriceUtils,
                                 ProductService productService, CatalogImageFilePathUtils catalogImageFilePathUtils,
                                 MerchantStoreInfoService merchantStoreInfoService, DigitalProductService digitalProductService,
                                 ProductRelationshipService productRelationshipService, LanguageInfoService languageInfoService) {
        this.productAttributeService = productAttributeService;
        this.productPriceUtils = productPriceUtils;
        this.productService = productService;
        this.catalogImageFilePathUtils = catalogImageFilePathUtils;
        this.merchantStoreInfoService = merchantStoreInfoService;
        this.digitalProductService = digitalProductService;
        this.productRelationshipService = productRelationshipService;
        this.languageInfoService = languageInfoService;
    }

    @RequestMapping(path = "/{productId}/dimension", method = RequestMethod.GET)
    public ResponseEntity<?> getDimensionForProduct(@PathVariable("productId") Long productId) {
        Product product = this.productService.getById(productId);
        if (product != null) {
            DimensionDTO dimensionDTO = new DimensionDTO(
                    product.getProductWidth() != null ? product.getProductWidth().doubleValue() : null,
                    product.getProductLength() != null ? product.getProductLength().doubleValue() : null,
                    product.getProductHeight() != null ? product.getProductHeight().doubleValue() : null,
                    product.getProductWeight() != null ? product.getProductWeight().doubleValue() : null
            );
            return ResponseEntity.ok(dimensionDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(path = "/{productId}/shipping", method = RequestMethod.GET)
    public ResponseEntity<?> getProductAvailabilityInformation(@PathVariable("productId") Long productId) {
        Product product = this.productService.getById(productId);
        if (product != null) {
            AvailabilityInformationDTO availabilityInformationDTO = new AvailabilityInformationDTO(
                    product.isAvailable(),
                    product.isProductShipeable(),
                    product.isProductVirtual());
            return ResponseEntity.ok(availabilityInformationDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(path = "/{productId}/descriptions", method = RequestMethod.GET)
    public ResponseEntity<?> getProductDescriptions(@PathVariable("productId") Long productId) {
        Product product = this.productService.getById(productId);
        if (product != null) {
            Set<ProductDescriptionDTO> descriptions = new HashSet<>();
            if (product.getDescriptions() != null) {
                for (ProductDescription productDescription : product.getDescriptions()) {
                    descriptions.add(new ProductDescriptionDTO(
                            productDescription.getId(),
                            productDescription.getName(),
                            productDescription.getSeUrl(),
                            productDescription.getLanguage() != null ? productDescription.getLanguage().getId().longValue() : null
                    ));
                }
            }
            return ResponseEntity.ok(descriptions);
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(path = "/{productId}/merchant", method = RequestMethod.GET)
    public ResponseEntity<?> getProductMerchant(@PathVariable("productId") Long productId) {
        Product product = this.productService.getById(productId);
        if (product != null) {
            return ResponseEntity.ok(product.getMerchantStore() != null ? product.getMerchantStore().getId() : null);
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(path = "/{productId}/tax-class", method = RequestMethod.GET)
    public ResponseEntity<?> getProductTaxClass(@PathVariable("productId") Long productId) {
        Product product = this.productService.getById(productId);
        if (product != null) {
            return ResponseEntity.ok(product.getTaxClass() != null ? product.getTaxClass().getId() : null);
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(path = "/{productId}/attributes", method = RequestMethod.GET)
    public ResponseEntity<?> getProductAttributes(@PathVariable("productId") Long productId) {
        Product product = this.productService.getById(productId);
        if (product != null) {
            if (product.getAttributes() != null) {
                Set<ProductAttributeDTO> attributes = new HashSet<>();
                for (ProductAttribute productAttribute : product.getAttributes()) {
                    attributes.add(new ProductAttributeDTO(
                            productAttribute.getId(),
                            productAttribute.getProductAttributePrice() != null ? productAttribute.getProductAttributePrice().doubleValue() : null,
                            productAttribute.getProductAttributeIsFree(),
                            productAttribute.getProductAttributeWeight() != null ? productAttribute.getProductAttributeWeight().doubleValue() : null,
                            productAttribute.getProductOption().getId(),
                            productAttribute.getProductOptionValue().getId()));
                }
                return ResponseEntity.ok(attributes);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(path = "/{productId}/default-image", method = RequestMethod.GET)
    public ResponseEntity<?> getProductDefaultImage(@PathVariable("productId") Long productId) {
        Product product = this.productService.getById(productId);
        if (product != null) {
            ProductImageDTO productImageDTO = null;
            if (product.getProductImage() != null) {
                ProductImage image = product.getProductImage();
                String imageUrl = catalogImageFilePathUtils.buildProductImageUtils(product.getMerchantStore(), product.getSku(), image.getProductImage());
                String contextPath = catalogImageFilePathUtils.getContextPath();
                if (contextPath != null) {
                    imageUrl = contextPath + imageUrl;
                }
                productImageDTO = new ProductImageDTO(image.getId(),
                        image.getProductImage(),
                        image.isDefaultImage(),
                        imageUrl);
            }
            return ResponseEntity.ok(productImageDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(path = "/{productId}/final-price", method = RequestMethod.GET)
    public FinalPriceDTO getProductFinalPrice(@PathVariable("productId") Long productId, @RequestParam(value = "attributeIds", required = false) List<Long> attributeIds) {
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

    @RequestMapping(path = "/{productId}/availability", method = RequestMethod.GET)
    public ResponseEntity<?> getProductAvailabilityForRegion(@PathVariable("productId") Long productId, @RequestParam("region") String region) {
        Product product = this.productService.getById(productId);
        if (product != null && product.getAvailabilities() != null) {
            for (ProductAvailability productAvailability : product.getAvailabilities()) {
                if (productAvailability.getRegion().equals(region)) {
                    return ResponseEntity.ok(productAvailability.getProductQuantity());
                }
            }
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(path = "/{productId}/available", method = RequestMethod.GET)
    public ResponseEntity<?> getProductAvailability(@PathVariable("productId") Long productId) {
        Product product = this.productService.getById(productId);
        if (product != null) {
            boolean available = true;
            Set<ProductAvailability> availabilities = product.getAvailabilities();
            if (availabilities == null) {
                available = false;
            } else if (!product.isAvailable()) {
                available = false;
            } else if (!DateUtil.dateBeforeEqualsDate(product.getDateAvailable(), new Date())) {
                available = false;
            } else {
                for (ProductAvailability availability : availabilities) {
                    if (availability.getProductQuantity() == null || availability.getProductQuantity() == 0) {
                        available = false;
                    }
                }
            }
            return ResponseEntity.ok(available);
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(path = "/{productId}/digital/file-name", method = RequestMethod.GET)
    public ResponseEntity<?> getDigitalProductNameByProduct(@PathVariable("productId") Long productId, @RequestParam("storeCode") String storeCode) {
        MerchantStoreInfo storeInfo = this.merchantStoreInfoService.findbyCode(storeCode);
        Product product = this.productService.getById(productId);
        DigitalProduct digitalProduct = null;
        try {
            digitalProduct = digitalProductService.getByProduct(storeInfo, product);
        } catch (ServiceException e) {
            return ResponseEntity.notFound().build();
        }
        if (digitalProduct != null) {
            return ResponseEntity.ok(digitalProduct.getProductFileName());
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(path = "/store/{storeCode}/groups", method = RequestMethod.GET)
    public ResponseEntity<?> getProductGroups(@PathVariable("storeCode") String storeCode) {
        MerchantStoreInfo storeInfo = this.merchantStoreInfoService.findbyCode(storeCode);
        List<ProductRelationship> groups = productRelationshipService.getGroups(storeInfo);
        if (groups != null) {
            return ResponseEntity.ok(groups.stream().map(ProductRelationship::getCode).collect(Collectors.toList()));
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(path = "/{productId}/breadcrumb", method = RequestMethod.GET)
    public ResponseEntity<?> getBreadcrumbItemForLocale(@PathVariable("productId") long productId, @RequestParam("languageCode") String languageCode, @RequestParam("locale") Locale locale) throws ServiceException {
        LanguageInfo languageInfo = this.languageInfoService.findbyCode(languageCode);
        Product product = this.productService.getProductForLocale(productId, languageInfo, locale);
        if (product != null) {
            BreadcrumbItem productItem = new BreadcrumbItem();
            productItem.setId(product.getId());
            productItem.setItemType(BreadcrumbItemType.PRODUCT);
            productItem.setLabel(product.getProductDescription().getName());
            productItem.setUrl(product.getProductDescription().getSeUrl());
            ResponseEntity.ok(productItem);
        }
        return ResponseEntity.notFound().build();
    }
}
