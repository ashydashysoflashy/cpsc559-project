import React from "react";
import axios from "axios";
import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import api from "../../services/api";
import styles from "./Login.module.scss";
import FormField from "../../components/FormField/FormField";
import GradientButton from "../../components/GradientButton/GradientButton";
import { LockOutlined, PersonOutline } from "@mui/icons-material";
import { Alert, Divider, Typography } from "@mui/material";

// Define an interface for what we expect in the response
interface LoginResponse {
  token: string;
}

function Login() {
  const [username, setUsername] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [displayError, setDisplayError] = useState(false);

  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    try {
      // Get response data (token)
      const res = await api.post<LoginResponse>("/api/auth/login", {
        username: username,
        password: password,
      });
      const { token } = res.data;

      // Save it to browser storage
      localStorage.setItem("token", token);

      // Go to homepage
      navigate("/home");
    } catch (err) {
      console.error(err);
      if (axios.isAxiosError(err) && err.response?.status === 500) {
        setDisplayError(true);
      }
    }
  };

  return (
    <div className={styles.loginPage}>
      {displayError && (
        <Alert
          variant="filled"
          severity="error"
          className={styles.invalidLogin}
        >
          The username or password you entered is incorrect.
        </Alert>
      )}
      <div className={styles.loginContainer}>
        <div className={styles.loginAvatar}>
          <LockOutlined sx={{ color: "white", fontSize: 30 }} />
        </div>
        <h2>Login</h2>
        <form className={styles.form} onSubmit={handleSubmit}>
          <FormField
            label="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            icon={<PersonOutline />}
          />
          <FormField
            label="Password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            icon={<LockOutlined />}
            showToggle={true}
          />
          <GradientButton
            type="submit"
            sx={{
              width: "40%",
              margin: "0 auto",
              marginBottom: "30px",
              marginTop: "10px",
            }}
          >
            Log in
          </GradientButton>
        </form>
        <Divider
          sx={{
            my: 2,
            width: "80%",
            margin: "0 auto",
          }}
        />
        <Typography variant="body2" className={styles.loginLinkText}>
          Don't have an account? <Link to="/register">Register Now!</Link>
        </Typography>
      </div>
    </div>
  );
}

export default Login;
