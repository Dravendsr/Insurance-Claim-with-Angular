import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs/operators';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { Payment } from '../../models/payment';

@Component({
  selector: 'app-payments',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './payments.component.html',
  styleUrls: ['./payments.component.css'],
})
export class PaymentsComponent implements OnInit {
  payments: Payment[] = [];
  loading = true;
  error = '';
  flashMessage = '';
  flashType: 'success' | 'error' | '' = '';
  activeTab: 'records' | 'send' = 'records';
  showGatewayModal = false;
  gatewayState: 'confirm' | 'success' | 'error' = 'confirm';
  gatewayMessage = '';
  selectedPayment?: Payment;
  showReceiveModal = false;
  receivePayment?: Payment;

  constructor(
    private api: ApiService,
    public auth: AuthService,
    private cdr: ChangeDetectorRef
  ) {
    this.activeTab = this.auth.isAdmin ? 'send' : 'records';
  }

  ngOnInit(): void {
    this.loadPayments();
  }

  loadPayments(): void {
    this.loading = true;
    this.error = '';
    this.payments = [];

    const userId = Number(this.auth.userId);
    const paymentRequest =
      this.auth.role === 'POLICYHOLDER' && userId
        ? this.api.get<any>(`/payments/user/${userId}`)
        : this.api.get<any>('/payments');

    paymentRequest
      .pipe(
        finalize(() => {
          this.loading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (response) => {
          if (!response?.status) {
            this.error =
              response?.message || 'Unable to load payments.';
            return;
          }

          const raw = Array.isArray(response.data)
            ? response.data
            : response.data
            ? [response.data]
            : [];

          this.payments = raw.map((p: any) => ({
            id: p.paymentId,
            claimId: p.claim?.claimId ?? (p.claimId ?? undefined),
            claimNumber: p.claim?.claimNumber ?? p.claimNumber,
            amount: p.paymentAmount ?? p.amount,
            paymentDate: p.paymentDate,
            paymentStatus: p.paymentStatus ?? p.status ?? '',
            paymentMode: p.paymentMode,
            transactionId: p.transactionId,
            companyBankName: p.companyBankName,
            companyAccountNumber: p.companyAccountNumber,
            userId: p.user?.userId ?? p.userId,
          } as Payment));

          if (this.auth.role === 'POLICYHOLDER') {
            const receivedPayment = this.payments.find(
              (payment) => payment.paymentStatus?.toUpperCase() === 'COMPLETED'
            );
            if (receivedPayment) {
              this.receivePayment = receivedPayment;
              this.showReceiveModal = true;
            }
          }

          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error('Error fetching payments:', error);
          this.error =
            'Unable to load payments. Please refresh or try again.';
        },
      });
  }

  get pendingPayments(): Payment[] {
    return this.payments.filter(
      (payment) => payment.paymentStatus?.toUpperCase() === 'PENDING'
    );
  }

  openPaymentGateway(payment: Payment): void {
    this.selectedPayment = payment;
    this.gatewayState = 'confirm';
    this.gatewayMessage = '';
    this.showGatewayModal = true;
  }

  closeGatewayModal(): void {
    this.showGatewayModal = false;
    this.selectedPayment = undefined;
  }

  confirmSend(): void {
    if (!this.selectedPayment?.id) {
      return;
    }

    this.gatewayState = 'confirm';
    this.gatewayMessage = '';

    this.api.put<any>(`/payments/${this.selectedPayment.id}/process`, {}).subscribe({
      next: (response) => {
        if (response?.status) {
          this.gatewayState = 'success';
          this.gatewayMessage = 'Amount sent successfully!';
          this.loadPayments();
        } else {
          this.gatewayState = 'error';
          this.gatewayMessage = response?.message || 'Unable to send payment.';
        }
      },
      error: (error) => {
        console.error('Error sending payment:', error);
        this.gatewayState = 'error';
        this.gatewayMessage = 'Payment could not be sent. Please try again.';
      },
    });
  }

  closeReceiveModal(): void {
    this.showReceiveModal = false;
    this.receivePayment = undefined;
  }

  getDisplayStatus(payment: Payment): string {
    return payment.paymentStatus?.toUpperCase() ?? 'UNKNOWN';
  }

  getBadgeClass(payment: Payment): string {
    const status = payment.paymentStatus?.toUpperCase();
    if (status === 'PENDING') {
      return 'status-pill pending';
    }
    if (status === 'COMPLETED' || status === 'SETTLED') {
      return 'status-pill completed';
    }
    return 'status-pill unknown';
  }

  checkPaymentStatus(payment: any): void {
    const status = payment.paymentStatus?.toUpperCase?.() ?? '';
    if (status === 'COMPLETED' || status === 'SETTLED') {
      this.showPopup('Payment success', 'success');
    } else {
      this.showPopup('Payment failure', 'error');
    }
  }

  private showPopup(message: string, type: 'success' | 'error'): void {
    this.flashMessage = message;
    this.flashType = type;
    setTimeout(() => {
      this.flashMessage = '';
      this.flashType = '';
    }, 5000);
  }
}