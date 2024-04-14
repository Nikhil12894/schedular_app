package com.nk.schedular.constants;

/**
 * Constants for api implementation.
 */
public class ApiConstants {

    // Private constructor to prevent instantiation
    private ApiConstants() {
        // This constructor is intentionally empty
    }

    public static final Integer DEFAULT_PAGE = 1;

    public static final Integer DEFAULT_PAGE_SIZE = 10;

    public static final String DEFAULT_SORT_CREATED = "CREATED_AT";

    public static final String DEFAULT_SORT_ORDER = "ASC";

    public static final String PAGE = "page";

    public static final String PAGE_SIZE = "page_size";

    public static final String SORT_ORDER = "sort_order";

    public static final String SORT_BY = "sort_by";

    public static final String NO_SORT = "NONE";

    public static final String ATTACHMENT = "attachment";

    public static final String CSV_TEMPLATE_NAME = "template.csv";

    public static final String APPEND = "APPEND";

    public static final String REPLACE = "REPLACE";

    public static final int MAX_SEARCH_CHARS = 3;

    public static final String APPLICATION_CSV = "application/csv";

    public static final String TASK_ID = "task_id";

    public static final String INVALID_TASK_ID = "Invalid task id";

    public static final String SCHEDULE_ID = "schedule_id";

    public static final String INVALID_SCHEDULE_ID = "Invalid schedule id";

    public static final String ID = "ID";

    public static final String SCHEDULE_ALREADY_EXISTS = "Schedule already exists";

    public static final String SCHEDULE_NOT_FOUND = "Schedule not found";

    public static final String CRON_EXPRESSION = "cron_expression";

}