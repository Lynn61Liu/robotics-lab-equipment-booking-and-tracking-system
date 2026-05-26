const url = window.location.pathname;
const parts = url.split("/");
const incidentId = parts[parts.length - 1];
var bookingItem;
var equipmentItem;
var incidentItem;
var userRole;
var userUPI;
var descriptionBox;


document.addEventListener('DOMContentLoaded', async function () {
    function fetchRole() {
        return fetch("/getRole", {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            },
        })
            .then(response => response.text())
            .then(role => {
                console.log(role);
                userRole = role;
            })
            .catch(error => {
                console.error("Error:", error);
            });
    }
    await fetchRole();
    //get upi
    function fetchUPI() {
        return fetch("/getUPI", {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            },
        })
            .then(response => response.text())
            .then(data => {
                console.log(data);
                userUPI = data;
            })
            .catch(error => {
                console.error("Error:", error);
            });
    }
    await fetchUPI();
    // Function to fetch booking details
    function fetchBookingDetails(bookingID) {
        return fetch(`/booking/${bookingID}`, { method: 'GET' })
            .then(response => response.json());
    }
    // Function to fetch equipment details
    function fetchEquipmentDetails(equipmentID) {
        return fetch(`/equipment/${equipmentID}`, { method: 'GET' })
            .then(response => response.json());
    }
    function fetchIncidentDetails(incidentId) {
        return fetch(`/incidentDetails/${incidentId}`, { method: 'GET' })
            .then(response => response.json())
            .then(incidentDetail => {
                console.log("Incident data", incidentDetail);
                incidentItem = incidentDetail;
                descriptionBox = document.getElementById('description');
                descriptionBox.textContent = incidentItem.description;
                const incidentStatusSpan = document.getElementById('incidentStatus');
                incidentStatusSpan.textContent = incidentItem.incident_status;
                // console.log("add status style");

                incidentStatusSpan.classList.add("status-style", "style-" + incidentItem.incident_status.replace(/\s+/g, '').toLowerCase());
                const selectedIncidentId = incidentItem.incidentId;
                document.getElementById('bookingID').textContent = incidentItem.booking_id;
                document.getElementById('incidentID').textContent = incidentItem.incidentId;


                //fetch booking & equipment
                // console.log(incidentDetail.booking_id);
                const bookingID = incidentItem.booking_id;
                return fetchBookingDetails(bookingID)
                    .then(bookingDetails => {
                        bookingItem = bookingDetails;

                        // Do something with bookingDetails
                        document.getElementById('bookedBy').textContent = bookingDetails.bookedBy;
                        document.getElementById('startTime').textContent = bookingDetails.startTime;
                        document.getElementById('endTime').textContent = bookingDetails.endTime;
                        document.getElementById('dueday').textContent = bookingDetails.endTime;
                        document.getElementById('status').textContent = bookingDetails.status;

                        // Assuming bookingDetails contains equipmentID
                        const equipmentID = bookingDetails.equipmentid;

                        // Fetch equipment details based on equipmentID
                        return fetchEquipmentDetails(equipmentID);
                    })
                    .then(equipmentDetails => {
                        equipmentItem = equipmentDetails;
                        // Do something with equipmentDetails
                        if (equipmentDetails.imageURL) {
                            document.getElementById('incident-img').src = equipmentDetails.imageURL;
                        } else {
                            document.getElementById('incident-img').src = "/img/uoalogo.png"
                        }
                        document.getElementById('eq-name').textContent = equipmentDetails.description;
                        document.getElementById('uoaID').textContent = equipmentDetails.uoaID;
                        document.getElementById('brand').textContent = equipmentDetails.brand;
                        document.getElementById('model').textContent = equipmentDetails.model;
                        document.getElementById('snum').textContent = equipmentDetails.serialNumber;
                        document.getElementById('maxdays').textContent = equipmentDetails.maxUseDay;
                        renderAcitonBtn();

                    })
                    .catch(error => {
                        console.error('Error:', error);
                    });
            });
    }

    Promise.all([fetchIncidentDetails(incidentId)])
        .catch(error => {
            console.error('Error:', error);
        });

    function renderAcitonBtn() {
        const btnDiv = document.getElementById('actionButtons');
        const cancelBtn = document.createElement('button');
        cancelBtn.type = "button";
        cancelBtn.className = 'btn btn-primary my-primary';
        // cancelBtn.textContent
        const editCancelBtn = document.createElement('button');
        editCancelBtn.type = "button";
        editCancelBtn.className = 'btn btn-primary my-primary';
        editCancelBtn.id = "editCancelBtn";
        editCancelBtn.textContent = "Cancel";

        const editBtn = document.createElement('button');
        editBtn.type = "button";
        editBtn.className = 'btn btn-primary my-primary';

        const WithdrawBtn = document.createElement('button');
        WithdrawBtn.type = "button";
        WithdrawBtn.className = 'btn btn-primary my-primary withdraw-btn';
        WithdrawBtn.textContent = "Withdraw";

        const submitBtn = document.createElement('button');
        submitBtn.type = "button";
        submitBtn.className = 'btn btn-primary my-primary';
        // Submit Button
        submitBtn.id = "submitBtn";
        submitBtn.textContent = "Submit";
        submitBtn.addEventListener("click", (e) => {
            // console.log("click for save and submit ");
            const today = moment().format('YYYY-MM-DD');
            const formData = new FormData();

            let formDataJson = {};
            formDataJson['booking_id'] = incidentItem.booking_id;
            formDataJson['description'] = descriptionBox.value;
            formDataJson['incident_status'] = "Pending";
            formDataJson['created_date'] = today;

            formData.forEach((value, key) => {
                formDataJson[key] = value;
            });
            var api_url = '/incidentUpdate/' + incidentItem.incidentId;
            const url = api_url;
            fetch(url, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formDataJson),
            })
                .then(response => response.json())
                .then(data => {
                    // console.log('Response from server:', data);
                    window.location.reload();
                })
                .catch(error => {
                    console.error('Error:', error);
                    window.location.reload();
                });
        });

        //Cancel Button 
        WithdrawBtn.id = "WithdrawBtn";
        // cancelBtn.textContent = "Cancel";
        WithdrawBtn.addEventListener("click", (e) => {
            fetch("/CancelledIncident/" + incidentItem.incidentId, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                },
            })
                .then((response) => response.json())
                .then((data) => {
                    // Handle the response from the backend
                    // console.log("cancelBtn");
                    window.location.reload();

                })
                .catch((error) => {
                    console.error("Error:", error);
                    window.location.reload();
                });
        });

        //Edit Button
        editBtn.id = "editBtn";
        editBtn.textContent = "Edit";
        let oldValue;
        editBtn.addEventListener("click", (e) => {
            descriptionBox.disabled = false;
            btnDiv.innerHTML = "";
            btnDiv.appendChild(editCancelBtn);
            btnDiv.appendChild(submitBtn);
            oldValue = descriptionBox.value;
        });
        //editCancel Button
        editCancelBtn.addEventListener("click", (e) => {

            window.location.reload();
        });


        // change the userRole in here
          userRole = "ROLE_STUDENT";
            //  userRole = "ROLE_ADMIN";
        //  userRole = "ROLE_STAFF";
        console.log("userRole =", userRole);
        function studentRoleBtns() {

            if (incidentItem.incident_status == "new" || incidentItem.incident_status == null) {
                btnDiv.innerHTML = "";
                //show submit
                btnDiv.appendChild(cancelBtn);
                btnDiv.appendChild(submitBtn);
            } else if (incidentItem.incident_status == "Pending") {
                descriptionBox.disabled = true;
                btnDiv.innerHTML = "";
                btnDiv.appendChild(WithdrawBtn);
                btnDiv.appendChild(editBtn);

                // btnDiv.appendChild(requestMoreBtn);

            } else if (incidentItem.incident_status == "More Info Requested") {
                btnDiv.innerHTML = "";
                btnDiv.appendChild(submitBtn);
                // btnDiv.appendChild(backBtn);
            } else if (incidentItem.incident_status == "Closed" || incidentItem.incident_status == "Cancelled") {
                descriptionBox.disabled = true;
                btnDiv.innerHTML = "";
                // btnDiv.appendChild(backBtn);
            }

        }

        if (userRole == "ROLE_STUDENT") {
            console.log("studnet actions", userRole);
            studentRoleBtns();

        } else if (userRole == "ROLE_ADMIN") {
            console.log("admin actions");
            //admin actions
            const requestMoreBtn = document.createElement('button');
            requestMoreBtn.type = "button";
            requestMoreBtn.className = 'btn btn-primary my-primary';

            //Request More Button
            requestMoreBtn.id = "requestMoreBtn";
            requestMoreBtn.textContent = "Request More";
            requestMoreBtn.addEventListener("click", (e) => {
                fetch("/moreInfoIncident/" + incidentItem.incidentId, {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json",
                    },
                })
                    // .then((response) => response.json())
                    .then((data) => {

                        window.location.reload();
                    })
                    .catch((error) => {
                        console.error("Error:", error);
                    });
            });


            //del Button
            const delBtn = document.createElement('button');
            delBtn.type = "button";
            delBtn.className = 'btn btn-primary my-primary';
            delBtn.id = "delBtn";
            delBtn.textContent = "Delete";
            delBtn.addEventListener("click", (e) => {
                fetch("/deleteIncident/" + incidentItem.incidentId, {
                    method: "DELETE",
                    headers: {
                        "Content-Type": "application/json",
                    },
                })
                    // .then((response) => response.json())
                    .then((data) => {
                        // Handle the response from the backend
                        window.location.href = "http://localhost:8080/allIncidents";
                    })
                    .catch((error) => {
                        console.error("Error:", error);
                    });
            });


            const confirmBtn = document.createElement('button');
            confirmBtn.type = "button";
            confirmBtn.className = 'btn btn-primary my-primary';
            confirmBtn.id = "closeBtn";
            confirmBtn.textContent = "Confirm";
            confirmBtn.addEventListener("click", (e) => {
                fetch("/closedIncident/" + incidentItem.incidentId, {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json",
                    },
                })
                    .then((response) => response.json())
                    .then((data) => {
                        // Handle the response from the backend
                        // console.log(data);
                        window.location.reload();
                    })
                    .catch((error) => {
                        console.error("Error:", error);
                        window.location.reload();
                    });
            });
            if (incidentItem.incident_status == "Pending") {
                descriptionBox.disabled = true;
                btnDiv.innerHTML = "";

                btnDiv.appendChild(requestMoreBtn);
                btnDiv.appendChild(confirmBtn);

            } else if (incidentItem.incident_status == "More Info Requested") {
                btnDiv.innerHTML = "";
                btnDiv.appendChild(confirmBtn);
                descriptionBox.disabled = true;
                document.getElementById("tips").classList.remove("hidden");

                // btnDiv.appendChild(submitBtn);

            } else if (incidentItem.incident_status == "Closed" || incidentItem.incident_status == "Cancelled") {
                descriptionBox.disabled = true;
                btnDiv.innerHTML = "";
                btnDiv.appendChild(delBtn);
            }



        }
        else if (userRole == "ROLE_STAFF") {
            console.log("staff upi=", userUPI);
            if (userUPI == bookingItem.bookedBy) {
                console.log(" staff + book by me = student");
                studentRoleBtns();
            }
            else {
                descriptionBox.disabled = true;
                console.log(" staff + book by obter=  view ");
            }

        }



        const backBtn = document.createElement('button');
        backBtn.type = "button";
        backBtn.className = 'btn btn-primary my-primary';
        backBtn.id = "backBtn";
        backBtn.textContent = "Back";
        backBtn.addEventListener("click", (e) => {
            window.location.href = "/allIncidents";
        });
        btnDiv.appendChild(backBtn);

    }
});
