# Getting started with URE

## 1. Clone the repository

Clone the URE github repo into a fresh project folder on your local workstation.

```
git clone https://github.com/gilmore606/ure.git
```

## 2. Import the project

Import the ``build.gradle`` file from the project root into your IDE as a new project.  URE is mostly developed with IntelliJ IDEA;
however, any Java IDE such as Eclipse that supports Gradle integration should work.  Gradle should auto-install and then pull in the
dependencies for the project.

## 3. Create a runconfig

(IntelliJ) Click Edit Configuration... in the upper right next to the green Run button, and create a new Gradle run config.
For the Gradle task enter 'run'.  You should now be able to click Run to build and run URE's example game.

## 4. Start hacking

Although URE is provided as an importable package, and can be used this way for some purposes, most game developers will want to
customize and extend the URE base classes such as Thing and Actor.  This requires operating directly on the URE source.

We recommend you follow some simple practices to ensure minimal merge conflicts in the future, should you choose to merge URE
updates and fixes back into your project.

- Use the event bus to respond to game events, rather than hacking directly into engine methods to trigger on things that happen in the engine.
- If you must add to a URE method, call out to a method of your own which does all the work; minimize the change you make to the URE methods.

A good way to get started is to follow the included ExampleGame as a pattern.  Create a new package in the project 
for your game.  Make a new main class for your game in that
package, and modify ExampleMain.java to call your main class.  Then use the code from ExampleGame.main() as a starting point.
