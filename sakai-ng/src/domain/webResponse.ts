import { ScheduleShortBy, Sort } from "./schedule";

export interface WebResponse<T> {
    status: number;
    data: T;
    message: string;
}

export interface QueryParam {
    sort_order: Sort,
    sort_by: ScheduleShortBy,
    page?: number,
    page_size?: number
}