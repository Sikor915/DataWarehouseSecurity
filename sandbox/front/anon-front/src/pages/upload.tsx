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

const Upload: React.FC = () => {
  return (
    <>
        <Navbar />
        Hello
    </>
  );
};

export default Upload;