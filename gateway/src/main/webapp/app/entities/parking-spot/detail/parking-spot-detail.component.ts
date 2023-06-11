import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IParkingSpot } from '../parking-spot.model';

@Component({
  selector: 'jhi-parking-spot-detail',
  templateUrl: './parking-spot-detail.component.html',
})
export class ParkingSpotDetailComponent implements OnInit {
  parkingSpot: IParkingSpot | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ parkingSpot }) => {
      this.parkingSpot = parkingSpot;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
