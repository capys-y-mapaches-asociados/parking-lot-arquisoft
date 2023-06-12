import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { TicketFormService, TicketFormGroup } from './ticket-form.service';
import { ITicket } from '../ticket.model';
import { TicketService } from '../service/ticket.service';
import { IParkingSpot } from 'app/entities/parking-spot/parking-spot.model';
import { ParkingSpotService } from 'app/entities/parking-spot/service/parking-spot.service';
import { TicketStatus } from 'app/entities/enumerations/ticket-status.model';

@Component({
  selector: 'jhi-ticket-update',
  templateUrl: './ticket-update.component.html',
})
export class TicketUpdateComponent implements OnInit {
  isSaving = false;
  ticket: ITicket | null = null;
  ticketStatusValues = Object.keys(TicketStatus);

  parkingSpotsSharedCollection: IParkingSpot[] = [];

  editForm: TicketFormGroup = this.ticketFormService.createTicketFormGroup();

  constructor(
    protected ticketService: TicketService,
    protected ticketFormService: TicketFormService,
    protected parkingSpotService: ParkingSpotService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareParkingSpot = (o1: IParkingSpot | null, o2: IParkingSpot | null): boolean => this.parkingSpotService.compareParkingSpot(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ticket }) => {
      this.ticket = ticket;
      if (ticket) {
        this.updateForm(ticket);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const ticket = this.ticketFormService.getTicket(this.editForm);
    if (ticket.id !== null) {
      this.subscribeToSaveResponse(this.ticketService.update(ticket));
    } else {
      this.subscribeToSaveResponse(this.ticketService.create(ticket));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITicket>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(ticket: ITicket): void {
    this.ticket = ticket;
    this.ticketFormService.resetForm(this.editForm, ticket);

    this.parkingSpotsSharedCollection = this.parkingSpotService.addParkingSpotToCollectionIfMissing<IParkingSpot>(
      this.parkingSpotsSharedCollection,
      ticket.parkingSpotId
    );
  }

  protected loadRelationshipsOptions(): void {
    this.parkingSpotService
      .query()
      .pipe(map((res: HttpResponse<IParkingSpot[]>) => res.body ?? []))
      .pipe(
        map((parkingSpots: IParkingSpot[]) =>
          this.parkingSpotService.addParkingSpotToCollectionIfMissing<IParkingSpot>(parkingSpots, this.ticket?.parkingSpotId)
        )
      )
      .subscribe((parkingSpots: IParkingSpot[]) => (this.parkingSpotsSharedCollection = parkingSpots));
  }
}
