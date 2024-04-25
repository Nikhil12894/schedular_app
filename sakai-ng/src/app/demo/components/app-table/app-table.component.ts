import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Customer, CustomerService } from './costomer.service';
import { Table, TableLazyLoadEvent, TableSelectAllChangeEvent } from 'primeng/table';
import { FilterMatchMode, FilterService, SelectItem } from 'primeng/api';

@Component({
    selector: 'app-table',
    templateUrl: 'app-table.component.html'
})
export class AppTableComponent implements OnInit{
    @Input()description: string="Manage Schedules";
    @Input()dataList!: Customer[];

    @Input()tableColumns!: TableColumnDetails[];

    @Input()totalRecords!: number;

    @Input()loading: boolean = false;


    @Input()selectAll: boolean = false;

    @Input()selectedRows!: Customer[];

    @Output() loadDataEvent = new EventEmitter<TableLazyLoadEvent>();
    @Output() onSelectionEvent = new EventEmitter<any[]|null>();
    @Output() onSelectAllEvent = new EventEmitter<TableSelectAllChangeEvent>();

    matchModeOptions: SelectItem[];

    constructor(private customerService: CustomerService, private filterService: FilterService) {
        this.tableColumns = [
            { field: 'name', header: 'Name', isShotEnabled: true, isFilterEnabled: true },
            { field: 'country.name', header: 'Country', isShotEnabled: true },
            { field: 'company', header: 'Company', isShotEnabled: true },
            { field: 'representative.name', header: 'Representative', isShotEnabled: false }
        ]
    }

    ngOnInit() {
        this.loading = true;
        const customFilterName = 'custom-equals';

        this.filterService.register(customFilterName, (value, filter): boolean => {
            if (filter === undefined || filter === null || filter.trim() === '') {
                return true;
            }

            if (value === undefined || value === null) {
                return false;
            }

            return value.toString() === filter.toString();
        });

        this.matchModeOptions = [
            { label: 'Contains', value: FilterMatchMode.CONTAINS }
        ];
    }

    loadData(event: TableLazyLoadEvent) {
        
        this.loadDataEvent.emit(event);
        this.loading = true;
        setTimeout(() => {
            this.customerService.getCustomers(event).then((res) => {
                this.dataList = res.customers;
                this.totalRecords = res.totalRecords;
                this.loading = false;
            });
        }, 1000);
    }

    onSelectionChange(value = []) {
        this.onSelectionEvent.emit(value);
        this.selectAll = value.length === this.totalRecords;
        this.selectedRows = value;
    }

    // onSelectAllChange(event: TableSelectAllChangeEvent) {
    //     const checked = event.checked;
    //     if (checked) {
    //         this.customerService.getCustomers().then((res) => {
    //             this.selectedRows = res.customers;
    //             this.selectAll = true;
    //         });
    //     } else {
    //         this.selectedRows = [];
    //         this.selectAll = false;
    //     }
    // }

    filterValue!: Customer[];
    filterData(event: any) {
        console.log(event)
    }


}



interface TableColumnDetails {
    field?: string;
    header?: string;
    isShotEnabled?: boolean;
    isFilterEnabled?: boolean;
}
