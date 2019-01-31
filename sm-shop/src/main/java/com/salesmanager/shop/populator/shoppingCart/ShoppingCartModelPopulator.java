/**
 * 
 */
package com.salesmanager.shop.populator.shoppingCart;

import com.salesmanager.common.business.exception.ServiceException;
import com.salesmanager.core.business.repositories.catalog.ProductInfoRepository;
import com.salesmanager.core.business.services.shoppingcart.ShoppingCartService;
import com.salesmanager.core.business.utils.AbstractDataPopulator;
import com.salesmanager.core.model.catalog.ProductAttributeInfo;
import com.salesmanager.core.model.catalog.ProductInfo;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.shoppingcart.ShoppingCart;
import com.salesmanager.shop.model.shoppingcart.ShoppingCartAttribute;
import com.salesmanager.shop.model.shoppingcart.ShoppingCartData;
import com.salesmanager.shop.model.shoppingcart.ShoppingCartItem;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Umesh A
 */

@Service(value="shoppingCartModelPopulator")
public class ShoppingCartModelPopulator
    extends AbstractDataPopulator<ShoppingCartData,ShoppingCart>
{

	private static final Logger LOG = LoggerFactory.getLogger(ShoppingCartModelPopulator.class);

    private ShoppingCartService shoppingCartService;
    
    private Customer customer;

    @Getter @Setter
    private ProductInfoRepository productInfoRepository;

    public ShoppingCartService getShoppingCartService() {
		return shoppingCartService;
	}


	public void setShoppingCartService(ShoppingCartService shoppingCartService) {
		this.shoppingCartService = shoppingCartService;
	}

    @Override
    public ShoppingCart populate(ShoppingCartData shoppingCart,ShoppingCart cartMdel,final MerchantStore store, Language language)
    {


        // if id >0 get the original from the database, override products
       try{
        if ( shoppingCart.getId() > 0  && StringUtils.isNotBlank( shoppingCart.getCode()))
        {
            cartMdel = shoppingCartService.getByCode( shoppingCart.getCode(), store );
            if(cartMdel==null){
                cartMdel=new ShoppingCart();
                cartMdel.setShoppingCartCode( shoppingCart.getCode() );
                cartMdel.setMerchantStore( store );
                if ( customer != null )
                {
                    cartMdel.setCustomerId( customer.getId() );
                }
                shoppingCartService.create( cartMdel );
            }
        }
        else
        {
            cartMdel.setShoppingCartCode( shoppingCart.getCode() );
            cartMdel.setMerchantStore( store );
            if ( customer != null )
            {
                cartMdel.setCustomerId( customer.getId() );
            }
            shoppingCartService.create( cartMdel );
        }

        List<ShoppingCartItem> items = shoppingCart.getShoppingCartItems();
        Set<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> newItems =
            new HashSet<com.salesmanager.core.model.shoppingcart.ShoppingCartItem>();
        if ( items != null && items.size() > 0 )
        {
            for ( ShoppingCartItem item : items )
            {

                Set<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> cartItems = cartMdel.getLineItems();
                if ( cartItems != null && cartItems.size() > 0 )
                {

                    for ( com.salesmanager.core.model.shoppingcart.ShoppingCartItem dbItem : cartItems )
                    {
                        if ( dbItem.getId().longValue() == item.getId() )
                        {
                            dbItem.setQuantity( item.getQuantity() );
                            // compare attributes
                            Set<com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem> attributes =
                                dbItem.getAttributes();
                            Set<com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem> newAttributes =
                                new HashSet<com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem>();
                            List<ShoppingCartAttribute> cartAttributes = item.getShoppingCartAttributes();
                            if ( !CollectionUtils.isEmpty( cartAttributes ) )
                            {
                                for ( ShoppingCartAttribute attribute : cartAttributes )
                                {
                                    for ( com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem dbAttribute : attributes )
                                    {
                                        if ( dbAttribute.getId().longValue() == attribute.getId() )
                                        {
                                            newAttributes.add( dbAttribute );
                                        }
                                    }
                                }
                                
                                dbItem.setAttributes( newAttributes );
                            }
                            else
                            {
                                dbItem.removeAllAttributes();
                            }
                            newItems.add( dbItem );
                        }
                    }
                }
                else
                {// create new item
                    com.salesmanager.core.model.shoppingcart.ShoppingCartItem cartItem =
                        createCartItem( cartMdel, item, store );
                    Set<com.salesmanager.core.model.shoppingcart.ShoppingCartItem> lineItems =
                        cartMdel.getLineItems();
                    if ( lineItems == null )
                    {
                        lineItems = new HashSet<>();
                        cartMdel.setLineItems( lineItems );
                    }
                    lineItems.add( cartItem );
                    shoppingCartService.update( cartMdel );
                }
            }// end for
        }// end if
       }catch(ServiceException se){
           LOG.error( "Error while converting cart data to cart model.."+se );
           throw new ConversionException( "Unable to create cart model", se ); 
       }
       catch (Exception ex){
           LOG.error( "Error while converting cart data to cart model.."+ex );
           throw new ConversionException( "Unable to create cart model", ex );  
       }

        return cartMdel;
    }

   
    private com.salesmanager.core.model.shoppingcart.ShoppingCartItem createCartItem( com.salesmanager.core.model.shoppingcart.ShoppingCart cart,
                                                                                               ShoppingCartItem shoppingCartItem,
                                                                                               MerchantStore store )
        throws Exception
    {

        ProductInfo product = productInfoRepository.findOne(shoppingCartItem.getProductId());

        if ( product == null )
        {
            throw new Exception( "Item with id " + shoppingCartItem.getProductId() + " does not exist" );
        }

        if ( product.getMerchantStore().getId().intValue() != store.getId().intValue() )
        {
            throw new Exception( "Item with id " + shoppingCartItem.getProductId() + " does not belong to merchant "
                + store.getId() );
        }

        com.salesmanager.core.model.shoppingcart.ShoppingCartItem item =
            new com.salesmanager.core.model.shoppingcart.ShoppingCartItem( cart, product );
        item.setQuantity( shoppingCartItem.getQuantity() );
        item.setItemPrice( shoppingCartItem.getProductPrice() );
        item.setShoppingCart( cart );

        // attributes
        List<ShoppingCartAttribute> cartAttributes = shoppingCartItem.getShoppingCartAttributes();
        if ( !CollectionUtils.isEmpty( cartAttributes ) )
        {
            Set<com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem> newAttributes =
                new HashSet<com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem>();
            for ( ShoppingCartAttribute attribute : cartAttributes )
            {
                for (ProductAttributeInfo productAttribute : product.getAttributes()) {
                    if (attribute.getAttributeId() == productAttribute.getId()) {
                        com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem attributeItem =
                                new com.salesmanager.core.model.shoppingcart.ShoppingCartAttributeItem( item,
                                        productAttribute );
                        if ( attribute.getAttributeId() > 0 )
                        {
                            attributeItem.setId( attribute.getId() );
                        }
                        item.addAttributes( attributeItem );
                    }
                }
            }
            
            //item.setAttributes( newAttributes );
        }

        return item;

    }




    @Override
    protected ShoppingCart createTarget()
    {
      
        return new ShoppingCart();
    }


	public Customer getCustomer() {
		return customer;
	}


	public void setCustomer(Customer customer) {
		this.customer = customer;
	}


   


   

   

}
