import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IParkingLot } from '../parking-lot.model';

@Component({
  selector: 'jhi-parking-lot-detail',
  templateUrl: './parking-lot-detail.component.html',
})
export class ParkingLotDetailComponent implements OnInit {
  parkingLot: IParkingLot | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ parkingLot }) => {
      this.parkingLot = parkingLot;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
