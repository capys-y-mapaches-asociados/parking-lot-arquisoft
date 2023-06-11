import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IParkingLot } from '../parking-lot.model';
import { ParkingLotService } from '../service/parking-lot.service';

@Injectable({ providedIn: 'root' })
export class ParkingLotRoutingResolveService implements Resolve<IParkingLot | null> {
  constructor(protected service: ParkingLotService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IParkingLot | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((parkingLot: HttpResponse<IParkingLot>) => {
          if (parkingLot.body) {
            return of(parkingLot.body);
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
