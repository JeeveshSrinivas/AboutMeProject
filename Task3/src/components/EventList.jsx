import React, { useEffect, useState } from "react";
import { getEvents } from "../services/eventService";
import EventCard from "./EventCard";

export default function EventList() {
  const [events, setEvents] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [loading, setLoading] = useState(true);

  // Load events from Firestore when component mounts
  useEffect(() => {
    fetchEvents();
  }, []);

  const fetchEvents = async () => {
    try {
      setLoading(true);
      const data = await getEvents();
      setEvents(data);
    } catch (error) {
      console.error("Error fetching events:", error);
    } finally {
      setLoading(false);
    }
  };

  // Filter events dynamically based on search query
  const filteredEvents = events.filter((event) => {
    const searchLower = searchTerm.toLowerCase();
    const nameMatches = event.name?.toLowerCase().includes(searchLower);
    const descMatches = event.description?.toLowerCase().includes(searchLower);
    return nameMatches || descMatches;
  });

  return (
    <div className="event-list-container">
      {/* Search Input Bar */}
      <div className="search-bar">
        <input
          type="text"
          placeholder="🔍 Search events..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
      </div>

      {loading ? (
        <p className="loading-text">Loading events from Firestore...</p>
      ) : filteredEvents.length > 0 ? (
        <div className="event-grid">
          {filteredEvents.map((evt) => (
            <EventCard key={evt.id} event={evt} />
          ))}
        </div>
      ) : (
        <div className="empty-state">
          <p>No events found.</p>
        </div>
      )}
    </div>
  );
}