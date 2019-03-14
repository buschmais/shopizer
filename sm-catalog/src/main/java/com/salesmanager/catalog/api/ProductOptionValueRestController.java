package com.salesmanager.catalog.api;

import com.salesmanager.catalog.api.dto.product.ProductOptionValueDTO;
import com.salesmanager.catalog.business.service.product.attribute.ProductOptionValueService;
import com.salesmanager.catalog.model.product.attribute.ProductOptionValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/product-option-value")
public class ProductOptionValueRestController {

    private final ProductOptionValueService productOptionValueService;

    @Autowired
    public ProductOptionValueRestController(ProductOptionValueService productOptionValueService) {
        this.productOptionValueService = productOptionValueService;
    }

    @RequestMapping(path = "/{valueId}", method = RequestMethod.GET)
    public ResponseEntity<?> getProductOption(@PathVariable("valueId") Long optionId) {
        ProductOptionValue productOptionValue = productOptionValueService.getById(optionId);
        if (productOptionValue != null) {
            ProductOptionValueDTO dto = productOptionValue.toDTO();
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.notFound().build();
    }
}
