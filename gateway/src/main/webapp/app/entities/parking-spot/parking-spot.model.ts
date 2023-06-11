import { IParkingLot } from 'app/entities/parking-lot/parking-lot.model';
import { ParkingSpotStatus } from 'app/entities/enumerations/parking-spot-status.model';
import { ParkingSpotType } from 'app/entities/enumerations/parking-spot-type.model';
import { ParkingSpotVehicle } from 'app/entities/enumerations/parking-spot-vehicle.model';

export interface IParkingSpot {
  id: number;
  number?: number | null;
  status?: ParkingSpotStatus | null;
  spotType?: ParkingSpotType | null;
  spotVehicle?: ParkingSpotVehicle | null;
  parkingLotId?: Pick<IParkingLot, 'id'> | null;
  parkingLotId?: Pick<IParkingLot, 'id'> | null;
}

export type NewParkingSpot = Omit<IParkingSpot, 'id'> & { id: null };
