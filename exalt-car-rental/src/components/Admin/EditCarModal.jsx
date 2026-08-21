import React, { useState } from "react";
import { Modal, Form, Button, Alert } from "react-bootstrap";
import { updateCarDetails } from "../../services/firebaseConfig";

export const EditCarModal = ({ car, show, onHide }) => {
  const [name, setName] = useState(car.name);
  const [cost, setCost] = useState(car.costPerDay);
  const [image, setImage] = useState(car.image);
  const [posting, setPosting] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setPosting(true);
    try {
      await updateCarDetails(car.id, {
        name,
        costPerDay: Number(cost),
        image,
        // available: true
      });
      onHide();
    } catch (err) {
      setError("Failed to execute database asset mutation patch.");
    } finally {
      setPosting(false);
    }
  };

  return (
    <Modal show={show} onHide={onHide} centered data-test={`edit-modal-${car.id}`}>
      <div style={{ background: "#1E293B", color: "#fff", borderRadius: "16px", border: "1px solid rgba(255,255,255,0.1)" }}>
        <Modal.Header closeButton closeVariant="white" className="border-secondary border-opacity-25">
          <Modal.Title className="fw-bold fs-5">Modify Specifications</Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleSubmit}>
          <Modal.Body className="small">
            {error && <Alert variant="danger" className="py-2 small text-center">{error}</Alert>}
            <Form.Group className="mb-2">
              <Form.Label className="text-secondary fw-semibold">VEHICLE MODEL NAME</Form.Label>
              <Form.Control type="text" required value={name} onChange={e => setName(e.target.value)} data-test="edit-input-name" className="bg-dark text-white border-secondary" />
            </Form.Group>
            <Form.Group className="mb-2">
              <Form.Label className="text-secondary fw-semibold">COST PER EXALT DAY ($)</Form.Label>
              <Form.Control type="number" required value={cost} onChange={e => setCost(e.target.value)} data-test="edit-input-cost" className="bg-dark text-white border-secondary" />
            </Form.Group>
            <Form.Group className="mb-2">
              <Form.Label className="text-secondary fw-semibold">DIRECT IMAGE PATH URL</Form.Label>
              <Form.Control type="url" required value={image} onChange={e => setImage(e.target.value)} data-test="edit-input-image" className="bg-dark text-white border-secondary" />
            </Form.Group>
          </Modal.Body>
          <Modal.Footer className="border-secondary border-opacity-25">
            <Button variant="outline-secondary" onClick={onHide} size="sm" className="text-white">Cancel</Button>
            <Button type="submit" disabled={posting} variant="info" size="sm" data-test="edit-submit-btn" className="text-white">
              {posting ? "Saving Changes..." : "Apply Matrix Changes"}
            </Button>
          </Modal.Footer>
        </Form>
      </div>
    </Modal>
  );
};