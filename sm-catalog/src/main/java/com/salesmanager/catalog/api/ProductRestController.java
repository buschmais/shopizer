package com.salesmanager.catalog.api;

import com.salesmanager.catalog.api.dto.product.AvailabilityInformationDTO;
import com.salesmanager.catalog.api.dto.product.DimensionDTO;
import com.salesmanager.catalog.business.service.product.ProductService;
import com.salesmanager.catalog.model.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/catalog/product")
public class ProductRestController {

    private final ProductService productService;

    @Autowired
    public ProductRestController(ProductService productService) {
        this.productService = productService;
    }

    @RequestMapping(path = "/{productId}/dimension", method = RequestMethod.GET)
    public ResponseEntity<?> getDimensionForProduct(@PathVariable("productId") Long productId) {
        Product product = this.productService.getById(productId);
        if (product != null) {
            DimensionDTO dimensionDTO =  new DimensionDTO(
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
}
