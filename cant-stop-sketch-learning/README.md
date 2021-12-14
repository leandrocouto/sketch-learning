Python implementation of the board game "Can't Stop".

To run Bottom-Up Search:

python src/main.py -search BottomUpSearch -e EvalColumnActionDefeatsStrategy -bound 8 -n 10 -log_file log_test -program_file program_test -time 1200

To run Simulated Annealing:

python src/main.py -search SimulatedAnnealing --double-programs --reuse-tree -e EvalStateBasedImitationAgent -beta 200 -alpha 0.9 -temperature 100  -n 100 -log_file log_test -program_file program_test -time 1200

To run UCT:

python src/main.py -search UCT --double-programs --hole-node -e EvalDoubleProgramDefeatsStrategy -sim_function SA -n 100 -c 0.01 -sims 1 -log_file log_test -program_file program_test -time 1200  

To run Sketch Simulated Annealing:

python src/main.py -search SketchSimulatedAnnealing --double-programs --reuse-tree -e EvalStateBasedImitationAgent -e_2 EvalDoubleProgramDefeatsStrategy -beta 200 -alpha 0.9 -temperature 100 -n 100 -log_file log_test -program_file program_test -time 1200

To run Sketch UCT:

python src/main.py -search SketchUCT --double-programs --hole-node -e EvalStateBasedImitationAgent -e_2 EvalDoubleProgramDefeatsStrategy -sim_function SA -n 100 -c 0.01 -sims 1 -log_file log_test -program_file program_test -time 1200 -time_2 1200

To run Random Walk Selfplay:

python src/main.py -search RandomWalkSelfplay --double-programs -n 10 -log_file log_test -program_file program_test -time 1200

To run Random Walk Fictitious Play:

python src/main.py -search RandomWalkFictitiousPlay --double-programs -n 10 -log_file log_test -program_file program_test -time 1200
