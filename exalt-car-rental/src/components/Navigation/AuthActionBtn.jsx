import React, { useState } from "react";
import { Button } from "react-bootstrap";
import { useNavigate } from "react-router-dom";

export const AuthActionBtn = ({ currentUser, logoutUser }) => {
  const navigate = useNavigate();
  const [isHovered, setIsHovered] = useState(false);

  const handleAuthClick = async () => {
    if (currentUser) {
      try {
        await logoutUser();
        navigate("/login");
      } catch (error) {
        console.error("Failed to execute logout sequence:", error);
      }
    } else {
      navigate("/login");
    }
  };

  const loginStyle = {
    background: isHovered 
      ? "linear-gradient(135deg, #0369A1 0%, #0284C7 100%)" 
      : "linear-gradient(135deg, #0284C7 0%, #38BDF8 100%)",
    border: "none",
    transform: isHovered ? "translateY(-1px)" : "none",
  };

  const logoutStyle = {
    background: isHovered ? "rgba(255, 255, 255, 0.1)" : "rgba(255, 255, 255, 0.04)",
    border: "1px solid rgba(255, 255, 255, 0.2)",
    transform: isHovered ? "translateY(-1px)" : "none",
  };

  return (
    <div className="d-flex flex-column flex-lg-row align-items-lg-center gap-3 w-100 justify-content-end">
      {/* 👍 Fix 2: Renders the active user identity email string cleanly when authenticated */}
      {currentUser?.email && (
        <span className="text-secondary small font-monospace d-block text-lg-end" style={{ letterSpacing: "0.3px" }}>
          Logged as: <span className="text-info fw-semibold">{currentUser.email}</span>
        </span>
      )}
      
      <Button
        variant="info"
        onClick={handleAuthClick}
        data-test="navbar-auth-btn"
        onMouseEnter={() => setIsHovered(true)}
        onMouseLeave={() => setIsHovered(false)}
        className="fw-semibold px-4 py-2 text-white"
        style={{
          borderRadius: "12px",
          color: currentUser ? "#F1F5F9" : "#fff",
          transition: "all 0.2s cubic-bezier(0.4, 0, 0.2, 1)",
          ...(currentUser ? logoutStyle : loginStyle)
        }}
      >
        {currentUser ? "Sign Out" : "Log In"}
      </Button>
    </div>
  );
};