var userRole;

function GetIndividualData(sysID) {
  console.log(userRole);
     userRole = "ROLE_STUDENT"
    //  userRole = "ROLE_STAFF"
 console.log("change the role",userRole);
 if(userRole==="ROLE_STUDENT"){
  console.log( document.getElementById("deleteButton"));
document.getElementById("deleteButton").classList.add("hidden");
document.getElementById("editButton").classList.add("hidden");
}


  fetch(`/equipmentIndividual?systemID=${sysID}`)
    .then((response) => response.json())
    .then((data) => {
      console.log("Individual Equipment Profile Data:", data);
      if (data) {

        const elementMapping = {
          systemID: "systemID",
          description: "description",
          brand: "brand",
          model: "model",
          uoaID: "uoaID",
          serialNumber: "serialNumber",
          building: "building",
          room: "room",
          notes: "notes",
          normalPosition: "normalPosition",
          widthMM: "widthMM",
          depthMM: "depthMM",
          heightMM: "heightMM",
          powerRating: "powerRating",
          phase1or3: "phase1or3",
          imageURL: "imageURL",
          maxUseDay: "maxUseDay",
          bookedBy: "bookedBy",
        };

        // Loop through the data object properties and populate corresponding elements
        for (const property in elementMapping) {
          const elementID = elementMapping[property];
          const element = document.getElementById(elementID);

          if (element) {
            element.textContent = data[property];
          }
        }


        //mapping image
        var detailPageImg = document.querySelector("#previewImage");

        //  console.log("detailPageImg",detailPageImg);
        //  console.log("data.imageURL",data.imageURL);
        if (data.imageURL) { detailPageImg.src = data.imageURL; }
        else {
          detailPageImg.src = "/img/blank.png";
        }

        //TIME FORMAT

        var dueDay = document.getElementById("dueDay");
        const dueNZTime = moment.unix(data.dueDay / 1000).utcOffset("+12:00");
        // Get N/A if there is no value for dueDay
        if (!dueDay || dueDay.textContent.trim() === "") {
          dueDay.textContent = "Has Not Been Booked Before";
        } else if (dueNZTime.isValid()) {
          dueDay.textContent = dueNZTime.format("DD MMM YYYY");
        }
        var lastEditTime = document.getElementById("lastEditTime");
        lastEditTime.textContent = moment
          .unix(data.lastEditTime / 1000)
          .utcOffset("+12:00")
          .format("DD MMM YYYY HH:mm:ss");

        var previewImage = document.getElementById("previewImage");
        previewImage.src = data.imageURL;

        const pickupAllowedElement = document.getElementById("pickupAllowed");
        console.log(data.pickupAllowed);
        if (data.pickupAllowed) {
          pickupAllowedElement.textContent = "Yes";
        } else {
          pickupAllowedElement.textContent = "No";
        }


        document.getElementById("safetyRequirements").textContent=data.safetyRequirements;

        document.getElementById("operatingRequirements").textContent=data.operatingRequirements;





        var deleteButton = document.getElementById("deleteButton");
        deleteButton.setAttribute("data-systemID", data.systemID);

        // pathName: "path-name",
        var pathName = document.getElementById("path-name");
        pathName.textContent = data.description;
        var bookButton = document.getElementById("bookButton");
        var backButton = document.getElementById("backButton");
        var cancelButton = document.getElementById("cancelButton");
        var saveButton = document.getElementById("saveButton");

        //fetch booking 
        
        fetch(`/presentBooking/equipmentID/${sysID}`)
        .then((response) => response.json())
        .then((bookingdata) => {
          console.log("booking data:",bookingdata);

            const equipmentStatusElement = document.getElementById("equipmentStatus");
            if (bookingdata.status) {
              equipmentStatusElement.textContent = bookingdata.status;
            } else {
              equipmentStatusElement.textContent = "Available";
              equipmentStatusElement.classList.add("available");
            }

        const dueDate = document.getElementById("dueDay");
        if (bookingdata.endTime) {
          console.log(bookingdata.endTime);
          dueDate.textContent = bookingdata.endTime;
        } else {
          dueDate.textContent = "N/A";
        }

        const bookedBy = document.getElementById("bookedBy");
        if (bookingdata.bookedBy) {
          bookedBy.textContent = bookingdata.bookedBy;
        } else {
          bookedBy.textContent = "N/A";
        }


          })
          .catch(error => {
            console.error('Error:', error);

          });



        // edit
        var spanElements = document.querySelectorAll(".detail span[id]");
        editButton.addEventListener("click", editable);
        function editable() {
          for (var i = 0; i < spanElements.length; i++) {
            var span = spanElements[i];
            // console.log(span);
            if (span.id == "maxUseDay") {
              continue;
            }
            if (span.id == "dueDay") {
              continue;
            }
            if (span.id == "e-detail") {
              continue;
            }
            if (span.id == "equipmentStatus") {
              continue;
            }
            if (span.id == "bookedBy") {
              continue;
            }
            if (span.id == "lastEditTime") {
              continue;
            }
            if (span.id == "building") {
              continue;
            }
            if (span.id == "room") {
              continue;
            }

            var input = document.createElement("input");
            input.value = span.textContent;
            input.id = span.id;
            input.setAttribute('name', span.id);
            var parentDiv = span.parentNode;
            parentDiv.removeChild(span);
            parentDiv.appendChild(input);
          }

          var previewImage = document.getElementById("previewImage");
          var imagechange = document.getElementById("image-change");
          var imageInput = document.getElementById("imageInput");

          imagechange.classList.remove("noshow");


          var pickupOld = document.getElementById("pickupAllowed");
          var pickupOldValue = pickupOld.textContent;
          var pickupParentDiv = pickupOld.parentNode;
          var newPU = document.createElement("select");
          newPU.setAttribute('name', "pickupAllowed");
          newPU.innerHTML =
            "<select>" +
            '<option value="Yes" ' +
            (pickupOldValue === "Yes" ? "selected" : "") +
            ">Yes</option>" +
            '<option value="No" ' +
            (pickupOldValue === "No" ? "selected" : "") +
            ">No</option>" +
            "</select>";
          pickupParentDiv.replaceChild(newPU, pickupOld);





          var oldmaxDay = document.getElementById("maxUseDay");
          var oldDays = parseInt(oldmaxDay.textContent);
          var maxDayParentDiv = oldmaxDay.parentNode;
          var newDays = document.createElement("input");
          newDays.id = "max-day";
          newDays.type = "number";
          newDays.value = oldDays;
          newDays.setAttribute('name', "maxUseDay");
          maxDayParentDiv.replaceChild(newDays, oldmaxDay);

          //building must type of number
          var oldbuilding = document.getElementById("building");
          var oldbuildingValue = parseInt(oldbuilding.textContent);
          console.log(oldbuildingValue);
          var buildingParentDiv = oldbuilding.parentNode;
          var newbuilding = document.createElement("input");
          newbuilding.id = "building";
          newbuilding.type = "number";
          newbuilding.required = true;
          newbuilding.value = oldbuildingValue;
          var newbuildingSpan = document.createElement("span");
          newbuildingSpan.textContent = "*";
          newbuildingSpan.classList.add("must");
          newbuilding.setAttribute('name', "building");
          buildingParentDiv.replaceChild(newbuilding, oldbuilding);
          buildingParentDiv.appendChild(newbuildingSpan);



          //room required
          var oldRoom = document.getElementById("room");
          var roomParentDiv = oldRoom.parentNode;
          var newRoom = document.createElement("input");
          newRoom.id = "room";
          newRoom.required = true;
          newRoom.value = oldRoom.textContent;
          newRoom.setAttribute('name', "room");
          var newRoomSpan = document.createElement("span");
          newRoomSpan.textContent = "*";
          newRoomSpan.classList.add("must");
          roomParentDiv.replaceChild(newRoom, oldRoom);
          roomParentDiv.appendChild(newRoomSpan);




          var noteTextarea = document.getElementById("notes");
          noteTextarea.classList.remove("disabled");
          noteTextarea.removeAttribute("disabled");
          noteTextarea.style.backgroundColor = "white";

          editButton.style.display = "none";
          saveButton.classList.remove("hidden");
          cancelButton.style.display = "block";
          bookButton.style.display = "none";
          deleteButton.style.display = "none";

          var backButton = document.querySelector("#backButton");
          backButton.style.display = "none";

          var elementToScroll = document.querySelector(".detail");
          const yOffset = 175;
          window.scrollTo({
            top: elementToScroll.offsetTop - yOffset,
            behavior: "smooth",
          });





        }

        //delete btn
        function showTost(msg, status) {
          const toastLiveExample = document.getElementById('liveToast')
          const toast = bootstrap.Toast.getOrCreateInstance(toastLiveExample)
          toasmassage = document.getElementById('toast-body');
          toasmassage.textContent = msg + "  The page will automatically jump after 3 seconds.  ";
          if (status === "success") {
            toasmassage.classList.add("bg-success");
            toast.show();
            setTimeout(() => { window.location.href = "http://localhost:8080/equipmentList"; },
              5000)


          }
          else {
            toasmassage.classList.add("bg-danger");
            toast.show();
            setTimeout(function () { location.reload(); }, 5000);
          }

        }
        var dataSysId;
        var toasmassage;
        deleteButton.addEventListener("click", function () {
          dataSysId = this.getAttribute("data-systemID");

          document.querySelector(".modal-body").textContent =

            " Are you sure to delete the item which UOA ID: " + data.uoaID;
        });

        var dialogDelBtn = document.querySelector(".dialog-btn-del");
        dialogDelBtn.addEventListener("click", () => {
          console.log("DELE SYSTEM ID=", dataSysId);
          //sent id to backend
          fetch("/equipmentDelete?systemID=" + dataSysId, {
            method: "DELETE",
            headers: {
              "Content-Type": "application/json",
            },
          })
            .then((response) => response.json())
            .then((data) => {
              // Handle the response from the backend
              console.log(data);
              showTost(data.message, data.status)


            })
            .catch((error) => {
              console.error("Error:", error);
            });
        });


        // cancel
        cancelButton.addEventListener("click", function () {
          location.reload(); // Reload the page
        });

        bookButton.addEventListener("click", function () {
          // Redirect to the new booking page with the corresponding systemID
          window.location.href = `http://localhost:8080/newBooking/${sysID}`;
        });

        document.getElementById("booking-view").addEventListener("click", function () {
          // Redirect to the new booking page with the corresponding systemID
          // window.location.href = `http://localhost:8080/BookingCalendar/${sysID}`;

          const url = `http://localhost:8080/BookingCalendar/${sysID}`;
          window.open(url, '_blank');
        });

        imageInput.addEventListener("change", function (event) {
          var selectedFile = event.target.files[0];
          if (selectedFile) {
            var reader = new FileReader();

            reader.onload = function (e) {
              previewImage.src = e.target.result;
            };

            reader.readAsDataURL(selectedFile);
          }
        });



        // view all
        var backButton = document.getElementById("backButton");
        backButton.addEventListener("click", function () {
          window.location.href = "http://localhost:8080/equipmentList";
        });

        //DATE
        var dueDateElement = document.getElementById("dueDay");
        var dueDateStr = dueDateElement.textContent;
        const dateFormat = "DD-MM-YYYY";
        const dateObject = moment(dueDateStr, dateFormat);

        const now = moment();
        if (dateObject.isBefore(now)) {
          // console.log("due,显示图标 ");
        } else {
          // console.log(dueDateElement);
          var icon = dueDateElement.nextElementSibling;
          icon.style.display = "none";
          // console.log("due,不显示图标 ");
        }


        document.getElementById('equipmentEditForm').addEventListener('submit', function (event) {
          event.preventDefault();
          // const systemid=generateUUID();
          const lastEditTime = new Date().getTime();
          const formData = new FormData(event.target);
          const formDataJson = {};

          formData.forEach((value, key) => {
            if (key === 'pickupAllowed' ) {
              formDataJson[key] = value.toLowerCase() === 'yes';
             
            } else if (key === 'widthMM' || key === 'heightMM' || key === 'depthMM' || key === 'maxUseDay') {
              // check if the value is an empty string, and if it is, replace it with null value
              formDataJson[key] = value.trim() === '' ? null : parseInt(value);
            } else {
              console.log(value);
              formDataJson[key] = value;
            }
          });

          formDataJson['systemID'] = data.systemID;
          formDataJson['lastEditTime'] = lastEditTime;
          formDataJson['imageURL'] = previewImage.src;
          console.log(formDataJson);
          // sendDataToBackend(formDataJson);
          fetch(`/equipmentEdit/${data.systemID}`, {
            method: 'PUT',
            headers: {
              'Content-Type': 'application/json'
            },
            body: JSON.stringify(formDataJson)
          })
            .then(response => {
              if (!response.ok) {
                console.error('HTTP status code:', response.status);
                console.error('Response text:', response.statusText);
                throw new Error('Network response was not ok');
              }
              return response.json();
            })
            .then(data => {
              console.log('Response from server:', data);
              window.location.reload(); //Reload the page when save button is pressed and submission is sent through
            })
            .catch(error => {
              console.error('Error:', error);

            });
        });

      }
      else {
        // 404 no find Equipment
        var pageBody = document.querySelector(".detail");
        pageBody.textContent = " 404  THIS EQUIPMENT PAGE DOES NOT EXIST"
      }
    })
    .catch((error) => {
      console.error("Error fetching data:", error);
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
          userRole = role;
      })
      .catch(error => {
          console.error("Error:", error);
      });
}
window.addEventListener("load", async function () {
  const urlParams = new URLSearchParams(window.location.search);
  const sysID = urlParams.get("sysID");
  await fetchRole(); 
  GetIndividualData(sysID);
});
