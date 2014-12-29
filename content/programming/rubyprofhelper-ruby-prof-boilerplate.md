---
title: "RubyProfHelper: Reducing ruby-prof Boilerplate"
kind: article
created_at: Mon, 02 Jun 2008 15:34:26 +0000
categories: programming
summary: This is meant to save me from having to write the same boilerplate profiling code over and over.
additional_stylesheets:
  - pygments
---

This is meant to save me from having to write the same boilerplate profiling code over and over.

By default, it creates an HTML graph and saves it to RAILS_ROOT/ruby-prof/profile.html .

The method url_path is a convenience method for saving your HTML graph to a location based on the URL used.

To use, add the below to lib, then add `around_filter RubyProfHelper` to whatever controllers you want to profile. Then go to the URL you want to profile and add profile=true to the query string. You should probably ensure that this can't be run by any schmoe in production. Also, you'll probably want to have your SCM ignore the ruby-prof directory.

If you want to get more fine grained with your filter, use the following:

```ruby
RubyProfHelper.run("output_file_name.html") do
  code_to_profile(here)
end
```

p. When you visit the corresponding URL, *leave out* profile=true
from the query string.

This arguably needs to be a plugin, but I find it more
straightforward as a /lib addition.

```ruby
class RubyProfHelper
  class << self
    def filter(controller, &block)
      return (yield block) unless controller.params[:profile] 

      # Instead of "profile.html" you can try url_path(controller),
      # which will return a file path based on the url visited
      run("profile.html", &block)
    end

    def run(output_file_name = "profile.html", &block)
      require 'ruby-prof'
      RubyProf.start

      yield block

      result                 = RubyProf.stop
      printer                = RubyProf::GraphHtmlPrinter.new(result)

      # create directory
      dir                    = File.join(RAILS_ROOT, "ruby-prof")
      FileUtils.mkdir_p dir

      file_path              = File.join(dir, output_file_name)
      f                      = File.open(file_path, "w+")
      printer.print(f)
    end

    # will evaluate to home.html or something like
    # products.html
    # products/2.html
    def url_path(controller)
      if controller.request.path =~ /\/$|^$/
        "home.html"
      else
        controller.request.path + ".html"
      end
    end
  end
end
```
