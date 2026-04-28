# Tomasulo Algorithm Simulator

## 📌 Overview

This project is a Java-based simulator for the Tomasulo algorithm, developed as part of a Microprocessors course. It demonstrates how modern processors achieve instruction-level parallelism using dynamic scheduling.

The simulator executes instructions cycle-by-cycle while tracking the state of registers, reservation stations, buffers, and execution units.

---

## 🧠 What is Tomasulo Algorithm?

Tomasulo’s Algorithm is a hardware algorithm used to improve the performance of out-of-order execution by handling data hazards dynamically.

This simulator models:

* RAW (Read After Write)
* WAR (Write After Read)
* WAW (Write After Write)
* Dynamic instruction scheduling
* Register renaming concepts

---

## 🚀 Features

* Cycle-by-cycle execution simulation
* Reservation stations and buffers handling
* Load/Store operations with memory simulation
* Arithmetic operations (Add, Sub, Mul, Div)
* Instruction parsing and execution tracking
* Hazard detection and resolution
* Modular and extensible architecture

---

## 🛠️ Tech Stack

* Language: Java
* Architecture: Object-Oriented Design
* Build Tool: Maven (pom.xml)

---

## 📁 Project Structure

* `core/` → Main logic (Tomasulo implementation)
* `gui/` → User interface (if implemented)
* `parser/` → Instruction parsing
* `memory/` → Memory and cache simulation

---

## ⚙️ How to Run

```bash
# Compile project
mvn clean install

# Run the simulator
mvn exec:java
```

---

## 🧪 Example Capabilities

The simulator supports execution of instruction sequences including:

* Sequential code
* Loop-based code
* Mixed hazards scenarios

It demonstrates how instructions move through:

* Issue
* Execute
* Write-back stages

---

## 👥 Team Contribution

This project was developed as part of a team. My contribution focused on implementing core logic, execution flow, and system behavior.

---

## 📌 Notes

* This repository is a personal copy for portfolio purposes
* Based on academic project requirements
* Designed for educational and simulation purposes only
