import React from 'react';
import styles from './TaskCard.module.css';

const TaskCard = ({ task }) => {
  const priorityClass = task.priorityLevel.toLowerCase();

  return (
    <div className={`${styles.card} ${styles[priorityClass]}`}>
      <h4>{task.title}</h4>
      <p>{task.description}</p>
    </div>
  );
};

export default TaskCard;
