import dayjs from 'dayjs/esm';

import { INotification, NewNotification } from './notification.model';

export const sampleWithRequiredData: INotification = {
  id: 30621,
  reservationId: '05a8ca40-7022-4832-8605-5c933ab5156a',
  sentAt: dayjs('2023-06-10T06:48'),
  recipientId: '838c2eea-45d2-4b9f-a237-351489256ff4',
};

export const sampleWithPartialData: INotification = {
  id: 89089,
  reservationId: 'a7003767-1fbf-4f26-81ed-e53a9ac5af03',
  sentAt: dayjs('2023-06-10T05:20'),
  recipientId: 'b908a2b2-3602-4b58-8411-94145e42bf5b',
};

export const sampleWithFullData: INotification = {
  id: 42557,
  reservationId: '6dc9bd28-a2c4-4b2d-931e-c1b1dc7e5b0c',
  message: 'Soft Borders',
  sentAt: dayjs('2023-06-10T04:29'),
  recipientId: 'a35f2944-7654-47bc-a3bc-41259f6d212c',
};

export const sampleWithNewData: NewNotification = {
  reservationId: '68c4dce1-7fbf-4df1-8089-becb44eee5d3',
  sentAt: dayjs('2023-06-10T17:29'),
  recipientId: 'ee258a6e-51f5-48be-8719-b7c94a4feba6',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
