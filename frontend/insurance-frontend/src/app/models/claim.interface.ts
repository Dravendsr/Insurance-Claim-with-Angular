export interface Claim {
  claimId: number;
  claimNumber: string;
  claimAmount: number;
  approvedAmount: number;
  claimStatus: 'PENDING' | 'APPROVED' | 'REJECTED' | 'SETTLED';
  approverComment?: string;
  rejectionReason?: string;
  claimDate: Date;
  approvedDate?: Date;
  user: any;
  treatment: any;
  insurancePolicy: any;
  assignedApprover?: any;
}