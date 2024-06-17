import {Role} from "./role";

export interface User {
  id?: number;
  username: string;
  password: string;
  name?: string;
  email?: string;
  token?: string;
  roles?: Role[];
  createdAt?: Date;
  lastLoggedIn?: Date;
}
