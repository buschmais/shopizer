package com.salesmanager.catalog.business.integration.core.adapter;

import com.salesmanager.catalog.business.integration.core.service.LanguageInfoService;
import com.salesmanager.catalog.model.integration.core.LanguageInfo;
import com.salesmanager.catalog.business.integration.core.dto.LanguageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class LanguageInfoAdapter {

    private final LanguageInfoService languageInfoService;
    private final RestTemplate coreRestTemplate;

    @Autowired
    public LanguageInfoAdapter(LanguageInfoService languageInfoService, RestTemplate coreRestTemplate) {
        this.languageInfoService = languageInfoService;
        this.coreRestTemplate = coreRestTemplate;
    }

    public LanguageInfo findOrRequet(String languageCode) {
        LanguageInfo languageInfo = this.languageInfoService.findbyCode(languageCode);
        if (languageInfo == null) {
            Map<String, Object> params = new HashMap<>();
            params.put("languageCode", languageCode);
            ResponseEntity<LanguageDTO> response = coreRestTemplate.exchange("/core/language/{languageCode}",
                    HttpMethod.GET, null, new ParameterizedTypeReference<LanguageDTO>() {}, params);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                languageInfo = createOrUpdateLanguageInfo(response.getBody());
            }
        }
        return languageInfo;
    }

    public LanguageInfo createOrUpdateLanguageInfo(LanguageDTO languageDTO) {
        LanguageInfo language = new LanguageInfo(
                languageDTO.getId(),
                languageDTO.getCode()
        );
        return this.languageInfoService.save(language);
    }
}
