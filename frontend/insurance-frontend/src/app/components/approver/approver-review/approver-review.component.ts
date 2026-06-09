import { Component, ChangeDetectorRef, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule, Router } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { ClaimService } from '../../../services/claim.service';
import { AuthService } from '../../../services/auth.service';
import { Claim } from '../../../models/claim';
import { Document } from '../../../models/document';

@Component({
  selector: 'app-approver-review',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <section class="approver-review">
      <header class="page-header">
        <div>
          <p class="eyebrow">Claim Review</p>
          <h2>Review Claim Details</h2>
          <p class="description">Approve or reject the selected claim, review policy details, and inspect supporting documents.</p>
        </div>
      </header>

      <div *ngIf="loading" class="loading-state">Loading claim details...</div>
      <div *ngIf="error" class="alert alert-error">{{ error }}</div>
      <div *ngIf="claim" class="review-panel">
        <div class="details-grid">
          <div class="detail-card">
            <h3>Claim Summary</h3>
            <p><strong>Number:</strong> {{ claim.claimNumber }}</p>
            <p><strong>Status:</strong> {{ claim.claimStatus }}</p>
            <p><strong>Amount:</strong> {{ claim.claimAmount | currency:'INR':'symbol':'1.2-2' }}</p>
            <p><strong>Submitted:</strong> {{ claim.claimDate | date:'mediumDate' }}</p>
            <p><strong>Assigned To:</strong> {{ claim.assignedApprover?.fullName || claim.assignedApprover?.username || 'Unassigned' }}</p>
          </div>
          <div class="detail-card">
            <h3>Policy & Treatment</h3>
            <p><strong>Policy:</strong> {{ claim.insurancePolicy?.policyNumber || 'N/A' }}</p>
            <p><strong>Coverage:</strong> {{ claim.insurancePolicy?.coverageAmount | currency:'INR':'symbol':'1.2-2' }}</p>
            <p><strong>Hospital:</strong> {{ claim.treatment?.hospital?.hospitalName || 'N/A' }}</p>
            <p><strong>Diagnosis:</strong> {{ claim.treatment?.diagnosis || 'N/A' }}</p>
            <p><strong>Provider:</strong> {{ claim.treatment?.doctor?.doctorName || 'N/A' }}</p>
          </div>
          <div class="detail-card">
            <h3>Fraud & Risk</h3>
            <p><strong>Recommendation:</strong> {{ claim.recommendationStatus || 'Unknown' }}</p>
            <p><strong>Score:</strong> {{ claim.recommendationScore || 'N/A' }}</p>
            <p><strong>Reason:</strong> {{ claim.recommendationReason || 'No recommendation data' }}</p>
            <p><strong>Past rejections:</strong> {{ claim.user?.rejectedClaimsCount || 'N/A' }}</p>
          </div>
        </div>

        <div class="document-section">
          <h3>Supporting Documents</h3>
          <p *ngIf="!claim.documents?.length">No documents uploaded yet.</p>
          <table *ngIf="claim.documents?.length" class="document-table">
            <thead>
              <tr>
                <th>#</th>
                <th>Name</th>
                <th>Type</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let document of claim.documents; index as i">
                <td>{{ i + 1 }}</td>
                <td>{{ document.documentName || 'Document' }}</td>
                <td>{{ document.documentType || 'Unknown' }}</td>
                <td>
                  <button type="button" class="action-button view-button" (click)="viewDocument(document)">View</button>
                  <button type="button" class="action-button download-button" (click)="downloadDocument(document)">Download</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="decision-panel">
          <h3>Decision</h3>
          <div *ngIf="claim.claimStatus === 'APPROVED' || claim.claimStatus === 'REJECTED'" class="readonly-message">
            Claim has already been {{ claim.claimStatus.toLowerCase() }}.
          </div>

          <div *ngIf="claim.claimStatus !== 'APPROVED' && claim.claimStatus !== 'REJECTED'">
            <div class="form-field">
              <label for="approvedAmount">Approved Amount</label>
              <input id="approvedAmount" type="number" [(ngModel)]="approvedAmount" min="0" [max]="claim.claimAmount ?? null" />
            </div>

            <div class="form-field">
              <label for="approverComment">Comment</label>
              <textarea id="approverComment" [(ngModel)]="approverComment" rows="4" placeholder="Add feedback or comments"></textarea>
            </div>

            <div class="form-buttons">
              <button class="primary-button" (click)="approve()" [disabled]="decisionLoading">Approve</button>
              <button class="danger-button" (click)="reject()" [disabled]="decisionLoading">Reject</button>
            </div>
          </div>
        </div>
      </div>
    </section>
  `,
  styles: [
    ".approver-review { padding: 1.5rem; max-width: 1180px; margin: 0 auto; }",
    ".page-header { margin-bottom: 1.5rem; }",
    ".eyebrow { margin: 0 0 0.5rem; color: var(--primary); text-transform: uppercase; letter-spacing: 0.12em; font-size: 0.8rem; }",
    ".description { margin: 0; color: var(--muted); max-width: 46rem; }",
    ".alert-error { background: #fee2e2; color: #b91c1c; border: 1px solid #fecaca; border-radius: 0.75rem; padding: 1rem; margin-bottom: 1.25rem; }",
    ".loading-state { padding: 1rem; color: #374151; }",
    ".review-panel { display: grid; gap: 1.5rem; }",
    ".details-grid { display: grid; gap: 1rem; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); }",
    ".detail-card { background: var(--surface); border: 1px solid var(--border); border-radius: 1rem; padding: 1.25rem; }",
    ".detail-card h3 { margin-top: 0; margin-bottom: 0.75rem; font-size: 1rem; }",
    ".detail-card p { margin: 0.5rem 0; color: var(--muted); }",
    ".document-section { background: var(--surface); border: 1px solid var(--border); border-radius: 1rem; padding: 1.25rem; }",
    ".document-table { width: 100%; border-collapse: collapse; margin-top: 1rem; }",
    ".document-table th, .document-table td { padding: 0.85rem 0.75rem; border-bottom: 1px solid var(--border); text-align: left; color: var(--text); }",
    ".document-table th { background: var(--surface-strong); color: var(--text); font-weight: 600; }",
    ".action-button { border: none; border-radius: 9999px; padding: 0.5rem 1rem; font-weight: 600; cursor: pointer; margin-right: 0.5rem; }",
    ".view-button { background: var(--primary); color: #ffffff; }",
    ".download-button { background: #10b981; color: #ffffff; }",
    ".decision-panel { background: var(--surface); border: 1px solid var(--border); border-radius: 1rem; padding: 1.25rem; }",
    ".decision-panel h3 { margin-top: 0; margin-bottom: 1rem; }",
    ".form-field { margin-bottom: 1rem; display: grid; gap: 0.5rem; }",
    ".form-field label { font-weight: 600; color: var(--text); }",
    ".form-field input, .form-field textarea { width: 100%; border: 1px solid var(--border); border-radius: 0.75rem; padding: 0.85rem; font: inherit; background: var(--surface); color: var(--text); }",
    ".form-buttons { display: flex; gap: 0.75rem; flex-wrap: wrap; }",
    ".primary-button, .danger-button { border: none; border-radius: 9999px; padding: 0.75rem 1.25rem; font-weight: 600; cursor: pointer; }",
    ".primary-button { background: var(--primary); color: #ffffff; }",
    ".danger-button { background: #dc2626; color: #ffffff; }",
    ".readonly-message { padding: 1rem; border-radius: 0.75rem; background: var(--surface-strong); color: var(--text); border: 1px solid var(--border); margin-bottom: 1rem; }",
  ],
})
export class ApproverReviewComponent implements OnInit {
  claim?: Claim;
  approvedAmount: number | null = null;
  approverComment = '';
  rejectionReason = '';
  loading = false;
  decisionLoading = false;
  error = '';

  constructor(
    private route: ActivatedRoute,
    private claimService: ClaimService,
    private auth: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    const claimId = Number(this.route.snapshot.paramMap.get('id'));
    if (claimId) {
      this.loadClaim(claimId);
    } else {
      this.error = 'Invalid claim ID provided.';
    }
  }

  private loadClaim(claimId: number): void {
    this.loading = true;
    this.error = '';
    this.claimService
      .getClaimById(claimId)
      .pipe(finalize(() => {
        this.loading = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: (response) => {
          if (!response?.status) {
            this.error = response?.message || 'Unable to load claim details.';
            return;
          }
          this.claim = response.data;
          this.approvedAmount = this.claim?.claimAmount ?? 0;
        },
        error: (error) => {
          console.error('Error loading claim', error);
          this.error = 'Unable to load claim details. Please try again later.';
        },
      });
  }

  private getMimeType(document: Document): string {
    const extension = document.documentName?.split('.').pop()?.toLowerCase();
    switch (extension) {
      case 'pdf':
        return 'application/pdf';
      case 'png':
        return 'image/png';
      case 'jpg':
      case 'jpeg':
        return 'image/jpeg';
      case 'gif':
        return 'image/gif';
      default:
        return 'application/octet-stream';
    }
  }

  private async createBlobUrl(document: Document): Promise<string | null> {
    if (!document.documentPath) {
      return null;
    }

    const path = document.documentPath.trim();
    if (path.startsWith('data:') || path.startsWith('http') || path.startsWith('/') || path.startsWith('blob:')) {
      return path;
    }

    const mimeType = this.getMimeType(document);
    const dataUrl = path.includes(',') ? path : `data:${mimeType};base64,${path.replace(/\s+/g, '')}`;

    try {
      const resp = await fetch(dataUrl);
      if (!resp.ok) {
        throw new Error('fetch failed');
      }
      const blob = await resp.blob();
      return URL.createObjectURL(blob);
    } catch (error) {
      try {
        const base64Data = dataUrl.split(',').pop() || '';
        const byteCharacters = atob(base64Data.replace(/\s+/g, ''));
        const byteNumbers = new Array(byteCharacters.length);
        for (let i = 0; i < byteCharacters.length; i++) {
          byteNumbers[i] = byteCharacters.charCodeAt(i);
        }
        const byteArray = new Uint8Array(byteNumbers);
        const blob = new Blob([byteArray], { type: mimeType });
        return URL.createObjectURL(blob);
      } catch (err) {
        return dataUrl;
      }
    }
  }

  async viewDocument(document: Document): Promise<void> {
    const path = document.documentPath?.trim() || '';
    let url = '';

    if (path.startsWith('/')) {
      const id = (document as any).documentId ?? (document as any).id;
      url = id ? `/api/documents/file/${id}` : path;
    } else if (path.startsWith('http://') || path.startsWith('https://')) {
      url = path;
    } else {
      const created = await this.createBlobUrl(document);
      if (!created) {
        return;
      }
      url = created;
    }

    const mime = this.getMimeType(document);
    const encodedUrl = url.replace(/"/g, '%22');
    const win = window.open('', '_blank');
    if (!win) {
      return;
    }

    const viewer = mime === 'application/pdf'
      ? `<embed src="${encodedUrl}" type="${mime}" width="100%" height="100%" />`
      : mime.startsWith('image/')
      ? `<img src="${encodedUrl}" style="max-width:100%;height:auto;display:block;margin:0 auto;"/>`
      : `<a href="${encodedUrl}" target="_blank">Open document</a>`;

    const html = `<!doctype html><html><head><title>${document.documentName || 'Document'}</title><meta charset="utf-8" /><style>html,body{height:100%;margin:0;background:#111;color:#fff}img,embed{display:block;max-height:100vh;margin:0 auto}</style></head><body>${viewer}</body></html>`;
    win.document.open();
    win.document.write(html);
    win.document.close();
  }

  async downloadDocument(document: Document): Promise<void> {
    const path = document.documentPath?.trim() || '';
    let url = '';

    if (path.startsWith('/')) {
      const id = (document as any).documentId ?? (document as any).id;
      url = id ? `/api/documents/file/${id}` : path;
    } else if (path.startsWith('http://') || path.startsWith('https://')) {
      url = path;
    } else {
      const created = await this.createBlobUrl(document);
      if (!created) {
        return;
      }
      url = created;
    }

    const anchor = window.document.createElement('a');
    anchor.href = url;
    anchor.download = document.documentName || 'document';
    window.document.body.appendChild(anchor);
    anchor.click();
    window.document.body.removeChild(anchor);

    if (!document.documentPath?.startsWith('data:') && !path.startsWith('http://') && !path.startsWith('https://')) {
      setTimeout(() => URL.revokeObjectURL(url), 1000);
    }
  }

  approve(): void {
    if (!this.claim || this.claim.claimStatus === 'APPROVED' || this.claim.claimStatus === 'REJECTED') {
      return;
    }
    this.decisionLoading = true;
    this.error = '';
    const claimId = this.claim.claimId ?? 0;

    if (!this.claim?.claimId) {
      this.error = 'Invalid claim identifier.';
      this.decisionLoading = false;
      return;
    }

    if (this.approvedAmount == null || this.approvedAmount < 0) {
      this.error = 'Please enter a valid approved amount.';
      this.decisionLoading = false;
      return;
    }

    this.claimService
      .approveClaim(claimId, this.approvedAmount ?? 0, this.approverComment)
      .pipe(finalize(() => {
        this.decisionLoading = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: (response) => {
          if (response?.status) {
            this.router.navigate(['/approver/pending']);
          } else {
            this.error = response?.message || 'Approval failed.';
          }
        },
        error: (error) => {
          console.error('Approval error', error);
          this.error = 'Unable to approve claim. Please check the form and try again.';
        },
      });
  }

  reject(): void {
    if (!this.claim || this.claim.claimStatus === 'APPROVED' || this.claim.claimStatus === 'REJECTED') {
      return;
    }
    this.decisionLoading = true;
    this.error = '';
    const claimId = this.claim.claimId ?? 0;

    if (!this.claim?.claimId) {
      this.error = 'Invalid claim identifier.';
      this.decisionLoading = false;
      return;
    }

    if (!this.approverComment) {
      this.error = 'Please add a rejection reason before rejecting the claim.';
      this.decisionLoading = false;
      return;
    }

    this.claimService
      .rejectClaim(claimId, this.approverComment)
      .pipe(finalize(() => {
        this.decisionLoading = false;
        this.cdr.detectChanges();
      }))
      .subscribe({
        next: (response) => {
          if (response?.status) {
            this.router.navigate(['/approver/pending']);
          } else {
            this.error = response?.message || 'Rejection failed.';
          }
        },
        error: (error) => {
          console.error('Rejection error', error);
          this.error = 'Unable to reject claim. Please try again.';
        },
      });
  }
}
