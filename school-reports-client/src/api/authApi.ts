import axiosClient from "./axiosClient.ts";
import type {LoginRequest, LoginResponse} from "../types/auth.ts";

export const authApi = {

    login: async (
        data: LoginRequest
    ): Promise<LoginResponse> => {
        const response = await axiosClient.post<LoginResponse>(
            "/auth/login",
            data
        );

        return response.data;
    }

};
