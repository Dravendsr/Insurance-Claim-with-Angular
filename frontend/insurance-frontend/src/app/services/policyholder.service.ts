import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

@Injectable({
  providedIn: 'root',
})
export class PolicyholderService {
  constructor(private api: ApiService) {}

  getPoliciesForUser(userId: number): Observable<any> {
    return this.api.get<any>(`/user-policies/user/${userId}`);
  }

  getClaimsForUser(userId: number): Observable<any> {
    return this.api.get<any>(`/claims/user/${userId}`);
  }

  getUserById(userId: number): Observable<any> {
    return this.api.get<any>(`/users/${userId}`);
  }

  getPayments(): Observable<any> {
    return this.api.get<any>('/payments');
  }

  getDocuments(): Observable<any> {
    return this.api.get<any>('/documents');
  }

  createClaim(payload: any): Observable<any> {
    return this.api.post<any>('/claims', payload);
  }

  uploadDocument(payload: any): Observable<any> {
    return this.api.post<any>('/documents', payload);
  }
}
