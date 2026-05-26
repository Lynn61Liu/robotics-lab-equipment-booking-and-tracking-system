const url = window.location.pathname;
const parts = url.split("/");
const sysID = parts[parts.length - 1];
console.log(sysID);

var bookingByEquipment = "";

var cancelButton = document.getElementById("edit-cancel");
var bookButton = document.getElementById("btn-book");
var endTime = document.getElementById("endTime");
var startTime = document.getElementById("startTime");

cancelButton.addEventListener("click", function () {
  window.location.href = `/equipmentList`;
});

endTime.addEventListener("blur", (e) => {
  if (endTime.value && startTime.value) {
    checkDate();
  }
});

startTime.addEventListener("blur", (e) => {
  if (endTime.value && startTime.value) {
    checkDate();

  }
});
// var dateCheck=false;
function checkDate() {
  const endTimeDate = moment(endTime.value, "YYYY-MM-DD");
  const startTimeDate = moment(startTime.value, "YYYY-MM-DD");
  const daysDifference = endTimeDate.diff(startTimeDate, "days");
  console.log("daysDifference,", daysDifference);
  if (daysDifference <= 0) {
    alert("start Date should befotr End Date");
    bookButton.disabled = true;
  } else if (
    daysDifference > document.getElementById("max-rental-duration").textContent
  ) {
    bookButton.disabled = true;
    bookButton.disabled = false;
    document.getElementById("maxtips").classList.remove("hidden");
  } else {
    // dateCheck=true;
    bookButton.disabled = true;
    bookButton.disabled = false;
    console.log("dateCheck=true");
    document.getElementById("maxtips").classList.add("hidden");
  }
}

document.addEventListener("DOMContentLoaded", function () {
    let userRole;

    function fetchEquipmentDetails() {
    return fetch(`/equipmentIndividual?systemID=${sysID}`)
      .then((response) => response.json())
      .then((data) => {
        console.log("Individual Equipment Profile Data:", data);
        return data;
      });
  }
  function fetchBookingDetails() {

    // return fetch(`/bookingdetails/equipmentID/${sysID}`)
    return fetch("/getallbookings", {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    })
      .then((response) => response.json())
      .then((bookingdata) => {
        console.log("ALL booking data:", bookingdata);
        return bookingdata;
      })
      .catch((error) => {
        console.error("Error:", error);
      });
  }

  function fetchRole() {
      return fetch("/getRole", {
          method: "GET",
          headers: {
              "Content-Type": "application/json",
          },
      })
          .then(response => response.text())
          .then(role => {
              console.log("Your role is:", role);
              userRole = role;
          })
          .catch(error => {
              console.error("Error:", error);
          });
  }


  Promise.all([fetchEquipmentDetails(), fetchBookingDetails(), fetchRole()])
    .then(([equipmentDetails, bookingdata]) => {
      const currentDate = new Date().toISOString().split('T')[0];
      const allowedStatus = ['Approved', 'Requested', 'Lent', 'Waiting For Pickup','Lent Incident'];
console.log(bookingdata);
      const activeBookings = bookingdata.filter(item => item.equipmentid === equipmentDetails.systemID && allowedStatus.includes(item.status))
        .sort((a, b) => new Date(a.startTime) - new Date(b.startTime))
        .slice(0, 5);

      console.log(activeBookings);

      const tableContainer = document.getElementById("calendarTable");
      if (activeBookings.length !== 0) {


        const table = document.createElement("table");
        table.className = "table align-middle";

        const thead = document.createElement("thead");
        thead.className = "table-light";

        const tbody = document.createElement("tbody");

        const headerRow = document.createElement("tr");
        const headers = ["Start Day", "Return Day", "Status", "Equipment Name", "UOA ID", "Applicant"];

        headers.forEach(headerText => {
          const th = document.createElement("th");
          th.scope = "col";
          th.textContent = headerText;
          headerRow.appendChild(th);
        });

        thead.appendChild(headerRow);
        table.appendChild(thead);

        for (const booking of activeBookings) {
          const tr = document.createElement("tr");
            const formattedStartDate = new Date(booking.startTime).toLocaleDateString('en-NZ', { year: 'numeric', month: 'short', day: 'numeric' });
            const formattedEndDate = new Date(booking.endTime).toLocaleDateString('en-NZ', { year: 'numeric', month: 'short', day: 'numeric' });

            console.log("I'm here!!!" + formattedEndDate);
          console.log(equipmentDetails);
          const columns = [
            formattedStartDate,
            formattedEndDate,
            `<span class="status-style style-${booking.status.toLowerCase()}">${booking.status}</span>`,
            equipmentDetails.description, // Replace with actual equipment name
            equipmentDetails.uoaID, // Assuming ID can be used as UOA ID
            booking.bookedBy
          ];

          columns.forEach(columnText => {
            const td = document.createElement("td");
            td.scope = "col";
            td.innerHTML = columnText;
            tr.appendChild(td);
          });

          tbody.appendChild(tr);
        }

        table.appendChild(tbody);
        tableContainer.appendChild(table);


      } else {
        console.log('activeBookings=NULL');
        tableContainer.textContent = "There are no reservations on this device."
      }

      //   const today = new Date().toISOString().split("T")[0];
      const today = moment().add(1, 'days').format('YYYY-MM-DD')
      document.getElementById("startTime").min = today;
      document.getElementById("endTime").min = today;

      document.getElementById("uoaID").textContent = equipmentDetails.uoaID;
      document.getElementById("description").textContent =
        equipmentDetails.description;
      document.getElementById("brand").textContent = equipmentDetails.brand;
      document.getElementById("model").textContent = equipmentDetails.model;
      document.getElementById("serialNumber").textContent =
        equipmentDetails.serialNumber;
      document.getElementById("Pickup").textContent =
        equipmentDetails.pickupAllowed;
      document.getElementById("max-rental-duration").textContent =
        equipmentDetails.maxUseDay;
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

      // if (bookingDetails) {
      //     document.getElementById('status').textContent = bookingDetails.status;
      //     document.getElementById('endTime').textContent = bookingDetails.endTime;
      // }

      document
        .getElementById("newBookingForm")
        .addEventListener("submit", function (event) {
          event.preventDefault();

            // Ensure userRole is available before proceeding
            if (userRole) {
                const Lastedittime = moment().format("YYYY-MM-DD");
                const today = moment().format("YYYY-MM-DD");
                const formData = new FormData(event.target);
                let formDataJson = {};
                formDataJson["last_modified"] = Lastedittime;
                formDataJson["equipmentid"] = sysID;
               formDataJson["status"] = "Requested";
                
                // formDataJson["status"] = userRole === "ROLE_ADMIN" ? "Approved" : "Requested";
                formDataJson["request_date"] = today;

                formData.forEach((value, key) => {
                    formDataJson[key] = value;
                });

                const url = "/process";
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
                            window.location.href = "http://localhost:8080/allBookings";
                        }
                        if (data.status === "error") {
                        alert("Booking date conflicts with an existing booking.");
                          // window.location.href = "http://localhost:8080/allBookings";
                      }
                    })
                    .catch((error) => {
                        console.error("Error:", error);
                    });
            } else {
                console.error("User role not available.");
            }
        });
    })
      .catch((error) => {
          console.error("Error:", error);
      });
});


