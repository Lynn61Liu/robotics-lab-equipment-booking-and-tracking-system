console.log("here is all equipment ");

fetch("/equipment", {
  method: "GET",
})
  .then((response) => {
    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }

    return response.json();
  })
  .then((data) => {
    console.log(data);
  })
  .catch((error) => {
    console.error("Fetch error:", error);
  });

function fetchEquipmentDetails() {
    const systemID = document.getElementById('systemID').textContent;
    console.log(systemID);
    window.location.href = `/newbooking/${systemID}`;
}

