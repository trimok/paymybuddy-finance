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

    public static final String AUTHORITY_USER = "AUTHORITY_USER";
    public static final String AUTHORITY_OAUTH2_USER = "AUTHORITY_OAUTH2_USER";
    public static final String AUTHORITY_OIDC_USER = "AUTHORITY_OIDC_USER";

    /**
     * ERROR_SELECT_ACCOUNT_TO_REMOVE
     */
    public static final String ERROR_SELECT_ACCOUNT_TO_REMOVE = "error_selectAccountToRemove";

    /**
     * ERROR_ACCOUNT_ALREADY_EXISTS
     */
    public static final String ERROR_ACCOUNT_ALREADY_EXISTS = "error_accountAlreadyExists";

    /**
     * ERROR_SELECT_ACCOUNT_TO_ADD
     */
    public static final String ERROR_SELECT_ACCOUNT_TO_ADD = "error_selectAccountToAdd";

    /**
     * ERROR_ORIGIN_ACCOUNT_AMOUNT_NOT_SUFFICIENT
     */
    public static final String ERROR_ORIGIN_ACCOUNT_AMOUNT_NOT_SUFFICIENT = "error_originAccountAmountNotSufficient";

    /**
     * ERROR_TRANSACTION_MUST_BE_FROM_BUDDY_ACCOUNT
     */
    public static final String ERROR_TRANSACTION_MUST_BE_FROM_BUDDY_ACCOUNT = "error_transactionMustBeFromBuddyAccount";

    /**
     * ERROR_ACCOUNTS_MUST_BE_DIFFERENT
     */
    public static final String ERROR_ACCOUNTS_MUST_BE_DIFFERENT = "error_accountsMustBeDifferent";

    /**
     * ERROR_SELECT_ACCOUNT_TO
     */
    public static final String ERROR_SELECT_ACCOUNT_TO = "error_selectAccountTo";

    /**
     * ERROR_SELECT_ACCOUNT_FROM
     */
    public static final String ERROR_SELECT_ACCOUNT_FROM = "error_selectAccountFrom";

}
