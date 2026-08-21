import React from "react";
import { Navbar, Container } from "react-bootstrap";
import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { NavLinks } from "./Navigation/NavLinks";
import { AuthActionBtn } from "./Navigation/AuthActionBtn";

export const Navigation = () => {
  const { currentUser, isAdmin, logoutUser } = useAuth();

  return (
    <Navbar 
      expand="lg" 
      variant="dark"
      className="py-3 sticky-top"
      style={{ 
        background: "rgba(15, 23, 42, 0.7)", 
        backdropFilter: "blur(16px)",
        borderBottom: "1px solid rgba(255, 255, 255, 0.05)"
      }}
    >
      <Container>
        {/* Modern Corporate Branding Element Logo */}
        <Navbar.Brand as={Link} to="/" className="fw-bold fs-3 text-white tracking-tight me-4">
          EXALT<span className="text-info">.</span>
        </Navbar.Brand>

        <Navbar.Toggle aria-controls="exalt-navbar-nav" className="border-0 bg-dark bg-opacity-20" />
        
        <Navbar.Collapse id="exalt-navbar-nav" className="gap-3 mt-3 mt-lg-0">
          <NavLinks currentUser={currentUser} isAdmin={isAdmin} />
          <AuthActionBtn currentUser={currentUser} logoutUser={logoutUser} />
        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};
