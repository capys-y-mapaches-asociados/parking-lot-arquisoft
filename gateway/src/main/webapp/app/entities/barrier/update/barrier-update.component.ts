import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { BarrierFormService, BarrierFormGroup } from './barrier-form.service';
import { IBarrier } from '../barrier.model';
import { BarrierService } from '../service/barrier.service';
import { IParkingLot } from 'app/entities/parking-lot/parking-lot.model';
import { ParkingLotService } from 'app/entities/parking-lot/service/parking-lot.service';
import { BarrierType } from 'app/entities/enumerations/barrier-type.model';
import { BarrierStatus } from 'app/entities/enumerations/barrier-status.model';

@Component({
  selector: 'jhi-barrier-update',
  templateUrl: './barrier-update.component.html',
})
export class BarrierUpdateComponent implements OnInit {
  isSaving = false;
  barrier: IBarrier | null = null;
  barrierTypeValues = Object.keys(BarrierType);
  barrierStatusValues = Object.keys(BarrierStatus);

  parkingLotsSharedCollection: IParkingLot[] = [];

  editForm: BarrierFormGroup = this.barrierFormService.createBarrierFormGroup();

  constructor(
    protected barrierService: BarrierService,
    protected barrierFormService: BarrierFormService,
    protected parkingLotService: ParkingLotService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareParkingLot = (o1: IParkingLot | null, o2: IParkingLot | null): boolean => this.parkingLotService.compareParkingLot(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ barrier }) => {
      this.barrier = barrier;
      if (barrier) {
        this.updateForm(barrier);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const barrier = this.barrierFormService.getBarrier(this.editForm);
    if (barrier.id !== null) {
      this.subscribeToSaveResponse(this.barrierService.update(barrier));
    } else {
      this.subscribeToSaveResponse(this.barrierService.create(barrier));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBarrier>>): void {
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

  protected updateForm(barrier: IBarrier): void {
    this.barrier = barrier;
    this.barrierFormService.resetForm(this.editForm, barrier);

    this.parkingLotsSharedCollection = this.parkingLotService.addParkingLotToCollectionIfMissing<IParkingLot>(
      this.parkingLotsSharedCollection,
      barrier.parkingLot
    );
  }

  protected loadRelationshipsOptions(): void {
    this.parkingLotService
      .query()
      .pipe(map((res: HttpResponse<IParkingLot[]>) => res.body ?? []))
      .pipe(
        map((parkingLots: IParkingLot[]) =>
          this.parkingLotService.addParkingLotToCollectionIfMissing<IParkingLot>(parkingLots, this.barrier?.parkingLot)
        )
      )
      .subscribe((parkingLots: IParkingLot[]) => (this.parkingLotsSharedCollection = parkingLots));
  }
}
