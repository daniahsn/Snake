# 🎮 CIS 1200 Snake Game Project

## ✅ Core Concepts

- **`RunSnake`**: Initializes the game window and GUI, including instructions and menus.
- **`GameCourt`**: Manages the main game loop, including object movement, collision detection, and user input.
- **`GameObj`**: Abstract base class for all visual game elements (position, size, etc.).
- **`Snake`**: Represents the snake; manages direction, growth, and self-collision logic.
- **`Apple` and `GoldenApple`**: Implement `Food` and define how different food types affect the game state.
- **Other utility classes**: Handle image loading, score tracking, and file operations.

This game implements four core Java concepts. Below is a breakdown of how each is applied and what features they support:

### 1. Collections

The `GameObj` class uses a `LinkedList<Point>` to store the coordinates of dynamic game elements, such as the snake’s segments. This data structure enables efficient insertions and deletions, making it well-suited for the frequently changing positions in the game.

The `intersects()` method iterates through the list to check for collisions using bounding box overlap. Upon collision, the relevant point is removed—enabling both detection and real-time updates of game state.

### 2. File I/O

File I/O is used for the following functionalities:

- **Reading images**: Used to display "Game Over" and instruction screens, as well as to render game elements like apples.
- **Score persistence**: The best score is read from and written to a file so it can persist across game sessions.
- **Saving and loading game state**: The snake’s segments, apples’ positions, and score are written to files so the game can be resumed later.
- **Game restoration**: On reloading, all object positions and game variables are reconstructed from saved files.

This use of file I/O supports both immersion (visuals) and usability (state persistence).

### 3. Inheritance

An abstract class `GameObj` defines shared attributes like position and size. This allows `Snake`, `Apple`, and `GoldenApple` to inherit common behavior while extending specific logic.

- `Apple` and `GoldenApple` both extend `GameObj` and implement the `Food` interface. They override methods like `draw(Graphics g)` and `intersects(GameObj that)` to define distinct behaviors.
- The `Food` interface enforces the contract `updateSnake(Snake snake)`—allowing for polymorphic handling of food items.
- `Snake` also extends `GameObj` and adds custom fields/methods specific to movement and growth.

This use of inheritance promotes code reuse and enforces structure across game entities.

### 4. JUnit

Encapsulation in this project allows for unit testing of independent classes:

- `RunSnake` handles UI setup and game start logic.
- `GameCourt` manages the game loop and state transitions.
- `Snake`, `Apple`, and `GoldenApple` encapsulate object-specific state and behavior.

This modular design makes it easier to test classes in isolation and ensures correctness of individual components.


## 🌐 External Resources

- Game inspiration: [Cool Math Games - Snake](https://www.coolmathgames.com/0-snake)
- Red Apple Image: [Google Images](https://images.app.goo.gl/tsYcJkRkQ5sATAuz8)
- Golden Apple Image: [Google Images](https://images.app.goo.gl/j4ZLNL6RnakE64Pp8)
- Instruction screen design: [Canva](https://www.canva.com)



