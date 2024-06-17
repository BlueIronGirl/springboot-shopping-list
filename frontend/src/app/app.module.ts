import {isDevMode, NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HomeComponent} from './components/einkaufszettel/home/home.component';
import {StoreModule} from '@ngrx/store';
import {StoreDevtoolsModule} from '@ngrx/store-devtools';
import {EffectsModule} from '@ngrx/effects';
import {EinkaufszettelEffects} from "./store/einkaufszettel/einkaufszettel.effects";
import {
  einkaufszettelFeature,
  einkaufszettelFeatureKey,
  einkaufszettelReducer
} from "./store/einkaufszettel/einkaufszettel.reducer";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {CardModule} from "primeng/card";
import {CheckboxModule} from "primeng/checkbox";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {PanelModule} from "primeng/panel";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {EditArtikelComponent} from './components/einkaufszettel/edit-artikel/edit-artikel.component';
import {InputNumberModule} from "primeng/inputnumber";
import {ButtonModule} from "primeng/button";
import {InputTextModule} from "primeng/inputtext";
import {MessageModule} from "primeng/message";
import {MessagesModule} from "primeng/messages";
import {LoginComponent} from './components/auth/login/login.component';
import {TokenInterceptor} from "./interceptor/token-interceptor.service";
import {PasswordModule} from "primeng/password";
import {RegisterComponent} from './components/auth/register/register.component';
import {ConfirmationService, MessageService} from "primeng/api";
import {ToastModule} from "primeng/toast";
import {ArchivComponent} from './components/archiv/archiv.component';
import {DividerModule} from "primeng/divider";
import {EditEinkaufszettelComponent} from './components/einkaufszettel/edit-einkaufszettel/edit-einkaufszettel.component';
import {MultiSelectModule} from "primeng/multiselect";
import {ConfirmDialogModule} from "primeng/confirmdialog";
import {TableModule} from "primeng/table";
import {TooltipModule} from "primeng/tooltip";
import { UserComponent } from './components/user/user.component';
import { RegistrationConfirmationComponent } from './components/auth/registration-confirmation/registration-confirmation.component';
import {AuthEffects} from "./store/auth/auth.effects";
import {authFeature, authFeatureKey, authReducer} from "./store/auth/auth.reducer";
import {UserEffects} from "./store/user/user.effects";
import {userFeature} from "./store/user/user.reducer";
import {ArchivEffects} from "./store/archiv/archiv.effects";
import {archivFeature} from "./store/archiv/archiv.reducer";
import { NavigationLinksComponent } from './components/common/navigation-links/navigation-links.component';
import { SplitButtonComponent } from './components/common/split-button/split-button.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    EditArtikelComponent,
    LoginComponent,
    RegisterComponent,
    ArchivComponent,
    EditEinkaufszettelComponent,
    UserComponent,
    RegistrationConfirmationComponent,
    NavigationLinksComponent,
    SplitButtonComponent
  ],
  imports: [
    // standard angular
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,

    // ngrx
    StoreModule.forRoot({}, {}),
    EffectsModule.forRoot([]),
    StoreDevtoolsModule.instrument({maxAge: 25, logOnly: !isDevMode()}),
    EffectsModule.forFeature([EinkaufszettelEffects]),
    StoreModule.forFeature(einkaufszettelFeature),
    EffectsModule.forFeature([AuthEffects]),
    StoreModule.forFeature(authFeature),
    EffectsModule.forFeature([UserEffects]),
    StoreModule.forFeature(userFeature),
    EffectsModule.forFeature([ArchivEffects]),
    StoreModule.forFeature(archivFeature),

    //primeng
    ButtonModule,
    CardModule,
    CheckboxModule,
    InputNumberModule,
    PasswordModule,
    InputTextModule,
    PanelModule,
    MessageModule,
    MessagesModule,
    ToastModule,
    DividerModule,
    MultiSelectModule,
    ConfirmDialogModule,
    TableModule,
    TooltipModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true
    },
    MessageService, ConfirmationService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
