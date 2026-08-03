import {useNavigate} from "react-router-dom";
import {useAuth} from "../../context/AuthContext.tsx";
import {type FormEvent, useState} from "react";

export default function LoginPage() {

    const navigate = useNavigate();
    const { login } = useAuth();
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();

        try {
            await  login({
                email,
                password,
            });
            navigate("/dashboard");
        } catch (error) {
            console.error(error);
            alert("Invalid email or password.");
        }
    };

    return (
        <div
            style={{
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
                minHeight: "100vh"
            }}
        >
            <form
                onSubmit={handleSubmit}
                style={{
                    display: "flex",
                    flexDirection: "column",
                    gap: "1rem",
                    width: "320px",
                }}
            >
                <h2>Login</h2>

                <input
                    type="email"
                    placeholder="Email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                />

                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />

                <button type="submit">
                    Login
                </button>
            </form>

        </div>
    );
}