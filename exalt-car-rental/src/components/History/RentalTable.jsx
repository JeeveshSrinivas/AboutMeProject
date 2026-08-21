import React from "react";
import { Table, Badge } from "react-bootstrap";
import { parsePeriodDates } from "../../utils/dateFormatter";

export const RentalTable = ({ rentals }) => {
  if (!rentals || rentals.length === 0) {
    return (
      <div className="text-center p-5 rounded-4" style={{ background: "rgba(30, 41, 59, 0.4)", border: "1px solid rgba(255, 255, 255, 0.05)" }}>
        <p className="text-secondary m-0" data-test="empty-history-message">No verified reservations found under this account identity.</p>
      </div>
    );
  }

  return (
    <div className="table-responsive rounded-4 shadow-lg" style={{ border: "1px solid rgba(255, 255, 255, 0.08)", overflow: "hidden" }}>
      <Table variant="dark" hover className="m-0 align-middle" style={{ background: "rgba(30, 41, 59, 0.6)", backdropFilter: "blur(12px)" }}>
        <thead className="text-secondary small" style={{ background: "rgba(15, 23, 42, 0.8)" }}>
          <tr>
            <th className="p-3 border-0">TRANSACTION ID</th>
            <th className="p-3 border-0">VEHICLE ASSET</th>
            <th className="p-3 border-0">FLEET SKU CODE</th>
            <th className="p-3 border-0">DURATION PERIOD</th>
            <th className="p-3 border-0 text-end">TOTAL BILLED</th>
          </tr>
        </thead>
        <tbody className="border-0">
          {rentals.map((item) => {
            const { start, end } = parsePeriodDates(item.period);
            return (
              <tr key={item.id} data-test={`history-row-${item.id}`} style={{ borderBottom: "1px solid rgba(255, 255, 255, 0.05)" }}>
                {/* 1. Transaction short hash */}
                <td className="p-3 font-monospace text-info small" style={{ fontSize: "11px" }}>
                  {item.id.substring(0, 8).toUpperCase()}...
                </td>
                
                {/* 2. Human Readable Friendly Vehicle Name */}
                <td className="p-3 fw-bold text-white" style={{ fontSize: "15px" }}>
                  {item.carName || "Premium Fleet Vehicle"}
                </td>

                {/* 3. MNC Operational Asset Track Identifier */}
                <td className="p-3 font-monospace">
                  <Badge bg="dark" className="text-info border border-secondary border-opacity-50 px-2 py-1.5 fw-semibold" style={{ letterSpacing: "0.5px" }}>
                    {item.assetSku || `EXALT-FL-${item.carId.substring(0, 4).toUpperCase()}`}
                  </Badge>
                </td>
                
                {/* 4. Beautifully Formatted Local Dates */}
                <td className="p-3 small text-light text-opacity-75">
                  <span>{start}</span> <span className="text-secondary mx-1">→</span> <span>{end}</span>
                </td>
                
                {/* 5. Total Price Cost Matrix Statement */}
                <td className="p-3 text-end fw-bold text-success" data-test="row-total-price" style={{ fontSize: "16px" }}>
                  ${item.totalPrice}
                </td>
              </tr>
            );
          })}
        </tbody>
      </Table>
    </div>
  );
};