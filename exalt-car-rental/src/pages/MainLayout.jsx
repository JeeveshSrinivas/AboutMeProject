import React, { useState, useEffect } from "react";
import { Container, Row, Spinner } from "react-bootstrap";
import { useAuth } from "../context/AuthContext";
import { subscribeToCars } from "../services/firebaseConfig";
import { RentModal } from "../components/RentModal";
import { CarCard } from "../components/Catalog/CarCard";
import { AdminFAB } from "../components/Catalog/AdminFAB";

export const MainLayout = () => {
  const [cars, setCars] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedCar, setSelectedCar] = useState(null);
  const [showModal, setShowModal] = useState(false);

  const { isAdmin } = useAuth();

  useEffect(() => {
    setLoading(true);
    const unsubscribe = subscribeToCars((updatedCars) => {
      setCars(updatedCars);
      setLoading(false);
    });
    return () => unsubscribe();
  }, []);

  const handleBookClick = (car) => {
    setSelectedCar(car);
    setShowModal(true);
  };

  return (
    <div style={{ backgroundColor: "#0F172A", minHeight: "100vh" }} className="py-5 text-white">
      <Container>
        <header className="mb-5 text-center">
          <h1 className="fw-bold display-5 mb-2" data-test="catalog-header">Fleet Catalog</h1>
          <p className="text-secondary fs-5">Select a luxury vehicle to initiate your reservation</p>
        </header>

        {loading ? (
          <div className="text-center py-5" data-test="catalog-spinner">
            <Spinner animation="border" variant="info" />
          </div>
        ) : (
          <Row className="g-4" data-test="car-grid">
            {Array.isArray(cars) && cars.map((car) => (
              <CarCard key={car.id} car={car} onBookClick={handleBookClick} />
            ))}
          </Row>
        )}

        <AdminFAB show={isAdmin} />

        {selectedCar && (
          <RentModal car={selectedCar} show={showModal} onHide={() => setShowModal(false)} />
        )}
      </Container>
    </div>
  );
};