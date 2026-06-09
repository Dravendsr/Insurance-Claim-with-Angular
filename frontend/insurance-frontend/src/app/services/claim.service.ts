import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

@Injectable({
  providedIn: 'root'
})
export class ClaimService {
  private api = inject(ApiService);
  // ApiService already prepends '/api', so we just provide the resource path
  private resourcePath = '/claims';

  getAllClaims(): Observable<any> {
    return this.api.get<any>(this.resourcePath);
  }

  getClaimsByUser(userId: number): Observable<any> {
    return this.api.get<any>(`${this.resourcePath}/user/${userId}`);
  }

  getClaimsByStatus(status: string): Observable<any> {
    return this.api.get<any>(`${this.resourcePath}/status/${status}`);
  }

  getClaimsByApprover(approverId: number): Observable<any> {
    return this.api.get<any>(`${this.resourcePath}/approver/${approverId}`);
  }

  getClaimById(claimId: number): Observable<any> {
    return this.api.get<any>(`${this.resourcePath}/${claimId}`);
  }

  patchClaim(claimId: number, updates: any): Observable<any> {
    return this.api.patch<any>(`${this.resourcePath}/${claimId}`, updates);
  }

  approveClaim(claimId: number, approvedAmount: number, approverComment?: string): Observable<any> {
    return this.api.put<any>(`${this.resourcePath}/${claimId}/approve`, { approvedAmount, approverComment });
  }

  rejectClaim(claimId: number, rejectionReason: string): Observable<any> {
    return this.api.put<any>(`${this.resourcePath}/${claimId}/reject`, { rejectionReason });
  }
}