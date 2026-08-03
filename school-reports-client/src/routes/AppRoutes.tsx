import { Routes, Route, Navigate } from "react-router-dom";
import ProtectedRoute from "./ProtectedRoute.tsx";
import LoginPage from "../features/auth/LoginPage.tsx";
import DashboardPage from "../features/dashboard/DashboardPage.tsx";

export default  function AppRoutes() {
    return(
        <Routes>
            {/* Redirect root URL */}
            <Route
                path="/"
                element={<Navigate to="/login" replace/>}
            />

            {/* Public routes */}
            <Route
                path="/login"
                element={<LoginPage />}
            />

            {/* Protected routes */}
            <Route element={<ProtectedRoute />}>
                <Route
                    path="/dashboard"
                    element={<DashboardPage />}
                    />
            </Route>

        </Routes>
    );
}