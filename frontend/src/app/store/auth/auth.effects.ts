import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import {catchError, map, concatMap, tap} from 'rxjs/operators';
import {Router} from "@angular/router";
import {AuthService} from "../../service/auth.service";
import {AuthActions} from "./auth.actions";
import {of} from "rxjs";
import {MessageService} from "primeng/api";


@Injectable()
export class AuthEffects {

  register$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.register),
      map(action => action.data),
      concatMap(inputData => this.loginService.register(inputData).pipe(
        map(data => AuthActions.registerSuccess({data: data})),
        catchError(error => of(AuthActions.registerFailure({error})))
      ))
    )
  });

  registerSuccess$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.registerSuccess),
      tap((action) => {
        this.messageService.clear();
        this.messageService.add({
          severity: 'success',
          summary: 'Die Registrierung war erfolgreich! Der Bestätigungslink wurde per E-Mail versandt.'
        });
        this.router.navigateByUrl("/login");
      }),
    ), {dispatch: false});

  login$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.login),
      map(action => action.data),
      concatMap(inputData => this.loginService.login(inputData).pipe(
        map(data => AuthActions.loginSuccess({data: data})),
        catchError(error => of(AuthActions.loginFailure({error})))
      ))
    )
  });

  loginSuccess$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.loginSuccess),
      tap((action) => {
        this.loginService.saveLoginStateToLocalStorage(action.data);
        this.router.navigateByUrl("/home");
      }),
    ), {dispatch: false});

  refreshToken$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.refreshToken),
      map(action => action.data),
      concatMap(inputData => this.loginService.refreshToken(inputData).pipe(
        map(data => AuthActions.refreshTokenSuccess({data: data})),
        catchError(error => of(AuthActions.refreshTokenFailure({error})))
      ))
    )
  });

  refreshTokenSuccess$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.refreshTokenSuccess),
      tap((action) => {
        this.loginService.saveLoginStateToLocalStorage(action.data);
      }),
    ), {dispatch: false});

  loginFailure = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.loginFailure),
      tap((action) => {
        this.messageService.clear();
        this.messageService.add({
          severity: 'error',
          summary: 'Der Benutzername oder das Passwort sind falsch! Bitte überprüfen Sie Ihre Eingaben.'
        });
      }),
    ), {dispatch: false});

  logout$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AuthActions.logout),
      map(() => AuthActions.logoutSuccess()),
      tap(data => {
        this.loginService.saveLoginStateToLocalStorage(null);
        this.router.navigateByUrl("/login");
      }),
    )
  });


  constructor(private actions$: Actions, private messageService: MessageService, private router: Router, private loginService: AuthService) {}
}
