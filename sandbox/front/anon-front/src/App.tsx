import { Navigate, Outlet } from "react-router-dom";

function PrivateRoute() {
  const token = localStorage.getItem("token");
  return token ? <Outlet /> : <Navigate to="/login" replace />;
}

import { BrowserRouter, Routes, Route } from "react-router-dom";
import Login from "./pages/login";
import NotFoundPage from "./pages/notfound";
import Register from "./pages/register";
import Home from "./pages/home";
import Datasets from "./pages/datasets";
import Upload from "./pages/upload";
import Grant from "./pages/grant";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        {/* chronione */}
        <Route element={<PrivateRoute />}>
            <Route path="/" element={<Home />} />
            <Route path="/home" element={<Home />} />
            <Route path="/datasets" element={<Datasets />}/>
            <Route path="/upload" element={<Upload />}/>
            <Route path="/grant" element={<Grant />}/>
        </Route>
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </BrowserRouter>
  );
}


