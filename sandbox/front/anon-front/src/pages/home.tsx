import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/navbar";
import "./home.css";

interface UserData {
  firstName: string;
  lastName: string;
  trustLevel: number;
}

const trustLevelNames = ["Novice", "Learner", "Contributor", "Trusted", "Admin"];

const Home: React.FC = () => {
  const [user, setUser] = useState<UserData | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchUser = async () => {
      const token = localStorage.getItem("token");
      if (!token) {
        navigate("/login");
        return;
      }

      try {
        const response = await fetch("http://localhost:8080/me", {
          headers: {
            "Authorization": `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          localStorage.removeItem("token");
          navigate("/login");
          return;
        }

        const data: UserData = await response.json();
        setUser(data);
      } catch (error) {
        console.error("Błąd podczas pobierania danych:", error);
        localStorage.removeItem("token");
        navigate("/login");
      }
    };

    fetchUser();
  }, [navigate]);

  return (
    <div>
      <Navbar />
      <div className="home-container">
        {user && (
          <>
            <h1>
              Welcome {user.firstName} {user.lastName}!
            </h1>
            <p>Your trust level: {trustLevelNames[user.trustLevel - 1]}</p>
          </>
        )}

        <div className="cards-container">
          <div className="card">
            <h2>About Project</h2>
            <p>
              This project aims to automate data sharing for epilepsy patients
              using anonymization algorithms, ensuring privacy and safe data usage.
            </p>
          </div>

          <div className="card">
            <h2>Features</h2>
            <ul>
              <li>User registration and login</li>
              <li>Password hashing with BCrypt</li>
              <li>JWT-based authentication</li>
              <li>Frontend integration with React</li>
              <li>Data anonymization and controlled access</li>
            </ul>
          </div>

          <div className="card">
            <h2>Next Steps</h2>
            <p>
              Extend the project by adding email verification, role-based access,
              and refreshing JWT tokens to enhance security and UX.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Home;