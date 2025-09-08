import React, { useEffect, useState } from "react";
import Navbar from "../components/navbar";
import "./grant.css";

interface UserData {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  trustLevel: number;
}

const trustLevelNames = ["Novice", "Learner", "Contributor", "Trusted", "Admin"];

const Grant: React.FC = () => {
  const [users, setUsers] = useState<UserData[]>([]);
  const [loading, setLoading] = useState(true);
  const [editedLevels, setEditedLevels] = useState<Record<number, number>>({});

  const token = localStorage.getItem("token");

  const currentUserId = token
    ? JSON.parse(atob(token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/"))).userId
    : 0;

  useEffect(() => {
    fetch("http://localhost:8080/users", {
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    })
      .then(res => res.json())
      .then((data: UserData[]) => setUsers(data))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  }, [token]);

  const handleLevelChange = (id: number, newLevel: number) => {
    setEditedLevels(prev => ({ ...prev, [id]: newLevel }));
  };

  const handleUpdate = (id: number) => {
    const newLevel = editedLevels[id];
    if (!newLevel) return;

    fetch(`http://localhost:8080/users/${id}/trust`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({ trustLevel: newLevel }),
    })
      .then(res => {
        if (!res.ok) throw new Error("Failed to update trust level");
        setUsers(prev => prev.map(u => u.id === id ? { ...u, trustLevel: newLevel } : u));
        setEditedLevels(prev => {
          const copy = { ...prev };
          delete copy[id];
          return copy;
        });
      })
      .catch(err => console.error(err));
  };

  return (
    <>
      <Navbar />
      <div className="home-container">
        <div className="cards-container">
          {loading ? (
            <p>Loading users…</p>
          ) : (
            users.map(user => (
              <div key={user.id} className="card">
                <h2>{user.firstName} {user.lastName}</h2>
                <p>Email: {user.email}</p>
                <p>Current Trust: {trustLevelNames[user.trustLevel - 1]}</p>
                <p>
                  Change Trust Level:{" "}
                  <select
                    value={editedLevels[user.id] ?? ""}
                    onChange={e => handleLevelChange(user.id, Number(e.target.value))}
                  >
                    <option value="" disabled>Select level</option>
                    {trustLevelNames.slice(0, 4).map((name, index) => (
                      <option key={index} value={index + 1}>{name}</option>
                    ))}
                  </select>
                  {editedLevels[user.id] !== undefined && (
                    <button
                      onClick={() => handleUpdate(user.id)}
                      style={{
                        marginLeft: "10px",
                        padding: "4px 10px",
                        borderRadius: "6px",
                        border: "none",
                        backgroundColor: "#007bff",
                        color: "white",
                        cursor: "pointer",
                      }}
                    >
                      Update
                    </button>
                  )}
                </p>
              </div>
            ))
          )}
        </div>
      </div>
    </>
  );
};

export default Grant;
