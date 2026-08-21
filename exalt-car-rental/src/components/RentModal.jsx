import React, { useState, useEffect } from "react";
import { Modal, Form, Button, Alert } from "react-bootstrap";
import { useAuth } from "../context/AuthContext";
import { createRental, updateCarDetails } from "../services/firebaseConfig";

export const RentModal = ({ car, show, onHide }) => {
  const { currentUser } = useAuth();
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [totalPrice, setTotalPrice] = useState(0);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  // Timezone-safe date utility calculation engine
  useEffect(() => {
    if (startDate && endDate && car?.costPerDay) {
      // Split strings to parse calendar dates locally rather than defaulting to UTC midnight
      const [sYear, sMonth, sDay] = startDate.split("-").map(Number);
      const [eYear, eMonth, eDay] = endDate.split("-").map(Number);

      const startLocal = new Date(sYear, sMonth - 1, sDay);
      const endLocal = new Date(eYear, eMonth - 1, eDay);

      const diffTime = endLocal - startLocal;
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

      if (diffDays > 0) {
        setTotalPrice(diffDays * car.costPerDay);
        setError("");
      } else {
        setTotalPrice(0);
        setError("End date must be selected after the start date.");
      }
    } else {
      setTotalPrice(0);
    }
  }, [startDate, endDate, car?.costPerDay]);

  // Clean form state hooks on close toggle operations
  useEffect(() => {
    if (!show) {
      setStartDate("");
      setEndDate("");
      setTotalPrice(0);
      setError("");
      setSubmitting(false);
    }
  }, [show]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (totalPrice <= 0) return;

    setSubmitting(true);
    setError("");

    try {
      const rentalPayload = {
        carId: car.id,
        carName: car.name, // 👍 New: Stores the friendly name directly in the rental ledger
        assetSku: `EXALT-${car.name.substring(0, 3).toUpperCase()}-${Math.floor(1000 + Math.random() * 9000)}`, // 👍 New: Generates an enterprise-grade tracking SKU
        userId: currentUser?.uid || "guest_testing_account",
        period: `${startDate} - ${endDate}`,
        totalPrice: totalPrice,
        createdAt: new Date().toISOString()
      };

      // Write reservation data to database store
      await createRental(rentalPayload);

      // Update vehicle global state status to reserved
      await updateCarDetails(car.id, { available: false });

      onHide();
    } catch (err) {
      setError("Failed to execute booking. Please check database permissions.");
    } finally {
      setSubmitting(false);
    }
  };

  // Get current local system date string structure to restrict historical selections
  const todayString = new Date().toISOString().split("T")[0];

  return (
    <Modal
      show={show}
      onHide={onHide}
      centered
      data-test="rent-modal"
      contentClassName="border-0 text-white"
      style={{ backdropFilter: "blur(8px)" }}
    >
      <div
        style={{
          background: "#1E293B",
          borderRadius: "20px",
          border: "1px solid rgba(255, 255, 255, 0.1)",
          overflow: "hidden"
        }}
      >
        <Modal.Header closeButton closeVariant="white" className="border-secondary border-opacity-25 px-4 pt-4">
          <Modal.Title className="fw-bold fs-4" data-test="rent-modal-title">
            Reserve {car?.name}
          </Modal.Title>
        </Modal.Header>

        <Form onSubmit={handleSubmit}>
          <Modal.Body className="p-4">
            {error && (
              <Alert variant="danger" data-test="rent-modal-error" className="py-2 small text-center">
                {error}
              </Alert>
            )}

            <Form.Group className="mb-3" controlId="startDate">
              <Form.Label className="small text-secondary fw-semibold">START DATE</Form.Label>
              <Form.Control
                type="date"
                required
                min={todayString}
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
                data-test="input-start-date"
                className="bg-dark text-white border-secondary border-opacity-50 py-2"
                style={{ borderRadius: "10px" }}
              />
            </Form.Group>

            <Form.Group className="mb-4" controlId="endDate">
              <Form.Label className="small text-secondary fw-semibold">END DATE</Form.Label>
              <Form.Control
                type="date"
                required
                min={startDate || todayString}
                value={endDate}
                onChange={(e) => setEndDate(e.target.value)}
                data-test="input-end-date"
                className="bg-dark text-white border-secondary border-opacity-50 py-2"
                style={{ borderRadius: "10px" }}
              />
            </Form.Group>

            <div
              className="p-3 rounded-3 text-center my-3"
              style={{ background: "rgba(15, 23, 42, 0.6)", border: "1px solid rgba(255, 255, 255, 0.05)" }}
            >
              <span className="text-secondary small d-block mb-1">Total Cost Summary</span>
              <h3 className="fw-bold text-info m-0" data-test="total-price-display">
                ${totalPrice}
              </h3>
            </div>
          </Modal.Body>

          <Modal.Footer className="border-secondary border-opacity-25 px-4 pb-4">
            <Button
              variant="outline-secondary"
              onClick={onHide}
              data-test="button-cancel-rent"
              className="px-4 text-white border-opacity-50"
              style={{ borderRadius: "10px" }}
            >
              Cancel
            </Button>
            <Button
              type="submit"
              disabled={submitting || totalPrice <= 0}
              data-test="button-confirm-rent"
              variant="info"
              className="px-4 text-white fw-semibold"
              style={{
                borderRadius: "10px",
                background: "linear-gradient(135deg, #0284C7 0%, #38BDF8 100%)",
                border: "none",
              }}
            >
              {submitting ? "Processing..." : "Confirm Rental"}
            </Button>
          </Modal.Footer>
        </Form>
      </div>
    </Modal>
  );
};