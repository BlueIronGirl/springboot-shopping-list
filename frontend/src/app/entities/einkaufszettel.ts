import {Artikel} from "./artikel";
import {User} from "./user";
import {Action} from "../util/action";

export interface Einkaufszettel {
  id: number;
  name: string;
  artikels?: Artikel[];
  owners: User[];
  sharedWith: User[];
  einkaufszettelActions?: Action[];
}
