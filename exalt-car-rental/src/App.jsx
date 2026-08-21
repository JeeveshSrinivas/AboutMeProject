import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthProvider } from "./context/AuthContext";
import { Navigation } from "./components/Navigation";
import { ProtectedRoute } from "./components/ProtectedRoute";
import { appRoutes } from "./routes/appRoutes";

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <div style={{ backgroundColor: "#0F172A", minHeight: "100vh" }}>
          <Routes>
            {appRoutes.map((route, index) => {
              const Component = route.element;

              if (route.fallbackRedirect) {
                return <Route key={index} path={route.path} element={<Component to="/" replace />} />;
              }

              return (
                <Route
                  key={index}
                  path={route.path}
                  element={
                    route.protected ? (
                      <ProtectedRoute adminOnly={route.adminOnly}>
                        {/* 👍 Fix 1: Navigation now only injects inside authorized perimeters */}
                        <Navigation />
                        <Component />
                      </ProtectedRoute>
                    ) : (
                      <Component />
                    )
                  }
                />
              );
            })}
          </Routes>
        </div>
      </AuthProvider>
    </BrowserRouter>
  );
}