# Robotics Lab Equipment Booking and Tracking System
This project is a web-based system for managing equipment in a robotics laboratory. It allows users to book equipment, view their bookings, track incidents, and receive notifications when bookings are confirmed and equipment is ready.

# Technologies
- Spring Boot <br>
- Spring Security <br>
- Google OAuth 2.0
- Maven <br>
- PostgreSQL <br>
- JSON Web Tokens (JWT) <br>

# Installation
To install and run this project, you can: <br>

1.) Have OpenJDK 17+ and Maven installed on your local machine. <br>

2.) Import the project as a maven application to your IDE of choice. IntelliJ IDEA was used for this project. <br>

# Run 
To build and run the project, you can: <br>

1.) Clone the repository:
- git clone https://github.com/Lynn61Liu/robotics-lab-equipment-booking-and-tracking-system.git <br>
- Navigate to the project directory: cd robotics-lab-equipment-booking-and-tracking-system <br>
- Build the project: mvn clean install <br>
- Run the project: mvn spring-boot:run <br>
  
2.) Run the main application on your IDE of choice. <br>

# View

The application will be available at http://localhost:8080.

# Configuration

Sensitive credentials are not stored in the repository. Configure these environment variables before enabling Google OAuth or email notifications:

- `GOOGLE_CLIENT_ID`
- `GOOGLE_CLIENT_SECRET`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`
- `MAIL_FROM_ADDRESS`
