#!/bin/bash

declare -a params=("--double-programs--reuse-tree--triage")
declare -a evals=("EvalDoubleProgramDefeatsStrategy")

output="output/"
mkdir -p ${output}

for iter in {1..10}; do
	for confidence_triage in 0.1 0.5 0.6 0.7 0.8 0.9; do
		for temp in 100; do
			for alpha in 0.9; do
				for beta in 200; do
					for n in 1000; do
						for eval in ${evals[@]}; do
							for param in ${params[@]}; do
								name_param=${param//--/}
								name_param=${param//-/}
								param=${param//--/ --}
								
								alpha_name=${alpha//./}
								confidence_name=${confidence_triage//./}
							
								lower_eval=$(echo ${eval} | tr "A-Z" "a-z")
								output_exp="${output}sa-${lower_eval}-beta${beta}-alpha${alpha_name}-c${confidence_name}-temp${temp}-n${n}-${name_param}-v${iter}"
								log_file="log-${lower_eval}-beta${beta}-alpha${alpha_name}-c${confidence_name}-temp${temp}-n${n}-${name_param}-v${iter}"
								program_file="programs-${lower_eval}-beta${beta}-alpha${alpha_name}-c${confidence_name}-temp${temp}-n${n}-${name_param}-v${iter}"
					
								#echo ${output_exp}
								#echo ${log_file}
								#echo ${program_file}
								#echo ${param}
					
								sbatch --output=${output_exp} --export=log_file="${log_file}",confidence_triage=${confidence_triage},param="${param}",temp=${temp},alpha=${alpha},beta=${beta},program_file=${program_file},eval=${eval},n=${n} run_sa_triage.sh
							done
						done
					done
				done
			done
		done
	done
done

