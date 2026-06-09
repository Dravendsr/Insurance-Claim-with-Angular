import { Component, signal, OnInit } from '@angular/core';
import { NgFor, NgIf, NgClass, CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-claims',
  standalone: true,
  imports: [NgFor, NgIf, NgClass, CurrencyPipe],
  templateUrl: './claims.component.html',
  styleUrl: './claims.component.css'
})
export class Claims implements OnInit {
  protected claims = signal<any[]>([]);
  protected loading = signal(true);
  protected error = signal<string | null>(null);
  protected flashMessage = signal<string | null>(null);
  protected flashType = signal<'success' | 'error' | null>(null);

  ngOnInit(): void {
    // Simulate fetching data
    setTimeout(() => {
      this.claims.set([
        { claimNumber: 'CLM-001', policyId: 'POL-101', status: 'PENDING', amount: 5000 },
        { claimNumber: 'CLM-002', policyId: 'POL-101', status: 'APPROVED', amount: 1200 },
        { claimNumber: 'CLM-003', policyId: 'POL-202', status: 'SETTLED', amount: 2500 },
        { claimNumber: 'CLM-004', policyId: 'POL-303', status: 'SUBMITTED', amount: 3500 }
      ]);
      this.loading.set(false);
    }, 800);
  }

  protected checkClaimStatus(claim: any): void {
    if (!claim?.status) {
      this.setFlash('Claim status is unavailable.', 'error');
      return;
    }

    if (claim.status === 'SUBMITTED') {
      this.claims.update(claims => claims.map(item => item === claim ? { ...item, status: 'PENDING' } : item));
      this.setFlash('Claim moved from Submitted to Pending verification.', 'success');
      return;
    }

    if (claim.status === 'PENDING') {
      this.setFlash('This claim is pending approval by the verifier.', 'success');
      return;
    }

    if (claim.status === 'APPROVED') {
      this.setFlash('This claim has been approved. Payment will follow soon.', 'success');
      return;
    }

    if (claim.status === 'REJECTED') {
      this.setFlash('This claim was rejected. Please review approver comments.', 'error');
      return;
    }

    if (claim.status === 'SETTLED') {
      this.setFlash('This claim is settled. Thank you!', 'success');
      return;
    }

    this.setFlash(`Claim is currently ${claim.status}.`, 'success');
  }

  private setFlash(message: string, type: 'success' | 'error'): void {
    this.flashMessage.set(message);
    this.flashType.set(type);
    setTimeout(() => this.flashMessage.set(null), 4200);
  }
}