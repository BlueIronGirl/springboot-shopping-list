export interface Action {
  label: string;
  icon: string;
  callback?: () => void;
  routerLink?: any[];
}
