Play! with Java @ LAJUG 9/10/13
==============================

Intro
-----

This is going to be a hands-on demonstration, so I really encourage everyone to
bring a laptop and pairup. I will do a brief amount of power-pointish stuff,
but primarily my goal is to demo Play! and give you a chance to experience a
bit of it first-hand.

Setup
-----

I'll be using linux with Intellij 12. First, if you don't have IntelliJ 12, you
can download free 30-day trial at http://www.jetbrains.com. You will want to 
install the `Play 2.0` plugin and maybe scala plugin as well.

Next, you may want to have Scala installed on your computer because we'll do a
tiny bit of playing with it at the command line, here http://www.scala-lang.org. 
If you are having any difficulty with this, I wouldn't worry about it too much 
as the Play! application has a scala repl you can use.

Next, you'll want to have play installed http://www.playframework.com/.

Creating A Hello World Application
----------------------------------
Play is a "full-stack" framework. It includes a built-in server (netty) and
build system (sbt). It is possible to create stand-alone war files to deploy
to other servers and different build tools, such as gradle or maven. However,
if you have play installed, then you have everything you need. Netty handles
really well, and although sbt has a reputation for being complex, within the
context of a Play! application, it is pretty simple to use.

At the command line and go to the location where you wish to create your
play application, then type:

    % play new hello

You will get a dialog box asking you the name of the application and whether it
should be a java or scala application. Your application file structure should
look something like this:
    
    % tree
      .
      ├── app
      │   ├── controllers
      │   │   └── Application.java
      │   └── views
      │       ├── index.scala.html
      │       └── main.scala.html
      ├── conf
      │   ├── application.conf
      │   └── routes
      ├── project
      │   ├── build.properties
      │   ├── Build.scala
      │   └── plugins.sbt
      ├── public
      │   ├── images
      │   │   └── favicon.png
      │   ├── javascripts
      │   │   └── jquery-1.9.0.min.js
      │   └── stylesheets
      │       └── main.css
      ├── README
      └── test
          ├── ApplicationTest.java
          └── IntegrationTest.java

The `conf/routes` file maps http verb/uri combinations to specific controller
functions. The `project/Build.scala` is an sbt configuration where you will
declare your managed dependencies. The rest should be self-explanatory at this
point. Now, cd into the directory and do

    % play clean reload update compile test run

Give it a moment to start up, and if you point your browser to localhost:9000,
you should see the basic hello-world application running. Now, a bit of
explanation about these commands.

* reload: you will only need to do this if you make changes to Build.scala
  and wish to make play aware of those changes while in interactive mode.
* update: retrives new dependencies you've specified in Build.scala to a local
  repository.

If you make these sbt actions from the command line, you can specify multiple
steps, but in interactive-mode, you can only do one at a time.

Now, stop the application and enter play interactive mode:

    % play

Within interactive mode, we want to create the intellij project files, so do
the following command:

    [hello] $ idea with-sources=yes no-sbt-build-module

This will create the idea project files. Note that every time you update the
dependencies, you will want to do `% play clean reload update compile`, delete
the existing intellij files (I do `rm -rf .idea*` at the command line), and
then rebuild the idea project files from within play (This is a kind of a pain,
but you shouldn't need to change the dependencies all that often).

`with-sources=yes` says that you want intellij to have the source code. Of
course, much of the source code will be scala, so including that depends upon
whether you wish to navigate through the sources from within intellij.
`no-sbt-build-module` says that the intellij project will have just one module
for the project (as opposed to two: one for the source code and the other for
the build). In my experience, it is better to let play itself manage building and
compiling, though it is possible to have intellij handle it.

Next, start intellij idea. You should already have the play 2.0 and scala
plugins installed. Now open the project. If you go to File -> Project Settings,
you should see 2 errors within a red bar on the bottom. If you click on the
text `2 errors found`, it will tell you:

* Library 'Scala 2.9.2' is not used
* Library 'Scala 2.10.0' is not used

To the right, you should see a little light bulb for each error. Clicking on it
will present 2 choices:

* Add to Dependencies
* Remove Library

For Scala 2.9.2, remove the library, and for Scala 2.10.0, add it to
dependencies. Scala 2.9.2 is used by sbt. And you'll need Scala 2.10.0.

Next, in the upper-left-hand side of the Project Structure dialog box you'll
see an area called `Project Settings`, go ahead and select `Modules`. In the
right-side of the screen, select the `Sources` tab and you'll be presented with
a list of source/test/excluded folders as well as a project tree. Within the
list, start by clicking the `x` to remove everything except the Content Root
(all source, test, and excluded folders).

Now, within the project tree, directories as Sources/Test/Excluded as follows:

* Sources: /app, /conf, target/scala-2.10/src_managed
* Excluded: /.idea, .idea_modules, /target/resolution-cache,
  /target/scala-2.10/cache, /target/scala-2.10/classes,
  /target/scala-2.10/classes_managed, /target/streams
* Test: /test

Congrats!, You've built your first Play! application and managed to get
IntelliJ to understand your project. You're off to a great start!

Outline for the Presentation
----------------------------

For the presentation, we'll use the first couple of minutes to make sure that
everyone was able to get Play installed, run the `hello` project, and open it
within Intellij.

Next, I'll spend a few minutes to explain a few constructs of the scala
language (for-comprehension, map, and List, and lambda expressions). Instead of
a separate markup language (like JSP), Play uses plain old Scala constructs for 
the view templates, so you'll need a smattering of it to write the views.

Next I'll explain a bit about `conf/routes` `conf/application.conf`, 
`project/Build.scala`, `app/controllers/Application.java`, and 
`app/views/main.scala.html`

Finally, we'll work through the existing hello project and make a few changes,
illustrating how all the pieces flow together. If we have a bit of extra time,
I can also show everyone a bit of Akka, a very important library (both java
and scala) that uses Erlang constructs to address concurrency. Besides Actors,
Akka also makes use of Futures/Promises, which you will will probably encounter
in Play! should you do any non-trivial projects with it.
