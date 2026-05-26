console.log("BOOKING VIEW JS");
var userRole;
var userUPI;
var backeBtn;
var BookingData;
var equipmentData;
var myBookingID;
var myIncidentID;

async function fetchEquipmentDetails(sysID) {
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
    const response = await fetch(`/equipmentIndividual?systemID=${sysID}`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
        },
    });

    if (!response.ok) {
        throw new Error("Network response was not ok");
    }

    const data = await response.json();
    //   console.log("Data from equipmentIndividual:", data);
    return data;
}

async function fetchBookingDetails(bookingID) {
    // console.log(bookingID);
    // console.log(typeof bookingID);
    const response = await fetch(`/bookingDetails/${bookingID}`, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
        },
    });

    if (!response.ok) {
        throw new Error("Network response was not ok");
    }

    const data = await response.json();
    //   console.log("Data from backend:", data);
    return data;
}

function UpdateStatusbyname(endpoint) {
    fetch(endpoint, {
        method: "PUT",
    })
        .then(response => { response.json(); location.reload(); })
        .then(data => {
            console.log(data);
            if (data.status === "success") {
                window.location.href = `http://localhost:8080//viewBooking/${id}`;
            }
        })
        .catch(error => console.error("Error:", error));
}



function updateStatus(mystatus) {
    let endpoint = `/editStatusBooking?bookingID=${BookingData.id}&status=${mystatus}`;
    console.log(endpoint);

    fetch(endpoint, {
        method: "PUT",
    })
        .then(response => { response.json(); location.reload(); })
        .then(data => {
            console.log(data);
            // if (data.status === "success") {
            //     window.location.href = `http://localhost:8080//viewBooking/${id}`;
            // }
        })
        .catch(error => console.error("Error:", error));
}
async function renderPage(BookingData, equipmentDetails) {
   

    // mapping equipment details previewImage
    var img = document.getElementById("previewImage")
    if (equipmentDetails.imageURL) {
        console.log("equipmentDetails.imageURL not NULL");
        img.setAttribute("src", equipmentDetails.imageURL)
        img.src = equipmentDetails.imageURL;
    }
    else {
        console.log("equipmentDetails.imageURL IS NULL");
        img.src = "/img/blank.png";

    }
    document.getElementById("uoaID").textContent = equipmentDetails.uoaID;
    document.getElementById("description").textContent =
        equipmentDetails.description;
    document.getElementById("brand").textContent = equipmentDetails.brand;
    document.getElementById("model").textContent = equipmentDetails.model;
    document.getElementById("serialNumber").textContent =
        equipmentDetails.serialNumber;
    document.getElementById("Pickup").textContent =
        equipmentDetails.pickupAllowed;
        // operating
        document.getElementById("operating").textContent =
        equipmentDetails.operatingRequirements;
        document.getElementById("safety").textContent =
        equipmentDetails.safetyRequirements;

    document.getElementById("max-rental-duration").textContent =
        equipmentDetails.maxUseDay;

    // mapping BookingData details
    myBookingID = BookingData.id;
    await fetch(`/getIncidentIDWithBookingID/${myBookingID}`, { method: 'GET' })
    .then(response => response.json())
    .then(data => {
        console.log("incident id= :", data);
        myIncidentID = data;
    }).catch(error => {
        console.log(" not incident  id ", error);
      
    });
    document.getElementById("bookingid").textContent = BookingData.id;
    document.getElementById("bookedBy").textContent = BookingData.bookedBy;
    document.getElementById("startTime").textContent = BookingData.startTime;
    document.getElementById("endTime").textContent = BookingData.endTime;
    document.getElementById("createDate").textContent = BookingData.request_date;
    document.getElementById("lastmodifieddate").textContent = BookingData.last_modified;
    // document.getElementById("note").textContent=BookingData.booking_note;
    var rejectNote = document.getElementById("rejectNote");
    rejectNote.textContent = BookingData.reject_reason;
    var bookingStatus = document.getElementById("bookingStatus");
    bookingStatus.textContent = BookingData.status;

    const formStatus = bookingStatus.textContent.toLowerCase().replace(/\s+/g, '');
    switch (formStatus) {
        case "requested":
            bookingStatus.classList.add("style-requested");
            break;
        case "approved":
            bookingStatus.classList.add("style-collected");
            break;
        case "rejected":
            bookingStatus.classList.add("style-rejected");
            break;
        case "cancelled":
            bookingStatus.classList.add("style-cancelled");
            break;
        case "waitingforpickup":
            bookingStatus.classList.add("style-pickup");
            break;
        case "lent":
            bookingStatus.classList.add("style-collected");
            break;
        case "incident":
        case "lentincident":
            bookingStatus.classList.add("style-incident");
            const iconElement = document.createElement("i");
            iconElement.classList.add("fa-solid", "fa-eye", "incident-view");
            bookingStatus.parentElement.appendChild(iconElement);
            console.log("myIncidentID");
             if(myIncidentID){
                console.log("=========myIncidentID",myIncidentID);
            iconElement.addEventListener("click", (e) => {
                console.log("onclick this icon and get incident id =", myIncidentID)
                const newURL = `http://localhost:8080/incidentDetail/${myIncidentID}`;
                window.open(newURL);
            })}else{
                iconElement.style.display = "none"
            }




            break;
        case "safelyreturn":
            bookingStatus.classList.add("style-cancelled");
            break;
        case "faultyreturn":
            bookingStatus.classList.add("style-incident");
            break;
        default:
            break;
    }


    createActions(formStatus);

    backeBtn = document.getElementById("back-button");
    backeBtn.addEventListener("click", (e) => {
        window.location.href = 'http://localhost:8080/allBookings';
    })
}

