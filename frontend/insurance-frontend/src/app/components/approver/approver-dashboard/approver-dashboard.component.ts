import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { ClaimService } from '../../../services/claim.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-approver-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <section class="approver-dashboard">
      <header class="page-header">
        <div>
          <p class="eyebrow">Approver Works</p>
          <h2>Claim Review Workspace</h2>
          <p class="description">Quickly review submitted claims, manage your assigned queue, and keep approvals moving.</p>
        </div>
      </header>

      <div class="summary-grid">
        <article class="summary-card">
          <p class="card-title">New Submitted</p>
          <strong>{{ submittedCount }}</strong>
          <p class="card-note">Claims awaiting assignment to an approver.</p>
          <a routerLink="/approver/pending" class="card-link">Open pending claims</a>
        </article>

        <article class="summary-card">
          <p class="card-title">Pending Review</p>
          <strong>{{ pendingCount }}</strong>
          <p class="card-note">Claims currently under review or waiting for decision.</p>
          <a routerLink="/approver/pending" class="card-link">View review queue</a>
        </article>

        <article class="summary-card">
          <p class="card-title">Assigned To You</p>
          <strong>{{ assignedCount }}</strong>
          <p class="card-note">Claims already taken by your account for review.</p>
          <a routerLink="/approver/pending" class="card-link">Continue my work</a>
        </article>
      </div>

      <div class="dashboard-footer" *ngIf="error">
        <p class="error-message">{{ error }}</p>
      </div>
    </section>
  `,
  styles: [
    ".approver-dashboard { padding: 1.5rem; max-width: 1100px; margin: 0 auto; }",
    ".page-header { display: flex; flex-direction: column; gap: 0.75rem; margin-bottom: 1.5rem; }",
    ".eyebrow { text-transform: uppercase; letter-spacing: 0.14em; font-size: 0.85rem; color: #4f46e5; margin: 0; }",
    ".description { max-width: 42rem; color: #52525b; margin: 0; }",
    ".summary-grid { display: grid; gap: 1rem; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); }",
    ".summary-card { background: var(--surface); border: 1px solid var(--border); border-radius: 1rem; padding: 1.25rem; box-shadow: var(--card-shadow); }",
    ".card-title { margin: 0 0 0.5rem; font-size: 0.95rem; color: var(--text); }",
    ".summary-card strong { display: block; font-size: 2.5rem; margin-bottom: 0.75rem; color: var(--text); }",
    ".card-note { margin: 0 0 1rem; color: var(--muted); line-height: 1.6; }",
    ".card-link { color: var(--primary); font-weight: 600; text-decoration: none; }",
    ".dashboard-footer { margin-top: 1rem; }",
    ".error-message { color: #b91c1c; }",
  ],
})
export class ApproverDashboardComponent implements OnInit {
  submittedCount = 0;
  pendingCount = 0;
  assignedCount = 0;
  error = '';

  constructor(private claimService: ClaimService, public auth: AuthService) {}

  ngOnInit(): void {
    this.loadSummary();
  }

  private loadSummary(): void {
    const approverId = Number(this.auth.userId);
    forkJoin({
      submitted: this.claimService.getClaimsByStatus('SUBMITTED'),
      pending: this.claimService.getClaimsByStatus('PENDING'),
      assigned: this.auth.userId ? this.claimService.getClaimsByApprover(approverId) : of({ data: [] }),
    }).subscribe({
      next: ({ submitted, pending, assigned }) => {
        this.submittedCount = Array.isArray(submitted?.data) ? submitted.data.length : 0;
        this.pendingCount = Array.isArray(pending?.data) ? pending.data.length : 0;
        this.assignedCount = Array.isArray(assigned?.data) ? assigned.data.length : 0;
      },
      error: (error) => {
        console.error('Approver dashboard load failed', error);
        this.error = 'Unable to load approver dashboard summary.';
      },
    });
  }
}
