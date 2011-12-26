module CustomTags
  
  #TODO - allow method specification instead of line numbers
  def source(opts)
    local_path, highlighted, range = /([^ :]*)(?::(\d+))?(?: (\d+-\d+))?/.match(opts[:text])[1..3]
    code = File.readlines(File.join(File.expand_path(File.dirname(__FILE__)), local_path))
    if range
      start, finish = range.split("-").collect{|i| i.to_i}
      code = code[(start-1)..(finish-1)]
    end
    indentation_level = /^ */.match(code[0])[0].size
    code.collect!{|l| l.sub(/^ {#{indentation_level}}/, '')} #remove indendation
    code = code.join
    
    html = "<div class='attachment-path source'>"
    html << "<a href='/attachments/#{local_path}'>#{local_path}</a></div>"
    options = { 
      :line_numbers => :inline,
      :wrap         => :div,
      :bold_every   => false 
    }
    options.merge!(:line_number_start => start) if start
    html << CodeRay.scan(code, :ruby).html(options)

    html
  end
  
  # Ignore notes text; just want to keep it on page for later development
  def notes(opts)
    return ""
  end
end
RedCloth::Formatters::HTML.send(:include, CustomTags)
