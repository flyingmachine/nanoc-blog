---
title: Announcing Job Search Sanity
created_at: Wed Dec 09 2015 11:15:00 -0500
kind: article
categories: penguins
summary: "Today I'm releasing a free web application, <a href='https://jobsearchsanity.com'>Job Search Sanity</a>. It lets you take a methodical approach to looking for a job search, helping you make progress and feel in control."
---

One of the least fun experiences in my life was getting fired twice in
three months back in 2008 when the economy decided to go all _YOLO_
and drive off a cliff with its middle finger waving out the
window. The first time, I was a wreck. I was in a relationship with
someone who had a chronic medical condition and the attendant medical
bills. As the sole money earner, I was pretty bummed! Every day I felt
dread and panic as I pored over job listings. I made many
stress-induced mistakes like emailing the same contact twice or
forgetting phone interviews.

But somehow I found a job at a startup pretty quickly, only to be
regrettably let go a couple months later because they were running out
of money. This time, I did things differently. I took a _Getting
Things Done_ inspired approach to the job search. Approaching the 
search in an organized, methodical way made me feel in control. It
also gave me confidence that I was making real progress on finding a
job because I could actually see all the steps I was taking. At the
end of the day I knew I had done what I could to move forward.

Today I'm releasing a free web application,
[Job Search Sanity](https://jobsearchsanity.com), to help others do
the same. It helps you keep track of each job opportunity and the next
actions you need to do to move forward. It also keeps cover letters
and interview remindres in place. And one of its best features is that
it lets you save your job searches on the site so you can keep them
all in one place instead of visiting dozens of sites every day.

If you're looking for a job and feel overwhelmed, then
please give it a try! If you know someone who's looking for a job,
please send them a link!

## Personal Notes

The main thing that motivated me to create this site was seeing my
brother struggle with finding employment after graduating with a
Pharm.D. (doctor of pharmacy) degree last year. After spending a
decade putting himself through school, he had the modest expectation
of finding decent-paying, stable work. Instead, he found that the
pharmacy market had radically changed while he was getting his degree,
and jobs were scarce.

I can only imagine how crushing it would feel to commit so much of
your life (and accumulating large student loan debts) to learning
profession, only to find that the rules had changed and there was
actually little demand for your skills. To find that the future you've
been working toward for so long has vanished, replaced with utter
uncertainty.

It was a rough time for him, but thankfully he did eventually find a
job as a pharmacist. While he was looking, he came to live with me,
and I was so glad that I was able to help in some way (and that I got
to spend so much time with him), but part of me wished I could have
done more.

Cut to the present day, where my wife has been looking for a better
job for a while, and I can see the stress affecting her in similar
ways (though to a much lesser degree, thankfully).

All of which is to say - I know that looking for a job can be
incredibly stressful, and I hope that by providing this free app I can
help people in similar situations. I kind of need to make something
that I actually accept payments for soon, but for this site I didn't
want their to be any barriers to signing up and using it. Perhaps I'll
figure out way to monetize it, but for now I'm proud of the site and
I'll count it a success if it helps even a dozen people as they find a
job.

## Development Notes

Job Search Sanity was built using:

* The [Boot](http://boot-clj.com/) build tool. Boot made it easy to
  integrate a Clojure backend, ClojureScript frontend, and Sass
  compilation
* [Liberator](http://clojure-liberator.github.io/liberator/), to
  structure the handling of back-end API calls
* [Datomic](/programming/datomic-for-five-year-olds/) for the database
* [re-frame](https://github.com/Day8/re-frame) as the lightweight
  front-end structure imposed on
  [reagent](https://reagent-project.github.io/)

I absolutely *loved* working on the site because the tools made it so
much *fun*. One of my personal goals was to stretch myself design- and
UX-wise, and the above programming tools made that possible by making
it so simple and easy to implement the basic functionality.

ClojureScript has come incredibly far in the past couple years. A year
or two ago I looked at it but stuck with Coffeescript because cljs
looked like a pain in the ass to use, but that isn't the case any
longer. The only downside here is that, once again, Clojure has
spoiled me, and I never want to go back to my previous tools.
