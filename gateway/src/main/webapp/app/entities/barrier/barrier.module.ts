import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { BarrierComponent } from './list/barrier.component';
import { BarrierDetailComponent } from './detail/barrier-detail.component';
import { BarrierUpdateComponent } from './update/barrier-update.component';
import { BarrierDeleteDialogComponent } from './delete/barrier-delete-dialog.component';
import { BarrierRoutingModule } from './route/barrier-routing.module';

@NgModule({
  imports: [SharedModule, BarrierRoutingModule],
  declarations: [BarrierComponent, BarrierDetailComponent, BarrierUpdateComponent, BarrierDeleteDialogComponent],
})
export class BarrierModule {}
