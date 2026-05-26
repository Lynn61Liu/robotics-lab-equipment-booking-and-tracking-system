window.onload = async function () {
    // Access the table by its ID
    var table = document.getElementById("my-booking-list");
    async function getData() {
        // return new Promise(function (resolve, reject) {
        fetch("/getmybookings", {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            },
        })
            .then((response) => response.json())
            .then((data) => {
                console.log("data====:",data);
                data.sort((a, b) => b.id - a.id);
                data.forEach(function (item) {
                    var row = createTableRow(item);
                    table.appendChild(row);
                });
                managePagination();
            })
            .catch((error) => {
                console.error("Error:", error);
            });
    }
    // Function to dynamically create table rows
    function createTableRow(data) {
        // Create the "Approve" button
        // var approveButton = document.createElement("button");
        // approveButton.type = "button";
        // approveButton.className = "btn btn-primary btn-sm btn-dark";
        // approveButton.textContent = "Approve";
        // approveButton.setAttribute("data-bs-toggle", "modal");
        // approveButton.setAttribute("data-bs-target", "#confirmApproveModal");

        // Create the "Reject" button
        // var rejectButton = document.createElement("button");
        // rejectButton.type = "button";
        // rejectButton.className = "btn btn-primary btn-sm btn-dark";
        // rejectButton.textContent = "Reject";
        // rejectButton.setAttribute("data-bs-toggle", "modal");
        // rejectButton.setAttribute("data-bs-target", "#confirmRejectModal");

        // Create the "Cancel" button
        var cancelButton = document.createElement("div");
        // deleteButton.type = "button";
        cancelButton.href="javascript:void(0);";
        cancelButton.className = "me-2 w-10 action-color";
       
         cancelButton.textContent = "Cancel";
         cancelButton.setAttribute("data-bs-toggle", "modal");
         cancelButton.setAttribute("data-bs-target", "#confirmCancelModal");
        cancelButton.onclick = function() {
            cancelBooking(data['id'])
        };
        // console.log(cancelButton);

        var iconCancel = document.createElement("i");
        iconCancel.className   =   "fa-regular fa-calendar-xmark action-color";
        cancelButton.appendChild(iconCancel);

        /**Incident button */
        var incidentButton = document.createElement("div");
        incidentButton.href="javascript:void(0);";
        incidentButton.className = "me-2 w-10 action-color";
        incidentButton.title="Incident";
        incidentButton.textContent="Incident";
  
        var iconIncident = document.createElement("i");
        iconIncident.className   =   "fa fa-file-circle-exclamation action-color";
         incidentButton.appendChild(iconIncident);
        // iconIncident.appendChild(incidentButton);
        incidentButton.onclick = function() {
            window.location.href = "/addNewIncident/"+data['id'];
        };
        /**Incident Button */



            // Create the "Incident" button
        // var incidentButton = document.createElement("button");
        // incidentButton.type = "button";
        // incidentButton.className = "btn btn-primary btn-sm btn-dark";
        // incidentButton.textContent = "Incident";
        // incidentButton.setAttribute("data-bs-toggle", "modal");
        // incidentButton.setAttribute("data-bs-target", "#confirmIncidentModal");
        var row = document.createElement("tr");
        row.className	= "row-item";
        // Define the order and names of the table columns
        var columns = ["startTime", "id", "status", "equipmentid", "bookedBy", "startTime", "endTime", "action"];
        columns.forEach(function (columnName) {
            var cell = document.createElement("td");
            if(columnName == "equipmentid" || columnName == "id" ){
                let anchor = document.createElement("a");
                anchor.className	= "text-decoration-none";
                if(columnName == "equipmentid" ){
                    anchor.setAttribute("href", "/equipmentDetail?sysID="+data[columnName]);
                }else if (columnName == "id"){
                    anchor.setAttribute("href", "/viewBooking/"+data[columnName]);
                }
                anchor.textContent = data[columnName];
                cell.appendChild(anchor);
            }
            else if(columnName == "status"){
                let span 	= document.createElement("span");
                let status	= "";
                if(data[columnName]=="Available"){
                    status	=	"primary";
                }else if(data[columnName]=="Approved"){
                    status	=	"success";
                }else if(data[columnName]=="Returned"){
                    status	=	"secondary";
                }else if(data[columnName]=="Overdue"){
                    status	=	"warning";
                }else if(data[columnName]=="Rejected"){
                    status	=	"danger";
                }else{
                    status	=	"dark";
                }
                span.className	= "badge bg-"+status+" bold-text";
                span.textContent = data[columnName];
                cell.appendChild(span);
            }
            else if(columnName == "action"){
                if(data["status"]=="Requested"){
                    console.log("cancelButton  actiong=",cancelButton);
                    cell.appendChild(cancelButton);
                }else if(data["status"]=='Lent' || data["status"]=='Approved' ){
                    cell.appendChild(incidentButton);
                }else{
                    cell.style.display = 'none';
                }
            }
            else if(columnName=='endTime'){
                const currentDate = new Date();
                const endDate = new Date(data[columnName]);
                if (currentDate > endDate) {
                    let span = document.createElement("span");
                    span.className	= "bg-danger date-passed";
                    span.textContent = data[columnName];
                    cell.appendChild(span);
                }else{
                    cell.textContent = data[columnName];
                }
            }
            else{
                cell.textContent = data[columnName];
            }
            row.appendChild(cell);
        });
        return row;
    }
    getData();
    /** pagination starts */
    const managePagination = ()=>{
        const paginationNumbers = document.getElementById("pagination-numbers");
        var paginatedList = document.querySelector("#my-booking-list");
        var listItems = paginatedList.querySelectorAll(".row-item");
        const nextButton = document.getElementById("next-button");
        const prevButton = document.getElementById("prev-button");
        const paginationLimit = 14;
        const pageCount = Math.ceil(listItems.length / paginationLimit);

        let currentPage = 1;
        const appendPageNumber = (index) => {
            const pageNumber = document.createElement("button");
            pageNumber.className = "pagination-number";
            pageNumber.innerHTML = index;
            pageNumber.setAttribute("page-index", index);
            pageNumber.setAttribute("aria-label", "Page " + index);
            paginationNumbers.appendChild(pageNumber);
        };
        const getPaginationNumbers = () => {
            for (let i = 1; i <= pageCount; i++) {
                appendPageNumber(i);
            }
        };

        const setCurrentPage = (pageNum) => {
            currentPage = pageNum;
            handleActivePageNumber();
            const prevRange = (pageNum - 1) * paginationLimit;
            const currRange = pageNum * paginationLimit;
            listItems.forEach((item, index) => {
                item.classList.add("hidden");
                item.classList.remove("element-to-scroll");
                if (index == prevRange) {
                    item.classList.add("element-to-scroll");
                }
                if (index >= prevRange && index < currRange) {
                    item.classList.remove("hidden");
                }
            });
        };

        const handleActivePageNumber = () => {
            document.querySelectorAll(".pagination-number").forEach((button) => {
                button.classList.remove("active-pagination");

                const pageIndex = Number(button.getAttribute("page-index"));
                if (pageIndex == currentPage) {
                    button.classList.add("active-pagination");
                }
            });
        };

        getPaginationNumbers();
        setCurrentPage(1);

        document.querySelectorAll(".pagination-number").forEach((button) => {
            const pageIndex = Number(button.getAttribute("page-index"));
            if (pageIndex) {
                button.addEventListener("click", () => {
                    setCurrentPage(pageIndex);
                    const elementToScroll =
                        document.querySelector(".element-to-scroll");
                    if (elementToScroll) {
                        const yOffset = 175;
                        window.scrollTo({
                            top: elementToScroll.offsetTop - yOffset,
                            behavior: "smooth",
                        });
                    }
                });
            }
        });
    }
    /** pagination ends */
    
    /** ACTION BUTTONS */
    var selectedBookingId   =   0;
    function showHideModal(modal){
        modal.toggle();
        selectedBookingId = 0
    }

    /** Cancel Action */
    const cancelModalElem =   document.querySelector('#confirmCancelModal');
    cancelModal           =   new window.bootstrap.Modal(cancelModalElem);
    cancelBooking = (id)=> {
        showHideModal(cancelModal);
        selectedBookingId   =   id;
    }
    confirmCancel = () =>{
        fetch("/cancelBooking/" + selectedBookingId, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
        })
        .then((response) => response.text())
        .then((data) => {
            // Handle the response from the backend
            console.log(data);

            // Reload the page
            window.location.reload();
        })
        .catch((error) => {
            console.error("Error:", error);
        });
        showHideModal(cancelModal);
    }
    /** Cancel Action */
};