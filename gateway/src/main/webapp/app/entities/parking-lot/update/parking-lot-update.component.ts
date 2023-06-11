import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ParkingLotFormService, ParkingLotFormGroup } from './parking-lot-form.service';
import { IParkingLot } from '../parking-lot.model';
import { ParkingLotService } from '../service/parking-lot.service';

@Component({
  selector: 'jhi-parking-lot-update',
  templateUrl: './parking-lot-update.component.html',
})
export class ParkingLotUpdateComponent implements OnInit {
  isSaving = false;
  parkingLot: IParkingLot | null = null;

  editForm: ParkingLotFormGroup = this.parkingLotFormService.createParkingLotFormGroup();

  constructor(
    protected parkingLotService: ParkingLotService,
    protected parkingLotFormService: ParkingLotFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ parkingLot }) => {
      this.parkingLot = parkingLot;
      if (parkingLot) {
        this.updateForm(parkingLot);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const parkingLot = this.parkingLotFormService.getParkingLot(this.editForm);
    if (parkingLot.id !== null) {
      this.subscribeToSaveResponse(this.parkingLotService.update(parkingLot));
    } else {
      this.subscribeToSaveResponse(this.parkingLotService.create(parkingLot));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IParkingLot>>): void {
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

  protected updateForm(parkingLot: IParkingLot): void {
    this.parkingLot = parkingLot;
    this.parkingLotFormService.resetForm(this.editForm, parkingLot);
  }
}
