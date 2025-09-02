import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import "./login.css";

const Login: React.FC = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    const users = JSON.parse(localStorage.getItem("users") || "[]");
    const user = users.find(
      (u: any) => u.username === username && u.password === password
    );

    if (user) {
      localStorage.setItem("token", "fake-jwt-token");
      navigate("/dashboard");
    } else {
      alert("Błędny login lub hasło");
    }
  };

  return (
    <div className="login-container">
      <h2>Login</h2>
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
