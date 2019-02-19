package com.salesmanager.catalog.business.integration.core.listener;

import com.salesmanager.catalog.business.integration.core.repository.LanguageInfoRepository;
import com.salesmanager.catalog.model.integration.core.LanguageInfo;
import com.salesmanager.core.integration.language.LanguageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LanguageCoreEventListener {

    private LanguageInfoRepository languageInfoRepository;

    @Autowired
    public LanguageCoreEventListener(LanguageInfoRepository languageInfoRepository) {
        this.languageInfoRepository = languageInfoRepository;
    }

    @KafkaListener(topics = "language", containerFactory = "languageKafkaListenerContainerFactory")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleMerchantStoreEvent(LanguageDTO languageDTO) {
        if (languageDTO != null) {
            switch (languageDTO.getEventType()) {
                case CREATED:
                case UPDATED:
                    LanguageInfo language = new LanguageInfo(
                            languageDTO.getId(),
                            languageDTO.getCode()
                    );
                    this.languageInfoRepository.save(language);
                    break;
                case DELETED:
                    this.languageInfoRepository.delete(languageDTO.getId());
                    break;
            }

        }
    }

}
