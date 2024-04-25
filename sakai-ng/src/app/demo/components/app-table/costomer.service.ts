import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";

@Injectable({
    providedIn: 'root'
})
export class CustomerService {
    constructor(private http: HttpClient) { }

    getCustomers(params?: any) {
        const filters = params.filters;
        Object.keys(params.filters).forEach(key => {
            if(params.filters[key] instanceof Array){
                const filter = params.filters[key][0];
                delete filter['operator']
                params.filters[key]=filter;
            }
        });
        // console.log(keys)
        const paramdata={ lazyEvent: JSON.stringify(params) }
        console.log('params', params)
        return this.http.get<any>('https://www.primefaces.org/data/customers', { params: paramdata }).toPromise();
    }
}


export interface Country {
    name?: string;
    code?: string;
}

export interface Representative {
    name?: string;
    image?: string;
}

export interface Customer {
    id?: number;
    name?: number;
    country?: Country;
    company?: string;
    date?: string;
    status?: string;
    representative?: Representative;
}