import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs/operators';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.css'],
})
export class AnalyticsComponent implements OnInit {
  role: string | null = null;
  analytics: any = null;
  isLoading = true;
  errorMessage = '';

  constructor(
    private auth: AuthService,
    private api: ApiService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.role = this.auth.role;
    this.loadAnalytics();
  }

  loadAnalytics(): void {
    this.isLoading = true;
    this.analytics = null;
    this.errorMessage = '';

    this.api
      .get<any>('/admin/analytics/overview')
      .pipe(
        finalize(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (response) => {
          if (!response?.status) {
            this.errorMessage =
              response?.message || 'Unable to load analytics summary.';
            return;
          }

          this.analytics = response?.data || {};

          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error('Error loading analytics data:', error);

          this.errorMessage =
            'Unable to load analytics summary. Please try again later.';

          this.cdr.detectChanges();
        },
      });
  }

  get heading(): string {
    return this.role === 'ADMIN' ? 'Admin Analytics' : 'Analytics';
  }

  private safeNumber(value: any): number {
    if (typeof value === 'number') {
      return value;
    }
    const parsed = Number(value);
    return Number.isFinite(parsed) ? parsed : 0;
  }

  get totalClaims(): number {
    return this.safeNumber(this.analytics?.totalClaims);
  }

  get pendingClaims(): number {
    return this.safeNumber(this.analytics?.totalPendingClaims);
  }

  get approvedClaims(): number {
    return this.safeNumber(this.analytics?.totalApprovedClaims);
  }

  get rejectedClaims(): number {
    return this.safeNumber(this.analytics?.totalRejectedClaims);
  }

  get claimPercentages(): { approved: number; pending: number; rejected: number } {
    const computedTotal = this.totalClaims || this.approvedClaims + this.pendingClaims + this.rejectedClaims;
    if (!computedTotal) {
      return { approved: 0, pending: 0, rejected: 0 };
    }
    return {
      approved: (this.approvedClaims / computedTotal) * 100,
      pending: (this.pendingClaims / computedTotal) * 100,
      rejected: (this.rejectedClaims / computedTotal) * 100,
    };
  }

  get pieGradient(): string {
    const { approved, pending, rejected } = this.claimPercentages;
    const approvedEnd = approved;
    const pendingEnd = approved + pending;
    return `conic-gradient(#22c55e 0 ${approvedEnd}%, #f59e0b ${approvedEnd}% ${pendingEnd}%, #ef4444 ${pendingEnd}% 100%)`;
  }

  private percentOfTotalAmount(value: number): number {
    const total = this.totalApprovedAmount + this.totalPaidAmount;
    if (!total) {
      return 0;
    }
    return Math.min(100, (value / total) * 100);
  }

  get approvedAmountWidth(): number {
    return this.percentOfTotalAmount(this.totalApprovedAmount);
  }

  get paidAmountWidth(): number {
    return this.percentOfTotalAmount(this.totalPaidAmount);
  }

  get totalApprovedAmount(): number {
    return this.safeNumber(this.analytics?.totalApprovedAmount);
  }

  get totalPaidAmount(): number {
    return this.safeNumber(this.analytics?.totalPaidAmount);
  }
}
