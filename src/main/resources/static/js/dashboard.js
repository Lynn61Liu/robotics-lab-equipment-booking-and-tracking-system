console.log("this is dashboard js");

var equipmentData;

const buttons = document.querySelectorAll('.tab-btn');
const articles = document.querySelectorAll(".content");

// tab 
buttons.forEach(button => {
  button.addEventListener('click', () => {
    console.log("BTN click:", button.dataset.id);
    buttons.forEach(b => b.classList.remove('text-or', 'text-grn', 'text-pu'));
    console.log("remove all style");
    if (button.dataset.id === 'approve') {
      button.classList.add('text-or');
    } else if (button.dataset.id === 'pickup') {
      button.classList.add('text-grn');
    } else if (button.dataset.id === 'return') {
      button.classList.add('text-pu');
    }
    console.log("add active style");


    // hide other articles
    articles.forEach(function (article) {
      article.classList.remove("tab-show");
    });
    console.log("close all");
    const element = document.getElementById(button.dataset.id);
    element.classList.add("tab-show");
    console.log("show  one");

  });
});


// fetch Data from backend
var dashboardData;


function getBookingData() {
  fetch("/getallbookings", {
      method: "GET",
      headers: {
          "Content-Type": "application/json",
      },
  })
      .then((response) => response.json())
      .then((data) => {
        console.log(data);
        dashboardData=data;

        // now filter the data into the three sections
        processData();
      })
      .catch((error) => {
          console.error("Error:", error);
      });
}
getBookingData();

function processData() {
    // console.log("Processed data from dashboardData:", dashboardData);

    // Filtering
    const approvalBookings = dashboardData.filter(booking => booking.status.toLowerCase().replace(/\s+/g, '') === "requested");
    // counting
   
    const approvalRow = approvalBookings.length;
    document.querySelector("#approve-num").textContent=approvalRow;
    const pickUpBookings = dashboardData.filter(booking => booking.status.toLowerCase().replace(/\s+/g, '') === "waitingforpickup");
    // counting

    // console.log(pickUpBookings);
    const pickUpRow = pickUpBookings.length;
    // console.log('table-pickup Row have:', pickUpRow);
    document.querySelector("#pickup-num").textContent=pickUpRow;

    const returnBookings = dashboardData.filter(booking => booking.status.toLowerCase().replace(/\s+/g, '') === "lent"
         || booking.status.toLowerCase().replace(/\s+/g, '') === "lentincident");
    // counting
    console.log(returnBookings);
    const returnRow = returnBookings.length;
    document.querySelector("#returned-num").textContent = returnRow;

    // Data population
    populateApprovalTable(approvalBookings);
    populatePickUpTable(pickUpBookings);
    populateReturnTable(returnBookings);

    //reject 
var rejectBtns = document.querySelectorAll(".action-reject");
// console.log("rejectBtns",rejectBtns);
rejectBtns.forEach(function (btn) {
    btn.addEventListener("click", function () {
        var bookingId = this.getAttribute("data-booking-id");
        console.log(bookingId);

var messageDiv = document.createElement("div");
messageDiv.textContent = "Are you sure to Reject this booking which ID: " + bookingId;
var textareaElement = document.createElement("textarea");
textareaElement.setAttribute("placeholder", "Enter your reason");
textareaElement.classList.add("dialog-input");
var modalBody = document.querySelector("#RejecteDialog .modal-body");
modalBody.textContent="";
modalBody.appendChild(messageDiv);
modalBody.appendChild(textareaElement);
    });
});


//cancel when no come to pickup
var cancelBtns = document.querySelectorAll(".action-cancel");
cancelBtns.forEach(function (btn) {
    btn.addEventListener("click", function () {
      console.log("cancel are clicked",btn);
        var bookingId = this.getAttribute("data-booking-id");

document.querySelector("#cancelDialog .modal-body").textContent = "Are you sure to Cancel this booking and ID: " + bookingId;
    });
});

}

