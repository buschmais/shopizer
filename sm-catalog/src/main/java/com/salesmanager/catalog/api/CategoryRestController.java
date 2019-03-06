package com.salesmanager.catalog.api;

import com.salesmanager.catalog.business.integration.core.service.LanguageInfoService;
import com.salesmanager.catalog.business.service.category.CategoryService;
import com.salesmanager.catalog.model.category.Category;
import com.salesmanager.catalog.model.integration.core.LanguageInfo;
import com.salesmanager.common.presentation.model.BreadcrumbItem;
import com.salesmanager.common.presentation.model.BreadcrumbItemType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/catalog/category")
public class CategoryRestController {

    private final CategoryService categoryService;
    private final LanguageInfoService languageInfoService;

    @Autowired
    public CategoryRestController(CategoryService categoryService, LanguageInfoService languageInfoService) {
        this.categoryService = categoryService;
        this.languageInfoService = languageInfoService;
    }


    @RequestMapping(path = "/{categoryId}/breadcrumb", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public BreadcrumbItem getBreadcrumbItemForLocale(@PathVariable("categoryId") Long categoryId, @RequestParam("languageCode") String languageCode) {
        LanguageInfo languageInfo = this.languageInfoService.findbyCode(languageCode);
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
