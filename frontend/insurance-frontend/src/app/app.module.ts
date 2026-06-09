import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './components/login/login.component';
import { UserDetailsComponent } from './components/user-details/user-details.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { UsersComponent } from './components/users/users.component';
import { PoliciesComponent } from './components/policies/policies.component';
import { ClaimsComponent } from './components/claims/claims.component';
import { HospitalsComponent } from './components/hospitals/hospitals.component';
import { DoctorsComponent } from './components/doctors/doctors.component';
import { TreatmentsComponent } from './components/treatments/treatments.component';
import { DocumentsComponent } from './components/documents/documents.component';
import { PaymentsComponent } from './components/payments/payments.component';
import { AnalyticsComponent } from './components/analytics/analytics.component';
import { NewClaimComponent } from './components/policyholder/new-claim/new-claim.component';
import { ApproverDashboardComponent } from './components/approver/approver-dashboard/approver-dashboard.component';
import { ApproverPendingComponent } from './components/approver/approver-pending/approver-pending.component';
import { ApproverReviewComponent } from './components/approver/approver-review/approver-review.component';

@NgModule({
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    AppComponent,
    LoginComponent,
    UserDetailsComponent,
    DashboardComponent,
    UsersComponent,
    PoliciesComponent,
    ClaimsComponent,
    HospitalsComponent,
    DoctorsComponent,
    TreatmentsComponent,
    DocumentsComponent,
    PaymentsComponent,
    AnalyticsComponent,
    NewClaimComponent,
    ApproverDashboardComponent,
    ApproverPendingComponent,
    ApproverReviewComponent,
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
