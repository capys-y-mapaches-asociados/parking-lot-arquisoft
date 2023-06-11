import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ParkingLotComponent } from './list/parking-lot.component';
import { ParkingLotDetailComponent } from './detail/parking-lot-detail.component';
import { ParkingLotUpdateComponent } from './update/parking-lot-update.component';
import { ParkingLotDeleteDialogComponent } from './delete/parking-lot-delete-dialog.component';
import { ParkingLotRoutingModule } from './route/parking-lot-routing.module';

@NgModule({
  imports: [SharedModule, ParkingLotRoutingModule],
  declarations: [ParkingLotComponent, ParkingLotDetailComponent, ParkingLotUpdateComponent, ParkingLotDeleteDialogComponent],
})
export class ParkingLotModule {}
