import { createFeature, createReducer, on } from '@ngrx/store';
import { AuthActions } from './auth.actions';
import {User} from "../../entities/user";

export const authFeatureKey = 'auth';

export interface State {
  loginUser: User | null;
}

export const initialState: State = {
  loginUser: null,
};

export const authReducer = createReducer(
  initialState,
// register
  on(AuthActions.registerSuccess, (state, action) => {
    return {...state, loginUser: action.data}
  }),

  // login
  on(AuthActions.loginSuccess, (state, action) => {
    return {...state, loginUser: action.data}
  }),
  on(AuthActions.loginLocalstorage, (state, action) => {
    return {...state, loginUser: action.data}
  }),
  on(AuthActions.refreshTokenSuccess, (state, action) => {
    return {...state, loginUser: action.data}
  }),

  // logout
  on(AuthActions.logout, (state, action) => {
    return {...state, loginUser: null}
  }),
);

export const authFeature = createFeature({
  name: authFeatureKey,
  reducer: authReducer,
});

