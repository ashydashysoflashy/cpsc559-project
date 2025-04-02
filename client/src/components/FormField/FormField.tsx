import React, { useState } from "react";
import {
  FormControl,
  TextField,
  InputAdornment,
  FormHelperText,
} from "@mui/material";
import { createTheme, ThemeProvider } from "@mui/material/styles";
import VisibilityIcon from "@mui/icons-material/Visibility";
import VisibilityOffIcon from "@mui/icons-material/VisibilityOff";
import IconButton from "@mui/material/IconButton";

const theme = createTheme({
  components: {
    MuiTextField: {
      styleOverrides: {
        root: {
          backgroundColor: "#F7F9FA", // background of the input field
          borderRadius: "4px", // rounded corners of the text field
          "&:hover": {
            // Hover styles
            "& .MuiOutlinedInput-notchedOutline": {
              borderColor: "#8795e8", // border color on hover
            },
            "& .MuiInputLabel-root, .MuiInputAdornment-root .MuiSvgIcon-root": {
              color: "#8795e8", // label and icon color on hover
              fontWeight: "600",
            },
          },
          "& .MuiOutlinedInput-root": {
            "&:hover": {
              // Re-define hover for nested input root
              "& .MuiOutlinedInput-notchedOutline": {
                borderColor: "#8795e8",
              },
              "& .MuiInputLabel-root, .MuiInputAdornment-root .MuiSvgIcon-root":
                {
                  color: "#8795e8",
                  fontWeight: "600",
                },
            },
            "& .MuiOutlinedInput-notchedOutline": {
              borderColor: "#8795e8", // default border color
            },
            "& .MuiInputLabel-root": {
              color: "#8795e8", // default label color
              fontWeight: "normal",
              "&:hover": {
                color: "#8795e8",
                fontWeight: "600",
              },
            },
            "& .MuiInputAdornment-root .MuiSvgIcon-root": {
              color: "#8795e8", // default icon color
            },
          },
          "& .MuiOutlinedInput-root.Mui-focused": {
            // Focused field styling
            "& .MuiOutlinedInput-notchedOutline": {
              borderWidth: "2px",
              borderColor: "#ad8cff",
            },
            "& .MuiInputLabel-root": {
              color: "#ad8cff",
              fontWeight: "600",
            },
            "& .MuiInputAdornment-root .MuiSvgIcon-root": {
              color: "#ad8cff",
              fontWeight: "600",
            },
          },
          "& .MuiInputLabel-root.Mui-focused": {
            color: "#ad8cff", // focused label color
            fontWeight: "600",
          },
        },
      },
    },
  },
});

interface FormFieldProps {
  label: string;
  type?: string;
  value: string;
  onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  icon: React.ReactNode;
  showToggle?: boolean;
  helperText?: string;
  error?: boolean;
}

function FormField({
  label,
  type = "text",
  value,
  onChange,
  icon,
  showToggle = false,
  helperText,
  error = false,
}: FormFieldProps) {
  const [showPassword, setShowPassword] = useState(false);

  const toggleVisibility = () => {
    setShowPassword((prev) => !prev);
  };
  return (
    <ThemeProvider theme={theme}>
      <FormControl fullWidth margin="normal" variant="outlined">
        <TextField
          label={label}
          type={showToggle ? (showPassword ? "text" : "password") : type}
          value={value}
          onChange={onChange}
          fullWidth
          error={error}
          InputProps={{
            startAdornment: (
              <InputAdornment position="start">{icon}</InputAdornment>
            ),
            endAdornment: showToggle && (
              <InputAdornment position="end">
                <IconButton onClick={toggleVisibility} edge="end" size="small">
                  {showPassword ? <VisibilityOffIcon /> : <VisibilityIcon />}
                </IconButton>
              </InputAdornment>
            ),
          }}
        />
        {helperText && (
          <FormHelperText error={error} sx={{ ml: 1 }}>
            {helperText}
          </FormHelperText>
        )}
      </FormControl>
    </ThemeProvider>
  );
}

export default FormField;
