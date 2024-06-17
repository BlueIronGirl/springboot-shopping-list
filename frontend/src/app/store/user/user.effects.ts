import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import {catchError, map, concatMap, switchMap, tap} from 'rxjs/operators';
import { Observable, EMPTY, of } from 'rxjs';
import { UserActions } from './user.actions';
import {MessageService} from "primeng/api";
import {Router} from "@angular/router";
import {AuthService} from "../../service/auth.service";
import {EinkaufszettelService} from "../../service/einkaufszettel.service";
import {UserService} from "../../service/user.service";
import {RoleService} from "../../service/role.service";


@Injectable()
export class UserEffects {

  loadUsersFriends$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserActions.loadUsersFriends),
      switchMap(() => this.userService.getAllUsersFriends().pipe(
          map(users => UserActions.loadUsersFriendsSuccess({data: users})),
          catchError(error => of(UserActions.loadUsersFriendsFailure({error})))
        )
      )
    );
  });

  loadUsers$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserActions.loadUsers),
      switchMap(() => this.userService.getAllUsers().pipe(
          map(users => UserActions.loadUsersSuccess({data: users})),
          catchError(error => of(UserActions.loadUsersFailure({error})))
        )
      )
    );
  });

  loadRoles$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserActions.loadRoles),
      switchMap(() => this.roleService.getAllRoles().pipe(
          map(roles => UserActions.loadRolesSuccess({data: roles})),
          catchError(error => of(UserActions.loadRolesFailure({error})))
        )
      )
    );
  });

  updateUser$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserActions.updateUser),
      map(action => action.data),
      concatMap(inputData => this.userService.updateUser(inputData).pipe(
        map(data => UserActions.updateUserSuccess({data: data})),
        catchError(error => of(UserActions.updateUserFailure({error})))
      ))
    )
  });

  updateUserSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserActions.updateUserSuccess),
      this.navigateWithMessage('User wurde gespeichert', 'user'),
      this.loadAllUsers()
    )
  });

  deleteUser$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserActions.deleteUser),
      map(action => action.data),
      concatMap(inputData => this.userService.deleteUser(inputData).pipe(
        map(data => UserActions.deleteUserSuccess({data: data})),
        catchError(error => of(UserActions.deleteUserFailure({error})))
      ))
    )
  });

  deleteUserSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserActions.deleteUserSuccess),
      this.navigateWithMessage('User wurde gelöscht', 'user'),
      this.loadAllUsers()
    )
  });

  private loadAllUsers() {
    return concatMap(() => this.userService.getAllUsers().pipe(
      map(data => UserActions.loadUsersSuccess({data: data})),
      catchError(error => of(UserActions.loadUsersFailure({error})))
    ));
  }

  private navigateWithMessage(message: string, navigationTarget: string) {
    return tap(() => {
      this.router.navigateByUrl(`/${navigationTarget}`);
      this.messageService.clear();
      this.messageService.add({severity: 'success', summary: message});
    });
  }


  constructor(private actions$: Actions, private messageService: MessageService, private router: Router, private loginService: AuthService, private einkaufszettelService: EinkaufszettelService, private userService: UserService, private roleService: RoleService) {}
}
