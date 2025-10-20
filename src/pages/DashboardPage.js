import TaskCard from '../components/TaskCard';

const DashboardPage = () => {
  const sampleTask = { title: "Sample Task", description: "Do this first", priority: "High" };
  return (
    <div>
      <h1>Dashboard</h1>
      <TaskCard task={sampleTask} />
    </div>
  );
};

export default DashboardPage;
