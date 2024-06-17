import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {Observable, retry, throwError} from "rxjs";
import {environment} from "../../environments/environment";
import {User} from "../entities/user";
import {Einkaufszettel} from "../entities/einkaufszettel";
import {catchError} from "rxjs/operators";
import {Role} from "../entities/role";

@Injectable({
  providedIn: 'root'
})
export class RoleService {
  private api = `${environment.webserviceurl}`;

  constructor(private httpClient: HttpClient) {
  }

  private static errorHandler(error: HttpErrorResponse): Observable<never> {
    console.error('Fehler aufgetreten!' + error);
    return throwError(() => error);
  }

  getAllRoles(): Observable<Role[]> {
    return this.httpClient.get<Role[]>(`${this.api}/role`).pipe(
      retry(3)
    );
  }
}
