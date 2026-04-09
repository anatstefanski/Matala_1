# SmartStudy 

SmartStudy is a mobile application designed to manage academic study sessions.

The app allows students to register for study sessions available in their courses, track their activity, and communicate with other students.
Administrators can create and manage courses and sessions.

## Features

### Student
- Register and login
- Password reset via email
- View available courses
- Search and filter courses
- Register for study sessions
- View "My Sessions" screen
- Visual statistics (sessions by category)
- Chat with other participants registered for the same session 
- Edit profile

### Admin
- Register and login
- Password reset via email
- View available courses
- Create new courses
- Create new study sessions
- Manage course sessions
- No registration to sessions 
- Search and filter courses
- Edit profile


## Screenshots

---

### Student Flow

#### Login
![Login](images/login.jpg)  
Students can log into their account using email and password.

#### Home 
![Catalog](images/main_stusent.jpg)  
Students can view all available courses and search or filter them.

#### Course Details
![Course](images/course.png)  
Students can view course information and available study sessions.

#### Session Registration
![Session](images/student_course_yes.jpg)  
Students can register for available study sessions.

#### My Sessions
![Stats](images/stats.png)  
Students can view their registered sessions along with statistics by category.

#### Chat
![Chat](images/chat.jpg)  
Students can communicate with other participants in the session.

#### Profile
![Profile](images/edit_profile.jpg)  
Students can edit their personal information.

---

### Admin Flow

#### Add Course
![Add Course](images/add_course.png)  
Admins can create new courses.

#### Add Session
![Add Session](images/add_session.png)  
Admins can create new study sessions inside a course.

#### Course Management
![Course](images/course.png)  
Admins can manage course sessions and content.




## Architecture

The application is built using the MVVM architecture:

- View (Activities & XML)
- ViewModel (handles UI logic)
- Repository (Firebase data handling)

This structure ensures clean code, scalability, and maintainability.
