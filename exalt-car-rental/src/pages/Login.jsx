import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Container, Card, Form, Button, Alert, Nav } from "react-bootstrap";
import { useAuth } from "../context/AuthContext";

export const Login = () => {
  const [isSignUp, setIsSignUp] = useState(false);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const { loginUser, registerUser, loginWithGoogle } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      if (isSignUp) {
        await registerUser(email, password);
      } else {
        await loginUser(email, password);
      }
      navigate("/"); // Fixed: Redirects directly to the main layout dashboard core
    } catch (err) {
      setError(err.message.replace("Firebase: ", ""));
    } finally {
      setLoading(false);
    }
  };

  const handleGoogleSignIn = async () => {
    setError("");
    setLoading(true);
    try {
      await loginWithGoogle();
      navigate("/"); // Fixed: Redirects directly to the main layout dashboard core
    } catch (err) {
      setError(err.message.replace("Firebase: ", ""));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div 
      className="d-flex align-items-center justify-content-center min-vh-100 py-5"
      style={{
        backgroundColor: "#0F172A",
        backgroundImage: "radial-gradient(circle at 50% 0%, rgba(56, 189, 248, 0.15) 0%, transparent 75%)",
      }}
    >
      <Container className="d-flex justify-content-center">
        <Card 
          data-test="auth-card"
          style={{
            width: "100%",
            maxWidth: "460px",
            background: "rgba(30, 41, 59, 0.7)",
            backdropFilter: "blur(16px)",
            WebkitBackdropFilter: "blur(16px)",
            borderRadius: "24px",
            border: "1px solid rgba(255, 255, 255, 0.12)",
            boxShadow: "0 25px 50px -12px rgba(0, 0, 0, 0.7)",
          }}
          className="text-white p-3 p-sm-4"
        >
          <Card.Body>
            <div className="text-center mb-4">
              <h2 className="fw-bold fs-3 mb-1" style={{ letterSpacing: "1px", color: "#F8FAFC" }}>
                EXALT
              </h2>
              <p className="text-secondary small">Precision Performance & Luxury Rentals</p>
            </div>

            {/* Mode Switcher */}
            <Nav 
              variant="pills" 
              activeKey={isSignUp ? "signup" : "signin"} 
              className="mb-4 nav-justified p-1 rounded-3"
              style={{ background: "rgba(15, 23, 42, 0.6)" }}
            >
              <Nav.Item>
                <Nav.Link 
                  eventKey="signin" 
                  data-test="tab-signin"
                  onClick={() => setIsSignUp(false)}
                  className={`py-2 text-white small fw-semibold rounded-2 ${!isSignUp ? "bg-primary" : ""}`}
                >
                  Sign In
                </Nav.Link>
              </Nav.Item>
              <Nav.Item>
                <Nav.Link 
                  eventKey="signup" 
                  data-test="tab-signup"
                  onClick={() => setIsSignUp(true)}
                  className={`py-2 text-white small fw-semibold rounded-2 ${isSignUp ? "bg-primary" : ""}`}
                >
                  Sign Up
                </Nav.Link>
              </Nav.Item>
            </Nav>

            {error && (
              <Alert 
                variant="danger" 
                data-test="auth-error-alert" 
                className="py-2 small text-center border-0" 
                style={{ background: "rgba(239, 68, 68, 0.2)", color: "#FCA5A5" }}
              >
                {error}
              </Alert>
            )}

            <Form onSubmit={handleSubmit}>
              <Form.Group className="mb-3" controlId="authEmail">
                <Form.Label className="small text-secondary fw-semibold">EMAIL ADDRESS</Form.Label>
                <Form.Control
                  type="email"
                  required
                  placeholder="client@exalt.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  data-test="input-email"
                  className="bg-dark text-white border-secondary border-opacity-50 py-2"
                  style={{ borderRadius: "10px" }}
                />
              </Form.Group>

              <Form.Group className="mb-4" controlId="authPassword">
                <Form.Label className="small text-secondary fw-semibold">PASSWORD</Form.Label>
                <div className="position-relative">
                  <Form.Control
                    type={showPassword ? "text" : "password"}
                    required
                    placeholder="••••••••"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    data-test="input-password"
                    className="bg-dark text-white border-secondary border-opacity-50 py-2 pe-5"
                    style={{ borderRadius: "10px" }}
                  />
                  <Button
                    variant="link"
                    size="sm"
                    data-test="toggle-password-visibility"
                    onClick={() => setShowPassword(!showPassword)}
                    className="position-absolute end-0 top-50 translate-middle-y text-secondary text-decoration-none pe-3"
                  >
                    {showPassword ? "Hide" : "Show"}
                  </Button>
                </div>
              </Form.Group>

              <Button
                disabled={loading}
                type="submit"
                data-test="button-auth-submit"
                className="w-100 py-2.5 fw-bold text-white border-0 mb-3"
                style={{
                  background: "linear-gradient(135deg, #0284C7 0%, #38BDF8 100%)",
                  borderRadius: "10px",
                  boxShadow: "0 4px 14px rgba(56, 189, 248, 0.3)",
                }}
              >
                {loading ? "Processing..." : isSignUp ? "Create Free Account" : "Access Portal"}
              </Button>
            </Form>

            <div className="d-flex align-items-center my-3">
              <hr className="flex-grow-1 border-secondary" />
              <span className="px-2 small text-secondary">OR</span>
              <hr className="flex-grow-1 border-secondary" />
            </div>

            <Button
              disabled={loading}
              variant="outline-light"
              data-test="button-google-login"
              onClick={handleGoogleSignIn}
              className="w-100 py-2 d-flex align-items-center justify-content-center gap-2 border-secondary border-opacity-50"
              style={{ borderRadius: "10px", backgroundColor: "rgba(15, 23, 42, 0.4)" }}
            >
              <svg width="18" height="18" viewBox="0 0 24 24">
                <path
                  fill="#4285F4"
                  d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"
                />
                <path
                  fill="#34A853"
                  d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"
                />
                <path
                  fill="#FBBC05"
                  d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z"
                />
                <path
                  fill="#EA4335"
                  d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z"
                />
              </svg>
              <span>Continue with Google</span>
            </Button>
          </Card.Body>
        </Card>
      </Container>
    </div>
  );
};