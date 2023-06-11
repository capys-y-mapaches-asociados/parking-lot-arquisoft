import { PaymentStatus } from 'app/entities/enumerations/payment-status.model';
import { PaymentMethod } from 'app/entities/enumerations/payment-method.model';

import { IPayment, NewPayment } from './payment.model';

export const sampleWithRequiredData: IPayment = {
  id: 47537,
  customerId: 'bb75e764-58a7-4ee9-bc22-72eb2852f49a',
  amount: 695,
  paymentStatus: PaymentStatus['REFUNDED'],
  paymentMethod: PaymentMethod['CARD'],
};

export const sampleWithPartialData: IPayment = {
  id: 59959,
  customerId: 'e89be337-efa6-4a0d-b09c-e2dc746aa30f',
  reservationID: '9542c264-08ac-4410-8ba4-c37e9ba3dbbb',
  amount: 160,
  paymentStatus: PaymentStatus['PENDING'],
  paymentMethod: PaymentMethod['TRANSFERENCE'],
};

export const sampleWithFullData: IPayment = {
  id: 34238,
  customerId: '0aa778d3-5e6b-403e-a65f-a7a5b3e6cd53',
  reservationID: '95946345-0cf4-4394-8261-ba6a6eb1a825',
  amount: 855,
  paymentStatus: PaymentStatus['RECEIVED'],
  paymentMethod: PaymentMethod['CARD'],
};

export const sampleWithNewData: NewPayment = {
  customerId: '51f6c826-a006-4cd4-ae2b-6b34157a8edd',
  amount: 440,
  paymentStatus: PaymentStatus['PENDING'],
  paymentMethod: PaymentMethod['CARD'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
