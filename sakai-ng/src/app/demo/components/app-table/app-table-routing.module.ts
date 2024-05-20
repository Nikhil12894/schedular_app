import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AppTableComponent } from './app-table.component';

@NgModule({
	imports: [RouterModule.forChild([
		{ path: '', component: AppTableComponent }
	])],
	exports: [RouterModule]
})
export class AppTableRoutingModule { }
