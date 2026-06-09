import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { forkJoin, Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { AuthService } from '../../services/auth.service';
import { ApiService } from '../../services/api.service';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit {
  role: string | null = null;
  username: string | null = null;
  totalPolicies = 0;
  activePoliciesCount = 0;
  totalClaims = 0;
  approvedClaims = 0;
  pendingClaims = 0;
  rejectedClaims = 0;
  totalReimbursement = 0;
  isLoading = true;
  userEmail: string | null = null;
  userFullName: string | null = null;
  userStatus: string | null = null;

  constructor(
    private auth: AuthService,
    private api: ApiService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.role = this.auth.role;
    this.username = this.auth.username;
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.isLoading = true;
    this.totalPolicies = 0;
    this.activePoliciesCount = 0;
    this.totalClaims = 0;
    this.approvedClaims = 0;
    this.pendingClaims = 0;
    this.rejectedClaims = 0;
    this.totalReimbursement = 0;

    const userId = Number(this.auth.userId);
    const policyPath = userId ? `/user-policies/user/${userId}` : '/policies';
    const claimsPath = userId ? `/claims/user/${userId}` : '/claims';

    const policies$ = this.api.get<any>(policyPath);
    const claims$ = this.api.get<any>(claimsPath);
    const payments$ = this.api.get<any>('/payments');

    forkJoin({ policies: policies$, claims: claims$, payments: payments$ })
      .pipe(
        finalize(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: ({ policies, claims, payments }) => {
          if (policies?.status) {
            const policyData = Array.isArray(policies.data) ? policies.data : policies.data ? [policies.data] : [];
            this.totalPolicies = policyData.length;
            this.activePoliciesCount = policyData.filter((policy: any) => policy.insurancePolicy?.policyStatus === 'ACTIVE' || policy.policyStatus === 'ACTIVE').length;
          }

          if (claims?.status) {
            const claimData = Array.isArray(claims.data) ? claims.data : claims.data ? [claims.data] : [];
            this.totalClaims = claimData.length;
            this.approvedClaims = claimData.filter((claim: any) => ['APPROVED', 'PAID', 'PARTIALLY_APPROVED'].includes(claim.claimStatus || claim.status)).length;
            this.pendingClaims = claimData.filter((claim: any) => ['SUBMITTED', 'UNDER_REVIEW', 'PENDING'].includes(claim.claimStatus || claim.status)).length;
            this.rejectedClaims = claimData.filter((claim: any) => (claim.claimStatus || claim.status) === 'REJECTED').length;
          }

          if (payments?.status) {
            const paymentData = Array.isArray(payments.data) ? payments.data : payments.data ? [payments.data] : [];
            const filteredPayments = paymentData.filter((payment: any) => {
              if (!userId) {
                return true;
              }
              return (
                payment.user?.userId === userId ||
                payment.userId === userId ||
                payment.claim?.user?.userId === userId ||
                payment.claim?.userId === userId
              );
            });
            this.totalReimbursement = filteredPayments.reduce((sum: number, payment: any) => sum + (payment.paymentAmount ?? payment.amount ?? 0), 0);
          }

          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error('Error loading dashboard metrics:', error);
        },
      });

    if (userId) {
      this.api.get<any>(`/users/${userId}`).subscribe({
        next: (response) => {
          if (response?.status && response.data) {
            this.userFullName = response.data.fullName || this.username;
            // persist full name for other UI components
            if (response.data.fullName) {
              this.auth.setFullName(response.data.fullName);
            }
            this.userEmail = response.data.email || this.auth.email || 'N/A';
            this.userStatus = response.data.accountStatus || 'ACTIVE';
            this.cdr.detectChanges();
          }
        },
        error: (error) => {
          console.warn('Unable to load user profile for dashboard:', error);
        },
      });
    }
  }

  get title(): string {
    if (this.role === 'ADMIN') {
      return 'Administrator Dashboard';
    }
    if (this.role === 'APPROVER') {
      return 'Approver Dashboard';
    }
    if (this.role === 'POLICYHOLDER') {
      return (this.userFullName && this.userFullName.trim().length > 0)
        ? `${this.userFullName} Dashboard`
        : 'Policyholder Dashboard';
    }
    return 'Policyholder Dashboard';
  }

  get subtitle(): string {
    if (this.role === 'ADMIN') {
      return 'Manage users, policies, hospitals, doctors, and payments from one place.';
    }
    if (this.role === 'APPROVER') {
      return 'Review pending claims, verify documents, and approve or reject claims.';
    }
    return 'View your policy details, active claims, and reimbursement status.';
  }

  get cards() {
    if (this.role === 'ADMIN') {
      return [
        {
          title: 'User Management',
          description: 'Create and manage administrators, approvers, and policyholders.',
          route: '/users',
        },
        {
          title: 'Payment Processing',
          description: 'Process reimbursements and view settled payments.',
          route: '/payments',
        },
        {
          title: 'Administrator Profile',
          description: 'View your account details and administrative status.',
          route: '/user-details',
        },
      ];
    }

    if (this.role === 'APPROVER') {
      return [
        {
          title: 'Pending Claims',
          description: 'See claims waiting for approval and review documentation.',
          route: '/claims',
        },
        {
          title: 'My Tasks',
          description: 'Track active claim verifications and approve or reject.',
          route: '/claims',
        },
      ];
    }

    return [
      {
        title: 'My Policies',
        description: 'View the policies assigned to you and coverage details.',
        route: '/policies',
      },
      {
        title: 'My Claims',
        description: 'Check claim status, history, and reimbursement details.',
        route: '/claims',
      },
      {
        title: 'Document Upload',
        description: 'Upload bills, prescriptions and supporting documents for claims.',
        route: '/documents',
      },
    ];
  }
}
