import React from "react";
import { Button } from "react-bootstrap";
import { Link } from "react-router-dom";

export const AdminFAB = ({ show }) => {
  if (!show) return null;

  return (
    <Button
      data-test="admin-add-car-fab"
      as={Link}
      to="/admin"
      className="position-fixed bottom-0 end-0 m-4 m-md-5 rounded-circle d-flex align-items-center justify-content-center shadow-lg border-0 text-decoration-none"
      style={{
        width: "64px",
        height: "64px",
        backgroundColor: "#2563EB",
        zIndex: 1000,
        boxShadow: "0 10px 25px rgba(37, 99, 235, 0.5)",
      }}
    >
      <span className="fs-2 text-white font-weight-bold" style={{ marginTop: "-4px" }}>+</span>
    </Button>
  );
};