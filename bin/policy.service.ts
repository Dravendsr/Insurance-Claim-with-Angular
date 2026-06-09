import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PolicyService {
  private readonly apiUrl = 'http://localhost:8989/api/policies';

  constructor(private http: HttpClient) {}

  getAllPolicies(): Observable<any> {
    return this.http.get<any>(this.apiUrl);
  }

  getPolicyById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  createPolicy(policy: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, policy);
  }

  updatePolicy(id: number, policy: any): Observable<any> {
    // Ensure the ID in the body matches the ID in the URL for the backend to process the update
    const payload = { ...policy, policyId: id };
    return this.http.put<any>(`${this.apiUrl}/${id}`, payload);
  }
}