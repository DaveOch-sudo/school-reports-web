import { useNavigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext.tsx";
import { type FormEvent, useState } from "react";
import { Button } from "../../components/ui/Button.tsx";
import { Input } from "../../components/ui/Input.tsx";
import { Select } from "../../components/ui/Select.tsx";

export default function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();

    try {
      await login({
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
        minHeight: "100vh",
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

        <Input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />

        <Input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <Select
          label="Subject"
          options={[
            { value: "1", label: "Primary One" },
            { value: "2", label: "Primary Two" },
            { value: "3", label: "Primary Three" },
          ]}
        />

        <Button type="submit">Login</Button>
      </form>
    </div>
  );
}
