package com.nk.schedular.enums;

public enum TaskShortBy {
	TASK_ID("taskId"),
    DESCRIPTION("description"),
	ID("id"),
	NONE("NONE"),
	CREATED_BY("createdBy"),
    CREATED_AT("createdAt"),
    LAST_UPDATED_BY("lastUpdatedBy"),
    LAST_UPDATED_AT("lastUpdatedAt");

	private String orderBy;

	TaskShortBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getOrderBy() {
		return this.orderBy;
	}
}