function populateApprovalTable(approvalBookings) {
    var approvalTableBody = document.getElementById("approval-table-body");

    // approvalTableBody.innerHTML = "";

    approvalBookings.forEach(function (booking) {
        var row = approvalTableBody.insertRow();
        var bookingDate = row.insertCell(0);
        var bookingID = row.insertCell(1);
        var equipmentName = row.insertCell(2);
        var uoaID = row.insertCell(3);
        var status = row.insertCell(4);
        var applicant = row.insertCell(5);
        var startDay = row.insertCell(6);
        var returnDay = row.insertCell(7);
        var action = row.insertCell(8);

        // Need to access equipment details based on booking ID
        var equipmentID = booking.equipmentid;

        fetch(`/equipment/${equipmentID}`)
            .then(response => response.json())
            .then(equipment => {
                equipmentData=equipment;
                equipmentName.textContent = equipment.description;
                //  uoaID.textContent = equipment.systemID;

                const systemIDLink = document.createElement('a');
                systemIDLink.href=`http://localhost:8080/equipmentDetail?sysID=${equipment.systemID}`
             
                const systemIDSpan = document.createElement('span');
                systemIDSpan.className="equipmentView"
                systemIDSpan.textContent = equipment.systemID;
                systemIDLink.appendChild(systemIDSpan);
                uoaID.appendChild(systemIDLink);

            })
            .catch(error => {
                console.error("Error fetching equipment details:", error);
            });

        bookingDate.textContent = booking.startTime;


        //bookingID.textContent = booking.id;
        const bookingIdLink = document.createElement('a');
        bookingIdLink.href=`http://localhost:8080/viewBooking/${booking.id}`
 
        const bookingSpan = document.createElement('span');
        bookingSpan.className="BookingView"
        bookingSpan.textContent = booking.id;
        bookingIdLink.appendChild(bookingSpan);
        bookingID.appendChild(bookingIdLink);
      

        applicant.textContent = booking.bookedBy;
        // startDay.textContent = calculateDay(booking.startTime); // calculate from startTime
        startDay.textContent = booking.startTime;
        returnDay.textContent = booking.endTime;

        const styleSpan = document.createElement('span');
        styleSpan.className="status-style style-requested"
        styleSpan.textContent = booking.status;
        status.appendChild(styleSpan);


        // Adding buttons
        const buttonsOutline = document.createElement('div');
        buttonsOutline.className = 'table-action';

        const approveButton = document.createElement('div');
        approveButton.className = 'action-btn action-approve';
        approveButton.id = 'approveButton';
        approveButton.textContent = 'Approve';
        approveButton.addEventListener('click', async function() {
            var status;
        //     console.log(equipmentData);
        //   alert(equipmentData.pickupAllowed);

        await fetch(`/equipment/${equipmentID}`)
.then(response => response.json())
.then(equipment => {
   
    if(equipment.pickupAllowed){
        // alert(" edit to pickupAllowed");
         status = 'Waiting For Pickup';
    }else{
        // alert(" edit to Approved");
        status = 'Approved';
    }

    //  approveEquipment(booking.id, status);
     updateStatus(booking.id, status);
})
.catch(error => {
    console.error("Error fetching equipment details:", error);
});

        });

        const rejectButton = document.createElement('div');
        rejectButton.className = 'action-btn action-reject';
        rejectButton.id = 'rejectButton';
        rejectButton.textContent = 'Reject';
        // rejectButton.setAttribute( "data-bs-toggle","modal");
        // rejectButton.setAttribute( "data-bs-target","#RejecteDialog");
        // rejectButton.setAttribute( "data-booking-id",booking.id);
        rejectButton.addEventListener('click', function() {
            alert(booking.id)
            updateStatus(booking.id, "rejected");
        })
        
        buttonsOutline.appendChild(approveButton);
        buttonsOutline.appendChild(rejectButton);
        action.appendChild(buttonsOutline);
  
        
    });




}

