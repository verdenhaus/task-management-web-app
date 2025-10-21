# Task Management Web App

A full-stack task management application built with **Spring Boot** (backend) and **React** (frontend). Features include user authentication, task creation, auto-assign, Kanban-style task board, and status tracking.

## Features

* User registration & login (JWT authentication)
* Create, edit, delete tasks
* Auto-assign tasks to users
* Kanban board for task status (`Assigned`, `In Progress`, `Testing`, `Complete`)
* Drag-and-drop task management
* View tasks assigned to yourself or other users

---

## Backend (Spring Boot)

### Requirements

* Java 17+
* Maven
* PostgreSQL (or your preferred database)

### Setup

1. Clone the repository:

```bash
git clone https://github.com/verdenhaus/task-management-web-app.git
cd task-management-web-app/backend
```

2. Configure database connection in `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/taskdb
spring.datasource.username=your_db_user
spring.datasource.password=your_db_password
spring.jpa.hibernate.ddl-auto=update
```

3. Build and run the backend:

```bash
mvn clean install
mvn spring-boot:run
```

The backend API will be available at `http://localhost:8080/api`.

### API Endpoints (Highlights)

* `POST /api/auth/login` – User login
* `POST /api/auth/register` – User registration
* `GET /api/users/me` – Get current logged-in user
* `GET /api/tasks` – Get all tasks
* `POST /api/tasks` – Create task
* `PUT /api/tasks/{id}` – Update task
* `POST /api/tasks/assign/{id}` – Auto-assign task
* `POST /api/tasks/{id}/status` – Update task status

---

## Frontend (React)

### Requirements

* Node.js 18+
* npm or yarn

### Setup

1. Navigate to the frontend folder:

```bash
cd ../frontend
```

2. Install dependencies:

```bash
npm install
# or
yarn install
```

3. Start the frontend server:

```bash
npm start
# or
yarn start
```

The app will open at `http://localhost:3000`.

---

## Usage

1. Open the frontend in your browser.
2. Register a new account or log in.
3. Create tasks using the modal form.
4. Drag tasks between Kanban columns to update status.
5. Auto-assign tasks will automatically assign them to available users.
6. View other users’ tasks in their respective sections.

---

## Notes

* Ensure backend is running on port `8080` when running frontend.
* JWT token is stored in `localStorage` for authentication.
* Both manual assignment and auto-assign display the correct username in popups.

---
