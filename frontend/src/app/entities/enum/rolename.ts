// "as const" turns this into a readonly, "real" constant
import {EnumLike} from "../../util/enumlike";

export const ROLE_NAME = {
  ROLE_ADMIN: 'ROLE_ADMIN',
  ROLE_USER: 'ROLE_USER',
  ROLE_GUEST: 'ROLE_GUEST',
} as const;

// This is the associated type of the readonly constant
// In case you want to use it as a type
export type RoleName = EnumLike<typeof ROLE_NAME>;

// Function to get all role values
export const getAllRoleValues = (): RoleName[] => {
  return Object.values(ROLE_NAME);
}
