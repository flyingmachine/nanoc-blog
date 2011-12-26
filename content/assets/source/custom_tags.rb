module CustomTags  
  #TODO - allow method specification instead of line numbers
  def source(opts)
    local_path, highlighted, range = /([^ :]*)(?::(\d+))?(?: (\d+-\d+))?/.match(opts[:text])[1..3]
    code = File.readlines(File.join(File.expand_path(File.dirname(__FILE__)), local_path))
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

    html << IO.popen("pygmentize -O linenos=table,linenostart=#{start} -f html -l ruby", 'a+') do |pygmentize|
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
