import {createFeature, createReducer, on} from '@ngrx/store';
import {EinkaufszettelActions} from './einkaufszettel.actions';
import {Einkaufszettel} from "../../entities/einkaufszettel";

export const einkaufszettelFeatureKey = 'einkaufszettel';

export interface State {
  einkaufszettel: Einkaufszettel[];
}

export const initialState: State = {
  einkaufszettel: []
};

export const einkaufszettelReducer = createReducer(
  initialState,

  // loadEinkaufszettels
  on(EinkaufszettelActions.loadEinkaufszettelsSuccess, (state, action) => {
    return {...state, einkaufszettel: action.data}
  })
);

export const einkaufszettelFeature = createFeature({
  name: einkaufszettelFeatureKey,
  reducer: einkaufszettelReducer,
});

