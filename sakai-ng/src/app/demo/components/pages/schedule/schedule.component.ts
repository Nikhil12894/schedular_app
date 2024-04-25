import { Component, OnInit } from '@angular/core';
import { Tab } from '@sbzen/ng-cron';
import { MessageService } from 'primeng/api';
import { Table } from 'primeng/table';
import { Product } from 'src/app/demo/api/product';
import { ProductService } from 'src/app/demo/service/product.service';
import { ScheduleDTO, ScheduleDTOList, ScheduleShortBy, Sort } from 'src/domain/schedule';
import { QueryParam } from 'src/domain/webResponse';
import { ScheduleService } from 'src/service/schedule.service';

@Component({
    templateUrl: './schedule.component.html',
    providers: [MessageService],
})
export class ScheduleComponent implements OnInit {
    readonly tabs = [Tab.SECONDS, Tab.MINUTES, Tab.HOURS, Tab.DAY, Tab.MONTH];

    isSearch: boolean = false;

    scheduleDialog: boolean = false;

    deleteScheduleDialog: boolean = false;

    deleteSchedulesDialog: boolean = false;

    schedules: ScheduleDTO[] = [];

    schedule: ScheduleDTO = {};

    selectedSchedules: ScheduleDTO[] = [];

    scheduleDTOList: ScheduleDTOList={};

    submitted: boolean = false;

    cols: any[] = [];

    statuses: any[] = [];

    rowsPerPageOptions = [5, 10, 20];

    queryParam:QueryParam={
        sort_order:Sort.ASC,
        sort_by:ScheduleShortBy.CREATED_AT,
        page:1,
        page_size:10
    }

    constructor(
        private productService: ScheduleService,
        private messageService: MessageService
    ) {}

    ngOnInit() {
        this.productService
            .getSchedule(this.queryParam).subscribe((res) => {
                this.scheduleDTOList = res.data;
                this.schedules = this.scheduleDTOList.scheduleDTOs;
            })
        this.cols = [
            { field: 'schedule_id', header: 'Schedule ID' },
            { field: 'cron_schedule', header: 'Cron Expiration' },
            { field: 'created_by', header: 'Created By' },
            { field: 'creation_date', header: 'Created Date' },
            { field: 'last_updated_by', header: 'Updated By' },
            { field: 'last_update_date', header: 'Updated Date' },
        ];

    }

    openNew() {
        this.schedule = {};
        this.submitted = false;
        this.scheduleDialog = true;
    }

    deleteSelectedSchedules() {
        this.deleteSchedulesDialog = true;
    }

    editProduct(scheduleObj: ScheduleDTO) {
        this.schedule = { ...scheduleObj };
        this.scheduleDialog = true;
    }

    deleteProduct(scheduleObj: ScheduleDTO) {
        this.deleteScheduleDialog = true;
        this.schedule = { ...scheduleObj };
    }

    confirmDeleteSelected() {
        this.deleteSchedulesDialog = false;
        this.schedules = this.schedules.filter(
            (val) => !this.selectedSchedules.includes(val)
        );
        this.messageService.add({
            severity: 'success',
            summary: 'Successful',
            detail: 'Products Deleted',
            life: 3000,
        });
        this.selectedSchedules = [];
    }

    confirmDelete() {
        this.deleteScheduleDialog = false;
        this.schedules = this.schedules.filter(
            (val) => val.id !== this.schedule.id
        );
        this.messageService.add({
            severity: 'success',
            summary: 'Successful',
            detail: 'Product Deleted',
            life: 3000,
        });
        this.schedule = {};
    }

    hideDialog() {
        this.scheduleDialog = false;
        this.submitted = false;
    }

    saveSchedule() {
        this.submitted = true;

            if (this.schedule.id) {
                // @ts-ignore
                this.schedules[this.findIndexById(this.schedule.id)] =
                    this.schedule;
                this.messageService.add({
                    severity: 'success',
                    summary: 'Successful',
                    detail: 'Schedule Updated',
                    life: 3000,
                });
            } else {
                this.schedules.push(this.schedule);
                this.messageService.add({
                    severity: 'success',
                    summary: 'Successful',
                    detail: 'Schedule Created',
                    life: 3000,
                });
            }
            this.schedules = [...this.schedules];
            this.scheduleDialog = false;
            this.schedule = {};
    }

    findIndexById(id: number): number {
        let index = -1;
        for (let i = 0; i < this.schedules.length; i++) {
            if (this.schedules[i].id === id) {
                index = i;
                break;
            }
        }

        return index;
    }

    onGlobalFilter(table: Table, event: Event) {
        table.filterGlobal(
            (event.target as HTMLInputElement).value,
            'contains'
        );
    }
}
