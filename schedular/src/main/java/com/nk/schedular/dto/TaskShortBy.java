package com.nk.schedular.dto;

public enum TaskShortBy {
	NAME("taskId"),
    DESCRIPTION("description"),
	ID("id"),
	NONE("NONE");

	private String orderBy;

	TaskShortBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getOrderBy() {
		return this.orderBy;
	}
}
