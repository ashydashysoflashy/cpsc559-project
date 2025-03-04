import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../services/api";
import styles from "./Register.module.scss";

interface RegisterResponse {
  message: string;
}

function Register() {
  const [email, setEmail] = useState<string>("");
  const [username, setUsername] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [confirmPassword, setConfirmPassword] = useState<string>("");

  const navigate = useNavigate();

  const validateEmail = (email: string): boolean => {
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

    return emailRegex.test(email);
  };

  const validatePassword = (password: string): boolean => {
    const minLength = 8;
    const hasUpperCase = /[A-Z]/.test(password);
    const hasLowerCase = /[a-z]/.test(password);
    const hasNumber = /[0-9]/.test(password);
    const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(password);

    return (
      password.length >= minLength &&
      hasUpperCase &&
      hasLowerCase &&
      hasNumber &&
      hasSpecialChar
    );
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    // Validate the email string
    if (!validateEmail(email)) {
      alert("Please enter a valid e-mail address.");
      return;
    }

    // Check both password fields match
    if (password !== confirmPassword) {
      alert("Passwords do not match");
      return;
    }

    // Validate the password
    if (!validatePassword(password)) {
      alert(
        "Password must be at least 8 characters long and include an uppercase letter, a lowercase letter, a number, and a special character."
      );
      return;
    }

    try {
      // Send registration request
      const res = await api.post<RegisterResponse>("/auth/register", {
        email,
        username,
        password,
      });

      alert(res.data.message || "Registration successful");
      navigate("/"); // Redirect to login after registration
    } catch (err) {
      console.error(err);
      alert("Registration failed");
    }
  };

  return (
    <div className={styles.registerPage}>
      <div className={styles.registerContainer}>
        <h2>Register</h2>
        <form onSubmit={handleSubmit}>
          <input
            type="text"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <input
            type="password"
            placeholder="Confirm Password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
          />
          <button type="submit">Register</button>
        </form>
        <p style={{ marginTop: "10px" }}>
          Already have an account? <a href="/">Login</a>
        </p>
      </div>
    </div>
  );
}

export default Register;
