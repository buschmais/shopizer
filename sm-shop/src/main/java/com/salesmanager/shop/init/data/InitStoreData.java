package com.salesmanager.shop.init.data;

import com.salesmanager.common.business.exception.ServiceException;
import com.salesmanager.core.business.services.customer.CustomerService;
import com.salesmanager.core.business.services.merchant.MerchantStoreService;
import com.salesmanager.core.business.services.order.OrderService;
import com.salesmanager.core.business.services.reference.country.CountryService;
import com.salesmanager.core.business.services.reference.currency.CurrencyService;
import com.salesmanager.core.business.services.reference.language.LanguageService;
import com.salesmanager.core.business.services.reference.zone.ZoneService;
import com.salesmanager.core.business.services.tax.TaxClassService;
import com.salesmanager.core.business.services.user.GroupService;
import com.salesmanager.core.model.common.Billing;
import com.salesmanager.core.model.common.Delivery;
import com.salesmanager.core.model.customer.Customer;
import com.salesmanager.core.model.customer.CustomerGender;
import com.salesmanager.core.model.merchant.MerchantStore;
import com.salesmanager.core.model.order.Order;
import com.salesmanager.core.model.order.OrderTotal;
import com.salesmanager.core.model.order.orderproduct.OrderProduct;
import com.salesmanager.core.model.order.orderproduct.OrderProductDownload;
import com.salesmanager.core.model.order.orderproduct.OrderProductPrice;
import com.salesmanager.core.model.order.orderstatus.OrderStatus;
import com.salesmanager.core.model.order.orderstatus.OrderStatusHistory;
import com.salesmanager.core.model.payments.PaymentType;
import com.salesmanager.core.model.reference.country.Country;
import com.salesmanager.core.model.reference.currency.Currency;
import com.salesmanager.core.model.reference.language.Language;
import com.salesmanager.core.model.reference.zone.Zone;
import com.salesmanager.core.model.tax.taxclass.TaxClass;
import com.salesmanager.core.model.user.Group;
import com.salesmanager.core.model.user.GroupType;
import com.salesmanager.common.presentation.constants.Constants;
import com.salesmanager.shop.utils.LocaleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Component
public class InitStoreData implements InitData {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InitStoreData.class);
	
	@Inject
	protected MerchantStoreService merchantService;
	
	@Inject
	protected LanguageService languageService;
	
	@Inject
	protected CountryService countryService;
	
	@Inject
	protected ZoneService zoneService;
	
	@Inject
	protected CustomerService customerService;
	
	@Inject
	protected CurrencyService currencyService;
	
	@Inject
	protected OrderService orderService;
	
	@Inject
	private PasswordEncoder passwordEncoder;
	
	@Inject
	protected GroupService   groupService;
	
	@Autowired
	private TaxClassService taxClassService;

	public void initInitialData() throws ServiceException {
		

		LOGGER.info("Starting the initialization of test data");

		//2 languages by default
		Language en = languageService.getByCode("en");
		Language fr = languageService.getByCode("fr");

		Country canada = countryService.getByCode("CA");
		Zone zone = zoneService.getByCode("QC");
		
		//create a merchant
		MerchantStore store = merchantService.getMerchantStore(MerchantStore.DEFAULT_STORE);

		TaxClass taxClass = this.taxClassService.getByCode(TaxClass.DEFAULT_TAX_CLASS);
		    
		//Create a customer (user name[nick] : shopizer password : password)

		Customer customer = new Customer();
		customer.setMerchantStore(store);
		customer.setEmailAddress("test@shopizer.com");
		customer.setGender(CustomerGender.M);
		customer.setAnonymous(false);
		customer.setCompany("CSTI Consulting");
		customer.setDateOfBirth(new Date());

		customer.setDefaultLanguage(en);
		customer.setNick("shopizer");

		String password = passwordEncoder.encode("password");
		customer.setPassword(password);

		List<Group> groups = groupService.listGroup(GroupType.CUSTOMER);


		for(Group group : groups) {
			  if(group.getGroupName().equals(Constants.GROUP_CUSTOMER)) {
				  customer.getGroups().add(group);
			  }
		}

		Delivery delivery = new Delivery();
		delivery.setAddress("358 Du Languadoc");
		delivery.setCity( "Boucherville" );
		delivery.setCountry(canada);
//		    delivery.setCountryCode(canada.getIsoCode());
		delivery.setFirstName("Leonardo" );
		delivery.setLastName("DiCaprio" );
		delivery.setPostalCode("J4B-8J9" );
		delivery.setZone(zone);

		Billing billing = new Billing();
		billing.setAddress("358 Du Languadoc");
		billing.setCity("Boucherville");
		billing.setCompany("CSTI Consulting");
		billing.setCountry(canada);
//		    billing.setCountryCode(canada.getIsoCode());
		billing.setFirstName("Leonardo" );
		billing.setLastName("DiCaprio" );
		billing.setPostalCode("J4B-8J9");
		billing.setZone(zone);

		customer.setBilling(billing);
		customer.setDelivery(delivery);
		customerService.create(customer);

		Currency currency = currencyService.getByCode("CAD");

		OrderStatusHistory orderStatusHistory = new OrderStatusHistory();

		//create an order

		Order order = new Order();
		order.setDatePurchased(new Date());
		order.setCurrency(currency);
		order.setLastModified(new Date());
		order.setBilling(billing);


		order.setLocale(LocaleUtils.getLocale(store));

		order.setCurrencyValue(new BigDecimal(0.98));//compared to based currency (not necessary)
		order.setCustomerId(customer.getId());
		order.setBilling(billing);
		order.setDelivery(delivery);
		order.setCustomerEmailAddress("leo@shopizer.com");
		order.setDelivery(delivery);
		order.setIpAddress("ipAddress" );
		order.setMerchant(store);
		order.setOrderDateFinished(new Date());//committed date

		orderStatusHistory.setComments("We received your order");
		orderStatusHistory.setCustomerNotified(1);
		orderStatusHistory.setStatus(OrderStatus.ORDERED);
		orderStatusHistory.setDateAdded(new Date() );
		orderStatusHistory.setOrder(order);
		order.getOrderHistory().add( orderStatusHistory );


		order.setPaymentType(PaymentType.PAYPAL);
		order.setPaymentModuleCode("paypal");
		order.setStatus( OrderStatus.DELIVERED);
		order.setTotal(new BigDecimal(23.99));


		//OrderProductDownload - Digital download
		OrderProductDownload orderProductDownload = new OrderProductDownload();
		orderProductDownload.setDownloadCount(1);
		orderProductDownload.setMaxdays(31);
		orderProductDownload.setOrderProductFilename("Your digital file name");

		//OrderProductPrice
		OrderProductPrice oproductprice = new OrderProductPrice();
		oproductprice.setDefaultPrice(true);
		oproductprice.setProductPrice(new BigDecimal(19.99) );
		oproductprice.setProductPriceCode("baseprice" );
		oproductprice.setProductPriceName("Base Price" );
		//oproductprice.setProductPriceSpecialAmount(new BigDecimal(13.99) );


		//OrderProduct
		OrderProduct oproduct = new OrderProduct();
		oproduct.getDownloads().add( orderProductDownload);
		oproduct.setOneTimeCharge( new BigDecimal(19.99) );
		oproduct.setOrder(order);
		oproduct.setProductName( "Product name" );
		oproduct.setProductQuantity(1);
		oproduct.setSku("TB12345" );
		oproduct.getPrices().add(oproductprice ) ;

		oproductprice.setOrderProduct(oproduct);
		orderProductDownload.setOrderProduct(oproduct);
		order.getOrderProducts().add(oproduct);

		//OrderTotal
		OrderTotal subtotal = new OrderTotal();
		subtotal.setModule("summary" );
		subtotal.setSortOrder(0);
		subtotal.setText("Summary" );
		subtotal.setTitle("Summary" );
		subtotal.setOrderTotalCode("subtotal");
		subtotal.setValue(new BigDecimal(19.99 ) );
		subtotal.setOrder(order);

		order.getOrderTotal().add(subtotal);

		OrderTotal tax = new OrderTotal();
		tax.setModule("tax" );
		tax.setSortOrder(1);
		tax.setText("Tax" );
		tax.setTitle("Tax" );
		tax.setOrderTotalCode("tax");
		tax.setValue(new BigDecimal(4) );
		tax.setOrder(order);

		order.getOrderTotal().add(tax);

		OrderTotal total = new OrderTotal();
		total.setModule("total" );
		total.setSortOrder(2);
		total.setText("Total" );
		total.setTitle("Total" );
		total.setOrderTotalCode("total");
		total.setValue(new BigDecimal(23.99) );
		total.setOrder(order);

		order.getOrderTotal().add(total);

		orderService.create(order);

		LOGGER.info("Ending the initialization of test data");
		
	}

}
