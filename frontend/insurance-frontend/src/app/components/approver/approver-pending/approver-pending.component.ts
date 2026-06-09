import { Component, ChangeDetectorRef, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { ClaimService } from '../../../services/claim.service';
import { AuthService } from '../../../services/auth.service';
import { Claim } from '../../../models/claim';

@Component({
  selector: 'app-approver-pending',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <section class="approver-pending">
      <header class="page-header">
        <div>
          <p class="eyebrow">Approver Queue</p>
          <h2>Pending Claims</h2>
          <p class="description">Take over unassigned claims and review items assigned to you to keep the approval pipeline moving.</p>
        </div>
      </header>

      <div *ngIf="error" class="alert alert-error">{{ error }}</div>
      <div *ngIf="loading" class="loading-state">Loading claims...</div>
      <div *ngIf="!loading && pendingClaims.length === 0" class="empty-state">
        No pending claims are available right now.
      </div>

      <table *ngIf="!loading && pendingClaims.length > 0" class="claims-table">
        <thead>
          <tr>
            <th>Claim #</th>
            <th>Policyholder</th>
            <th>Hospital</th>
            <th>Amount</th>
            <th>Status</th>
            <th>Assigned To</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          <tr *ngFor="let claim of pendingClaims">
            <td>{{ claim.claimNumber }}</td>
            <td>{{ claim.user?.fullName || claim.user?.username || 'Unknown' }}</td>
            <td>{{ claim.treatment?.hospital?.hospitalName || 'N/A' }}</td>
            <td>{{ claim.claimAmount | currency:'INR':'symbol':'1.2-2' }}</td>
            <td><span class="status-pill" [ngClass]="claim.claimStatus?.toLowerCase()">{{ claim.claimStatus }}</span></td>
            <td>
              <span *ngIf="claim.assignedApprover">{{ claim.assignedApprover.fullName || claim.assignedApprover.username }}</span>
              <span *ngIf="!claim.assignedApprover">Unassigned</span>
            </td>
            <td>
              <button class="primary-button" (click)="claim.assignedApprover ? reviewClaim(claim) : takeOverClaim(claim)">
                {{ claim.assignedApprover ? 'Review' : 'Take Over' }}
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </section>
  `,
  styles: [
    ".approver-pending { padding: 1.5rem; max-width: 1200px; margin: 0 auto; }",
    ".page-header { margin-bottom: 1.5rem; }",
    ".eyebrow { margin: 0 0 0.5rem; color: #4338ca; text-transform: uppercase; letter-spacing: 0.12em; font-size: 0.8rem; }",
    ".description { margin: 0; color: #4b5563; max-width: 46rem; }",
    ".alert { border-radius: 0.75rem; padding: 1rem; margin-bottom: 1rem; }",
    ".alert-error { background: #fee2e2; color: #b91c1c; border: 1px solid #fecaca; }",
    ".loading-state, .empty-state { padding: 1rem; color: #374151; }",
    ".claims-table { width: 100%; border-collapse: collapse; overflow: hidden; border-radius: 0.75rem; background: #0b1220; box-shadow: 0 12px 30px rgba(2, 6, 23, 0.6); }",
    ".claims-table th, .claims-table td { padding: 1rem 1.1rem; border-bottom: 1px solid rgba(255,255,255,0.06); text-align: left; color: #e6eef8; }",
    ".claims-table th { font-weight: 700; color: #e6eef8; background: #071025; }",
    ".claims-table tbody tr:hover { background: rgba(255,255,255,0.02); transition: background 150ms ease; }",
    ".status-pill { display: inline-flex; align-items: center; padding: 0.35rem 0.75rem; border-radius: 9999px; font-size: 0.85rem; font-weight: 600; text-transform: uppercase; letter-spacing: 0.02em; }",
    ".status-pill.pending { background: #f8fafc; color: #0f172a; }",
    ".status-pill.submitted { background: #fef3c7; color: #92400e; }",
    ".status-pill.approved { background: #dcfce7; color: #166534; }",
    ".status-pill.rejected { background: #fee2e2; color: #991b1b; }",
    ".primary-button { border: none; border-radius: 9999px; background: #4338ca; color: #ffffff; padding: 0.6rem 1rem; cursor: pointer; font-weight: 600; }",
  ],
})
export class ApproverPendingComponent implements OnInit {
  pendingClaims: Claim[] = [];
  loading = false;
  error = '';

  constructor(
    private claimService: ClaimService,
    private auth: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadPendingClaims();
  }

  loadPendingClaims(): void {
    this.loading = true;
    this.error = '';
    this.pendingClaims = [];

    forkJoin({
      submitted: this.claimService.getClaimsByStatus('SUBMITTED'),
      pending: this.claimService.getClaimsByStatus('PENDING'),
    })
      .pipe(finalize(() => {
        this.loading = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: ({ submitted, pending }) => {
          const submittedClaims = Array.isArray(submitted?.data) ? submitted.data : [];
          const pendingClaims = Array.isArray(pending?.data) ? pending.data : [];
          const allClaims = [...submittedClaims, ...pendingClaims];
          const currentApproverId = Number(this.auth.userId);

          this.pendingClaims = allClaims
            .map((claim: any) => this.normalizeClaim(claim))
            .filter((claim) => {
              return !claim.assignedApprover || claim.assignedApprover?.userId === currentApproverId;
            });
        },
        error: (error) => {
          console.error('Unable to load approver claims', error);
          this.error = 'Could not load pending claims. Please refresh the page.';
        },
      });
  }

  takeOverClaim(claim: Claim): void {
    if (!claim.claimId) {
      return;
    }

    const approverId = Number(this.auth.userId);
    this.claimService
      .patchClaim(claim.claimId, { assignedApprover: approverId, claimStatus: 'PENDING' })
      .subscribe({
        next: (response) => {
          if (response?.status) {
            this.router.navigate(['/approver/review', claim.claimId]);
          } else {
            this.error = response?.message || 'Unable to take over the claim.';
          }
        },
        error: (error) => {
          console.error('Takeover failed', error);
          this.error = 'Could not take over claim. It may have been assigned to another approver.';
        },
      });
  }

  reviewClaim(claim: Claim): void {
    if (claim.claimId) {
      this.router.navigate(['/approver/review', claim.claimId]);
    }
  }

  private normalizeClaim(claim: any): Claim {
    return {
      claimId: claim.claimId,
      claimNumber: claim.claimNumber,
      claimAmount: claim.claimAmount,
      approvedAmount: claim.approvedAmount,
      claimStatus: (claim.claimStatus ?? '').toUpperCase(),
      approverComment: claim.approverComment,
      rejectionReason: claim.rejectionReason,
      claimDate: claim.claimDate ? new Date(claim.claimDate) : new Date(),
      approvedDate: claim.approvedDate ? new Date(claim.approvedDate) : undefined,
      user: claim.user,
      treatment: claim.treatment,
      insurancePolicy: claim.insurancePolicy,
      assignedApprover: claim.assignedApprover,
    } as Claim;
  }
}
