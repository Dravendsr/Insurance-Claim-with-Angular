import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgIf } from '@angular/common';
import { Router } from '@angular/router';
import { Auth } from '../../services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, NgIf],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class Login {
  private readonly auth = inject(Auth);
  private readonly router = inject(Router);

  protected credentials = { username: '', password: '' };
  protected isLoading = false;
  protected message = '';

  async login(): Promise<void> {
    if (!this.credentials.username || !this.credentials.password) {
      this.message = 'Please enter both username and password.';
      return;
    }

    this.isLoading = true;
    // Simulate API call - In production, call your Spring Boot backend here
    setTimeout(() => {
      const role = this.credentials.username.includes('admin') ? 'ADMIN' : 'POLICYHOLDER';
      this.auth.login(this.credentials.username, role);
      this.isLoading = false;
      this.router.navigate(['/dashboard']);
    }, 1000);
  }
}