import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import styles from "./List.module.scss";
import Task from "./Task.tsx";

interface Task {
  id: number;
  name: string;
  isComplete: boolean;
}

interface ListResponse {
  name: string;
  author: string;
  tasks: Task[];
}

function List() {
  const { listId } = useParams();
  const navigate = useNavigate();

  const [ listName, setListName ] = useState("");
  const [ listAuthor, setListAuthor ] = useState("");
  const [ tasks, setTasks ] = useState<Task[]>([]);

  useEffect(() => {
    // TODO: uncomment
    // axios.get<ListResponse>(`/lists/${listId}`)
    //     .then(response => {
    //         setListName(response.data.name)
    //         setListAuthor(response.data.author)
    //         setTasks(response.data.tasks);
    //     })
    //     .catch(() => {
    //       console.log("Error fetching list");
    //     });

    // TODO: placeholders, remove
    setListName("List name")
    setListAuthor("username")
    setTasks(
        [
          {id: 1, name: "task1", isComplete: false},
          {id: 2, name: "task2", isComplete: true}
        ]
    );
  }, [listId, navigate]);

  if (tasks === undefined) {
    return <h2>404 Not Found</h2>;
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
            {tasks.map(task => (
              <Task
                id = {task.id}
                name = {task.name}
                isComplete={task.isComplete}
              />
            ))}
          </div>
          <button className="add-task">+ Add a task</button>
        </div>
      </>
  )
}

export default List;
