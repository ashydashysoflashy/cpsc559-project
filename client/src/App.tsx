import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./pages/Login/Login";
import Register from "./pages/Register/Register";
import ProtectedRoute from "./components/ProtectedRoute";
import Home from "./pages/Home/Home";
import PublicRoute from "./components/PublicRoute";
import List from "./pages/List/List";

function App() {
  return (
    <Router>
      <Routes>
        {/*Public routes, need to be logged out*/}
        <Route
          path="/"
          element={
            <PublicRoute>
              <Login />
            </PublicRoute>
          }
        />
        <Route
          path="/register"
          element={
            <PublicRoute>
              <Register />
            </PublicRoute>
          }
        />
        {/*Protected routes, need to be logged in*/}
        <Route
          path="/home"
          element={
            <PublicRoute>
              <Home />
            </PublicRoute>
            /* keeping it public for now, will change when database is set up to authenticate
            <ProtectedRoute>
              <Home />
            </ProtectedRoute>
          */
          }
        />
        {/*unprotected route for now, will change when database is set up to authenticate*/}
        <Route
          path="/list"
          element={
            <PublicRoute>
              <List />
            </PublicRoute>
          }
        />
        {/*Handle unknown routes with 404 message*/}
        <Route path="*" element={<h2>404 Not Found</h2>} />
      </Routes>
    </Router>
  );
}

export default App;
