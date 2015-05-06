# Compiler for a custom language

This compiler was created by Jonathan Lee under the guidance of Richard Page of the Harker School.
This repository simply demonstrates the source code for the theoretical compiler so it must be run from an environment.
The file that the compiler reads from is "compiler.txt" so you can put the custom language code into that file and run the main method.
The compiled machine code will be put into "compiled.asm" which can then be run in a simulator such as [QtSpim](http://spimsimulator.sourceforge.net/).

This compiler implements several theories involved in parsing with context-free grammar such as left recursion elimination and descending precedence.

You can find more information about the compiler and its design by reading the paper that I wrote which is located in the root folder. This paper details usual limitations of compilers and solutions to some of those limitations. To give some context, I was given a certain set of grammar and had to analyze it and then correct it.
