import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin, of } from 'rxjs';
import { catchError, finalize, map } from 'rxjs/operators';
import { ApiService } from '../../services/api.service';
import { PolicyholderService } from '../../services/policyholder.service';
import { InsurancePolicy } from '../../models/insurance-policy';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-policies',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './policies.component.html',
  styleUrls: ['./policies.component.css'],
})
export class PoliciesComponent implements OnInit {
  policies: any[] = [];
  loading = true;
  error = '';
  flashMessage = '';
  flashType: 'success' | 'error' | '' = '';
  popupTimer: any;
  showAddForm = false;
  role: string | null = null;
  showEditForm = false;
  editingPolicy: InsurancePolicy | null = null;
  newPolicy: InsurancePolicy = {
    policyNumber: '',
    policyName: '',
    policyType: '',
    coverageAmount: 0,
    premiumAmount: 0,
    benefits: '',
    policyStatus: 'ACTIVE',
    startDate: '',
    endDate: ''
  };

  constructor(
    private api: ApiService,
    private auth: AuthService,
    private policyholderService: PolicyholderService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.role = this.auth.role;
    this.loadPolicies();
  }

  loadPolicies(): void {
    this.loading = true;
    this.error = '';
    this.policies = [];

    const policyRequest =
      this.role === 'POLICYHOLDER' && this.auth.userId
        ? this.policyholderService.getPoliciesForUser(Number(this.auth.userId))
        : this.api.get<any>('/policies');

    policyRequest
      .pipe(
        finalize(() => {
          this.loading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (response) => {
          if (!response?.status) {
            this.error = response?.message || 'Unable to load policies from the database.';
            return;
          }

          const raw = Array.isArray(response.data)
            ? response.data
            : response.data
            ? [response.data]
            : [];

          this.policies = raw.map((item: any) => this.buildDisplayPolicy(item));
          const fetchNeeded = this.policies.some(p => p.policyId && !p.policyNumber);

          if (fetchNeeded) {
            const requests = this.policies.map((policy) => {
              if (policy.policyId && !policy.policyNumber) {
                return this.api.get<any>(`/policies/${policy.policyId}`).pipe(
                  map((policyResponse) => {
                    const fetchedPolicy = policyResponse?.data ?? null;
                    return fetchedPolicy ? this.buildDisplayPolicy(fetchedPolicy) : policy;
                  }),
                  catchError(() => of(policy))
                );
              }
              return of(policy);
            });

            forkJoin(requests).subscribe((updatedPolicies) => {
              this.policies = updatedPolicies;
              this.cdr.detectChanges();
            });
          }

          console.log('PoliciesComponent loaded policies', response.data, this.policies);
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error fetching policies:', err);
          this.error = 'Unable to load policies from the database. Please refresh or try again.';
        },
      });
  }

  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;
    if (this.showAddForm) this.showEditForm = false;
  }

  editPolicy(policy: InsurancePolicy): void {
    this.editingPolicy = { ...policy };
    this.showEditForm = true;
    this.showAddForm = false;
  }

  updatePolicy(): void {
    if (!this.editingPolicy || !this.editingPolicy.policyId) return;

    const payload: any = { ...this.editingPolicy };
    // Ensure numeric fields are sent as numbers
    if (payload.coverageAmount !== undefined) payload.coverageAmount = Number(payload.coverageAmount) || 0;
    if (payload.premiumAmount !== undefined) payload.premiumAmount = Number(payload.premiumAmount) || 0;
    if (payload.policyId !== undefined) payload.policyId = Number(payload.policyId);

    console.log('Updating policy with payload:', payload);

    this.api.put<any>(`/policies/${this.editingPolicy.policyId}`, payload).subscribe({
      next: (response) => {
        if (response.status) {
          const index = this.policies.findIndex(p => p.policyId === this.editingPolicy?.policyId);
          if (index !== -1 && response.data) {
            this.policies[index] = response.data;
            this.cdr.detectChanges();
          }
          this.showEditForm = false;
          this.editingPolicy = null;
          this.showPopup('Policy updated successfully.', 'success');
          console.log('Policy updated successfully', response.message || response);
        } else {
          this.showPopup(response.message || 'Failed to update policy.', 'error');
        }
      },
      error: (err) => {
        console.error('Update Policy Error:', err);
        const msg = err.error?.message || err.message || 'An error occurred while updating the policy. Check the console (F12) for details.';
        this.showPopup(msg, 'error');
      }
    });
  }

