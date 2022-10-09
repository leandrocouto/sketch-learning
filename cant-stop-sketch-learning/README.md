# What can we Learn Even From the Weakest? Learning Sketches for Programmatic Strategies

by
Leandro C. Medeiros,
David S. Aleixo,
Levi H. S. Levis

This paper was submitted and accepted for publication in *AAAI-2022*.


## Abstract

> In this paper we show that behavioral cloning can be used to learn effective sketches of programmatic strategies. We show that even the sketches learned by cloning the behavior of weak players can help the synthesis of programmatic strategies. This is because even weak players can provide helpful information, e.g., that a player must choose an action in their turn of the game. If behavioral cloning is not employed, the synthesizer needs to learn even the most basic information by playing the game, which can be computationally expensive. We demonstrate empirically the advantages of our sketch-learning approach with simulated annealing and UCT synthesizers. We evaluate our synthesizers in the games of Can't Stop and MicroRTS. The sketch-based synthesizers are able to learn stronger programmatic strategies than their original counterparts. Our synthesizers generate strategies of Can't Stop that defeat a traditional programmatic strategy for the game. They also synthesize strategies that defeat the best performing method from the latest MicroRTS competition.


# Code Structure

    .
    ├── results/                   # Results files presented in the article
    ├── src/                       # Source files
    │   ├── dsl/                       # Base DSL
    │   ├── learning/                  # Learning-based algorithms
    │   ├── players/                   # Player implementations
    │   ├── search/                    # Search-based algorithms
    │   ├── evaluation.py              # Implementation of all types of evaluations used
    │   ├── game.py                    # Implementation of Can't Stop rules
    │   ├── main.py                    # Main file used for running the algorithms
    │   └── play_cant_stop.py          # Used to generate data for cloning
    ├── human_matches.pkl          # Data from a Human player
    ├── glenn_matches.pkl          # Data from Glenn and Aloi's strategy
    ├── random_matches.pkl         # Data from Random player
    ├── plot_results_cant_stop.py  # Generate the graphs in the article from results/
    ├── requirements.txt     
    └── README.md        

# Dependencies

`Python 3.9` was used for this project. You will need a Python environment that contains the packages listed in `requirements.txt`. 
You can create one with Python's built-in module `venv` running the following command:

```
python3 -m venv /path/to/project/cant-stop-sketch-learning/venv/
```
After [activating](https://docs.python.org/3/library/venv.html) the virtual enviroment, you'll need to install the required packages:

```
python3 -m pip install -r requirements.txt
```

# Running the code

`main.py` programmatically instantiates an algorithm with its arguments based on the flags used when running `main.py`. Below, there is a brief explanation of the flags available for the user. Refer to their respective files (`main.py`, `players/`, `evaluation.py`, etc) for detailed information in the docstrings.

- `-search`: Defines which search algorithm will be used `(SimulatedAnnealing, BottomUpSearch, SketchSimulatedAnnealing, SketchUCT)`.
- `-e`: Defines the evaluation function to be used in the search algorithm `(EvalDoubleProgramDefeatsStrategy, EvalStateBasedImitationAgent, EvalStateBasedImitationGlennAgent, EvalStateBasedImitationRandomAgent, EvalActionBasedImitationAgent, EvalActionBasedImitationGlennAgent, EvalActionBasedImitationRandomAgent)`.
- `-e_2`: Defines the evaluation function to be used in the BR-search from the sketch-search  `(EvalDoubleProgramDefeatsStrategy, EvalStateBasedImitationAgent, EvalStateBasedImitationGlennAgent, EvalStateBasedImitationRandomAgent, EvalActionBasedImitationAgent, EvalActionBasedImitationGlennAgent, EvalActionBasedImitationRandomAgent)`.
- `-time`: Defines the maximum time the search algorithm will run for, in seconds.
- `-time_2`: Defines the maximum time the BR-search from the sketch-search will run for, in seconds.
- `-n`: Defines the number of games played for evaluation.
- `--triage`: Asserts that triage will be used in the evaluation step.
- `-confidence_triage`: Defines the confidence value used to determine the number of games to be played and evaluated during triage evaluation `([0,1])`. 
- `-log_file`: Defines the name of the file where logs will be written as text.
- `-program_file`: Defines the name of the file where programs will be written as text.
- `--double-programs`: Asserts that the synthesis step should synthesize both types of actions in the Can't Stop game. This is a deprecated option, this flag is always necessary currently for the program to run properly.
- `--hole-node`: Asserts that the production rules from the AST allows the use of Hole nodes.
- `-temperature`: Defines the initial temperature of Simulated Annealing's schedule.
- `-alpha`: Simulated Annealing's hyperparameter used in the temperature schedule.
- `-beta`: Simulated Annealing's hyperparameter used in the acceptance function.
- `-c`: Defines the exploration term in UCT.
- `-sim_function`: Defines the simulation function used in the rollout stage in UCT `(SA, Random)`.
- `-sims`: Defines the number of times the function `-sim_function` will run at each UCT step. If greater than one, the returned value is averaged.
- `--reuse-tree`: Asserts that UCT's and SA's trees are maintained in between different runs. 
- `--bidirectional`: Asserts that UCT will use a set of programs previously generated by Bottom Up Search instead of running a function `-sim_function` to fill Hole nodes in programs.
- `-bidirectional_depth`: Defines the depth of programs generated by Bottom Up Search that are going to be used in UCT if `--bidirectional` is set.
- `--detect-equivalence`: Asserts that Bottom Up Search will ignore new programs that produce the same output of previously seen programs, even if they are not exactly equal.
- `-bound`: Defines the maximum size of a program generated by Bottom Up Search.
- `--iterated-best-response`: Asserts that Iterated Best-Response algorithm will be used.

## Examples
Bottom Up Search:
```
python3 src/main.py -search BottomUpSearch -e EvalDoubleProgramDefeatsStrategy -bound 8 -n 10 --detect-equivalence -log_file log_test -program_file program_test -time 10000
```
Iterated Best-Response:
```
python3 src/main.py --iterated-best-response --double-programs --reuse-tree -e EvalDoubleProgramDefeatsStrategy -beta 200 -alpha 0.9 -temperature 100 -n 100 -log_file log_test -program_file program_test -time 10000
```
Simulated Annealing:
```
python3 src/main.py -search SimulatedAnnealing --double-programs --reuse-tree -e EvalDoubleProgramDefeatsStrategy -beta 200 -alpha 0.9 -temperature 100 -n 100 -log_file log_test -program_file program_test -time 10000
```
UCT:
```
python3 src/main.py -search UCT --double-programs --hole-node -e EvalDoubleProgramDefeatsStrategy -sim_function SA -n 100 -c 0.01 -sims 1 -log_file log_test -program_file program_test -time 10000
```
Sketch-SA:
```
python3 src/main.py -search SketchSimulatedAnnealing --double-programs --reuse-tree -e EvalStateBasedImitationAgent -e_2 EvalDoubleProgramDefeatsStrategy -beta 200 -alpha 0.9 -temperature 100 -n 100 -log_file log_test -program_file program_test -time 10000
```
Sketch-UCT:
```
python3 src/main.py -search SketchUCT --double-programs --hole-node -e EvalStateBasedImitationAgent -e_2 EvalDoubleProgramDefeatsStrategy -sim_function SA -n 100 -c 0.01 -sims 1 -log_file log_test -program_file program_test -time 10000 -time_2 10000
```
# License

Distributed under the MIT License.

```
Copyright (c) 2022 Leandro C. Medeiros, David S. Aleixo, Levi H. S. Levis

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
```

