import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IBarrier } from '../barrier.model';

@Component({
  selector: 'jhi-barrier-detail',
  templateUrl: './barrier-detail.component.html',
})
export class BarrierDetailComponent implements OnInit {
  barrier: IBarrier | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ barrier }) => {
      this.barrier = barrier;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
