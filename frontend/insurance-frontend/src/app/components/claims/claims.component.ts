import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs/operators';
import { ClaimService } from '../../services/claim.service';
import { AuthService } from '../../services/auth.service';
import { Claim } from '../../models/claim';

@Component({
  selector: 'app-claims',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './claims.component.html',
  styleUrls: ['./claims.component.css'],
})
export class ClaimsComponent implements OnInit, OnDestroy {
  claims: Claim[] = [];
  loading = true;
  error = '';
  flashMessage = '';
  flashType: 'success' | 'error' | '' = '';
  private lifecycleTimers: Record<number, number> = {};

  constructor(
    private auth: AuthService,
    private claimService: ClaimService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
  console.log('ClaimsComponent Initialized');
  this.loadClaims();
}

  loadClaims(): void {
    console.log('loadClaims Called');
    this.loading = true;
    this.error = '';
    this.claims = [];

    const role = this.auth.role;
    const claimRequest =
      role === 'POLICYHOLDER' && this.auth.userId
        ? this.claimService.getClaimsByUser(Number(this.auth.userId))
        : this.claimService.getAllClaims();

    claimRequest
      .pipe(
        finalize(() => {
          this.loading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (response) => {
          console.log('API Response:', response);
          if (!response?.status) {
            this.error = response?.message || 'Unable to load claims.';
            this.showPopup(this.error, 'error');
            return;
          }

          const raw = Array.isArray(response.data)
            ? response.data
            : response.data
            ? [response.data]
            : [];

          this.claims = raw.map((c: any) => {
            const status = (c.claimStatus ?? c.status ?? 'SUBMITTED').toUpperCase();
            const claim = {
              id: c.claimId ?? c.id,
              claimNumber: c.claimNumber,
              policyId: c.insurancePolicy?.policyId ?? c.policyId,
              status,
              amount: c.claimAmount ?? c.amount,
              approverComment: c.approverComment,
              rejectionReason: c.rejectionReason,
            } as Claim;

            if (status === 'SUBMITTED' && typeof claim.id === 'number') {
              this.schedulePendingTransition(claim);
            }
            return claim;
          });
          console.log('Claims Loaded:', this.claims);
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error('Error fetching claims:', error);
          this.error = 'Unable to load claims. Please refresh or try again.';
          this.showPopup(this.error, 'error');
        },
      });
  }

  checkClaimStatus(claim: Claim): void {
    const status = (claim.status ?? '').toUpperCase();
    if (status === 'SUBMITTED') {
      this.showPopup('Claim submitted successfully. It will move to pending review shortly.', 'success');
      return;
    }
    if (status === 'PENDING') {
      this.showPopup('This claim is pending verification by your approver.', 'success');
      return;
    }
    if (status === 'APPROVED') {
      this.showPopup('Claim approved. Payment will be processed soon.', 'success');
      return;
    }
    if (status === 'REJECTED') {
      this.showPopup('Claim rejected. Please review approver comments.', 'error');
      return;
    }
    if (status === 'SETTLED') {
      this.showPopup('Claim settled successfully. Thank you!', 'success');
      return;
    }
    this.showPopup(`Claim status is ${status || 'unknown'}.`, 'success');
  }

  private schedulePendingTransition(claim: Claim): void {
    const claimId = claim.id ?? Date.now();
    if (this.lifecycleTimers[claimId]) {
      return;
    }

    this.lifecycleTimers[claimId] = window.setTimeout(() => {
      const index = this.claims.findIndex((item) => item.id === claim.id);
      if (index >= 0 && this.claims[index].status === 'SUBMITTED') {
        this.claims[index].status = 'PENDING';
        this.showPopup(`Claim ${this.claims[index].claimNumber} has moved to pending review.`, 'success');
      }
    }, 8000);
  }

  ngOnDestroy(): void {
    Object.values(this.lifecycleTimers).forEach((timerId) => window.clearTimeout(timerId));
  }

  private showPopup(message: string, type: 'success' | 'error'): void {
    this.flashMessage = message;
    this.flashType = type;
    setTimeout(() => this.clearFlash(), 5000);
  }

  getStageClasses(claim: Claim, stage: 'SUBMITTED' | 'PENDING' | 'APPROVED' | 'REJECTED' | 'SETTLED'): string {
    if (!claim?.status) {
      return '';
    }
    const status = claim.status.toUpperCase();
    if (stage === 'SUBMITTED') {
      return status !== 'SUBMITTED' ? 'completed' : 'active';
    }
    if (stage === 'PENDING') {
      return ['PENDING', 'APPROVED', 'SETTLED', 'REJECTED'].includes(status) ? 'completed' : status === 'PENDING' ? 'active' : '';
    }
    if (stage === 'APPROVED') {
      return ['APPROVED', 'SETTLED'].includes(status) ? 'active completed' : '';
    }
    if (stage === 'REJECTED') {
      return status === 'REJECTED' ? 'active completed' : '';
    }
    if (stage === 'SETTLED') {
      return status === 'SETTLED' ? 'active completed' : '';
    }
    return '';
  }

  isStageCompleted(claim: Claim, stage: 'SUBMITTED' | 'PENDING' | 'APPROVED' | 'REJECTED' | 'SETTLED'): boolean {
    return this.getStageClasses(claim, stage).includes('completed');
  }

  private clearFlash(): void {
    this.flashMessage = '';
    this.flashType = '';
  }
}
