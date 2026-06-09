import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  credentials = { username: '', password: '' };
  message = '';
  isLoading = false;

  constructor(
    private api: ApiService,
    private auth: AuthService,
    private router: Router
  ) {}

  login(): void {
    if (!this.credentials.username || !this.credentials.password) {
      this.message = 'Please fill in both fields.';
      return;
    }

    this.isLoading = true;
    this.message = '';

    this.api.post<{ status: boolean; message: string; role: string; email?: string; userId?: number }>('/users/login', this.credentials).subscribe({
      next: (response) => {
        console.log('Login response', response);
        if (response.status) {
          this.auth.setUsername(this.credentials.username);
          if (response.role) {
            this.auth.setRole(response.role);
          }
          if (response.email) {
            this.auth.setEmail(response.email);
          }
          if (response.userId !== undefined && response.userId !== null) {
            this.auth.setUserId(response.userId);
          }
          this.auth.setToken('authenticated');
          this.auth.setCredentials(this.credentials.username, this.credentials.password);
            // Fetch user profile to persist full name for UI immediately
            if (response.userId !== undefined && response.userId !== null) {
              this.api.get<any>(`/users/${response.userId}`).subscribe({
                next: (userResp) => {
                  if (userResp?.status && userResp.data) {
                    const full = userResp.data.fullName || null;
                    if (full) {
                      this.auth.setFullName(full);
                    }
                    if (userResp.data.email) {
                      this.auth.setEmail(userResp.data.email);
                    }
                  }
                },
                error: () => {
                  // ignore profile fetch error; navigation can proceed
                },
                complete: () => {
                  const target = '/user-details';
                  this.router.navigateByUrl(target).then((success) => {
                    if (!success) {
                      console.warn('Router navigation failed, retrying with navigateByUrl:', target);
                      this.router.navigateByUrl(target);
                    }
                  });
                }
              });
            } else {
              const target = '/user-details';
              this.router.navigateByUrl(target).then((success) => {
                if (!success) {
                  console.warn('Router navigation failed, retrying with navigateByUrl:', target);
                  this.router.navigateByUrl(target);
                }
              });
            }
        } else {
          this.message = response.message || 'Login failed. Please check your credentials.';
        }
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Login error', error);
        this.message = error?.error?.message || 'Login failed. Please check your credentials or backend connection.';
        this.isLoading = false;
      },
    });
  }
}
