import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/navbar";

interface UserData {
  firstName: string;
  lastName: string;
  trustLevel: number;
}

const trustLevelNames = ["Novice", "Learner", "Contributor", "Trusted", "Admin"];

const Grant: React.FC = () => {
  return (
    <>
        <Navbar />
        Hello
    </>
  );
};

export default Grant;