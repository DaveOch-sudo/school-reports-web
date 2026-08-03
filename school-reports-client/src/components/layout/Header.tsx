import {useAuth} from "../../context/AuthContext.tsx";

export default function Header() {

    const {
        user,
        logout
    } = useAuth();

    return (
        <header>

            <div>
                Welcome, {user?.name}
            </div>

            <button onClick={logout}>
                Logout
            </button>

        </header>
    );
}