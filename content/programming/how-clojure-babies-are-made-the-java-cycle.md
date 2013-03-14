---
title: "How Clojure Babies Are Made: Compiling and Running a Java Program"
created_at: Mar 12 23:23:00 -0500 2013
kind: article
categories: programming
summary: "If you're at all like me, the moment you got your first Clojure program running you belted out, \"SOOO MUUUUUUUCH POOOOOOOWEEEEEEEER!\" and thrust one or both fists into the air. Then, fifteen minutes later, you found yourself muttering things like \"What's a maven?\" and \"Classpath what now?\" and \"What is Leiningen actually doing, anyway? Sure, those are the most handsome mustaches I ever laid eyes on, but can I really trust them?\""
additional_stylesheets:
  - pygments
---

If you're at all like me, the moment you got your first Clojure
program running you belted out, "SOOO MUUUUUUUCH POOOOOOOWEEEEEEEER!"
and thrust one or both fists into the air. Then, fifteen minutes
later, you found yourself muttering things like "What's a maven?" and
"Classpath what now?" and "What is Leiningen actually doing, anyway?
Sure, those are the most handsome mustaches I ever laid eyes on, but
can I really trust them?"

This post is here to help you out. In it, I will describe how Java
compiles and runs programs, and how this relates to Clojure. In an
upcoming post, I will also describe how Leiningen makes this process
way easier. Here's an outline of what's to come:

* Java
    * Compilation
        * javac
        * Class lookup rules & classpath
        * imports
        * packages
        * JAR files
    * Running
        * Class file
        * classpath
        * Executable JAR
* Leiningen
    * Running an app without Leiningen
    * classpath management
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
we actually care about: Clojure. Along the way, you will learn about:

## Compiling and Running a Basic Program

This section will lay a foundation for your understanding of Java. It
doesn't address Clojure directly, but the knowledge you gain will be
useful in your Clojuring.

