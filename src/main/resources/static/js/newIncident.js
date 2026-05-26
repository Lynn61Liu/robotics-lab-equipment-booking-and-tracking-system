console.log("this is the new incident js");
const url = window.location.pathname;
const parts = url.split("/");
const bookingID = parts[parts.length - 1];
var bookingData;
var equipmentDetails;

document.addEventListener('DOMContentLoaded', function () {

    // Function to fetch booking details
    function fetchBookingDetails() {
        console.log("fetch by id=", bookingID);
        return fetch(`/booking/${bookingID}`, { method: 'GET' })
            .then(response => response.json())
            .then((data) => {
                
                sysID = data.equipmentid;
                bookingData = data;
                return data;
            });
    }


    // Function to fetch equipment details
    function fetchEquipmentDetails(equipmentID) {
        return fetch(`/equipment/${equipmentID}`, { method: 'GET' })
            .then(response => response.json()).then((data) => {
                
                equipmentDetails=data
               
                renderPage(equipmentDetails, bookingData);
            });
    }


    let bookingDetails;
    Promise.all([fetchBookingDetails()])
        .then(([details]) => {
            bookingDetails = details;
            document.getElementById('bookedBy').textContent = bookingDetails.bookedBy;
            document.getElementById('startTime').textContent = bookingDetails.startTime;
            document.getElementById('endTime').textContent = bookingDetails.endTime;

            // Assuming bookingDetails contains equipmentID
            const equipmentID = bookingDetails.equipmentid;

            // Fetch equipment details based on equipmentID
            fetchEquipmentDetails(equipmentID);
        })
});

function renderPage(equipmentDetails, bookingData) {
    console.log(bookingData);
    console.log(equipmentDetails);
   

    if(equipmentDetails.imageURL){
        document.getElementById('incident-img').src=equipmentDetails.imageURL;
    }
    else{
        document.getElementById('incident-img').src="/img/uoalogo.png"
    }
    document.getElementById('eq-name').textContent = equipmentDetails.description;
    document.getElementById('uoaID').textContent = equipmentDetails.uoaID;
    document.getElementById('brand').textContent = equipmentDetails.brand;
    document.getElementById('model').textContent = equipmentDetails.model;
    document.getElementById('snum').textContent = equipmentDetails.serialNumber;
    document.getElementById('maxdays').textContent = equipmentDetails.maxUseDay;
    document.getElementById('status').textContent = bookingData.status;
    document.getElementById('dueday').textContent = bookingData.endTime;
    document.getElementById('bookingID').textContent = bookingData.id;
    document.getElementById('bookedBy').textContent = bookingData.bookedBy;
    document.getElementById('startTime').textContent = bookingData.startTime;
    document.getElementById('endTime').textContent = bookingData.endTime;

    var submitBtn= document.getElementById('submit');
    submitBtn.addEventListener("click",e=>{
      const content= document.getElementById('incident-description').value;
      console.log(content);
 // sendDataToBackend(formDataJson);
let formDataJson = {};
const today = moment().add(0, 'days').format('YYYY-MM-DD')  
formDataJson["description"] = content;
formDataJson["booking_id"] = bookingID;
formDataJson["incident_status"] = "Pending";
// formDataJson["created_date"] = today;
console.log(formDataJson);
 const url = "/AddIncident";
 fetch(url, {
   method: "POST",
   headers: {
     "Content-Type": "application/json",
   },
   body: JSON.stringify(formDataJson),
 })
   .then((response) => response.json())
   .then((data) => {
     console.log("Response from server:", data);
     if (data.status === "success") {
        console.log("success");
        changeBookingStatus(bookingID);
        //http://localhost:8080/allIncidents
        window.location.href = 'http://localhost:8080/allBookings';
   
        
     }
   })
   .catch((error) => {
     console.error("Error:", error);
   });


    })

}
changeBookingStatus = (bookingId)=>{
let incidentStatus;
console.log(equipmentDetails.pickupAllowed);
if(equipmentDetails.pickupAllowed){
    incidentStatus="Lent Incident"
}else{incidentStatus="Incident"}

// alert(url);
fetch(`/editStatusBooking?bookingID=${bookingId}&status=${incidentStatus}`, {
    method: "PUT",
    headers: {
        "Content-Type": "application/json",
    },
})
    .then((response) => {
        if (response.ok) {
            console.log("Incident approved successfully");
        } else {
            console.error("Failed to approve Incident");
        }
    })
    .then(data=> {
        console.log(data);})
    .catch((error) => {
        console.error("Error:", error);
    });

}



