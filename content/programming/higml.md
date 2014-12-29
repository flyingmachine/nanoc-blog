---
title: "Higml: Hierarchical, Cascading, Dynamic Configuration"
created_at: Jan 21 23:23:00 -0500 2011
kind: article
categories: programming
summary: If your configuration is convoluted as all hell, Higml might be for you. Also, this is my first attempt at writing first-class documentation.
---

## Purpose

Higml allows you to write hierarchical, cascading, dynamic configuration files with Ruby. It visually reflects the conditions governing how rules are applied, allowing otherwise-complicated rules to be expressed concisely and understood easily.

## Examples

### Applying Hierarchical, Cascading Rules

```
# Higml rules; let's pretend they're in rules.higml
action:create
  :channel Create Saved Search
  
action:show
  :channel Search
  
  search_type:job
    :channel Job Search
```

```ruby
# Let's pretend the following is run by ruby
rules = "rules.higml"

input1 = {:action => "show"}
input2 = {:action => "show", :search_type => "job"}
input3 = {:action => "create", :search_type => "job"}

Higml.values_for(input1, rules)
# {:channel => "Search"}

Higml.values_for(input2, rules)
## {:channel => "Job Search"}

Higml.values_for(input3, rules)
# {:channel => "Create Saved Search"}
```

Higml checks an input hash for key/value pairs and builds an output
hash. Higml rules are cascading in that if a matching rule is defined
later in the Higml file, it takes precedence over a previously defined
rule.

You can see this with inputs 1 &amp; 2. `input2` matches the
`action:show` rule, but it also matches the `search_type:job` rule
which is defined later. The `search_type:job` rule therefore takes
precedence.

Higml is hierarchical in that it does not apply child (indented) rules if the parents don't match. You can see this by comparing inputs 2 &amp; 3;

### Dynamic Output

```
# Higml rules; let's pretend they're in rules.higml
action:show
  :keywords= @keywords
```

```ruby
# Let's pretend everything that follows is run by ruby
# Context object
context = Object.new
context.instance_variable_set(:@keywords, "cat mouse bird")

# Higml invocation, including context object
Higml.values_for({:action => "show"}, 'rules.higml', context)
# {:keywords => "cat mouse bird"}
```

Whenever Higml encounters `=` in the output definition, it uses `instance_eval` on the context object on the text that follows; in this case, `@keywords`.

## Rule Application Summarized

* Rules take the form `input_key:value` or `input_key`. If value is
  not present, any value (including nil) will cause the rule to be
  applied.
* Multiple rules can be present on the same line, separated by
  commas. Example: `action:show, action:new`. This is equivalent to an
  `OR` - if any rule matches, the rule is applied.
* Indented (child) rules are only checked if their parent matches.
* Output definitions take the form `:output_key Value` or `:output_key= code_instance_evald_in_context`.
* Output definitions written later on the page take precedence over
  those written earlier

## Pronunciation

It's pronounced like "hig-uh-mole".