Let's put our game faces on and start with a basic program. Visit the
[github repo](https://github.com/flyingmachine/make-a-clojure-baby)
for the examples in this post and clone that baby for all you're
worth.

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

```bash
$ ls
ShiverMeTimbers.class ShiverMeTimbers.java
```

You've just compiled your first Java program, son! Now run it with
`java ShiverMeTimbers`. You should see:

```
Shiver me timbers!!!
```

Which I'm pretty sure is like the Lord's prayer, but for pirates.
Anyway, what's happening here is you used the Java compiler, `javac`,
to create a Java class file, `ShiverMeTimbers.class`. This file is
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

In the next section you'll learn about handling program code that's
spread over more than one file. If you don't remove your socks now,
they're liable to get knocked off!

## Packages and Imports

In this section, you'll learn about how Java handles programs which
are spread over more than one file. You'll also learn how to use Java
libraries. Once again, we'll look at both compiling and running a
program. This section has direct implications for Clojure programming,
so pay attention!

Let's start with a couple definitions:

* **package:** Similar to Clojure's namespaces, packages serve two
  purposes. They provide code organization, just like clojure
  namespaces. They also enforce access rules, which we don't really
  care about. The directory that a Java file lives in must mirror the
  package it belongs to. If a file has the line `package com.shapemaster`
  in it, then it must be located at com/shapemaster somewhere on your
  classpath. More about classpath later.
* **import:** Java allows you to import classes, which basically means
  that you can refer to them without using their namespace prefix. So,
  if you have a class in `com.shapemaster` named `Square`, you could
  write `import com.shapemaster.Square;` or `import com.shapemaster.*;`
  at the top of a `.java` file so that you can use `Square` in your
  code instead of `com.shapemaster.Square`. Code example below.

Now let's see `package` and `import` in action. Here they are as used
by the files in 
`make-a-clojure-baby/ContrivedPackageExample`. Pay
attention to the comments, as they explain a lot of what's going on

```java
// Contents of:  make-a-clojure-baby/ContrivedPackageExample/Conversation.java
public class Conversation
{    
    public static void main(String[] args)
    {
        // The "ns1" prefix is necessary because ShyGhost belongs to
        // the "ns1" package, and we haven't imported the classes in
        // that package
        ns1.ShyGhost shyGhost = new ns1.ShyGhost();
        shyGhost.talk();
    }
}

////////
// Contents of: make-a-clojure-baby/ContrivedPackageExample/ns1/ShyGhost.java
// The classes defined in this file belong to the "ns1" package.
// Notice that this file is in the "ns1" directory.
package ns1;

// This basically means, "I hate typing the namespace prefix all the
// time so please allow me to not do that"
import ns2.*;

public class ShyGhost
{
    public void talk() {
        // the shy ghost can't speak for himself and has to get
        // someone else to do it for him
        
        // Notice that even though SuperManlyIguana belongs to the ns2
        // namespace, we don't have to use the ns2 prefix
        SuperManlyIguana smi = new SuperManlyIguana();
        smi.talk();
    }
}


////////
// Contentsof make-a-clojure-baby/ContrivedPackageExample/ns2/SuperManlyIguana.java
// The classes defined in this file belong to the "ns2" package
package ns2;

public class SuperManlyIguana
{
    public void talk()
    {
        System.out.println("Why hello there");
    }
}

```

You can run all the above code with the following:

```
cd make-a-clojure-baby/ContrivedPackageExample
javac Conversation.java
java Conversation
```

Can you guess what this outputs? I bet you can!

Anyway, so far we've established the relationship between importing,
packages, and directory structure: Packages organize code and require
a matching directory structure. Importing classes allows you to
"de-namespace" them.

One piece that's missing, which I alluded to above, is the role of the
classpath. Try the following:

```
cd make-a-clojure-baby/ContrivedPackageExample/ns1
javac ../Conversation.java
```

Boom! The Java compiler just told you to hang your head in shame, and
maybe weep a little:

```
../Conversation.java:13: error: package ns1 does not exist
        ns1.ShyGhost shyGhost = new ns1.ShyGhost();
           ^
../Conversation.java:13: error: package ns1 does not exist
        ns1.ShyGhost shyGhost = new ns1.ShyGhost();
                                       ^
```

It thinks that the `ns1` package doesn't exist. But that's stupid,
right? I mean, you're in the `ns1` directory and everything!

What's happening here is that the java compiler is looking for
`./ns1/ShyGhost` which it can't find because you're already in the
`ns1` directory. This is because the default classpath includes '.'.
Without changing directories, try running `javac -classpath ../
../Conversation.java`

Et voila! It works! So let's amend our understanding of the
relationship between importing, packages, and the directory
structures: when you're compiling a Java program, Java searches your
classpath for packages.

Guess what: the same things happens when you're running a Java
program, too. Run the following:

```
cd make-a-clojure-baby/ContrivedPackageExample
mkdir hide
mv ns1 hide
java Conversation
```
Another explosion! Now try:

```
java -classpath .:hide Converstaion
```

Success!

I hope this clarifies the relationship between your directory
structure, the classpath, packages, and importing.

**Key Ideas**

* Packages provide a way for you to organize classes. Your directory
  structure must reflect your package names
* Imports allow you to be lazy and not prefix a class name with its
  package
* `javac` and `java` search the classpath for packages

## JAR Files

JAR, or Java ARchive, files allow you to bundle all your .class files
into one single file. Run the following:

```
cd make-a-clojure-baby/ContrivedPackageExample
jar cvfe contrived.jar Conversation *.class ns*/*.class
java -jar contrived.jar
```

It works, just like before. You bundled all the class files into
`contrived.jar` with the file patterns `*.class` and `ns*/*.class`.
You also indicated that the `Conversation` class is the "entry point"
with the `e` flag. The "entry point" is the class which contains the
`main` method which should be executed when the JAR as a whole is run.

Behind the scenes, java knows which class is the entry point because
when you create a jar, the file `META-INF/MANIFEST.MF` automaticaally
gets generated and added. When you add the `e` flag, the following
line gets added:

```
Main-Class: Conversation
```


By the way, you might be wondering why Java isn't throwing any
exceptions like "can't find package". The reason is that the JAR file
maintains the directory structure. You can see its contents with:

```
jar tf contrived.jar
```

You'll see that the directory structure is maintained.

## Pulling it All Together: clojure.jar

Let's pull all of this together with some Clojure! Download
[the 1.5.1 stable release](http://repo1.maven.org/maven2/org/clojure/clojure/1.5.1/clojure-1.5.1.zip)
and unzip it. Then `cd` to the directory that gets created and run

```
java -jar clojure-1.5.1.jar
```

You should get that most soothing of sights, the Clojure REPL. So, how
did it actually start up? Let's have a look at `META-INF/MANIFEST.MF`
in the jar file:

```
Manifest-Version: 1.0
Archiver-Version: Plexus Archiver
Created-By: Apache Maven
Built-By: hudson
Build-Jdk: 1.6.0_20
Main-Class: clojure.main
```

It looks like `clojure.main` is specified as the entry point. Where
does this class come from? Well, have a look at
[clojure/main.java on github](https://github.com/clojure/clojure/blob/master/src/jvm/clojure/main.java):

```java
/**
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 *   which can be found in the file epl-v10.html at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 **/

package clojure;

import clojure.lang.Symbol;
import clojure.lang.Var;
import clojure.lang.RT;

public class main{

final static private Symbol CLOJURE_MAIN = Symbol.intern("clojure.main");
final static private Var REQUIRE = RT.var("clojure.core", "require");
final static private Var LEGACY_REPL = RT.var("clojure.main", "legacy-repl");
final static private Var LEGACY_SCRIPT = RT.var("clojure.main", "legacy-script");
final static private Var MAIN = RT.var("clojure.main", "main");

public static void legacy_repl(String[] args) {
    REQUIRE.invoke(CLOJURE_MAIN);
    LEGACY_REPL.invoke(RT.seq(args));
}

public static void legacy_script(String[] args) {
    REQUIRE.invoke(CLOJURE_MAIN);
    LEGACY_SCRIPT.invoke(RT.seq(args));
}

public static void main(String[] args) {
    REQUIRE.invoke(CLOJURE_MAIN);
    MAIN.applyTo(RT.seq(args));
}
}
```

As you can see, the class `main` is defined. It belongs to the package
`clojure` and defines a `public static main` method. This is all Java
needs to run a program.

I hope this has helped clarify Java and how it relates to Clojure! In
my next post, I'll dig in to Leiningen. Fun for everybody!!!
