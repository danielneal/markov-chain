# markov-chain

Alice in Markov Chains.

A simple markov chain implementation with 2-grams,
implemented at the West London Hack Night by Oliver, Dan and Luke.

## Usage

Run the function `markov`, specifying the source text, a two-word seed
(that must exist in the source), and a maximum number of words to return.

Example: to generate 40 words, starting with "it was" using Alice in Wonderland
as a source, do the following:

    (markov alice "it was" 40)



## License

Copyright © 2014

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
