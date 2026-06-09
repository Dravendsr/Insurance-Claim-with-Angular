import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { UserDetailsComponent } from './components/user-details/user-details.component';
import { UsersComponent } from './components/users/users.component';
import { PoliciesComponent } from './components/policies/policies.component';
import { ClaimsComponent } from './components/claims/claims.component';
import { HospitalsComponent } from './components/hospitals/hospitals.component';
import { DoctorsComponent } from './components/doctors/doctors.component';
import { TreatmentsComponent } from './components/treatments/treatments.component';
import { DocumentsComponent } from './components/documents/documents.component';
import { PaymentsComponent } from './components/payments/payments.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { AnalyticsComponent } from './components/analytics/analytics.component';
import { NewClaimComponent } from './components/policyholder/new-claim/new-claim.component';
import { ApproverDashboardComponent } from './components/approver/approver-dashboard/approver-dashboard.component';
import { ApproverPendingComponent } from './components/approver/approver-pending/approver-pending.component';
import { ApproverReviewComponent } from './components/approver/approver-review/approver-review.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'user-details', component: UserDetailsComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'new-claim', component: NewClaimComponent },
  { path: 'users', component: UsersComponent },
  { path: 'policies', component: PoliciesComponent },
  { path: 'claims', component: ClaimsComponent },
  { path: 'hospitals', component: HospitalsComponent },
  { path: 'doctors', component: DoctorsComponent },
  { path: 'treatments', component: TreatmentsComponent },
  { path: 'documents', component: DocumentsComponent },
  { path: 'payments', component: PaymentsComponent },
  { path: 'approver/dashboard', component: ApproverDashboardComponent },
  { path: 'approver/pending', component: ApproverPendingComponent },
  { path: 'approver/review/:id', component: ApproverReviewComponent },
  { path: 'analytics', component: AnalyticsComponent },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {
    initialNavigation: 'enabledBlocking',
    onSameUrlNavigation: 'reload',
    scrollPositionRestoration: 'enabled',
  })],
  exports: [RouterModule],
})
export class AppRoutingModule {}
