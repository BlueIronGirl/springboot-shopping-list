import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromUser from './user.reducer';

export const selectUserState = createFeatureSelector<fromUser.State>(
  fromUser.userFeatureKey
);

export const selectAllUsers = createSelector(
  selectUserState,
  state => state.users
);

export const selectAllUsersFriends = createSelector(
  selectUserState,
  state => state.usersFriends
);

export const selectAllRoles = createSelector(
  selectUserState,
  state => state.roles
);
