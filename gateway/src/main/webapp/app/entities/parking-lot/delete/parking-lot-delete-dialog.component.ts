import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IParkingLot } from '../parking-lot.model';
import { ParkingLotService } from '../service/parking-lot.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './parking-lot-delete-dialog.component.html',
})
export class ParkingLotDeleteDialogComponent {
  parkingLot?: IParkingLot;

  constructor(protected parkingLotService: ParkingLotService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.parkingLotService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
