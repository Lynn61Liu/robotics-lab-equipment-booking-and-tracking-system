const url = window.location.pathname;
const parts = url.split("/");
const sysID = parts[parts.length - 1];
// console.log(sysID);

var bookingByEquipment = "";
var equipmentDetails = "";
var allBookingsByEID=""
function createCalendar(activeBookings) {
  console.log("activeBookings IN  FUNCTION");

  const tableContainer = document.getElementById("calendarTable");
  tableContainer.textContent="";
  console.log("===EMPTY ");
  if (activeBookings.length !== 0) {
    const table = document.createElement("table");
    table.className = "table align-middle";

    const thead = document.createElement("thead");
    thead.className = "table-light";

    const tbody = document.createElement("tbody");

    const headerRow = document.createElement("tr");
    const headers = [
      "Start Day",
      "Return Day",
      "Status",
      "Equipment Name",
      "UOA ID",
      "Applicant",
    ];

    headers.forEach((headerText) => {
      const th = document.createElement("th");
      th.scope = "col";
      th.textContent = headerText;
      headerRow.appendChild(th);
    });

    thead.appendChild(headerRow);
    table.appendChild(thead);

    for (const booking of activeBookings) {
      const tr = document.createElement("tr");
      const formattedStartDate = new Date(booking.startTime).toLocaleDateString(
        "en-NZ",
        { year: "numeric", month: "short", day: "numeric" }
      );
      const formattedEndDate = new Date(booking.endTime).toLocaleDateString(
        "en-NZ",
        { year: "numeric", month: "short", day: "numeric" }
      );

      const columns = [
        formattedStartDate,
        formattedEndDate,
        `<span class="status-style style-${booking.status
          .toLowerCase()
          .replace(/\s+/g, "")}">${booking.status}</span>`,
        equipmentDetails.description,
        equipmentDetails.uoaID,
        booking.bookedBy,
      ];

      columns.forEach((columnText) => {
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
    console.log("activeBookings=NULL");
    tableContainer.textContent = "There are no reservations on this device.";
  }
}
function filterData() {

  const startDate = document.getElementById("startDate").value;
  const endDate = document.getElementById("endDate").value;
if(startDate && endDate){

  const filteredBookings = allBookingsByEID.filter(booking => {
    const bookingStartTime = new Date(booking.startTime);
    const bookingEndTime = new Date(booking.endTime);
    return bookingStartTime >= new Date(startDate) && bookingEndTime < new Date(endDate);
  });

let sortfiltered=filteredBookings.sort((a, b) => new Date(b.startTime) - new Date(a.startTime));
console.log("sortfiltered=",sortfiltered);

createCalendar(sortfiltered);}
else{
    createCalendar(allBookingsByEID);
}
}

document.addEventListener("DOMContentLoaded", function () {
  function fetchEquipmentDetails() {
    return fetch(`/equipmentIndividual?systemID=${sysID}`)
      .then((response) => response.json())
      .then((data) => {
        console.log("Individual Equipment Profile Data:", data);
        equipmentDetails = data;
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
        // console.log("ALL booking data:", bookingdata);
        return bookingdata;
      })
      .catch((error) => {
        console.error("Error:", error);
      });
  }
  Promise.all([fetchEquipmentDetails(), fetchBookingDetails()])
    .then(([equipmentDetails, bookingdata]) => {
      console.log(bookingdata);
        allBookingsByEID = bookingdata
        .filter((item) => item.equipmentid === equipmentDetails.systemID)
        .sort((a, b) => new Date(b.startTime) - new Date(a.startTime));
        // allBookingsByEID = activeBookings
      console.log(allBookingsByEID);
      createCalendar(allBookingsByEID);

      //   const today = new Date().toISOString().split("T")[0];
      //   const today = moment().add(1, 'days').format('YYYY-MM-DD')
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
      var img = document.getElementById("previewImage");
      if (equipmentDetails.imageURL) {
        // console.log("equipmentDetails.imageURL not NULL");
        img.setAttribute("src", equipmentDetails.imageURL);
        img.src = equipmentDetails.imageURL;
      } else {
        // console.log("equipmentDetails.imageURL IS NULL");
        img.src = "/img/blank.png";
      }

      // if (bookingDetails) {
      //     document.getElementById('status').textContent = bookingDetails.status;
      //     document.getElementById('endTime').textContent = bookingDetails.endTime;
      // }
    })
    .catch((error) => {
      console.error("Error:", error);
    });
});
