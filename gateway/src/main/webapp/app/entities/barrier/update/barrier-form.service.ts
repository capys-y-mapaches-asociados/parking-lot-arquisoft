import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IBarrier, NewBarrier } from '../barrier.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBarrier for edit and NewBarrierFormGroupInput for create.
 */
type BarrierFormGroupInput = IBarrier | PartialWithRequiredKeyOf<NewBarrier>;

type BarrierFormDefaults = Pick<NewBarrier, 'id'>;

type BarrierFormGroupContent = {
  id: FormControl<IBarrier['id'] | NewBarrier['id']>;
  name: FormControl<IBarrier['name']>;
  type: FormControl<IBarrier['type']>;
  status: FormControl<IBarrier['status']>;
  parkingLot: FormControl<IBarrier['parkingLot']>;
};

export type BarrierFormGroup = FormGroup<BarrierFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BarrierFormService {
  createBarrierFormGroup(barrier: BarrierFormGroupInput = { id: null }): BarrierFormGroup {
    const barrierRawValue = {
      ...this.getFormDefaults(),
      ...barrier,
    };
    return new FormGroup<BarrierFormGroupContent>({
      id: new FormControl(
        { value: barrierRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(barrierRawValue.name, {
        validators: [Validators.required],
      }),
      type: new FormControl(barrierRawValue.type, {
        validators: [Validators.required],
      }),
      status: new FormControl(barrierRawValue.status, {
        validators: [Validators.required],
      }),
      parkingLot: new FormControl(barrierRawValue.parkingLot, {
        validators: [Validators.required],
      }),
    });
  }

  getBarrier(form: BarrierFormGroup): IBarrier | NewBarrier {
    return form.getRawValue() as IBarrier | NewBarrier;
  }

  resetForm(form: BarrierFormGroup, barrier: BarrierFormGroupInput): void {
    const barrierRawValue = { ...this.getFormDefaults(), ...barrier };
    form.reset(
      {
        ...barrierRawValue,
        id: { value: barrierRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): BarrierFormDefaults {
    return {
      id: null,
    };
  }
}
