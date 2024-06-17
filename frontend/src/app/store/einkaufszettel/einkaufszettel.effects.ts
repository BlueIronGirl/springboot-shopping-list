import {Injectable} from '@angular/core';
import {Actions, createEffect, ofType} from '@ngrx/effects';
import {catchError, concatMap, map, tap} from 'rxjs/operators';
import {of} from 'rxjs';
import {EinkaufszettelActions} from './einkaufszettel.actions';
import {EinkaufszettelService} from "../../service/einkaufszettel.service";
import {Router} from "@angular/router";
import {MessageService} from "primeng/api";


@Injectable()
export class EinkaufszettelEffects {
  loadEinkaufszettels$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(EinkaufszettelActions.loadEinkaufszettels),
      this.loadAllEinkaufszettel()
    );
  });

  createEinkaufszettel$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(EinkaufszettelActions.createEinkaufszettel),
      concatMap(inputData => this.einkaufszettelService.createEinkaufszettel(inputData.data).pipe(
        map(data => EinkaufszettelActions.createEinkaufszettelSuccess({data: data})),
        catchError(error => of(EinkaufszettelActions.createEinkaufszettelFailure({error})))
      ))
    )
  });

  createEinkaufszettelSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(EinkaufszettelActions.createEinkaufszettelSuccess),
      this.navigateToHomeWithMessage('Einkaufszettel wurde gespeichert'),
      this.loadAllEinkaufszettel()
    )
  });

  updateEinkaufszettel$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(EinkaufszettelActions.updateEinkaufszettel),
      map(action => action.data),
      concatMap(inputData => this.einkaufszettelService.updateEinkaufszettel(inputData).pipe(
        map(data => EinkaufszettelActions.updateEinkaufszettelSuccess({data: data})),
        catchError(error => of(EinkaufszettelActions.updateEinkaufszettelFailure({error})))
      ))
    )
  });

  updateEinkaufszettelSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(EinkaufszettelActions.updateEinkaufszettelSuccess),
      this.navigateToHomeWithMessage('Einkaufszettel wurde gespeichert'),
      this.loadAllEinkaufszettel()
    )
  });

  deleteEinkaufszettel$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(EinkaufszettelActions.deleteEinkaufszettel),
      map(action => action.data),
      concatMap(inputData => this.einkaufszettelService.deleteEinkaufszettel(inputData).pipe(
        map(data => EinkaufszettelActions.deleteEinkaufszettelSuccess({data: data})),
        catchError(error => of(EinkaufszettelActions.deleteEinkaufszettelFailure({error})))
      )),
      this.loadAllEinkaufszettel()
    )
  });

  deleteEinkaufszettelSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(EinkaufszettelActions.deleteEinkaufszettelSuccess),
      this.navigateToHomeWithMessage('Einkaufszettel wurde gelöscht'),
      this.loadAllEinkaufszettel()
    )
  });

  createArtikel$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(EinkaufszettelActions.createArtikel),
      concatMap(inputData => this.einkaufszettelService.createArtikel(inputData.einkaufszettelId, inputData.data).pipe(
        map(data => EinkaufszettelActions.createArtikelSuccess({data: data})),
        catchError(error => of(EinkaufszettelActions.createArtikelFailure({error})))
      ))
    )
  });

  createArtikelSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(EinkaufszettelActions.createArtikelSuccess),
      this.navigateToHomeWithMessage('Artikel wurde gespeichert'),
      this.loadAllEinkaufszettel()
    )
  });

  updateArtikel$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(EinkaufszettelActions.updateArtikel),
      concatMap(inputData => this.einkaufszettelService.updateArtikel(inputData.einkaufszettelId, inputData.data).pipe(
        map(data => EinkaufszettelActions.updateArtikelSuccess({data: data})),
        catchError(error => of(EinkaufszettelActions.updateArtikelFailure({error})))
      ))
    )
  });

  updateArtikelSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(EinkaufszettelActions.updateArtikelSuccess),
      this.navigateToHomeWithMessage('Artikel wurde gespeichert'),
      this.loadAllEinkaufszettel()
    )
  });

  deleteArtikel$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(EinkaufszettelActions.deleteArtikel),
      concatMap(inputData => this.einkaufszettelService.deleteArtikel(inputData.einkaufszettelId, inputData.data).pipe(
        map(data => EinkaufszettelActions.deleteArtikelSuccess({data: data})),
        catchError(error => of(EinkaufszettelActions.deleteArtikelFailure({error})))
      ))
    )
  });

  deleteArtikelSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(EinkaufszettelActions.deleteArtikelSuccess),
      this.navigateToHomeWithMessage('Artikel wurde gelöscht'),
      this.loadAllEinkaufszettel()
    )
  });

  archiviereArtikel$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(EinkaufszettelActions.archiviereArtikel),
      concatMap(inputData => this.einkaufszettelService.archiviereArtikel(inputData.einkaufszettelId).pipe(
        map(data => EinkaufszettelActions.archiviereArtikelSuccess({data: data})),
        catchError(error => of(EinkaufszettelActions.archiviereArtikelFailure({error})))
      ))
    )
  });

  archiviereArtikelSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(EinkaufszettelActions.archiviereArtikelSuccess),
      this.navigateToHomeWithMessage('Artikel wurden archiviert'),
      this.loadAllEinkaufszettel()
    )
  });

  private loadAllEinkaufszettel() {
    return concatMap(() => this.einkaufszettelService.getAllEinkaufszettel().pipe(
      map(data => EinkaufszettelActions.loadEinkaufszettelsSuccess({data: data})),
      catchError(error => of(EinkaufszettelActions.loadEinkaufszettelsFailure({error})))
    ));
  }

  private navigateToHomeWithMessage(message: string) {
    return this.navigateWithMessage(message, 'home');
  }

  private navigateWithMessage(message: string, navigationTarget: string) {
    return tap(() => {
      this.router.navigateByUrl(`/${navigationTarget}`);
      this.messageService.clear();
      this.messageService.add({severity: 'success', summary: message});
    });
  }

  constructor(private actions$: Actions, private messageService: MessageService, private router: Router, private einkaufszettelService: EinkaufszettelService) {
  }
}