  addPolicy(): void {

  const policyNumber = this.newPolicy.policyNumber?.trim();
  const policyName = this.newPolicy.policyName?.trim();
  const policyType = this.newPolicy.policyType?.trim();

  if (!policyNumber || !policyName || !policyType) {
    this.showPopup(
      'Please fill in required fields: Number, Name, Type.',
      'error'
    );
    return;
  }

  const payload = {
    ...this.newPolicy,
    policyNumber,
    policyName,
    policyType
  };

  console.log('Submitting Policy:', payload);

  this.api.post<any>('/policies', payload).subscribe({
    next: (response) => {

      console.log('Policy Create Response:', response);

      if (response?.status === true || response?.data) {

        const createdPolicy = response.data ?? response;

        if (createdPolicy) {
          this.policies.push(createdPolicy);
        } else {
          this.loadPolicies();
        }

        this.showAddForm = false;

        this.newPolicy = {
          policyNumber: '',
          policyName: '',
          policyType: '',
          coverageAmount: 0,
          premiumAmount: 0,
          benefits: '',
          policyStatus: 'ACTIVE',
          startDate: '',
          endDate: ''
        };

        this.cdr.detectChanges();

        this.showPopup(
          'Policy added successfully.',
          'success'
        );

      } else {
        this.showPopup(
          response?.message || 'Failed to add policy.',
          'error'
        );
      }
    },
    error: (err) => {

      console.error('Add Policy Error:', err);

      this.showPopup(
        err?.error?.message ||
        'An error occurred while adding the policy.',
        'error'
      );
    }
  });
}
  removePolicy(id: number | undefined): void {
    if (!id) return;

    if (confirm('Are you sure you want to remove this policy?')) {
      this.api.delete<any>(`/policies/${id}`).subscribe({
        next: (response) => {
          if (response.status) {
            this.policies = this.policies.filter(p => p.policyId !== id);
            this.cdr.detectChanges();
            this.showPopup('Policy removed successfully.', 'success');
          } else {
            this.showPopup(response.message || 'Failed to remove policy.', 'error');
          }
        },
        error: () => this.showPopup('An error occurred while removing the policy.', 'error')
      });
    }
  }

  private extractPolicy(item: any): any {
    if (!item) {
      return null;
    }

    if (item.insurancePolicy) {
      return item.insurancePolicy;
    }

    if (item.insurance_policy) {
      return item.insurance_policy;
    }

    if (item.policy) {
      return item.policy;
    }

    return item;
  }

  private buildDisplayPolicy(item: any): any {
    const policy = this.extractPolicy(item) || {};

    return {
      policyId:
        policy.policyId ??
        item.insurancePolicy?.policyId ??
        item.insurance_policy?.policyId ??
        item.policy?.policyId,
      policyNumber:
        policy.policyNumber ??
        item.insurancePolicy?.policyNumber ??
        item.insurance_policy?.policyNumber ??
        item.policy?.policyNumber,
      policyName:
        policy.policyName ??
        item.insurancePolicy?.policyName ??
        item.insurance_policy?.policyName ??
        item.policy?.policyName,
      policyType:
        policy.policyType ??
        item.insurancePolicy?.policyType ??
        item.insurance_policy?.policyType ??
        item.policy?.policyType,
      coverageAmount:
        policy.coverageAmount ??
        item.insurancePolicy?.coverageAmount ??
        item.insurance_policy?.coverageAmount ??
        item.policy?.coverageAmount,
      premiumAmount:
        policy.premiumAmount ??
        item.insurancePolicy?.premiumAmount ??
        item.insurance_policy?.premiumAmount ??
        item.policy?.premiumAmount,
      benefits:
        policy.benefits ??
        item.insurancePolicy?.benefits ??
        item.insurance_policy?.benefits ??
        item.policy?.benefits,
      policyStatus:
        policy.policyStatus ??
        item.policyActiveStatus ??
        item.insurancePolicy?.policyStatus ??
        item.insurance_policy?.policyStatus ??
        item.policy?.policyStatus,
      startDate:
        policy.startDate ??
        item.insurancePolicy?.startDate ??
        item.insurance_policy?.startDate ??
        item.policy?.startDate,
      endDate:
        policy.endDate ??
        item.insurancePolicy?.endDate ??
        item.insurance_policy?.endDate ??
        item.policy?.endDate,
    };
  }

  private showPopup(message: string, type: 'success' | 'error'): void {
    this.flashMessage = message;
    this.flashType = type;
    clearTimeout(this.popupTimer);
    this.popupTimer = setTimeout(() => {
      this.clearFlash();
    }, 3000);
  }

  private clearFlash(): void {
    this.flashMessage = '';
    this.flashType = '';
  }
}
