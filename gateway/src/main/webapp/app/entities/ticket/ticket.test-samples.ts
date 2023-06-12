import dayjs from 'dayjs/esm';

import { TicketStatus } from 'app/entities/enumerations/ticket-status.model';

import { ITicket, NewTicket } from './ticket.model';

export const sampleWithRequiredData: ITicket = {
  id: 65582,
  ticketCode: '6{6, 10}',
  issuedAt: dayjs('2023-06-12T20:41'),
  entryTime: dayjs('2023-06-12T11:24'),
  exitTime: dayjs('2023-06-12T00:24'),
  status: TicketStatus['ACTIVE'],
};

export const sampleWithPartialData: ITicket = {
  id: 7730,
  ticketCode: 'A{6, 10}',
  issuedAt: dayjs('2023-06-12T21:49'),
  entryTime: dayjs('2023-06-12T20:28'),
  exitTime: dayjs('2023-06-12T04:16'),
  status: TicketStatus['EXPIRED'],
};

export const sampleWithFullData: ITicket = {
  id: 94764,
  ticketCode: '1{6, 10}',
  issuedAt: dayjs('2023-06-12T19:31'),
  entryTime: dayjs('2023-06-12T09:06'),
  exitTime: dayjs('2023-06-12T09:47'),
  status: TicketStatus['EXPIRED'],
};

export const sampleWithNewData: NewTicket = {
  ticketCode: 'D{6, 10}',
  issuedAt: dayjs('2023-06-12T16:44'),
  entryTime: dayjs('2023-06-12T04:33'),
  exitTime: dayjs('2023-06-12T03:22'),
  status: TicketStatus['EXPIRED'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
