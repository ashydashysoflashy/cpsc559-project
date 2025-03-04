import React from "react";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../services/api";
import styles from "./Login.module.scss";

// Define an interface for what we expect in the response
interface LoginResponse {
  token: string;
}

function Login() {
  const [email, setEmail] = useState<string>("");
  const [password, setPassword] = useState<string>("");

  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    try {
      // Get response data (token)
      const res = await api.post<LoginResponse>("/auth/login", {
        email,
        password,
      });
      const { token } = res.data;

      // Save it to browser storage
      localStorage.setItem("token", token);

      // Add the token to request headers from now on
      api.defaults.headers.common["Authorization"] = `Bearer ${token}`;

      // Go to homepage
      navigate("/todos");
    } catch (err) {
      console.error(err);
      alert("Login failed");
    }
  };

  return (
    <div className={styles.loginPage}>
      <div className={styles.loginContainer}>
        <h2>Login</h2>
        <form onSubmit={handleSubmit}>
          <input
            type="text"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <button type="submit">Login</button>
        </form>
        <p style={{ marginTop: "10px" }}>
          Don't have an account? <a href="/register">Register</a>
        </p>
      </div>
    </div>
  );
}

export default Login;
