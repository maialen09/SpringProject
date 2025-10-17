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

  // Simulated events list
  const events = [
    { event_name: "React Conference", event_date: "2025-11-10", event_location: "Madrid" },
    { event_name: "Spring Boot Meetup", event_date: "2025-12-05", event_location: "Bilbao" },
    { event_name: "Docker Day", event_date: "2026-01-20", event_location: "Barcelona" }
  ];

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
              <table>
                <thead>
                  <tr>
                    <th>Event Name</th>
                    <th>Date</th>
                    <th>Location</th>
                  </tr>
                </thead>
                <tbody>
                  {events.map((event, idx) => (
                    <tr key={idx}>
                      <td>{event.event_name}</td>
                      <td>{event.event_date}</td>
                      <td>{event.event_location}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}