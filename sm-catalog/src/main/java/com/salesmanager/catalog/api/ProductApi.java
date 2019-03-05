package com.salesmanager.catalog.api;

import com.salesmanager.common.business.exception.ServiceException;
import com.salesmanager.common.presentation.model.BreadcrumbItem;
import com.salesmanager.core.integration.language.LanguageDTO;

import java.util.Locale;

public interface ProductApi {

    BreadcrumbItem getBreadcrumbItemForLocale(long productId, LanguageDTO languageDTO, Locale locale) throws ServiceException;

    boolean isAvailable(Long productId);

}
