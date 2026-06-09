import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = '/api';

  constructor(private http: HttpClient, private auth: AuthService) {}

  private getAuthHeaders(): HttpHeaders {
    const credentials = this.auth.credentials;

    if (credentials) {
      console.log('Sending API request with Basic Auth credentials');
      return new HttpHeaders({
        Authorization: `Basic ${credentials}`,
        'Content-Type': 'application/json',
      });
    }
    console.warn('No credentials found for API request');
    return new HttpHeaders({
      'Content-Type': 'application/json',
    });
  }

  get<T>(path: string): Observable<T> {
    const headers = this.getAuthHeaders();
    console.log('GET request to:', this.baseUrl + path);
    return this.http.get<T>(`${this.baseUrl}${path}`, { headers });
  }

  post<T>(path: string, body: any): Observable<T> {
    const headers = this.getAuthHeaders();
    console.log('POST request to:', this.baseUrl + path);
    return this.http.post<T>(`${this.baseUrl}${path}`, body, { headers });
  }

  patch<T>(path: string, body: any): Observable<T> {
    const headers = this.getAuthHeaders();
    console.log('PATCH request to:', this.baseUrl + path);
    return this.http.patch<T>(`${this.baseUrl}${path}`, body, { headers });
  }

  put<T>(path: string, body: any): Observable<T> {
    const headers = this.getAuthHeaders();
    console.log('PUT request to:', this.baseUrl + path);
    return this.http.put<T>(`${this.baseUrl}${path}`, body, { headers });
  }

  delete<T>(path: string): Observable<T> {
    const headers = this.getAuthHeaders();
    console.log('DELETE request to:', this.baseUrl + path);
    return this.http.delete<T>(`${this.baseUrl}${path}`, { headers });
  }
}
