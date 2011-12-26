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
