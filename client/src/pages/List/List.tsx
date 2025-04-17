import { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import styles from "./List.module.scss";
import Task from "../../components/List/Task.tsx";
import AddTaskModal from "../../components/List/AddTaskModal.tsx";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPlus } from "@fortawesome/free-solid-svg-icons";
import api from "../../services/api.ts";

interface Task {
  id: number;
  title: string;
  completed: boolean;
}

interface ListResponse {
  name: string;
  author: string;
  items: Task[];
}

function List() {
  const modal = useRef<HTMLDialogElement>(null);

  const { listId } = useParams();

  const [listName, setListName] = useState("");
  const [listAuthor, setListAuthor] = useState("");
  const [tasks, setTasks] = useState<Task[]>([]);

  const [error, setError] = useState(false);

  useEffect(() => {
    api
      .get<ListResponse>(`/api/todolists/${listId}`)
      .then((response) => {
        setListName(response.data.name);
        setListAuthor(response.data.author);
        setTasks(response.data.items);
      })
      .catch(() => {
        console.log("Error fetching list");
        setError(true);
      });
  }, [listId]);

  if (error || listId === undefined) {
    return <h2>404 Not Found</h2>;
  }

  function deleteTask(taskId: number) {
    api
      .delete(`/api/todolists/${listId}/items/${taskId}`)
      .then(() => {
        setTasks((prevTasks) => prevTasks.filter((task) => task.id !== taskId));
      })
      .catch(() => {
        console.log("Error deleting task");
      });
    console.log("Deleted task");
  }

  function addTask(taskName: string) {
    api
      .post(`/api/todolists/${listId}/items`, {
        title: taskName,
        completed: false,
        todoList: listId,
      })
      .then(() => {
        console.log(`created new task "${taskName}"`);
        window.location.reload();
      })
      .catch((error) => {
        console.error("Failed to add task", error);
      });
    modal.current?.close();
  }

  function showModal() {
    modal.current?.showModal();
  }

  return (
    <>
      <div className={styles.topMenu}>
        <a href="/home">&lt; Back to All Lists</a>
      </div>

      <div className={styles.listContainer}>
        <div className={styles.header}>
          <h2>{listName}</h2>
          <h3>by {listAuthor}</h3>
        </div>
        <div className={styles.listItems}>
          {Array.isArray(tasks) && tasks.length > 0 ? (
            tasks.map((task) => (
              <Task
                key={task.id}
                listId={listId}
                itemId={task.id}
                name={task.title}
                completed={task.completed}
                deleteTask={(id) => deleteTask(id)}
              />
            ))
          ) : (
            <>
              <h4>No tasks yet!</h4>
              <p>Click "Add a Task" below to create a new task</p>
            </>
          )}
        </div>
        <button className={styles.addBtn} onClick={showModal}>
          <FontAwesomeIcon icon={faPlus} className={styles.iconSpacing} />
          Add a Task
        </button>
      </div>
      <AddTaskModal ref={modal} addTask={addTask} />
    </>
  );
}

export default List;
