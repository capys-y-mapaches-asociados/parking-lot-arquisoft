import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'barrier',
        data: { pageTitle: 'gatewayApp.barrier.home.title' },
        loadChildren: () => import('./barrier/barrier.module').then(m => m.BarrierModule),
      },
      {
        path: 'customer',
        data: { pageTitle: 'gatewayApp.customer.home.title' },
        loadChildren: () => import('./customer/customer.module').then(m => m.CustomerModule),
      },
      {
        path: 'notification',
        data: { pageTitle: 'gatewayApp.notification.home.title' },
        loadChildren: () => import('./notification/notification.module').then(m => m.NotificationModule),
      },
      {
        path: 'parking-lot',
        data: { pageTitle: 'gatewayApp.parkingLot.home.title' },
        loadChildren: () => import('./parking-lot/parking-lot.module').then(m => m.ParkingLotModule),
      },
      {
        path: 'parking-spot',
        data: { pageTitle: 'gatewayApp.parkingSpot.home.title' },
        loadChildren: () => import('./parking-spot/parking-spot.module').then(m => m.ParkingSpotModule),
      },
      {
        path: 'payment',
        data: { pageTitle: 'gatewayApp.payment.home.title' },
        loadChildren: () => import('./payment/payment.module').then(m => m.PaymentModule),
      },
      {
        path: 'reservation',
        data: { pageTitle: 'gatewayApp.reservation.home.title' },
        loadChildren: () => import('./reservation/reservation.module').then(m => m.ReservationModule),
      },
      {
        path: 'ticket',
        data: { pageTitle: 'gatewayApp.ticket.home.title' },
        loadChildren: () => import('./ticket/ticket.module').then(m => m.TicketModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
