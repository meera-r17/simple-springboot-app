<img width="945" alt="Screenshot 2025-03-29 at 12 25 06 AM" src="https://github.com/user-attachments/assets/d6f72fdb-1fc7-4ea7-a88a-0f2e2204ceec" />Cart Offer API

Project Overview
This repository contains a Spring Boot application that implements a discount management system for an e-commerce platform, specifically designed for a food delivery service like Zomato. The system allows for creating and applying segment-based offers to customer carts with different discount types.

Business Context
The business has divided its customers into three segments: p1, p2, and p3. Based on these segments, different offers can be applied to their carts during checkout. The application supports two types of discount mechanisms:

FLATX - A flat amount discount (e.g., Rs.10 off)
FLATPERCENT - A percentage discount (e.g., 10% off)

Testing
A comprehensive test suite is included to verify the functionality of the cart offer system. The test cases cover:

Basic functionality for both discount types
Edge cases (zero values, large discounts, etc.)
Multi-segment offers
Competing offers
Cross-restaurant offer applications
Input validation

The test framework uses Spring Boot's testing capabilities to ensure the API works as expected.

Setup and Running the Application

Clone the repository
Make sure you have Java and Maven installed
Run mvn spring-boot:run to start the server on port 9001
Access the API at http://localhost:9001/api/v1/

The API responses were as follows:

<img width="500" alt="Screenshot 2025-03-29 at 12 43 02 AM" src="https://github.com/user-attachments/assets/03ea22f7-901c-4463-8e1e-488930d3bf8a" />

<img width="500" alt="Screenshot 2025-03-29 at 12 42 51 AM" src="https://github.com/user-attachments/assets/d471db34-e786-48e3-8b9e-966242857236" />

<img width="500" alt="Screenshot 2025-03-29 at 12 43 09 AM" src="https://github.com/user-attachments/assets/c005ac72-c076-466b-b57e-80d6fbcb2f48" />

<img width="720" alt="Screenshot 2025-03-29 at 12 42 44 AM" src="https://github.com/user-attachments/assets/d52203d0-2688-4bc5-8841-10244988aa04" />

Based on some incorrect API responses, my test script has failed and these are genuine reasons. The API was returning 200 as cart value instead of 190 as you can see above

Report:

<img width="700" alt="Screenshot 2025-03-29 at 12 25 00 AM" src="https://github.com/user-attachments/assets/3a535fe5-ee15-4ea1-9ff3-d46b4cd3a73c" />

<img width="700" alt="Screenshot 2025-03-29 at 12 25 06 AM" src="https://github.com/user-attachments/assets/2c69d291-d2b4-4368-a3c7-0d127e7a3b5a" />

<img width="700" alt="Screenshot 2025-03-29 at 12 31 19 AM" src="https://github.com/user-attachments/assets/fa3ca4e7-1305-44a1-af48-30cbd4ba0d9c" />





