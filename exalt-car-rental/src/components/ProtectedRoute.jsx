import React from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { Spinner, Container } from "react-bootstrap";

export const ProtectedRoute = ({ children, adminOnly = false }) => {
  const { currentUser, loading, isAdmin } = useAuth();

  // Show a clean loading spinner while Firebase checks the authentication state
  if (loading) {
    return (
      <Container className="d-flex justify-content-center align-items-center min-vh-100">
        <Spinner animation="border" variant="info" />
      </Container>
    );
  }

  // Intercept unauthenticated traffic and bounce them to the login hub
  if (!currentUser) {
    return <Navigate to="/login" replace />;
  }

  // Intercept unauthorized users trying to access strict admin routing paths
  if (adminOnly && !isAdmin) {
    return (
      <Container className="d-flex align-items-center justify-content-center" style={{ minHeight: "80vh" }}>
        <div 
          className="text-center p-5 rounded-4 text-white" 
          style={{ background: "rgba(239, 68, 68, 0.1)", border: "1px solid rgba(239, 68, 68, 0.2)" }}
        >
          <h2 className="fw-bold text-danger mb-2" data-test="access-denied-header">Access Denied</h2>
          <p className="text-secondary m-0">You do not possess the administrator credentials required to access this suite.</p>
        </div>
      </Container>
    );
  }

  // Allow safe entry into the protected views
  return children;
};