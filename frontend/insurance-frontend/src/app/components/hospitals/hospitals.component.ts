import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { Hospital } from '../../models/hospital';

@Component({
  selector: 'app-hospitals',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './hospitals.component.html',
  styleUrls: ['./hospitals.component.css'],
})
export class HospitalsComponent implements OnInit {
  hospitals: Hospital[] = [];
  loading = true;
  error = '';
  showAddForm = false;
  showEditForm = false;
  editingHospital: Hospital | null = null;
  newHospital: Hospital = {
    hospitalName: '',
    hospitalType: '',
    address: '',
    phoneNumber: ''
  };

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.api.get<any>('/hospitals').subscribe({
      next: (response) => {
        // Accessing .data as per the HospitalController response format
        this.hospitals = response.data || [];
        this.loading = false;
      },
      error: () => {
        this.error = 'Unable to load hospitals.';
        this.loading = false;
      },
    });
  }

  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
    if (this.showAddForm) this.showEditForm = false;
  }

  addHospital(): void {
    if (!this.newHospital.hospitalName || !this.newHospital.hospitalType || !this.newHospital.address || !this.newHospital.phoneNumber) {
      alert('Please fill in all fields.');
      return;
    }

    this.api.post<any>('/hospitals', this.newHospital).subscribe({
      next: (response) => {
        if (response.status) {
          this.hospitals.push(response.data);
          this.showAddForm = false;
          this.newHospital = { hospitalName: '', hospitalType: '', address: '', phoneNumber: '' };
        } else {
          alert(response.message || 'Failed to add hospital.');
        }
      },
      error: (error) => {
        console.error('Add Hospital Error:', error);
        alert('An error occurred while adding the hospital. Check the console for details.');
      }
    });
  }

  editHospital(hospital: Hospital): void {
    this.editingHospital = { ...hospital };
    this.showEditForm = true;
    this.showAddForm = false;
  }

  updateHospital(): void {
    if (!this.editingHospital || !this.editingHospital.hospitalId) return;

    this.api.put<any>(`/hospitals/${this.editingHospital.hospitalId}`, this.editingHospital).subscribe({
      next: (response) => {
        if (response.status) {
          const index = this.hospitals.findIndex(h => h.hospitalId === this.editingHospital?.hospitalId);
          if (index !== -1 && response.data) {
            this.hospitals[index] = response.data;
          }
          this.showEditForm = false;
          this.editingHospital = null;
        } else {
          alert(response.message || 'Failed to update hospital.');
        }
      },
      error: () => alert('An error occurred while updating the hospital.')
    });
  }

  removeHospital(id: number | undefined): void {
    if (!id) return;

    if (confirm('Are you sure you want to remove this hospital?')) {
      this.api.delete<any>(`/hospitals/${id}`).subscribe({
        next: (response) => {
          if (response.status) {
            this.hospitals = this.hospitals.filter(h => h.hospitalId !== id);
          } else {
            alert(response.message || 'Failed to remove hospital.');
          }
        },
        error: () => alert('An error occurred while removing the hospital.')
      });
    }
  }
}
