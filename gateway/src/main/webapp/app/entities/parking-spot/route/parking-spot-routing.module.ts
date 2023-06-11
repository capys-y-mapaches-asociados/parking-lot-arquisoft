import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ParkingSpotComponent } from '../list/parking-spot.component';
import { ParkingSpotDetailComponent } from '../detail/parking-spot-detail.component';
import { ParkingSpotUpdateComponent } from '../update/parking-spot-update.component';
import { ParkingSpotRoutingResolveService } from './parking-spot-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const parkingSpotRoute: Routes = [
  {
    path: '',
    component: ParkingSpotComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ParkingSpotDetailComponent,
    resolve: {
      parkingSpot: ParkingSpotRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ParkingSpotUpdateComponent,
    resolve: {
      parkingSpot: ParkingSpotRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ParkingSpotUpdateComponent,
    resolve: {
      parkingSpot: ParkingSpotRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(parkingSpotRoute)],
  exports: [RouterModule],
})
export class ParkingSpotRoutingModule {}
