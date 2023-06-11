import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ParkingLotComponent } from '../list/parking-lot.component';
import { ParkingLotDetailComponent } from '../detail/parking-lot-detail.component';
import { ParkingLotUpdateComponent } from '../update/parking-lot-update.component';
import { ParkingLotRoutingResolveService } from './parking-lot-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const parkingLotRoute: Routes = [
  {
    path: '',
    component: ParkingLotComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ParkingLotDetailComponent,
    resolve: {
      parkingLot: ParkingLotRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ParkingLotUpdateComponent,
    resolve: {
      parkingLot: ParkingLotRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ParkingLotUpdateComponent,
    resolve: {
      parkingLot: ParkingLotRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(parkingLotRoute)],
  exports: [RouterModule],
})
export class ParkingLotRoutingModule {}
