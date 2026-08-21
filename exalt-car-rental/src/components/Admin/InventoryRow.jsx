import React, { useState } from "react";
import { Button, Badge } from "react-bootstrap";
import { EditCarModal } from "./EditCarModal";

export const InventoryRow = ({ car, onDelete }) => {
  const [showEditModal, setShowEditModal] = useState(false);
  const isAvailable = car.available !== false;

  return (
    <div 
      className="p-3 d-flex flex-column flex-md-row align-items-md-center justify-content-between gap-3 rounded-3"
      style={{ background: "rgba(30, 41, 59, 0.5)", border: "1px solid rgba(255, 255, 255, 0.06)" }}
      data-test={`inventory-item-${car.id}`}
    >
      <div className="d-flex align-items-center gap-3">
        <img 
          src={car.image} 
          alt="" 
          style={{ width: "60px", height: "40px", objectFit: "cover", borderRadius: "6px" }} 
        />
        <div>
          <h5 className="m-0 fw-bold fs-6" data-test="inventory-car-name">{car.name}</h5>
          <span className="text-info small fw-semibold">${car.costPerDay}/day</span>
        </div>
      </div>

      <div className="d-flex align-items-center gap-3 justify-content-end">
        <Badge bg={isAvailable ? "success" : "secondary"} className="px-2 py-1.5 small">
          {isAvailable ? "Active Catalog" : "Reserved Ledger"}
        </Badge>
        
        <Button 
          variant="outline-light" 
          size="sm" 
          data-test={`edit-asset-${car.id}`}
          onClick={() => setShowEditModal(true)}
          className="border-secondary border-opacity-50 text-opacity-75 small"
        >
          Modify
        </Button>
        
        <Button 
          variant="outline-danger" 
          size="sm" 
          data-test={`delete-asset-${car.id}`}
          onClick={() => onDelete(car.id)}
          className="border-danger border-opacity-25 small"
        >
          Wipe
        </Button>
      </div>

      <EditCarModal car={car} show={showEditModal} onHide={() => setShowEditModal(false)} />
    </div>
  );
};