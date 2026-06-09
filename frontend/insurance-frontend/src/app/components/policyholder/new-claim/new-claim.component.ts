import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PolicyholderService } from '../../../services/policyholder.service';
import { AuthService } from '../../../services/auth.service';
import { UserPolicy } from '../../../models/user-policy';
import { finalize } from 'rxjs/operators';

interface UploadItem {
  file: File | null;
  dataUrl: string;
  error: string;
}

@Component({
  selector: 'app-new-claim',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './new-claim.component.html',
  styleUrls: ['./new-claim.component.css'],
})
export class NewClaimComponent implements OnInit {
  currentStep = 1;
  stepTitles = [
    'Policy Selection',
    'Treatment Details',
    'Doctor Details',
    'Hospital Details',
    'Claim Amount',
    'Document Upload',
    'Duplicate Review',
  ];
  isLoading = true;
  submitLoading = false;
  submitSuccess = false;
  popupMessage = '';
  popupType: 'success' | 'error' | '' = '';
  showDuplicateConfirmation = false;
  policyList: UserPolicy[] = [];
  claimHistory: any[] = [];
  selectedPolicyId: number | null = null;

  form = {
    treatmentName: '',
    diagnosis: '',
    treatmentDate: '',
    treatmentCost: 0,
    doctorName: '',
    doctorSpecialization: '',
    doctorRegistrationNumber: '',
    hospitalName: '',
    hospitalAddress: '',
    admissionDate: '',
    dischargeDate: '',
    requestedAmount: 0,
    documents: {
      medicalBill: { file: null, dataUrl: '', error: '' } as UploadItem,
      treatmentBill: { file: null, dataUrl: '', error: '' } as UploadItem,
      hospitalBill: { file: null, dataUrl: '', error: '' } as UploadItem,
      doctorPrescription: { file: null, dataUrl: '', error: '' } as UploadItem,
    },
  };

  constructor(
    private auth: AuthService,
    private policyService: PolicyholderService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadPolicyholderData();
  }

  loadPolicyholderData(): void {
    const userId = Number(this.auth.userId);
    if (!userId) {
      this.showPopup('Unable to resolve logged-in user. Please log in again.', 'error');
      this.isLoading = false;
      return;
    }

    this.isLoading = true;
    this.policyService.getPoliciesForUser(userId).subscribe({
      next: (response) => {
        if (response?.status && response.data) {
          this.policyList = Array.isArray(response.data) ? response.data : [response.data];
        } else {
          this.policyList = [];
          this.showPopup(response?.message || 'Unable to load your policies.', 'error');
        }
      },
      error: (error) => {
        console.error('Error loading policies:', error);
        this.showPopup('Unable to load your policies. Please refresh.', 'error');
      },
      complete: () => {
        this.isLoading = false;
        this.cdr.detectChanges();
      },
    });

    this.policyService.getClaimsForUser(userId).subscribe({
      next: (response) => {
        if (response?.status && response.data) {
          this.claimHistory = Array.isArray(response.data) ? response.data : [response.data];
        }
      },
      error: () => {
        console.warn('Unable to load claim history for duplicate check.');
      },
    });
  }

  get currentStepTitle(): string {
    const titles = [
      'Policy Selection',
      'Treatment Details',
      'Doctor Details',
      'Hospital Details',
      'Claim Amount',
      'Document Upload',
      'Duplicate Claim Review',
    ];
    return titles[this.currentStep - 1] || 'New Claim';
  }

  selectPolicy(policyId: number | undefined | null): void {
    this.selectedPolicyId = policyId ?? null;
    this.cdr.detectChanges();
  }

  goToStep(step: number): void {
    if (step < this.currentStep) {
      this.currentStep = step;
    }
  }

  nextStep(): void {
    if (!this.validateCurrentStep()) {
      return;
    }
    if (this.currentStep === 7) {
      this.confirmDuplicateAndSubmit();
      return;
    }
    this.currentStep = Math.min(7, this.currentStep + 1);
  }

  previousStep(): void {
    if (this.currentStep > 1) {
      this.currentStep -= 1;
    }
  }

