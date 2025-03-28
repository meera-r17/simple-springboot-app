Cart Offer API

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
