import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import styles from './ProfilePage.module.css';

const ProfilePage = () => {
  const navigate = useNavigate();
  const [user, setUser] = useState({ username:'', email:'' });
  const [passwordData, setPasswordData] = useState({ currentPassword: '', newPassword: '', confirmPassword: '' });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [changingPassword, setChangingPassword] = useState(false);
  const [showError, setShowError] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [showSuccess, setShowSuccess] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');
  const token = localStorage.getItem('jwt');

  if (!token) { window.location.href = '/login'; }

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const res = await axios.get('/api/users/me', { headers: { Authorization: `Bearer ${token}` } });
        setUser(res.data);
      } catch (err) {
        console.error(err);
        setErrorMessage(err.response?.data?.message || "Failed to load profile");
        setShowError(true);
        setTimeout(() => setShowError(false), 3000);
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
      setSuccessMessage("Profile updated successfully!");
      setShowSuccess(true);
      setTimeout(() => setShowSuccess(false), 3000);
    } catch (err) {
      console.error(err);
      setErrorMessage(err.response?.data?.message || "Failed to update profile");
      setShowError(true);
      setTimeout(() => setShowError(false), 3000);
    } finally {
      setSaving(false);
    }
  };

  const handlePasswordChange = async (e) => {
    e.preventDefault();
    if (passwordData.newPassword !== passwordData.confirmPassword) {
      setErrorMessage("New passwords do not match");
      setShowError(true);
      setTimeout(() => setShowError(false), 3000);
      return;
    }
    
    setChangingPassword(true);
    try {
      await axios.put(`/api/users/me/password`, {
        currentPassword: passwordData.currentPassword,
        newPassword: passwordData.newPassword
      }, { headers: { Authorization: `Bearer ${token}` } });
      
      setSuccessMessage("Password changed successfully!");
      setShowSuccess(true);
      setPasswordData({ currentPassword: '', newPassword: '', confirmPassword: '' });
      setTimeout(() => setShowSuccess(false), 3000);
    } catch (err) {
      console.error(err);
      setErrorMessage(err.response?.data?.message || "Failed to change password");
      setShowError(true);
      setTimeout(() => setShowError(false), 3000);
    } finally {
      setChangingPassword(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('jwt');
    navigate('/login');
  };

  if (loading) return <div className={styles.container}><p>Loading...</p></div>;

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <h1>Profile Settings</h1>
        <button onClick={() => navigate('/dashboard')} className={styles.backButton}>
          ← Back to Dashboard
        </button>
      </div>

      <div className={styles.profileCard}>
        <h2>Edit Profile</h2>
        <form onSubmit={handleSubmit} className={styles.form}>
          <div className={styles.fieldContainer}>
            <label>Username</label>
            <input 
              type="text" 
              value={user.username} 
              onChange={e => setUser({...user, username:e.target.value})} 
              className={styles.input}
            />
          </div>
          
          <div className={styles.fieldContainer}>
            <label>Email</label>
            <input 
              type="email" 
              value={user.email} 
              onChange={e => setUser({...user, email:e.target.value})} 
              className={styles.input}
            />
          </div>
          
          <button type="submit" disabled={saving} className={styles.button}>
            {saving ? "Saving..." : "Save Profile"}
          </button>
        </form>
      </div>

      <div className={styles.profileCard}>
        <h2>Change Password</h2>
        <form onSubmit={handlePasswordChange} className={styles.form}>
          <div className={styles.fieldContainer}>
            <label>Current Password</label>
            <input 
              type="password" 
              value={passwordData.currentPassword} 
              onChange={e => setPasswordData({...passwordData, currentPassword:e.target.value})} 
              className={styles.input}
            />
          </div>
          
          <div className={styles.fieldContainer}>
            <label>New Password</label>
            <input 
              type="password" 
              value={passwordData.newPassword} 
              onChange={e => setPasswordData({...passwordData, newPassword:e.target.value})} 
              className={styles.input}
            />
          </div>
          
          <div className={styles.fieldContainer}>
            <label>Confirm New Password</label>
            <input 
              type="password" 
              value={passwordData.confirmPassword} 
              onChange={e => setPasswordData({...passwordData, confirmPassword:e.target.value})} 
              className={styles.input}
            />
          </div>
          
          <button type="submit" disabled={changingPassword} className={styles.button}>
            {changingPassword ? "Changing..." : "Change Password"}
          </button>
        </form>
      </div>

      <div className={styles.profileCard}>
        <h2>Account Actions</h2>
        <button onClick={handleLogout} className={styles.logoutButton}>
          🚪 Logout
        </button>
      </div>

      {showError && (
        <div className={styles.errorPopup}>
          {errorMessage}
        </div>
      )}
      
      {showSuccess && (
        <div className={styles.successPopup}>
          {successMessage}
        </div>
      )}
    </div>
  );
};

export default ProfilePage;
