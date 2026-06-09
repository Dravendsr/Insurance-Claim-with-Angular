import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private tokenKey = 'auth_token';
  private roleKey = 'auth_role';
  private usernameKey = 'auth_username';
  private emailKey = 'auth_email';
  private fullNameKey = 'auth_full_name';
  private userIdKey = 'auth_user_id';

  get token(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  get role(): string | null {
    return localStorage.getItem(this.roleKey);
  }

  get username(): string | null {
    return localStorage.getItem(this.usernameKey);
  }

  get email(): string | null {
    return localStorage.getItem(this.emailKey);
  }

  get fullName(): string | null {
    return localStorage.getItem(this.fullNameKey);
  }

  get userId(): string | null {
    return localStorage.getItem(this.userIdKey);
  }

  get isAdmin(): boolean {
    return this.role === 'ADMIN';
  }

  get credentials(): string | null {
    return sessionStorage.getItem('credentials') ?? localStorage.getItem('auth_credentials');
  }

  setCredentials(username: string, password: string): void {
    const encoded = btoa(`${username}:${password}`);
    sessionStorage.setItem('credentials', encoded);
    localStorage.setItem('auth_credentials', encoded);
  }

  restoreCredentials(): void {
    const stored = localStorage.getItem('auth_credentials');
    if (stored && !sessionStorage.getItem('credentials')) {
      sessionStorage.setItem('credentials', stored);
    }
  }

  setToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  setRole(role: string): void {
    localStorage.setItem(this.roleKey, role);
  }

  setUsername(username: string): void {
    localStorage.setItem(this.usernameKey, username);
  }

  setEmail(email: string): void {
    localStorage.setItem(this.emailKey, email);
  }

  setFullName(fullName: string): void {
    localStorage.setItem(this.fullNameKey, fullName);
  }

  setUserId(userId: string | number): void {
    localStorage.setItem(this.userIdKey, String(userId));
  }

  clearToken(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.roleKey);
    localStorage.removeItem(this.usernameKey);
    localStorage.removeItem(this.emailKey);
    localStorage.removeItem(this.fullNameKey);
    localStorage.removeItem(this.userIdKey);
    localStorage.removeItem('auth_credentials');
    sessionStorage.removeItem('credentials');
  }

  isAuthenticated(): boolean {
    return !!this.token;
  }
}
