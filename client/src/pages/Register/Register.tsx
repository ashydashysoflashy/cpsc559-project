import React, { useState } from "react";
import axios from "axios";
import { useNavigate, Link } from "react-router-dom";
import api from "../../services/api";
import styles from "./Register.module.scss";
import FormField from "../../components/FormField/FormField";
import GradientButton from "../../components/GradientButton/GradientButton";
import { Alert, Divider, Typography, FormHelperText } from "@mui/material";
import {
  EmailOutlined,
  PersonOutline,
  LockOutlined,
  Lock,
} from "@mui/icons-material";

interface RegisterResponse {
  message: string;
}

function Register() {
  const [email, setEmail] = useState<string>("");
  const [username, setUsername] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [confirmPassword, setConfirmPassword] = useState<string>("");
  const [success, setSuccess] = useState(false);
  const [failure, setFailure] = useState(false);

  const [emailError, setEmailError] = useState("");
  const [usernameError, setUsernameError] = useState("");
  const [passwordError, setPasswordError] = useState("");
  const [confirmPasswordError, setConfirmPasswordError] = useState("");

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

    let hasError = false;
    setEmailError("");
    setUsernameError("");
    setPasswordError("");
    setConfirmPasswordError("");

    if (!validateEmail(email)) {
      setEmailError("Please enter a valid e-mail address.");
      hasError = true;
    }

    if (username.length < 3) {
      setUsernameError("Username must be at least 3 characters.");
      hasError = true;
    }

    if (!validatePassword(password)) {
      setPasswordError(
        "Password must be at least 8 characters, with uppercase, lowercase, number, and special character."
      );
      hasError = true;
    }

    if (password !== confirmPassword) {
      setConfirmPasswordError("Passwords do not match.");
      hasError = true;
    }

    if (hasError) return;

    try {
      const res = await api.post<RegisterResponse>("/api/auth/register", {
        email,
        username,
        password,
      });

      setSuccess(true);

      //waiting 5 seconds before redirecting so they can see the success message
      setTimeout(() => {
        navigate("/");
      }, 3000);
    } catch (err) {
      console.error(err);
      if (axios.isAxiosError(err) && err.response?.status === 500) {
        setFailure(true);
      }
    }
  };

  return (
    <div className={styles.registerPage}>
      {success && (
        <Alert
          variant="filled"
          severity="success"
          className={styles.successMessage}
        >
          Account registered successfully!
        </Alert>
      )}
      {failure && (
        <Alert
          variant="filled"
          severity="error"
          className={styles.failureMessage}
        >
          Something went wrong. Please try again.
        </Alert>
      )}
      <div className={styles.registerContainer}>
        <h2>Create Account</h2>
        <form onSubmit={handleSubmit}>
          <FormField
            label="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            icon={<EmailOutlined />}
          />
          {emailError && <FormHelperText error>{emailError}</FormHelperText>}
          <FormField
            label="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            icon={<PersonOutline />}
          />
          {usernameError && (
            <FormHelperText error>{usernameError}</FormHelperText>
          )}
          <div className={styles.passwordRow}>
            <div>
              <FormField
                label="Password"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                icon={<LockOutlined />}
                showToggle={true}
                error={!!passwordError}
              />
              {passwordError && (
                <FormHelperText
                  error
                  sx={{ marginTop: "-10px", marginLeft: "14px" }}
                >
                  {passwordError}
                </FormHelperText>
              )}
            </div>
            <div>
              <FormField
                label="Confirm Password"
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                icon={<Lock />}
                showToggle={true}
                error={!!confirmPasswordError}
              />
              {confirmPasswordError && (
                <FormHelperText
                  error
                  sx={{ marginTop: "-10px", marginLeft: "14px" }}
                >
                  {confirmPasswordError}
                </FormHelperText>
              )}
            </div>
          </div>
          <GradientButton
            type="submit"
            sx={{
              width: "40%",
              margin: "0 auto",
              marginTop: "10px",
              marginBottom: "30px",
            }}
          >
            Register
          </GradientButton>
        </form>
        <Divider
          sx={{
            my: 2,
            width: "80%",
            margin: "0 auto",
          }}
        />
        <Typography variant="body2" className={styles.registerLinkText}>
          Already have an account? <Link to="/">Log in</Link>
        </Typography>
      </div>
    </div>
  );
}

export default Register;
