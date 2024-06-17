import { createActionGroup, emptyProps, props } from '@ngrx/store';
import {User} from "../../entities/user";
import {HttpErrorResponse} from "@angular/common/http";

export const AuthActions = createActionGroup({
  source: 'Auth',
  events: {
    'Register': props<{ data: User }>(),
    'Register Success': props<{ data: User }>(),
    'Register Failure': props<{ error: HttpErrorResponse }>(),

    'Login': props<{ data: User }>(),
    'Login Localstorage': props<{ data: User }>(),
    'Login Success': props<{ data: User }>(),
    'Login Failure': props<{ error: HttpErrorResponse }>(),

    'Refresh Token': props<{ data: string }>(),
    'Refresh Token Success': props<{ data: User }>(),
    'Refresh Token Failure': props<{ error: HttpErrorResponse }>(),

    'Logout': emptyProps(),
    'Logout Success': emptyProps(),
  }
});
