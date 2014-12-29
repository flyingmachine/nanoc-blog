---
title: Rails Prototypal Attributes
created_at: Sat, 05 Apr 2008 15:18:39 +0000
kind: article
categories: programming
summary: Give your models javascript-esque prototypal methods. When accessing an attribute on the prototypal object, the attribute's value is returned if not nil. Otherwise, the "linked" object's attribute value is returned.
additional_stylesheets:
  - pygments
---

Give your models javascript-esque prototypal methods. When accessing
an attribute on the prototypal object, the attribute's value is
returned if not nil. Otherwise, the "linked" object's attribute value
is returned.

```ruby
# Prototypal
# Example
# =======
# 
# class Clipping
#   has_many :saved_clippings
# end
# 
# class SavedClipping
#   belongs_to :clipping
#   
#   include Prototypal
#   prototypal_attributes(:clipping, :title, :description)
# end
# 
# clipping = Clipping.new(:title => "Base Title", :description => "Base description")
# saved_clipping = SavedClipping.new(
#   :clipping => clipping, 
#   :description => "Saved clipping description"
# )
# 
# saved_clipping.title
# => "Base Title"
# 
# saved_clipping.description
# => "Saved clipping description"

module Prototypal
  def prototypal_attributes(link_name, *attribute_names)
    attribute_names.each do |attribute_name|
      
      module_eval <<-"end;"
        def #{attribute_name}
          self[:#{attribute_name}].nil? ? #{link_name}.#{attribute_name} : super
        end
      end;
      
    end
  end
end
```
