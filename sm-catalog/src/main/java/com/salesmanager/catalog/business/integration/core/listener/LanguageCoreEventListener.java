package com.salesmanager.catalog.business.integration.core.listener;

import com.salesmanager.catalog.business.integration.core.adapter.LanguageInfoAdapter;
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
    private final LanguageInfoAdapter languageInfoAdapter;

    @Autowired
    public LanguageCoreEventListener(LanguageInfoRepository languageInfoRepository, LanguageInfoAdapter languageInfoAdapter) {
        this.languageInfoRepository = languageInfoRepository;
        this.languageInfoAdapter = languageInfoAdapter;
    }

    @KafkaListener(topics = "language", containerFactory = "languageKafkaListenerContainerFactory")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleMerchantStoreCreateEvent(LanguageDTO languageDTO) {
        if (languageDTO != null) {
            switch (languageDTO.getEventType()) {
                case CREATE:
                case UPDATE:
                    this.languageInfoAdapter.createOrUpdateLanguageInfo(languageDTO);
                    break;
                case DELETE:
                    this.languageInfoRepository.delete(languageDTO.getId());
                    break;
            }

        }
    }

}
