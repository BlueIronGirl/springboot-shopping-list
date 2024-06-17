import { createActionGroup, emptyProps, props } from '@ngrx/store';
import {Role} from "../../entities/role";
import {HttpErrorResponse} from "@angular/common/http";
import {User} from "../../entities/user";

export const UserActions = createActionGroup({
  source: 'User',
  events: {
    'Load Roles': emptyProps(),
    'Load Roles Success': props<{ data: Role[] }>(),
    'Load Roles Failure': props<{ error: HttpErrorResponse }>(),

    'Load Users': emptyProps(),
    'Load Users Success': props<{ data: User[] }>(),
    'Load Users Failure': props<{ error: HttpErrorResponse }>(),

    'Load Users Friends': emptyProps(),
    'Load Users Friends Success': props<{ data: User[] }>(),
    'Load Users Friends Failure': props<{ error: HttpErrorResponse }>(),

    'Update User': props<{ data: User }>(),
    'Update User Success': props<{ data: User }>(),
    'Update User Failure': props<{ error: HttpErrorResponse }>(),

    'Delete User': props<{ data: User }>(),
    'Delete User Success': props<{ data: User }>(),
    'Delete User Failure': props<{ error: HttpErrorResponse }>(),
  }
});
