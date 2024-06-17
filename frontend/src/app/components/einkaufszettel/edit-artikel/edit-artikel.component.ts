import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {Store} from "@ngrx/store";
import {selectArtikelById} from "../../../store/einkaufszettel/einkaufszettel.selectors";
import {Artikel} from "../../../entities/artikel";
import {EinkaufszettelActions} from "../../../store/einkaufszettel/einkaufszettel.actions";
import {ConfirmationService} from "primeng/api";

@Component({
  selector: 'app-edit-artikel',
  templateUrl: './edit-artikel.component.html',
  styleUrls: ['./edit-artikel.component.scss']
})
export class EditArtikelComponent implements OnInit {
  artikelForm: FormGroup = this.formBuilder.group({
    id: [{value: '', disabled: true}, Validators.required],
    name: [{value: ''}, Validators.compose([Validators.required, Validators.minLength(1)])],
    // kategorie: ['', Validators.compose([Validators.required, Validators.minLength(1)])], TODO
    anzahl: ['', Validators.compose([Validators.required, Validators.min(1), Validators.max(100)])],
    gekauft: ['', Validators.required]
  });

  einkaufszettelId: number = 0;
  edit: boolean = false;
  header: string = '';

  constructor(private activatedRoute: ActivatedRoute, private formBuilder: FormBuilder, private store: Store, private confirmationService: ConfirmationService,) {
  }

  ngOnInit(): void {
    this.einkaufszettelId = Number(this.activatedRoute.snapshot.paramMap.get('einkaufszettelId'));
    const artikelId = Number(this.activatedRoute.snapshot.paramMap.get('artikelId'));
    if (artikelId > 0) {
      this.initEdit(artikelId);
    } else {
      this.initNew();
    }
  }

  private initEdit(artikelId: number) {
    this.edit = true;
    this.header = 'Artikel bearbeiten';

    this.store.select(selectArtikelById(this.einkaufszettelId, artikelId)).subscribe(artikel => this.artikelForm.patchValue(artikel));
  }

  private initNew() {
    const emptyArtikel: Artikel = {
      id: -1,
      name: '',
      anzahl: 1,
      gekauft: false
    };
    this.artikelForm.patchValue(emptyArtikel);
  }

  save() {
    const formValue = this.artikelForm.getRawValue();
    const artikel: Artikel = {...formValue};

    if (this.edit) {
      this.store.dispatch(EinkaufszettelActions.updateArtikel({
        einkaufszettelId: this.einkaufszettelId,
        data: artikel
      }));
    } else {
      this.store.dispatch(EinkaufszettelActions.createArtikel({
        einkaufszettelId: this.einkaufszettelId,
        data: artikel
      }));
    }
  }

  delete(event: Event) {
    const formValue = this.artikelForm.getRawValue();
    const artikel: Artikel = {...formValue};

    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: 'Sind Sie sich sicher, dass Sie den Artikel löschen möchten?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Ja',
      rejectLabel: 'Nein',
      acceptIcon: "none",
      rejectIcon: "none",
      rejectButtonStyleClass: "p-button-text",
      accept: () => {
        this.store.dispatch(EinkaufszettelActions.deleteArtikel({
          einkaufszettelId: this.einkaufszettelId,
          data: artikel
        }));
        this.store.dispatch(EinkaufszettelActions.loadEinkaufszettels());
      }
    });
  }
}
