import { PaymentStatus } from 'app/entities/enumerations/payment-status.model';
import { PaymentMethod } from 'app/entities/enumerations/payment-method.model';

import { IPayment, NewPayment } from './payment.model';

export const sampleWithRequiredData: IPayment = {
  id: 47537,
  customerId: 72336,
  amount: 744,
  paymentStatus: PaymentStatus['PLACED'],
  paymentMethod: PaymentMethod['CASH'],
};

export const sampleWithPartialData: IPayment = {
  id: 46452,
  customerId: 39655,
  reservationID: 28713,
  amount: 320,
  paymentStatus: PaymentStatus['RECEIVED'],
  paymentMethod: PaymentMethod['CASH'],
};

export const sampleWithFullData: IPayment = {
  id: 44057,
  customerId: 89152,
  reservationID: 90810,
  amount: 602,
  paymentStatus: PaymentStatus['PLACED'],
  paymentMethod: PaymentMethod['TRANSFERENCE'],
};

export const sampleWithNewData: NewPayment = {
  customerId: 12909,
  amount: 137,
  paymentStatus: PaymentStatus['PLACED'],
  paymentMethod: PaymentMethod['CARD'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
