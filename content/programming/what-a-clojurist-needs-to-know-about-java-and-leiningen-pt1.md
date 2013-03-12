---
title: How Clojure Babies Are Made: a Tale of Leiningen and Java
created_at: Mar 11 23:23:00 -0500 2013
kind: article
categories: programming
summary: A way to avoid namespace collisions.
draft: true
---

If you're at all like me, the moment you got your first Clojure
program running you belted out, "SOOO MUUUUUUUCH POOOOOOOWEEEEEEEER!"
and thrust one or both fists into the air. Then, fifteen minutes
later, you found yourself muttering things like "What's a maven?" and
"Classpath what now?" and "What is Leiningen actually doing, anyway?
Sure, those are the most handsome mustaches I ever laid eyes on, but
can I really trust them?"

This post is here to help you out. In it, I will describe how Java
compiles and runs programs, and how this relates to Clojure. I will
then describe how Leiningen makes this process way easier. Here's an
outline of what's to come:

* Java
    * Compilation
        * javac
        * Class lookup rules & classpath
        * imports
        * packages
        * jar files
    * Running
        * Class file
        * classpath
        * Executable Jar
* Leiningen
    * Running an app without Leiningen
    * CLASSPATH management
    * Incorporating Clojure automatically
    * Dependency managemenet

## Java

Java is going to rule the universe. The sooner you accept that, the
better. If you don't have the
[JDK installed](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
installed, you might want to do that now. To check, just try running
`java` and `javac` in your terminal. By the way - this post assumes
you're somewhat familiar with the terminal. It also assumes you're
familiar with object-oriented programming.

In this section, we're going to learn just enough Java to understand
what the hell is going on when we run programs written in the language
we actually care about: Clojure.

### Compiling and Running a Basic Program

Let's put our game faces on and start with a basic program. Visit the
[github repo](https://github.com/flyingmachine/make-a-clojure-baby)
for this post and clone that baby for all you're worth.

In the folder `ShiverMeTimbers`, you should find an article named
`ShiverMeTimbers.java`. Here are its contents:

```java
public class ShiverMeTimbers
{
    public static void main(String[] args)
    {
        System.out.println("Shiver me timbers!!!");
    }
}
```

Once you've recovered from your swoon, `cd` to `ShiverMeTimbers` and
run `javac ShiverMeTimbers.java`. If you typed everything correctly
*and* you're pure of heart, you should now see a file named
`ShiverMeTimbers.class`.

```shell
$ ls
ShiverMeTimbers.class ShiverMeTimbers.java
```

You've just compiled your first Java program, son! Now run it with
`java ShiverMeTimbers`. You should see:

```shell
Shiver me timbers!!!
```

Which I'm pretty sure is like the Lord's prayer, but for pirates.
Anyway, what's happening here is you used the Java compiler, `javac`,
to create a Java class file, `ShiverMeTimbers.java`. This file is
packed with oodles of Java bytecode which the
[Java Virtual Machine](http://en.wikipedia.org/wiki/Java_virtual_machine)
executes when running a program.

When you ran `java ShiverMeTimbers`, the JVM first looked on your
`classpath` for a class named `ShiverMeTimbers`. In Java, you're only
allowed to have one public class per file and the filename and class
name must be the same. This is how `java` knows to try looking in
`ShiverMeTimbers.class` for the ShiverMeTimbers class's bytecode.

Also, by default, the `classpath` includes the directory `.`, or the
current directory. Try running `java -classpath /tmp ShiverMeTimbers`
and you will get an error, even though `ShiverMeTimbers.class` is
right there in the same directory.

After `java` found the bytecode for the `ShiverMeTimbers` class, it
executed that class's `main` method. Java's kind of like C that way,
in that whenever you say "run something, and use this class as your
entry point", it always will run that class's `main` method. Which
means that that method has to be `public`, as you can see above.
([Here's more info on Java's access modifiers](http://docs.oracle.com/javase/tutorial/java/javaOO/accesscontrol.html)
if you're curious.)

**Key Ideas**

* `javac` compiles java source code to bytecode which the JVM can
  execute
* `java` searches the `classpath` for the class you specified and
  executes its `main` method. That method must be public.  

In the next section you'll learn enough about packages, imports, and
jars to understand what's happening when you run

```shell
java -cp clojure-1.4.0.jar clojure.main
```

### Packages, Imports, and Jars

