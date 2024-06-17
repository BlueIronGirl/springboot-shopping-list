import {Component, Input} from '@angular/core';
import {Action} from "../../../util/action";
import {Router} from "@angular/router";

@Component({
  selector: 'app-split-button',
  templateUrl: './split-button.component.html',
  styleUrls: ['./split-button.component.scss']
})
export class SplitButtonComponent {
  @Input() actions: Action[] = [];
  @Input({ required: true }) width!: string;
  isOpen = false;

  constructor(private router: Router) {}

  toggleMenu() {
    this.isOpen = !this.isOpen;
  }

  executeAction(action: Action) {
    if (action.callback) {
      action.callback();
    }
    if (action.routerLink) {
      this.router.navigate(action.routerLink);
    }
    this.isOpen = false;
  }
}
