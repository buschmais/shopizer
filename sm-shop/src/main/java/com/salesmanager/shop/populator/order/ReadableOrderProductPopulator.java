package com.salesmanager.shop.populator.order;

import com.salesmanager.catalog.api.CatalogImageFilePathApi;
import com.salesmanager.catalog.api.ProductApi;
import com.salesmanager.catalog.api.ProductPriceApi;
import com.salesmanager.catalog.model.product.image.ProductImage;
import com.salesmanager.core.business.services.catalog.ProductInfoService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.model.catalog.ProductInfo;
import com.salesmanager.shop.model.catalog.product.ReadableProduct;
import com.salesmanager.common.business.exception.ConversionException;
import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.orderproduct.OrderProduct;
import com.salesmanager.core.model.order.orderproduct.OrderProductAttribute;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.shop.model.order.ReadableOrderProduct;
import com.salesmanager.shop.model.order.ReadableOrderProductAttribute;
import com.salesmanager.shop.populator.catalog.ReadableProductPopulator;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ReadableOrderProductPopulator extends
		AbstractDataPopulator<OrderProduct, ReadableOrderProduct> {
	
	private ProductApi productApi;
	private ProductPriceApi productPriceApi;
	private CatalogImageFilePathApi imageFilePathApi;
	private CustomerService customerService;

	@Getter @Setter
	private ProductInfoService productInfoService;

	@Getter @Setter
	private LanguageService languageService;

	@Override
	public ReadableOrderProduct populate(OrderProduct source,
			ReadableOrderProduct target, MerchantStore store, Language language)
			throws ConversionException {
		
		Validate.notNull(productApi,"Requires ProductAPI");
		Validate.notNull(productPriceApi,"Requires productPriceApi");
		Validate.notNull(imageFilePathApi,"Requires imageFilePathApi");
		target.setId(source.getId());
		target.setOrderedQuantity(source.getProductQuantity());
		try {
			target.setPrice(productPriceApi.getStoreFormattedAmountWithCurrency(store.toDTO(), source.getOneTimeCharge()));
		} catch(Exception e) {
			throw new ConversionException("Cannot convert price",e);
		}
		target.setProductName(source.getProductName());
		target.setSku(source.getSku());
		
		//subtotal = price * quantity
		BigDecimal subTotal = source.getOneTimeCharge();
		subTotal = subTotal.multiply(new BigDecimal(source.getProductQuantity()));
		
		try {
			String subTotalPrice = productPriceApi.getStoreFormattedAmountWithCurrency(store.toDTO(), subTotal);
			target.setSubTotal(subTotalPrice);
		} catch(Exception e) {
			throw new ConversionException("Cannot format price",e);
		}
		
		if(source.getOrderAttributes()!=null) {
			List<ReadableOrderProductAttribute> attributes = new ArrayList<ReadableOrderProductAttribute>();
			for(OrderProductAttribute attr : source.getOrderAttributes()) {
				ReadableOrderProductAttribute readableAttribute = new ReadableOrderProductAttribute();
				readableAttribute.setAttributeName(attr.getProductAttributeName());
				readableAttribute.setAttributeValue(attr.getProductAttributeValueName());
				attributes.add(readableAttribute);
			}
			target.setAttributes(attributes);
		}
		

			String productSku = source.getSku();
			if(!StringUtils.isBlank(productSku)) {
				ProductInfo product = productInfoService.getProductForLocale(productSku, language);
				if(product!=null) {

					ReadableProductPopulator populator = new ReadableProductPopulator();
					populator.setImageFilePathApi(imageFilePathApi);
					populator.setProductPriceApi(productPriceApi);
					populator.setLanguageService(languageService);
					populator.setProductApi(productApi);
					
					ReadableProduct productProxy = populator.populate(product, new ReadableProduct(), store, language);
					target.setProduct(productProxy);
					
					ProductImage defaultImage = this.productApi.getDefaultImage(product.getId());
					if(defaultImage!=null) {
						target.setImage(defaultImage.getProductImage());
					}
				}
			}
		
		
		return target;
	}

	@Override
	protected ReadableOrderProduct createTarget() {

		return null;
	}

	public ProductPriceApi getProductPriceApi() {
		return productPriceApi;
	}

	public void setProductPriceApi(ProductPriceApi productPriceApi) {
		this.productPriceApi = productPriceApi;
	}

	public CatalogImageFilePathApi getImageFilePathApi() {
		return imageFilePathApi;
	}

	public void setImageFilePathApi(CatalogImageFilePathApi imageFilePathApi) {
		this.imageFilePathApi = imageFilePathApi;
	}

	public ProductApi getProductApi() {
		return productApi;
	}


	public void setProductApi(ProductApi productApi) {
		this.productApi = productApi;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}
}
