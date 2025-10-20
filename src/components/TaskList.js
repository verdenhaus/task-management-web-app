import TaskCard from './TaskCard';

const TaskList = ({ tasks }) => {
  return (
    <div>
      {tasks.map(task => <TaskCard key={task.id} task={task} />)}
    </div>
  );
};

export default TaskList;
