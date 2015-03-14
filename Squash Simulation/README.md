# Project that is submitted as a challenge for the module 387com (Mobile Application Development) at Coventry University (Spring, 2015)

## Overview
This project was initially created as an exercise for Android development and 3D-modeling with OpenGL for Embedded Systems. This project was started about a year ago (February 2014) and developed in my spare time. In the course of the module taught at Coventry University and for this submission, I improved various aspects of the application. The changes that were made for this submissions are those from [commit e3fe0795fee0f63f6d5da3eb36cf54ab52338beb](https://github.com/furgerf/Squash/commit/e3fe0795fee0f63f6d5da3eb36cf54ab52338beb) onwards.

The purpose of this project is to simulate the environment on a glass squash court with realistic physics. I will try to give a "brief" overview over the project.

## Interesting Things
### Data Persistence
The user can change various aspects of the squash environment, and about 25 different settings are automatically stored in SharedPreferences. These settings range from the initial position of the ball to the color of the seats and many more.

All settings can be found in the menu of the application, with shortcuts to individual submenus. This is organized with different `PreferenceScreen`s and `PreferenceCategory`.

### Graphics
The squash world is drawn with OpenGL ES which is required to run this application. For that reason, it is specified in the AndroidManifest so that only phones that are capable of running OpenGL ES 2.0 or later would see the application on the Play Store.

The core of the graphics are in `SquashRenderer` which holds references to the different objects needed for drawing such as transformation matrices, shaders, shapes, and so forth. Furthermore, it contains the code that is run when the graphical representation is created and updated on each frame.

Graphical objects (referred to as shapes) such as `Ball`, `Chair`, `Quadrilateral`, ... all inherit from `AbstractShape`. Each shape has data that is required by the renderer to draw the shape like the vertices, colors, and (sometimes) normal vectors. The normal vectors are required for calculating light effects by the shaders which isn't completely implemented and thus turned off. The standout shape is the `Ball` that can be configured with different numbers of edges to achieve different levels of "roundness" (and memory/GPU consumption).

The shapes used by the `SquashRenderer` are organized into `ShapeCollection`s; currently there are collection "court", "chairs", "arena". There, the actual shapes are split into transparent and opaque shapes. This is necessary since all opaque shapes have to be drawn first so that they aren't "hidden" by transparent shapes.

### Physics
Several classes are used to implement physics, with the base probably being the `IVector` interface. This interface is implemented by `PhysicalVector` and `Vector`. `PhysicalVector` is basically just a wrapper around a `Vector` that adds a graphical representation to it. The `Vector` implements geometric functions such as calculating its length or the cross product with another `IVector`.

Shapes that aren't stationary (currently only the `Ball`) have a `Movable` object that keep track of the shape's moving parameters like applied forces or current speed (in the form of `IVectors`). Furthermore, it handles the shape's movement over a specified amount of time (by default around 50ms). This includes reacting to collisions. Collisions are detected in the `Collision` class where current position, speed and time are taken into account to determine whether the `Ball` collides with any of the solid shapes. If so, various information about the collision is calculated (e.g. exact place, exact time, angle of the collision, ...).

All movement is controlled by the `MovementEngine`.

### Sound Effects
Sound effects are played by the `MovementEngine` when collisions occur. These effects are stored in mp3-format as raw resources.

### Touch Gestures
The main activity, `SquashActivity`, implements the `OnDoubleTapListener` and `OnGestureListener` interfaces to intercept some complex gestures on the `SquashView` (the area where the squash environment is drawn). These gestures are:

* swipe up: hides/shows user interface which consists of an `ActionBar` and the `Button`s on the bottom of the screen
* swipe down: same as swipe up
* long press: resets position and speed of the movable objects
* double tap: assigns a random speed vector to the ball
* single tap: starts/stops the `MovementEngine`

### Unit Tests
Many of the geometric methods are fairly complex and difficult to get right. This lends itself to unit testing, which is done in the separate test project "Squash Simulation Test". Without these tests I'm sure I'd still have cases where the ball falls through the floor...

## Reflection
Overall, this has been and is a highly educational project and I'm happy that I could improve it for this submission. There are many many features that I want to add (a list of which can be seen in the [todo](https://raw.githubusercontent.com/furgerf/Squash/master/Squash%20Simulation/todo.txt) file).

One of the major learning experiences with this project is how to plan for larger-than-school-project size applications. Several times I had to redesign certain parts of the application, which is never a fun thing to do. Getting back to work on the project this past few weeks and months I saw my design decisions from another perspective and, by far and large, am happy with the decisions I've made previously.

Since I was working on this project just for my own benefit it has certainly been lacking in terms of usability and configurability since I know how to use it and I could modify source files. With that in mind I think I managed to improve the quality of the application quite significantly.
