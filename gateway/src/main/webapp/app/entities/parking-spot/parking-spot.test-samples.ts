import { ParkingSpotStatus } from 'app/entities/enumerations/parking-spot-status.model';
import { ParkingSpotType } from 'app/entities/enumerations/parking-spot-type.model';
import { ParkingSpotVehicle } from 'app/entities/enumerations/parking-spot-vehicle.model';

import { IParkingSpot, NewParkingSpot } from './parking-spot.model';

export const sampleWithRequiredData: IParkingSpot = {
  id: 12186,
  number: 2939,
  status: ParkingSpotStatus['OCCUPIED'],
  spotType: ParkingSpotType['REGULAR'],
  spotVehicle: ParkingSpotVehicle['E_CAR'],
};

export const sampleWithPartialData: IParkingSpot = {
  id: 24222,
  number: 11405,
  status: ParkingSpotStatus['AVAILABLE'],
  spotType: ParkingSpotType['EMERGENCY'],
  spotVehicle: ParkingSpotVehicle['E_SCOOTER'],
};

export const sampleWithFullData: IParkingSpot = {
  id: 98159,
  number: 3073,
  status: ParkingSpotStatus['OUT_OF_SERVICE'],
  spotType: ParkingSpotType['EMERGENCY'],
  spotVehicle: ParkingSpotVehicle['CARGO'],
};

export const sampleWithNewData: NewParkingSpot = {
  number: 7494,
  status: ParkingSpotStatus['OUT_OF_SERVICE'],
  spotType: ParkingSpotType['LOADING'],
  spotVehicle: ParkingSpotVehicle['CARGO'],
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
