# SDIS Module 2: Distributed Spotify TCP Server

<div align="center">
  
  ![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
  ![Sockets](https://img.shields.io/badge/Network-TCP_Sockets-blue?style=for-the-badge)
  ![Serialization](https://img.shields.io/badge/Data-Object_Serialization-success?style=for-the-badge)
  ![Concurrency](https://img.shields.io/badge/Concurrency-Thread_Pool-orange?style=for-the-badge)
  ![Academic](https://img.shields.io/badge/Academic_Project-2nd_Year-purple?style=for-the-badge)

</div>

> **ABOUT THIS MODULE:**
> This repository contains Module 2 of the Distributed Systems (SDIS) course project. It elevates the architecture from Module 1 by implementing a complex, multithreaded server simulating a **Spotify-like playlist manager**. Instead of raw text, this module utilizes **Java Object Serialization** to transmit immutable `ProtocolMessage` structures safely across the network.

---

## Architecture & Key Features

Building upon the anti-brute force mechanisms of Module 1, this module introduces advanced thread-safe state management:

* **Custom Binary Protocol:** Network communication is strictly typed using a custom `ProtocolMessage` DTO and a `ProtocolPrimitive` Enum, ensuring predictable parsing.
* **Stateful Sessions:** The server maintains an `isAuthenticated` state per client handler, rejecting protected commands (`ADD2L`, `DELETEL`) via HTTP-like `401` and `403` logical errors if not logged in.
* **Thread-Safe Generic `ConcurrentMultiMap`:** * Implements a non-blocking map `ConcurrentMap<K, ConcurrentLinkedQueue<T>>`.
  * Allows hundreds of concurrent clients to add (`push`) and read (`pop`) songs from playlists atomically without locking the entire map.
* **Deadlock Prevention:** Safe initialization of `ObjectOutputStream` using `flush()` before `ObjectInputStream` instantiation, avoiding the classic Java Sockets blocking deadlock.

---

## Project Structure

```text
src/sdis/spotify/
 ┣ 📂 common
 ┃ ┣ 📜 ProtocolPrimitive.java     # Protocol Commands (XAUTH, ADD2L...)
 ┃ ┣ 📜 ProtocolMessage.java       # Immutable Serializable DTO
 ┃ ┗ 📜 ServerMessages.java        # Centralized string constants
 ┣ 📂 utils
 ┃ ┣ 📜 ConnectionManager.java     # Rate-limiting and Brute-Force protection
 ┃ ┗ 📜 ConcurrentMultiMap.java    # Thread-Safe generic Queue mapping
 ┣ 📂 server
 ┃ ┣ 📜 SpotifyServer.java         # Thread Pool (ExecutorService) init
 ┃ ┗ 📜 ClientHandler.java         # Runnable protocol executor
 ┗ 📂 client
   ┗ 📜 SpotifyInteractiveClient.java  # CLI Client for interactive testing
```

## Getting Started
### 1. Compilation
Navigate to the root directory and compile the entire project structure:

```bash
javac sdis/spotify/common/*.java sdis/spotify/utils/*.java sdis/spotify/server/*.java sdis/spotify/client/*.java
```

### 2. Execution
Start the Server:

```bash
java sdis.spotify.server.SpotifyServer
```

Start the Interactive Client:

```bash
java sdis.spotify.client.SpotifyInteractiveClient
```

Interaction Example
Client Terminal (Adding a song):
```bash
--- SPOTIFY MENU ---
1. Login
2. Add Song to Playlist
3. Read Playlist
4. Delete Playlist
Select an option: 2
Playlist Name: Rock Classics
Song Name: Stairway to Heaven
>> Sending: ADD2L: Rock Classics -> Stairway to Heaven
<< Received: ADDED
```

Server Logs:
```bash
SERVER: [STARTING] on Port 2000
SERVER: New connection from 127.0.0.1
Handler [0]: Processed ADD2L from 127.0.0.1 successfully.
```

### Authors

-Iván Moro Cienfuegos, David Martín Sebastián, Eric Soto San José and Héctor
