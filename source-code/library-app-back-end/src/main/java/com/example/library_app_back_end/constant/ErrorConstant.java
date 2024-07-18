package com.example.library_app_back_end.constant;

public class ErrorConstant {
    /**
     *  over length field
     */
    public static final String OVER_LENGTH_FIELD = "OVER_LENGTH_FIELD";
    public static final String OVER_LENGTH_FIELD_LABEL = "%s can not excess %d characters";

    /**
     * token
     */
    public static final String ERROR_TOKEN = "ERROR_TOKEN";
    public static final String MISSING_ADMIN_EMAIL = "Admin email is missing";
    public static final String MISSING_USER_EMAIL = "User email is missing";

    /**
     * BLANK FIELD
     */
    public static final String BLANK_FIELD = "BLANK_FIELD";
    public static final String BLANK_FIELD_LABEL = "%s must not be empty";

    /**
     * OBJECT
     */
    public static final String EXIST_OBJECT = "EXIST_OBJECT";
    public static final String EXISTED_OBJECT_LABEL = "%s is exist";
    public static final String NOT_FOUND_OBJECT = "NOT_FOUND_OBJECT";
    public static final String NOT_FOUND_OBJECT_LABEL = "%s is not found";

    /**
     * Book
     */

    public static final String BOOK = "book";
    public static final String INVALID_CHECKOUT = "INVALID_CHECKOUT";
    public static final String INVALID_CHECKOUT_LABEL = "Book doesn't exist or already checked out by user";
    public static final String BOOK_NOT_EXIST_CHECKOUT = "book is not checked out";
    public  static final String BOOK_QUANTITY_LOCKED = "book quantity is locked";

    /**
     * Payment
     */
    public static final String PAYMENT = "payment";

    public static final String PARSE_ERROR = "PARSE_ERROR";
    public static final String PARSE_ERROR_LABEL = "There is error during parsing";

    /**
     * Fees Outstanding
     */
    public static final String OUTSTANDING_FEES = "OUTSTANDING_FEES";
    public static final String OUTSTANDING_FEES_LABEL = "there are outstanding fees";

    /**
     * Message
     */
    public static final String ANSWERED_MESSAGE = "ANSWERED_MESSAGE";
    public static final String ANSWERED_MESSAGE_LABEL = "message is already answered";


}
