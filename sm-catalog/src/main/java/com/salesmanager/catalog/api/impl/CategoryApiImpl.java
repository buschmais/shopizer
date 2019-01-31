package com.salesmanager.catalog.api.impl;

import com.salesmanager.catalog.api.CategoryApi;
import com.salesmanager.catalog.business.integration.core.service.LanguageInfoService;
import com.salesmanager.catalog.business.service.category.CategoryService;
import com.salesmanager.catalog.model.category.Category;
import com.salesmanager.catalog.model.integration.core.LanguageInfo;
import com.salesmanager.common.presentation.model.BreadcrumbItem;
import com.salesmanager.common.presentation.model.BreadcrumbItemType;
import com.salesmanager.core.integration.language.LanguageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryApiImpl implements CategoryApi {

    private CategoryService categoryService;

    private LanguageInfoService languageInfoService;

    @Autowired
    public CategoryApiImpl(CategoryService categoryService, LanguageInfoService languageInfoService) {
        this.categoryService = categoryService;
        this.languageInfoService = languageInfoService;
    }

    @Override
    public BreadcrumbItem getBreadcrumbItemForLocale(Long categoryId, LanguageDTO languageDTO) {
        LanguageInfo languageInfo = this.languageInfoService.findbyCode(languageDTO.getCode());
        Category category = this.categoryService.getByLanguage(categoryId, languageInfo);
        if(category!=null) {
            BreadcrumbItem categoryItem = new  BreadcrumbItem();
            categoryItem.setId(category.getId());
            categoryItem.setItemType(BreadcrumbItemType.CATEGORY);
            categoryItem.setLabel(category.getDescription().getName());
            categoryItem.setUrl(category.getDescription().getSeUrl());
            return categoryItem;
        }
        return null;
    }

}
