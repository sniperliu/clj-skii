# clj-skii

A small application designed to solve a puzzle, and find the longest and steepest routes from a peak.

## Algorithm

The problem is solved by using shortest path search method of DAG (Directed Acyclic Graph).

## Usage

```bash

#map.txt is the puzzle file and stores below resources folder

#run the jar
lein uberjar
java -jar target/clj-skii-0.1.0-SNAPSHOT-standalone.jar map.txt

#run with lein
lein run map.txt

#run repl
lein repl

nREPL server started on port 56968 on host 127.0.0.1 - nrepl://127.0.0.1:56968
REPL-y 0.3.5, nREPL 0.2.7
Clojure 1.6.0
Java HotSpot(TM) 64-Bit Server VM 1.8.0_31-b13
    Docs: (doc function-name-here)
              (find-doc "part-of-name-here")
	        Source: (source function-name-here)
		 Javadoc: (javadoc java-object-or-class-here)
		     Exit: Control+D or (exit) or (quit)
		      Results: Stored in vars *1, *2, *3, an exception in *e

clj-skii.core=> (def skii-map (parse-map "map.txt"))
...
clj-skii.core=> (find-best-skii-routes skii-map)

```

## License

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
