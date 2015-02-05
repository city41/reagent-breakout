# Reagent Breakout

This is an implementation of [Breakout](http://jsbreakouts.org) done in [Reagent](http://holmsand.github.io/reagent/) and [ClojureScript](https://github.com/clojure/clojurescript).

It is mostly done, but still has a few more things to do. Check out the [issues](https://github.com/city41/reagent-breakout/issues) to see what's left to tackle.

You can play it [here](http://city41.github.io/reagent-breakout/index.html)

# Hacking on the code

You will need [Leiningen](http://leiningen.org/) installed as well as a Java SDK 1.7 or better. (Typical Clojure environment).

run `lein figwheel` to setup the dev environment. You can then see the game at http://localhost:3449.

Also `lein garden auto` will automatically start compiling the garden (at src/clj/breakout/style.clj) into css.

Any CSS or ClojureScript changes you make will be picked up right away by the browser. But since this is a game with a lot of state, requestAnimationFrame, etc, you still often need to refresh the browser to see your changes.

# Deploying

run `lein build-prod`. Once done, the contents of `resources/public/` will have everything you need:

* index.html
* js/app.min.js
* img/ -- all of the images in here
* css/site.css

`resources/public/` is what gets deployed out to the gh-pages branch with deploy.sh


