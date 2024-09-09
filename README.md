`(begin "readme")`

# ráð (Java + gammel-norsk for "advice/råd")

## Building

```
mvn package
```

Or, using the `Justfile`:

```
just package
```

## Running

```
just repl
```

Voila:

```
> (+ 1 2)
>>: 3.0
> ((lambda (x) (+ 1 x)) 2)
>>: 3.0
```

## Syntax

Atoms:

```lisp
2
3
"hello"
#t
#f
```

Quotation:

```lisp
(quote (1 2 3))
'(1 2 3)
```

Assignment:

```lisp
(define f (lambda (n) (+ n 1)))
;; Following is not implemented (not comments either):
(define (adder n) (lambda (n) (+ n 1)))
```

Sequence of forms (returns 4):

```lisp
(begin
  (define x 2)
  (+ x 2))
```


Lambdas:

```lisp
(lambda (a b c) (+ a b c))
```

Conditional:

```lisp
(if #t "true" "not true")
```

Want to have:

 - `cond`
 - `let`
 - `letrec`
 - `map`, `filter`, etc
 - Vararg lambdas
 - Quasiquote
 
 
 
## References

https://courses.cs.washington.edu/courses/cse341/04wi/lectures/12-scheme.html

Recursive Functions of Symbolic Expressions
Their Computation by Machine, Part I
https://dl.acm.org/doi/pdf/10.1145/367177.367199
https://www-formal.stanford.edu/jmc/recursive.pdf


Lots of tests to run later: https://github.com/ashinn/chibi-scheme/blob/master/tests/r5rs-tests.scm

**Eliza Chatbot**
http://lisp.plasticki.com/show?24GC
https://github.com/norvig/paip-lisp/blob/main/lisp/eliza.lisp



### Other implementations

**Lisp in 1k lines of C**

https://github.com/Robert-van-Engelen/lispg

Peter Norvig's
https://norvig.com/lispy.html

[GRScheme](https://andreyor.st/posts/2020-02-25-grscheme-design-part-1/)

### Fun

Poems and jokes about Lisp
https://web.archive.org/web/20170320142011/http://www.gotlisp.com/lambda/lambda.txt

List of Lisp humor
https://www.cliki.net/humor

### Other

[How Lisp Became God's Own Programming Language](https://twobithistory.org/2018/10/14/lisp.html)

[What is the origin and meaning of the phrase “Lambda the ultimate?”](https://softwareengineering.stackexchange.com/questions/107687/what-is-the-origin-and-meaning-of-the-phrase-lambda-the-ultimate)

Lots of pictures of old Lisp machines
https://www.ifis.uni-luebeck.de/~moeller/symbolics-info/index.html

Scheme tutorial
https://www.cs.rpi.edu/academics/courses/fall00/ai/scheme/reference/Scheme.html

[Scheme grammar](https://www.scheme.com/tspl3/grammar.html)
From this book: https://www.scheme.com/tspl3/
