import React, { useState, useEffect } from "react";
import { Container, Button, Row, Col, Spinner, Alert } from "react-bootstrap";
import { subscribeToCars, deleteCarDoc } from "../services/firebaseConfig";
import { InventoryRow } from "../components/Admin/InventoryRow";
import { AddCarModal } from "../components/Admin/AddCarModal";

export const AdminDashboard = () => {
  const [cars, setCars] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [showAddModal, setShowAddModal] = useState(false);

  useEffect(() => {
    const unsubscribe = subscribeToCars((data) => {
      setCars(data);
      setLoading(false);
    });
    return () => unsubscribe();
  }, []);

  const handleDelete = async (id) => {
    if (!window.confirm("Are you entirely sure you want to scrub this vehicle asset?")) return;
    try {
      await deleteCarDoc(id);
    } catch (err) {
      setError("Failed to execute database document deletion request.");
    }
  };

  return (
    <div style={{ backgroundColor: "#0F172A", minHeight: "90vh" }} className="py-5 text-white">
      <Container>
        <header className="d-flex flex-column flex-sm-row align-items-sm-center justify-content-between mb-5 gap-3">
          <div>
            <h2 className="fw-bold" data-test="admin-header">Fleet Configuration Management</h2>
            <p className="text-secondary small m-0">Add, edit, modify pricing matrices, or wipe assets from the global rental manifest.</p>
          </div>
          <Button 
            variant="primary" 
            data-test="admin-create-asset-btn"
            onClick={() => setShowAddModal(true)}
            className="fw-semibold px-4 py-2"
            style={{ borderRadius: "10px", background: "linear-gradient(135deg, #2563EB 0%, #1D4ED8 100%)", border: "none" }}
          >
            + Provision New Asset
          </Button>
        </header>

        {error && <Alert variant="danger">{error}</Alert>}

        {loading ? (
          <div className="text-center py-5"><Spinner animation="border" variant="primary" /></div>
        ) : (
          <Row className="g-3" data-test="admin-inventory-list">
            {cars.map((car) => (
              <Col xs={12} key={car.id}>
                <InventoryRow car={car} onDelete={handleDelete} />
              </Col>
            ))}
          </Row>
        )}

        <AddCarModal show={showAddModal} onHide={() => setShowAddModal(false)} />
      </Container>
    </div>
  );
};