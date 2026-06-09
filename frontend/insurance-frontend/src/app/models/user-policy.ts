import { InsurancePolicy } from './insurance-policy';

export interface UserPolicy {
  userPolicyId?: number;
  insurancePolicy?: InsurancePolicy;
  purchasedDate?: string;
  expiryDate?: string;
  policyActiveStatus?: string;
  user?: any;
}
