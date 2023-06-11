import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IParkingLot, NewParkingLot } from '../parking-lot.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IParkingLot for edit and NewParkingLotFormGroupInput for create.
 */
type ParkingLotFormGroupInput = IParkingLot | PartialWithRequiredKeyOf<NewParkingLot>;

type ParkingLotFormDefaults = Pick<NewParkingLot, 'id'>;

type ParkingLotFormGroupContent = {
  id: FormControl<IParkingLot['id'] | NewParkingLot['id']>;
  name: FormControl<IParkingLot['name']>;
  location: FormControl<IParkingLot['location']>;
  capacity: FormControl<IParkingLot['capacity']>;
};

export type ParkingLotFormGroup = FormGroup<ParkingLotFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ParkingLotFormService {
  createParkingLotFormGroup(parkingLot: ParkingLotFormGroupInput = { id: null }): ParkingLotFormGroup {
    const parkingLotRawValue = {
      ...this.getFormDefaults(),
      ...parkingLot,
    };
    return new FormGroup<ParkingLotFormGroupContent>({
      id: new FormControl(
        { value: parkingLotRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(parkingLotRawValue.name, {
        validators: [Validators.required],
      }),
      location: new FormControl(parkingLotRawValue.location, {
        validators: [Validators.required, Validators.minLength(12)],
      }),
      capacity: new FormControl(parkingLotRawValue.capacity, {
        validators: [Validators.required, Validators.max(13000)],
      }),
    });
  }

  getParkingLot(form: ParkingLotFormGroup): IParkingLot | NewParkingLot {
    return form.getRawValue() as IParkingLot | NewParkingLot;
  }

  resetForm(form: ParkingLotFormGroup, parkingLot: ParkingLotFormGroupInput): void {
    const parkingLotRawValue = { ...this.getFormDefaults(), ...parkingLot };
    form.reset(
      {
        ...parkingLotRawValue,
        id: { value: parkingLotRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ParkingLotFormDefaults {
    return {
      id: null,
    };
  }
}
