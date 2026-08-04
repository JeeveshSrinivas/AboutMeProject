import React from "react";
import { useNavigate } from "react-router-dom";

export default function EventCard({ event }) {
  const navigate = useNavigate();

  // 🔄 Updated helper function to calculate full background color status
  const getDateStatus = (eventDateStr) => {
    if (!eventDateStr) return "status-default";

    // 1. Get current date string in YYYY-MM-DD format (Local time)
    const now = new Date();
    const todayStr = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`;
    
    // 2. Get event date string in YYYY-MM-DD format from the stored datetime-local input
    // The value stored is usually "YYYY-MM-DDTHH:MM"
    const eventStr = eventDateStr.split("T")[0];

    // 3. Precise Comparison Logic:
    if (eventStr < todayStr) {
      return "status-past";      // 🔴 Past date (Yesterday or older)
    } else if (eventStr === todayStr) {
      return "status-current";   // 🔵 Current date (Today)
    } else {
      return "status-future";    // 🟣 Future date (Tomorrow or later)
    }
  };

  // Get the appropriate CSS class (e.g., status-past, status-current, status-future)
  const statusClass = getDateStatus(event.date);

  // Navigate to form and pass event data via React Router state
  const handleCardClick = () => {
    navigate("/form", { state: { eventToEdit: event } });
  };

  return (
    // 🔄 We apply the statusClass to the main div
    <div className={`event-card ${statusClass}`} onClick={handleCardClick}>
      <div className="event-card-header">
        <h3>{event.name}</h3>
        {/* We use a slight variant for the date badge to look good against the background */}
        <span className="badge-overlay">
          {new Date(event.date).toLocaleString([], { 
            dateStyle: "medium", 
            timeStyle: "short" 
          })}
        </span>
      </div>
      <p className="event-description">{event.description}</p>
      <small className="click-hint">Click card to edit →</small>
    </div>
  );
}