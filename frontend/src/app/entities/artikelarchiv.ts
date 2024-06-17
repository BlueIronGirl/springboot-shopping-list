import {Einkaufszettel} from "./einkaufszettel";
import {User} from "./user";

export interface ArtikelArchiv {
  id: number;
  name: string;
  anzahl: number;
  gekauft: boolean;
  erstellungsZeitpunkt?: string;
  kaufZeitpunkt?: string;
  einkaufszettel: Einkaufszettel;
  kaeufer?: User;
}
