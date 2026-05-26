console.log("equipmentAdd.js");

var cancelButton = document.getElementById("add-equipment-cancel");
cancelButton.addEventListener("click", function () {
    window.location.href = "http://localhost:8080/equipmentList";
});




//image input
var previewImage = document.getElementById("previewImage");
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


document.getElementById('equipmentAddForm').addEventListener('submit', function (event) {
    event.preventDefault();
    const Lastedittime = new Date().getTime();
    const formData = new FormData(event.target);
    const formDataJson = {};
    formData.forEach((value, key) => {
        if (key === 'building' || key === 'depthMM' || key === 'heightMM' || key === 'maxUseDay' || key === 'widthMM') {

            formDataJson[key] = parseInt(value);
        }
        else if (key === 'pickupAllowed') {
            formDataJson[key] = value.toLowerCase() === 'yes'? true : false;
        }
    //     else if (key === 'safetyRequirements') {
    //       formDataJson[key] = value.toLowerCase() === 'yes'? true : false;
    //   }
    //   else if (key === 'operatingRequirements') {
    //     formDataJson[key] = value.toLowerCase() === 'yes'? true : false;
    // }
        else if (key === 'equipmentStatus') {
            formDataJson[key] = value.toLowerCase() === 'available';
        }
        else {
            formDataJson[key] = value;
        }
    });

    formDataJson['Lastedittime'] = Lastedittime;
    formDataJson['imageURL'] = previewImage.src;
    console.log(formDataJson);
    // sendDataToBackend(formDataJson);

    // send http request for json
    fetch(`/equipmentAdd`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(formDataJson)
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
        return response.json();
      })
        .then(data => {
            console.log('Response from server:', data);

            // fetch url from equipmentAdd method in equipment controller class
            if (data.redirectUrl) {
                console.log(data.redirectUrl)
                // Redirect to the equipmentDetail page
                window.location.href = data.redirectUrl;
            }
        })
      .catch(error => {
        console.error('Error:', error);

      });
});
  