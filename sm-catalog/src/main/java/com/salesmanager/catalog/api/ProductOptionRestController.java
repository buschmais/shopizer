package com.salesmanager.catalog.api;

import com.salesmanager.catalog.api.dto.product.ProductOptionDTO;
import com.salesmanager.catalog.business.service.product.attribute.ProductOptionService;
import com.salesmanager.catalog.model.product.attribute.ProductOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/product-option")
public class ProductOptionRestController {

    private final ProductOptionService productOptionService;

    @Autowired
    public ProductOptionRestController(ProductOptionService productOptionService) {
        this.productOptionService = productOptionService;
    }

    @RequestMapping(path = "/{optionId}", method = RequestMethod.GET)
    public ResponseEntity<?> getProductOption(@PathVariable("optionId") Long optionId) {
        ProductOption productOption = productOptionService.getById(optionId);
        if (productOption != null) {
            ProductOptionDTO dto = productOption.toDTO();
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.notFound().build();
    }
}
