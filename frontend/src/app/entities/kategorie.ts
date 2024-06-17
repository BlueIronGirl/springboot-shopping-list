import {Artikel} from "./artikel";

export interface Kategorie {
  id: number;
  name: string;
  artikels?: Artikel[];
}
