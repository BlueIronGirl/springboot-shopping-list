import {createActionGroup, emptyProps, props} from '@ngrx/store';
import {HttpErrorResponse} from "@angular/common/http";
import {Artikel} from "../../entities/artikel";
import {Einkaufszettel} from "../../entities/einkaufszettel";

export const EinkaufszettelActions = createActionGroup({
  source: 'Einkaufszettel',
  events: {
    'Load Einkaufszettels': emptyProps(),
    'Load Einkaufszettels Success': props<{ data: Einkaufszettel[] }>(),
    'Load Einkaufszettels Failure': props<{ error: HttpErrorResponse }>(),

    'Create Einkaufszettel': props<{ data: Einkaufszettel }>(),
    'Create Einkaufszettel Success': props<{ data: Einkaufszettel }>(),
    'Create Einkaufszettel Failure': props<{ error: HttpErrorResponse }>(),

    'Update Einkaufszettel': props<{ data: Einkaufszettel }>(),
    'Update Einkaufszettel Success': props<{ data: Einkaufszettel }>(),
    'Update Einkaufszettel Failure': props<{ error: HttpErrorResponse }>(),

    'Delete Einkaufszettel': props<{ data: Einkaufszettel }>(),
    'Delete Einkaufszettel Success': props<{ data: Einkaufszettel }>(),
    'Delete Einkaufszettel Failure': props<{ error: HttpErrorResponse }>(),


    'Load Artikels': emptyProps(),
    'Load Artikels Success': props<{ data: Artikel[] }>(),
    'Load Artikels Failure': props<{ error: HttpErrorResponse }>(),

    'Create Artikel': props<{ einkaufszettelId: number, data: Artikel }>(),
    'Create Artikel Success': props<{ data: Artikel }>(),
    'Create Artikel Failure': props<{ error: HttpErrorResponse }>(),

    'Update Artikel': props<{ einkaufszettelId: number, data: Artikel }>(),
    'Update Artikel Success': props<{ data: Artikel }>(),
    'Update Artikel Failure': props<{ error: HttpErrorResponse }>(),

    'Delete Artikel': props<{ einkaufszettelId: number, data: Artikel }>(),
    'Delete Artikel Success': props<{ data: Artikel }>(),
    'Delete Artikel Failure': props<{ error: HttpErrorResponse }>(),

    'Archiviere Artikel': props<{ einkaufszettelId: number }>(),
    'Archiviere Artikel Success': props<{ data: Artikel[] }>(),
    'Archiviere Artikel Failure': props<{ error: HttpErrorResponse }>(),
  }
});
