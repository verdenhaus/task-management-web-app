import React from 'react';
import styles from './TaskCard.module.css';

const TaskCard = ({ task }) => {
  const priorityClass = task.priorityLevel.toLowerCase();
  const formattedDate = new Date(task.creationTimestamp).toLocaleString();

  return (
    <div className={`${styles.card} ${styles[priorityClass]}`}>
      <h4>{task.title}</h4>
      <p>{task.description}</p>
      <small className={styles.timestamp}>
        Created: {formattedDate}
      </small>
    </div>
  );
};

export default TaskCard;
