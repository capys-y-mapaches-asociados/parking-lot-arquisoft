import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IParkingSpot } from '../parking-spot.model';
import { ParkingSpotService } from '../service/parking-spot.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './parking-spot-delete-dialog.component.html',
})
export class ParkingSpotDeleteDialogComponent {
  parkingSpot?: IParkingSpot;

  constructor(protected parkingSpotService: ParkingSpotService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.parkingSpotService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
