# 🏫 SmartGate – Hostel Leave Management System

SmartGate is a web-based hostel leave management system designed to simplify and digitize the leave approval process for students, wardens, HODs, and parents.

## 🚀 Live Demo

🔗 **Live Application:**  
https://smartgate-hostel-management-system.onrender.com/

## ✨ Features

- 👨‍🎓 Student profile management
- 📝 Online hostel leave application
- 👨‍👩‍👧 Parent approval through email
- 🛡️ Warden leave approval
- 🎓 HOD leave approval
- 📧 Automated email notifications
- 🔐 Role-based access and visibility
- 📅 Working-day and holiday leave handling
- 💾 Persistent database storage
- 🌐 Live cloud deployment

## 👥 User Roles

### Student
- Maintain profile details
- Apply for hostel leave
- View leave status

### Parent
- Receive leave request emails
- Accept or reject leave requests

### HOD
- Review student leave requests
- Approve or reject working-day leave requests

### Warden
- Review hostel leave requests
- Send requests to parents
- Approve or reject leave requests

## 🛠️ Tech Stack

**Backend**
- Java
- Spring Boot
- Spring Data JPA
- REST APIs
- Hibernate

**Frontend**
- HTML
- CSS
- JavaScript
- Bootstrap

**Database**
- MySQL

**Email**
- Gmail SMTP

**Deployment**
- Docker
- Render
- Aiven MySQL

## 🔄 Leave Approval Flow


Student
   ↓
Leave Application
   ↓
HOD / Warden Review
   ↓
Parent Email Approval
   ↓
Parent Accept / Reject
   ↓
Final HOD / Warden Decision
   ↓
Leave Status Updated
📂 Project Structure
SmartGate
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
├── Dockerfile
├── pom.xml
├── mvnw
└── README.md
⚙️ Running Locally
1. Clone the repository
git clone https://github.com/NagaBhavaniYandra2012/Smartgate.git
2. Open the project

Open the project in IntelliJ IDEA, Eclipse, or Cursor.

3. Configure MySQL

Update the database configuration in application.properties.

4. Run the application
./mvnw spring-boot:run

The application will be available at:

http://localhost:8080
🌐 Deployment

SmartGate is deployed using Docker on Render, with MySQL hosted on Aiven.

🎯 Project Goal

The goal of SmartGate is to replace manual hostel leave processes with a centralized digital workflow that improves communication between students, parents, HODs, and wardens.

👩‍💻 Author

Naga Bhavani Yandra

Java Developer | Spring Boot | Web Development
