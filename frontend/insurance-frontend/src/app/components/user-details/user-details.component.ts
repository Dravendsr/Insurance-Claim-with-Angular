import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-user-details',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './user-details.component.html',
  styleUrls: ['./user-details.component.css'],
})
export class UserDetailsComponent implements OnInit {

  userFullName = 'User Details';
  userEmail = 'N/A';
  userStatus = 'ACTIVE';
  profileInitials = 'U';
  userGreeting = 'User';

  isLoading = true;
  errorMessage = '';
  successMessage = '';

  constructor(public auth: AuthService, private api: ApiService) {}

  ngOnInit(): void {
    const full = this.auth.fullName;
    if (full && full.trim().length > 0) {
      const parts = full.trim().split(/\s+/);
      const initials = parts.length === 1
        ? parts[0].slice(0, 2).toUpperCase()
        : (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
      this.profileInitials = initials;
      this.userGreeting = full;
    } else {
      const role = this.auth.role;
      if (role === 'ADMIN') {
        this.profileInitials = 'A';
        this.userGreeting = 'Administrator';
      } else if (role === 'APPROVER') {
        this.profileInitials = 'AR';
        this.userGreeting = 'Approver';
      } else if (role === 'POLICYHOLDER') {
        this.profileInitials = 'PR';
        this.userGreeting = 'Policy Holder';
      } else {
        this.profileInitials = 'U';
        this.userGreeting = 'User';
      }
    }

    this.userEmail = this.auth.email || 'N/A';
    this.userStatus = 'ACTIVE';

    if (!this.auth.email) {
      this.loadEmailFallback();
    }

    this.isLoading = false;
  }

  private loadEmailFallback(): void {
    if (!this.auth.username) {
      return;
    }

    this.api.get<any>('/users').subscribe({
      next: (response) => {
        const users = Array.isArray(response?.data) ? response.data : response?.data ? [response.data] : [];
        const currentUser = users.find((user: any) => user.username?.toLowerCase() === this.auth.username?.toLowerCase());
        if (currentUser && currentUser.email) {
          this.userEmail = currentUser.email;
          this.auth.setEmail(currentUser.email);
        }
      },
      error: () => {
        // Ignore fallback failure; email remains N/A if unavailable
      }
    });
  }

  get displayRole(): string {
    const role = this.auth.role;
    if (!role) return 'User';
    if (role === 'ADMIN') return 'Administrator';
    if (role === 'APPROVER') return 'Approver';
    if (role === 'POLICYHOLDER') return 'Policyholder';
    return role.charAt(0).toUpperCase() + role.slice(1).toLowerCase();
  }
}
