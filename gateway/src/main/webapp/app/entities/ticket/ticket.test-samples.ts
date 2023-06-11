import dayjs from 'dayjs/esm';

import { TicketStatus } from 'app/entities/enumerations/ticket-status.model';

import { ITicket, NewTicket } from './ticket.model';

export const sampleWithRequiredData: ITicket = {
  id: 65582,
  ticketCode: '6{6, 10}',
  issuedAt: dayjs('2023-06-10T22:46'),
  entryTime: dayjs('2023-06-10T13:30'),
  exitTime: dayjs('2023-06-10T02:29'),
  status: TicketStatus['ACTIVE'],
};

export const sampleWithPartialData: ITicket = {
  id: 7730,
  ticketCode: 'A{6, 10}',
  issuedAt: dayjs('2023-06-10T23:55'),
  entryTime: dayjs('2023-06-10T22:33'),
  exitTime: dayjs('2023-06-10T06:21'),
  status: TicketStatus['EXPIRED'],
};

export const sampleWithFullData: ITicket = {
  id: 94764,
  ticketCode: '1{6, 10}',
  issuedAt: dayjs('2023-06-10T21:36'),
  entryTime: dayjs('2023-06-10T11:12'),
  exitTime: dayjs('2023-06-10T11:53'),
  status: TicketStatus['EXPIRED'],
};

export const sampleWithNewData: NewTicket = {
  ticketCode: 'D{6, 10}',
  issuedAt: dayjs('2023-06-10T18:50'),
  entryTime: dayjs('2023-06-10T06:38'),
  exitTime: dayjs('2023-06-10T05:27'),
  status: TicketStatus['EXPIRED'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
