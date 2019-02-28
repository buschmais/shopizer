package com.salesmanager.shop.api;

import com.salesmanager.common.business.exception.ServiceException;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.model.reference.language.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/core/language")
public class LanguageRestController {

    private final LanguageService languageService;

    @Autowired
    public LanguageRestController(LanguageService languageService) {
        this.languageService = languageService;
    }

    @RequestMapping(path = "/{languageCode}", method = RequestMethod.GET)
    public ResponseEntity<?> getMerchantStore(@PathVariable("languageCode") String languageCode) {
        try {
            Language language = this.languageService.getByCode(languageCode);
            if (language != null) {
                return ResponseEntity.ok(language.toDTO());
            }
        } catch (ServiceException e) {}

        return ResponseEntity.notFound().build();
    }

}
