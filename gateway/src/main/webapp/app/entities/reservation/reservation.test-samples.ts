import dayjs from 'dayjs/esm';

import { ReservationStatus } from 'app/entities/enumerations/reservation-status.model';

import { IReservation, NewReservation } from './reservation.model';

export const sampleWithRequiredData: IReservation = {
  id: 52422,
  ticketId: 25554,
  startTime: dayjs('2023-06-12T20:01'),
  endTime: dayjs('2023-06-12T07:04'),
  status: ReservationStatus['CANCELLED'],
  reservationCode: 'WQ-1d3Dfa6a8d1',
};

export const sampleWithPartialData: IReservation = {
  id: 41013,
  ticketId: 9563,
  startTime: dayjs('2023-06-12T18:19'),
  endTime: dayjs('2023-06-12T18:27'),
  status: ReservationStatus['CANCELLED'],
  reservationCode: 'ET-b6d64FC18ce433',
};

export const sampleWithFullData: IReservation = {
  id: 19641,
  ticketId: 90285,
  startTime: dayjs('2023-06-12T07:15'),
  endTime: dayjs('2023-06-12T02:28'),
  status: ReservationStatus['ACTIVE'],
  reservationCode: 'AG-F4491CB1f1f4C',
};

export const sampleWithNewData: NewReservation = {
  ticketId: 65199,
  startTime: dayjs('2023-06-12T22:36'),
  endTime: dayjs('2023-06-13T01:14'),
  status: ReservationStatus['ACTIVE'],
  reservationCode: 'PN-8A6EDEb8d1f21',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
