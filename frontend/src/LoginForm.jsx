import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getJWTToken, lookupCustomerByName } from './restdb';

export function LoginForm(props) {
  let [formData, setFormData] = useState({ username: "", password: "" });
  let [status, setStatus] = useState({ status: "init", message: "Enter credentials and click 'login'", token: "" });
  const navigate = useNavigate();

  const handleInputChange = (event) => {
    const { name, value } = event.target;
    setFormData({ ...formData, [name]: value });
  };

  const onRegisterClick = (formData) => {
    navigate("/register");
  };

  let onLoginClick = async function (credentials) {
    if (credentials === undefined ||
      credentials === null ||
      credentials.username === ""
      || credentials.password === "") {
      alert("Username and password cannot be empty");
      return;
    }
    const response = await getJWTToken(credentials.username, credentials.password);
    setStatus({ status: response.status, message: response.message, token: response.token });
    if (response.status === "error") {
      return;
    } else if (response.status === "success") {
      props.setUsername(credentials.username);
      // store isAdmin flag in localStorage by decoding token payload
      try {
        const token = response.token || localStorage.getItem('jwtToken');
        if (token) {
          const parts = token.split('.');
          if (parts.length >= 2) {
            const payload = JSON.parse(atob(parts[1].replace(/-/g, '+').replace(/_/g, '/')));
            if (payload && payload.isAdmin) {
              localStorage.setItem('isAdmin', 'true');
            } else {
              localStorage.removeItem('isAdmin');
            }
          }
        }
      } catch (e) {
        console.error('Failed to parse token payload', e);
      }
      // Fetch customer object and store ID
      try {
        const customer = await fetchCustomerId(credentials.username);
        if (customer && customer.id !== undefined) {
          localStorage.setItem('customerId', customer.id);
          console.log('Customer ID stored in localStorage:', customer.id);
        }
      } catch (err) {
        console.error('Error fetching customer ID:', err);
      }
      navigate("/app");
    }
  }

  // Helper to fetch customer object by username
  async function fetchCustomerId(username) {
    const token = localStorage.getItem('jwtToken');
    const headers = {};
    if (token) {
      headers["Authorization"] = "Bearer " + token;
    }
    const url = `http://localhost:8080/api/customers/byname?username=${encodeURIComponent(username)}`;
    const myInit = {
      method: 'GET',
      headers,
      mode: 'cors'
    };
    const response = await fetch(url, myInit);
    if (!response.ok) {
      throw new Error('Failed to fetch customer by name');
    }
    return await response.json();
  }

  return (
    <form className='boxed'>
      <h3>Login</h3>
      {/* <p>Please enter your username and password to continue.</p>             */}

      Username:<br />
      <input type="text" name="username"
        value={formData.username}
        onChange={handleInputChange} />

      <br />

      Password:<br />
      <input type="password" name="password"
        value={formData.password}
        onChange={handleInputChange} />

      <br /><br />
      <button type="button" onClick={() => onLoginClick(formData)}>Login</button>
      <button type="button" onClick={() => onRegisterClick(formData)}>Register</button>
      <br />
      <p>{status.message} </p>

    </form>
  )
}