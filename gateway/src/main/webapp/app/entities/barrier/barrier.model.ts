import { IParkingLot } from 'app/entities/parking-lot/parking-lot.model';
import { BarrierType } from 'app/entities/enumerations/barrier-type.model';
import { BarrierStatus } from 'app/entities/enumerations/barrier-status.model';

export interface IBarrier {
  id: number;
  name?: string | null;
  type?: BarrierType | null;
  status?: BarrierStatus | null;
  parkingLot?: Pick<IParkingLot, 'id'> | null;
}

export type NewBarrier = Omit<IBarrier, 'id'> & { id: null };
