import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ParkingSpotComponent } from './list/parking-spot.component';
import { ParkingSpotDetailComponent } from './detail/parking-spot-detail.component';
import { ParkingSpotUpdateComponent } from './update/parking-spot-update.component';
import { ParkingSpotDeleteDialogComponent } from './delete/parking-spot-delete-dialog.component';
import { ParkingSpotRoutingModule } from './route/parking-spot-routing.module';

@NgModule({
  imports: [SharedModule, ParkingSpotRoutingModule],
  declarations: [ParkingSpotComponent, ParkingSpotDetailComponent, ParkingSpotUpdateComponent, ParkingSpotDeleteDialogComponent],
})
export class ParkingSpotModule {}
