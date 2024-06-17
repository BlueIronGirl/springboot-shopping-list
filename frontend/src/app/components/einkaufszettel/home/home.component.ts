import {Component, OnInit} from '@angular/core';
import {Store} from "@ngrx/store";
import {EinkaufszettelActions} from "../../../store/einkaufszettel/einkaufszettel.actions";
import {selectAllEinkaufszettel} from "../../../store/einkaufszettel/einkaufszettel.selectors";
import {Artikel} from "../../../entities/artikel";
import {Einkaufszettel} from "../../../entities/einkaufszettel";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  einkaufszettel!: Einkaufszettel[];

  constructor(private store: Store) {
  }

  archiviereGekaufteArtikel(einkaufszettel: Einkaufszettel) {
    this.store.dispatch(EinkaufszettelActions.archiviereArtikel({einkaufszettelId: einkaufszettel.id}));
  }

  ngOnInit(): void {
    this.store.dispatch(EinkaufszettelActions.loadEinkaufszettels());

    this.store.select(selectAllEinkaufszettel).subscribe(einkaufszettel => {
      this.einkaufszettel = JSON.parse(JSON.stringify(einkaufszettel)); // deep copy of store, so that changes are possible
      this.einkaufszettel.forEach(einkaufszettel => einkaufszettel.einkaufszettelActions = [
        {label: 'Einstellungen', routerLink: ['/einkaufszettel', einkaufszettel.id], icon: 'fas fa-gear'},
        {
          label: 'Gekaufte Artikel archivieren',
          callback: () => this.archiviereGekaufteArtikel(einkaufszettel),
          icon: 'fas fa-box-archive'
        },
      ]);
    });
  }

  changeArtikelGekauft(einkaufszettel: Einkaufszettel, artikel: Artikel) {
    this.store.dispatch(EinkaufszettelActions.updateArtikel({
      einkaufszettelId: einkaufszettel.id,
      data: artikel
    }));
  }
}
