import { Component, inject, computed } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Auth } from '../../services/auth';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [NgFor, NgIf, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class Dashboard {
  private readonly auth = inject(Auth);

  protected readonly role = this.auth.role();
  protected readonly title = 'Insurance Overview';
  protected readonly subtitle = 'Monitor your policy performance and claim statuses.';

  protected readonly cards = [
    { title: 'View Claims', description: 'Check the status of your submitted claims.', route: '/claims' },
    { title: 'Policies', description: 'Review your active insurance coverage.', route: '/policies' }
  ];

  constructor() {
    if (this.role === 'ADMIN') {
      this.cards.push(
        { title: 'User Management', description: 'Manage portal access and roles.', route: '/users' },
        { title: 'Providers', description: 'Manage hospitals and doctors.', route: '/hospitals' }
      );
    }
  }
}