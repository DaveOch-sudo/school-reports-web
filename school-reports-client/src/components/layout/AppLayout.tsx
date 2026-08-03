import {Outlet} from "react-router-dom";
import Sidebar from "./Sidebar.tsx";
import Header from "./Header.tsx";


export default function AppLayout() {

    return(
        <div>
            <Sidebar />
            <Header />

            <main>
                <Outlet />
            </main>


        </div>
    );
}