  validateCurrentStep(): boolean {
    switch (this.currentStep) {
      case 1:
        if (!this.selectedPolicyId) {
          this.showPopup('Please select one of your active policies to continue.', 'error');
          return false;
        }
        return true;
      case 2:
        if (!this.form.treatmentName.trim()) {
          this.showPopup('Please provide the treatment name.', 'error');
          return false;
        }
        if (!this.form.diagnosis.trim()) {
          this.showPopup('Please provide the diagnosis.', 'error');
          return false;
        }
        if (!this.form.treatmentDate) {
          this.showPopup('Please select the treatment date.', 'error');
          return false;
        }
        if (this.form.treatmentCost <= 0) {
          this.showPopup('Please enter a valid treatment cost.', 'error');
          return false;
        }
        return true;
      case 3:
        if (!this.form.doctorName.trim()) {
          this.showPopup('Please enter the doctor name.', 'error');
          return false;
        }
        if (!this.form.doctorSpecialization.trim()) {
          this.showPopup('Please enter the doctor specialization.', 'error');
          return false;
        }
        if (!this.form.doctorRegistrationNumber.trim()) {
          this.showPopup('Please enter the registration number.', 'error');
          return false;
        }
        return true;
      case 4:
        if (!this.form.hospitalName.trim()) {
          this.showPopup('Please enter the hospital name.', 'error');
          return false;
        }
        if (!this.form.hospitalAddress.trim()) {
          this.showPopup('Please enter the hospital address.', 'error');
          return false;
        }
        if (!this.form.admissionDate) {
          this.showPopup('Please select the admission date.', 'error');
          return false;
        }
        if (!this.form.dischargeDate) {
          this.showPopup('Please select the discharge date.', 'error');
          return false;
        }
        return true;
      case 5:
        if (this.form.requestedAmount <= 0) {
          this.showPopup('Please enter the requested claim amount.', 'error');
          return false;
        }
        const selectedPolicy = this.policyList.find((policy) => policy.insurancePolicy?.policyId === this.selectedPolicyId);
        const coverageAmount = selectedPolicy?.insurancePolicy?.coverageAmount || 0;
        if (this.form.requestedAmount > coverageAmount) {
          this.showPopup('Requested amount exceeds policy coverage. Please lower the amount.', 'error');
          return false;
        }
        return true;
      case 6:
        const requiredFiles = ['medicalBill', 'treatmentBill', 'hospitalBill', 'doctorPrescription'];
        for (const key of requiredFiles) {
          const item = this.form.documents[key as keyof typeof this.form.documents];
          if (!item.file) {
            this.showPopup('All required documents must be uploaded before continuing.', 'error');
            return false;
          }
          if (item.error) {
            this.showPopup(item.error, 'error');
            return false;
          }
        }
        return true;
      default:
        return true;
    }
  }

  onFileChange(type: keyof typeof this.form.documents, event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0] ?? null;
    const item = this.form.documents[type];

    if (!file) {
      item.file = null;
      item.dataUrl = '';
      item.error = 'Please select a document file.';
      return;
    }

    const allowedTypes = ['application/pdf', 'image/jpeg', 'image/jpg', 'image/png'];
    const extension = file.name.split('.').pop()?.toLowerCase() || '';
    const allowedExtensions = ['pdf', 'jpg', 'jpeg', 'png'];

    if (!allowedTypes.includes(file.type) && !allowedExtensions.includes(extension)) {
      item.file = null;
      item.dataUrl = '';
      item.error = 'Only PDF, JPG, JPEG, and PNG files are supported.';
      return;
    }

    if (file.size > 5 * 1024 * 1024) {
      item.file = null;
      item.dataUrl = '';
      item.error = 'Each file must be smaller than 5MB.';
      return;
    }

