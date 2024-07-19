# Full Stack: React and Spring Boot


## Front-end : React Installation Guides

* [Linux](document/install-react-tools/linux/install-linux.md)

* [Mac](document/install-react-tools/mac/install-mac.md)

* [Microsoft Windows](document/install-react-tools/ms-windows/install-ms-windows.md)

## Back-end Installation

* Java Development Kit (JDK)
  
* Java IDE
  
* Maven

## Source Code

* [Source code](source-code)



## Project Description: Book Library Web Application

### Overview
The Book Library application is a full-stack project designed to provide users with a seamless experience for exploring, checking out, and reviewing books. It features a dynamic, responsive interface that adapts to both desktop and mobile devices, ensuring an optimal user experience across platforms. The application also includes an admin site to manage the library, allowing administrators to efficiently oversee book inventories and user interactions.

### Features
1. **Homepage**:
   - Welcome banner with a call to action to explore top books.
   - Carousel showcasing featured books.
   - Sign-up prompt for new users.
   - Contact option for library admin.

2. **Explore Top Books**:
   - Search functionality to filter books by title and category.
   - Detailed book view with image, title, author, description, and user reviews.
   - Availability status and user checkout information.

3. **User Interaction**:
   - User sign-up and sign-in functionalities.
   - Book checkout system with real-time updates.
   - Review submission system for users to rate and comment on books.
   - User bookshelf to manage current loans, return, and renew books.
   - Loan history to track previously borrowed books.

4. **Admin Panel**:
   - Admin sign-in for managing book inventory.
   - Adding new books and updating book quantities.
   - Responding to user messages.

### Tech Stack
#### Front-end
- **React**: A JavaScript library for building user interfaces, allowing the creation of reusable UI components.
- **React Router DOM**: Used for routing in the application, enabling navigation between different views.
- **TypeScript**: A superset of JavaScript that adds static typing, enhancing code quality and developer productivity.
- **Okta React**: Integration for secure authentication and user management.
- **Okta Sign-In Widget**: Provides a customizable sign-in experience, leveraging Okta for user authentication.
- **Axios**: A promise-based HTTP client for making requests to the back-end API.
- **CSS, Bootstrap**: For styling the application components to ensure a modern and responsive design.

#### Back-end
- **Spring Boot**: A Java-based framework used to create stand-alone, production-grade Spring applications.
- **Spring Security**: Provides authentication and authorization capabilities for securing the application.
- **MySQL**: A relational database management system for storing and managing application data.

#### Integrations
- **Okta**: For secure user authentication and authorization.
- **Stripe**: For handling payments securely with PCI compliance.

### API Endpoints
- [Refer to API Specification document](document/API-Specification.xlsx)

### Security and Compliance
- **HTTPS**: All communications between the client and server are encrypted using HTTPS.
- **Okta Integration**: Ensures secure authentication and authorization.
- **Stripe Integration**: Ensures secure handling of payment information without storing sensitive data on the server.

### Responsive Design
The application is designed to be fully responsive, providing a seamless user experience on both desktop and mobile devices. The layout dynamically adjusts to different screen sizes, ensuring that all functionalities are accessible and easy to use on any device.
