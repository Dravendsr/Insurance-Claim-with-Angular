export interface Payment {
  id?: number;
  claimId?: number;
  claimNumber?: string;
  amount?: number;
  paymentDate?: string;
  paymentStatus?: string;
  paymentMode?: string;
  transactionId?: string;
  companyBankName?: string;
  companyAccountNumber?: string;
  userId?: number;
  transferStatus?: string;
}
