import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IParkingSpot } from '../parking-spot.model';
import { ParkingSpotService } from '../service/parking-spot.service';

@Injectable({ providedIn: 'root' })
export class ParkingSpotRoutingResolveService implements Resolve<IParkingSpot | null> {
  constructor(protected service: ParkingSpotService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IParkingSpot | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((parkingSpot: HttpResponse<IParkingSpot>) => {
          if (parkingSpot.body) {
            return of(parkingSpot.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
