import type {UserRole} from './enums';

export interface User {
  id: number;
  name: string;
  email: string;
  phone?: string;
  role: UserRole;
  schoolId?: number;
  schoolName?: string;
  assignedClassId?: number;
  assignedClassName?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  tokenType: string;
  user: User;
}

export interface RegisterUserRequest {
  name: string;
  email: string;
  phone?: string;
  password: string;
  role: UserRole;
  schoolId?: number;
  assignedClassId?: number;
}
