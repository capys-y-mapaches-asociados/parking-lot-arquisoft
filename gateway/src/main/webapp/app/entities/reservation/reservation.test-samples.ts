import dayjs from 'dayjs/esm';

import { ReservationStatus } from 'app/entities/enumerations/reservation-status.model';

import { IReservation, NewReservation } from './reservation.model';

export const sampleWithRequiredData: IReservation = {
  id: 52422,
  customerId: '43cf7ea3-97b2-484d-8e69-6144cb2cc5d6',
  parkingSpotId: 'dc419f67-bbb3-4ecf-8c03-a4ccf91198a8',
  startTime: dayjs('2023-06-10T06:40'),
  endTime: dayjs('2023-06-10T22:01'),
  status: ReservationStatus['EXPIRED'],
  reservationCode: 'AI-E{10, 14}',
};

export const sampleWithPartialData: IReservation = {
  id: 59091,
  customerId: '8cf0d323-5e6a-48a9-85ce-3ae474669be1',
  parkingSpotId: '1e63c63f-9399-4f94-9e63-b5eee06d3fd1',
  startTime: dayjs('2023-06-10T01:04'),
  endTime: dayjs('2023-06-10T06:26'),
  status: ReservationStatus['ACTIVE'],
  reservationCode: 'FN-6{10, 14}',
};

export const sampleWithFullData: IReservation = {
  id: 48579,
  customerId: 'a73041c6-24f8-4067-9461-d17c952463bb',
  parkingSpotId: '3055601a-6542-4e3e-b6fd-fb3b02ff444c',
  startTime: dayjs('2023-06-10T10:49'),
  endTime: dayjs('2023-06-10T09:13'),
  status: ReservationStatus['ACTIVE'],
  reservationCode: 'TD-c{10, 14}',
};

export const sampleWithNewData: NewReservation = {
  customerId: 'd70f3331-f860-4d2f-ae24-8b8e029c6ae6',
  parkingSpotId: '6baac431-375b-45e6-aaad-1e766af454c9',
  startTime: dayjs('2023-06-10T14:11'),
  endTime: dayjs('2023-06-11T00:49'),
  status: ReservationStatus['PLACED'],
  reservationCode: 'GL-D{10, 14}',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
