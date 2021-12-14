#!/bin/bash

declare -a params=("--double-programs--reuse-tree")
#declare -a evals=("EvalDoubleProgramDefeatsStrategy")
declare -a evals=("EvalStateBasedImitationAgent" "EvalStateBasedImitationGlennAgent" "EvalStateBasedImitationRandomAgent" "EvalActionBasedImitationAgent" "EvalActionBasedImitationGlennAgent" "EvalActionBasedImitationRandomAgent")
#declare -a evals=("EvalActionBasedImitationGlennAgent" "EvalActionBasedImitationRandomAgent")

output="output/"
mkdir -p ${output}

for iter in {1..30}; do
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
						
							lower_eval=$(echo ${eval} | tr "A-Z" "a-z")
							output_exp="${output}sketch-sa-${lower_eval}-beta${beta}-alpha${alpha_name}-temp${temp}-n${n}-${name_param}-v${iter}"
							log_file="log-${lower_eval}-beta${beta}-alpha${alpha_name}-temp${temp}-n${n}-${name_param}-v${iter}"
							program_file="programs-${lower_eval}-beta${beta}-alpha${alpha_name}-temp${temp}-n${n}-${name_param}-v${iter}"
				
							#echo ${output_exp}
							#echo ${log_file}
							#echo ${program_file}
							#echo ${param}
				
							sbatch --output=${output_exp} --export=log_file="${log_file}",param="${param}",temp=${temp},alpha=${alpha},beta=${beta},program_file=${program_file},eval=${eval},n=${n} run_sketch_sa_two_evals.sh
						done
					done
				done
			done
		done
	done
done

