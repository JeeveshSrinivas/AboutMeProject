import React, { useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { createEvent, updateEvent } from "../services/eventService";

export default function EventForm() {
  const location = useLocation();
  const navigate = useNavigate();

  // If we came from clicking a card, eventToEdit will exist in router state
  const eventToEdit = location.state?.eventToEdit;

  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [date, setDate] = useState("");
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (eventToEdit) {
      setName(eventToEdit.name || "");
      setDescription(eventToEdit.description || "");
      setDate(eventToEdit.date || "");
    }
  }, [eventToEdit]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!name || !date) {
      alert("Please fill in both the event name and date!");
      return;
    }

    try {
      setSaving(true);
      const eventData = { name, description, date };

      if (eventToEdit) {
        // Update existing event in Firestore
        await updateEvent(eventToEdit.id, eventData);
      } else {
        // Add new event to Firestore
        await createEvent(eventData);
      }

      // Navigate back to main list
      navigate("/");
    } catch (error) {
      console.error("Error saving event:", error);
      alert("Failed to save event. Check browser console for details.");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="form-container">
      <form onSubmit={handleSubmit} className="event-form">
        <div className="form-group">
          <label>Event Name *</label>
          <input
            type="text"
            required
            placeholder="e.g. React & Firebase Tech Talk"
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
        </div>

        <div className="form-group">
          <label>Date & Time *</label>
          <input
            type="datetime-local"
            required
            value={date}
            onChange={(e) => setDate(e.target.value)}
          />
        </div>

        <div className="form-group">
          <label>Description</label>
          <textarea
            rows="4"
            placeholder="Enter event details..."
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />
        </div>

        <div className="form-actions">
          <button type="button" className="btn-secondary" onClick={() => navigate("/")}>
            Cancel
          </button>
          <button type="submit" className="btn-primary" disabled={saving}>
            {saving ? "Saving..." : eventToEdit ? "Update Event" : "Create Event"}
          </button>
        </div>
      </form>
    </div>
  );
}