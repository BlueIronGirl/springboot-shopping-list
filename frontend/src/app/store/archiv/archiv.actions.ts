import { createActionGroup, emptyProps, props } from '@ngrx/store';
import {HttpErrorResponse} from "@angular/common/http";
import {ArtikelArchiv} from "../../entities/artikelarchiv";

export const ArchivActions = createActionGroup({
  source: 'Archiv',
  events: {
    'Load Archiv': emptyProps(),
    'Load Archiv Success': props<{ data: ArtikelArchiv[] }>(),
    'Load Archiv Failure': props<{ error: HttpErrorResponse }>(),
  }
});
