package com.library.constants;

public final class AppConstants {
    public static final int DEFAULT_BORROW_LIMIT = 5;
    public static final int DEFAULT_LOAN_DAYS = 14;
    public static final double DEFAULT_FINE_PER_DAY = 1.0;

    public static final String CONFIG_MAX_BOOKS = "max.books.per.patron";
    public static final String CONFIG_LOAN_DAYS = "loan.period.days";
    public static final String CONFIG_FINE_PER_DAY = "fine.per.day";
    public static final String CONFIG_EMAIL_NOTIFICATIONS = "email.notifications";

    public static final String LOG_FILE = "library.log";

    private AppConstants() {
    }
}