//fetch data
document.addEventListener("DOMContentLoaded", function () {
    async function fetchDataAndRender() {
        try {
            const url = window.location.pathname;
            const parts = url.split("/");
            const bookingID = parts[parts.length - 1];

            BookingData = await fetchBookingDetails(bookingID);
            console.log("BookingData:", BookingData);
            if (BookingData) {
                equipmentData = await fetchEquipmentDetails(
                    BookingData.equipmentid
                );
                console.log("equipmentData:", equipmentData);
            }
            renderPage(BookingData, equipmentData);
        } catch (error) {
            console.error("Error:", error);
        }
    }

    fetchDataAndRender();
});



function createActions(formStatus) {
    const adminOpDiv = document.querySelector('.admin-op');
    function createButton(type, id, text) {
        const button = document.createElement('button');
        button.type = type;
        button.className = 'btn btn-primary';
        button.id = id;
        button.textContent = text;
        return button;
    }
      userRole = "ROLE_STUDENT";
    //    userRole = "ROLE_STAFF";
   userUPI="yilu747";
    //   userUPI="jlee842";
    //    userUPI="thuy744";
      console.log("now  userRole=",userRole);
      console.log("now  userUPI=",userUPI);
    if (userRole === "ROLE_STUDENT" || userRole == "ROLE_STAFF") {
       //btns create by user role (STUDNET)
   console.log(BookingData.bookedBy);
    if(userUPI== BookingData.bookedBy){
        if (
            formStatus === "requested" 
            // ||
            // formStatus === "waitingforpickup"
        ) {
            const withdrawButton = createButton('button', 'btn-Withdraw', 'Withdraw');
            withdrawButton.dataset.bsToggle = 'modal';
            withdrawButton.dataset.bsTarget = '#cancelcomfirm';
            adminOpDiv.appendChild(withdrawButton);

            const editButton = createButton('button', 'edit-btn', 'Edit');
            adminOpDiv.appendChild(editButton);

            const cancelButton = createButton('button', 'edit-cancel', 'Cancel');
            cancelButton.classList.add('noshow');
            adminOpDiv.appendChild(cancelButton);

            const saveButton = createButton('submit', 'edit-save', 'Save');
            saveButton.classList.add('noshow');
            saveButton.disabled = true;
            adminOpDiv.appendChild(saveButton);

        }
        if (
            // formStatus === "requested" ||
            formStatus === "waitingforpickup"
        ) {
            const withdrawButton = createButton('button', 'btn-Withdraw', 'Withdraw');
            withdrawButton.dataset.bsToggle = 'modal';
            withdrawButton.dataset.bsTarget = '#cancelcomfirm';
            adminOpDiv.appendChild(withdrawButton);

            // const editButton = createButton('button', 'edit-btn', 'Edit');
            // adminOpDiv.appendChild(editButton);

            // const cancelButton = createButton('button', 'edit-cancel', 'Cancel');
            // cancelButton.classList.add('noshow');
            // adminOpDiv.appendChild(cancelButton);

            // const saveButton = createButton('submit', 'edit-save', 'Save');
            // saveButton.classList.add('noshow');
            // saveButton.disabled = true;
            // adminOpDiv.appendChild(saveButton);

        }
        if (formStatus === "rejected") {
            whyReject.classList.remove("noshow");
        }

        if (
            formStatus === "lent" ||
            formStatus === "approved"
        ) {
            const incidentButton = createButton('button', 'incident-btn', 'Report Incident');
            adminOpDiv.appendChild(incidentButton);
            incidentButton.addEventListener("click", (e) => {
                console.log("incidentButton onclick");
                console.log("myBookingID", myBookingID);
                const newURL = `http://localhost:8080/newIncident/${myBookingID}`;
                // window.open(newURL);
                window.location.href = newURL;

            })

        }


        //btns action
        var btnEdit = document.getElementById("edit-btn");
        var startTime = document.getElementById("startTime");
        var endTime = document.getElementById("endTime");

        var WithdrawBtn = document.getElementById("btn-Withdraw");
        var cancelEditBtn = document.getElementById("edit-cancel");
        var saveEditBtn = document.getElementById("edit-save");

        var confirmBtn = document.getElementById("confirmBtn");

        //edit
        if (btnEdit) {
            btnEdit.addEventListener("click", editable);
            function editable() {
                const mytoday = moment().add(1, 'days').format('YYYY-MM-DD');
                let oldstartTimeValue = startTime.textContent;
                var startTimeParentDiv = startTime.parentNode;
                var newinput = document.createElement("input");
                newinput.id = "startTime";
                newinput.type = "date";
                newinput.required = true;
                newinput.setAttribute("name", "startTime");
                newinput.value = oldstartTimeValue;
                newinput.min = mytoday;

                var newspan = document.createElement("span");
                newspan.className = "must",
                    newspan.textContent = "*"

                startTimeParentDiv.replaceChild(newinput, startTime);
                startTimeParentDiv.appendChild(newspan);


                var endTimeParentDiv = endTime.parentNode;
                let value = endTime.textContent;
                var newinput2 = document.createElement("input");
                newinput2.id = "endTime";
                newinput2.type = "date";
                newinput2.required = true;
                newinput2.value = value;
                newinput2.setAttribute("name", "endTime");
                newinput2.min = mytoday;
                endTimeParentDiv.replaceChild(newinput2, endTime);
                var newspan2 = document.createElement("span");
                newspan2.className = "must",
                    newspan2.textContent = "*"
                endTimeParentDiv.appendChild(newspan2);

                var noteTextarea = document.getElementById("note");
                noteTextarea.removeAttribute("disabled");
                noteTextarea.style.backgroundColor = "white";

                saveEditBtn.classList.remove("noshow");
                cancelEditBtn.classList.remove("noshow");
                backeBtn.classList.add("noshow");
                btnEdit.classList.add("noshow");
                WithdrawBtn.classList.add("noshow");

                newinput2.addEventListener("blur", (e) => {
                    console.log("ENDTIME blur");
                    if (newinput2.value && newinput.value) {
                        checkDate();
                    }
                });

                newinput.addEventListener("blur", (e) => {
                    console.log("start blur");
                    if (newinput2.value && newinput.value) {
                        checkDate();

                    }
                });

                function checkDate() {
                    const endTimeDate = moment(newinput2.value, "YYYY-MM-DD");
                    const startTimeDate = moment(newinput.value, "YYYY-MM-DD");
                    const daysDifference = endTimeDate.diff(startTimeDate, "days");
                    console.log("daysDifference,", daysDifference);
                    if (daysDifference <= 0) {
                        alert("start Date should befotr End Date");
                        saveEditBtn.disabled = true;
                    } else if (
                        daysDifference > document.getElementById("max-rental-duration").textContent
                    ) {
                        saveEditBtn.disabled = true;
                        saveEditBtn.disabled = false;
                        document.getElementById("maxtips").classList.remove("hidden");
                    } else {
                        // dateCheck=true;
                        saveEditBtn.disabled = true;
                        saveEditBtn.disabled = false;
                        console.log("dateCheck=true");
                        document.getElementById("maxtips").classList.add("hidden");
                    }
                }


            }
        }
        //cancelEdit
        if (cancelEditBtn) {
            cancelEditBtn.addEventListener("click", (e) => {
                location.reload();
            });
        }
        //save Edit
        document
            .getElementById("editBookingForm")
            .addEventListener("submit", function (event) {
                event.preventDefault();
                const Lastedittime = moment().format('YYYY-MM-DD');
                const formData = new FormData(event.target);
                const formDataJson = {};
                formDataJson["last_modified"] = Lastedittime;
                formDataJson["bookedBy"] = BookingData.bookedBy;
                formDataJson["status"] = BookingData.status;
                formDataJson["equipmentid"] = BookingData.equipmentid;
                formDataJson["request_date"] = BookingData.request_date;
                formDataJson["rejection_reason"] = BookingData.rejection_reason;


                formData.forEach((value, key) => {
                    formDataJson[key] = value;
                });
                console.log(formDataJson);


                const url = `/editBooking/${BookingData.id} `
                console.log(url);
                fetch(url, {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(formDataJson),
                })
                    .then((response) => response.json())
                    .then((data) => {
                        console.log("Response from server:", data);
                        if (data) {
                            window.location.reload();
                        }
                    })
                    .catch((error) => {
                        console.error("Error:", error);
                    });

            });


        // confirm Withdraw
        confirmBtn.addEventListener("click", (e) => {
            console.log("cancel booing id=", BookingData.id);
            const id = BookingData.id;
            const status = "cancelled";
            // let endpoint = `/cancelBooking/${id} `;
            // console.log(endpoint);
            updateStatus(status);


        });
//8888END
    }
        //=============================================student end ========================================
    }
    else {
        console.log("admin actions");
        //=====================================user role (ADMIN)=================================
        //btns create by user role (ADMIN)
        var approveButton = "";
        var rejectButton = "";
        var safelyReturnedBtn = "";
        var faultyReturnedBtn = "";
        var deleteBtn = "";
        var lentBtn = "";
        var cancelBtn = "";
        // console.log("========formStatus==========:",formStatus);
        switch (formStatus) {
            case "requested":
                approveButton = createButton('button', 'approve-btn', 'Approve');
                adminOpDiv.appendChild(approveButton);
                rejectButton = createButton('button', 'reject-btn', 'Reject');
                adminOpDiv.appendChild(rejectButton);
                break;

            case "approved":
            case "lent":
            case "lentincident":
            case "incident":
                safelyReturnedBtn = createButton('button', 's-return-btn', 'Safely Return');
                adminOpDiv.appendChild(safelyReturnedBtn);
                faultyReturnedBtn = createButton('button', 'f-return-btn', 'Faulty Return');
                adminOpDiv.appendChild(faultyReturnedBtn);
                break;

            case "rejected":
            case "cancelled":
            case "safelyreturn":
            case "faultyreturn":
                deleteBtn = createButton('button', 'del-btn', 'Delete');
                adminOpDiv.appendChild(deleteBtn);
                break;

            case "waitingforpickup":
                lentBtn = createButton('button', 'lent-btn', 'Lent');
                adminOpDiv.appendChild(lentBtn);
                cancelBtn = createButton('button', 'cancel-btn', 'Cancel');
                adminOpDiv.appendChild(cancelBtn);
                break;

            default:
                break;
        }

        if (approveButton) {
            approveButton.addEventListener('click', (e) => {

                console.log(equipmentData.pickupAllowed);
                if (equipmentData.pickupAllowed) {
                    updateStatus("Waiting for pickup")

                } else {
                    updateStatus("Approved")

                }

            })
        }
        if (rejectButton) {
            rejectButton.addEventListener('click', (e) => {
                updateStatus("Rejected")
            })
        }
        if (safelyReturnedBtn) {
            safelyReturnedBtn.addEventListener('click', (e) => {
                updateStatus("Safely Return")
            })
        }
        if (lentBtn) {
            lentBtn.addEventListener('click', (e) => {
                updateStatus("Lent")
            })
        }
        if (faultyReturnedBtn) {
            faultyReturnedBtn.addEventListener('click', (e) => {
                updateStatus("Faulty Return")

            })
        }
        if (cancelBtn) {
            cancelBtn.addEventListener('click', (e) => {
                updateStatus("Cancelled")

            })
        }
        if (deleteBtn) {
            deleteBtn.addEventListener('click', (e) => {

                // incident delete  must befor booking delete
console.log("click delte btn:",myIncidentID);
                if (myIncidentID) {
                    console.log("have IncidentID  ,del first");
                    fetch("/deleteIncident/" + myIncidentID, {
                        method: "DELETE",
                        headers: {
                            "Content-Type": "application/json",
                        },
                    })
                        .then((data) => {
                            console.log("deleteIncident");
                            delMyBooking();
                        })
                        .catch((error) => {
                            console.error("Error:", error);
                            delMyBooking();
                        });
                }

                function delMyBooking() {
                    console.log("delMyBooking");
                    let endpoint = `/deleteBooking/${BookingData.id} `;
                    fetch(endpoint, {
                        method: "DELETE",
                    })
                        .then(response => {
                            response.json();
                            window.location.href = "http://localhost:8080/allBookings";
                        })
                        .then(data => {
                            console.log(data);
                            window.location.href = "http://localhost:8080/allBookings";
                        })
                        .catch(error => console.error("Error:", error));
                }
            })
        }


        //==========================================================================================
        // back btn
    }
}
