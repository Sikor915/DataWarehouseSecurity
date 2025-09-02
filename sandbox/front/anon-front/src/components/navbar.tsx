import React from "react";
import { Link, useNavigate } from "react-router-dom";
import "./css/navbar.css";
import logo from "../assets/logo.png"; // upewnij się, że ścieżka do logo jest poprawna

const Navbar: React.FC = () => {
  const navigate = useNavigate();
  const token = localStorage.getItem("token"); // sprawdzamy, czy użytkownik zalogowany

  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/login");
  };

  return (
    <nav className="navbar">
      <div className="logo">
        <div className="logo-crop">
          <img src={logo} alt="Logo" />
        </div>
      </div>

      <ul className="nav-links">
        <li>
          <Link to="/home">Home</Link>
        </li>
        <li>
          <Link to="/dashboard">Dashboard</Link>
        </li>
        <li>
          <Link to="/datasets">Datasets</Link>
        </li>
        {token && (
          <li>
            <button
              onClick={handleLogout}
              style={{
                padding: "6px 16px",
                backgroundColor: "#dc3545",
                color: "white",
                border: "none",
                borderRadius: "6px",
                cursor: "pointer",
              }}
            >
              Logout
            </button>
          </li>
        )}
      </ul>
    </nav>
  );
};

export default Navbar;
