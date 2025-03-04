import {useState} from "react";
import styles from "./List.module.scss";
import axios from "axios";

interface TaskProps {
  id: number;
  name: string;
  isComplete: boolean;
}

function Task(props: TaskProps) {

  const [ editing, setEditing ] = useState(false);
  const [ textInput, setTextInput ] = useState(props.name);
  const [ isComplete, setIsComplete ] = useState(props.isComplete);
  const [ name, setName ] = useState(props.name);

  function toggleComplete(taskId: number) {
    // TODO: for testing, remove
    setIsComplete(isComplete => !isComplete)

    // TODO: uncomment
    // axios.put(`/lists/${props.id}/task`, {
    //   id: props.id,
    //   name: props.name,
    //   isComplete: !isComplete,
    // })
    // // update the UI
    // .then(() => {
    //   setIsComplete(isComplete => !isComplete)
    // })
    // .catch(error => {
    //   console.error("Failed to update task", error);
    // });
  }

  function handleSave() {
    // TODO: for testing, remove
    setName(textInput);

    // TODO: uncomment
    // axios.put(`/lists/${props.id}/task`, {
    //   id: props.id,
    //   name: textInput,
    //   isComplete: isComplete,
    // })
    // // update the UI
    // .then(() => {
    //   setName(textInput);
    // })
    // .catch(error => {
    //   console.error("Failed to update task", error);
    // });

    setEditing(false);
  }

  return (
    <div key={props.id} className={styles.listItem}>
      <input
          type="checkbox"
          checked={isComplete}
          onChange={() => toggleComplete(props.id)}
      />
      {editing ? (
        <>
          <input
            value = {textInput}
            onChange={(e) => setTextInput(e.target.value)}
          />
          <button onClick={handleSave}>Save</button>
          <button className={styles.deleteButton}>Delete</button>
        </>
      ) : (
        <>
          <span className={isComplete ? styles.completed : ""}>
            {name}
          </span>
            <button onClick={() => setEditing(true)}>Edit</button>
            <button className={styles.deleteButton}>Delete</button>
        </>
      )}

    </div>
  )
}

export default Task;
