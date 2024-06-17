import {Component, OnInit} from '@angular/core';
import {Einkaufszettel} from "../../../entities/einkaufszettel";
import {Store} from "@ngrx/store";
import {EinkaufszettelActions} from "../../../store/einkaufszettel/einkaufszettel.actions";
import {selectEinkaufszettelById} from "../../../store/einkaufszettel/einkaufszettel.selectors";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {User} from "../../../entities/user";
import {ConfirmationService} from "primeng/api";
import {UserActions} from "../../../store/user/user.actions";
import {selectAllUsersFriends} from "../../../store/user/user.selectors";

@Component({
  selector: 'app-einkaufszettel',
  templateUrl: './edit-einkaufszettel.component.html',
  styleUrls: ['./edit-einkaufszettel.component.scss']
})
export class EditEinkaufszettelComponent implements OnInit {
  einkaufszettelForm: FormGroup = this.formBuilder.group({
    id: [{value: '', disabled: true}, Validators.required],
    name: [{value: ''}, Validators.compose([Validators.required, Validators.minLength(1)])],
    owners: new FormControl<User[] | null>([]),
    sharedWith: new FormControl<User[] | null>([])
  });

  edit: boolean = false;
  header: string = '';
  allUsersFriends: User[] = [];

  constructor(private activatedRoute: ActivatedRoute, private formBuilder: FormBuilder, private store: Store, private confirmationService: ConfirmationService) {
  }

  ngOnInit(): void {
    this.store.dispatch(EinkaufszettelActions.loadEinkaufszettels());
    this.store.dispatch(UserActions.loadUsersFriends());

    const einkaufszettelId = Number(this.activatedRoute.snapshot.paramMap.get('einkaufszettelId'));
    if (einkaufszettelId > 0) {
      this.initEdit(einkaufszettelId);
    } else {
      this.initNew();
    }
    this.store.select(selectAllUsersFriends).subscribe(users => this.allUsersFriends = users);
  }

  private initEdit(einkaufszettelId: number) {
    this.edit = true;
    this.header = 'Einkaufszettel bearbeiten';

    this.store.select(selectEinkaufszettelById(einkaufszettelId)).subscribe(einkaufszettel => this.einkaufszettelForm.patchValue(einkaufszettel));
  }

  private initNew() {
    const einkaufszettel: Einkaufszettel = {
      id: -1,
      name: '',
      owners: [],
      sharedWith: []
    };
    this.einkaufszettelForm.patchValue(einkaufszettel);
  }

  save() {
    const formValue = this.einkaufszettelForm.getRawValue();
    const einkaufszettel: Einkaufszettel = {...formValue};

    if (this.edit) {
      this.store.dispatch(EinkaufszettelActions.updateEinkaufszettel({data: einkaufszettel}));
    } else {
      this.store.dispatch(EinkaufszettelActions.createEinkaufszettel({data: einkaufszettel}));
    }
  }

  delete(event: Event) {
    const formValue = this.einkaufszettelForm.getRawValue();
    const einkaufszettel: Einkaufszettel = {...formValue};

    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: 'Sind Sie sich sicher, dass Sie den Einkaufszettel löschen möchten?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Ja',
      rejectLabel: 'Nein',
      acceptIcon: "none",
      rejectIcon: "none",
      rejectButtonStyleClass: "p-button-text",
      accept: () => {
        this.store.dispatch(EinkaufszettelActions.deleteEinkaufszettel({data: einkaufszettel}));
        this.store.dispatch(EinkaufszettelActions.loadEinkaufszettels());
      }
    });
  }
}
