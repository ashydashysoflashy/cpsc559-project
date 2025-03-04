import { Navigate } from "react-router-dom";

// Decide whether or not a route is accessible by login
import { ReactNode } from "react";

function ProtectedRoute({ children }: { children: ReactNode }) {
  // Get the token
  const token = localStorage.getItem("token");

  // If there's no token, redirect to login
  if (!token) {
    return <Navigate to="/" replace />;
  }

  // Otherwise, render the protected page
  return children;
}

export default ProtectedRoute;
