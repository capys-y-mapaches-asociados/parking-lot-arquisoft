import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IParkingLot, NewParkingLot } from '../parking-lot.model';

export type PartialUpdateParkingLot = Partial<IParkingLot> & Pick<IParkingLot, 'id'>;

export type EntityResponseType = HttpResponse<IParkingLot>;
export type EntityArrayResponseType = HttpResponse<IParkingLot[]>;

@Injectable({ providedIn: 'root' })
export class ParkingLotService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/parking-lots');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(parkingLot: NewParkingLot): Observable<EntityResponseType> {
    return this.http.post<IParkingLot>(this.resourceUrl, parkingLot, { observe: 'response' });
  }

  update(parkingLot: IParkingLot): Observable<EntityResponseType> {
    return this.http.put<IParkingLot>(`${this.resourceUrl}/${this.getParkingLotIdentifier(parkingLot)}`, parkingLot, {
      observe: 'response',
    });
  }

  partialUpdate(parkingLot: PartialUpdateParkingLot): Observable<EntityResponseType> {
    return this.http.patch<IParkingLot>(`${this.resourceUrl}/${this.getParkingLotIdentifier(parkingLot)}`, parkingLot, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IParkingLot>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IParkingLot[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getParkingLotIdentifier(parkingLot: Pick<IParkingLot, 'id'>): number {
    return parkingLot.id;
  }

  compareParkingLot(o1: Pick<IParkingLot, 'id'> | null, o2: Pick<IParkingLot, 'id'> | null): boolean {
    return o1 && o2 ? this.getParkingLotIdentifier(o1) === this.getParkingLotIdentifier(o2) : o1 === o2;
  }

  addParkingLotToCollectionIfMissing<Type extends Pick<IParkingLot, 'id'>>(
    parkingLotCollection: Type[],
    ...parkingLotsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const parkingLots: Type[] = parkingLotsToCheck.filter(isPresent);
    if (parkingLots.length > 0) {
      const parkingLotCollectionIdentifiers = parkingLotCollection.map(parkingLotItem => this.getParkingLotIdentifier(parkingLotItem)!);
      const parkingLotsToAdd = parkingLots.filter(parkingLotItem => {
        const parkingLotIdentifier = this.getParkingLotIdentifier(parkingLotItem);
        if (parkingLotCollectionIdentifiers.includes(parkingLotIdentifier)) {
          return false;
        }
        parkingLotCollectionIdentifiers.push(parkingLotIdentifier);
        return true;
      });
      return [...parkingLotsToAdd, ...parkingLotCollection];
    }
    return parkingLotCollection;
  }
}
