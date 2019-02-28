package com.salesmanager.catalog.business.integration.core.adapter;

import com.salesmanager.catalog.business.integration.core.service.LanguageInfoService;
import com.salesmanager.catalog.business.integration.core.service.MerchantStoreInfoService;
import com.salesmanager.catalog.model.integration.core.LanguageInfo;
import com.salesmanager.catalog.model.integration.core.MerchantStoreInfo;
import com.salesmanager.core.integration.merchant.MerchantStoreDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MerchantStoreInfoAdapter {

    private final LanguageInfoService languageInfoService;
    private final MerchantStoreInfoService merchantStoreInfoService;
    private final RestTemplate coreRestTemplate;

    @Autowired
    public MerchantStoreInfoAdapter(LanguageInfoService languageInfoService, MerchantStoreInfoService merchantStoreInfoService, RestTemplate coreRestTemplate) {
        this.languageInfoService = languageInfoService;
        this.merchantStoreInfoService = merchantStoreInfoService;
        this.coreRestTemplate = coreRestTemplate;
    }

    public MerchantStoreInfo findOrRequest(String storeCode) {
        MerchantStoreInfo merchantStoreInfo = this.merchantStoreInfoService.findbyCode(storeCode);
        if (merchantStoreInfo == null) {
            Map<String, Object> params = new HashMap<>();
            params.put("storeCode", storeCode);
            ResponseEntity<MerchantStoreDTO> response = coreRestTemplate.exchange("/core/merchant/{storeCode}",
                    HttpMethod.GET, null, new ParameterizedTypeReference<MerchantStoreDTO>() {}, params);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                merchantStoreInfo =  createOrUpdateMerchantStoreInfo(response.getBody());
            }
        }
        return merchantStoreInfo;
    }

    public MerchantStoreInfo createOrUpdateMerchantStoreInfo(MerchantStoreDTO storeDTO) {
        MerchantStoreInfo storeInfo = new MerchantStoreInfo(
                storeDTO.getId(),
                storeDTO.getCode(),
                storeDTO.getCurrency(),
                storeDTO.getDefaultLanguage(),
                storeDTO.getCountryIsoCode(),
                storeDTO.isCurrencyFormatNational(),
                storeDTO.isUseCache(),
                storeDTO.getStoreTemplate(),
                storeDTO.getDomainName(),
                getLanguages(storeDTO));
        return merchantStoreInfoService.save(storeInfo);
    }

    private List<LanguageInfo> getLanguages(MerchantStoreDTO storeDTO) {
        return storeDTO.getLanguages().stream()
                .map(languageInfoService::findbyCode)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
