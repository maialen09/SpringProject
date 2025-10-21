import { useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';

export function Account(props) {
  
  const navigate = useNavigate();
  const [showEvents, setShowEvents] = useState(false);
  let appTitle = "Customer App";
  let accountContainer = {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: '16px'
  }
  let padDivTop = {
    paddingTop: '16px',
  }

  let onLogoutClick = function () {
    if (window.confirm("Do you want to log out?")) {
      props.setUsername("");
      navigate("/login"); 
    } 
  }

  // Real events list from backend
  const [events, setEvents] = useState([]);
  const [eventsLoaded, setEventsLoaded] = useState(false);
  const [userRegistrations, setUserRegistrations] = useState([]);

  // Helper: detect admin from JWT token payload
  const isAdminFromToken = () => {
    try {
      const token = localStorage.getItem('jwtToken');
      if (!token) return false;
      const parts = token.split('.');
      if (parts.length < 2) return false;
      const payload = JSON.parse(atob(parts[1].replace(/-/g, '+').replace(/_/g, '/')));
      return payload && payload.isAdmin === true;
    } catch (e) {
      return false;
    }
  };

  const isAdmin = (localStorage.getItem('isAdmin') === 'true') || isAdminFromToken() || props.username === 'admin';
  console.log('Account: isAdmin=', isAdmin, 'username=', props.username, 'token=', localStorage.getItem('jwtToken'));

  // Fetch all events
  const fetchEvents = () => {
    fetch('http://localhost:8080/api/events')
      .then(res => res.json())
      .then(data => {
        setEvents(data);
        setEventsLoaded(true);
      })
      .catch(() => setEventsLoaded(true));
  };

  // Fetch user's registrations
  const fetchUserRegistrations = () => {
    const customerId = localStorage.getItem('customerId');
    if (!customerId) return;
    fetch(`http://localhost:8080/api/registrations/customer/${customerId}`)
      .then(res => res.json())
      .then(data => setUserRegistrations(data));
  };

  // Fetch events and registrations when showing events
  useEffect(() => {
    if (showEvents && !eventsLoaded) {
      fetchEvents();
      fetchUserRegistrations();
    }
    
    // Listen for registration updates from AppPage component
    const handleRegistrationUpdate = () => {
      if (showEvents) {
        fetchUserRegistrations();
      }
    };
    window.addEventListener('registrationsUpdated', handleRegistrationUpdate);
    
    return () => {
      window.removeEventListener('registrationsUpdated', handleRegistrationUpdate);
    };
  }, [showEvents, eventsLoaded]);

  // Register for an event
  const handleRegisterEvent = async (eventId) => {
    const customerId = localStorage.getItem('customerId');
    if (!customerId) return;
    const response = await fetch('http://localhost:8080/api/registrations', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ customerId: Number(customerId), eventId: eventId })
    });
    if (response.ok) {
      fetchUserRegistrations(); // Refresh registrations in this component
      // Trigger a custom event to notify other components
      window.dispatchEvent(new Event('registrationsUpdated'));
    }
  };

  // Admin: create event (sends Authorization header)
  const handleCreateEvent = async (event) => {
    const token = localStorage.getItem('jwtToken');
    const response = await fetch('http://localhost:8080/api/events', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', ...(token ? { Authorization: 'Bearer ' + token } : {}) },
      body: JSON.stringify(event)
    });
    if (response.ok) {
      fetchEvents(); // Immediately refresh the events list
    } else {
      console.error('Failed to create event', response.status);
    }
  };

  // Admin: delete event (sends Authorization header)
  const handleDeleteEventAdmin = async (eventId) => {
    const token = localStorage.getItem('jwtToken');
    const response = await fetch(`http://localhost:8080/api/events/${eventId}`, { method: 'DELETE', headers: { ...(token ? { Authorization: 'Bearer ' + token } : {}) } });
    if (response.ok) {
      fetchEvents(); // Immediately refresh the events list
    }
  };

  // Small admin create-event form component
  function AdminCreateEventForm({ onCreate }) {
    const [form, setForm] = useState({ eventName: '', eventDate: '', location: '' });
    const onChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });
    const onSubmit = (e) => {
      e.preventDefault();
      // Call the parent's onCreate function with the form data
      onCreate(form);
      // Clear the form
      setForm({ eventName: '', eventDate: '', location: '' });
    };
    return (
      <form onSubmit={onSubmit} style={{ marginBottom: '1em' }}>
        <input name="eventName" value={form.eventName} onChange={onChange} placeholder="Event Name" required />
        <input name="eventDate" value={form.eventDate} onChange={onChange} placeholder="Date (YYYY-MM-DD)" required />
        <input name="location" value={form.location} onChange={onChange} placeholder="Location" required />
        <button type="submit" style={{marginLeft: '8px'}}>Create Event</button>
      </form>
    );
  }

  // Admin: edit event inline
  const [editingEventId, setEditingEventId] = useState(null);
  const [editForm, setEditForm] = useState({ eventName: '', eventDate: '', location: '' });

  const startEdit = (event) => {
    setEditingEventId(event.id);
    setEditForm({ eventName: event.event_name || event.eventName || '', eventDate: event.event_date || event.eventDate || '', location: event.location || '' });
  };

  const cancelEdit = () => {
    setEditingEventId(null);
    setEditForm({ eventName: '', eventDate: '', location: '' });
  };

  const saveEdit = async (id) => {
    const token = localStorage.getItem('jwtToken');
    const payload = { eventName: editForm.eventName, eventDate: editForm.eventDate, location: editForm.location };
    const res = await fetch(`http://localhost:8080/api/events/${id}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json', ...(token ? { Authorization: 'Bearer ' + token } : {}) },
      body: JSON.stringify(payload)
    });
    if (res.ok) {
      cancelEdit();
      fetchEvents();
    } else {
      console.error('Failed to update event', res.status);
    }
  };

  return (
    <div style={padDivTop}>
      <div className='boxed pad5' >
        <div style={{ display: 'flex', flexDirection: 'row', alignItems: 'flex-start', justifyContent: 'space-between' }}>
          <div style={{ flex: 1 }}>
            <h3 className='floatL' style={accountContainer}>
              {appTitle}
              <button className='floatR icon-button'
                onClick={onLogoutClick}
                title="Click here to log out!" >
                <img
                  src="/person.png"
                  alt="Person"
                  style={{ width: '24px', height: '24px' }}
                />
                {props.username}
              </button>
              <button className='floatR icon-button' onClick={() => setShowEvents(!showEvents)} style={{marginLeft: '10px'}}>
                <img
                  src="/event.jpg"
                  alt="Events"
                  style={{ width: '24px', height: '24px' }}
                />
                Events
              </button>
            </h3>
          </div>
          {showEvents && (
            <div style={{ minWidth: '320px', marginLeft: '32px', background: '#f9f9f9', border: '1px solid #ddd', borderRadius: '8px', padding: '16px' }}>
              <h4>All Events</h4>
              {isAdmin && (
                <AdminCreateEventForm onCreate={handleCreateEvent} />
              )}
              <table>
                <thead>
                  <tr>
                    <th>Event Name</th>
                    <th>Date</th>
                    <th>Location</th>
                    {isAdmin && <th>Admin</th>}
                    <th>Action</th>
                  </tr>
                </thead>
                <tbody>
                  {events.length === 0 ? (
                    <tr><td colSpan={isAdmin ? 5 : 4}>No events found</td></tr>
                  ) : (
                    events.map((event, idx) => {
                      const isRegistered = userRegistrations.some(reg => reg.eventId === event.id);
                      const editing = editingEventId === event.id;
                      return (
                        <tr key={idx}>
                          {editing ? (
                            <>
                              <td><input value={editForm.eventName} onChange={(e) => setEditForm(f => ({...f, eventName: e.target.value}))} /></td>
                              <td><input value={editForm.eventDate} onChange={(e) => setEditForm(f => ({...f, eventDate: e.target.value}))} /></td>
                              <td><input value={editForm.location} onChange={(e) => setEditForm(f => ({...f, location: e.target.value}))} /></td>
                              {isAdmin && (
                                <td>
                                  <button onClick={() => saveEdit(event.id)} style={{color: 'green'}}>Save</button>
                                  <button onClick={cancelEdit} style={{marginLeft:'6px'}}>Cancel</button>
                                </td>
                              )}
                              <td></td>
                            </>
                          ) : (
                            <>
                              <td>{event.event_name || event.eventName}</td>
                              <td>{event.event_date || event.eventDate}</td>
                              <td>{event.location}</td>
                              {isAdmin && (
                                <td>
                                  <button onClick={() => handleDeleteEventAdmin(event.id)} style={{color: 'red', marginRight: '10px'}}>Delete</button>
                                  <button onClick={() => startEdit(event)} style={{marginLeft:'10px'}}>Edit</button>
                                </td>
                              )}
                              <td>
                                {!isRegistered && !isAdmin && (
                                  <button onClick={() => handleRegisterEvent(event.id)} style={{color: 'green'}}>Sign Up</button>
                                )}
                                {isRegistered && !isAdmin && (
                                  <span style={{color: 'gray'}}>Registered</span>
                                )}
                              </td>
                            </>
                          )}
                        </tr>
                      );
                    })
                  )}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}