import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./pages/Login/Login";
import Register from "./pages/Register/Register";
import ProtectedRoute from "./components/ProtectedRoute";
import Home from "./pages/Home/Home";
import PublicRoute from "./components/PublicRoute";
import List from "./pages/List/List.tsx";
import CreateList from "./pages/CreateList/CreateList.tsx";

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
            <ProtectedRoute>
              <Home />
            </ProtectedRoute>
          }
        />
        <Route
            path="/create-list"
            element={
              <ProtectedRoute>
                <CreateList />
              </ProtectedRoute>
            }
        />
        <Route
          path="/list/:listId"
          element={
            <ProtectedRoute>
                <List />
            </ProtectedRoute>
          }
        />
        {/*Handle unknown routes with 404 message*/}
        <Route path="*" element={<h2>404 Not Found</h2>} />
      </Routes>
    </Router>
  );
}

export default App;