    item.file = file;
    item.error = '';
    const reader = new FileReader();
    reader.onload = () => {
      item.dataUrl = reader.result as string;
      this.cdr.detectChanges();
    };
    reader.readAsDataURL(file);
  }

  confirmDuplicateAndSubmit(): void {
    if (this.hasExistingClaimForSelectedPolicy()) {
      this.showDuplicateConfirmation = true;
      return;
    }
    this.submitClaim();
  }

  hasExistingClaimForSelectedPolicy(): boolean {
    return this.claimHistory.some(
      (claim) => claim.insurancePolicy?.policyId === this.selectedPolicyId
    );
  }

  continueDuplicateSubmission(): void {
    this.showDuplicateConfirmation = false;
    this.submitClaim();
  }

  cancelDuplicateSubmission(): void {
    this.showDuplicateConfirmation = false;
  }

  goToClaims(): void {
    this.router.navigate(['/claims']);
  }

  submitClaim(): void {
    if (!this.validateCurrentStep()) {
      return;
    }
    const userId = Number(this.auth.userId);
    if (!userId || !this.selectedPolicyId) {
      this.showPopup('Unable to resolve user or policy. Please refresh and try again.', 'error');
      return;
    }

    this.submitLoading = true;
    const payload = {
      claimNumber: `CL-${Date.now()}`,
      claimAmount: Number(this.form.requestedAmount || this.form.treatmentCost),
      approvedAmount: 0,
      claimStatus: 'SUBMITTED',
      user: { userId },
      insurancePolicy: { policyId: this.selectedPolicyId },
      treatment: {
        diagnosis: this.form.diagnosis,
        treatmentDescription: this.form.treatmentName,
        treatmentAmount: Number(this.form.treatmentCost),
        treatmentDate: this.form.treatmentDate,
        doctorName: this.form.doctorName,
        doctorSpecialization: this.form.doctorSpecialization,
        doctorQualification: 'MD',
        doctorExperienceYears: 0,
        hospitalName: this.form.hospitalName,
        hospitalAddress: this.form.hospitalAddress,
        hospitalPhone: '',
      },
    };

    this.policyService.createClaim(payload).subscribe({
      next: async (response) => {
        if (!response?.status || !response.data?.claimId) {
          this.showPopup(response?.message || 'Unable to submit claim.', 'error');
          this.submitLoading = false;
          return;
        }

        const claimId = response.data.claimId;
        try {
          await this.uploadAllDocuments(claimId);
          this.showPopup('Claim submitted successfully.', 'success');
          this.submitSuccess = true;
          // after showing success popup, redirect to user dashboard
          setTimeout(() => {
            this.router.navigate(['/dashboard']);
          }, 2000);
        } catch (uploadError) {
          console.error(uploadError);
          this.showPopup('Claim submitted, but documents could not be uploaded.', 'error');
          this.submitSuccess = true;
          // still redirect to dashboard after brief delay
          setTimeout(() => {
            this.router.navigate(['/dashboard']);
          }, 2200);
        }
      },
      error: (error) => {
        console.error('Claim submission error:', error);
        this.showPopup('Claim submission failed. Please check the form and try again.', 'error');
      },
      complete: () => {
        this.submitLoading = false;
        this.cdr.detectChanges();
      },
    });
  }

  private uploadAllDocuments(claimId: number): Promise<void[]> {
    const uploadEntries = Object.entries(this.form.documents) as [
      keyof typeof this.form.documents,
      UploadItem
    ][];

    const promises = uploadEntries.map(async ([key, item]) => {
      if (!item.file) {
        return Promise.resolve();
      }

      const dataUrl = item.dataUrl || (await this.readFileAsDataUrl(item.file));
      const documentType = this.getDocumentTypeLabel(key);
      return this.policyService.uploadDocument({
        documentName: item.file.name,
        documentType,
        documentPath: dataUrl,
        claim: { claimId },
      }).toPromise();
    });

    return Promise.all(promises).then(() => []);
  }

  private getDocumentTypeLabel(key: keyof typeof this.form.documents): string {
    switch (key) {
      case 'medicalBill':
        return 'Medical Bill';
      case 'treatmentBill':
        return 'Treatment Bill';
      case 'hospitalBill':
        return 'Hospital Bill';
      case 'doctorPrescription':
        return 'Doctor Prescription';
      default:
        return 'Claim Document';
    }
  }

  private readFileAsDataUrl(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = () => {
        resolve(reader.result as string);
      };
      reader.onerror = () => reject(reader.error);
      reader.readAsDataURL(file);
    });
  }

  showPopup(message: string, type: 'success' | 'error'): void {
    this.popupMessage = message;
    this.popupType = type;
    setTimeout(() => this.clearPopup(), 5500);
  }

  clearPopup(): void {
    this.popupMessage = '';
    this.popupType = '';
  }
}
