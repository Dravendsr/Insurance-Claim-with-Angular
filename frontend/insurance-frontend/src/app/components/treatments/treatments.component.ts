import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { Treatment } from '../../models/treatment';

@Component({
  selector: 'app-treatments',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './treatments.component.html',
  styleUrls: ['./treatments.component.css'],
})
export class TreatmentsComponent implements OnInit {
  treatments: Treatment[] = [];
  loading = true;
  error = '';

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.api.get<Treatment[]>('/treatments').subscribe({
      next: (result) => {
        this.treatments = result;
        this.loading = false;
      },
      error: () => {
        this.error = 'Unable to load treatments.';
        this.loading = false;
      },
    });
  }
}
