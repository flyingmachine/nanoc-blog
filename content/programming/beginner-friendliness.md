---
title: Beginner Friendliness
created_at: Sat Jul 20 2013 19:23:00 -0500
kind: article
categories: programming
summary: "In the Clojure community, one of the unwritten tenets is that frameworks can eat shit."
draft: true
---

Unfortunately, it seems like some in the Clojure community subscribe
to the idea that it's misguided to make tools easier for beginners to
use. I'm not sure if this is exactly what Rich Hickey (who created
Clojure) believes, but I perceived it in his talk [Design,
Composition, and
Performance](https://www.infoq.com/presentations/Design-Composition-Performance/)
(around minute 34):

> Instruments are made for people who can play them... They're made
> for people who can actually play them. (sarcastically) And that's a
> problem, right?  Because beginners can't play. They're not yet
> players, they don't know how to do it.
>
> ...(sarcastically) We should fix like, the cello. Should cellos
> auto-tune? Or maybe they should have red and green lights? It's
> green when it's in-tune and it's red when it's out of tune.
>
> ...If they had any of those kinds of aids, they would never actually
> learn how to play cello. They'd never learn to hear themselves, or
> to tune themselves, or to listen. And playing a cello is about being
> able to hear, more than anything else.
>
> ...Just as we shouldn't target beginners in our designs, nor should
> we try to eliminate all effort... It's OK for there to be effort.

I don't understand this argument. I don't understand what prompted it.
It's bizarre and self-contradictory: adapting an instrument is bad,
but these children are using child-sized instruments and that's fine.
If you watch the whole talk, some of it is dedicated to explaining how
design reduces the effort to understand a system, how design reduces
the effort to extend a system, how it enables reuse - which also
reduces effort. But for some reason, reducing effort is a bad thing
when it helps beginners?

The talk raises and dismisses the idea of using red and green lights
to tell the player when he's in tune or out of town. This is funny
because years ago I decided to pick up violin, and as I was learning
the finger positions I would keep a tuner on to give me feedback on
when I was in tune and out of tune -- using red and green
lights. Initially I didn't know what in-tune and out-of-tune sounded
like, or where exactly to position my fingers to create the correct
sounds. The tuner gave me the feedback I needed to make
corrections. Using the tuner is what actually helped me learn how to
listen. It truly boggles my mind that someone would argue against
creating or adapting tools to help beginners.

Or take this adorable video of a father playing _Everything Counts_ by
Depeche Mode with his young children:

<iframe width="560" height="315" src="https://www.youtube.com/embed/BxQSEvHdyjQ" frameborder="0" allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>

Notice the colored stickers designating notes on the xylophone and
keyboard. Is this the kind of instrument adaptation that we should put
down because it's reducing effort?

The rant against beginner-friendliness defies logic, but it's there
anyway, which makes me conclude that its only purpose is to heap scorn
on the notion of accommodating beginners and on the idea that
beginners might need accommodation.

What really gets me is this bit:

> Coltrane couldn't build a web site in a day. I don't know why this
> has become so important to us. It's really like a stupid thing to be
> important, especially to an entire industry.

Why is this stupid? Isn't it a sign of progress that difficult tasks
have gotten easier over time? Isn't that something to strive for?
Maybe I'm missing something. Maybe it truly is stupid to want to
figure out how to help people build a web site in a day. It's
definitely possible that I'm grossly misinterpreting this
talk. Perhaps I am taking this personally because I have friends and
family who have literally transformed their lives by learning Rails
and Django, tools that prioritize beginner friendliness and being able
to do stupid things like "build a web site in a day."

One more counter-example: 


I apologize for going on about this so much. I feel strongly about
it. Clojure does not have a reputation for beginner-friendliness,
despite the incredible efforts of many people in the community to make
it more accessible. The strain of anti-beginner-friendliness that's
present is unnecessary, and I think it can and should change.
Creating or adapting tools to help beginners is not stupid. (That
doesn't mean I think anybody is obligated to do that work.) I want to
welcome and embrace beginners. I want them to be able to quickly make
cool stuff.

I am a photographer. My instrument, if you want to call it that, is
the camera. I have a professional camera, and I know how to use
it. Some of my photos required a fair amount of technical knowledge
and specialized equipment:

(insert photo here)

This isn't something you can create with a camera phone, yet somehow
I'm able to enjoy myself and my art without saying it's stupid that
point-and-shoot cameras exist.

Novices benefit greatly from expert guidance. I don't think you can
become a master photographer using your phone's camera, but with the
phone's "guidance" you can take some damn good photos and be proud of
them. And if you do want to become a master, that kind of positive
feedback and sense of accomplishment will give you the motivation to
stick with it and learn the hard stuff. Frameworks provide this
guidance by creating a safe path around all the quicksand and pit
traps that you can stumble into when creating an app. Frameworks help
beginners. This is a feature, not a bug.
