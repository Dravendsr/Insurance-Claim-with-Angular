import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { ClaimService } from '../../services/claim.service';
import { AuthService } from '../../services/auth.service';
import { Claim } from '../../models/claim';
import { Document } from '../../models/document';

@Component({
  selector: 'app-documents',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './documents.component.html',
  styleUrls: ['./documents.component.css'],
})
export class DocumentsComponent implements OnInit {
  documents: Document[] = [];
  approverClaims: Claim[] = [];
  selectedClaim?: Claim;
  loading = true;
  error = '';

  constructor(
    private api: ApiService,
    private claimService: ClaimService,
    public auth: AuthService
  ) {}

  getMimeType(document: Document): string {
    if (!document.documentName) {
      return 'application/octet-stream';
    }
    const extension = document.documentName.split('.').pop()?.toLowerCase();
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

  createBlobUrl(document: Document): string | null {
    if (!document.documentPath) {
      return null;
    }

    const path = document.documentPath.trim();
    if (path.startsWith('data:')) {
      return path;
    }

    const base64Data = path.replace(/\s+/g, '');
    const mimeType = this.getMimeType(document);
    const byteCharacters = atob(base64Data);
    const byteNumbers = new Array(byteCharacters.length);
    for (let i = 0; i < byteCharacters.length; i++) {
      byteNumbers[i] = byteCharacters.charCodeAt(i);
    }
    const byteArray = new Uint8Array(byteNumbers);
    const blob = new Blob([byteArray], { type: mimeType });
    return URL.createObjectURL(blob);
  }

  viewDocument(doc: Document): void {
    const url = this.createBlobUrl(doc);
    if (!url) {
      return;
    }
    window.open(url, '_blank');
  }

  downloadDocument(doc: Document): void {
    const url = this.createBlobUrl(doc);
    if (!url) {
      return;
    }
    const anchor = window.document.createElement('a');
    anchor.href = url;
    anchor.download = doc.documentName || 'document';
    window.document.body.appendChild(anchor);
    anchor.click();
    window.document.body.removeChild(anchor);
    if (!doc.documentPath?.startsWith('data:')) {
      URL.revokeObjectURL(url);
    }
  }

  ngOnInit(): void {
    if (this.auth.role === 'APPROVER' && this.auth.userId) {
      this.loadApproverClaims();
    } else {
      this.loadPolicyholderDocuments();
    }
  }

  loadPolicyholderDocuments(): void {
    this.loading = true;
    this.error = '';
    const userId = Number(this.auth.userId);
    if (!userId) {
      this.error = 'Unable to determine user for documents.';
      this.documents = [];
      this.loading = false;
      return;
    }

    this.api.get<any>(`/documents/user/${userId}`).subscribe({
      next: (result) => {
        if (!result?.status) {
          this.error = result?.message || 'Unable to load documents.';
          this.documents = [];
        } else {
          this.documents = Array.isArray(result.data) ? result.data : [];
        }
        this.loading = false;
      },
      error: () => {
        this.error = 'Unable to load documents.';
        this.loading = false;
      },
    });
  }

  loadApproverClaims(): void {
    this.loading = true;
    this.error = '';
    const approverId = Number(this.auth.userId);
    this.claimService.getClaimsByApprover(approverId).subscribe({
      next: (response) => {
        if (!response?.status) {
          this.error = response?.message || 'Unable to load assigned claims.';
          this.loading = false;
          return;
        }
        this.approverClaims = Array.isArray(response.data) ? response.data : [];
        this.selectedClaim = this.approverClaims.length ? this.approverClaims[0] : undefined;
        this.loading = false;
      },
      error: () => {
        this.error = 'Unable to load assigned claims.';
        this.loading = false;
      },
    });
  }

  onClaimChange(event: Event): void {
    const select = event.target as HTMLSelectElement | null;
    const claimId = select?.value ?? '';
    this.selectedClaim = this.approverClaims.find((claim) => String(claim.claimId) === claimId);
  }

  get selectedDocuments(): Document[] {
    return this.selectedClaim?.documents ?? [];
  }
}