function populatePickUpTable(pickUpBookings) {
    var pickUpTableBody = document.getElementById("pick-up-table-body");

    // pickUpTableBody.innerHTML = "";

    pickUpBookings.forEach(function (booking) {
        var row = pickUpTableBody.insertRow();
        var bookingDate = row.insertCell(0);
        var bookingID = row.insertCell(1);
        var equipmentName = row.insertCell(2);
        var uoaID = row.insertCell(3);
        var building = row.insertCell(4);
        var room = row.insertCell(5);
        var position = row.insertCell(6);
        var status = row.insertCell(7);
        var applicant = row.insertCell(8);
        var startDay = row.insertCell(9);
        var action = row.insertCell(10);

        // Need to access equipment details based on booking ID
        var equipmentID = booking.equipmentid;

        fetch(`/equipment/${equipmentID}`)
            .then(response => response.json())
            .then(equipment => {
                // console.log(equipment);
                equipmentName.textContent = equipment.description;
               // uoaID.textContent = equipment.systemID;
               const systemIDLink = document.createElement('a');
               systemIDLink.href=`http://localhost:8080/equipmentDetail?sysID=${equipment.systemID}`
            
               const systemIDSpan = document.createElement('span');
               systemIDSpan.className="equipmentView"
               systemIDSpan.textContent = equipment.systemID;
               systemIDLink.appendChild(systemIDSpan);
               uoaID.appendChild(systemIDLink);
                building.textContent = equipment.building;
                room.textContent = equipment.room;
                position.textContent = equipment.position;
            })
            .catch(error => {
                console.error("Error fetching equipment details:", error);
            });

        bookingDate.textContent = booking.startTime;
       // bookingID.textContent = booking.id;
       const bookingIdLink = document.createElement('a');
       bookingIdLink.href=`http://localhost:8080/viewBooking/${booking.id}`

       const bookingSpan = document.createElement('span');
       bookingSpan.className="BookingView"
       bookingSpan.textContent = booking.id;
       bookingIdLink.appendChild(bookingSpan);
       bookingID.appendChild(bookingIdLink);
        // status.textContent = booking.status;
        // style waiting pickup
        const styleSpan = document.createElement('span');
        styleSpan.className="status-style style-pickup"
        styleSpan.textContent = booking.status;
        status.appendChild(styleSpan);


        applicant.textContent = booking.bookedBy;
        startDay.textContent = calculateDay(booking.startTime); // calculate from startTime

        // Adding buttons
        const cancelButton = document.createElement('div');
        cancelButton.className = 'action-btn action-cancel';
        // cancelButton.id = 'cancelButton';
        cancelButton.textContent = 'Cancel';
        cancelButton.addEventListener('click', function() {
        //  alert("cancel btn");
            updateStatus(booking.id, "cancelled");
       });
       
        // cancelButton.setAttribute( "data-bs-toggle","modal");
        // cancelButton.setAttribute( "data-bs-target","#cancelDialog");
        // cancelButton.setAttribute( "data-booking-id",booking.id);

        const collectedButton = document.createElement('div');
        collectedButton.className = 'action-btn action-Lent';
        collectedButton.id = 'collectedButton';
        collectedButton.textContent = 'Lent';
        collectedButton.addEventListener('click', function() {
            // alert("lent btn");
               updateStatus(booking.id, "lent");
          });


        const buttonsOutline = document.createElement('div');
        buttonsOutline.className = 'table-action';

        buttonsOutline.appendChild(cancelButton);
        buttonsOutline.appendChild(collectedButton);
        action.appendChild(buttonsOutline);
    });
}

function populateReturnTable(returnBookings) {
    var returnTableBody = document.getElementById("return-table-body");

    // requestedTableBody.innerHTML = "";

    returnBookings.forEach(async function (booking) {
        var row = returnTableBody.insertRow();
        var bookingDate = row.insertCell(0);
        var bookingID = row.insertCell(1);
        var equipmentName = row.insertCell(2);
        var uoaID = row.insertCell(3);
        var status = row.insertCell(4);
        var applicant = row.insertCell(5);
        var returnDay = row.insertCell(6);
        var incidentReport = row.insertCell(7);
        var action = row.insertCell(8);

        // Need to access equipment details based on booking ID
        var equipmentID = booking.equipmentid;

        fetch(`/equipment/${equipmentID}`)
            .then(response => response.json())
            .then(equipment => {
                equipmentName.textContent = equipment.description;
               // uoaID.textContent = equipment.uoaID;
               const systemIDLink = document.createElement('a');
               systemIDLink.href=`http://localhost:8080/equipmentDetail?sysID=${equipment.systemID}`
            
               const systemIDSpan = document.createElement('span');
               systemIDSpan.className="equipmentView"
               systemIDSpan.textContent = equipment.systemID;
               systemIDLink.appendChild(systemIDSpan);
               uoaID.appendChild(systemIDLink);
            })
            .catch(error => {
                console.error("Error fetching equipment details:", error);
            });

        bookingDate.textContent = booking.startTime;
      //  bookingID.textContent = booking.id;
      const bookingIdLink = document.createElement('a');
      bookingIdLink.href=`http://localhost:8080/viewBooking/${booking.id}`

      const bookingSpan = document.createElement('span');
      bookingSpan.className="BookingView"
      bookingSpan.textContent = booking.id;
      bookingIdLink.appendChild(bookingSpan);
      bookingID.appendChild(bookingIdLink);
   
        const styleSpan = document.createElement('span');
        if(booking.status.toLowerCase() === "lent"){
            styleSpan.className="status-style style-lent"
        }
        if(booking.status.toLowerCase().replace(/\s+/g, '') === "lentincident"){
            styleSpan.className="status-style style-lent";
              // Retrieving the incident ID

              console.log("==========================");
        await fetch(`/getIncidentIDWithBookingID/${booking.id}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Incident not found');
                
            }
            return response.text();
           
        })
        .then(incidentID => {
            const incidentIDLink = document.createElement('a');
            incidentIDLink.href=`http://localhost:8080/incidentDetail/${incidentID}`;
            incidentIDLink.textContent=incidentID;
            incidentReport.appendChild(incidentIDLink);

        })
        .catch(error => {
            console.error('Error retrieving incident ID:', error.message);
            // alert("Incident not fund");
        
        });
        // console.log("000000000000000");


    

        }
       
        styleSpan.textContent = booking.status;
        status.appendChild(styleSpan);
        applicant.textContent = booking.bookedBy;
        returnDay.textContent = booking.endTime;

      


        // Adding buttons
        const safelyReturnedButton = document.createElement('div');
        // safelyReturnedButton.type = 'button';
        safelyReturnedButton.className = 'action-btn action-s-returned';
        safelyReturnedButton.id = 'safelyReturnedButton';
        safelyReturnedButton.textContent = "safely return";
        safelyReturnedButton.addEventListener('click', function() {
            // alert("safelyreturn")
            updateStatus(booking.id, "Safely Return");
       });
        

        const faultyReturnButton = document.createElement('div');
        // faultyReturnButton.type = 'button';
        faultyReturnButton.className = 'action-btn action-f-returned';
        faultyReturnButton.id = 'faultyReturnButton';
        faultyReturnButton.textContent = 'Faulty Return';
        faultyReturnButton.addEventListener('click', function() {
            // alert("Faulty r");
            updateStatus(booking.id, "Faulty Return");
       });


        const buttonsOutline = document.createElement('div');
        buttonsOutline.className = 'table-action';

        buttonsOutline.appendChild(safelyReturnedButton);
        buttonsOutline.appendChild(faultyReturnButton);
        action.appendChild(buttonsOutline);

    });
}


