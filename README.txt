=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 1200 Game Project README
PennKey: 13584473
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

===================
=: Core Concepts :=
===================

- List the four core concepts, the features they implement, and why each feature
  is an appropriate use of the concept. Incorporate the feedback you got after
  submitting your proposal.

  1. Collections

  The class, GameObj, uses a LinkedList of Points to store the coordinates of the game objects.

  The linked list allows easy insertion, and removal of elements (via add() and remove(),
  suitable for representing the changing positions of game objects.

  The intersects method iterates through the linked list, checking for bounding box overlap.
  If an intersection is found, it removes the intersecting point from the linked list.
  This allows efficient collision detection and removal of intersecting points.

  2. File I/O

  To read images from files to display them as "game over" and "instructions" images.
  Also reads the images of the game objects and draws them.

  To read the best score from a file to initialize the bestScore variable.
  When a new best score is achieved, write it to the file, so the best score is saved for future games.

  To save the current state of the game (positions, velocities, and local score) to a file.
  This allows the user to resume the game later.

  To write the coordinates of game objects (snake segments, apple, and golden apple) to separate files.
  This is useful for restoring the game state.

  To read the saved game state from a file, allowing the user to reload and continue the game from where they left off.

  3. Inheritance

  Abstract class GameObj, which provides common attributes such as position, size for different game objects.
  Suitable to create a common base class that encapsulates shared attributes and methods among different game objects.


  Apple and GoldenApple classes extends GameObj and implement the Food interface.
  They Overrides draw(Graphics g) and intersects(GameObj that) methods for specific apple behavior.
  Inheritance is appropriately used to reuse the common attributes and behaviors from the GameObj class.
  The Food interface ensures that the apples implement their own version of updateSnake(Snake snake) method
  via dynamic dispatch.

  Snake also extends GameObj, introducing additional fields and methods specific to the behavior of the snake.

  Food interface declares the updateSnake(Snake snake) method without providing its implementation.
  Appropriate to define a contract (updateSnake) that classes like Apple and GoldenApple must implement.

  4. JUnit

  Use of modular, encapsulated classes. Each class in the game encapsulates specific functionality,
  making it easier to test individual components independently.

  For example, RunSnake is responsible for starting the game and setting up the GUI,
  GameCourt manages the game logic, and Snake, Apple, and GoldenApple represent the game objects.

  Each class encapsulates its state and behavior. For example, the Snake class encapsulates the snake's state
  and movement logic, allowing for testing its specific behavior without worrying about the entire game state.

===============================
=: File Structure Screenshot :=
===============================
- Include a screenshot of your project's file structure. This should include
  all of the files in your project, and the folders they are in. You can
  upload this screenshot in your homework submission to gradescope, named 
  "file_structure.png".

=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.


- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?

  I tried to implement a 2D array of type object to represent the overall board,
  so that each element represents a snake, apple, or a white square object


- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you refactor, if given the chance?

  Functionality: Each class has a specific responsibility, which contributes to a modular design.
  For example, GameCourt manages the game logic, and RunSnake handles the initialization and GUI setup.

  Private State: It is encapsulated within individual classes via use of private fields,
  along with getter and setter methods for controlled access to the internal state.

  Refactor: File Operations in GameCourt could be defined in a separate class.

  The RunSnake class contains both GUI setup and initiation of the game logic.
  Better to introduce separate class responsible for game initialization.


========================
=: External Resources :=
========================

- Cite any external resources (images, tutorials, etc.) that you may have used 
  while implementing your game.

  Game: https://www.coolmathgames.com/0-snake
  Red Apple: https://images.app.goo.gl/tsYcJkRkQ5sATAuz8
  Golden Apple: https://images.app.goo.gl/j4ZLNL6RnakE64Pp8
  Canva to design the Instruction Screen: https://www.canva.com



