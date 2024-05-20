import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { ScheduleDTOList } from "src/domain/schedule";
import { QueryParam, WebResponse } from "src/domain/webResponse";

@Injectable(
    {
        providedIn:'root'
    }
)
export class ScheduleService {
    
    baseUrl: string = "http://localhost:8080/taskservice";

    constructor(private http: HttpClient) { }


    getSchedule(queryParam:QueryParam):Observable<WebResponse<ScheduleDTOList>> {
        return this.http.get<WebResponse<ScheduleDTOList>>(this.baseUrl + `/api/schedule/all?page=${queryParam.page}&page_size=${queryParam.page_size}&sort_by=${queryParam.sort_by}&sort_order=${queryParam.sort_order}`);
    }
}