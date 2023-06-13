import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ITicket, NewTicket } from '../ticket.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITicket for edit and NewTicketFormGroupInput for create.
 */
type TicketFormGroupInput = ITicket | PartialWithRequiredKeyOf<NewTicket>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ITicket | NewTicket> = Omit<T, 'issuedAt' | 'entryTime' | 'exitTime'> & {
  issuedAt?: string | null;
  entryTime?: string | null;
  exitTime?: string | null;
};

type TicketFormRawValue = FormValueOf<ITicket>;

type NewTicketFormRawValue = FormValueOf<NewTicket>;

type TicketFormDefaults = Pick<NewTicket, 'id' | 'issuedAt' | 'entryTime' | 'exitTime'>;

type TicketFormGroupContent = {
  id: FormControl<TicketFormRawValue['id'] | NewTicket['id']>;
  ticketCode: FormControl<TicketFormRawValue['ticketCode']>;
  issuedAt: FormControl<TicketFormRawValue['issuedAt']>;
  entryTime: FormControl<TicketFormRawValue['entryTime']>;
  exitTime: FormControl<TicketFormRawValue['exitTime']>;
  status: FormControl<TicketFormRawValue['status']>;
  parkingSpotId: FormControl<TicketFormRawValue['parkingSpotId']>;
};

export type TicketFormGroup = FormGroup<TicketFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TicketFormService {
  createTicketFormGroup(ticket: TicketFormGroupInput = { id: null }): TicketFormGroup {
    const ticketRawValue = this.convertTicketToTicketRawValue({
      ...this.getFormDefaults(),
      ...ticket,
    });
    return new FormGroup<TicketFormGroupContent>({
      id: new FormControl(
        { value: ticketRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      ticketCode: new FormControl(ticketRawValue.ticketCode, {
        validators: [Validators.required, Validators.pattern('^[A-F0-9]{6,10}$')],
      }),
      issuedAt: new FormControl(ticketRawValue.issuedAt, {
        validators: [Validators.required],
      }),
      entryTime: new FormControl(ticketRawValue.entryTime, {
        validators: [Validators.required],
      }),
      exitTime: new FormControl(ticketRawValue.exitTime, {
        validators: [Validators.required],
      }),
      status: new FormControl(ticketRawValue.status, {
        validators: [Validators.required],
      }),
      parkingSpotId: new FormControl(ticketRawValue.parkingSpotId, {
        validators: [Validators.required],
      }),
    });
  }

  getTicket(form: TicketFormGroup): ITicket | NewTicket {
    return this.convertTicketRawValueToTicket(form.getRawValue() as TicketFormRawValue | NewTicketFormRawValue);
  }

  resetForm(form: TicketFormGroup, ticket: TicketFormGroupInput): void {
    const ticketRawValue = this.convertTicketToTicketRawValue({ ...this.getFormDefaults(), ...ticket });
    form.reset(
      {
        ...ticketRawValue,
        id: { value: ticketRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): TicketFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      issuedAt: currentTime,
      entryTime: currentTime,
      exitTime: currentTime,
    };
  }

  private convertTicketRawValueToTicket(rawTicket: TicketFormRawValue | NewTicketFormRawValue): ITicket | NewTicket {
    return {
      ...rawTicket,
      issuedAt: dayjs(rawTicket.issuedAt, DATE_TIME_FORMAT),
      entryTime: dayjs(rawTicket.entryTime, DATE_TIME_FORMAT),
      exitTime: dayjs(rawTicket.exitTime, DATE_TIME_FORMAT),
    };
  }

  private convertTicketToTicketRawValue(
    ticket: ITicket | (Partial<NewTicket> & TicketFormDefaults)
  ): TicketFormRawValue | PartialWithRequiredKeyOf<NewTicketFormRawValue> {
    return {
      ...ticket,
      issuedAt: ticket.issuedAt ? ticket.issuedAt.format(DATE_TIME_FORMAT) : undefined,
      entryTime: ticket.entryTime ? ticket.entryTime.format(DATE_TIME_FORMAT) : undefined,
      exitTime: ticket.exitTime ? ticket.exitTime.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
