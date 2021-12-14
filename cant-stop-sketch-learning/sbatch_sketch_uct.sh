#!/bin/bash

declare -a evals=("EvalStateBasedImitationAgent" "EvalStateBasedImitationGlennAgent" "EvalStateBasedImitationRandomAgent" "EvalActionBasedImitationAgent" "EvalActionBasedImitationGlennAgent" "EvalActionBasedImitationRandomAgent")
declare -a params=("--double-programs")
declare -a sim_functions=("SA")

output="output-sketch-uct/"
mkdir -p ${output}

for iter in {1..30}; do
	for n in 1000; do
		for sims in 1; do
			for c in 10; do
				for alpha in 0.9; do
					for beta in 200; do
						for eval in ${evals[@]}; do
							for sim_function in ${sim_functions[@]}; do
								for param in ${params[@]}; do
									lower_eval=$(echo ${eval} | tr "A-Z" "a-z")
									lower_sim=$(echo ${sim_function} | tr "A-Z" "a-z")
									c_name=${c//./}
									name_param=${param//double-program/doubleprogram}
									name_param=${name_param//--/-}
									param=${param//--/ --}
									alpha_name=${alpha//./}
									
									#output_exp="${output}${lower_eval}-n${n}-sims${sims}-c${c_name}-${lower_sim}-${name_param}-v${iter}"
									#log_file="log-${lower_eval}-n${n}-sims${sims}-c${c_name}-${lower_sim}-${name_param}-v${iter}"
									#program_file="programs-${lower_eval}-n${n}-sims${sims}-c${c_name}-${lower_sim}-${name_param}-v${iter}"
						
									output_exp="${output}sketch-uct-${lower_eval}-beta${beta}-alpha${alpha_name}-n${n}-c${c_name}-${lower_sim}${name_param}-v${iter}"
									log_file="log-${lower_eval}-beta${beta}-alpha${alpha_name}-n${n}-c${c_name}-${lower_sim}${name_param}-v${iter}"
									program_file="programs-${lower_eval}-beta${beta}-alpha${alpha_name}-n${n}-c${c_name}-${lower_sim}${name_param}-v${iter}"
						
									#echo ${output_exp}
									#echo ${log_file}
									#echo ${program_file}
									#echo ${param}
						
									sbatch --output=${output_exp} --export=log_file="${log_file}",param="${param}",sim_function=${sim_function},alpha=${alpha},beta=${beta},program_file=${program_file},eval=${eval},n=${n},sims=${sims},c=${c} run_sketch_uct.sh
								done
							done
						done
					done
				done
			done
		done
	done
done

