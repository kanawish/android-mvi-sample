# android-mvi-sample
Example MVI implementation, based off of Google's architectural samples.

## Description
This is a sample implementation of the Model View Intent pattern. Full Android examples for 
MVI are hard to find. It's a pattern that has seen wide adoption in the Javascript and .NET world.
Target audience include, but not limited to:

- Developers making the pivot from an imperative to declarative style of programming.
- Developers looking for best-practices in functional or reactive programming on Android.

Here are some articles/examples explaining the core principles of MVI and unidirectional flow:

http://hannesdorfmann.com/android/model-view-intent
https://cycle.js.org/model-view-intent.html
http://thenewstack.io/developers-need-know-mvi-model-view-intent/
http://blog.danlew.net/2017/07/27/an-introduction-to-functional-reactive-programming/

This project, while based off of Google's architectural samples repo, is my 
personal take on the MVI pattern. Motivation was to be able to first build a working 
sample, and only worry about getting formal approval if it gains some traction in the 
community. (Google architecture samples need to have their dependencies vetted, have 
some decent traction from dev community at large, use the `m` attribute prefix, etc.)

Hoping to contribute back any useful patterns found here to the Architecture repo once
these conditions are met.

## Stack

- Kotlin
- RxJava 2
  - RxBindings
  - RxRelay (TBD?)
- Toothpick DI
- Room
- [ ] TODO: Document other uses.

## Application Components

TODO: Consider adding some extra features to app, to demonstrate more complex uses of RxJava2. (Task ordering, etc.)
TODO: Rx based State Machine example
TODO: ...
