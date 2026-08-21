import React, { useState, useEffect } from "react";
import { Container, Spinner, Alert } from "react-bootstrap";
import { useAuth } from "../context/AuthContext";
import { getUserRentals } from "../services/firebaseConfig";
import { RentalTable } from "../components/History/RentalTable";

export const History = () => {
  const { currentUser } = useAuth();
  const [rentals, setRentals] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchHistory = async () => {
      if (!currentUser?.uid) return;
      try {
        const data = await getUserRentals(currentUser.uid);
        setRentals(data);
      } catch (err) {
        setError("Failed to fetch personal transaction logs.");
      } finally {
        setLoading(false);
      }
    };
    fetchHistory();
  }, [currentUser]);

  return (
    <div style={{ backgroundColor: "#0F172A", minHeight: "90vh" }} className="py-5 text-white">
      <Container>
        <header className="mb-4 text-center text-md-start">
          <h2 className="fw-bold" data-test="history-header">My Rentals Ledger</h2>
          <p className="text-secondary small">Review past verified rental requests and payment processing statements.</p>
        </header>

        {error && <Alert variant="danger" data-test="history-error">{error}</Alert>}

        {loading ? (
          <div className="text-center py-5" data-test="history-spinner">
            <Spinner animation="border" variant="info" />
          </div>
        ) : (
          <RentalTable rentals={rentals} />
        )}
      </Container>
    </div>
  );
};