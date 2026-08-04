import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Header from "./components/Header";
import EventList from "./components/EventList";
import EventForm from "./components/EventForm";
import "./App.css";

export default function App() {
  return (
    <Router>
      <div className="app-container">
        <Header />
        <main className="content">
          <Routes>
            <Route path="/" element={<EventList />} />
            <Route path="/form" element={<EventForm />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}