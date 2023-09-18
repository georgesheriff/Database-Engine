# Database Engine with Octree Indices

## Table of Contents
1. [Introduction](#introduction)
2. [Overview](#overview)
3. [Project Description](#project-description)
   - 3.1 [Tables](#tables)
   - 3.2 [Indices](#indices)
4. [How to Use](#how-to-use)
5. [Directory Structure](#directory-structure)
6. [Getting Started](#getting-started)

   

## Introduction
Welcome to the Database Engine with Octree Indices project! This project is designed to showcase a small database engine with support for Octree Indices. It's built as part of the CSEN604: Database II course at the German University in Cairo.

## Overview
- *Programming Language:* Java
- *Development Environment:* Eclipse, IntelliJ, or NetBeans

## Project Description
In this project, we've implemented a simplified database engine with Octree Index support. Here's a brief overview of the key features:

### Tables
- Tables are stored as pages on disk, with each page as a separate file.
- Supported column types include Integer, String, Double, and Date (in "YYYY-MM-DD" format).

### Indices
- Octree indices are used to improve query performance.
- Indices are created on-demand and updated when new data is added or removed.
- They are saved to disk and loaded during application startup.

## How to Use
1. Initialize the database engine using the `init()` method.
2. Create tables using `createTable(...)`.
3. Create Octree indices using `createIndex(...)`.
4. Insert data into tables with `insertIntoTable(...)`.
5. Update existing records using `updateTable(...)`.
6. Delete rows from tables with `deleteFromTable(...)`.
7. Perform queries with `selectFromTable(...)`.
8. The application can be configured via the `DBApp.config` file in the `resources` directory.

## Directory Structure
- `DBApp.config`: Configuration file for the application.

## Getting Started
1. Clone the repository.
2. Import the project into your preferred Java IDE (Eclipse, IntelliJ, or NetBeans).
3. Run the project.
