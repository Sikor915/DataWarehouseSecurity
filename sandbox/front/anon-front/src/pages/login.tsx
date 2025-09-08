import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import "./login.css";

const Login: React.FC = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      const response = await fetch("http://localhost:8080/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
      });

      if (!response.ok) {
        if (response.status === 401) {
          alert("Błędny email lub hasło");
        } else {
          alert("Błąd serwera");
        }
        return;
      }

      const data = await response.json();

      // zapis tokena JWT w localStorage
      localStorage.setItem("token", data.token);

      // przekierowanie do panelu użytkownika
      navigate("/home");
    } catch (error) {
      console.error("Błąd logowania:", error);
      alert("Nie udało się połączyć z serwerem");
    }
  };

  return (
    <div className="login-container">
      <h2>Login</h2>
      <form onSubmit={handleSubmit} className="login-form">
        <label>
          Email:
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </label>
        <label>
          Password:
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </label>
        <button type="submit">Submit</button>
      </form>

      <p className="register-text">
        Don't have an account?{" "}
        <Link to="/register" className="register-link">
          Click here
        </Link>
      </p>
    </div>
  );
};

export default Login;
