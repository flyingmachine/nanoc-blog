---
title: "How Clojure Babies Are Made: Understanding lein run"
created_at: Mar 18 9:23:00 -0500 2013
kind: article
categories: programming
summary: "Leiningen reminds me a lot of Major Alex Louis Armstrong from Fullmetal Alchemist: <ul><li>They are both manly</li><li>They are both artistic</li><li>Sometimes you don't know what the f* they're saying (but it's amazing anyway)</li><li>Mustaches</li><li>Sparkles</li></ul>"
additional_stylesheets:
  - pygments
---

Leiningen reminds me a lot of Major Alex Louis Armstrong from
Fullmetal Alchemist:

![What artistry!](/assets/images/posts/leiningen/so-sparkly.png)

* They are both manly
* They are both artistic
* Sometimes you don't know what the f* they're saying (but it's
  amazing anyway)
* Mustaches
* Sparkles

Though the Strong Arm Alchemist will forever remain inscrutable, we do
have some hope in understanding Leiningen better. In this post, we'll
peek under Leiningen's lederhosen so that we can begin to learn
precisely what it does and how to make effective use of it in
developing, testing, running, and deploying Clojure applications.

Here's what we'll learn:

* How to build and run a Clojure program without Leiningen
* What happens when you run `lein run`
* Basic Leiningen architecture
* What happens when you run a Leiningen task

In the next post, we'll learn:

* How Leiningen manages dependencies
* How to distribute a full application
* How to distribute a library
* Other stuff Leiningen does

This post builds on
[How Clojure Babies Are Made: Compiling and Running a Java Program](/programming/how-clojure-babies-are-made-the-java-cycle/),
so make sure you understand everything in that post first. It doesn't
have any pictures of sparkly, sensitive, muscle-bound men though so if
that's what you're hoping for you're out of luck. We're here to learn
Clojure, dammit, not ogle cartoons!

## Brief Overview of Leiningen

Leiningen is the Swiss Army Bazooka of Clojure development. It
handles:

* **Project compilation.** Your Clojure has to get converted into Java
  class files somehow, and Leiningen automates the process.
* **Dependency management.** Similar to Ruby's bundler and gemfiles,
    Leiningen automates the process of resolving and downloading the
    Java jars your project depends on
* **Running tasks.** Similar to Ruby's Rake.
* **Deployment.** Helps with creating Java jars which can be executed
  and/or incorporated in other projects. Similar to Ruby gems.

## How to Build and Run a Clojure Program Without Leiningen

By understanding how to build and run a Clojure program manually,
you'll better understand what Leiningen does to automate the process.
We'll be working with the files under `leiningen/manual-build` which
you can get from the
[make a clojure baby repo](https://github.com/flyingmachine/make-a-clojure-baby).
All path references will be relative to `make-a-clojure-baby/leiningen/manual-build`.

The program we're compiling will make us fluent in the ways of German
Love:

```clojure
(ns learn-a-language.important-phrases
  (:gen-class))

;; It's time for some German love! 
(def german
  [["I love you." "Ich liebe dich."]
   ["You make me so happy!" "Du machst mich so glucklich!"]
   ["I miss you." "Ich vermisse dich./Du fehlst mir."]
   ["Pass me the mustard." "Gib mir den Senf."]   
   ["Kiss me!" "Kuss mich!"]])

(defn -main
  [which]
  (let [phrases (get german (Integer. which))]
    (println "English: " (first phrases))
    (println "German: " (second phrases))))
```

One important thing to note here is that we included the `:gen-class`
directive in the `ns` declaration. This tells Clojure to generate a
named Java class when it compiles the namespace. Remember that Java
requires a `public main` method within a `public` class to serve as
the entry point for the JVM. Using `:gen-class` allows us to use
`learn-a-language.important-phrases/-main` as the entry point.

Let's go ahead and compile. First, start up a Clojure repl (note that
the git repo includes the 1.5.1 release of Clojure as clojure.jar):

```
java -cp .:clojure.jar clojure.main
```

Notice that we specifed the classpath with `-cp .:clojure.jar`. This
does two things:

* It allows us to execute the `main` method in `clojure.main`
  similarly to what we saw at the end of
  [the last post](/programming/how-clojure-babies-are-made-the-java-cycle/)
