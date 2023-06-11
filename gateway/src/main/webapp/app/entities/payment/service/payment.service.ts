import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPayment, NewPayment } from '../payment.model';

export type PartialUpdatePayment = Partial<IPayment> & Pick<IPayment, 'id'>;

export type EntityResponseType = HttpResponse<IPayment>;
export type EntityArrayResponseType = HttpResponse<IPayment[]>;

@Injectable({ providedIn: 'root' })
export class PaymentService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/payments');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(payment: NewPayment): Observable<EntityResponseType> {
    return this.http.post<IPayment>(this.resourceUrl, payment, { observe: 'response' });
  }

  update(payment: IPayment): Observable<EntityResponseType> {
    return this.http.put<IPayment>(`${this.resourceUrl}/${this.getPaymentIdentifier(payment)}`, payment, { observe: 'response' });
  }

  partialUpdate(payment: PartialUpdatePayment): Observable<EntityResponseType> {
    return this.http.patch<IPayment>(`${this.resourceUrl}/${this.getPaymentIdentifier(payment)}`, payment, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IPayment>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IPayment[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getPaymentIdentifier(payment: Pick<IPayment, 'id'>): number {
    return payment.id;
  }

  comparePayment(o1: Pick<IPayment, 'id'> | null, o2: Pick<IPayment, 'id'> | null): boolean {
    return o1 && o2 ? this.getPaymentIdentifier(o1) === this.getPaymentIdentifier(o2) : o1 === o2;
  }

  addPaymentToCollectionIfMissing<Type extends Pick<IPayment, 'id'>>(
    paymentCollection: Type[],
    ...paymentsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const payments: Type[] = paymentsToCheck.filter(isPresent);
    if (payments.length > 0) {
      const paymentCollectionIdentifiers = paymentCollection.map(paymentItem => this.getPaymentIdentifier(paymentItem)!);
      const paymentsToAdd = payments.filter(paymentItem => {
        const paymentIdentifier = this.getPaymentIdentifier(paymentItem);
        if (paymentCollectionIdentifiers.includes(paymentIdentifier)) {
          return false;
        }
        paymentCollectionIdentifiers.push(paymentIdentifier);
        return true;
      });
      return [...paymentsToAdd, ...paymentCollection];
    }
    return paymentCollection;
  }
}
