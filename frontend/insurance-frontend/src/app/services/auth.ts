import { Injectable, signal, computed } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class Auth {
  private readonly _isAuthenticated = signal<boolean>(false);
  private readonly _username = signal<string | null>(null);
  private readonly _role = signal<string | null>(null);

  public readonly isAuthenticated = computed(() => this._isAuthenticated());
  public readonly username = computed(() => this._username());
  public readonly role = computed(() => this._role());

  login(username: string, role: string): void {
    this._username.set(username);
    this._role.set(role);
    this._isAuthenticated.set(true);
    localStorage.setItem('user', JSON.stringify({ username, role }));
  }

  logout(): void {
    this._username.set(null);
    this._role.set(null);
    this._isAuthenticated.set(false);
    localStorage.removeItem('user');
  }
}