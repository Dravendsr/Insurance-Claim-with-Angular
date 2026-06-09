export interface Claim {
  id?: number;
  claimId?: number;
  claimNumber?: string;
  policyId?: number;
  status?: string;
  amount?: number;
  claimAmount?: number;
  approvedAmount?: number;
  claimStatus?: 'SUBMITTED' | 'PENDING' | 'APPROVED' | 'REJECTED' | 'SETTLED';
  approverComment?: string;
  rejectionReason?: string;
  claimDate?: Date;
  approvedDate?: Date;
  user?: any;
  treatment?: any;
  insurancePolicy?: any;
  assignedApprover?: any;
  documents?: any[];
  recommendationStatus?: string;
  recommendationScore?: number;
  recommendationReason?: string;
}
