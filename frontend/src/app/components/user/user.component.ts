import {Component} from '@angular/core';
import {User} from "../../entities/user";
import {ActivatedRoute} from "@angular/router";
import {FormBuilder} from "@angular/forms";
import {Store} from "@ngrx/store";
import {ConfirmationService} from "primeng/api";
import {Role} from "../../entities/role";
import {ROLE_NAME} from "../../entities/enum/rolename";
import {UserActions} from "../../store/user/user.actions";
import {selectAllRoles, selectAllUsers} from "../../store/user/user.selectors";

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent {
  allUsers: User[] = [];
  allUsersCloned: User[] = [];

  allRoles: Role[] = [];

  constructor(private activatedRoute: ActivatedRoute, private formBuilder: FormBuilder, private store: Store, private confirmationService: ConfirmationService) {
  }

  ngOnInit(): void {
    this.store.dispatch(UserActions.loadUsers());
    this.store.dispatch(UserActions.loadRoles());

    this.store.select(selectAllUsers).subscribe(users => {
      this.allUsers = JSON.parse(JSON.stringify(users)); // deep copy
      this.allUsersCloned = JSON.parse(JSON.stringify(users));
    });

    this.store.select(selectAllRoles).subscribe(roles => {
      this.allRoles = roles;
    });
  }

  onRowEditSave(user: User) {
    this.store.dispatch(UserActions.updateUser({data: user}));
  }

  onRowEditCancel(rowIndex: number) {
    this.allUsers[rowIndex] = {...this.allUsersCloned[rowIndex]};
  }

  isAdmin(user: User) {
    return user.roles!.filter(role => role.name === ROLE_NAME.ROLE_ADMIN).length > 0
  }

  deleteUser(user: User) {
    this.confirmationService.confirm({
      message: 'Sind Sie sich sicher, dass Sie den Benutzer löschen möchten?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Ja',
      rejectLabel: 'Nein',
      acceptIcon: "none",
      rejectIcon: "none",
      rejectButtonStyleClass: "p-button-text",
      accept: () => {
        this.store.dispatch(UserActions.deleteUser({data: user}));
        this.store.dispatch(UserActions.loadUsers());
      }
    });
  }
}
