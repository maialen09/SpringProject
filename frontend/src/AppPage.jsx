import React, { useState, useEffect } from 'react';
import { getAll, post, put, deleteById } from './restdb.jsx';
import './App.css';
import { useNavigate } from 'react-router-dom';
import { CustomerList } from './CustomerList.jsx';
import { CustomerAddUpdateForm } from './CustomerAddUpdateForm.jsx';
import { Account } from './Account.jsx';

export function App(props) {
  let blankCustomer = { "id": -1, "name": "", "email": "", "password": "" };
  const [customers, setCustomers] = useState([]);
  const [formObject, setFormObject] = useState(blankCustomer);
  
  let mode = (formObject.id >= 0) ? 'Update' : 'Add';

  const navigate = useNavigate();
  if(props.username === "") {
    navigate("/login");
  }

  useEffect(() => { getCustomers() }, [formObject]);

  const getCustomers = function () {
    getAll(setCustomers);
  }

  const handleListClick = function (item) {
    if (formObject.id === item.id) {
      setFormObject(blankCustomer);
    } else {
      setFormObject(item);
    }
  }

  const handleInputChange = function (event) {
    const name = event.target.name;
    const value = event.target.value;
    let newFormObject = { ...formObject }
    newFormObject[name] = value;
    setFormObject(newFormObject);
  }

  let onCancelClick = function () {
    setFormObject(blankCustomer);
  }

  let onDeleteClick = function () {
    let postopCallback = () => { setFormObject(blankCustomer); }
    if (formObject.id >= 0) {
      deleteById(formObject.id, postopCallback);
    } else {
      setFormObject(blankCustomer);
    }
  }

  let onSaveClick = function () {
    let postopCallback = () => { setFormObject(blankCustomer); }
    if (mode === 'Add') {
      post(formObject, postopCallback);
    }
    if (mode === 'Update') {
      put(formObject, postopCallback);
    }

  }

  let pvars = {
    mode: mode,
    handleInputChange: handleInputChange,
    formObject: formObject,
    onDeleteClick: onDeleteClick,
    onSaveClick: onSaveClick,
    onCancelClick: onCancelClick
  }

  // Simulate events for the logged-in user
  const userEvents = [
    { event_name: "React Conference", event_date: "2025-11-10", event_location: "Madrid" },
    { event_name: "Spring Boot Meetup", event_date: "2025-12-05", event_location: "Bilbao" },
    { event_name: "Docker Day", event_date: "2026-01-20", event_location: "Barcelona" }
  ];

  return ( 
    <div>
      <Account username={props.username} setUsername={props.setUsername}  />
      {/* Events Section */}
      <div className="boxed" style={{marginBottom: '2em'}}>
        <h4>My Events</h4>
        <table>
          <thead>
            <tr>
              <th>Event Name</th>
              <th>Date</th>
              <th>Location</th>
            </tr>
          </thead>
          <tbody>
            {userEvents.map((event, idx) => (
              <tr key={idx}>
                <td>{event.event_name}</td>
                <td>{event.event_date}</td>
                <td>{event.event_location}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      <CustomerList
        customers={customers}
        formObject={formObject}
        handleListClick={handleListClick}
      />
      <CustomerAddUpdateForm {...pvars} />
    </div>
  );
}

export default App;
