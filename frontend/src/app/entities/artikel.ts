import {User} from "./user";

export interface Artikel {
  id: number;
  name: string;
  anzahl: number;
  gekauft: boolean;
  erstellungsZeitpunkt?: string;
  kaufZeitpunkt?: string;
  kaeufer?: User;
}
