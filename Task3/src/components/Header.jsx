import React from "react";
import { useLocation, useNavigate } from "react-router-dom";

export default function Header() {
  const location = useLocation();
  const navigate = useNavigate();

  // Determine the header title based on current URL path and state
  let title = "Events";
  if (location.pathname === "/form") {
    if (location.state?.eventToEdit) {
      title = "Edit Event";
    } else {
      title = "Create Event";
    }
  }

  return (
    <header className="header">
      <h1>{title}</h1>
      {/* Show '+New' button ONLY when we are on the main List page */}
      {location.pathname === "/" && (
        <button 
          className="btn-primary" 
          onClick={() => navigate("/form")}
        >
          + New
        </button>
      )}
    </header>
  );
}