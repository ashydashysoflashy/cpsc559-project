import { useNavigate } from "react-router-dom";
import styles from "./NavBar.module.scss";
import api from "../../services/api";

function NavBar() {
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      const res = await api.post("/auth/logout"); // TODO
      alert(res.data.message || "Logged out successfully");
      navigate("/");
    } catch (err) {
      console.error(err);
      alert("Logout failed. Please try again.");
    }
  };

  return (
    <div className={styles.navbar}>
      <span className={styles.username}>username</span>
      <button onClick={handleLogout} className={styles.logoutBtn}>
        Logout
      </button>
    </div>
  );
}

export default NavBar;
