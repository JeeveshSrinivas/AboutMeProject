import React from "react";
import { Col, Card, Badge, Button } from "react-bootstrap";

export const CarCard = ({ car, onBookClick }) => {
  if (!car) return null;
  const isAvailable = car.available !== false;

  return (
    <Col xs={12} md={6} lg={4}>
      <Card
        data-test={`car-card-${car.id}`}
        className="h-100 border-0 text-white overflow-hidden"
        style={{
          background: "rgba(30, 41, 59, 0.6)",
          backdropFilter: "blur(12px)",
          WebkitBackdropFilter: "blur(12px)",
          borderRadius: "20px",
          border: "1px solid rgba(255, 255, 255, 0.08)",
          filter: isAvailable ? "none" : "grayscale(100%)",
          opacity: isAvailable ? 1 : 0.6,
        }}
      >
        <div className="position-relative" style={{ height: "220px", overflow: "hidden" }}>
          <Card.Img
            variant="top"
            src={car.image || "https://unsplash.com"}
            alt={car.name}
            style={{ height: "100%", objectFit: "cover" }}
            data-test="car-image"
          />
          <Badge
            bg={isAvailable ? "success" : "secondary"}
            data-test="car-availability-badge"
            className="position-absolute top-0 end-0 m-3 px-3 py-2"
            style={{ borderRadius: "10px" }}
          >
            {isAvailable ? "Available" : "Reserved"}
          </Badge>
        </div>

        <Card.Body className="d-flex flex-column p-4">
          <Card.Title className="fs-4 fw-bold mb-2" data-test="car-name">
            {car.name}
          </Card.Title>
          
          <Card.Text className="text-secondary small mb-4 flex-grow-1" data-test="car-description">
            {car.description || "High-performance luxury driving experience with premium comfort options."}
          </Card.Text>

          <div className="d-flex align-items-center justify-content-between pt-3 border-top border-secondary border-opacity-25">
            <div>
              <span className="fs-3 fw-bold text-info" data-test="car-price">
                ${car.costPerDay}
              </span>
              <span className="text-secondary small"> / day</span>
            </div>

            <Button
              disabled={!isAvailable}
              data-test={`button-book-${car.id}`}
              onClick={() => onBookClick(car)}
              variant="info"
              className="px-4 py-2 text-white fw-semibold"
              style={{
                borderRadius: "12px",
                background: isAvailable
                  ? "linear-gradient(135deg, #0284C7 0%, #38BDF8 100%)"
                  : "rgba(100, 116, 139, 0.5)",
                border: "none",
              }}
            >
              {isAvailable ? "Reserve" : "Unavailable"}
            </Button>
          </div>
        </Card.Body>
      </Card>
    </Col>
  );
};