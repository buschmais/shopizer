package com.salesmanager.core.business.utils;

import com.salesmanager.common.business.constants.Constants;
import com.salesmanager.core.model.merchant.MerchantStore;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.BigDecimalValidator;
import org.apache.commons.validator.routines.CurrencyValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PriceUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductUtils.class);

    private final static char DECIMALCOUNT = '2';
    private final static char DECIMALPOINT = '.';
    private final static char THOUSANDPOINT = ',';

    /**
     * This method will return the required formated amount
     * with the appropriate currency
     * @param store
     * @param amount
     * @return
     * @throws Exception
     */
    public String getAdminFormattedAmountWithCurrency(MerchantStore store, BigDecimal amount) throws Exception {
        if(amount==null) {
            return "";
        }

        NumberFormat nf = null;

        Currency currency = store.getCurrency().getCurrency();
        nf = NumberFormat.getInstance(Constants.DEFAULT_LOCALE);
        nf.setMaximumFractionDigits(Integer.parseInt(Character
                .toString(DECIMALCOUNT)));
        nf.setMinimumFractionDigits(Integer.parseInt(Character
                .toString(DECIMALCOUNT)));
        nf.setCurrency(currency);

        return nf.format(amount);
    }

    /**
     * This is the format that will be displayed
     * in the admin input text fields when editing
     * an entity having a BigDecimal to be displayed
     * as a raw amount 1,299.99
     * The admin user will also be force to input
     * the amount using that format
     * @param amount
     * @return
     * @throws Exception
     */
    public String getAdminFormattedAmount(BigDecimal amount) throws Exception {

        if(amount==null) {
            return "";
        }

        NumberFormat nf = null;

        nf = NumberFormat.getInstance(Constants.DEFAULT_LOCALE);

        nf.setMaximumFractionDigits(Integer.parseInt(Character
                .toString(DECIMALCOUNT)));
        nf.setMinimumFractionDigits(Integer.parseInt(Character
                .toString(DECIMALCOUNT)));

        return nf.format(amount);
    }

    /**
     * This method has to be used to format store front amounts
     * It will display national format amount ex:
     * $1,345.99
     * Rs.1.345.99
     * or international format
     * USD1,345.79
     * INR1,345.79
     * @param store
     * @param amount
     * @return String
     * @throws Exception
     */
    public String getStoreFormattedAmountWithCurrency(MerchantStore store, BigDecimal amount) throws Exception {
        if(amount==null) {
            return "";
        }

        Currency currency = Constants.DEFAULT_CURRENCY;
        Locale locale = Constants.DEFAULT_LOCALE;

        try {

            currency = store.getCurrency().getCurrency();
            locale = new Locale(store.getDefaultLanguage().getCode(),store.getCountry().getIsoCode());
        } catch (Exception e) {
            LOGGER.error("Cannot create currency or locale instance for store " + store.getCode());
        }

        NumberFormat currencyInstance = null;

        if(store.isCurrencyFormatNational()) {
            currencyInstance = NumberFormat.getCurrencyInstance(locale);//national
        } else {
            currencyInstance = NumberFormat.getCurrencyInstance();//international
        }
        currencyInstance.setCurrency(currency);


        return currencyInstance.format(amount.doubleValue());
    }

    /**
     * Transformation of an amount of money submited by the admin
     * user to be inserted as a BigDecimal in the database
     * @param amount
     * @param locale
     * @return
     * @throws Exception
     */
    public BigDecimal getAmount(String amount) throws Exception {

        // validations
        /**
         * 1) remove decimal and thousand
         *
         * String.replaceAll(decimalPoint, ""); String.replaceAll(thousandPoint,
         * "");
         *
         * Should be able to parse to Integer
         */
        StringBuffer newAmount = new StringBuffer();
        for (int i = 0; i < amount.length(); i++) {
            if (amount.charAt(i) != DECIMALPOINT
                    && amount.charAt(i) != THOUSANDPOINT) {
                newAmount.append(amount.charAt(i));
            }
        }

        try {
            Integer.parseInt(newAmount.toString());
        } catch (Exception e) {
            throw new Exception("Cannot parse " + amount);
        }

        if (!amount.contains(Character.toString(DECIMALPOINT))
                && !amount.contains(Character.toString(THOUSANDPOINT))
                && !amount.contains(" ")) {

            if (matchPositiveInteger(amount)) {
                BigDecimalValidator validator = CurrencyValidator.getInstance();
                BigDecimal bdamount = validator.validate(amount, Locale.US);
                if (bdamount == null) {
                    throw new Exception("Cannot parse " + amount);
                } else {
                    return bdamount;
                }
            } else {
                throw new Exception("Not a positive integer "
                        + amount);
            }

        } else {
            //TODO should not go this path in this current release
            StringBuffer pat = new StringBuffer();

            if (!StringUtils.isBlank(Character.toString(THOUSANDPOINT))) {
                pat.append("\\d{1,3}(" + THOUSANDPOINT + "?\\d{3})*");
            }

            pat.append("(\\" + DECIMALPOINT + "\\d{1," + DECIMALCOUNT + "})");

            Pattern pattern = Pattern.compile(pat.toString());
            Matcher matcher = pattern.matcher(amount);

            if (matcher.matches()) {

                Locale locale = Constants.DEFAULT_LOCALE;
                //TODO validate amount using old test case
                if (DECIMALPOINT == ',') {
                    locale = Locale.GERMAN;
                }

                BigDecimalValidator validator = CurrencyValidator.getInstance();
                BigDecimal bdamount = validator.validate(amount, locale);

                return bdamount;
            } else {
                throw new Exception("Cannot parse " + amount);
            }
        }
    }

    private boolean matchPositiveInteger(String amount) {

        Pattern pattern = Pattern.compile("^[+]?\\d*$");
        Matcher matcher = pattern.matcher(amount);
        if (matcher.matches()) {
            return true;

        } else {
            return false;
        }
    }
}
