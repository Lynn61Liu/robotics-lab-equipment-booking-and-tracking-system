document.addEventListener("DOMContentLoaded", function () {
  const links = document.querySelectorAll("a");
  links.forEach(function (link) {
    link.addEventListener("click", function (event) {
      event.preventDefault();
      console.log(link);
      console.log(this.id);
      switch (this.id) {
        case "nav-equipment":
          window.location.href = "http://localhost:8080/equipmentList";
          break;
        case "nav-dashboard":
          window.location.href = "http://localhost:8080/dashboard";
          break;
        case "nav-incident":
          window.location.href = "http://localhost:8080/allIncidents";
          break;
        case "nav-booking":
            window.location.href = "http://localhost:8080/allBookings";
          break;
        default:
          break;
      }

      links.forEach(function (otherLink) {
        otherLink.classList.remove("active");
      });
      this.classList.add("active");
    });
  });
});

console.log("NAV JS");
const navBtn = document.querySelectorAll(".nav-item");
console.log(navBtn);
