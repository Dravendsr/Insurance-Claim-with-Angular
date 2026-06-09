import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { NgIf } from '@angular/common';
import { AuthService } from './services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, NgIf],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  title = 'InsureFlow Portal';
  darkMode = localStorage.getItem('theme') === 'dark';
  showProfileMenu = false;

  constructor(public auth: AuthService, private router: Router) {
    this.auth.restoreCredentials();
    this.updateThemeClass();
  }

  toggleDarkMode(): void {
    this.darkMode = !this.darkMode;
    localStorage.setItem('theme', this.darkMode ? 'dark' : 'light');
    this.updateThemeClass();
  }

  private updateThemeClass(): void {
    const root = document.documentElement;
    root.classList.toggle('dark-theme', this.darkMode);
  }

  toggleProfileMenu(): void {
    this.showProfileMenu = !this.showProfileMenu;
  }

  logout(): void {
    this.auth.clearToken();
    this.showProfileMenu = false;
    this.router.navigate(['/login']);
  }

  get displayRole(): string {
    const role = this.auth.role;
    if (!role) return 'User';
    if (role === 'ADMIN') return 'Administrator';
    if (role === 'APPROVER') return 'Approver';
    if (role === 'POLICYHOLDER') {
      const full = this.auth.fullName;
      return full && full.trim().length > 0 ? full : 'Policyholder';
    }
    return role.charAt(0).toUpperCase() + role.slice(1).toLowerCase();
  }
}