function calculateDay(timestamp) {
    const date = new Date(timestamp * 1000);
    const monthNames = ["January", "February", "March", "April", "May", "June", "July", "August",
        "September", "October", "November", "December"];
    const month = monthNames[date.getMonth()];
    const day = date.getDate();

    return `${month}, ${day}`;
}

function updateStatus(bookingID, status) {
    const url=`/editStatusBooking?bookingID=${bookingID}&status=${status}`;
    // alert(url);
    fetch(`/editStatusBooking?bookingID=${bookingID}&status=${status}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
    })
        .then((response) => {
            if (response.ok) {
                console.log("Equipment approved successfully");
            } else {
                console.error("Failed to approve equipment");
            }
        })
        .then(data=> {
            console.log(data);
            window.location.reload();
        })
        .catch((error) => {
            console.error("Error:", error);
        });
}


// Get the modal element
var modal = document.getElementById("myModal");

// Function to open the Add Staff modal
function openAddStaffModal() {
    var addStaffModal = document.getElementById("addStaffModal");
    addStaffModal.style.display = "flex";
}

// Function to close the Add Staff modal
function closeAddStaffModal() {
    var addStaffModal = document.getElementById("addStaffModal");
    addStaffModal.style.display = "none";
}

// Function to open the Delete Staff modal
function openDeleteStaffModal() {
    var deleteStaffModal = document.getElementById("deleteStaffModal");
    deleteStaffModal.style.display = "flex";
}

// Function to close the Delete Staff modal
function closeDeleteStaffModal() {
    var deleteStaffModal = document.getElementById("deleteStaffModal");
    deleteStaffModal.style.display = "none";
}

// Function to add staff
function addStaff() {
    var email = document.getElementById("addStaffEmail").value;
    var upi = document.getElementById("addStaffUPI").value; // Get UPI value
    var name = document.getElementById("addStaffName").value; // Get Name value
    var userProfile = "staff"; // Get User Profile value
    var phone = document.getElementById("addStaffPhone").value; // Get Phone value

    if (!email) {
        // Email is null, display a prompt and return
        alert("Email is required.");
        return;
    }

    if (!upi) {
        // UPI is null, display a prompt and return
        alert("UPI is required.");
        return;
    }

    // Send a POST request to your backend with the form data
    fetch('/addStaff', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            email: email,
            upi: upi,
            name: name,
            user_profile: userProfile,
            phone: phone
        })
    })
        .then(response => response.json())
        .then(data => {
            // Check the response from the server
            if (data.success) {
                alert("User with email " + email + " has been designated as staff.");
                closeAddStaffModal(); // Close the Add Staff modal after adding staff
            } else {
                alert("Failed to designate the user as staff.");
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert("An error occurred while adding staff.");
        });
}


// Function to delete staff
function deleteStaff() {
    var email = document.getElementById("deleteStaffEmail").value;


    // Send a POST request to your backend to delete staff with the given email
    fetch('/deleteStaff', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            email: email,
        })
    })
        .then(response => response.json())
        .then(data => {
            // Check the response from the server
            if (data.success) {
                alert("User with email " + email + " has been deleted as staff.");
                closeDeleteStaffModal(); // Close the Delete Staff modal after deleting staff
            } else {
                alert("Failed to delete the user as staff.");
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert("An error occurred while deleting staff.");
        });
}


