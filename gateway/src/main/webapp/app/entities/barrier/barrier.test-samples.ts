import { BarrierType } from 'app/entities/enumerations/barrier-type.model';
import { BarrierStatus } from 'app/entities/enumerations/barrier-status.model';

import { IBarrier, NewBarrier } from './barrier.model';

export const sampleWithRequiredData: IBarrier = {
  id: 4584,
  name: 'Concrete invoice Lead',
  type: BarrierType['ENTRY'],
  status: BarrierStatus['MAINTENANCE'],
};

export const sampleWithPartialData: IBarrier = {
  id: 23586,
  name: 'purposes program holistic',
  type: BarrierType['EXIT'],
  status: BarrierStatus['OPEN'],
};

export const sampleWithFullData: IBarrier = {
  id: 94460,
  name: 'Consultant',
  type: BarrierType['ENTRY'],
  status: BarrierStatus['DISABLED'],
};

export const sampleWithNewData: NewBarrier = {
  name: 'incubate reboot',
  type: BarrierType['ENTRY'],
  status: BarrierStatus['OPEN'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
