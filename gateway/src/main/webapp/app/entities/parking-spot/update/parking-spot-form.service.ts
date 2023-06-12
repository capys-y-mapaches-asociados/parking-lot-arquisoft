import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IParkingSpot, NewParkingSpot } from '../parking-spot.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IParkingSpot for edit and NewParkingSpotFormGroupInput for create.
 */
type ParkingSpotFormGroupInput = IParkingSpot | PartialWithRequiredKeyOf<NewParkingSpot>;

type ParkingSpotFormDefaults = Pick<NewParkingSpot, 'id'>;

type ParkingSpotFormGroupContent = {
  id: FormControl<IParkingSpot['id'] | NewParkingSpot['id']>;
  number: FormControl<IParkingSpot['number']>;
  status: FormControl<IParkingSpot['status']>;
  spotType: FormControl<IParkingSpot['spotType']>;
  spotVehicle: FormControl<IParkingSpot['spotVehicle']>;
  parkingLotId: FormControl<IParkingSpot['parkingLotId']>;
};

export type ParkingSpotFormGroup = FormGroup<ParkingSpotFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ParkingSpotFormService {
  createParkingSpotFormGroup(parkingSpot: ParkingSpotFormGroupInput = { id: null }): ParkingSpotFormGroup {
    const parkingSpotRawValue = {
      ...this.getFormDefaults(),
      ...parkingSpot,
    };
    return new FormGroup<ParkingSpotFormGroupContent>({
      id: new FormControl(
        { value: parkingSpotRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      number: new FormControl(parkingSpotRawValue.number, {
        validators: [Validators.required, Validators.min(1), Validators.max(13000)],
      }),
      status: new FormControl(parkingSpotRawValue.status, {
        validators: [Validators.required],
      }),
      spotType: new FormControl(parkingSpotRawValue.spotType, {
        validators: [Validators.required],
      }),
      spotVehicle: new FormControl(parkingSpotRawValue.spotVehicle, {
        validators: [Validators.required],
      }),
      parkingLotId: new FormControl(parkingSpotRawValue.parkingLotId, {
        validators: [Validators.required],
      }),
    });
  }

  getParkingSpot(form: ParkingSpotFormGroup): IParkingSpot | NewParkingSpot {
    return form.getRawValue() as IParkingSpot | NewParkingSpot;
  }

  resetForm(form: ParkingSpotFormGroup, parkingSpot: ParkingSpotFormGroupInput): void {
    const parkingSpotRawValue = { ...this.getFormDefaults(), ...parkingSpot };
    form.reset(
      {
        ...parkingSpotRawValue,
        id: { value: parkingSpotRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ParkingSpotFormDefaults {
    return {
      id: null,
    };
  }
}
