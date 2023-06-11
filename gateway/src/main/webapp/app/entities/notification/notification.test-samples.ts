import dayjs from 'dayjs/esm';

import { INotification, NewNotification } from './notification.model';

export const sampleWithRequiredData: INotification = {
  id: 30621,
  sentAt: dayjs('2023-06-11T00:30'),
  recipientId: '5a8ca407-0228-4328-a055-c933ab5156ac',
};

export const sampleWithPartialData: INotification = {
  id: 23094,
  message: 'digital Web',
  sentAt: dayjs('2023-06-10T16:50'),
  recipientId: 'd2b9fa23-7351-4489-a56f-f43ea7003767',
};

export const sampleWithFullData: INotification = {
  id: 10550,
  message: 'parse Cotton Fresh',
  sentAt: dayjs('2023-06-10T19:18'),
  recipientId: 'a9ac5af0-3db9-408a-ab23-602b58041194',
};

export const sampleWithNewData: NewNotification = {
  sentAt: dayjs('2023-06-10T23:15'),
  recipientId: '45e42bf5-b66d-4c9b-928a-2c4b2d131ec1',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
