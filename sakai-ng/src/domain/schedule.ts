export interface ScheduleDTO{
    created_by?: number,
    creation_date?: Date,
    last_updated_by?: number,
    last_update_date?: Date,
    id?: number,
    schedule_id?: string,
    cron_schedule?: string
};

export interface ScheduleDTOList{
    scheduleDTOs?: ScheduleDTO[],
    total_pages?: number,
	total?: number,
	sort_order?:Sort,
	sort_by?: ScheduleShortBy
}

export enum Sort{
    ASC = 'ASC',
    DESC = 'DESC'
}

export enum ScheduleShortBy{
    ID = 'ID',
    CREATED_BY = 'CREATED_BY',
    CREATED_AT = 'CREATED_AT',
    LAST_UPDATED_BY = 'LAST_UPDATED_BY',
    LAST_UPDATED_AT = 'LAST_UPDATED_AT',
    SCHEDULE_ID = 'SCHEDULE_ID',
    CRON_SCHEDULE = 'CRON_SCHEDULE',
    NONE='NONE'
}

