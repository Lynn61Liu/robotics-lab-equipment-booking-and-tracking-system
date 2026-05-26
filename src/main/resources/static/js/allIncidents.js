console.log("all incident js");
var userRole;
var bookingItem;
var userUPI;

window.onload = async function () {
    // Access the table by its ID
    console.log("onload incident js");
    //get user role
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
    const managePagination = () => {
        const paginationNumbers = document.getElementById("pagination-numbers");
        var paginatedList = document.querySelector("#all-incident-list");
        var listItems = paginatedList.querySelectorAll(".row-item");

        const nextButton = document.getElementById("next-button");
        const prevButton = document.getElementById("prev-button");
        const paginationLimit = 10;
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
    var table = document.getElementById("all-incident-list");

    function getData() {
        return new Promise(function (resolve, reject) {
            fetch("/getAllIncidents", {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                },
            })
                .then((response) => response.json())
                .then((data) => {
        
                    var sortdata= data.sort((a, b) => b.incidentId - a.incidentId);
                    

                    sortdata.forEach(async function (item) {
                      
                        bookingItem = await fetch(`/booking/${item.booking_id}`, {
                            method: "GET",
                            headers: {
                                "Content-Type": "application/json",
                            },
                        })
                            .then((response) => response.json())
                            .then((data) => {
                                return data;
                            })
// test for change userRole
                   
                           userRole = "ROLE_STUDENT";
                         // userRole = "ROLE_STAFF";
                           userUPI="yilu747";
                        //    userUPI="jlee842";
                        //   userUPI="thuy744";
                        // renderpage by role and upi
                        if (userRole == "ROLE_STUDENT") {
                            if (userUPI == bookingItem.bookedBy) {
                                var row = createTableRow(item);
                                table.appendChild(row);
                            }

                        }else{
                            var row = createTableRow(item);
                            table.appendChild(row);
                        }

                        resolve(sortdata);

                    });


                })
                .catch((error) => {
                    console.error("Error:", error);
                    reject(error); // Reject the promise with an error
                });
        });

    }

    // Function to dynamically create table rows
    function createTableRow(data) {

        var row = document.createElement("tr");
        row.className = "row-item";
        // Define the order and names of the table columns
        var columns = ["created_date",
            "incidentId",
            "description",
            "booking_id",
            "incident_status",
            "reporter",
            "action"
        ];
        columns.forEach(async function (columnName) {
            var cell = document.createElement("td");
            if (columnName == "created_date") {
                const dateFormat = "DD-MM-YYYY";
                const dateObject = moment(data[columnName]);
                cell.textContent = dateObject.format(dateFormat);
            } else if (columnName == "incidentId") {
                let anchor = document.createElement("a");
                anchor.className = "text-decoration-none";
                anchor.setAttribute("href", "/incidentDetail/" + data[columnName]);
                anchor.textContent = data[columnName];
                cell.appendChild(anchor);
            } else if (columnName == "booking_id") {
                let anchor = document.createElement("a");
                anchor.className = "text-decoration-none";
                anchor.setAttribute("href", "/viewBooking/" + data[columnName]);
                anchor.textContent = data[columnName];
                cell.appendChild(anchor);
            } else if (columnName == "incident_status") {
                let span = document.createElement("span");
                let status = "";
                if (data[columnName] == "Pending") {
                    status = "warning";
                } else if (data[columnName] == "Closed") {
                    status = "success";
                } else if (data[columnName] == "Cancelled") {
                    status = "secondary";
                } else if (data[columnName] == "More Info Requested") {
                    status = "danger";
                } else {
                    status = "dark";
                }
                span.className = "badge bg-" + status + " bold-text";
                span.textContent = data[columnName];
                cell.appendChild(span);
            } else if (columnName == "reporter") {

                cell.textContent = bookingItem.bookedBy;
            }
          
        
            else if (columnName == "description") {
                cell.textContent = data[columnName];
                cell.className = "report-content";
            }
            else {

                cell.textContent = data[columnName];
            }
            row.appendChild(cell);
        });
        return row;
    }

    //  getData();
    await getData()
        .then(data => {
            managePagination();
        });


    /** ACTION BUTTONS */
    var selectedIncidentId = 0;
    function showHideModal(modal) {
        modal.toggle();
        selectedIncidentId = 0
    }
    /** Delete Action */
    const deleteModalElem = document.querySelector('#confirmDeleteModal');
    deleteModal = new window.bootstrap.Modal(deleteModalElem);
    deleteIncident = async (id) => {
        console.log('id', id);
        await showHideModal(deleteModal);
        selectedIncidentId = id;
    }
    confirmDelete = () => {
        fetch("/deleteIncident/" + selectedIncidentId, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
            },
        })
            // .then((response) => response.json())
            .then((data) => {
                // Handle the response from the backend
                console.log(data);
                window.location.reload();
            })
            .catch((error) => {
                console.error("Error:", error);
            });
        showHideModal(deleteModal);
    }
    /** Delete Action */

    /** Close Action */
    const closeModalElem = document.querySelector('#confirmCloseModal');
    closeModal = new window.bootstrap.Modal(closeModalElem);
    closeIncident = async (id) => {
        // console.log('id',id);
        await showHideModal(closeModal);
        selectedIncidentId = id;
    }
    confirmClose = () => {
        fetch("/closedIncident/" + selectedIncidentId, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
        })
            // .then((response) => response.json())
            .then((data) => {
                // Handle the response from the backend
                console.log(data);
                window.location.reload();
            })
            .catch((error) => {
                console.error("Error:", error);
            });
        showHideModal(closeModal);
    }
    /** Close Action */

    /** Cancel Action */
    const cancelModalElem = document.querySelector('#confirmCancelModal');
    cancelModal = new window.bootstrap.Modal(cancelModalElem);
    cancelIncident = async (id) => {
        // console.log('id',id);
        await showHideModal(cancelModal);
        selectedIncidentId = id;
    }
    confirmCancel = () => {
        fetch("/CancelledIncident/" + selectedIncidentId, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
        })
            // .then((response) => response.json())
            .then((data) => {
                // Handle the response from the backend
                console.log(data);
                window.location.reload();
            })
            .catch((error) => {
                console.error("Error:", error);
            });
        showHideModal(cancelModal);
    }
    /** Cancel Action */


};


