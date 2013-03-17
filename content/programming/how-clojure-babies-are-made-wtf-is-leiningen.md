---
title: "How Clojure Babies Are Made: Leiningen Deconstructed"
created_at: Mar 16 9:23:00 -0500 2013
kind: article
categories: programming
summary: "If you're at all like me, the moment you got your first Clojure program running you belted out, \"SOOO MUUUUUUUCH POOOOOOOWEEEEEEEER!\" and thrust one or both fists into the air. Then, fifteen minutes later, you found yourself muttering things like \"What's a maven?\" and \"Classpath what now?\" and \"What is Leiningen actually doing, anyway? Sure, those are the most handsome mustaches I ever laid eyes on, but can I really trust them?\""
additional_stylesheets:
  - pygments
---

Leiningen reminds me a lot of Major Alex Louis Armstrong from
Fullmetal Alchemist:

![What artistry!](/assets/images/posts/leiningen/so-sparkly.png)

* They are both manly
* They are both artistic
* Sometimes you don't know what the f* they're saying
* Mustaches
* Sparkles

Though the Strong Arm Alchemist will forever remain inscrutable, we do
have some hope in understanding Leiningen better. In this post, we'll
peek under Leiningen's lederhosen to learn precisely what it does and
how to make effective use of it in developing, testing, running, and
deploying Clojure applications.

Here's what we'll learn:

* How to build and run a Clojure program without Leiningen
* What happens when you run "leiningen run"
* Running a Leiningen task
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

## Brief overview of Leiningen

Leiningen is the Swiss Army Bazooka of Clojure development. It
handles:

* **Project compilation.** Your Clojure has to get converted into Java
  class files somehow, and Leiningen makes this process transparent.
* **Dependency management.** Similar to Ruby's bundler and gemfiles.
* **Running tasks.** Similar to Ruby's Rake.
* **Deployment.** Helps with creating Java jars which can be executed
  and/or incorporated in other projects. Similar to Ruby gems.

## How to build and run a Clojure program without Leiningen

By understanding how to build and run a Clojure program manually,
you'll better understand what Leiningen does to automate the process.
We'll be working with the files under `leiningen/manual-build` which
you can get from the
[make a clojure baby repo](https://github.com/flyingmachine/make-a-clojure-baby).
All path references will be relative to `make-a-clojure-baby/leiningen/manual-build`.

The program we're compiling will make us fluent in the ways of Russian
Love:

```clojure
(ns learn-a-language.important-phrases
  (:gen-class))

;; It's time for some German love! 
(def german
  [["I love you." "Ich liebe dich."]
   ["You make me so happy!" "Du machst mich so glücklich!"]
   ["I miss you." "Ich vermisse dich./Du fehlst mir."]
   ["Pass me the mustard." "Gib mir den Senf."]   
   ["Kiss me!" "Küss mich!"]])

(defn -main
  [which]
  (let [phrases (get german (Integer. which))]
    (println "English: " (first phrases))
    (println "German: " (second phrases))))
```

One important thing to note here is that we included the `:gen-class`
directive in the `ns` declaration. This generates a named Java class
when we compile the namespace, which will allow us to actually execute
the `-main` method directly from the command line.

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
[clojure's compilation documentation](http://clojure.org/compilation))

After going through the above steps, you might have a question on your
mind grapes: "Where did the `classes` directory come from?"

Oh, gentle-hearted reader. Your dedication to learning has touched my
heart. I shall answer your query: Clojure places compiled files under
`*compile-path*`, which is `classes` by default. Therefore, `classes`
must exist and be accessible from the classpath. You'll noticed that
there's a `classes` directory in the git repo with a `.gitkeep` file
in it. Never change, dear reader. Never!

Now that we've compiled our Clojure program, let's get it running:


```
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

I hope this brief foray into the world of manually building and
running a Clojure program has been educational. You can see how it
would be burdensome to go through his process over and over again
while developing a program. Let's finally bring in our pal Leiningen
to automate this process.

## How 
