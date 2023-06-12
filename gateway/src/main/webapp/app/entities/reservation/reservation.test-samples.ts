import dayjs from 'dayjs/esm';

import { ReservationStatus } from 'app/entities/enumerations/reservation-status.model';

import { IReservation, NewReservation } from './reservation.model';

export const sampleWithRequiredData: IReservation = {
  id: 52422,
  ticketId: 25554,
  startTime: dayjs('2023-06-12T17:04'),
  endTime: dayjs('2023-06-12T04:07'),
  status: ReservationStatus['CANCELLED'],
  reservationCode: 'WQ-E{10, 14}',
};

export const sampleWithPartialData: IReservation = {
  id: 60561,
  ticketId: 44488,
  startTime: dayjs('2023-06-12T05:51'),
  endTime: dayjs('2023-06-12T19:05'),
  status: ReservationStatus['EXPIRED'],
  reservationCode: 'VH-8{10, 14}',
};

export const sampleWithFullData: IReservation = {
  id: 42928,
  ticketId: 60593,
  startTime: dayjs('2023-06-12T12:57'),
  endTime: dayjs('2023-06-12T20:30'),
  status: ReservationStatus['ACTIVE'],
  reservationCode: 'US-E{10, 14}',
};

export const sampleWithNewData: NewReservation = {
  ticketId: 76094,
  startTime: dayjs('2023-06-12T03:21'),
  endTime: dayjs('2023-06-12T15:00'),
  status: ReservationStatus['CANCELLED'],
  reservationCode: 'VT-F{10, 14}',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
