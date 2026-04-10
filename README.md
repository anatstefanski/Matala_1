# SmartStudy 

SmartStudy is a mobile application designed to manage academic study sessions.

The app allows a student to register for study sessions available in their courses, track their activity, and communicate with other students.
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
- No registration to sessions 
- Search and filter courses
- Edit profile


## Screenshots

---
### Shared Screens (Student & Admin)

#### Login
![Login](images/login.jpg)  
The user can log into their account using email and password.

#### Register
![Register](images/register.jpg)  
The user can create a new account.

#### Forgot Password
![Forgot Password](images/forgot_password.jpg)  
The user can request a password reset via email.

#### Profile
![Profile](images/edit_profile.jpg)  
The user can edit their personal information and log out of their account.


### Student Flow

#### Home 
<p align="center">
  <img src="images/main_student.jpg" width="45%" />
  <img src="images/filter_s.jpg" width="45%" />
  <img src="images/search_s.jpg" width="45%" />
</p>   
The student can view all available courses and search or filter them.

#### Course Details
<p align="center">
  <img src="images/sessions_student.jpg" width="45%" />
  <img src="images/student_course_no.jpg" width="45%" />
</p>  

**When sessions are available:**  
The student can view the available study sessions they can register for.

**When no sessions are available:**  
A message is displayed indicating that there are currently no study sessions available for this course.

#### Session Registration
![Session](images/student_course_yes.jpg)  
The student can register for available study sessions.

#### My Sessions
<p align="center">
  <img src="images/my_sessions_yes.jpg" width="45%" />
  <img src="images/my_sessions_no.jpg" width="45%" />
</p>    

**When the student is registered to sessions:**  
The student can view all the study sessions they have registered for.  
In addition, a visual chart is displayed showing the distribution of their sessions by category (in percentages), along with a breakdown of how many sessions belong to each category.

**When the student is not registered to any sessions:**  
No sessions are displayed and the statistics chart remains empty.

#### Chat
![Chat](images/chat.jpg)  
The student can communicate with other participants in the session.

#### Profile
![Profile](images/edit_profile.jpg)  
The student can edit their personal information and log out of their account.

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
