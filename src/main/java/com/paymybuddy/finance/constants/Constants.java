package com.paymybuddy.finance.constants;

/**
 * @author trimok
 *
 */
public class Constants {
    /**
     * COMMISSION_RATE
     */
    public final static double COMMISSION_RATE = 0.05;

    /**
     * PAY_MY_BUDDY_BANK
     */
    public static final String PAY_MY_BUDDY_BANK = "BUDDY_BANK";
    /**
     * USER_GENERIC_BANK
     */
    public static final String USER_GENERIC_BANK = "USER_BANK";
    /**
     * PAY_MY_BUDDY_GENERIC_USER
     */
    public static final String PAY_MY_BUDDY_GENERIC_USER = "BUDDY";
    /**
     * PAY_MY_BUDDY_GENERIC_USER_EMAIL
     */
    public static final String PAY_MY_BUDDY_GENERIC_USER_EMAIL = "BUDDY_EMAIL";
    /**
     * GENERIC_PASSWORD
     */
    public static final String GENERIC_PASSWORD = "$2y$10$1R.PcD/61WRFZVqngcynOeX/B/9Ia0QZWFbsNOlnlEb48GkSzU5EK";
    /**
     * PAY_MY_BUDDY_GENERIC_USER_PASSWORD_ENCODED
     */
    public static final String PAY_MY_BUDDY_GENERIC_USER_PASSWORD_ENCODED = GENERIC_PASSWORD;
    /**
     * TRANSACTION_COMMISSION_DESCRIPTION
     */
    public static final String TRANSACTION_COMMISSION_DESCRIPTION = "Commission";
    /**
     * AMOUNT_BEGIN
     */
    public static final int AMOUNT_BEGIN = 10000;

    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_OAUTH2_USER = "ROLE_OAUTH2_USER";
    public static final String ROLE_OIDC_USER = "ROLE_OIDC_USER";
}
