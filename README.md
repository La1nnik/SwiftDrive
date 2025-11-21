# SwiftDrive - control you SwiftBot using a playsation controller


<p align="center">
  <img src="https://img.shields.io/badge/Java-17+-blue" />
  <img src="https://img.shields.io/badge/Platform-SwiftBot-0A82FF" />
  <img src="https://img.shields.io/badge/Input-PS%20Gamepad-7957D5" />
  <img src="https://img.shields.io/badge/License-MIT-yellow" />
</p>


A responsive and safe controller interface for the **SwiftBot robot**, using a **PlayStation-style gamepad**.
Includes smooth movement control, a color-fading underlight animation running in a dedicated thread

---

## ✨ Features

* 🎮 **Analog joystick drive control**

  * Smooth cubic-turn calculations
  * Deadzone filtering
* 🌈 **Underlight fading system**

  * Runs in a background thread
  * Can be toggled live with the D-pad

* 🛠 Built with **JInput + SwiftBotAPI**

---

## 🎮 Controls

| Input            | Action                         |
| ---------------- | ------------------------------ |
| **Left Stick**   | Drive and turn the robot       |
| **D-Pad Left**   | Toggle underlight fader ON/OFF |
| **Share Button** | Stop robot and exit safely     |

---



## 🧠 How It Works

### Main Loop

* Polls gamepad every 50 ms
* Reads analog axes
* Applies deadzone shaping
* Calculates tank-drive left & right wheel speed
* Sends movement to the robot

### Underlight Thread

Runs independently:

```text
START → waits for running = true
    → fades colors smoothly
    → pauses again if toggled off
```


## 🚀 Getting Started

### Requirements

* Java 17+
* SwiftBot API JARs
* JInput JAR + native libraries
* SwiftBot hardware with I2C enabled

### To Start Run Command

```bash
sudo java -Djava.library.path=. \
 -cp .:SwiftBot-API.jar:jinput.jar:jinput-natives.jar \
 TestingGamepad
```

---

## 💡 Future Improvements

* Add support for different gamepands and input devices
* Add button layot customization 
  

---

## 📜 License

MIT License
You’re free to use, modify, and copy this project.

