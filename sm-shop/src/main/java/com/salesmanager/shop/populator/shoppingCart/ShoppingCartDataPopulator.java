/**
 *
 */
package com.salesmanager.shop.populator.shoppingCart;

import com.salesmanager.catalog.api.ProductPriceApi;
import com.salesmanager.core.business.services.catalog.ProductInfoService;
import com.salesmanager.core.business.services.shoppingcart.ShoppingCartCalculationService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.catalog.ProductDescriptionInfo;
import com.salesmanager.core.model.catalog.ProductImageInfo;
import com.salesmanager.core.model.catalog.ProductOptionDescriptionInfo;
import com.salesmanager.core.model.catalog.ProductOptionValueDescriptionInfo;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.OrderSummary;
import com.salesmanager.core.model.order.OrderTotalSummary;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shoppingcart.ShoppingCart;
import com.salesmanager.shop.model.order.total.OrderTotal;
import com.salesmanager.shop.model.shoppingcart.ShoppingCartAttribute;
import com.salesmanager.shop.model.shoppingcart.ShoppingCartData;
import com.salesmanager.shop.model.shoppingcart.ShoppingCartItem;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;


/**
 * @author Umesh A
 *
 */


public class ShoppingCartDataPopulator extends AbstractDataPopulator<ShoppingCart,ShoppingCartData>
{

    private static final Logger LOG = LoggerFactory.getLogger(ShoppingCartDataPopulator.class);

    private ProductPriceApi productPriceApi;

    private  ShoppingCartCalculationService shoppingCartCalculationService;
    
    @Getter @Setter
    private ProductInfoService productInfoService;

    @Override
    public ShoppingCartData createTarget()
    {

        return new ShoppingCartData();
    }



    public ShoppingCartCalculationService getOrderService() {
        return shoppingCartCalculationService;
    }



    public ProductPriceApi getProductPriceApi() {
        return productPriceApi;
    }


