import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { BarrierComponent } from '../list/barrier.component';
import { BarrierDetailComponent } from '../detail/barrier-detail.component';
import { BarrierUpdateComponent } from '../update/barrier-update.component';
import { BarrierRoutingResolveService } from './barrier-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const barrierRoute: Routes = [
  {
    path: '',
    component: BarrierComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BarrierDetailComponent,
    resolve: {
      barrier: BarrierRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BarrierUpdateComponent,
    resolve: {
      barrier: BarrierRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BarrierUpdateComponent,
    resolve: {
      barrier: BarrierRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(barrierRoute)],
  exports: [RouterModule],
})
export class BarrierRoutingModule {}
