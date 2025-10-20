import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import styles from './UserLogin.module.css';
import axios from 'axios';

const UserLogin = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState({});
  const [showSuccess, setShowSuccess] = useState(false);
  const [showError, setShowError] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  const validateEmail = (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!email) return 'Email is required';
    if (!emailRegex.test(email)) return 'Please enter a valid email address';
    return null;
  };

  const validatePassword = (password) => {
    if (!password) return 'Password is required';
    if (password.length < 6) return 'Password must be at least 6 characters';
    return null;
  };

  const handleInputChange = (field, value) => {
    if (field === 'email') {
      setEmail(value);
      const error = validateEmail(value);
      setErrors(prev => ({ ...prev, email: error }));
    } else if (field === 'password') {
      setPassword(value);
      const error = validatePassword(value);
      setErrors(prev => ({ ...prev, password: error }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!email || !password) { 
      alert("Please fill in both email and password."); 
      return; 
    }

    setLoading(true);
    try {
      const res = await axios.post('/api/auth/login', { email, password });
      localStorage.setItem('jwt', res.data.token);
      
      setShowSuccess(true);
      
      setTimeout(() => {
        navigate('/dashboard');
      }, 1500);
    } catch (err) {
      console.error(err);
      const errorMsg = err.response?.data?.message || err.response?.data || "Login failed. Check credentials.";
      setErrorMessage(errorMsg);
      setShowError(true);
      
      setTimeout(() => {
        setShowError(false);
      }, 3000);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.container}>
      <form onSubmit={handleSubmit} className={styles.form}>
        <h2>Login</h2>
        
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

        <button type="submit" disabled={loading || errors.email || errors.password} className={styles.button}>
          {loading ? "Logging in..." : "Login"}
        </button>
        <p>
          Don't have an account? <button type="button" onClick={()=>navigate('/register')} className={styles.linkButton}>Register</button>
        </p>
      </form>
      
      {showSuccess && (
        <div className={styles.successPopup}>
          Login Successful! Redirecting...
        </div>
      )}
      
      {showError && (
        <div className={styles.errorPopup}>
          {errorMessage}
        </div>
      )}
    </div>
  );
};

export default UserLogin;
