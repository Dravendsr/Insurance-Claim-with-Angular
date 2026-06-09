import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { ApiService } from '../../services/api.service';
import { User } from '../../models/user';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css'],
})
export class UsersComponent implements OnInit {
  users: User[] = [];
  loading = true;
  error = '';
  flashMessage = '';
  flashType: 'success' | 'error' | '' = '';
  popupTimer: any;
  showAddForm = false;
  showEditForm = false;
  editingUser: User | null = null;

  newUser: any = {
    username: '',
    password: '',
    fullName: '',
    email: '',
    phoneNumber: '',
    role: 'POLICYHOLDER',
    accountStatus: 'ACTIVE'
  };

  constructor(
    private api: ApiService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.loading = true;
    this.error = '';
    this.users = [];

    this.api
      .get<{ data: User[]; status: boolean; message: string }>('/users')
      .pipe(
        finalize(() => {
          this.loading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (response) => {
          console.log('Users response:', response);

          if (!response?.status) {
            this.error = response?.message || 'Unable to load users.';
            return;
          }

          const payload = response.data || [];
          this.users = Array.isArray(payload) ? payload : [payload];

          console.log('Parsed users:', this.users);

          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error('Error fetching users:', error);
          this.error =
            'Unable to load users. Please refresh or try again.';
        },
      });
  }

  toggleAddForm(): void {
    this.showAddForm = !this.showAddForm;

    if (this.showAddForm) {
      this.showEditForm = false;
    }
  }

  addUser(): void {

  const username = this.newUser.username?.trim();
  const password = this.newUser.password?.trim();
  const fullName = this.newUser.fullName?.trim();

  if (!username || !password || !fullName) {
    this.showPopup(
      'Please fill in required fields (Username, Password, Name).',
      'error'
    );
    return;
  }

  const payload = {
    ...this.newUser,
    username,
    password,
    fullName
  };

  console.log('Submitting User:', payload);

  this.api.post<any>('/users', payload).subscribe({
    next: (response) => {

      console.log('Create User Response:', response);

      const success =
        response?.status === true ||
        response?.data !== undefined;

      if (success) {

        const createdUser = response.data ?? response;

        if (createdUser) {
          this.users.push(createdUser);
        } else {
          this.loadUsers();
        }

        this.showAddForm = false;

        this.newUser = {
          username: '',
          password: '',
          fullName: '',
          email: '',
          phoneNumber: '',
          role: 'POLICYHOLDER',
          accountStatus: 'ACTIVE'
        };

        this.cdr.detectChanges();

        this.showPopup(
          'User added successfully.',
          'success'
        );

      } else {
        this.showPopup(
          response?.message || 'Failed to add user.',
          'error'
        );
      }
    },
    error: (error) => {
  console.error('Error adding user:', error);

  let message = 'Unable to add user at this time.';

  if (error?.error?.message?.includes('cannot be null')) {
    message =
      'User could not be added because some required user details are missing. The User Is not Added in Insurance Buying policy Webpage';
  }

  this.showPopup(message, 'error');
}
  });
}

  editUser(user: User): void {
    this.editingUser = { ...user };
    this.showEditForm = true;
    this.showAddForm = false;
  }

  updateUser(): void {
    const userToSave = this.editingUser;

    if (!userToSave || !userToSave.userId) {
      return;
    }

    this.api.put<any>(`/users/${userToSave.userId}`, userToSave).subscribe({
      next: (response) => {
        if (response.status) {
          const index = this.users.findIndex(
            (u) => u.userId === userToSave.userId
          );

          if (index !== -1 && response.data) {
            this.users[index] = response.data;

            this.cdr.detectChanges();
          }

          this.showEditForm = false;
          this.editingUser = null;

          this.showPopup('User updated successfully.', 'success');
          console.log('User updated successfully');
        } else {
          this.showPopup(response.message || 'Failed to update user.', 'error');
        }
      },
      error: (error) => {
        console.error('Error updating user:', error);
        this.showPopup('An error occurred while trying to update the user.', 'error');
      },
    });
  }

  removeUser(userId: number | undefined): void {
    if (!userId) {
      return;
    }

    if (
      confirm(
        'Are you sure you want to remove this user? This action cannot be undone.'
      )
    ) {
      this.api.delete<any>(`/users/${userId}`).subscribe({
        next: (response) => {
          if (response.status) {
            this.users = this.users.filter(
              (u) => u.userId !== userId
            );

            this.cdr.detectChanges();

            this.showPopup('User removed successfully.', 'success');
            console.log('User removed successfully');
          } else {
            this.showPopup(response.message || 'Failed to remove user.', 'error');
          }
        },
        error: (error) => {
          console.error('Error removing user:', error);
          this.showPopup('An error occurred while trying to remove the user.', 'error');
        }
      });
    }
  }

  private showPopup(
  message: string,
  type: 'success' | 'error'
): void {

  clearTimeout(this.popupTimer);

  this.flashMessage = message;
  this.flashType = type;

  this.popupTimer = setTimeout(() => {
    this.clearFlash();
  }, 3000);

  this.cdr.detectChanges();
}

  private clearFlash(): void {
    this.flashMessage = '';
    this.flashType = '';
  }
}