import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import styles from "./Home.module.scss";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPlus, faPencil, faTrash } from "@fortawesome/free-solid-svg-icons";
import api from "../../services/api";
import NavBar from "../../components/NavBar/NavBar";

interface UserResponse {
  id: number;
  username: string;
  email: string;
}
interface ListResponse {
  id: number;
  name: string;
  author: string;
}

function Home() {
  const [currentUser, setCurrentUser] = useState<UserResponse>();
  const [lists, setLists] = useState<ListResponse[]>([]);
  const [error, setError] = useState(false);

  const navigate = useNavigate();

  useEffect(() => {
    const fetchCurrentUser = async () => {
      const token = localStorage.getItem("token");
      if (!token) {
        console.log("No auth token found.");
        setError(true);
        return;
      }

      try {
        const response = await api.get<UserResponse>("api/auth/me", {
          headers: { Authorization: `Bearer ${token}` },
        });
        setCurrentUser(response.data);
      } catch (err) {
        console.error("Error fetching user:", err);
        setError(true);
      }
    };

    fetchCurrentUser();
  }, []);

  useEffect(() => {
    const fetchLists = async () => {
      try {
        const response = await api.get<ListResponse[]>("/api/todolists");
        setLists(response.data);
      } catch (err) {
        console.error("Error fetching lists:", err);
        setError(true);
      }
    };

    fetchLists();
  }, []);

  if (error || currentUser === null) {
    return <h2>404 Not Found</h2>;
  }

  const handleEditList = async (listId: number) => {
    navigate(`/list/${listId}`);
  };

  const handleDeleteList = async (listId: number) => {
    try {
      const listRes = await api.get(`/api/todolists/${listId}`);
      const listAuthor = listRes.data.author;

      if (!currentUser || currentUser.username !== listAuthor) {
        alert("You do not have permission to delete this list.");
        return;
      }

      await api.delete(`/api/todolists/${listId}`);
      setLists((prevLists) => prevLists.filter((list) => list.id !== listId));
    } catch (err) {
      console.error("Failed to delete the list:", err);
      alert("Failed to delete the list.");
    }
  };

  const handleCreateList = () => {
    navigate("/create-list");
  };

  return (
    <div className={styles.homePage}>
      <NavBar />
      <div className={styles.mainContent}>
        <h2 className={styles.title}>All Lists</h2>
        <div className={styles.listContainer}>
          {Array.isArray(lists) && lists.length > 0 ? (
            lists.map((list) => (
              <div key={list.id} className={styles.listItem}>
                <span>
                  {list.name} <em>(@{list.author || "Loading..."})</em>
                </span>
                <div>
                  <button
                    className={styles.iconButton}
                    onClick={() => handleEditList(list.id)}
                  >
                    <FontAwesomeIcon icon={faPencil} />
                  </button>
                  {currentUser?.username === list.author && (
                    <button
                      className={styles.deleteButton}
                      onClick={() => handleDeleteList(list.id)}
                    >
                      <FontAwesomeIcon icon={faTrash} />
                    </button>
                  )}
                </div>
              </div>
            ))
          ) : (
            <div className={styles.emptyState}>
              <span className={styles.emptyMessageBold}>No lists yet!</span>
              <span className={styles.emptyMessageSub}>
                Click "Create List" below to get started
              </span>
            </div>
          )}
        </div>

        <button className={styles.addBtn} onClick={handleCreateList}>
          <FontAwesomeIcon icon={faPlus} className={styles.iconSpacing} />
          Create a List
        </button>
      </div>
    </div>
  );
}

export default Home;
