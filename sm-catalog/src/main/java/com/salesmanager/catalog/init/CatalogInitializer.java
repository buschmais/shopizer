package com.salesmanager.catalog.init;

import com.salesmanager.catalog.business.integration.core.service.LanguageInfoService;
import com.salesmanager.catalog.business.integration.core.service.MerchantStoreInfoService;
import com.salesmanager.catalog.business.integration.core.service.TaxClassInfoService;
import com.salesmanager.catalog.business.service.category.CategoryService;
import com.salesmanager.catalog.business.service.product.ProductService;
import com.salesmanager.catalog.business.service.product.image.ProductImageService;
import com.salesmanager.catalog.business.service.product.manufacturer.ManufacturerService;
import com.salesmanager.catalog.business.service.product.relationship.ProductRelationshipService;
import com.salesmanager.catalog.business.service.product.type.ProductTypeService;
import com.salesmanager.catalog.model.category.Category;
import com.salesmanager.catalog.model.category.CategoryDescription;
import com.salesmanager.catalog.model.content.FileContentType;
import com.salesmanager.catalog.model.content.ImageContentFile;
import com.salesmanager.catalog.model.integration.core.LanguageInfo;
import com.salesmanager.catalog.model.integration.core.MerchantStoreInfo;
import com.salesmanager.catalog.model.integration.core.TaxClassInfo;
import com.salesmanager.catalog.model.product.Product;
import com.salesmanager.catalog.model.product.availability.ProductAvailability;
import com.salesmanager.catalog.model.product.description.ProductDescription;
import com.salesmanager.catalog.model.product.image.ProductImage;
import com.salesmanager.catalog.model.product.manufacturer.Manufacturer;
import com.salesmanager.catalog.model.product.manufacturer.ManufacturerDescription;
import com.salesmanager.catalog.model.product.price.ProductPrice;
import com.salesmanager.catalog.model.product.price.ProductPriceDescription;
import com.salesmanager.catalog.model.product.relationship.ProductRelationship;
import com.salesmanager.catalog.model.product.relationship.ProductRelationshipType;
import com.salesmanager.catalog.model.product.type.ProductType;
import com.salesmanager.common.business.exception.ServiceException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class CatalogInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogInitializer.class);

    private String name;

    private final CategoryService categoryService;

    private final MerchantStoreInfoService merchantStoreInfoService;

    private final LanguageInfoService languageInfoService;

    private final ManufacturerService manufacturerService;

    private final ProductImageService productImageService;

    private final ProductRelationshipService productRelationshipService;

    private final ProductService productService;

    private final ProductTypeService productTypeService;

    private final TaxClassInfoService taxClassInfoService;


    @Autowired
    public CatalogInitializer(CategoryService categoryService, MerchantStoreInfoService merchantStoreInfoService, LanguageInfoService languageInfoService, ManufacturerService manufacturerService, ProductImageService productImageService, ProductRelationshipService productRelationshipService, ProductService productService, ProductTypeService productTypeService, TaxClassInfoService taxClassInfoService) {
        this.categoryService = categoryService;
        this.merchantStoreInfoService = merchantStoreInfoService;
        this.languageInfoService = languageInfoService;
        this.manufacturerService = manufacturerService;
        this.productImageService = productImageService;
        this.productRelationshipService = productRelationshipService;
        this.productService = productService;
        this.productTypeService = productTypeService;
        this.taxClassInfoService = taxClassInfoService;
    }

    @Transactional
    public void initCatalog(String contextName) throws ServiceException {
        this.name = contextName;

        createManufacturer();

        createSubReferences();
    }

    private void createManufacturer() throws ServiceException {
        MerchantStoreInfo storeInfo = this.merchantStoreInfoService.findbyCode("DEFAULT");
        LanguageInfo en = languageInfoService.findbyCode("en");
        //create default manufacturer
        Manufacturer defaultManufacturer = new Manufacturer();
        defaultManufacturer.setCode("DEFAULT");
        defaultManufacturer.setMerchantStore(storeInfo);

        ManufacturerDescription manufacturerDescription = new ManufacturerDescription();
        manufacturerDescription.setLanguage(en);
        manufacturerDescription.setName("DEFAULT");
        manufacturerDescription.setManufacturer(defaultManufacturer);
        manufacturerDescription.setDescription("DEFAULT");
        defaultManufacturer.getDescriptions().add(manufacturerDescription);

        manufacturerService.create(defaultManufacturer);
    }

    private void createSubReferences() throws ServiceException {

        LOGGER.info(String.format("%s : Loading catalog sub references ", name));



        ProductType productType = new ProductType();
        productType.setCode(ProductType.GENERAL_TYPE);
        productTypeService.create(productType);

    }

    public void initInitialData() throws ServiceException {

        Date date = new Date(System.currentTimeMillis());

        LanguageInfo enInfo = languageInfoService.findbyCode("en");
        LanguageInfo frInfo = languageInfoService.findbyCode("fr");

        MerchantStoreInfo storeInfo = merchantStoreInfoService.findbyCode("DEFAULT");
        ProductType generalType = productTypeService.getProductType(ProductType.GENERAL_TYPE);

        TaxClassInfo taxClassInfo = this.taxClassInfoService.findByCode("DEFAULT");

        Category book = new Category();
        book.setMerchantStore(storeInfo);
        book.setCode("computerbooks");
        book.setVisible(true);

        CategoryDescription bookEnglishDescription = new CategoryDescription();
        bookEnglishDescription.setName("Computer Books");
        bookEnglishDescription.setCategory(book);
        bookEnglishDescription.setLanguage(enInfo);
        bookEnglishDescription.setSeUrl("computer-books");

        CategoryDescription bookFrenchDescription = new CategoryDescription();
        bookFrenchDescription.setName("Livres d'informatique");
        bookFrenchDescription.setCategory(book);
        bookFrenchDescription.setLanguage(frInfo);
        bookFrenchDescription.setSeUrl("livres-informatiques");

        List<CategoryDescription> descriptions = new ArrayList<CategoryDescription>();
        descriptions.add(bookEnglishDescription);
        descriptions.add(bookFrenchDescription);

        book.setDescriptions(descriptions);

        categoryService.create(book);

        Category novs = new Category();
        novs.setMerchantStore(storeInfo);
        novs.setCode("novels");
        novs.setVisible(false);

        CategoryDescription novsEnglishDescription = new CategoryDescription();
        novsEnglishDescription.setName("Novels");
        novsEnglishDescription.setCategory(novs);
        novsEnglishDescription.setLanguage(enInfo);
        novsEnglishDescription.setSeUrl("novels");

        CategoryDescription novsFrenchDescription = new CategoryDescription();
        novsFrenchDescription.setName("Romans");
        novsFrenchDescription.setCategory(novs);
        novsFrenchDescription.setLanguage(frInfo);
        novsFrenchDescription.setSeUrl("romans");

        List<CategoryDescription> descriptions2 = new ArrayList<CategoryDescription>();
        descriptions2.add(novsEnglishDescription);
        descriptions2.add(novsFrenchDescription);

        novs.setDescriptions(descriptions2);

        categoryService.create(novs);

        Category tech = new Category();
        tech.setMerchantStore(storeInfo);
        tech.setCode("tech");

        CategoryDescription techEnglishDescription = new CategoryDescription();
        techEnglishDescription.setName("Technology");
        techEnglishDescription.setCategory(tech);
        techEnglishDescription.setLanguage(enInfo);
        techEnglishDescription.setSeUrl("technology");

        CategoryDescription techFrenchDescription = new CategoryDescription();
        techFrenchDescription.setName("Technologie");
        techFrenchDescription.setCategory(tech);
        techFrenchDescription.setLanguage(frInfo);
        techFrenchDescription.setSeUrl("technologie");

        List<CategoryDescription> descriptions4 = new ArrayList<CategoryDescription>();
        descriptions4.add(techEnglishDescription);
        descriptions4.add(techFrenchDescription);

        tech.setDescriptions(descriptions4);

        tech.setParent(book);

        categoryService.create(tech);
        categoryService.addChild(book, tech);

        Category web = new Category();
        web.setMerchantStore(storeInfo);
        web.setCode("web");
        web.setVisible(true);

        CategoryDescription webEnglishDescription = new CategoryDescription();
        webEnglishDescription.setName("Web");
        webEnglishDescription.setCategory(web);
        webEnglishDescription.setLanguage(enInfo);
        webEnglishDescription.setSeUrl("the-web");

        CategoryDescription webFrenchDescription = new CategoryDescription();
        webFrenchDescription.setName("Web");
        webFrenchDescription.setCategory(web);
        webFrenchDescription.setLanguage(frInfo);
        webFrenchDescription.setSeUrl("le-web");

        List<CategoryDescription> descriptions3 = new ArrayList<CategoryDescription>();
        descriptions3.add(webEnglishDescription);
        descriptions3.add(webFrenchDescription);

        web.setDescriptions(descriptions3);

        web.setParent(book);

        categoryService.create(web);
        categoryService.addChild(book, web);



        Category fiction = new Category();
        fiction.setMerchantStore(storeInfo);
        fiction.setCode("fiction");
        fiction.setVisible(true);

        CategoryDescription fictionEnglishDescription = new CategoryDescription();
        fictionEnglishDescription.setName("Fiction");
        fictionEnglishDescription.setCategory(fiction);
        fictionEnglishDescription.setLanguage(enInfo);
        fictionEnglishDescription.setSeUrl("fiction");

        CategoryDescription fictionFrenchDescription = new CategoryDescription();
        fictionFrenchDescription.setName("Sc Fiction");
        fictionFrenchDescription.setCategory(fiction);
        fictionFrenchDescription.setLanguage(frInfo);
        fictionFrenchDescription.setSeUrl("fiction");

        List<CategoryDescription> fictiondescriptions = new ArrayList<CategoryDescription>();
        fictiondescriptions.add(fictionEnglishDescription);
        fictiondescriptions.add(fictionFrenchDescription);

        fiction.setDescriptions(fictiondescriptions);

        fiction.setParent(novs);

        categoryService.create(fiction);
        categoryService.addChild(novs, fiction);


        Category business = new Category();
        business.setMerchantStore(storeInfo);
        business.setCode("business");
        business.setVisible(true);

        CategoryDescription businessEnglishDescription = new CategoryDescription();
        businessEnglishDescription.setName("Business");
        businessEnglishDescription.setCategory(business);
        businessEnglishDescription.setLanguage(enInfo);
        businessEnglishDescription.setSeUrl("business");

        CategoryDescription businessFrenchDescription = new CategoryDescription();
        businessFrenchDescription.setName("Affaires");
        businessFrenchDescription.setCategory(business);
        businessFrenchDescription.setLanguage(frInfo);
        businessFrenchDescription.setSeUrl("affaires");

        List<CategoryDescription> businessdescriptions = new ArrayList<CategoryDescription>();
        businessdescriptions.add(businessEnglishDescription);
        businessdescriptions.add(businessFrenchDescription);

        business.setDescriptions(businessdescriptions);


        categoryService.create(business);



        Category cloud = new Category();
        cloud.setMerchantStore(storeInfo);
        cloud.setCode("cloud");
        cloud.setVisible(true);

        CategoryDescription cloudEnglishDescription = new CategoryDescription();
        cloudEnglishDescription.setName("Cloud computing");
        cloudEnglishDescription.setCategory(cloud);
        cloudEnglishDescription.setLanguage(enInfo);
        cloudEnglishDescription.setSeUrl("cloud-computing");

        CategoryDescription cloudFrenchDescription = new CategoryDescription();
        cloudFrenchDescription.setName("Programmation pour le cloud");
        cloudFrenchDescription.setCategory(cloud);
        cloudFrenchDescription.setLanguage(frInfo);
        cloudFrenchDescription.setSeUrl("programmation-cloud");

        List<CategoryDescription> clouddescriptions = new ArrayList<CategoryDescription>();
        clouddescriptions.add(cloudEnglishDescription);
        clouddescriptions.add(cloudFrenchDescription);

        cloud.setDescriptions(clouddescriptions);

        cloud.setParent(tech);

        categoryService.create(cloud);
        categoryService.addChild(tech, cloud);

        // Add products
        // ProductType generalType = productTypeService.

        Manufacturer oreilley = new Manufacturer();
        oreilley.setMerchantStore(storeInfo);
        oreilley.setCode("oreilley");

        ManufacturerDescription oreilleyd = new ManufacturerDescription();
        oreilleyd.setLanguage(enInfo);
        oreilleyd.setName("O\'Reilley");
        oreilleyd.setManufacturer(oreilley);
        oreilley.getDescriptions().add(oreilleyd);

        manufacturerService.create(oreilley);


        Manufacturer sams = new Manufacturer();
        sams.setMerchantStore(storeInfo);
        sams.setCode("sams");

        ManufacturerDescription samsd = new ManufacturerDescription();
        samsd.setLanguage(enInfo);
        samsd.setName("Sams");
        samsd.setManufacturer(sams);
        sams.getDescriptions().add(samsd);

        manufacturerService.create(sams);

        Manufacturer packt = new Manufacturer();
        packt.setMerchantStore(storeInfo);
        packt.setCode("packt");

        ManufacturerDescription packtd = new ManufacturerDescription();
        packtd.setLanguage(enInfo);
        packtd.setName("Packt");
        packtd.setManufacturer(packt);
        packt.getDescriptions().add(packtd);

        manufacturerService.create(packt);

        Manufacturer manning = new Manufacturer();
        manning.setMerchantStore(storeInfo);
        manning.setCode("manning");

        ManufacturerDescription manningd = new ManufacturerDescription();
        manningd.setLanguage(enInfo);
        manningd.setManufacturer(manning);
        manningd.setName("Manning");
        manning.getDescriptions().add(manningd);

        manufacturerService.create(manning);

        Manufacturer novells = new Manufacturer();
        novells.setMerchantStore(storeInfo);
        novells.setCode("novells");

        ManufacturerDescription novellsd = new ManufacturerDescription();
        novellsd.setLanguage(enInfo);
        novellsd.setManufacturer(novells);
        novellsd.setName("Novells publishing");
        novells.getDescriptions().add(novellsd);

        manufacturerService.create(novells);


        // PRODUCT 1

        Product product = new Product();
        product.setProductHeight(new BigDecimal(10));
        product.setProductLength(new BigDecimal(3));
        product.setProductWidth(new BigDecimal(6));
        product.setSku("TB12345");
        product.setManufacturer(manning);
        product.setType(generalType);
        product.setMerchantStore(storeInfo);
        product.setProductShipeable(true);
        product.setTaxClass(taxClassInfo);

        // Availability
        ProductAvailability availability = new ProductAvailability();
        availability.setProductDateAvailable(date);
        availability.setProductQuantity(100);
        availability.setRegion("*");
        availability.setProduct(product);// associate with product



        ProductPrice dprice = new ProductPrice();
        dprice.setDefaultPrice(true);
        dprice.setProductPriceAmount(new BigDecimal(39.99));
        dprice.setProductAvailability(availability);

        ProductPriceDescription dpd = new ProductPriceDescription();
        dpd.setName("Base price");
        dpd.setProductPrice(dprice);
        dpd.setLanguage(enInfo);

        dprice.getDescriptions().add(dpd);

        availability.getPrices().add(dprice);
        product.getAvailabilities().add(availability);

        // Product description
        ProductDescription description = new ProductDescription();
        description.setName("Spring in Action");
        description.setLanguage(enInfo);
        description.setSeUrl("Spring-in-Action");
        description.setProduct(product);

        product.getDescriptions().add(description);

        product.getCategories().add(tech);
        product.getCategories().add(web);


        productService.create(product);



        try {
            ClassPathResource classPathResource = new ClassPathResource("/demo/spring.png");
            InputStream inStream = classPathResource.getInputStream();
            this.saveFile(inStream, "spring.png", product);
        } catch(Exception e) {
            LOGGER.error("Error while reading demo file spring.png",e);
        }


        // PRODUCT 2

        Product product2 = new Product();
        product2.setProductHeight(new BigDecimal(4));
        product2.setProductLength(new BigDecimal(3));
        product2.setProductWidth(new BigDecimal(1));
        product2.setSku("TB2468");
        product2.setManufacturer(packt);
        product2.setType(generalType);
        product2.setMerchantStore(storeInfo);
        product2.setProductShipeable(true);
        product2.setTaxClass(taxClassInfo);

        // Product description
        description = new ProductDescription();
        description.setName("Node Web Development");
        description.setLanguage(enInfo);
        description.setProduct(product2);
        description.setSeUrl("Node-Web-Development");

        product2.getDescriptions().add(description);

        product2.getCategories().add(tech);
        product2.getCategories().add(web);

        // Availability
        ProductAvailability availability2 = new ProductAvailability();
        availability2.setProductDateAvailable(date);
        availability2.setProductQuantity(100);
        availability2.setRegion("*");
        availability2.setProduct(product2);// associate with product

        ProductPrice dprice2 = new ProductPrice();
        dprice2.setDefaultPrice(true);
        dprice2.setProductPriceAmount(new BigDecimal(29.99));
        dprice2.setProductAvailability(availability2);

        dpd = new ProductPriceDescription();
        dpd.setName("Base price");
        dpd.setProductPrice(dprice2);
        dpd.setLanguage(enInfo);

        dprice2.getDescriptions().add(dpd);

        availability2.getPrices().add(dprice2);
        product2.getAvailabilities().add(availability2);

        productService.create(product2);

        try {
            ClassPathResource classPathResource = new ClassPathResource("/demo/node.jpg");
            InputStream inStream = classPathResource.getInputStream();
            this.saveFile(inStream, "node.jpg", product2);
        } catch(Exception e) {
            LOGGER.error("Error while reading demo file node.jpg",e);
        }



        // PRODUCT 3

        Product product3 = new Product();
        product3.setProductHeight(new BigDecimal(4));
        product3.setProductLength(new BigDecimal(3));
        product3.setProductWidth(new BigDecimal(1));
        product3.setSku("NB1111");
        product3.setManufacturer(oreilley);
        product3.setType(generalType);
        product3.setMerchantStore(storeInfo);
        product3.setProductShipeable(true);
        product3.setTaxClass(taxClassInfo);

        // Product description
        description = new ProductDescription();
        description.setName("Programming for PAAS");
        description.setLanguage(enInfo);
        description.setProduct(product3);
        description.setSeUrl("programming-for-paas");

        product3.getDescriptions().add(description);

        product3.getCategories().add(cloud);

        // Availability
        ProductAvailability availability3 = new ProductAvailability();
        availability3.setProductDateAvailable(date);
        availability3.setProductQuantity(100);
        availability3.setRegion("*");
        availability3.setProduct(product3);// associate with product

        ProductPrice dprice3 = new ProductPrice();
        dprice3.setDefaultPrice(true);
        dprice3.setProductPriceAmount(new BigDecimal(19.99));
        dprice3.setProductAvailability(availability3);

        dpd = new ProductPriceDescription();
        dpd.setName("Base price");
        dpd.setProductPrice(dprice3);
        dpd.setLanguage(enInfo);

        dprice3.getDescriptions().add(dpd);

        availability3.getPrices().add(dprice3);
        product3.getAvailabilities().add(availability3);


        productService.create(product3);


        try {
            ClassPathResource classPathResource = new ClassPathResource("/demo/paas.JPG");
            InputStream inStream = classPathResource.getInputStream();
            this.saveFile(inStream, "paas.JPG", product3);
        } catch(Exception e) {
            LOGGER.error("Error while reading demo file paas.jpg",e);
        }

        // PRODUCT 4
        Product product4 = new Product();
        product4.setProductHeight(new BigDecimal(4));
        product4.setProductLength(new BigDecimal(3));
        product4.setProductWidth(new BigDecimal(1));
        product4.setSku("SF333345");
        product4.setManufacturer(sams);
        product4.setType(generalType);
        product4.setMerchantStore(storeInfo);
        product4.setProductShipeable(true);
        product4.setTaxClass(taxClassInfo);

        // Product description
        description = new ProductDescription();
        description.setName("Android development");
        description.setLanguage(enInfo);
        description.setProduct(product4);
        description.setSeUrl("android-application-development");

        product4.getDescriptions().add(description);

        product4.getCategories().add(tech);

        // Availability
        ProductAvailability availability4 = new ProductAvailability();
        availability4.setProductDateAvailable(date);
        availability4.setProductQuantity(100);
        availability4.setRegion("*");
        availability4.setProduct(product4);// associate with product


        ProductPrice dprice4 = new ProductPrice();
        dprice4.setDefaultPrice(true);
        dprice4.setProductPriceAmount(new BigDecimal(18.99));
        dprice4.setProductAvailability(availability4);

        dpd = new ProductPriceDescription();
        dpd.setName("Base price");
        dpd.setProductPrice(dprice4);
        dpd.setLanguage(enInfo);

        dprice4.getDescriptions().add(dpd);

        availability4.getPrices().add(dprice4);
        product4.getAvailabilities().add(availability4);

        productService.create(product4);



        try {
            ClassPathResource classPathResource = new ClassPathResource("/demo/android.jpg");
            InputStream inStream = classPathResource.getInputStream();
            this.saveFile(inStream, "android.jpg", product4);
        } catch(Exception e) {
            LOGGER.error("Error while reading demo file android.jpg",e);
        }

        // PRODUCT 5
        Product product5 = new Product();
        product5.setProductHeight(new BigDecimal(4));
        product5.setProductLength(new BigDecimal(3));
        product5.setProductWidth(new BigDecimal(1));
        product5.setSku("SF333346");
        product5.setManufacturer(packt);
        product5.setType(generalType);
        product5.setMerchantStore(storeInfo);
        product5.setProductShipeable(true);
        product5.setTaxClass(taxClassInfo);

        // Product description
        description = new ProductDescription();
        description.setName("Android 3.0 Cookbook");
        description.setLanguage(enInfo);
        description.setProduct(product5);
        description.setSeUrl("android-3-cookbook");

        product5.getDescriptions().add(description);

        product5.getCategories().add(tech);


        // Availability
        ProductAvailability availability5 = new ProductAvailability();
        availability5.setProductDateAvailable(date);
        availability5.setProductQuantity(100);
        availability5.setRegion("*");
        availability5.setProduct(product5);// associate with product

        // productAvailabilityService.create(availability5);

        ProductPrice dprice5 = new ProductPrice();
        dprice5.setDefaultPrice(true);
        dprice5.setProductPriceAmount(new BigDecimal(18.99));
        dprice5.setProductAvailability(availability5);

        dpd = new ProductPriceDescription();
        dpd.setName("Base price");
        dpd.setProductPrice(dprice5);
        dpd.setLanguage(enInfo);

        dprice5.getDescriptions().add(dpd);

        availability5.getPrices().add(dprice5);
        product5.getAvailabilities().add(availability5);

        productService.create(product5);



        try {
            ClassPathResource classPathResource = new ClassPathResource("/demo/android2.jpg");
            InputStream inStream = classPathResource.getInputStream();
            this.saveFile(inStream, "android2.jpg", product5);
        } catch(Exception e) {
            LOGGER.error("Error while reading demo file android2.jpg",e);
        }

        // PRODUCT 6

        Product product6 = new Product();
        product6.setProductHeight(new BigDecimal(4));
        product6.setProductLength(new BigDecimal(3));
        product6.setProductWidth(new BigDecimal(1));
        product6.setSku("LL333444");
        product6.setManufacturer(novells);
        product6.setType(generalType);
        product6.setMerchantStore(storeInfo);
        product6.setProductShipeable(true);
        product6.setTaxClass(taxClassInfo);

        // Product description
        description = new ProductDescription();
        description.setName("The Big Switch");
        description.setLanguage(enInfo);
        description.setProduct(product6);
        description.setSeUrl("the-big-switch");

        product6.getDescriptions().add(description);

        product6.getCategories().add(business);

        // Availability
        ProductAvailability availability6 = new ProductAvailability();
        availability6.setProductDateAvailable(date);
        availability6.setProductQuantity(100);
        availability6.setRegion("*");
        availability6.setProduct(product6);// associate with product

        //productAvailabilityService.create(availability6);

        ProductPrice dprice6 = new ProductPrice();
        dprice6.setDefaultPrice(true);
        dprice6.setProductPriceAmount(new BigDecimal(18.99));
        dprice6.setProductAvailability(availability6);

        dpd = new ProductPriceDescription();
        dpd.setName("Base price");
        dpd.setProductPrice(dprice6);
        dpd.setLanguage(enInfo);

        dprice6.getDescriptions().add(dpd);

        availability6.getPrices().add(dprice6);
        product6.getAvailabilities().add(availability6);

        productService.create(product6);



        try {

            ClassPathResource classPathResource = new ClassPathResource("/demo/google.jpg");
            InputStream inStream = classPathResource.getInputStream();
            this.saveFile(inStream, "google.jpg", product6);
        } catch(Exception e) {
            LOGGER.error("Error while reading demo file google.jpg",e);
        }

        //featured items

        ProductRelationship relationship = new ProductRelationship();
        relationship.setActive(true);
        relationship.setCode(ProductRelationshipType.FEATURED_ITEM.name());
        relationship.setStore(storeInfo);
        relationship.setRelatedProduct(product);

        productRelationshipService.saveOrUpdate(relationship);

        relationship = new ProductRelationship();
        relationship.setActive(true);
        relationship.setCode(ProductRelationshipType.FEATURED_ITEM.name());
        relationship.setStore(storeInfo);
        relationship.setRelatedProduct(product6);

        productRelationshipService.saveOrUpdate(relationship);


        relationship = new ProductRelationship();
        relationship.setActive(true);
        relationship.setCode(ProductRelationshipType.FEATURED_ITEM.name());
        relationship.setStore(storeInfo);
        relationship.setRelatedProduct(product5);

        productRelationshipService.saveOrUpdate(relationship);


        relationship = new ProductRelationship();
        relationship.setActive(true);
        relationship.setCode(ProductRelationshipType.FEATURED_ITEM.name());
        relationship.setStore(storeInfo);
        relationship.setRelatedProduct(product2);

        productRelationshipService.saveOrUpdate(relationship);

    }

    private void saveFile(InputStream fis, String name, Product product) throws Exception {

        if(fis==null) {
            return;
        }

        final byte[] is = IOUtils.toByteArray( fis );
        final ByteArrayInputStream inputStream = new ByteArrayInputStream( is );
        final ImageContentFile cmsContentImage = new ImageContentFile();
        cmsContentImage.setFileName( name );
        cmsContentImage.setFile( inputStream );
        cmsContentImage.setFileContentType(FileContentType.PRODUCT);


        ProductImage productImage = new ProductImage();
        productImage.setProductImage(name);
        productImage.setProduct(product);


        productImageService.addProductImage(product, productImage, cmsContentImage);


    }
}
