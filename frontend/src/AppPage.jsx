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

  // Real events for the logged-in user
  const [userEvents, setUserEvents] = useState([]);

  // Store registrations to link event and registrationId
  const [registrations, setRegistrations] = useState([]);
  useEffect(() => {
    const customerId = localStorage.getItem('customerId');
    if (!customerId) return;
    fetch(`http://localhost:8080/api/registrations/customer/${customerId}`)
      .then(res => res.json())
      .then(async regs => {
        setRegistrations(regs);
        const eventPromises = regs.map(reg =>
          fetch(`http://localhost:8080/api/events/${reg.eventId}`)
            .then(res => res.ok ? res.json() : null)
        );
        const events = await Promise.all(eventPromises);
        // Attach registrationId to each event for deletion
        const eventsWithRegId = events.map((event, idx) => event ? { ...event, registrationId: regs[idx].id } : null);
        setUserEvents(eventsWithRegId.filter(e => e));
      });
  }, []);

  // Delete registration for an event
  const handleDeleteEvent = (registrationId) => {
    fetch(`http://localhost:8080/api/registrations/${registrationId}`, { method: 'DELETE' })
      .then(res => {
        if (res.ok) {
          // Remove event from userEvents
          setUserEvents(prev => prev.filter(ev => ev.registrationId !== registrationId));
        }
      });
  };

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
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {userEvents.length === 0 ? (
              <tr><td colSpan="4">No events found</td></tr>
            ) : (
              userEvents.map((event, idx) => (
                <tr key={idx}>
                  <td>{event.event_name || event.eventName}</td>
                  <td>{event.event_date || event.eventDate}</td>
                  <td>{event.location}</td>
                  <td>
                    <button onClick={() => handleDeleteEvent(event.registrationId)} style={{color: 'red'}}>Delete</button>
                  </td>
                </tr>
              ))
            )}
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
