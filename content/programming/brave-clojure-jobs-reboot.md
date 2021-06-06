---
title: A Better Clojure Job Board
created_at: Sun May 23 2021 15:34:00 -0500
kind: article
categories: programming
summary: "I've re-written jobs.braveclojure.com to create the best possible Clojure job board."
---

I've rebuilt Brave Clojure Jobs to make it nearly effortless for companies that
use Clojure to let developers know they're hiring. I want you to be able to get
a job listing in front of interested devs in less time than it takes to sing
Queen's _We Are the Champions_.

Here's what's new:

* **It’s now freemium.** You can post as many listings as you want without
  giving a credit card number. Charging per listing is nice for the job board's
  owner (me!), but I don't think it's the best model if the job board is to be
  as _useful_ as possible. The most useful job board would include every
  available opening, so I've tried to remove barriers to that goal. At the same
  time, I do want to make money with this endeavor; I'll describe the paid
  benefits farther down.
* **You can import listings.** If you manage listings with Greenhouse, Workable,
  and similar services you can (probably) just paste a listing's URL to import
  it. Eventually, I want to update the site to automatically sync a company's
  listings on Brave Clojure Jobs with their primary source of job listings.
  Listing import isn't perfect but it should save time.
* **You can duplicate listings.** You can now create new listings from existing
  listings. The theme here is: make it super easy to create listings!
* **Copy/paste preserves formatting.** If you copy a listing you've created
  elsewhere and paste it in Brave Clojure Jobs, it will preserve the formatting,
  automatically converting it to markdown. I hope you'll find that this kind of
  attention to detail makes using Brave Clojure Jobs a great experience :D
* **Companies are first-class.** With the old version you could not manage your
  company's info separately from individual listings. Now you create a company
  profile, reducing repetition in your listings and creating a kind of company
  home page where devs can learn about you and see all your listings in one
  place.
* **I've filled out your company info for you.** I have been creating profiles
  for dozens of companies looking for Clojure developers. Be on the lookout for
  a link from me which will allow you to easily create a Brave Clojure Jobs user
  account associated with your company. I really want to make it easy for you to
  connect with a large audience of Clojure developers.

If you go through the process of "sign up with pre-filled company profile then
import listing" you could have a listing published in a minute or less.

Removing barriers to posting listings results in more listings. This makes Brave
Clojure Jobs more valuable to developers, growing the site's audience, which in
turn makes it more valuable to companies. It's a virtuous cycle. (Side note: I'm
really patting myself on the back for using the term "virtuous cycle.")

Beyond companies and job seekers, however, I'm hoping these changes are helpful
for the Clojure community as a whole by providing a definitive answer to the
question, "Can I get hired to write Clojure?" I think this is a concern for
anyone considering learning the language; it's a large time investment and
people naturally want it to pay off. I want to show that the payoff is real.

Indeed, I've seen a considerable uptick in Clojure job postings in 2021. More
and more companies want to hire people to write Clojure because they know doing
so brings tangible benefits. This site will be the beautifully-designed proof.

So, a secondary goal of this rewrite is to create a kind of marketing tool for
the Clojure community: I want something we can point to to show that if you
learn the language you won't just become a better programmer, you'll also gain
access to expanded career opportunities. This will help grow the community,
which in turn will help ~~us take over the programming world~~ make the language
an even more viable choice for everybody.

There are many more changes -- the UI is leagues better, there's better
integration with Stripe so you can manage payments without having to contact me,
etc. etc. etc. The main point is that I want the site help us all as much as a
job board possibly can. My hope is that this massive upgrade, which I've worked
on for more than a year, will be good for companies, good for devlopers, and
good for the entire Clojure community.

## Subscription benefits

There are three subscription levels available to companies: $75/month,
$150/month, and $250/month. Paying for a subscription gives you greater
exposure: paying companies are listed on the home page as "supporters", and
their listings get better positioning. Listings are sorted by grouping them into
seven-day buckets, then sorting by subscription level, then by posting date,
with the highest subscription levels appearing first, and with subscribers'
listings highlighted. Subscribing companies will also have their jobs tweeted
and sent to the Brave Clojure Jobs mailing list.

Over time I want to add more ways to give subscribers greater exposure. For
example, on the [public product
roadmap](https://trello.com/b/KLvxGv7u/product-roadmap), one of the first items
is "post bi-weekly job roundup to reddit." I need to coordinate with the
moderators first to see if this would be OK; it's just one idea for how I want to
provide value for subscribers.

Another idea is to include badges or some kind of flair for subscribing
companies to help your company stand out. For example, I want to create a
"conference sponsor" badge. Conference sponsors deserve public recognition, and
I think highlighting your contribution is valuable for job seekers, as it's more
appealing to work for companies that we know are giving back to the community.

If you have ideas for what you'd like included in a paid subscription, please
let me know! You can reach me by email,
[daniel@braveclojure.com](mailto:daniel@braveclojure.com). You can also upvote
items on the [product roadmap](https://trello.com/b/KLvxGv7u/product-roadmap) if
they strike your fancy.

The takeaway here is that there are currently some paid benefits focused on
increasing your listings' reach, and there are some in the works.

At the same time, I wanted to rebuild the site because I value generosity.
Engaging with others from a spirit of generosity just feels better to me. I
wanted to invest the effort to make improvements and switch to freemium model
because I believe it will help everybody. It's an attempt to "grow the pie", as
I assume they say at Harvard Business School. I may end up making less than
before, who knows, but I want to try to contribute more to a community that has
been so good to me. If you believe that this is a worthwhile endeavor and that
it will help everybody in the long run, including you, then I hope you'll
subscribe and help support me in making a great resource for everybody.
