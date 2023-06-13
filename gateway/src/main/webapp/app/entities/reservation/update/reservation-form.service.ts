import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IReservation, NewReservation } from '../reservation.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IReservation for edit and NewReservationFormGroupInput for create.
 */
type ReservationFormGroupInput = IReservation | PartialWithRequiredKeyOf<NewReservation>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IReservation | NewReservation> = Omit<T, 'startTime' | 'endTime'> & {
  startTime?: string | null;
  endTime?: string | null;
};

type ReservationFormRawValue = FormValueOf<IReservation>;

type NewReservationFormRawValue = FormValueOf<NewReservation>;

type ReservationFormDefaults = Pick<NewReservation, 'id' | 'startTime' | 'endTime'>;

type ReservationFormGroupContent = {
  id: FormControl<ReservationFormRawValue['id'] | NewReservation['id']>;
  ticketId: FormControl<ReservationFormRawValue['ticketId']>;
  startTime: FormControl<ReservationFormRawValue['startTime']>;
  endTime: FormControl<ReservationFormRawValue['endTime']>;
  status: FormControl<ReservationFormRawValue['status']>;
  reservationCode: FormControl<ReservationFormRawValue['reservationCode']>;
  customerId: FormControl<ReservationFormRawValue['customerId']>;
  notifications: FormControl<ReservationFormRawValue['notifications']>;
};

export type ReservationFormGroup = FormGroup<ReservationFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ReservationFormService {
  createReservationFormGroup(reservation: ReservationFormGroupInput = { id: null }): ReservationFormGroup {
    const reservationRawValue = this.convertReservationToReservationRawValue({
      ...this.getFormDefaults(),
      ...reservation,
    });
    return new FormGroup<ReservationFormGroupContent>({
      id: new FormControl(
        { value: reservationRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      ticketId: new FormControl(reservationRawValue.ticketId, {
        validators: [Validators.required],
      }),
      startTime: new FormControl(reservationRawValue.startTime, {
        validators: [Validators.required],
      }),
      endTime: new FormControl(reservationRawValue.endTime, {
        validators: [Validators.required],
      }),
      status: new FormControl(reservationRawValue.status, {
        validators: [Validators.required],
      }),
      reservationCode: new FormControl(reservationRawValue.reservationCode, {
        validators: [Validators.required, Validators.pattern('^([A-Z]{2})-([A-Fa-f0-9]{10,14})$')],
      }),
      customerId: new FormControl(reservationRawValue.customerId),
      notifications: new FormControl(reservationRawValue.notifications),
    });
  }

  getReservation(form: ReservationFormGroup): IReservation | NewReservation {
    return this.convertReservationRawValueToReservation(form.getRawValue() as ReservationFormRawValue | NewReservationFormRawValue);
  }

  resetForm(form: ReservationFormGroup, reservation: ReservationFormGroupInput): void {
    const reservationRawValue = this.convertReservationToReservationRawValue({ ...this.getFormDefaults(), ...reservation });
    form.reset(
      {
        ...reservationRawValue,
        id: { value: reservationRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ReservationFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      startTime: currentTime,
      endTime: currentTime,
    };
  }

  private convertReservationRawValueToReservation(
    rawReservation: ReservationFormRawValue | NewReservationFormRawValue
  ): IReservation | NewReservation {
    return {
      ...rawReservation,
      startTime: dayjs(rawReservation.startTime, DATE_TIME_FORMAT),
      endTime: dayjs(rawReservation.endTime, DATE_TIME_FORMAT),
    };
  }

  private convertReservationToReservationRawValue(
    reservation: IReservation | (Partial<NewReservation> & ReservationFormDefaults)
  ): ReservationFormRawValue | PartialWithRequiredKeyOf<NewReservationFormRawValue> {
    return {
      ...reservation,
      startTime: reservation.startTime ? reservation.startTime.format(DATE_TIME_FORMAT) : undefined,
      endTime: reservation.endTime ? reservation.endTime.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
