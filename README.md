# 🕹️ Minesweeper Android Game

This is a super simple implementation of the classic **Minesweeper** game, created for Android using **Kotlin**, **Compose UI**, and **Canvas Drawing**.

## ✨ Features

- **5x5 grid** with **3 randomly placed mines**.
- Two game modes:
  - **Try a Field**: Reveal numbers showing how many mines are nearby. If you click on a mine, the game ends.
  - **Place a Flag**: Flag cells where you suspect a mine is located. You win by either flagging all the mines or uncovering all non-mine cells.
- **Random mine placement** for each game, making each playthrough unique.
- Notifications to indicate whether you've won or lost the game.

## 📋 Requirements

- Android Studio
- Kotlin (for development)
- Android 5.0 (API level 21) or higher

## 🚀 Installation

1. Clone or download the repository:
   ```bash
   git clone https://github.com/yourusername/minesweeper-for-android.git
2. Open the project in Android Studio.
3. Build and run the project on your device or emulator.

## 🎮 Usage
1. Open the app and choose between Try a Field or Place a Flag mode.
2. In Try a Field, click cells to reveal numbers or hit mines. If you hit a mine, you lose!
3. In Place a Flag, flag suspected mines.
4. The app provides feedback through AlertDialogs to show whether you've won or lost.
