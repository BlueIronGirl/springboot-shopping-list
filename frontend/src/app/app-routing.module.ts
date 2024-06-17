import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from "./components/einkaufszettel/home/home.component";
import {EditArtikelComponent} from "./components/einkaufszettel/edit-artikel/edit-artikel.component";
import {LoginComponent} from "./components/auth/login/login.component";
import {AuthGuard} from "./guard/auth-guard";
import {RegisterComponent} from "./components/auth/register/register.component";
import {ArchivComponent} from "./components/archiv/archiv.component";
import {EditEinkaufszettelComponent} from "./components/einkaufszettel/edit-einkaufszettel/edit-einkaufszettel.component";
import {RoleGuard} from "./guard/role-guard";
import {UserComponent} from "./components/user/user.component";
import {ROLE_NAME} from "./entities/enum/rolename";
import {
  RegistrationConfirmationComponent
} from "./components/auth/registration-confirmation/registration-confirmation.component";

const routes: Routes = [
  {path: '', redirectTo: 'home', pathMatch: 'full'},
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'registration-confirmation', component: RegistrationConfirmationComponent},
  {path: 'home', component: HomeComponent, canActivate: [AuthGuard]},
  {path: 'einkaufszettel/:einkaufszettelId', component: EditEinkaufszettelComponent, canActivate: [AuthGuard]},
  {path: 'einkaufszettel', component: EditEinkaufszettelComponent, canActivate: [AuthGuard]},
  {path: 'artikel/new/:einkaufszettelId', component: EditArtikelComponent, canActivate: [AuthGuard]},
  {path: 'artikel/:einkaufszettelId/:artikelId', component: EditArtikelComponent, canActivate: [AuthGuard]},
  {path: 'archiv', component: ArchivComponent, canActivate: [AuthGuard]},
  {path: 'user', component: UserComponent, canActivate: [AuthGuard, RoleGuard], data: {expectedRole: ROLE_NAME.ROLE_ADMIN}}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
