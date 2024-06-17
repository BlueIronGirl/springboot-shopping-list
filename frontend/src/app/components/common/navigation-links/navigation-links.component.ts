import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {ROLE_NAME, RoleName} from "../../../entities/enum/rolename";
import {AuthActions} from "../../../store/auth/auth.actions";
import {Store} from "@ngrx/store";
import {ConfirmationService} from "primeng/api";
import {AuthService} from "../../../service/auth.service";
import {selectLogin} from "../../../store/auth/auth.selectors";
import {User} from "../../../entities/user";

@Component({
  selector: 'app-navigation-links',
  templateUrl: './navigation-links.component.html',
  styleUrls: ['./navigation-links.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class NavigationLinksComponent implements OnInit {

  protected readonly ROLE_NAME = ROLE_NAME;
  userRoles: RoleName[] = [];
  userIsLoggedIn = false;
  userLoggedIn: User | undefined;

  constructor(private store: Store, private authService: AuthService) {

  }

  ngOnInit(): void {
    this.store.select(selectLogin).subscribe(user => {
      this.userLoggedIn = user != null ? user : undefined;
      this.userIsLoggedIn = user != null;
      this.userRoles = [...this.authService.getAllRolesOfLoggedInUser()];
    });
  }

  hasRole(roleName: RoleName) {
    return this.userRoles.filter(role => role === roleName).length > 0
  }
}
