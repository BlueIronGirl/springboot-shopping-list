import { createFeature, createReducer, on } from '@ngrx/store';
import { UserActions } from './user.actions';
import {User} from "../../entities/user";
import {Role} from "../../entities/role";

export const userFeatureKey = 'user';

export interface State {
  usersFriends: User[];
  users: User[];
  roles: Role[];
}

export const initialState: State = {
  usersFriends: [],
  users: [],
  roles: []
};

export const reducer = createReducer(
  initialState,
  // loadUsersFriends
  on(UserActions.loadUsersFriendsSuccess, (state, action) => {
    return {...state, usersFriends: action.data}
  }),

  // loadUsers
  on(UserActions.loadUsersSuccess, (state, action) => {
    return {...state, users: action.data}
  }),

  // loadRoles
  on(UserActions.loadRolesSuccess, (state, action) => {
    return {...state, roles: action.data}
  }),
);

export const userFeature = createFeature({
  name: userFeatureKey,
  reducer,
});

