import React from "react";
import { Link } from "react-router-dom";

export default function NotFoundPage() {
  const token = localStorage.getItem("token"); // sprawdzamy, czy użytkownik jest zalogowany

  return (
    <div style={{ textAlign: "center", marginTop: "50px" }}>
      <h1 style={{ fontSize: "6rem", marginBottom: "20px" }}>404</h1>
      <p style={{ fontSize: "1.5rem", marginBottom: "30px" }}>Page not found</p>

      {token ? (
        <Link
          to="/dashboard"
          style={{
            padding: "12px 24px",
            backgroundColor: "#007bff",
            color: "white",
            borderRadius: "6px",
            textDecoration: "none",
            fontWeight: "bold",
            transition: "background-color 0.3s",
          }}
        >
          Return to Dashboard
        </Link>
      ) : (
        <Link
          to="/login"
          style={{
            padding: "12px 24px",
            backgroundColor: "#28a745",
            color: "white",
            borderRadius: "6px",
            textDecoration: "none",
            fontWeight: "bold",
            transition: "background-color 0.3s",
          }}
        >
          Login
        </Link>
      )}
    </div>
  );
}
