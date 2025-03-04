import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import styles from "./Home.module.scss";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPlus, faPencil, faTrash } from "@fortawesome/free-solid-svg-icons";

// mock Data for Lists & Users
const mockLists = [
  { id: "1", name: "Shopping List", author_id: "101" },
  { id: "2", name: "Work Tasks", author_id: "102" },
  { id: "3", name: "Goals", author_id: "103" },
  { id: "4", name: "Grocery List", author_id: "104" },
  { id: "5", name: "To-Do List", author_id: "105" },
  { id: "6", name: "CheckList", author_id: "106" },
  { id: "7", name: "Study Plans", author_id: "107" },
  { id: "8", name: "Projects to Start", author_id: "108" },
  { id: "9", name: "Materials", author_id: "109" },
  { id: "10", name: "Books to Read", author_id: "101" },
  { id: "11", name: "Movies to Watch", author_id: "102" },
  { id: "12", name: "Places to Visit", author_id: "103" },
];

const mockUsers = [
  { id: "101", username: "alice" },
  { id: "102", username: "bob" },
  { id: "103", username: "charlie" },
  { id: "104", username: "david" },
  { id: "105", username: "eve" },
  { id: "106", username: "frank" },
  { id: "107", username: "grace" },
  { id: "108", username: "heidi" },
  { id: "109", username: "ivan" },
];

const getUsername = (author_id: string) => {
  const user = mockUsers.find((user) => user.id === author_id);
  return user ? user.username : "Unknown User";
};

function Home() {
  const [lists, setLists] = useState(mockLists);
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/");
  };

  const handleEditList = (listId: string) => {
    navigate(`/list?listId=${listId}`); // TODO: redirect to list page with query params
  };

  const handleDeleteList = (listId: string, authorId: string) => {
    const currentUser = localStorage.getItem("userId"); // TODO: logged-in user

    if (currentUser !== authorId) {
      alert("You do not have permission to delete this list.");
      return;
    }

    setLists((prevLists) => prevLists.filter((list) => list.id !== listId)); //TODO: delete list from database
  };

  return (
    <div className={styles.homePage}>
      <div className={styles.navbar}>
        <span className={styles.username}>username</span>
        <button onClick={handleLogout} className={styles.logoutBtn}>
          Logout
        </button>
      </div>

      <div className={styles.mainContent}>
        <h2 className={styles.title}>All Lists</h2>

        <div className={styles.listContainer}>
          {lists.map((list) => (
            <div key={list.id} className={styles.listItem}>
              <span>
                {list.name} <em>(@{getUsername(list.author_id)})</em>
              </span>
              <div>
                <button
                  className={styles.iconButton}
                  onClick={() => handleEditList(list.id)}
                >
                  <FontAwesomeIcon icon={faPencil} />
                </button>
                <button
                  className={styles.deleteButton}
                  onClick={() => handleDeleteList(list.id, list.author_id)}
                >
                  <FontAwesomeIcon icon={faTrash} />
                </button>
              </div>
            </div>
          ))}
        </div>

        <button className={styles.addBtn}>
          <FontAwesomeIcon icon={faPlus} className={styles.iconSpacing} />
          Create a List
        </button>
      </div>
    </div>
  );
}

export default Home;
