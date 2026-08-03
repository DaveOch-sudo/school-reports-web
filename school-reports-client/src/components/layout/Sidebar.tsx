import {NavLink} from "react-router-dom";
import {useAuth} from "../../context/AuthContext.tsx";

export default function Sidebar() {
    const menuItems = [
        {
            label: "Dashboard",
            path: "/dashboard",
            roles: [
                "SUPER_ADMIN",
                "SCHOOL_ADMIN",
                "CLASS_TEACHER",
                "SUBJECT_TEACHER"
            ]
        },
        {
            label: "Students",
            path: "/students",
            roles: [
                "SUPER_ADMIN",
                "SCHOOL_ADMIN"
            ]
        },
        {
            label: "Classes",
            path: "/classes",
            roles: [
                "SUPER_ADMIN",
                "SCHOOL_ADMIN"
            ]
        },
        {
            label: "Marksheets",
            path: "/marksheets",
            roles: [
                "SUPER_ADMIN",
                "SCHOOL_ADMIN",
                "CLASS_TEACHER",
                "SUBJECT_TEACHER"
            ]
        },
        {
            label: "Reports",
            path: "/reports",
            roles: [
                "SUPER_ADMIN",
                "SCHOOL_ADMIN",
                "CLASS_TEACHER"
            ]
        }
    ];
    const {hasAnyRole} = useAuth();

    return (
        <aside>
            <h2>School Reports</h2>

            <nav>
                <ul>
                    { menuItems
                        .filter(item => hasAnyRole(...item.roles))
                        .map(item => (
                           <li>
                               <NavLink to={item.path} >{item.label}</NavLink>
                           </li>
                        ))
                    }

                    {/*<li>*/}
                    {/*    <NavLink to="/dashboard">Dashboard</NavLink>*/}
                    {/*</li>*/}
                    {/*<li>*/}
                    {/*    <NavLink to="/students">Students</NavLink>*/}
                    {/*</li>*/}
                    {/*<li>*/}
                    {/*    <NavLink to="/classes">Classes</NavLink>*/}
                    {/*</li>*/}
                    {/*<li>*/}
                    {/*    <NavLink to="/marksheets">Marksheets</NavLink>*/}
                    {/*</li>*/}
                    {/*<li>*/}
                    {/*    <NavLink to="/reports">Reports</NavLink>*/}
                    {/*</li>*/}
                </ul>
            </nav>
        </aside>
    );
}