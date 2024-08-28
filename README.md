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
