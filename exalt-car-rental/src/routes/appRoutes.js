import { Navigate } from "react-router-dom";
import { ProtectedRoute } from "../components/ProtectedRoute";
import { Login } from "../pages/Login";
import { MainLayout } from "../pages/MainLayout";
import { History } from "../pages/History";
import { AdminDashboard } from "../pages/AdminDashboard";

export const appRoutes = [
  {
    path: "/login",
    element: Login,
    isPublic: true // Public: Will never render the Navigation bar
  },
  {
    path: "/",
    element: MainLayout,
    protected: true // Protected: Requires authentication and renders the Navigation bar
  },
  {
    path: "/history",
    element: History,
    protected: true
  },
  {
    path: "/admin",
    element: AdminDashboard,
    protected: true,
    adminOnly: true
  },
  {
    path: "*",
    element: Navigate,
    isPublic: true,
    fallbackRedirect: true
  }
];