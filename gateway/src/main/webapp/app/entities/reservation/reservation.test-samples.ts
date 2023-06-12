import dayjs from 'dayjs/esm';

import { ReservationStatus } from 'app/entities/enumerations/reservation-status.model';

import { IReservation, NewReservation } from './reservation.model';

export const sampleWithRequiredData: IReservation = {
  id: 52422,
  ticketId: 25554,
  startTime: dayjs('2023-06-11T00:22'),
  endTime: dayjs('2023-06-10T11:25'),
  status: ReservationStatus['CANCELLED'],
  reservationCode: 'WQ-E{10, 14}',
};

export const sampleWithPartialData: IReservation = {
  id: 60561,
  ticketId: 44488,
  startTime: dayjs('2023-06-10T13:10'),
  endTime: dayjs('2023-06-11T02:24'),
  status: ReservationStatus['EXPIRED'],
  reservationCode: 'VH-8{10, 14}',
};

export const sampleWithFullData: IReservation = {
  id: 42928,
  ticketId: 60593,
  startTime: dayjs('2023-06-10T20:15'),
  endTime: dayjs('2023-06-11T03:48'),
  status: ReservationStatus['ACTIVE'],
  reservationCode: 'US-E{10, 14}',
};

export const sampleWithNewData: NewReservation = {
  ticketId: 76094,
  startTime: dayjs('2023-06-10T10:40'),
  endTime: dayjs('2023-06-10T22:18'),
  status: ReservationStatus['CANCELLED'],
  reservationCode: 'VT-F{10, 14}',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
