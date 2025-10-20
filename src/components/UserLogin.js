import { useState } from 'react';
import styles from './UserLogin.module.css';

const UserLogin = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!email || !password) {
      alert("Please fill in both fields");
      return;
    }
    console.log("Login submitted", { email, password });
  };

  return (
    <form onSubmit={handleSubmit} className={styles.form}>
      <input
        type="email"
        placeholder="Email"
        value={email}
        onChange={e => setEmail(e.target.value)}
        required
        className={styles.input}
      />
      <input
        type="password"
        placeholder="Password"
        value={password}
        onChange={e => setPassword(e.target.value)}
        required
        className={styles.input}
      />
      <button type="submit" className={styles.button}>Login</button>
    </form>
  );
};

export default UserLogin;
