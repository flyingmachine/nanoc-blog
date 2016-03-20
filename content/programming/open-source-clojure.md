---
title: A Directory of Open Source Clojure Projects
created_at: Sat Mar 19 2016 12:00:00 -0500
kind: article
categories: programming
summary: "<a href='http://open-source.braveclojure.com/'>Open Source Clojure Projects</a> is a directory of active projects welcoming new contributors. Its purpose is to make it easier for both new and experienced Clojurists to find ways to contribute."
---

Every now and again I'll come across a post where someone asks about
getting involved in open source clojure projects. Because every
question deserves a single-page app dedicated to its answer, I made
[Open Source Clojure Projects](http://open-source.braveclojure.com/),
a directory of active projects welcoming new contributors.

Each project has (or should have) clear instructions on:

* Developing the project locally
* Running tests if there are tests
* Contributing code (pull request? tests required?)
* Contacting other devs - slack, mailing list, IRC, etc

Also, there's a "beginner-friendly" flag so that new Clojurists can
easily find projects with tasks that are appropriate for their skill
level.

So far, 22 projects have been added, which is awesome. A random
sampling:

* [milestones](http://open-source.braveclojure.com/projects/milestones),
  which generates the best possible schedule from a set of tasks
* [reverie](http://open-source.braveclojure.com/projects/reverie), a
  CMS for power users
* [Scheje](http://open-source.braveclojure.com/projects/scheje), a
  little Scheme on Top of Clojure (!!!)
* [system](http://open-source.braveclojure.com/projects/system), a
  friendly layer on top of Stuart Sierra's component library

If you have an open source project and want people to contribute,
please [add it](http://open-source.braveclojure.com/projects/new)!

## The Stack

A few folks have asked me about what tools I used to create the
site. I'll explain more below but briefly:

* The backend is a weird, unholy mess of fantastic classic libraries
and sideshow code that I keep transplanting from one project to
another so that I can answer the question _what kind of abomination
will this morph into this time?_. The backend uses Github as a
database, though, and that's pretty neat.
* The frontend uses re-frame and includes some form handling code that
I'm proud of and that's actually worth stealing.
* Finally, there's also some code for deploying with Ansible that's
worth stealing.

In the time-honored fashion of technical blog posts everywhere, I'll
now "dive in" and elaborate on each of those bullet points.

### Backend

For the backend, the site uses ring, compojure, liberator, http-kit,
and the weird set of tools that have accreted in my projects over the
years. Even though the code footprint for the backend is pretty small,
it's pretty idiosyncratic, containing handfuls of half-formed ideas I
keep noodling around with. Hopefully someday soon I'll be able to
really nail down my approach to backend dev and share it, because it
_does_ allow me to write apps quickly.

One cool part of the site is that it uses a Github repo,
[Open Source Projects](https://github.com/braveclojure/open-source-projects),
as its database. Browse the `projects` directory and you can see every
project that's listed on the site stored as an EDN file. When the
backend starts up it reads from Github, and whenever someone posts or
edits a listing it writes the EDN file using Github's API.

The nice thing about this approach is that I didn't have to worry
about standing up a server or keeping backups. And it was just fun to
code. [Here's the source](https://github.com/braveclojure/open-source/blob/master/src/backend/open_source/db/github.clj)
for using Github as a db - I definitely see potential in reusing this
approach for similar lightweight directory sites.

### Frontend

[re-frame](https://github.com/Day8/re-frame) has a delightful README
that gracefully introduces you to reactive programming. If you haven't
read it then stop reading this article and read that instead; I am
100% confident that it's a better use of your time.

re-frame is a joy to use, and it's become my go-to tool for frontend
projects. I've written a fair amount of code
[for working with forms](https://github.com/braveclojure/open-source/blob/master/src/frontend/open_source/components/form_helpers.cljs)
and
[submitting values](https://github.com/braveclojure/open-source/blob/master/src/frontend/open_source/handlers/common.cljs#L13). This
code forms a kind of nano-framework on top of re-frame, and it's
allowed me to speed up the development process from project to
project. Here's
[an example form which uses the form helpers](https://github.com/braveclojure/open-source/blob/master/src/frontend/open_source/pub/projects/project_form.cljs). If
you'd like for me to write a detailed explanation of this code, please
leave a comment letting me know :)

[Boot](http://boot-clj.com/) deserves a special mention because it
makes it so easy to develop ClojureScript apps. With Boot you get live
reload (every time you change your frontend files, the changes are
automatically injected into the browser), giving you a
near-instantaneous feedback cycle that makes development much more
enjoyable. It's also easy to incorporate [Sass](http://sass-lang.com/)
compilation - and your Sass files also get the live reload treatment.

If you're not using Boot to build your ClojureScript app then chances
are you're causing yourself undue suffering. And hey, maybe you're
into that? No judgment. But if you're not, then take the time to learn
it - it's a great investment in yourself as a Clojure developer.

### Deployment

The
[infrastructure](https://github.com/braveclojure/open-source/tree/master/infrastructure)
directory contains scripts for provisioning and deploying to a server
with [Ansible](http://ansible.com/). It also has a
[Vagrantfile](https://www.vagrantup.com/) so that you can stand up a
test server locally; just run `vagrant up` to both create the virtual
machine and provision it. To deploy, `cd` into the `infrastructure`
directory and run `./build.sh && ./deploy-dev.sh`. Those two shell
scripts are extemely simple, so take a look at them to see what's
involved in deployment.

The Ansible configuration is almost completely reusable. You can check
it out in `infrastructure/ansible`. The `roles` directory in
particular has Ansible tasks for creating an nginx server that can run
Clojure apps.

## The Future

Eventually, I'd like to add more tools for project discovery and for
showing project stats. @yogthos, for example, suggested incorporating
a project's stats from github so that users can see how active and
popular each project is, information that can help them decide which
project to contribute to.

Also, I'm in the process of revamping
[braveclojure.com](http://braveclojure.com) to include more resources
for Clojure programmers. The site hovers around 3rd or 4th Google
result for "Clojure", just below clojure.org and the Wikipedia
entry. So, it's kind of popular I guess. My hope is that by featuring
Open Source Clojure Projects, I'll help more new developers get
involved and also help project maintainers squash some tickets.
