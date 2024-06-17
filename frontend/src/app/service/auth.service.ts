import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {Observable, throwError} from "rxjs";
import {User} from "../entities/user";
import {catchError} from "rxjs/operators";
import {Store} from "@ngrx/store";
import jwtDecode, {JwtPayload} from 'jwt-decode';
import {MessageService} from "primeng/api";
import {environment} from "../../environments/environment";
import {ROLE_NAME, RoleName} from "../entities/enum/rolename";
import {AuthActions} from "../store/auth/auth.actions";
import {selectLogin} from "../store/auth/auth.selectors";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private api = `${environment.webserviceurl}`;
  private userRoles: RoleName[] = [];

  constructor(private httpClient: HttpClient, private store: Store, private messageService: MessageService) {
  }

  private errorHandler(error: HttpErrorResponse): Observable<never> {
    console.error('Fehler aufgetreten!' + JSON.stringify(error));
    this.messageService.add({severity: 'error', summary: `Fehler beim Speichern! ${error.message}`});
    return throwError(() => error);
  }

  login(user: User) {
    return this.httpClient.post<User>(`${this.api}/auth/login`, user).pipe(
      catchError(error => this.errorHandler(error))
    );
  }

  register(user: User) {
    return this.httpClient.post<User>(`${this.api}/auth/register`, user).pipe(
      catchError(error => this.errorHandler(error))
    );
  }

  confirmRegistrationToken(token: string) {
    return this.httpClient.post<string>(`${this.api}/auth/confirm?token=${token}`, token).pipe(
      catchError(error => this.errorHandler(error))
    );
  }

  refreshToken(token: string) {
    return this.httpClient.post<any>(`${this.api}/auth/refresh-token`, token).pipe(
      catchError(error => this.errorHandler(error))
    );
  }

  saveLoginStateToLocalStorage(user: User | null) {
    localStorage.setItem('user', JSON.stringify(user));
  }

  getActiveLoginToken(): string {
    let token = '';

    this.store.select(selectLogin).subscribe(user => token = user?.token ? user.token : '');

    return token;
  }

  isLoginStateValid() {
    const userString = localStorage.getItem('user');

    if (userString) {
      const user: User = JSON.parse(userString);

      if (user && user.token && this.isTokenNotExpired(this.getExpire(user.token))) {
        this.store.dispatch(AuthActions.loginLocalstorage({data: user}));
        return true;
      }
    }

    return false;
  }

  getAllRolesOfLoggedInUser(): RoleName[] {
    if (this.userRoles.length > 0) {
      return this.userRoles;
    }

    this.store.select(selectLogin).subscribe(user => {
      const roles = user?.roles;
      if (roles) {
        this.userRoles = [];
        for (let role of roles) {
          Object.values(ROLE_NAME).forEach((value) => {
            const roleValue: RoleName = value;
            if (role.name === value) {
              this.userRoles.push(roleValue);
            }
          });
        }
      }

    });

    return this.userRoles;
  }

  private getExpire(token: string): Date {
    const params: JwtPayload = jwtDecode(token);
    if (params.exp) {
      return new Date(params.exp * 1000);
    } else {
      return new Date();
    }
  }

  private isTokenNotExpired(expire: Date | string | undefined): boolean {
    const date: Date | undefined = this.getDateOrStringAsDate(expire);
    return date !== undefined && date.getTime() > new Date().getTime();
  }

  private getDateOrStringAsDate(dateOrString: string | Date | undefined): Date | undefined {
    if (typeof dateOrString === 'string') {
      return new Date(dateOrString);
    } else if (dateOrString) {
      return dateOrString;
    } else {
      return undefined;
    }
  }
}
