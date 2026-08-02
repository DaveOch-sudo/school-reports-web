import {createContext, useState, useEffect, type ReactNode, useContext} from "react";
import {authApi} from "../api/authApi.ts";
import type {LoginRequest, User} from "../types/auth.ts";

interface AuthContextType {
    user: User | null;
    token: string | null;
    login: (credentials: LoginRequest) => Promise<void>;
    logout: () => void;
    isAuthenticated: boolean;
}

const AuthContext = createContext<
    AuthContextType | undefined
>(undefined);

interface AuthProviderProps {
    children: ReactNode;
}

export function AuthProvider(
    {children,}: AuthProviderProps) {

    const [user, setUser] = useState<User | null>(null);
    const [token, setToken] = useState<string | null>(null);
    const isAuthenticated = !!token;

    const login = async (
        credentials: LoginRequest
    )=> {
        const response = await authApi.login(credentials);

        setUser(response.user);
        setToken(response.token);

        localStorage.setItem("token", response.token);
        localStorage.setItem("user", JSON.stringify(response.user));
    }

    const logout = () => {
        setUser(null);
        setToken(null);
        localStorage.removeItem("token");
        localStorage.removeItem("user");
    }

    useEffect(() => {
        const storedToken = localStorage.getItem("token");
        const storedUser = localStorage.getItem("user");

        if (storedToken && storedUser) {
            setToken(storedToken);
            setUser(JSON.parse(storedUser));
        }
    }, []);

    return (
        <AuthContext.Provider
            value={{
                user,
                token,
                login,
                logout,
                isAuthenticated
            }}
            >
            {children}
        </AuthContext.Provider>
    )
}

export function useAuth() {
    const context = useContext(AuthContext);

    if(!context) {
        throw new Error(
          "useAuth must be used within an AuthProvider"
        );
    }

    return context;
}