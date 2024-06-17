import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import {catchError, map, switchMap} from 'rxjs/operators';
import { of } from 'rxjs';
import { ArchivActions } from './archiv.actions';
import {EinkaufszettelService} from "../../service/einkaufszettel.service";


@Injectable()
export class ArchivEffects {

  loadArchiv$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(ArchivActions.loadArchiv),
      switchMap(() => this.einkaufszettelService.loadAllArtikelArchiv().pipe(
          map(artikels => ArchivActions.loadArchivSuccess({data: artikels})),
          catchError(error => of(ArchivActions.loadArchivFailure({error})))
        )
      )
    );
  });


  constructor(private actions$: Actions, private einkaufszettelService: EinkaufszettelService) {}
}
