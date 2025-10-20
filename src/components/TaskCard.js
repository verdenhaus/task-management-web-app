import styles from './TaskCard.module.css';

const TaskCard = ({ task }) => {
  const priorityClass =
    task.priority === 'High'
      ? styles.high
      : task.priority === 'Medium'
      ? styles.medium
      : styles.low;

  return (
    <div className={`${styles.card} ${priorityClass}`}>
      <h3>{task.title}</h3>
      <p>{task.description}</p>
      <small>Priority: {task.priority}</small>
    </div>
  );
};

export default TaskCard;
