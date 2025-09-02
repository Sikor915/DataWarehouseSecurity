import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./login.css"; // korzystamy z tego samego CSS co Login

const Register: React.FC = () => {
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const navigate = useNavigate();

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (password !== confirmPassword) {
      alert("Hasła nie są takie same!");
      return;
    }

    // pobieramy istniejących użytkowników z localStorage
    const users = JSON.parse(localStorage.getItem("users") || "[]");

    // sprawdzamy, czy login już istnieje
    if (users.find((u: any) => u.username === username)) {
      alert("Użytkownik o takim loginie już istnieje");
      return;
    }

    // zapisujemy nowego użytkownika
    users.push({ username, email, password });
    localStorage.setItem("users", JSON.stringify(users));

    alert("Rejestracja zakończona sukcesem! Możesz się zalogować.");
    navigate("/login");
  };

  return (
    <div className="login-container">
      <h2>Register</h2>
      <form onSubmit={handleSubmit} className="login-form">
        <label>
          Login:
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
        </label>
        <label>
          E-mail:
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
        <label>
          Confirm Password:
          <input
            type="password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            required
          />
        </label>
        <button type="submit">Register</button>
      </form>
    </div>
  );
};

export default Register;
