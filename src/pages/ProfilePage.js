import { useState, useEffect } from 'react';
import axios from 'axios';

const ProfilePage = () => {
  const [user, setUser] = useState({ username:'', email:'' });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const token = localStorage.getItem('jwt');

  if (!token) { window.location.href = '/login'; }

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const currentUserId = payload.id;

        const res = await axios.get('/api/users', { headers: { Authorization: `Bearer ${token}` } });
        const me = res.data.find(u => u.id === currentUserId);
        if (!me) throw new Error("User not found");
        setUser(me);
      } catch (err) {
        console.error(err);
        alert(err.response?.data?.error || "Failed to load profile");
      } finally {
        setLoading(false);
      }
    };
    fetchProfile();
  }, [token]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      await axios.put(`/api/users/${user.id}`, user, { headers: { Authorization: `Bearer ${token}` } });
      alert("Profile updated");
    } catch (err) {
      console.error(err);
      alert(err.response?.data?.error || "Failed to update profile");
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <p>Loading...</p>;

  return (
    <form onSubmit={handleSubmit} style={{ padding: '2rem' }}>
      <h2>Edit Profile</h2>
      <input type="text" value={user.username} onChange={e => setUser({...user, username:e.target.value})} />
      <input type="email" value={user.email} onChange={e => setUser({...user, email:e.target.value})} />
      <button type="submit" disabled={saving}>{saving ? "Saving..." : "Save"}</button>
    </form>
  );
};

export default ProfilePage;
