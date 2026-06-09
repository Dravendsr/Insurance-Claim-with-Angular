export interface User {
  userId?: number;
  username?: string;
  fullName?: string;
  email?: string;
  phoneNumber?: string;
  dateOfBirth?: string;
  address?: string;
  role?: string;
  accountStatus?: string;
  bankAccountNumber?: string;
  ifscCode?: string;
  bankName?: string;
  createdAt?: string;
  lastLogin?: string;
  profilePhoto?: string; // Base64 encoded profile photo
}
