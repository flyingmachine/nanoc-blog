---
title: Writing Better Documentation
created_at: Jan 19 23:23:00 -0500 2011
kind: article
categories: programming, design
summary: The learning curve for code libraries is often unnecessarily steep. I frequently feel that if the author had written better documentation, I'd be able to use his work much more quickly, or at least figure out that it's wrong for me and move on. I'm trying to write better documentation myself.
additional_stylesheets:
  - pygments
---

The learning curve for code libraries is often unnecessarily steep. I
frequently feel that if the author had written better documentation,
I'd be able to use his work much more quickly, or at least figure out
that it's wrong for me and move on.

My own work could use better documentation, and that's part of why
I've started using [nesta](http://effectif.com/nesta) as my blogging
engine. Nesta looks like it provides just enough of a framework for me
to efficiently write articles which are mainly text. More importantly,
there's room for me to easily extend it in order to explore different
ideas for creating code documentation efficiently. As a first step,
I've written a few lines that will allow me to easily include source
code in a [textile](http://textile.thresholdstate.com/) document:

```ruby
module CustomTags
  #TODO - allow method specification instead of line numbers
  def source(opts)
    local_path, highlighted, range = /([^ :]*)(?::(\d+))?(?: (\d+-\d+))?/.match(opts[:text])[1..3]
    code = File.readlines(File.join(File.expand_path(File.dirname(__FILE__)), local_path))

    langs = {"rb" => "ruby", "clj" => "clojure", "haml" => "haml"}
    lang = langs[File.extname(local_path).gsub(".", "")]
    
    if range
      start, finish = range.split("-").collect{|i| i.to_i}
      code = code[(start-1)..(finish-1)]
    end
    start ||= 1
    
    indentation_level = /^ */.match(code[0])[0].size
    code.collect!{|l| l.sub(/^ {#{indentation_level}}/, '')} #remove indendation
    code = code.join
    
    html = "<div class='attachment-path source'>"
    html << "<a href='/assets/source/#{local_path}'>#{local_path}</a></div><div class='code pygments'>"

    html << IO.popen("pygmentize -O linenos=table,linenostart=#{start} -f html -l #{lang}", 'a+') do |pygmentize|
      pygmentize.puts code
      pygmentize.close_write
      result = ""
      while (line = pygmentize.gets)
        result << line
      end
      result
    end

    html << "</div>"
    
    html
  end
  
  # Ignore notes text; just want to keep it on page for later development
  def notes(opts)
    return ""
  end
end
RedCloth::Formatters::HTML.send(:include, CustomTags)
```

Hopefully that will come in handy when I want to include other, less
self-referential snippets.

In the mean time I've also been thinking about what makes good
documentation. [Tom Preston-Werner](http://tom.preston-werner.com)
recently wrote
[an article](http://tom.preston-werner.com/2010/08/23/readme-driven-development.html)
urging developers to write a README before coding, and I think that's
advice worth trying, though I haven't tried it myself. Unfortunately,
he's not very specific on what should be included in the
README. Personally, it'd be a lot easier for me to write a README if
there were some formula I could follow. I also believe that other
programmers would write better documentation if they had the tools
that facilitated the joy and creativity that they experience with
programming.

My initial thought on good documentation is that it should include the following:

* The purpose of the code
* When it should be used and when it shouldn't (i.e. "This authentication library is great if you don't require much customization.")
* Installation
* Basic usage
* How to extend it
* Its "home"
* Links to further documentation
* Other quality libraries which address the same problem
* A brief tutorial

That... looks like a lot of work. (Googling "what goes in a readme"
also reveals that it looks a bit like
[this excerpt from Ruby Best Practices](http://books.google.com/books?id=WOyuE3YYjiIC&lpg=PA211&ots=csqyUBZoOq&dq=what%20goes%20in%20a%20readme&pg=PA211#v=onepage&q&f=false)). It
also looks like it shouldn't all be put in the README.

In any case, my next little project is to write some documentation for
a little library I created. After that I'm sure I'll have a better
idea of what makes good documentation.
