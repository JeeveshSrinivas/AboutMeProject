import React from "react";
import { Nav } from "react-bootstrap";
import { Link, useLocation } from "react-router-dom";

export const NavLinks = ({ currentUser, isAdmin }) => {
  const location = useLocation();

  // If there is no active authenticated user session, hide the menu options entirely
  if (!currentUser) return null;
  const navLinkClass = "px-3 py-2 rounded-3 text-white fw-semibold link-light text-opacity-75 text-opacity-100-hover transition-all text-nowrap";

  return (
    <Nav className="me-auto gap-2 align-items-center">
      <Nav.Link
        as={Link}
        to="/"
        active={location.pathname === "/"}
        className={navLinkClass}
      >
        Fleet Catalog
      </Nav.Link>
      
      <Nav.Link
        as={Link}
        to="/history"
        active={location.pathname === "/history"}
        className={navLinkClass}
      >
        My Rentals
      </Nav.Link>

      {/* Render the strict administrative management link exclusively for authorized admin sessions */}
      {isAdmin && (
        <Nav.Link
          as={Link}
          to="/admin"
          active={location.pathname === "/admin"}
          className={"px-3 py-2 rounded-3 text-danger border border-danger border-opacity-25 transition-all fw-semibold text-nowrap"}
          style={{ background: "rgba(239, 68, 68, 0.05)" }}
        >
          Admin Suite
        </Nav.Link>
      )}
    </Nav>
  );
};