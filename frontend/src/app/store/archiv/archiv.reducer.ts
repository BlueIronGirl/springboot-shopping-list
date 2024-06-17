import { createFeature, createReducer, on } from '@ngrx/store';
import { ArchivActions } from './archiv.actions';
import {ArtikelArchiv} from "../../entities/artikelarchiv";

export const archivFeatureKey = 'archiv';

export interface State {
  artikelsArchiv: ArtikelArchiv[];
}

export const initialState: State = {
  artikelsArchiv: []
};

export const reducer = createReducer(
  initialState,
  // loadArchiv
  on(ArchivActions.loadArchivSuccess, (state, action) => {
    return {...state, artikelsArchiv: action.data}
  }),
);

export const archivFeature = createFeature({
  name: archivFeatureKey,
  reducer,
});

