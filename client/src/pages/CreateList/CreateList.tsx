import {useRef, useState} from "react";
import styles from "./CreateList.module.scss"
import listStyles from "../List/List.module.scss"
import AddTaskModal from "../../components/List/AddTaskModal.tsx";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPlus } from "@fortawesome/free-solid-svg-icons";
import CreateListTask from "../../components/List/CreateListTask.tsx";
import api from "../../services/api.ts";
import {useNavigate} from "react-router-dom";

interface Task {
  title: string;
  completed: boolean;
}

function CreateList() {

  const navigate = useNavigate();

  const modal = useRef<HTMLDialogElement>(null);

  const [ listName, setListName ] = useState("");
  const [ tasks, setTasks ] = useState<Task[]>([]);

  function deleteTask(index: number) {
    // Remove task at index given
    setTasks(prevTasks =>
        (prevTasks.filter((_, i) => i !== index))
    );
  }

  function toggleTask(index: number) {
    setTasks(prevTasks =>
        prevTasks.map((task, i) =>
            i === index ? { ...task, completed: !task.completed } : task
        )
    );
  }

  function addTask(taskName: string) {
    setTasks(prevTasks =>
        [...prevTasks, {title: taskName, completed: false}]
    );
    modal.current?.close()
  }

  function showModal() {
    modal.current?.showModal()
  }

  function createList() {
    api.post(`/api/todolists`, {
      name: listName,
      items: tasks
    })
    .then((response) => {
      navigate("/list/" + response.data.id)
    })
    .catch(error => {
      console.error("Failed to create list", error);
    });
  }

  return (
      <>
        <div className={listStyles.topMenu}>
          <a href="/home">&lt; Back to All Lists</a>
        </div>

        <div className={listStyles.listContainer}>
          <div className={listStyles.header}>
            <input
              placeholder="List Name"
              value = {listName}
              onChange={e => setListName(e.target.value)}
              className={styles.titleInput}
            />
          </div>
          <div className={listStyles.listItems}>
            {tasks.map(((task, index) => (
                <CreateListTask
                    key = {index}
                    itemId= {index}
                    name = {task.title}
                    completed={task.completed}
                    deleteTask={(id) => deleteTask(id)}
                    toggleTask={(id) => toggleTask(id)}
                />
            )))}
            {tasks.length === 0 && <><h4>No tasks yet!</h4><p>Click "Add a Task" below to create a new task</p></>}
          </div>
          <button className={listStyles.addBtn} onClick={showModal}>
            <FontAwesomeIcon icon={faPlus} className={listStyles.iconSpacing} />
            Add a Task
          </button>
          <button className={listStyles.addBtn} onClick={createList}>
            <FontAwesomeIcon icon={faPlus} className={listStyles.iconSpacing} />
            Create List
          </button>
        </div>
        <AddTaskModal ref={modal} addTask={addTask}/>
      </>
  )
}

export default CreateList;
