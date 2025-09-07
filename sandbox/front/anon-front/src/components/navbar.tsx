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

  //JWT decode function does not work :((
  function getTrustLevel(token: string | null): number {
  if (!token) return 0;
  try {
    const payload = token.split('.')[1];
    const decoded = JSON.parse(atob(payload.replace(/-/g, '+').replace(/_/g, '/')));
    return decoded.trustLevel ?? 0; // <-- zmienione na trustLevel
  } catch {
    return 0;
  }
}

  const trustLevel = getTrustLevel(token);

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
          <Link to="/datasets">Datasets</Link>
        </li>
        {trustLevel === 5 && (
          <>
            <li>
              <Link to="/upload">Upload</Link>
            </li>
            <li>
              <Link to="/grant">Grant</Link>
            </li>
          </>
        )}
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
