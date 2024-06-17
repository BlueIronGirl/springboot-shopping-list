import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromArchiv from './archiv.reducer';

export const selectArchivState = createFeatureSelector<fromArchiv.State>(
  fromArchiv.archivFeatureKey
);

export const selectAllArtikelArchiv = createSelector(
  selectArchivState,
  state => state.artikelsArchiv
);
