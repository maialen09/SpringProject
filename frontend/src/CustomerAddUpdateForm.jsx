export function CustomerAddUpdateForm(params) {
    // Check if the selected customer is the logged-in user
    const isOwnAccount = params.formObject.id >= 0 && params.formObject.id == params.currentCustomerId;
    
    return (
      <div className="boxed">
        <div>
          <h4>{params.mode}</h4>
          {isOwnAccount && (
            <p style={{color: 'red', fontSize: '12px', marginTop: '4px'}}>
              ⚠️ You are viewing your own account
            </p>
          )}
        </div>
        <form >
          <table id="customer-add-update" >
            <tbody>
              <tr>
                <td className={'label'} >Name:</td>
                <td><input
                  type="text"
                  name="name"
                  onChange={(e) => params.handleInputChange(e)}
                  value={params.formObject.name}
                  placeholder="Customer Name"
                  required /></td>
              </tr>
              <tr>
                <td className={'label'} >Email:</td>
                <td><input
                  type="email"
                  name="email"
                  onChange={(e) => params.handleInputChange(e)}
                  value={params.formObject.email}
                  placeholder="name@company.com" /></td>
              </tr>
              <tr>
                <td className={'label'} >Pass:</td>
                <td><input
                  type="text"
                  name="password"
                  onChange={(e) => params.handleInputChange(e)}
                  value={params.formObject.password}
                  placeholder="password" /></td>
              </tr>
              <tr className="button-bar">
                <td colSpan="2">
                  <input 
                    type="button" 
                    value="Delete" 
                    onClick={params.onDeleteClick}
                    disabled={isOwnAccount}
                    style={isOwnAccount ? {backgroundColor: '#ccc', cursor: 'not-allowed'} : {}}
                    title={isOwnAccount ? "You cannot delete your own account" : "Delete this customer"}
                  />
                  <input type="button" value="Save" onClick={params.onSaveClick} />
                  <input type="button" value="Cancel" onClick={params.onCancelClick} />
                </td>
              </tr>
            </tbody>
          </table>
        </form>
      </div>
    );
  }