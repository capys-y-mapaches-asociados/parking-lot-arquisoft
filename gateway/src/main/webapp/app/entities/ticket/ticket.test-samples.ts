import dayjs from 'dayjs/esm';

import { TicketStatus } from 'app/entities/enumerations/ticket-status.model';

import { ITicket, NewTicket } from './ticket.model';

export const sampleWithRequiredData: ITicket = {
  id: 65582,
  ticketCode: '6{6, 10}',
  issuedAt: dayjs('2023-06-10T22:46'),
  parkingSpotId: '7e31001c-bf72-498a-834c-ce4a0030eec8',
  entryTime: dayjs('2023-06-10T04:44'),
  exitTime: dayjs('2023-06-10T12:16'),
  status: TicketStatus['EXPIRED'],
};

export const sampleWithPartialData: ITicket = {
  id: 6087,
  ticketCode: '7{6, 10}',
  issuedAt: dayjs('2023-06-10T18:58'),
  parkingSpotId: '8f12ded2-5db5-4085-a707-23a9f506643b',
  entryTime: dayjs('2023-06-10T17:26'),
  exitTime: dayjs('2023-06-10T19:43'),
  status: TicketStatus['ACTIVE'],
};

export const sampleWithFullData: ITicket = {
  id: 46326,
  ticketCode: '2{6, 10}',
  issuedAt: dayjs('2023-06-10T03:48'),
  parkingSpotId: '526d16e6-a732-4253-816d-6858c2d91c96',
  entryTime: dayjs('2023-06-10T04:12'),
  exitTime: dayjs('2023-06-10T10:45'),
  status: TicketStatus['ACTIVE'],
};

export const sampleWithNewData: NewTicket = {
  ticketCode: 'C{6, 10}',
  issuedAt: dayjs('2023-06-10T04:09'),
  parkingSpotId: 'b87f79aa-d9e7-4e5e-8641-6931eba16fcf',
  entryTime: dayjs('2023-06-10T21:52'),
  exitTime: dayjs('2023-06-10T07:31'),
  status: TicketStatus['EXPIRED'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
