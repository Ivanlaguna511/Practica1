# SDIS Module 1: Secure TCP Authentication Server

<div align="center">
  
  ![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
  ![Sockets](https://img.shields.io/badge/Network-TCP_Sockets-blue?style=for-the-badge)
  ![Concurrency](https://img.shields.io/badge/Concurrency-Thread_Pool-success?style=for-the-badge)
  ![Security](https://img.shields.io/badge/Security-Anti--Brute_Force-red?style=for-the-badge)
  ![Academic](https://img.shields.io/badge/Academic_Project-2nd_Year-purple?style=for-the-badge)

</div>

> **ABOUT THIS MODULE:**
> This repository contains Module 1 of the Distributed Systems (SDIS) course project. It implements a robust, multithreaded Client-Server architecture using pure Java TCP Sockets. The primary focus of this module is **secure authentication**, **concurrency management**, and **resource protection**.

---

## Architecture & Key Features

Unlike basic academic socket implementations that spawn a new thread per connection (leading to memory exhaustion), this server is designed with enterprise-grade patterns:

* **Thread Pooling (`ExecutorService`):** Limits the number of concurrent active threads, queuing incoming connections gracefully when under heavy load.
* **Anti-Brute Force Protection (`ConnectionManager`):** * Implements rate-limiting using `ConcurrentHashMap`.
  * Tracks IPs and automatically bans clients exceeding the maximum allowed connections or failed login attempts.
* **Thread-Safe Shared State:** Uses atomic operations (`merge`, `computeIfPresent`) to ensure thread-safe tracking of logins and bans without relying on heavy blocking synchronization.
* **Resource Safety:** Strict use of `try-with-resources` to guarantee that Sockets and I/O Streams are closed reliably, preventing memory leaks and dangling file descriptors.

---

## Project Structure

```text
src/sdis/
 ┣ 📂 common
 ┃ ┗ 📜 ConnectionManager.java     # Thread-safe rate limiter and IP tracker
 ┣ 📂 server
 ┃ ┣ 📜 AuthServer.java            # Main server entry point (Thread Pool setup)
 ┃ ┗ 📜 ClientAuthHandler.java     # Runnable worker for protocol execution
 ┗ 📂 client
   ┗ 📜 AuthClient.java            # Interactive terminal client
```

## Getting Started
### 1. Compilation
Navigate to the root directory of your project and compile the Java files:

```bash
javac sdis/module1/server/*.java sdis/module1/client/*.java sdis/module1/common/*.java
```

### 2. Execution
Start the Server:
Open a terminal and start the authentication server. It will bind to port 2000 by default.

```bash
java sdis.module1.server.AuthServer
```

Start the Client:
Open a separate terminal (or multiple terminals to test concurrency/bans) and start the client.

```bash
java sdis.module1.client.AuthClient
```

Interaction Example
Client Terminal:
```bash
CLIENT: Attempting to connect to localhost:2000
SERVER: Welcome, please type your credentials to LOG in
Username > hector
SERVER: hector
SERVER: OK: password?
Password > 1234
SERVER: 1234
SERVER: User successfully logged in
```

Server Logs:
```bash
SERVER: [STARTING] Listening on port 2000
SERVER: New connection from 127.0.0.1
[BM] Connections for 127.0.0.1 = 1
SERVER: Thread finished. Last attempted user: hector
```

### Authors
-Iván Moro Cienfuegos, David Martín Sebastián, Eric Soto San José and Héctor
