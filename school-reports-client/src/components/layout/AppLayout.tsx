import { Outlet } from "react-router-dom";
import Sidebar from "./Sidebar.tsx";
import Header from "./Header.tsx";

export default function AppLayout() {
  return (
    <div className="min-h-screen bg-slate-50">
      <Sidebar />

      <Header />

      <main className="ml-64 min-h-screen pt-16">
        <div className="min-h-[calc(100vh-4rem)]">
          <Outlet />
        </div>
      </main>
    </div>
  );
}
