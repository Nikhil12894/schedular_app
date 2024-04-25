import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ScheduleComponent } from './schedule.component';

@NgModule({
	imports: [RouterModule.forChild([
		{ path: '', component: ScheduleComponent }
	])],
	exports: [RouterModule]
})
export class ScheduleRoutingModule { }
