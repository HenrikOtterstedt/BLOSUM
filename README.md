# Installation
Download the `BLOSUM.jar` file from `/out/artifacts` and use it with your local java version.

# Usage
This jar can be used to generate your very own BLOSUM matrix given the protein sequences for each of the alignment blocks.

## Options
**-s, --sequences \<arg>**   Sequence Blocks for the BLOSUM Matrix.
                        Within a block the Sequences have to be formatted
                        in the following way:
                        <Block1.1>,<Block1.2>,<Block1.3>
                        <Block2.1>,<Block2.2>...
                        The Blocks have to be separated by a \<Space>.
                         
**-t, --threshold \<arg>**   Threshold for the clustering required for the
                        BLOSUM Matrix. (For a BLOSUM-55 it is 55).
                        The parameter should be an Integer between 0 and
                        100.

**-h, --help** To get the help message.

## Output
The program will give you all the intermediary steps to get to the final substitution matrix.
This includes:
- doing the clusters within the inital alignment blocks
- the position dependent scoring matrix for all possible substitutions
- the sum table derived from the positional table
- the final substitution matrix derived from all input blocks

*All Floats in the matrices are given as fractions for better understandability.*

## Examples Usage
A usecase and output example can be found in `usage_example.txt`.
