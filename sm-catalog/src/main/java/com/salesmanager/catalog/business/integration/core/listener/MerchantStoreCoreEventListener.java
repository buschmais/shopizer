package com.salesmanager.catalog.business.integration.core.listener;

import com.salesmanager.catalog.business.integration.core.repository.MerchantStoreInfoRepository;
import com.salesmanager.catalog.business.integration.core.service.LanguageInfoService;
import com.salesmanager.catalog.model.integration.core.LanguageInfo;
import com.salesmanager.catalog.model.integration.core.MerchantStoreInfo;
import com.salesmanager.core.integration.merchant.MerchantStoreDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class MerchantStoreCoreEventListener {

    private MerchantStoreInfoRepository merchantStoreInfoRepository;

    private LanguageInfoService languageInfoService;

    @Autowired
    public MerchantStoreCoreEventListener(MerchantStoreInfoRepository merchantStoreInfoRepository, LanguageInfoService languageInfoService) {
        this.merchantStoreInfoRepository = merchantStoreInfoRepository;
        this.languageInfoService = languageInfoService;
    }

    @KafkaListener(topics = "merchant_store", containerFactory = "merchantStoreKafkaListenerContainerFactory")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleMerchantStoreEvent(MerchantStoreDTO storeDTO) {
        if (storeDTO != null) {
            switch (storeDTO.getEventType()) {
                case CREATED:
                case UPDATED:
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
                    merchantStoreInfoRepository.save(storeInfo);
                    break;
                case DELETED:
                    merchantStoreInfoRepository.delete(storeDTO.getId());
                    break;
            }

        }
    }

    private List<LanguageInfo> getLanguages(MerchantStoreDTO storeDTO) {
        List<LanguageInfo> languages = storeDTO.getLanguages().stream()
                .map(code -> {
                    return languageInfoService.findbyCode(code);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return languages;
    }

}
