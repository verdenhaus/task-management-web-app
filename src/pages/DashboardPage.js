import { useState, useEffect } from 'react';
import axios from 'axios';
import { DragDropContext, Droppable, Draggable } from 'react-beautiful-dnd';
import TaskCard from '../components/TaskCard';
import TaskModal from '../components/TaskModal';
import styles from './DashboardPage.module.css';

const DashboardPage = () => {
  const [tasks, setTasks] = useState([]);
  const [users, setUsers] = useState([]);
  const [currentUser, setCurrentUser] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingTask, setEditingTask] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showError, setShowError] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const token = localStorage.getItem('jwt');

  useEffect(() => {
    if (!token) {
      window.location.href = '/login';
      return;
    }

    const fetchAll = async () => {
      try {
        const currentUserRes = await axios.get('/api/users/me', {
          headers: { Authorization: `Bearer ${token}` }
        });
        const currentUserData = currentUserRes.data;
        console.log('Current User:', currentUserData);
        setCurrentUser(currentUserData);

        const tasksRes = await axios.get('/api/tasks', {
          headers: { Authorization: `Bearer ${token}` }
        });
        const allTasks = tasksRes.data;
        console.log('All tasks:', allTasks);
        setTasks(allTasks);

        const usersRes = await axios.get('/api/users', {
          headers: { Authorization: `Bearer ${token}` }
        });
        setUsers(usersRes.data);
      } catch (err) {
        console.error('Dashboard fetch error:', err.response?.data || err.message);
        setErrorMessage(`Failed to load dashboard data: ${err.response?.data?.message || err.message}`);
        setShowError(true);
        setTimeout(() => setShowError(false), 3000);
      } finally {
        setLoading(false);
      }
    };

    fetchAll();
  }, [token]);

  const onDragEnd = async (result) => {
    const { destination, source, draggableId } = result;
    if (!destination) return;

    if (destination.droppableId === source.droppableId && destination.index === source.index) return;

    const taskId = parseInt(draggableId);

    let newStatus = '';
    if (destination.droppableId.startsWith('user-')) {
      const parts = destination.droppableId.split('-');
      newStatus = parts.slice(3).join('-');
    } else {
      newStatus = destination.droppableId.replace('status-', '');
    }

    const updatedTasks = tasks.map(task =>
      task.id === taskId ? { ...task, status: newStatus } : task
    );
    setTasks(updatedTasks);

    try {
      await axios.put(`/api/tasks/${taskId}`, { status: newStatus }, {
        headers: { Authorization: `Bearer ${token}` }
      });
    } catch (err) {
      console.error(err);
      setTasks(tasks);
      setErrorMessage("You can't change the status of other people's tasks.");
      setShowError(true);
      setTimeout(() => setShowError(false), 3000);
    }
  };

  const handleSaveTask = async (taskData) => {
    try {
      if (taskData.id) {
        await axios.put(`/api/tasks/${taskData.id}`, taskData, { headers: { Authorization: `Bearer ${token}` } });
      } else {
        await axios.post('/api/tasks', taskData, { headers: { Authorization: `Bearer ${token}` } });
      }

      const tasksRes = await axios.get('/api/tasks', { headers: { Authorization: `Bearer ${token}` } });
      setTasks(tasksRes.data);
    } catch (err) {
      console.error(err);
      alert("Failed to save task");
    } finally {
      setModalOpen(false);
    }
  };

  if (loading) return <p>Loading dashboard...</p>;
  if (!currentUser) return <p>Loading user data...</p>;

  const myTasks = tasks.filter(task => task.assignedUserId === currentUser.id);
  const otherUsersTasks = tasks.filter(task => task.assignedUserId !== null && task.assignedUserId !== currentUser.id);

  const otherUsers = [...new Set(otherUsersTasks.map(t => t.assignedUserId))]
    .map(id => users.find(u => u.id === id))
    .filter(Boolean)
    .sort((a, b) => a.username.localeCompare(b.username));

  const statusColumns = [
    { id: 'ASSIGNED', title: 'Assigned' },
    { id: 'IN_PROGRESS', title: 'In Progress' },
    { id: 'TESTING', title: 'Testing' },
    { id: 'COMPLETE', title: 'Complete' }
  ];

  const getTasksByStatus = (taskList, status) => taskList.filter(task => (task.status || 'ASSIGNED') === status);

  const KanbanColumn = ({ status, title, tasks, canEdit, userId = null }) => {
    const droppableId = userId ? `user-${userId}-status-${status}` : `status-${status}`;
    return (
      <div className={styles.kanbanColumn}>
        <div className={styles.columnHeader}>
          <span className={styles.columnTitle}>{title}</span>
          <span className={styles.taskCount}>{tasks.length}</span>
        </div>
        <Droppable droppableId={droppableId}>
          {(provided, snapshot) => (
            <div
              ref={provided.innerRef}
              {...provided.droppableProps}
              className={styles.taskList}
              style={{ backgroundColor: snapshot.isDraggingOver ? '#3a3a4f' : 'transparent' }}
            >
              {tasks.length === 0 ? (
                <div className={styles.emptyColumn}>No tasks</div>
              ) : (
                tasks.map((task, index) => (
                  <Draggable key={task.id} draggableId={String(task.id)} index={index}>
                    {(provided, snapshot) => (
                      <div
                        ref={provided.innerRef}
                        {...provided.draggableProps}
                        {...provided.dragHandleProps}
                        onClick={() => {
                          if (canEdit) {
                            setEditingTask(task);
                            setModalOpen(true);
                          }
                        }}
                        style={{
                          ...provided.draggableProps.style,
                          opacity: snapshot.isDragging ? 0.8 : 1,
                        }}
                      >
                        <TaskCard task={task} />
                      </div>
                    )}
                  </Draggable>
                ))
              )}
              {provided.placeholder}
            </div>
          )}
        </Droppable>
      </div>
    );
  };

  return (
    <div className={styles.container}>
      <header className={styles.header}>
        <h1>Dashboard</h1>
        <button
          onClick={() => {
            setEditingTask(null);
            setModalOpen(true);
          }}
        >
          + Create Task
        </button>
      </header>

      <DragDropContext onDragEnd={onDragEnd}>
        <div className={styles.userSection}>
          <h2 className={styles.userSectionTitle}>My Tasks ({currentUser.username})</h2>
          <div className={styles.kanban}>
            {statusColumns.map(column => (
              <KanbanColumn
                key={column.id}
                status={column.id}
                title={column.title}
                tasks={getTasksByStatus(myTasks, column.id)}
                canEdit={true}
              />
            ))}
          </div>
        </div>

        {otherUsers.length > 0 && (
          <div className={`${styles.userSection} ${styles.otherUsersSection}`}>
            <h2 className={styles.userSectionTitle}>Other Users' Tasks</h2>
            {otherUsers.map(user => {
              const userTasks = otherUsersTasks.filter(t => t.assignedUserId === user.id);
              return (
                <div key={user.id} style={{ marginBottom: '1.5rem' }}>
                  <h3 style={{ color: '#a69eff', marginBottom: '0.5rem', fontSize: '1rem' }}>
                    {user.username}
                  </h3>
                  <div className={styles.kanban}>
                    {statusColumns.map(column => (
                      <KanbanColumn
                        key={`${user.id}-${column.id}`}
                        status={column.id}
                        title={column.title}
                        tasks={getTasksByStatus(userTasks, column.id)}
                        canEdit={false}
                        userId={user.id}
                      />
                    ))}
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </DragDropContext>

      {modalOpen && (
        <TaskModal
          task={editingTask}
          users={users}
          onClose={() => setModalOpen(false)}
          onSave={handleSaveTask}
        />
      )}

      {showError && <div className={styles.errorPopup}>{errorMessage}</div>}
    </div>
  );
};

export default DashboardPage;
