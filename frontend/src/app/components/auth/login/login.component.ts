import {Component, OnInit} from '@angular/core';
import {Store} from "@ngrx/store";
import {MessageService} from "primeng/api";
import {User} from "../../../entities/user";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {AuthActions} from "../../../store/auth/auth.actions";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  user?: User;
  loginForm: FormGroup = this.formBuilder.group({
    username: ['', Validators.required],
    password: ['', Validators.required]
  });

  constructor(private formBuilder: FormBuilder, private store: Store, private router: Router, private messageService: MessageService) {
  }

  ngOnInit(): void {
    const user: User = {
      username: '',
      password: ''
    }
    this.loginForm.patchValue(user);
  }

  login() {
    const formValue = this.loginForm.getRawValue();
    const user: User = {...formValue};

    this.store.dispatch(AuthActions.login({data: user}));
  }

  reset() {
    this.ngOnInit();
  }

  register() {
    this.router.navigateByUrl("/register")
  }
}
