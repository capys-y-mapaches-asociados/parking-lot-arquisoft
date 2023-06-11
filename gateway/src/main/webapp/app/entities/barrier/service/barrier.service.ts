import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IBarrier, NewBarrier } from '../barrier.model';

export type PartialUpdateBarrier = Partial<IBarrier> & Pick<IBarrier, 'id'>;

export type EntityResponseType = HttpResponse<IBarrier>;
export type EntityArrayResponseType = HttpResponse<IBarrier[]>;

@Injectable({ providedIn: 'root' })
export class BarrierService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/barriers');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/barriers');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(barrier: NewBarrier): Observable<EntityResponseType> {
    return this.http.post<IBarrier>(this.resourceUrl, barrier, { observe: 'response' });
  }

  update(barrier: IBarrier): Observable<EntityResponseType> {
    return this.http.put<IBarrier>(`${this.resourceUrl}/${this.getBarrierIdentifier(barrier)}`, barrier, { observe: 'response' });
  }

  partialUpdate(barrier: PartialUpdateBarrier): Observable<EntityResponseType> {
    return this.http.patch<IBarrier>(`${this.resourceUrl}/${this.getBarrierIdentifier(barrier)}`, barrier, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IBarrier>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IBarrier[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IBarrier[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getBarrierIdentifier(barrier: Pick<IBarrier, 'id'>): number {
    return barrier.id;
  }

  compareBarrier(o1: Pick<IBarrier, 'id'> | null, o2: Pick<IBarrier, 'id'> | null): boolean {
    return o1 && o2 ? this.getBarrierIdentifier(o1) === this.getBarrierIdentifier(o2) : o1 === o2;
  }

  addBarrierToCollectionIfMissing<Type extends Pick<IBarrier, 'id'>>(
    barrierCollection: Type[],
    ...barriersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const barriers: Type[] = barriersToCheck.filter(isPresent);
    if (barriers.length > 0) {
      const barrierCollectionIdentifiers = barrierCollection.map(barrierItem => this.getBarrierIdentifier(barrierItem)!);
      const barriersToAdd = barriers.filter(barrierItem => {
        const barrierIdentifier = this.getBarrierIdentifier(barrierItem);
        if (barrierCollectionIdentifiers.includes(barrierIdentifier)) {
          return false;
        }
        barrierCollectionIdentifiers.push(barrierIdentifier);
        return true;
      });
      return [...barriersToAdd, ...barrierCollection];
    }
    return barrierCollection;
  }
}