* It adds the current directory to the classpath so that when you
  actually start loading Clojure files and compiling namespaces, the
  Clojure repl can find them. To see what I mean, try starting the
  repl with `java -jar clojure.jar` and then running the code below.

You should now have a Clojure repl running in your terminal. Copy and
paste the following lines into it:

```clojure
(load "learn_a_language/important_phrases")
(compile 'learn-a-language.important-phrases)
```

The first line reads the specified file. The second actually compiles
the `learn-a-language.important-phrases` namespace, generating a
boatload of Java class files in the `classes/learn_a_language`
directory:

```
$ ls classes/learn_a_language/
important_phrases$_main.class
important_phrases$fn__19.class
important_phrases$fn__48.class
important_phrases$loading__4910__auto__.class
important_phrases.class
important_phrases__init.class
```

(I won't go into into detail about the purposes of the various class files,
but you can start to learn more about that in
[clojure's compilation documentation](http://clojure.org/compilation).)

After going through the above steps, you might have a question on your
mind grapes: "Where did the `classes` directory come from?"

Oh, gentle-hearted reader. Your dedication to learning has touched my
heart. I shall answer your query: Clojure places compiled files under
`*compile-path*`, which is `classes` by default. Therefore, `classes`
must exist and be accessible from the classpath. You'll notice that
there's a `classes` directory in the git repo with a `.gitkeep` file
in it. Never change, dear, inquisitive reader. Never!

Now that we've compiled our Clojure program, let's get it running:

```
# Notice that you have to provide a numerical argument
$ java -cp classes:clojure.jar learn_a_language/important_phrases 0
English:  I love you.
German:  Ich liebe dich.

$ java -cp classes:clojure.jar learn_a_language/important_phrases 3
English:  Pass me the mustard.
German:  Gib mir den Senf.
```

Success! You are now ready to express your love for Leiningen in its
native tongue. I highly recommend you use this program to come up with
tweets to send to @technomancy to express your appreciation.

But before you do that, notice the classpath. We need to include both
the `classes` directory, because that's where the
`learn_a_language/important_phrases` class files live, and
`clojure.jar`, because the class files generated by `compile` need to
be able to access Clojure class files.

To sum up:

```
# load clojure repl
$ java -cp .:clojure.jar clojure.main
# in Clojure repl
user=> (load "learn_a_language/important_phrases")
user=> (compile 'learn-a-language.important-phrases)
# back to command line
java -cp classes:clojure.jar learn_a_language/important_phrases 0
```

I hope this brief foray into the world of manually building and
running a Clojure program has been educational. You can see how it
would be burdensome to go through his process over and over again
while developing a program. Let's finally bring in our pal Leiningen
to automate this process.

## How Leiningen Compiles and Runs a Basic Clojure Program

Let's build the "learn a language" program with Leiningen. We have a
very basic project at `make-a-clojure-baby/leiningen/lein-build`.
Under that the directory, the file
`src/learn_a_language/important_phrases.clj` is the same as the one
listed above.

### lein run

Lein automates the build + run process with `lein run`. Go ahead and
try that now:

```
$ lein run 2
Compiling learn-a-language.important-phrases
English:  I miss you.
German:  Ich vermisse dich./Du fehlst mir.
```

You can probably guess what's happening at this point, at least to a
degree. Leiningen is compiling `important_phrases.clj` resulting in a
number of Java `.class` files. We can, in fact, see these class files:

```
$ ls target/classes/learn_a_language
important_phrases$_main.class
important_phrases$loading__4784__auto__.class
important_phrases__init.class
```

Leiningen then somehow constructs a classpath such that both Clojure
and the "important phrases" classes are accessible by Java. Finally,
the `-main` function is executed.

I know what you're thinking at this point, noble reader. You're
thinking that the "somehow" in "somehow constructs a classpath" is a
particularly un-manly, un-artistic, un-mustached, un-sparkly word,
unbefitting an article on Leiningen. And you are absolutely right. To
avoid your wrath, let's dig into Leiningen's source code so that we
can understand what's going on with complete clarity.

### Walking Through "lein run"

To get an idea of where to start, let's run `lein run 1` again and
then run `ps | grep lein`. The output has been broken up to make more
sense:

```
# you can actually copy and paste the part after the PID to try this
# in your terminal
8420 /usr/bin/java \
  -client -XX:+TieredCompilation  \
  -Xbootclasspath/a:/Users/daniel/.lein/self-installs/leiningen-2.0.0-standalone.jar \
  -Dfile.encoding=UTF-8 \
  -Dmaven.wagon.http.ssl.easy=false \
  -Dleiningen.original.pwd=/Users/daniel/projects/web_sites/make-a-clojure-baby/leiningen/lein-build \
  -Dleiningen.script=/Users/daniel/bin/lein \
  -classpath :/Users/daniel/.lein/self-installs/leiningen-2.0.0-standalone.jar \
  clojure.main \ `# clojure.main is the entry point` \
  -m leiningen.core.main \
  run 1

8432 /usr/bin/java \
  `# your classpath will be different` \
  -classpath \
    /Users/daniel/projects/web_sites/make-a-clojure-baby/leiningen/lein-build/test:\
    /Users/daniel/projects/web_sites/make-a-clojure-baby/leiningen/lein-build/src:\
    /Users/daniel/projects/web_sites/make-a-clojure-baby/leiningen/lein-build/dev-resources:\
    /Users/daniel/projects/web_sites/make-a-clojure-baby/leiningen/lein-build/resources:\
    /Users/daniel/projects/web_sites/make-a-clojure-baby/leiningen/lein-build/target/classes:\
    /Users/daniel/.m2/repository/org/clojure/clojure/1.5.1/clojure-1.5.1.jar
  -XX:+TieredCompilation \
  -Dclojure.compile.path=/Users/daniel/projects/web_sites/make-a-clojure-baby/leiningen/lein-build/target/classes \
  -Dlearn-a-language.version=0.1.0-SNAPSHOT \
  -Dfile.encoding=UTF-8 \
  -Dclojure.debug=false \
  clojure.main \ `# clojure.main is the entry point` \
  `# this next part specifies code to be evaluated by Clojure` \
  -e (do \
      (try \
       (clojure.core/require 'learn-a-language.important-phrases) \
       (catch java.io.FileNotFoundException ___6081__auto__)) \
      (set! *warn-on-reflection* nil) \
      (clojure.core/let \
       [v__6079__auto__ \
        (clojure.core/resolve 'learn-a-language.important-phrases/-main)] \
       (if \
        (clojure.core/ifn? v__6079__auto__) \
        (v__6079__auto__ "1") \
        (clojure.lang.Reflector/invokeStaticMethod \
         "learn-a-language.important-phrases" \
         "main" \
         (clojure.core/into-array \
          [(clojure.core/into-array java.lang.String '("1"))])))))
```

There are two things happening here. When you first run `lein run`,
then the process with PID 8420 starts. There are a lot of
configuration variables that we don't necessarily need to care about.
What's essentially happening is we're saying:

* Start up the JVM with the leiningen standalone jar on the classpath
* Use `clojure.main` as the Java entry point
* Pass `-m leiningen.core.main run 1` as arguments to `clojure.main`

That last step specifies the *Clojure* entry point, as opposed to the
*Java* entry point. Clojure uses it to load the `leiningen.core.main`
namespace and then execute the `-main` function within it.
`leiningen.core.main/-main` receives the arguments `run 1`.

We can view Leiningen's `leiningen.core.main/-main` function
[on github](https://github.com/technomancy/leiningen/blob/master/leiningen-core/src/leiningen/core/main.clj#L275):

```clojure
(defn -main
  "Command-line entry point."
  [& raw-args]
  (try
    (user/init)
    (let [project (project/init-project
                   (if (.exists (io/file "project.clj"))
                     (project/read)
                     (assoc (project/make (:user (user/profiles)))
                       :eval-in :leiningen :prep-tasks [])))
          [task-name args] (task-args raw-args project)]
      (when (:min-lein-version project) (verify-min-version project))
      (configure-http)
      (warn-chaining task-name args)
      (apply-task task-name project args))
    (catch Exception e
      (if (or *debug* (not (:exit-code (ex-data e))))
        (.printStackTrace e)
        (when-not (:suppress-msg (ex-data e))
          (println (.getMessage e))))
      (exit (:exit-code (ex-data e) 1))))
  (exit 0))
```

Just as we would suspect from the `ps` output, this is the command
line entry point for `lein`. One of the first things this function
does is figure out your project configuration from your `project.clj`
file. Here's ours:

```clojure
(defproject learn-a-language "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :main learn-a-language.important-phrases)
```

The most important line, for our purposes, is

```clojure
:main learn-a-language.important-phrases
```

This tells Leiningen what function to execute when you run `lein run`.
If you specify a namespace without a function, as we do above, then
Leiningen defaults to using the `-main` function.

Next, if you look about 2/3 of the way down in the
`leiningen.core.main/-main` function, you'll see the `apply-task`
function being called. This calls `resolve-task` which eventually
resolves to `leiningen.run`, which you can
[also see on github](https://github.com/technomancy/leiningen/blob/master/src/leiningen/run.clj).

This is pretty cool &mdash; `run` is just another task from
Leiningen's point of view. Wait... did I just say "cool"? I meant
*MANLY* and *ARTISTIC* and *SPARKLY*. But yeah, it looks like basic
leiningen architecture includes the `leiningen.core` namespace, which
handles task resolution and application, and plain ol' `leiningen`,
which appears to be mostly a collection of default tasks. Leiningen
uses this same mechanism to execute any function in your Clojure
project as a task. Bodacious!

Anyway, once the `run` task has been resolved, it is executed and the
result is the second process we saw in the `ps | grep lein` output we
saw above, the process with PID 8432. I won't go into how that command
gets constructed, as you can figure that all out from `leiningen/run`.
What's more important is what it actually does. The command loads
Clojure and tells it to evaluate the following:

```clojure
(do
  (try
    (clojure.core/require 'learn-a-language.important-phrases)
    (catch java.io.FileNotFoundException ___6081__auto__))
  (set! *warn-on-reflection* nil)
  (clojure.core/let
      [v__6079__auto__
       (clojure.core/resolve 'learn-a-language.important-phrases/-main)]
    (if
        (clojure.core/ifn? v__6079__auto__)
      (v__6079__auto__ "1")
      (clojure.lang.Reflector/invokeStaticMethod
       "learn-a-language.important-phrases"
       "main"
       (clojure.core/into-array
        [(clojure.core/into-array java.lang.String '("1"))])))))
```

You can see how the end result is that
`learn-a-language.important-phrases/-main` gets executed with the
argument `"1"`. One interesting thing to note about this approach is
that the `:gen-class` directive isn't actually needed. In the manual
build at the beginning of this article, we needed `:gen-class` because
we were specifying `learn-a-language.important-phrases` as the JVM
entry point. When we use `lein run`, the entry point is
`clojure.main`. Once Clojure is loaded, we use it to evaluate some
code which loads the `learn-a-language.important-phrases` namespace
and then calls the `-main` function.

## Wrapping Things Up

So now we know how Leiningen compiles and runs a basic Clojure
program! Can you feel your mustache growing? Can you feel your
artistry blooming? Are you feeling just a smidge more sparklier? I
sure hope so!

Here's everything we covered:

**Building and running a Clojure program manually:**

* Load the Clojure repl
* Load your Clojure code (make sure it includes `:gen-class`)
* Compile your Clojure code. By default code gets put in `classes`
  directory
* Run your code, making sure the classpath includes the `classes`
  directory and `clojure.jar`

```
# load clojure repl
$ java -cp .:clojure.jar clojure.main
# in Clojure repl
user=> (load "learn_a_language/important_phrases")
user=> (compile 'learn-a-language.important-phrases)
# back to command line
java -cp classes:clojure.jar learn_a_language/important_phrases 0
```

**Building and running a Clojure program with Leiningen:**

* Run `lein run`
* Magic!
    * Start Java with `clojure.main` as the entry point
    * Execute the `leiningen.core/-main` function
        * Setup project config with `project.clj`
        * Resolve the task to be run
    * Run the task
        * Automagically set the classpath
        * Use `clojure.main` as the Java entry point
        * Construct some Clojure code to evaluate so that the
          project's main function gets executed

In the next article we'll cover:

* How Leiningen manages dependencies
* How to distribute a full application
* How to distribute a library
* Manliness

I hope you enjoyed this article. Please do leave me any feedback - I'm
always looking for ways to improve my writing!
