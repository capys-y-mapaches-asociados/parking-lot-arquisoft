import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { IParkingSpot, NewParkingSpot } from '../parking-spot.model';

export type PartialUpdateParkingSpot = Partial<IParkingSpot> & Pick<IParkingSpot, 'id'>;

export type EntityResponseType = HttpResponse<IParkingSpot>;
export type EntityArrayResponseType = HttpResponse<IParkingSpot[]>;

@Injectable({ providedIn: 'root' })
export class ParkingSpotService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/parking-spots');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/parking-spots');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(parkingSpot: NewParkingSpot): Observable<EntityResponseType> {
    return this.http.post<IParkingSpot>(this.resourceUrl, parkingSpot, { observe: 'response' });
  }

  update(parkingSpot: IParkingSpot): Observable<EntityResponseType> {
    return this.http.put<IParkingSpot>(`${this.resourceUrl}/${this.getParkingSpotIdentifier(parkingSpot)}`, parkingSpot, {
      observe: 'response',
    });
  }

  partialUpdate(parkingSpot: PartialUpdateParkingSpot): Observable<EntityResponseType> {
    return this.http.patch<IParkingSpot>(`${this.resourceUrl}/${this.getParkingSpotIdentifier(parkingSpot)}`, parkingSpot, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IParkingSpot>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IParkingSpot[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IParkingSpot[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getParkingSpotIdentifier(parkingSpot: Pick<IParkingSpot, 'id'>): number {
    return parkingSpot.id;
  }

  compareParkingSpot(o1: Pick<IParkingSpot, 'id'> | null, o2: Pick<IParkingSpot, 'id'> | null): boolean {
    return o1 && o2 ? this.getParkingSpotIdentifier(o1) === this.getParkingSpotIdentifier(o2) : o1 === o2;
  }

  addParkingSpotToCollectionIfMissing<Type extends Pick<IParkingSpot, 'id'>>(
    parkingSpotCollection: Type[],
    ...parkingSpotsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const parkingSpots: Type[] = parkingSpotsToCheck.filter(isPresent);
    if (parkingSpots.length > 0) {
      const parkingSpotCollectionIdentifiers = parkingSpotCollection.map(
        parkingSpotItem => this.getParkingSpotIdentifier(parkingSpotItem)!
      );
      const parkingSpotsToAdd = parkingSpots.filter(parkingSpotItem => {
        const parkingSpotIdentifier = this.getParkingSpotIdentifier(parkingSpotItem);
        if (parkingSpotCollectionIdentifiers.includes(parkingSpotIdentifier)) {
          return false;
        }
        parkingSpotCollectionIdentifiers.push(parkingSpotIdentifier);
        return true;
      });
      return [...parkingSpotsToAdd, ...parkingSpotCollection];
    }
    return parkingSpotCollection;
  }
}
