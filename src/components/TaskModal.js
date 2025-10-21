import { useState } from 'react';
import styles from './TaskModal.module.css';

const TaskModal = ({ task, users, onClose, onSave }) => {
  const [form, setForm] = useState({
    title: task?.title || '',
    description: task?.description || '',
    priorityLevel: task?.priorityLevel || 'MEDIUM',
    autoAssign: task?.assignedUserId ? false : true,
    assigneeId: task?.assignedUserId || '',
  });
  const [search, setSearch] = useState('');
  const [saving, setSaving] = useState(false);

  const filteredUsers = users.filter(u =>
    u.username.toLowerCase().includes(search.toLowerCase())
  );

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.title.trim()) {
      alert("Title cannot be empty");
      return;
    }

    if (!form.autoAssign && !form.assigneeId) {
      alert("Select a user or enable auto-assign");
      return;
    }

    setSaving(true);
    try {
      await onSave({
        ...form,
        id: task?.id,
        assignedUserId: form.autoAssign ? null : form.assigneeId,
      });
    } catch (err) {
      console.error(err);
      alert("Failed to save task");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className={styles.modalBackdrop}>
      <div className={styles.modal}>
        <h2>{task ? "Edit Task" : "Create Task"}</h2>
        <form onSubmit={handleSubmit} className={styles.form}>
          <label>Title</label>
          <input
            type="text"
            value={form.title}
            onChange={e => setForm({ ...form, title: e.target.value })}
          />

          <label>Description</label>
          <textarea
            value={form.description}
            onChange={e => setForm({ ...form, description: e.target.value })}
          />

          <label>Priority</label>
          <select
            value={form.priorityLevel}
            onChange={e => setForm({ ...form, priorityLevel: e.target.value })}
            className={`${styles.priority} ${styles[form.priorityLevel.toLowerCase()]}`}
          >
            <option value="LOW">Low</option>
            <option value="MEDIUM">Medium</option>
            <option value="HIGH">High</option>
          </select>

          <div className={styles.checkboxRow}>
            <label className={styles.checkboxLabel}>
                <input
                type="checkbox"
                checked={form.autoAssign}
                onChange={e => setForm({ ...form, autoAssign: e.target.checked })}
                />
                Auto-assign
            </label>
            </div>



            {!form.autoAssign && (
              <>
                {task?.assignedUserId && (
                  <p className={styles.currentAssignee}>
                    Currently assigned to: <strong>{task.assignedUsername || 'Unknown'}</strong>
                  </p>
                )}
                <input
                  type="text"
                  placeholder="Search user..."
                  value={search}
                  onChange={e => setSearch(e.target.value)}
                  className={styles.searchInput}
                />
                <select
                  value={form.assigneeId}
                  onChange={e => setForm({ ...form, assigneeId: e.target.value })}
                >
                  <option value="">Select user</option>
                  {filteredUsers.map(u => (
                    <option key={u.id} value={u.id}>{u.username}</option>
                  ))}
                </select>
              </>
            )}


          <div className={styles.buttons}>
            <button type="submit" disabled={saving}>{saving ? "Saving..." : "Save"}</button>
            <button type="button" onClick={onClose}>Cancel</button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default TaskModal;
