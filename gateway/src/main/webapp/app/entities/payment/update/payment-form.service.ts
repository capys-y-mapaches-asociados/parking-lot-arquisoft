import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IPayment, NewPayment } from '../payment.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPayment for edit and NewPaymentFormGroupInput for create.
 */
type PaymentFormGroupInput = IPayment | PartialWithRequiredKeyOf<NewPayment>;

type PaymentFormDefaults = Pick<NewPayment, 'id'>;

type PaymentFormGroupContent = {
  id: FormControl<IPayment['id'] | NewPayment['id']>;
  customerId: FormControl<IPayment['customerId']>;
  reservationID: FormControl<IPayment['reservationID']>;
  amount: FormControl<IPayment['amount']>;
  paymentStatus: FormControl<IPayment['paymentStatus']>;
  paymentMethod: FormControl<IPayment['paymentMethod']>;
};

export type PaymentFormGroup = FormGroup<PaymentFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PaymentFormService {
  createPaymentFormGroup(payment: PaymentFormGroupInput = { id: null }): PaymentFormGroup {
    const paymentRawValue = {
      ...this.getFormDefaults(),
      ...payment,
    };
    return new FormGroup<PaymentFormGroupContent>({
      id: new FormControl(
        { value: paymentRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      customerId: new FormControl(paymentRawValue.customerId, {
        validators: [Validators.required],
      }),
      reservationID: new FormControl(paymentRawValue.reservationID),
      amount: new FormControl(paymentRawValue.amount, {
        validators: [Validators.required, Validators.max(1000.0)],
      }),
      paymentStatus: new FormControl(paymentRawValue.paymentStatus, {
        validators: [Validators.required],
      }),
      paymentMethod: new FormControl(paymentRawValue.paymentMethod, {
        validators: [Validators.required],
      }),
    });
  }

  getPayment(form: PaymentFormGroup): IPayment | NewPayment {
    return form.getRawValue() as IPayment | NewPayment;
  }

  resetForm(form: PaymentFormGroup, payment: PaymentFormGroupInput): void {
    const paymentRawValue = { ...this.getFormDefaults(), ...payment };
    form.reset(
      {
        ...paymentRawValue,
        id: { value: paymentRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): PaymentFormDefaults {
    return {
      id: null,
    };
  }
}
