---
title: tmux in 5 Minutes
created_at: Thurs Nov 7 2014 14:10:00 -0500
kind: article
categories: programming
summary: "If you work on projects that require you to open multiple terminal tabs, then tmux will help you be more productive! This brief guide will show you an easy way to get started."
---

If you work on projects that require you to open multiple terminal
tabs, then tmux (and its super buddy, tmuxinator) will help you be
more productive! tmux allows you to run multiple sessions in one
terminal, and [tmuxinator](https://github.com/tmuxinator/tmuxinator)
allows you to save tmux configurations.

For example, if you're a Rails developer, you could easily open
separate terminal sessions for running a Rails server, a Rails
console, and tailing logs. When working on a
[Clojure-based forum](/programming/building-a-forum-with-clojure-datomic-angular/)
I have four terminal sessions running: a shell, a grunt server
building the frontend, datomic, and a shell for deployments:

![tmuxinator.png](/assets/images/tmux/tmuxinator.png)

In order to start all this up, I only have to run one command: `mux
ath`. This is much more convenient than trying to remember which
services I need and manually starting each one up.

Below are instructions for getting started with tmux and tmuxinator.

* First, install tmux using the instructions (for mac users)
[in this gist](https://gist.github.com/simme/1297707).
* Next, install tmuxinator using `gem install tmuxinator`
* Create your first tmuxinator config file under
`~/.tmuxinator/sample.yml`. Its contents should look like this, where
the command under `server` is whatever's appropriate for your
environment:

```
name: sample
root: ~/path/to/your/project
pre: git pull
windows:
  - shell: 
  - server: bundle exec rails s
```

The `pre` option runs that command in the `root` directory before
trying to open any "windows". (I think of windows as tmux's "virtual
tabs"). You can then start this tmux session with "mux sample". To
navigate back and forth between the windows, use `C-b n` for "next
window", and `C-b p` for "previous window". `C-b` means "hold down
control and hit the 'b' key".  To leave a tmux session, you use the
key binding `C-b d`. If you leave the session, it's still actually
running; any process you started in your windows is still executing.
I rarerly ever use other commands, but if you need more, here's a
[tmux cheatsheet](http://cheat.errtheblog.com/s/tmux).


To completely end a tmux session, you have to kill it. To do that, you
run the command `tmux kill-session -t sample`, where "sample" is the
`name` option in your tmuxinator config. I've created an alias for
this, `alias "tmk"="tmux kill-session -t"`. That way I only have to
type `tmk sample`.

I hope you find this useful! For more information, you can check out
[tmuxinator's github repo](https://github.com/tmuxinator/tmuxinator). You
can do some pretty crazy stuff, like split your terminal into multiple
panes. There's also a [a handy book](http://www.amazon.com/gp/product/1934356964/ref=as_li_tl?ie=UTF8&camp=1789&creative=390957&creativeASIN=1934356964&linkCode=as2&tag=aflyingmachin-20&linkId=KX7OZEGOV4WX5K7W) on tmuxinator available from the Pragmatic Programmers if you want to really go nuts with it.

I hope you've found this tip useful!
