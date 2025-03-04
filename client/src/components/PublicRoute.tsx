import { Navigate } from "react-router-dom";

// Decide whether or not a route is accessible by login
import { ReactNode } from "react";

function PublicRoute({ children }: { children: ReactNode }) {
  // Get the token
  const token = localStorage.getItem("token");

  // If there is a token, redirect to home page
  if (token) {
    return <Navigate to="/home" replace />;
  }

  // Otherwise, render the public page
  return children;
}

export default PublicRoute;
