import React, { useState } from "react";
import { Modal, Form, Button, Alert } from "react-bootstrap";
import { addNewCar } from "../../services/firebaseConfig";

export const AddCarModal = ({ show, onHide }) => {
  const [name, setName] = useState("");
  const [cost, setCost] = useState("");
  const [image, setImage] = useState("");
  const [desc, setDesc] = useState("");
  const [posting, setPosting] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    setPosting(true);
    setError("");
    try {
      await addNewCar({
        name,
        costPerDay: Number(cost),
        image,
        description: desc,
        available: true
      });
      setName(""); setCost(""); setImage(""); setDesc("");
      onHide();
    } catch (err) {
      setError("Failed to create new catalog asset entry.");
    } finally {
      setPosting(false);
    }
  };

  return (
    <Modal show={show} onHide={onHide} centered data-test="add-asset-modal">
      <div style={{ background: "#1E293B", color: "#fff", borderRadius: "16px", border: "1px solid rgba(255,255,255,0.1)" }}>
        <Modal.Header closeButton closeVariant="white" className="border-secondary border-opacity-25">
          <Modal.Title className="fw-bold fs-5">Provision New Fleet Asset</Modal.Title>
        </Modal.Header>
        <Form onSubmit={handleSubmit}>
          <Modal.Body className="small">
            {error && <Alert variant="danger" className="py-2 small text-center">{error}</Alert>}
            <Form.Group className="mb-2">
              <Form.Label className="text-secondary fw-semibold">VEHICLE MODEL NAME</Form.Label>
              <Form.Control type="text" required value={name} onChange={e => setName(e.target.value)} data-test="add-input-name" className="bg-dark text-white border-secondary" />
            </Form.Group>
            <Form.Group className="mb-2">
              <Form.Label className="text-secondary fw-semibold">COST PER EXALT DAY ($)</Form.Label>
              <Form.Control type="number" required value={cost} onChange={e => setCost(e.target.value)} data-test="add-input-cost" className="bg-dark text-white border-secondary" />
            </Form.Group>
            <Form.Group className="mb-2">
              <Form.Label className="text-secondary fw-semibold">DIRECT IMAGE PATH URL</Form.Label>
              <Form.Control type="url" required value={image} onChange={e => setImage(e.target.value)} data-test="add-input-image" className="bg-dark text-white border-secondary" />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label className="text-secondary fw-semibold">VEHICLE DESCRIPTION SPECIFICATIONS</Form.Label>
              <Form.Control as="textarea" rows={2} required value={desc} onChange={e => setDesc(e.target.value)} data-test="add-input-desc" className="bg-dark text-white border-secondary" />
            </Form.Group>
          </Modal.Body>
          <Modal.Footer className="border-secondary border-opacity-25">
            <Button variant="outline-secondary" onClick={onHide} size="sm" className="text-white">Cancel</Button>
            <Button type="submit" disabled={posting} variant="primary" size="sm" data-test="add-submit-btn">
              {posting ? "Provisioning..." : "Publish Asset"}
            </Button>
          </Modal.Footer>
        </Form>
      </div>
    </Modal>
  );
};