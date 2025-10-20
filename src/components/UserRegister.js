import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import styles from './UserLogin.module.css';
import axios from 'axios';

const UserRegister = () => {
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirm, setConfirm] = useState('');
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});
  const [showSuccess, setShowSuccess] = useState(false);

  const validateUsername = (username) => {
    if (!username) return 'Username is required';
    if (username.length < 3) return 'Username must be at least 3 characters';
    if (username.length > 20) return 'Username must be less than 20 characters';
    return null;
  };

  const validateEmail = (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!email) return 'Email is required';
    if (!emailRegex.test(email)) return 'Please enter a valid email address';
    return null;
  };

  const validatePassword = (password) => {
    if (!password) return 'Password is required';
    if (password.length < 6) return 'Password must be at least 6 characters';
    if (password.length > 50) return 'Password must be less than 50 characters';
    return null;
  };

  const validateConfirmPassword = (confirm, password) => {
    if (!confirm) return 'Please confirm your password';
    if (confirm !== password) return 'Passwords do not match';
    return null;
  };

  const handleInputChange = (field, value) => {
    if (field === 'username') {
      setUsername(value);
      const error = validateUsername(value);
      setErrors(prev => ({ ...prev, username: error }));
    } else if (field === 'email') {
      setEmail(value);
      const error = validateEmail(value);
      setErrors(prev => ({ ...prev, email: error }));
    } else if (field === 'password') {
      setPassword(value);
      const passwordError = validatePassword(value);
      const confirmError = validateConfirmPassword(confirm, value);
      setErrors(prev => ({ 
        ...prev, 
        password: passwordError,
        confirm: confirmError
      }));
    } else if (field === 'confirm') {
      setConfirm(value);
      const error = validateConfirmPassword(value, password);
      setErrors(prev => ({ ...prev, confirm: error }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!username || !email || !password || !confirm) {
      alert("All fields are required");
      return;
    }
    if (password !== confirm) {
      alert("Passwords do not match");
      return;
    }

    setLoading(true);
    try {
      console.log('Sending registration request to:', '/api/auth/register');
      console.log('Request data:', { username, email, password });
      
      const response = await axios.post('/api/auth/register', { 
        username, 
        email, 
        password 
      });
      
      console.log('Registration response:', response);
      
      setShowSuccess(true);
      
      setTimeout(() => {
        navigate('/login');
      }, 1500);
    } catch (err) {
      console.error('Registration error:', err);
      console.error('Error response:', err.response);
      console.error('Error message:', err.message);
      
      if (err.response) {
        alert(`Registration failed: ${err.response.data?.message || err.response.statusText}`);
      } else if (err.request) {
        alert("Registration failed: No response from server. Check if backend is running.");
      } else {
        alert(`Registration failed: ${err.message}`);
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.container}>
      <form onSubmit={handleSubmit} className={styles.form}>
        <h2>Register</h2>
        
        <div className={styles.fieldContainer}>
          <input 
            type="text" 
            placeholder="Username" 
            value={username} 
            onChange={e => handleInputChange('username', e.target.value)} 
            className={`${styles.input} ${errors.username ? styles.error : username && !errors.username ? styles.success : ''}`} 
          />
          {errors.username && <div className={styles.errorMessage}>{errors.username}</div>}
        </div>

        <div className={styles.fieldContainer}>
          <input 
            type="email" 
            placeholder="Email" 
            value={email} 
            onChange={e => handleInputChange('email', e.target.value)} 
            className={`${styles.input} ${errors.email ? styles.error : email && !errors.email ? styles.success : ''}`} 
          />
          {errors.email && <div className={styles.errorMessage}>{errors.email}</div>}
        </div>

        <div className={styles.fieldContainer}>
          <input 
            type="password" 
            placeholder="Password" 
            value={password} 
            onChange={e => handleInputChange('password', e.target.value)} 
            className={`${styles.input} ${errors.password ? styles.error : password && !errors.password ? styles.success : ''}`} 
          />
          {errors.password && <div className={styles.errorMessage}>{errors.password}</div>}
        </div>

        <div className={styles.fieldContainer}>
          <input 
            type="password" 
            placeholder="Confirm Password" 
            value={confirm} 
            onChange={e => handleInputChange('confirm', e.target.value)} 
            className={`${styles.input} ${errors.confirm ? styles.error : confirm && !errors.confirm ? styles.success : ''}`} 
          />
          {errors.confirm && <div className={styles.errorMessage}>{errors.confirm}</div>}
        </div>

        <button 
          type="submit" 
          disabled={loading || errors.username || errors.email || errors.password || errors.confirm} 
          className={styles.button}
        >
          {loading ? "Registering..." : "Register"}
        </button>
        <p>
          Already have an account? <button type="button" onClick={()=>navigate('/login')} className={styles.linkButton}>Login</button>
        </p>
      </form>
      
      {showSuccess && (
        <div className={styles.successPopup}>
          Registration Successful! Redirecting...
        </div>
      )}
    </div>
  );
};

export default UserRegister;
