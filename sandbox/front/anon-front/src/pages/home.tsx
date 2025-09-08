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
              The project aims to create a secure platform for automated data sharing, focused on protecting privacy 
              while enabling authorized access to valuable datasets. It leverages advanced anonymization techniques,
              including the ARX library, to ensure that sensitive medical information or otherwise cannot be traced 
              back to individuals. Users with different trust levels can access data according to their authorization, 
              making the system safe, flexible, and suitable for a wide range of applications.
            </p>
          </div>

          <div className="card">
            <h2>Features</h2>
            <ul>
              <li>Access to anonymized data depending on your trust level</li>
              <li>Automatic anonymization of sensitive information</li>
              <li>Controlled sharing of patient data based on trust and permissions</li>
              <li>Automatic anonymization of sensitive information using the ARX library especially based statistic anonymization</li>
            </ul>
          </div>

          <div className="card">
            <h2>Next Steps</h2>
            <p>
              <li>Integrate real-time notifications for data updates</li>
              <li>Enhance data anonymization policies with configurable settings</li>
              <li>Develop user-friendly dashboards for visualizing anonymized data</li>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Home;