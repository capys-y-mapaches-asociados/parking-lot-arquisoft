import dayjs from 'dayjs/esm';

import { ReservationStatus } from 'app/entities/enumerations/reservation-status.model';

import { IReservation, NewReservation } from './reservation.model';

export const sampleWithRequiredData: IReservation = {
  id: 52422,
  parkingSpotId: '43cf7ea3-97b2-484d-8e69-6144cb2cc5d6',
  startTime: dayjs('2023-06-10T04:48'),
  endTime: dayjs('2023-06-10T06:46'),
  status: ReservationStatus['ACTIVE'],
  reservationCode: 'PY-c{10, 14}',
};

export const sampleWithPartialData: IReservation = {
  id: 46181,
  parkingSpotId: 'bbb3ecf4-c03a-44cc-b911-98a8c1a20539',
  startTime: dayjs('2023-06-10T12:16'),
  endTime: dayjs('2023-06-10T06:12'),
  status: ReservationStatus['CANCELLED'],
  reservationCode: 'WF-D{10, 14}',
};

export const sampleWithFullData: IReservation = {
  id: 19701,
  parkingSpotId: '5e6a8a90-5ce3-4ae4-b466-9be11e63c63f',
  startTime: dayjs('2023-06-10T10:19'),
  endTime: dayjs('2023-06-10T19:17'),
  status: ReservationStatus['EXPIRED'],
  reservationCode: 'YP-F{10, 14}',
};

export const sampleWithNewData: NewReservation = {
  parkingSpotId: 'de63b5ee-e06d-43fd-9fc7-238d7a73041c',
  startTime: dayjs('2023-06-10T15:07'),
  endTime: dayjs('2023-06-10T21:06'),
  status: ReservationStatus['ACTIVE'],
  reservationCode: 'NA-c{10, 14}',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
