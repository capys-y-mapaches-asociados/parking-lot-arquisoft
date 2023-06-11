import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IBarrier } from '../barrier.model';
import { BarrierService } from '../service/barrier.service';

@Injectable({ providedIn: 'root' })
export class BarrierRoutingResolveService implements Resolve<IBarrier | null> {
  constructor(protected service: BarrierService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBarrier | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((barrier: HttpResponse<IBarrier>) => {
          if (barrier.body) {
            return of(barrier.body);
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
