import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from "@angular/common/http";
import {Observable, retry, throwError} from "rxjs";
import {environment} from "../../environments/environment";
import {User} from "../entities/user";
import {Einkaufszettel} from "../entities/einkaufszettel";
import {catchError} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private api = `${environment.webserviceurl}`;

  constructor(private httpClient: HttpClient) {
  }

  private static errorHandler(error: HttpErrorResponse): Observable<never> {
    console.error('Fehler aufgetreten!' + error);
    return throwError(() => error);
  }

  getAllUsersFriends(): Observable<User[]> {
    return this.httpClient.get<User[]>(`${this.api}/user/friends`).pipe(
      retry(3)
    );
  }

  getAllUsers(): Observable<User[]> {
    return this.httpClient.get<User[]>(`${this.api}/user`).pipe(
      retry(3)
    );
  }

  updateUser(user: User) {
    return this.httpClient.put<User>(`${this.api}/user/${user.id}`, user).pipe(
      catchError(UserService.errorHandler)
    );
  }

  deleteUser(user: User) {
    return this.httpClient.delete<User>(`${this.api}/user/${user.id}`).pipe(
      catchError(UserService.errorHandler)
    );
  }
}
