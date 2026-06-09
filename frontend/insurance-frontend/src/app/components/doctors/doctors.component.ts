import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { Doctor } from '../../models/doctor';

@Component({
  selector: 'app-doctors',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './doctors.component.html',
  styleUrls: ['./doctors.component.css'],
})
export class DoctorsComponent implements OnInit {
  doctors: Doctor[] = [];
  loading = true;
  error = '';

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.api.get<Doctor[]>('/doctors').subscribe({
      next: (result) => {
        this.doctors = result;
        this.loading = false;
      },
      error: () => {
        this.error = 'Unable to load doctors.';
        this.loading = false;
      },
    });
  }
}
