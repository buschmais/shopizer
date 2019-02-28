package com.salesmanager.shop.api;

import com.salesmanager.common.business.exception.ServiceException;
import com.salesmanager.core.business.services.merchant.MerchantStoreService;
import com.salesmanager.core.model.merchant.MerchantStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/core/merchant")
public class MerchantStoreRestController {

    private final MerchantStoreService merchantStoreService;

    @Autowired
    public MerchantStoreRestController(MerchantStoreService merchantStoreService) {
        this.merchantStoreService = merchantStoreService;
    }

    @RequestMapping(path = "/{storeCode}", method = RequestMethod.GET)
    public ResponseEntity<?> getMerchantStore(@PathVariable("storeCode") String storeCode) {
        try {
            MerchantStore merchantStore = this.merchantStoreService.getByCode(storeCode);
            if (merchantStore != null) {
                return ResponseEntity.ok(merchantStore.toDTO());
            }
        } catch (ServiceException e) {}

        return ResponseEntity.notFound().build();
    }
}
