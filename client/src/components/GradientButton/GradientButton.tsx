import Button from "@mui/material/Button";
import { styled } from "@mui/material/styles";

const GradientButton = styled(Button)({
  background: "linear-gradient(45deg,#ad8cff, #8795e8, #94d0ff, #8cebff)",
  borderRadius: "30px",
  boxShadow: `1px 1px 0 rgba(0, 0, 0, 0.1)`,
  color: "white",
  fontWeight: "bold",
  padding: "10px 30px",
  backgroundSize: "400% 400%",
  animation: "gradient 5s ease-in-out infinite",
  "&:hover": {
    boxShadow: `2px 2px 0 rgba(0, 0, 0, 0.1)`,
  },
  "@keyframes gradient": {
    "0%": {
      backgroundPosition: "0% 50%",
    },
    "50%": {
      backgroundPosition: "100% 50%",
    },
    "100%": {
      backgroundPosition: "0% 50%",
    },
  },
});

export default GradientButton;
