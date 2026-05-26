console.log("here is all equipment================ ");
var userRole;


function getData() {
    // userRole="";
    // userRole="";
console.log(userRole);
//   userRole = "ROLE_STUDENT"
    userRole = "ROLE_STAFF"
 console.log("change the role",userRole);
// hidden student login
if(userRole==="ROLE_STUDENT"){
    console.log( document.getElementById("addEquipmentButton"));
 document.getElementById("addEquipmentButton").classList.add("hidden");
}
    function getAllEquipment() {
        return fetch(`/getall`, { method: 'GET' })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error('Failed to fetch all bookings');
                }
            });
    }

    function getAllBookings() {
        return fetch(`/getallbookings`, { method: 'GET' })
            .then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    throw new Error('Failed to fetch all bookings');
                }
            });
    }

    let bookingPromise = getAllBookings();
    bookingPromise.then(booking => {
        // console.log(booking);
        // return new Promise(function (resolve, reject) {
        Promise.all([getAllEquipment()])
            .then(([equipment]) => {
                equipment.forEach((item) => {
                    // console.log(item);

                    const eitemDiv = document.createElement("div");
                    eitemDiv.className = "eitem";

if( userRole != "ROLE_STUDENT"){
                    const delIcon = document.createElement("i");
                    delIcon.className = "fa-solid fa-trash del-icon";
                    delIcon.style.color = "#304b5b";
                    delIcon.setAttribute("data-bs-toggle", "modal");
                    delIcon.setAttribute("data-bs-target", "#delModal");
                    delIcon.setAttribute("data-sysID", item.systemID);
                    eitemDiv.appendChild(delIcon);
}
                    const labImg = document.createElement("div");
                    labImg.className = "lab-img";
                    const img = document.createElement("img");
                    if (item.imageURL) 
                        { img.src = item.imageURL; }
                    else {
                        img.src = "/img/blank.png";
                    }


                    img.alt = "";
                    labImg.appendChild(img);
                    eitemDiv.appendChild(labImg);

                    /////////// Create and append information (infor01)
                    const infor01Div = document.createElement("div");
                    infor01Div.className = "infor01";

                    const eNameDiv = document.createElement("div");
                    eNameDiv.id = "e-name";
                    eNameDiv.className = "item-unit item-name";
                    eNameDiv.textContent = item.description; // Replace 'item.name' with the actual name from the data
                    infor01Div.appendChild(eNameDiv);

                    const uoaIdDiv = document.createElement("div");
                    uoaIdDiv.className = "item-unit";
                    const uoaIdSpan = document.createElement("span");
                    uoaIdSpan.textContent = "UOA ID:";
                    uoaIdDiv.appendChild(uoaIdSpan);
                    const uoaIdValueSpan = document.createElement("span");
                    uoaIdValueSpan.textContent = item.uoaID; // Replace 'item.uoaId' with the actual UOA ID from the data
                    uoaIdDiv.appendChild(uoaIdValueSpan);
                    infor01Div.appendChild(uoaIdDiv);

                    const serialNumberDiv = document.createElement("div");
                    serialNumberDiv.className = "item-unit";
                    const serialNumberSpan = document.createElement("span");
                    serialNumberSpan.textContent = "Serial Number:";
                    serialNumberDiv.appendChild(serialNumberSpan);
                    const serialNumberValueSpan = document.createElement("span");
                    serialNumberValueSpan.textContent = item.serialNumber; // Replace 'item.serialNumber' with the actual serial number from the data
                    serialNumberDiv.appendChild(serialNumberValueSpan);
                    infor01Div.appendChild(serialNumberDiv);

                    const brandDiv = document.createElement("div");
                    brandDiv.className = "item-unit";
                    const brandSpan = document.createElement("span");
                    brandSpan.className = "unit-name";
                    brandSpan.textContent = "Brand:";
                    brandDiv.appendChild(brandSpan);
                    const brandValueSpan = document.createElement("span");
                    brandValueSpan.textContent = item.brand; // Replace 'item.brand' with the actual brand from the data
                    brandDiv.appendChild(brandValueSpan);
                    infor01Div.appendChild(brandDiv);

                    const modelDiv = document.createElement("div");
                    modelDiv.className = "item-unit";
                    const modelSpan = document.createElement("span");
                    modelSpan.className = "unit-name";
                    modelSpan.textContent = "Model:";
                    modelDiv.appendChild(modelSpan);
                    const modelValueSpan = document.createElement("span");
                    modelValueSpan.textContent = item.model;
                    modelDiv.appendChild(modelValueSpan);
                    infor01Div.appendChild(modelDiv);

                    const statusDiv = document.createElement("div");
                    statusDiv.className = "item-unit";
                    const statusSpan = document.createElement("span");
                    statusSpan.textContent = "Status:";
                    statusDiv.appendChild(statusSpan);
                    const statusValueSpan = document.createElement("span");
                    statusValueSpan.className = "status";


                    exactBooking = booking.find(exactBooking => exactBooking.equipmentid === item.systemID);
                    // console.log(exactBooking);
                    if (exactBooking) {
                        statusValueSpan.textContent = exactBooking.status;
                    } else {
                        statusValueSpan.textContent = 'Available';
                    }
                    statusDiv.appendChild(statusValueSpan);
                    infor01Div.appendChild(statusDiv);

                    // const dueDayDiv = document.createElement("div");
                    // dueDayDiv.className = "item-unit";
                    // const dueDaySpan = document.createElement("span");
                    // dueDaySpan.textContent = "Due Day:";
                    // dueDayDiv.appendChild(dueDaySpan);
                    // const dueDayValueSpan = document.createElement("span");
                    // if (exactBooking) {
                    //     dueDayValueSpan.textContent = exactBooking.endtime;
                    // }
                    // else {
                    //     dueDayValueSpan.textContent = "N/A";
                    // }
                    // dueDayDiv.appendChild(dueDayValueSpan);
                    // infor01Div.appendChild(dueDayDiv);

                    const detailButton = document.createElement("button");
                    detailButton.className = "btn-more";
                    detailButton.textContent = "Detail";
                    detailButton.setAttribute("data-sysID", item.systemID);
                    infor01Div.appendChild(detailButton);


                    eitemDiv.appendChild(infor01Div);


                    const container = document.getElementsByClassName("container")[0];
                    const loading = document.getElementsByClassName("spinner-box")[0];
                    loading.style.display = "none";

                    container.appendChild(eitemDiv);
                });
                // set status bgc
                const statusElementList = document.querySelectorAll(".status");
                statusElementList.forEach((statusElement) => {
                    if (statusElement.textContent.trim().toLowerCase() === "available") {
                        statusElement.classList.add("available");
                    }
                });



                //  pagination
                const paginationNumbers = document.getElementById("pagination-numbers");
                var paginatedList = document.querySelector(".container");
                var listItems = paginatedList.querySelectorAll(".eitem");

                const nextButton = document.getElementById("next-button");
                const prevButton = document.getElementById("prev-button");
                const paginationLimit = 8;
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
                            // console.log(elementToScroll);
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

                // link to other page url
                const btnDetails = document.querySelectorAll(".btn-more");
                // console.log(btnDetails);
                btnDetails.forEach((item) => {
                    item.addEventListener("click", () => {
                        const sysId = item.getAttribute("data-sysid");
                        window.location.href = `/equipmentDetail?sysID=${sysId}`;
                    });
                });

                //delete btn
                var clickItem
                var toasmassage
                var deleteIcons = document.querySelectorAll(".del-icon");
                deleteIcons.forEach(function (icon) {
                    icon.addEventListener("click", function () {
                        var dataSysId = this.getAttribute("data-sysid");
                        document.querySelector(".modal-body").textContent = " Are you sure to delete the item which systemID: " + dataSysId;
                        // console.log(this);
                        clickItem = this.parentNode;
                        // console.log(clickItem);

                    });
                });

                function showTost(msg) {
                    const toastLiveExample = document.getElementById('liveToast')
                    const toast = bootstrap.Toast.getOrCreateInstance(toastLiveExample)
                    toasmassage = document.getElementById('toast-body');
                    toasmassage.textContent += msg;
                    toast.show();
                    setTimeout(() => { toast.hide(); }, 5000)
                    // window.location.href = "http://localhost:8080/equipmentList";
                }

                var dialogDelBtn = document.querySelector(".dialog-btn-del");
                dialogDelBtn.addEventListener("click", () => {
                    var dataSysId = document.querySelector(".modal-body").textContent.split(" ").pop();
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
                            showTost(data.message)

                            if (data.status === "success") {
                                toasmassage.classList.add("bg-success");
                                clickItem.parentNode.removeChild(clickItem);
                            }
                            else {
                                toasmassage.classList.add("bg-danger");
                            }
                        })
                        .catch((error) => {
                            console.error("Error:", error);
                        });
                });
                var newEqBtn = document.querySelector(".btn-new");
                newEqBtn.addEventListener("click", () => {

                    window.location.href = "http://localhost:8080/equipmentAdd";
                });

                var dueDateList = document.querySelectorAll(".due-day");
                dueDateList.forEach((dueDateElement, i) => {
                    var dueDateStr = dueDateElement.textContent;
                    // console.log("dueDateString=", dueDateStr);
                    const dateFormat = "DD-MM-YYYY";
                    const dateObject = moment(dueDateStr, dateFormat);
                    const now = moment();

                    if (dateObject.isBefore(now)) {

                    } else {
                        var icon = dueDateElement.nextElementSibling;
                        icon.style.display = "none";
                    }
                });



                console.log("=========end====================");
            });
    })
        .catch((error) => {
            console.error("Error:", error)
        });


}


window.onload = async function () {
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
    getData();


};