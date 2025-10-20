import { useNavigate } from 'react-router-dom';
import { useState } from 'react';

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
  if (showEvents && !eventsLoaded) {
    fetchEvents();
    fetchUserRegistrations();
  }

  // Register for an event
  const handleRegisterEvent = (eventId) => {
    const customerId = localStorage.getItem('customerId');
    if (!customerId) return;
    fetch('http://localhost:8080/api/registrations', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ customerId: Number(customerId), eventId: eventId })
    })
      .then(res => {
        if (res.ok) {
          fetchUserRegistrations(); // Refresh registrations
        }
      });
  };

  // Admin: create event (sends Authorization header)
  const handleCreateEvent = (event) => {
    const token = localStorage.getItem('jwtToken');
    fetch('http://localhost:8080/api/events', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json', ...(token ? { Authorization: 'Bearer ' + token } : {}) },
      body: JSON.stringify(event)
    })
      .then(res => {
        if (res.ok) {
          setEventsLoaded(false); // force reload next time showing events
        } else {
          console.error('Failed to create event', res.status);
        }
      });
  };

  // Admin: delete event (sends Authorization header)
  const handleDeleteEventAdmin = (eventId) => {
    const token = localStorage.getItem('jwtToken');
    fetch(`http://localhost:8080/api/events/${eventId}`, { method: 'DELETE', headers: { ...(token ? { Authorization: 'Bearer ' + token } : {}) } })
      .then(res => {
        if (res.ok) setEventsLoaded(false);
      });
  };

  // Small admin create-event form component
  function AdminCreateEventForm({ onCreate }) {
    const [form, setForm] = useState({ event_name: '', event_date: '', location: '' });
    const onChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });
    const onSubmit = (e) => {
      e.preventDefault();
      const token = localStorage.getItem('jwtToken');
      fetch('http://localhost:8080/api/events', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', ...(token ? { Authorization: 'Bearer ' + token } : {}) },
        body: JSON.stringify(form)
      })
        .then(res => {
          if (res.ok) {
            setForm({ event_name: '', event_date: '', location: '' });
            if (onCreate) onCreate();
          } else {
            console.error('Create event failed', res.status);
          }
        });
    };
    return (
      <form onSubmit={onSubmit} style={{ marginBottom: '1em' }}>
        <input name="event_name" value={form.event_name} onChange={onChange} placeholder="Event Name" required />
        <input name="event_date" value={form.event_date} onChange={onChange} placeholder="Date (YYYY-MM-DD)" required />
        <input name="location" value={form.location} onChange={onChange} placeholder="Location" required />
        <button type="submit" style={{marginLeft: '8px'}}>Create Event</button>
      </form>
    );
  }

  return (
    <div style={padDivTop}>
      {/* Debug banner - visible in UI to help diagnose admin flag/token */}
      <div style={{background:'#fff3cd', border:'1px solid #ffeeba', padding:'8px', marginBottom:'8px'}}>
        <strong>Debug:</strong>
        <div>isAdmin prop check: {String(props.username === 'admin')}</div>
        <div>isAdmin localStorage: {String(localStorage.getItem('isAdmin'))}</div>
        <div>token present: {localStorage.getItem('jwtToken') ? 'yes' : 'no'}</div>
      </div>
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
                    <th></th>
                  </tr>
                </thead>
                <tbody>
                  {events.length === 0 ? (
                    <tr><td colSpan={isAdmin ? 5 : 4}>No events found</td></tr>
                  ) : (
                    events.map((event, idx) => {
                      const isRegistered = userRegistrations.some(reg => reg.eventId === event.id);
                      return (
                        <tr key={idx}>
                          <td>{event.event_name || event.eventName}</td>
                          <td>{event.event_date || event.eventDate}</td>
                          <td>{event.location}</td>
                          {isAdmin && (
                                <td>
                                  <button onClick={() => handleDeleteEventAdmin(event.id)} style={{color: 'red'}}>Delete</button>
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