    @Override
    public ShoppingCartData populate(final ShoppingCart shoppingCart,
                                     final ShoppingCartData cart, final MerchantStore store, final Language language) {

    	Validate.notNull(shoppingCart, "Requires ShoppingCart");
    	Validate.notNull(language, "Requires Language not null");
    	int cartQuantity = 0;
        cart.setCode(shoppingCart.getShoppingCartCode());
        Set<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> items = shoppingCart.getLineItems();
        List<ShoppingCartItem> shoppingCartItemsList=Collections.emptyList();
        try{
            if(items!=null) {
                shoppingCartItemsList=new ArrayList<ShoppingCartItem>();
                for(com.salesmanager.core.model.shoppingcart.ShoppingCartItem item : items) {

                    ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
                    shoppingCartItem.setCode(cart.getCode());
                    shoppingCartItem.setProductCode(item.getProduct().getSku());
                    shoppingCartItem.setProductVirtual(item.isProductVirtual());

                    shoppingCartItem.setProductId(item.getProductId());
                    shoppingCartItem.setId(item.getId());
                    
                    String itemName = item.getProduct().getProductDescription().getName();
                    if(!CollectionUtils.isEmpty(item.getProduct().getDescriptions())) {
                    	for(ProductDescriptionInfo productDescription : item.getProduct().getDescriptions()) {
                    		if(language != null && language.getId().intValue() == productDescription.getLanguageId()) {
                    			itemName = productDescription.getName();
                    			break;
                    		}
                    	}
                    }
                    
                    shoppingCartItem.setName(itemName);

                    shoppingCartItem.setPrice(productPriceApi.getStoreFormattedAmountWithCurrency(store.toDTO(), item.getItemPrice()));
                    shoppingCartItem.setQuantity(item.getQuantity());
                    
                    
                    cartQuantity = cartQuantity + item.getQuantity();
                    
                    shoppingCartItem.setProductPrice(item.getItemPrice());
                    shoppingCartItem.setSubTotal(productPriceApi.getStoreFormattedAmountWithCurrency(store.toDTO(), item.getSubTotal()));
                    ProductImageInfo image = productInfoService.getDefaultImage(item.getProduct().getId());
                    if(image != null) {
                        shoppingCartItem.setImage(image.getImageUrl());
                    }
                    Set<com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem> attributes = item.getAttributes();
                    if(attributes!=null) {
                        List<ShoppingCartAttribute> cartAttributes = new ArrayList<ShoppingCartAttribute>();
                        for(com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem attribute : attributes) {
                            ShoppingCartAttribute cartAttribute = new ShoppingCartAttribute();
                            cartAttribute.setId(attribute.getId());
                            cartAttribute.setAttributeId(attribute.getProductAttributeId());
                            cartAttribute.setOptionId(attribute.getProductAttribute().getProductOption().getId());
                            cartAttribute.setOptionValueId(attribute.getProductAttribute().getProductOptionValue().getId());
                            Set<ProductOptionDescriptionInfo> optionDescriptions = attribute.getProductAttribute().getProductOption().getDescriptions();
                            Set<ProductOptionValueDescriptionInfo> optionValueDescriptions = attribute.getProductAttribute().getProductOptionValue().getDescriptions();
                            if(!CollectionUtils.isEmpty(optionDescriptions) && !CollectionUtils.isEmpty(optionValueDescriptions)) {
                            	
                            	String optionName = optionDescriptions.iterator().next().getName();
                            	String optionValue = optionValueDescriptions.iterator().next().getName();
                            	
                            	for(ProductOptionDescriptionInfo optionDescription : optionDescriptions) {
                            		if(optionDescription.getLanguage() != null && optionDescription.getLanguage().getId().intValue() == language.getId().intValue()) {
                            			optionName = optionDescription.getName();
                            			break;
                            		}
                            	}
                            	
                            	for(ProductOptionValueDescriptionInfo optionValueDescription : optionValueDescriptions) {
                            		if(optionValueDescription.getLanguage() != null && optionValueDescription.getLanguage().getId().intValue() == language.getId().intValue()) {
                            			optionValue = optionValueDescription.getName();
                            			break;
                            		}
                            	}
                            	cartAttribute.setOptionName(optionName);
                            	cartAttribute.setOptionValue(optionValue);
                            	cartAttributes.add(cartAttribute);
                            }
                        }
                        shoppingCartItem.setShoppingCartAttributes(cartAttributes);
                    }
                    shoppingCartItemsList.add(shoppingCartItem);
                }
            }
            if(CollectionUtils.isNotEmpty(shoppingCartItemsList)){
                cart.setShoppingCartItems(shoppingCartItemsList);
            }

            OrderSummary summary = new OrderSummary();
            List<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> productsList = new ArrayList<com.salesmanager.core.model.shoppingcart.ShoppingCartItem>();
            productsList.addAll(shoppingCart.getLineItems());
            summary.setProducts(productsList);
            OrderTotalSummary orderSummary = shoppingCartCalculationService.calculate(shoppingCart,store, language );

            if(CollectionUtils.isNotEmpty(orderSummary.getTotals())) {
            	List<OrderTotal> totals = new ArrayList<OrderTotal>();
            	for(com.salesmanager.core.model.order.OrderTotal t : orderSummary.getTotals()) {
            		OrderTotal total = new OrderTotal();
            		total.setCode(t.getOrderTotalCode());
            		total.setValue(t.getValue());
            		totals.add(total);
            	}
            	cart.setTotals(totals);
            }
            
            cart.setSubTotal(productPriceApi.getStoreFormattedAmountWithCurrency(store.toDTO(), orderSummary.getSubTotal()));
            cart.setTotal(productPriceApi.getStoreFormattedAmountWithCurrency(store.toDTO(), orderSummary.getTotal()));
            cart.setQuantity(cartQuantity);
            cart.setId(shoppingCart.getId());
        }
        catch(Exception ex){
            LOG.error( "Error while converting cart Model to cart Data.."+ex );
            throw new ConversionException( "Unable to create cart data", ex );
        }
        return cart;


    };





    public void setProductPriceApi(final ProductPriceApi productPriceApi) {
        this.productPriceApi = productPriceApi;
    }






    public void setShoppingCartCalculationService(final ShoppingCartCalculationService shoppingCartCalculationService) {
        this.shoppingCartCalculationService = shoppingCartCalculationService;
    }




}
