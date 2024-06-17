import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {AuthService} from "../../../service/auth.service";

@Component({
  selector: 'app-registration-confirmation',
  templateUrl: './registration-confirmation.component.html',
  styleUrls: ['./registration-confirmation.component.scss']
})
export class RegistrationConfirmationComponent implements OnInit {
  message: string = '';
  error: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService
  ) {

  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const token = params['token'];
      if (token) {
        this.authService.confirmRegistrationToken(token).subscribe(
          response => {
            this.message = 'Die E-Mail-Adresse wurde erfolgreich bestätigt!';
            this.error = false;
          },
          error => {
            this.message = 'E-Mail-Bestätigung fehlgeschlagen. Der Token ist invalide oder abgelaufen.';
            this.error = true;
          }
        );
      } else {
        this.message = 'Der Link ist ungültig';
        this.error = true;
      }
    });
  }
}
