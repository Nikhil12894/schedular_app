package com.nk.schedular.enums;

public enum ScheduleShortBy {

    SCHEDULE_ID("scheduleId"),
    CRON_SCHEDULE("cronSchedule"),
    ID("id"),
    CREATED_BY("createdBy"),
    CREATED_AT("createdAt"),
    LAST_UPDATED_BY("lastUpdatedBy"),
    LAST_UPDATED_AT("lastUpdatedAt"),
    NONE("NONE");

    private String orderBy;

	ScheduleShortBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getOrderBy() {
		return this.orderBy;
    }
}
