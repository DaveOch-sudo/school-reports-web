import { Routes, Route, Navigate } from "react-router-dom";
import ProtectedRoute from "./ProtectedRoute.tsx";
import LoginPage from "../features/auth/LoginPage.tsx";
import DashboardPage from "../features/dashboard/DashboardPage.tsx";
import AppLayout from "../components/layout/AppLayout.tsx";
import StudentsPage from "../features/students/StudentsPage.tsx";
import ClassesPage from "../features/classes/ClassesPage.tsx";
import MarksheetsPage from "../features/marksheets/MarksheeetsPage.tsx";
import ReportsPage from "../features/reports/ReportsPage.tsx";

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

                <Route element={<AppLayout />}>

                    <Route
                        path="/dashboard"
                        element={<DashboardPage />}
                    />
                    <Route
                        path="/students"
                        element={<StudentsPage />}
                    />

                    <Route
                        path="/classes"
                        element={<ClassesPage />}
                    />

                    <Route
                        path="/marksheets"
                        element={<MarksheetsPage />}
                    />

                    <Route
                        path="/reports"
                        element={<ReportsPage />}
                    />

                </Route>

            </Route>

        </Routes>
    );
}