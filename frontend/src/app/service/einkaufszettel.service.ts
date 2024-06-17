import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {Observable, retry, throwError} from "rxjs";
import {Artikel} from "../entities/artikel";
import {catchError} from "rxjs/operators";
import {environment} from "../../environments/environment";
import {Einkaufszettel} from "../entities/einkaufszettel";
import {ArtikelArchiv} from "../entities/artikelarchiv";

@Injectable({
  providedIn: 'root'
})
export class EinkaufszettelService {
  private api = `${environment.webserviceurl}`;

  constructor(private httpClient: HttpClient) {
  }

  private static errorHandler(error: HttpErrorResponse): Observable<never> {
    console.error('Fehler aufgetreten!' + error);
    return throwError(() => error);
  }

  getAllEinkaufszettel(): Observable<Einkaufszettel[]> {
    return this.httpClient.get<Einkaufszettel[]>(`${this.api}/einkaufszettel`).pipe(
      retry(3)
    );
  }

  createEinkaufszettel(einkaufszettel: Einkaufszettel) {
    return this.httpClient.post<Einkaufszettel>(`${this.api}/einkaufszettel`, einkaufszettel).pipe(
      catchError(EinkaufszettelService.errorHandler)
    );
  }

  updateEinkaufszettel(einkaufszettel: Einkaufszettel) {
    return this.httpClient.put<Einkaufszettel>(`${this.api}/einkaufszettel/${einkaufszettel.id}`, einkaufszettel).pipe(
      catchError(EinkaufszettelService.errorHandler)
    );
  }

  deleteEinkaufszettel(einkaufszettel: Einkaufszettel) {
    return this.httpClient.delete<Einkaufszettel>(`${this.api}/einkaufszettel/${einkaufszettel.id}`).pipe(
      catchError(EinkaufszettelService.errorHandler)
    );
  }

  createArtikel(einkaufszettelId: number, artikel: Artikel) {
    return this.httpClient.post<Artikel>(`${this.api}/einkaufszettel/${einkaufszettelId}/artikel`, artikel).pipe(
      catchError(EinkaufszettelService.errorHandler)
    );
  }

  updateArtikel(einkaufszettelId: number, artikel: Artikel) {
    return this.httpClient.put<Artikel>(`${this.api}/einkaufszettel/${einkaufszettelId}/artikel/${artikel.id}`, artikel).pipe(
      catchError(EinkaufszettelService.errorHandler)
    );
  }

  deleteArtikel(einkaufszettelId: number, artikel: Artikel) {
    return this.httpClient.delete<Artikel>(`${this.api}/einkaufszettel/${einkaufszettelId}/artikel/${artikel.id}`).pipe(
      catchError(EinkaufszettelService.errorHandler)
    );
  }

  archiviereArtikel(einkaufszettelId: number) {
    return this.httpClient.post<Artikel[]>(`${this.api}/einkaufszettel/${einkaufszettelId}/archiviereGekaufteArtikel`, null).pipe(
      catchError(EinkaufszettelService.errorHandler)
    );
  }

  loadAllArtikelArchiv(): Observable<ArtikelArchiv[]> {
    return this.httpClient.get<ArtikelArchiv[]>(`${this.api}/archiv`).pipe(
      retry(3)
    );
  }
}
