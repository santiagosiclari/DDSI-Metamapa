# Information Systems Design Project - 2025

<h1 align="center">
MetaMapa
<p align="center">
  <img src="https://i.imgur.com/iAa9Kh5.png" alt="MetaMapa Logo" width="200">
</p>
</h1>

**MetaMapa** is an open-source, collaborative mapping platform developed as the annual academic project for the **Information Systems Design (DDSI)** course at **UTN FRBA**. The system is designed to collect, normalize, and aggregate geolocated information from multiple sources, ensuring data reliability through distributed logic.

---

## 🚀 System Overview

The core of MetaMapa is **intelligent data aggregation**. The platform goes beyond simple data visualization by utilizing consensus algorithms to validate the reliability of geolocated information before it is presented to the user.

### Key Features
* ✅ **Multi-Source Collection:** Ingests data from static sources (CSV), dynamic sources (External APIs), and user-generated content.
* ✅ **Data Curation:** Implements consensus algorithms to filter out erroneous or conflicting information.
* ✅ **Navigation Modes:** Supports both **Curated** (validated) and **Unrestricted** navigation views.
* ✅ **Scalable Architecture:** Built on decoupled microservices with scheduled background jobs for automated data synchronization.

---

## 🛠️ Tech Stack

The project leverages a modern, enterprise-grade stack focused on robustness and high-performance data processing.

| Component | Technology | Version |
| :--- | :--- | :--- |
| **Language** | **Java** | 21 |
| **Core Framework** | **Spring Boot** | 3.5.3 |
| **Persistence** | Hibernate / JPA | 5.4.18.Final |
| **Database** | SQL Server / MySQL | - |
| **Build Tool** | Maven | - |
| **Reactive Web** | Spring Webflux | - |
| **API Docs** | Swagger / OpenAPI | - |
| **Utilities** | Lombok (1.18.38), Cron-utils, OpenCSV | - |

---

## 🏗️ Microservices Architecture

MetaMapa follows a distributed microservices paradigm, allowing independent scaling and maintenance of each system component.

* **M-Agregador-Service:** The central engine managing data unification and business logic.
* **M-FuenteEstatica / Dinamica:** Dedicated services for handling various data ingestion protocols.
* **M-Usuarios-Service:** Identity management and role-based access control.
* **M-Estadistica-Service:** Analytics engine for system-wide metrics.
* **M-FuenteMetamapa:** Internal proxy for integrating system-specific data sources.

---

## 🌐 Infrastructure & Deployment

We utilize a professional **on-premise** infrastructure setup designed for security and reliability:
* **Containerization:** Fully orchestrated using **Docker** and managed via **Portainer**.
* **Security:** Integrated **Cloudflare Tunnels** to provide secure global edge access without exposing local ports.
* **Environment:** Dedicated servers running on **Linux** hardware.

---

## 👥 Team Members - Group 5

| Name | GitHub User |
| :--- | :--- |
| Fait, Agustin | [@AgustinFait](https://github.com/AgustinFait) |
| Knobel, Ignacio | [@ikiknobel](https://github.com/ikiknobel) |
| Ramazzi, Leandro | [@LRamazzi22](https://github.com/LRamazzi22) |
| Olivares, Lucas | [@Lucas-Olivares](https://github.com/Lucas-Olivares) |
| **Siclari, Santiago** | [**@santiagosiclari**](https://github.com/santiagosiclari) |
| Tantucci, Ignacio | [@ITantucci](https://github.com/ITantucci) |
| Vattimo, Lucrecia | [@lucreciavattimo](https://github.com/lucreciavattimo) |

---

## 📂 Documentation & Links

* 📄 [Project Guidelines (Spanish)](https://github.com/ITantucci/TP-DDSI/blob/main/Archivos/TPA%20DDSI%202025.pdf)
* 📐 [Design Diagrams (Architecture, Class, Sequence)](https://drive.google.com/drive/folders/1WRYC6QB1n6_0wvNXF7x73df-3QYI259v)
* 📊 **Swagger UI:** Accessible via specific service ports (Aggregator: 8080, Dynamic: 9001, Static: 9002).