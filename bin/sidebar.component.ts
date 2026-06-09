import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class AdminSidebar {
  constructor(private router: Router) {}

  onLogout() {
    localStorage.removeItem('currentUser');
    this.router.navigate(['/login']);
  }
}