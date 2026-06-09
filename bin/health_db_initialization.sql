-- Health Insurance Claim Management System - Database Initialization Script
-- Database: health_db

-- Create Database
CREATE DATABASE IF NOT EXISTS health_db;
USE health_db;

-- ============================================
-- TABLE: users
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    date_of_birth DATE NOT NULL,
    address VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    account_status VARCHAR(20) NOT NULL,
    bank_account_number VARCHAR(20),
    ifsc_code VARCHAR(20),
    bank_name VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_login DATETIME,
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- TABLE: insurance_policies
-- ============================================
CREATE TABLE IF NOT EXISTS insurance_policies (
    policy_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    policy_number VARCHAR(50) UNIQUE NOT NULL,
    policy_name VARCHAR(100) NOT NULL,
    policy_type VARCHAR(50) NOT NULL,
    coverage_amount DOUBLE NOT NULL,
    premium_amount DOUBLE NOT NULL,
    benefits TEXT,
    policy_status VARCHAR(20) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_policy_number (policy_number),
    INDEX idx_policy_status (policy_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- TABLE: user_policies
-- ============================================
CREATE TABLE IF NOT EXISTS user_policies (
    user_policy_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    policy_id BIGINT NOT NULL,
    purchased_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    policy_active_status VARCHAR(20) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (policy_id) REFERENCES insurance_policies(policy_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_policy_id (policy_id),
    UNIQUE KEY unique_user_policy (user_id, policy_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- TABLE: hospitals
-- ============================================
CREATE TABLE IF NOT EXISTS hospitals (
    hospital_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hospital_name VARCHAR(100) NOT NULL,
    hospital_type VARCHAR(50) NOT NULL,
    address VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    INDEX idx_hospital_name (hospital_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- TABLE: doctors
-- ============================================
CREATE TABLE IF NOT EXISTS doctors (
    doctor_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    doctor_name VARCHAR(100) NOT NULL,
    specialization VARCHAR(50) NOT NULL,
    qualification VARCHAR(50) NOT NULL,
    experience_years INT NOT NULL,
    hospital_id BIGINT NOT NULL,
    FOREIGN KEY (hospital_id) REFERENCES hospitals(hospital_id) ON DELETE CASCADE,
    INDEX idx_hospital_id (hospital_id),
    INDEX idx_specialization (specialization)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- TABLE: treatments
-- ============================================
CREATE TABLE IF NOT EXISTS treatments (
    treatment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    diagnosis VARCHAR(100) NOT NULL,
    treatment_description TEXT NOT NULL,
    treatment_amount DOUBLE NOT NULL,
    treatment_date DATE NOT NULL,
    user_id BIGINT NOT NULL,
    doctor_id BIGINT NULL,
    hospital_id BIGINT NULL,
    doctor_name VARCHAR(100),
    doctor_specialization VARCHAR(50),
    doctor_qualification VARCHAR(100),
    doctor_experience_years INT,
    hospital_name VARCHAR(100),
    hospital_address VARCHAR(255),
    hospital_phone VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE,
    FOREIGN KEY (hospital_id) REFERENCES hospitals(hospital_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_doctor_id (doctor_id),
    INDEX idx_hospital_id (hospital_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- TABLE: claims
-- ============================================
CREATE TABLE IF NOT EXISTS claims (
    claim_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    claim_number VARCHAR(50) UNIQUE NOT NULL,
    claim_amount DOUBLE NOT NULL,
    approved_amount DOUBLE DEFAULT 0,
    claim_status VARCHAR(20) NOT NULL,
    approver_comment TEXT,
    rejection_reason TEXT,
    claim_date DATE NOT NULL,
    approved_date DATE,
    user_id BIGINT NOT NULL,
    treatment_id BIGINT NOT NULL,
    policy_id BIGINT NOT NULL,
    assigned_approver_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (treatment_id) REFERENCES treatments(treatment_id) ON DELETE CASCADE,
    FOREIGN KEY (policy_id) REFERENCES insurance_policies(policy_id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_approver_id) REFERENCES users(user_id),
    INDEX idx_claim_number (claim_number),
    INDEX idx_user_id (user_id),
    INDEX idx_claim_status (claim_status),
    INDEX idx_approver_id (assigned_approver_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- TABLE: documents
-- ============================================
CREATE TABLE IF NOT EXISTS documents (
    document_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    document_name VARCHAR(100) NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    document_path VARCHAR(255) NOT NULL,
    uploaded_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    claim_id BIGINT NOT NULL,
    FOREIGN KEY (claim_id) REFERENCES claims(claim_id) ON DELETE CASCADE,
    INDEX idx_claim_id (claim_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- TABLE: payments
-- ============================================
CREATE TABLE IF NOT EXISTS payments (
    payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_amount DOUBLE NOT NULL,
    payment_date DATE NOT NULL,
    payment_mode VARCHAR(50) NOT NULL,
    transaction_id VARCHAR(50),
    payment_status VARCHAR(20) NOT NULL,
    company_account_number VARCHAR(20) NOT NULL,
    company_bank_name VARCHAR(100) NOT NULL,
    claim_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (claim_id) REFERENCES claims(claim_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_claim_id (claim_id),
    INDEX idx_user_id (user_id),
    UNIQUE KEY unique_claim_payment (claim_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ============================================
-- INSERT SAMPLE DATA - USERS
-- ============================================

-- ADMIN USER (1)
INSERT INTO users (username, password, full_name, email, phone_number, date_of_birth, address, role, account_status, bank_account_number, ifsc_code, bank_name, created_at) VALUES
('admin001', '$2a$10$slYQmyNdGzin7ZLBge2e2OPST9/PgBkqquzi.Ss7KIUgO2t0jKMUm', 'Admin User', 'admin@hicms.com', '9876543210', '1980-08-15', '123 Admin Office Street, New York', 'ADMIN', 'ACTIVE', NULL, NULL, NULL, NOW());

-- APPROVER USERS (2)
INSERT INTO users (username, password, full_name, email, phone_number, date_of_birth, address, role, account_status, bank_account_number, ifsc_code, bank_name, created_at) VALUES
('approver001', '$2a$10$slYQmyNdGzin7ZLBge2e2OPST9/PgBkqquzi.Ss7KIUgO2t0jKMUm', 'John Approver', 'approver1@hicms.com', '9876543211', '1975-05-20', '456 Approver Lane, Boston', 'APPROVER', 'ACTIVE', NULL, NULL, NULL, NOW()),
('approver002', '$2a$10$slYQmyNdGzin7ZLBge2e2OPST9/PgBkqquzi.Ss7KIUgO2t0jKMUm', 'Sarah Approver', 'approver2@hicms.com', '9876543212', '1978-03-10', '789 Reviewer Road, Chicago', 'APPROVER', 'ACTIVE', NULL, NULL, NULL, NOW());

-- POLICYHOLDER USERS (12)
INSERT INTO users (username, password, full_name, email, phone_number, date_of_birth, address, role, account_status, bank_account_number, ifsc_code, bank_name, created_at) VALUES
('rajesh17', '$2a$12$opoL9PSyGeyAHsm3yYZ28udtptuW3wXdF0ewgLFARTuFbuhCwBocC', 'Rajesh Kumar', 'rajesh@email.com', '9999888777', '1990-01-15', '100 Main Street, Mumbai', 'POLICYHOLDER', 'ACTIVE', '1234567890123456', 'ICIC0000001', 'ICICI Bank', NOW()),
('priya92', '$2a$12$05qSDPjY7feEIVFPB4Alx.c7WMgid/ECWEvR2pBRBo3QlVssGpz2i', 'Priya Singh', 'priya@email.com', '8888777666', '1992-02-20', '200 Park Avenue, Delhi', 'POLICYHOLDER', 'ACTIVE', '2345678901234567', 'HDFC0000002', 'HDFC Bank', NOW()),
('amit88', '$2a$12$05qSDPjY7feEIVFPB4Alx.c7WMgid/ECWEvR2pBRBo3QlVssGpz2i', 'Amit Patel', 'amit@email.com', '7777666555', '1988-03-25', '300 Market Street, Bangalore', 'POLICYHOLDER', 'ACTIVE', '3456789012345678', 'AXAX0000003', 'Axis Bank', NOW()),
('neha91', '$2a$12$05qSDPjY7feEIVFPB4Alx.c7WMgid/ECWEvR2pBRBo3QlVssGpz2i', 'Neha Gupta', 'neha@email.com', '6666555444', '1991-04-30', '400 Garden Road, Pune', 'POLICYHOLDER', 'ACTIVE', '4567890123456789', 'SBIN0000004', 'State Bank', NOW()),
('vikrant85', '$2a$12$05qSDPjY7feEIVFPB4Alx.c7WMgid/ECWEvR2pBRBo3QlVssGpz2i', 'Vikrant Desai', 'vikrant@email.com', '5555444333', '1985-05-12', '500 River Street, Hyderabad', 'POLICYHOLDER', 'ACTIVE', '5678901234567890', 'NDSE0000005', 'NDSE Bank', NOW()),
('divya93', '$2a$12$05qSDPjY7feEIVFPB4Alx.c7WMgid/ECWEvR2pBRBo3QlVssGpz2i', 'Divya Sharma', 'divya@email.com', '4444333222', '1993-06-18', '600 Valley Road, Chennai', 'POLICYHOLDER', 'ACTIVE', '6789012345678901', 'IDIB0000006', 'IDBI Bank', NOW()),
('karthik87', '$2a$12$05qSDPjY7feEIVFPB4Alx.c7WMgid/ECWEvR2pBRBo3QlVssGpz2i', 'Karthik Menon', 'karthik@email.com', '3333222111', '1987-07-22', '700 Mountain Street, Kochi', 'POLICYHOLDER', 'ACTIVE', '7890123456789012', 'BOBI0000007', 'BoB Bank', NOW()),
('anjali89', '$2a$12$05qSDPjY7feEIVFPB4Alx.c7WMgid/ECWEvR2pBRBo3QlVssGpz2i', 'Anjali Nair', 'anjali@email.com', '2222111000', '1989-08-28', '800 Forest Lane, Jaipur', 'POLICYHOLDER', 'ACTIVE', '8901234567890123', 'KKBK0000008', 'Kotak Bank', NOW()),
('harsh91', '$2a$12$05qSDPjY7feEIVFPB4Alx.c7WMgid/ECWEvR2pBRBo3QlVssGpz2i', 'Harsh Verma', 'harsh@email.com', '1111000999', '1991-09-05', '900 Desert Road, Surat', 'POLICYHOLDER', 'INACTIVE', '9012345678901234', 'AUBL0000009', 'Aurora Bank', NOW()),
('manisha86', '$2a$12$05qSDPjY7feEIVFPB4Alx.c7WMgid/ECWEvR2pBRBo3QlVssGpz2i', 'Manisha Iyer', 'manisha@email.com', '0000999888', '1986-10-11', '1000 Ocean Street, Ahmedabad', 'POLICYHOLDER', 'ACTIVE', '0123456789012345', 'FDRL0000010', 'Federal Bank', NOW()),
('sanjay90', '$2a$12$05qSDPjY7feEIVFPB4Alx.c7WMgid/ECWEvR2pBRBo3QlVssGpz2i', 'Sanjay Reddy', 'sanjay@email.com', '9999888777', '1990-11-16', '1100 Sky Lane, Kolkata', 'POLICYHOLDER', 'ACTIVE', '1122334455667788', 'YESB0000011', 'Yes Bank', NOW()),
('ritika94', '$2a$12$05qSDPjY7feEIVFPB4Alx.c7WMgid/ECWEvR2pBRBo3QlVssGpz2i', 'Ritika Chopra', 'ritika@email.com', '8888777666', '1994-12-22', '1200 Cloud Street, Lucknow', 'POLICYHOLDER', 'ACTIVE', '2233445566778899', 'SCBL0000012', 'Standard Chartered', NOW());

-- ============================================
-- INSERT SAMPLE DATA - INSURANCE POLICIES
-- ============================================
INSERT INTO insurance_policies (policy_number, policy_name, policy_type, coverage_amount, premium_amount, benefits, policy_status, start_date, end_date, created_at) VALUES
('POL001', 'Gold Health Plus', 'Individual', 500000, 15000, 'Covers hospitalization, surgeries, medications', 'ACTIVE', '2024-01-01', '2025-12-31', NOW()),
('POL002', 'Silver Health Basic', 'Individual', 300000, 10000, 'Basic coverage for hospitalization', 'ACTIVE', '2024-02-15', '2025-12-31', NOW()),
('POL003', 'Platinum Health Premium', 'Family', 1000000, 30000, 'Full coverage for family hospitalization and surgery', 'ACTIVE', '2023-12-01', '2024-11-30', NOW()),
('POL004', 'Bronze Health Entry', 'Individual', 200000, 5000, 'Emergency hospitalization coverage', 'ACTIVE', '2024-01-10', '2025-12-31', NOW()),
('POL005', 'Diamond Family Care', 'Family', 1500000, 40000, 'Premium family health coverage with dental', 'INACTIVE', '2023-06-01', '2024-05-31', NOW());

-- ============================================
-- INSERT SAMPLE DATA - USER POLICIES
-- ============================================
INSERT INTO user_policies (user_id, policy_id, purchased_date, expiry_date, policy_active_status) VALUES
(2, 1, '2024-01-01', '2025-12-31', 'ACTIVE'),
(3, 2, '2024-02-15', '2026-02-14', 'ACTIVE'),
(4, 1, '2024-03-01', '2025-12-31', 'ACTIVE'),
(5, 3, '2023-12-01', '2024-11-30', 'EXPIRED'),
(6, 2, '2024-04-10', '2026-04-09', 'ACTIVE'),
(7, 4, '2024-05-20', '2025-12-31', 'ACTIVE'),
(8, 1, '2024-06-01', '2026-05-31', 'ACTIVE'),
(9, 2, '2024-07-15', '2026-07-14', 'ACTIVE'),
(10, 3, '2024-08-01', '2026-07-31', 'ACTIVE'),
(11, 4, '2024-09-10', '2025-12-31', 'ACTIVE'),
(12, 1, '2024-10-05', '2026-10-04', 'ACTIVE'),
(13, 2, '2024-11-20', '2026-11-19', 'ACTIVE');

-- ============================================
-- INSERT SAMPLE DATA - HOSPITALS
-- ============================================
INSERT INTO hospitals (hospital_name, hospital_type, address, phone_number) VALUES
('Apollo Hospitals', 'Private', '100 Health Street, Mumbai', '9111111111'),
('Fortis Healthcare', 'Private', '200 Medical Plaza, Delhi', '9122222222'),
('Max Healthcare', 'Private', '300 Care Avenue, Bangalore', '9133333333'),
('Government Medical College', 'Government', '400 Public Hospital Road, Pune', '9144444444'),
('Medanta - The Medicity', 'Private', '500 Clinical Lane, Gurgaon', '9155555555');

-- ============================================
-- INSERT SAMPLE DATA - DOCTORS
-- ============================================
INSERT INTO doctors (doctor_name, specialization, qualification, experience_years, hospital_id) VALUES
('Dr. Rajesh Kumar', 'Cardiology', 'MD', 15, 1),
('Dr. Priya Sharma', 'Orthopedics', 'MBBS', 10, 1),
('Dr. Amit Singh', 'Neurology', 'MD', 12, 2),
('Dr. Neha Gupta', 'Gastroenterology', 'MD', 8, 2),
('Dr. Vikram Patel', 'Oncology', 'MD', 18, 3),
('Dr. Anjali Verma', 'Pediatrics', 'MBBS', 7, 3),
('Dr. Sanjay Kumar', 'Orthopedics', 'MBBS', 14, 4),
('Dr. Divya Naidu', 'General Medicine', 'MBBS', 9, 5),
('Dr. Karthik Menon', 'Surgery', 'MS', 16, 5),
('Dr. Seema Singh', 'Cardiology', 'MD', 20, 1);

-- ============================================
-- INSERT SAMPLE DATA - TREATMENTS
-- ============================================
INSERT INTO treatments (diagnosis, treatment_description, treatment_amount, treatment_date, user_id, doctor_id, hospital_id) VALUES
('Hypertension', 'Regular cardiac checkup and medication', 5000, '2024-10-01', 2, 1, 1),
('Arthritis', 'Joint replacement surgery', 150000, '2024-09-15', 3, 2, 1),
('Migraine', 'Neurological consultation and treatment', 8000, '2024-10-05', 4, 3, 2),
('Gastric Issues', 'Endoscopy and medication', 25000, '2024-10-10', 5, 4, 2),
('Tumor Detection', 'Cancer treatment and chemotherapy', 300000, '2024-09-20', 6, 5, 3),
('Fever', 'General checkup and pediatric treatment', 3000, '2024-10-03', 7, 6, 3),
('Fracture', 'Orthopedic surgery and physiotherapy', 120000, '2024-10-08', 8, 7, 4),
('General Ailment', 'General consultation and treatment', 5000, '2024-10-12', 9, 8, 5),
('Appendix', 'Appendectomy surgery', 80000, '2024-10-02', 10, 9, 5),
('Heart Disease', 'Bypass surgery', 400000, '2024-09-25', 11, 10, 1);

-- ============================================
-- INSERT SAMPLE DATA - CLAIMS
-- ============================================
INSERT INTO claims (claim_number, claim_amount, approved_amount, claim_status, approver_comment, rejection_reason, claim_date, approved_date, user_id, treatment_id, policy_id, assigned_approver_id) VALUES
('CLM001', 5000, 5000, 'APPROVED', 'Covered under policy', NULL, '2024-10-01', '2024-10-02', 2, 1, 1, 2),
('CLM002', 150000, 150000, 'APPROVED', 'Full coverage approved', NULL, '2024-09-15', '2024-09-18', 3, 2, 1, 2),
('CLM003', 8000, 8000, 'APPROVED', NULL, NULL, '2024-10-05', '2024-10-06', 4, 3, 2, 3),
('CLM004', 25000, 20000, 'APPROVED', 'Deductible applied - eligible amount reduced', NULL, '2024-10-10', '2024-10-11', 5, 4, 2, 3),
('CLM005', 300000, 0, 'REJECTED', NULL, 'Treatment not covered under current policy plan', '2024-09-20', '2024-09-22', 6, 5, 3, 2),
('CLM006', 3000, 3000, 'PENDING', NULL, NULL, '2024-10-03', NULL, 7, 6, 4, NULL),
('CLM007', 120000, 120000, 'APPROVED', 'Approved with document verification', NULL, '2024-10-08', '2024-10-09', 8, 7, 1, 2),
('CLM008', 5000, 2500, 'APPROVED', 'Partial coverage due to policy limit', NULL, '2024-10-12', '2024-10-13', 9, 8, 4, 3),
('CLM009', 80000, 80000, 'APPROVED', 'Emergency surgery covered', NULL, '2024-10-02', '2024-10-04', 10, 9, 1, 2),
('CLM010', 400000, 400000, 'APPROVED', NULL, NULL, '2024-09-25', '2024-09-27', 11, 10, 1, 3);

-- ============================================
-- INSERT SAMPLE DATA - DOCUMENTS
-- ============================================
INSERT INTO documents (document_name, document_type, document_path, uploaded_date, claim_id) VALUES
('Medical_Report_CLM001.pdf', 'Medical Report', '/documents/medical_reports/CLM001.pdf', NOW(), 1),
('Surgery_Invoice_CLM002.pdf', 'Invoice', '/documents/invoices/CLM002.pdf', NOW(), 2),
('Prescription_CLM003.pdf', 'Prescription', '/documents/prescriptions/CLM003.pdf', NOW(), 3),
('Test_Report_CLM004.pdf', 'Lab Report', '/documents/lab_reports/CLM004.pdf', NOW(), 4),
('Medical_Certificate_CLM005.pdf', 'Medical Certificate', '/documents/certificates/CLM005.pdf', NOW(), 5),
('Discharge_Summary_CLM006.pdf', 'Discharge Summary', '/documents/discharge/CLM006.pdf', NOW(), 6),
('Surgery_Report_CLM007.pdf', 'Surgery Report', '/documents/surgery_reports/CLM007.pdf', NOW(), 7),
('Medical_Report_CLM008.pdf', 'Medical Report', '/documents/medical_reports/CLM008.pdf', NOW(), 8),
('Emergency_Report_CLM009.pdf', 'Emergency Report', '/documents/emergency/CLM009.pdf', NOW(), 9),
('Surgery_Invoice_CLM010.pdf', 'Invoice', '/documents/invoices/CLM010.pdf', NOW(), 10);

-- ============================================
-- INSERT SAMPLE DATA - PAYMENTS
-- ============================================
-- Note: Payments only created for approved claims
INSERT INTO payments (payment_amount, payment_date, payment_mode, transaction_id, payment_status, company_account_number, company_bank_name, claim_id, user_id) VALUES
(5000, '2024-10-02', 'Bank Transfer', 'TXN001', 'COMPLETED', '1111222233334444', 'ICICI Bank', 1, 2),
(150000, '2024-09-18', 'Bank Transfer', 'TXN002', 'COMPLETED', '1111222233334444', 'ICICI Bank', 2, 3),
(8000, '2024-10-06', 'Cheque', 'CHQ001', 'COMPLETED', '1111222233334444', 'ICICI Bank', 3, 4),
(20000, '2024-10-11', 'Bank Transfer', 'TXN004', 'COMPLETED', '1111222233334444', 'ICICI Bank', 4, 5),
(120000, '2024-10-09', 'Bank Transfer', 'TXN007', 'PENDING', '1111222233334444', 'ICICI Bank', 7, 8),
(2500, '2024-10-13', 'Bank Transfer', 'TXN008', 'COMPLETED', '1111222233334444', 'ICICI Bank', 8, 9),
(80000, '2024-10-04', 'Bank Transfer', 'TXN009', 'COMPLETED', '1111222233334444', 'ICICI Bank', 9, 10),
(400000, '2024-09-27', 'Bank Transfer', 'TXN010', 'COMPLETED', '1111222233334444', 'ICICI Bank', 10, 11);

-- ============================================
-- VERIFY DATA
-- ============================================
SELECT 'Users:' as 'Total Count';
SELECT COUNT(*) FROM users;

SELECT 'Insurance Policies:' as 'Total Count';
SELECT COUNT(*) FROM insurance_policies;

SELECT 'User Policies:' as 'Total Count';
SELECT COUNT(*) FROM user_policies;

SELECT 'Hospitals:' as 'Total Count';
SELECT COUNT(*) FROM hospitals;

SELECT 'Doctors:' as 'Total Count';
SELECT COUNT(*) FROM doctors;

SELECT 'Treatments:' as 'Total Count';
SELECT COUNT(*) FROM treatments;

SELECT 'Claims:' as 'Total Count';
SELECT COUNT(*) FROM claims;

SELECT 'Documents:' as 'Total Count';
SELECT COUNT(*) FROM documents;

SELECT 'Payments:' as 'Total Count';
SELECT COUNT(*) FROM payments;
