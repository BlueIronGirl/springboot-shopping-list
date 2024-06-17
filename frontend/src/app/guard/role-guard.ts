import {Injectable} from '@angular/core';
import {AuthService} from "../service/auth.service";
import {ActivatedRouteSnapshot, Router, UrlTree} from "@angular/router";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class RoleGuard {

  constructor(private router: Router, private authService: AuthService) {
  }

  canActivate(route: ActivatedRouteSnapshot):
    | Observable<boolean | UrlTree>
    | Promise<boolean | UrlTree>
    | boolean
    | UrlTree {
    return this.authService.getAllRolesOfLoggedInUser().filter(role => role === route.data['expectedRole']).length > 0;
  }
}
