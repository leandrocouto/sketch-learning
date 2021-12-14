#!/bin/bash

#declare -a evals=("EvalSingleProgramDefeatsStrategy" "EvalColumnActionDefeatsStrategy" "EvalYesNoActionDefeatsStrategy")
#declare -a params=("--triage--double-programs--reuse-tree" "--triage--double-programs")
declare -a params=("--triage--double-programs--reuse-tree")
declare -a evals=("EvalDoubleProgramDefeatsStrategy")

output="output/"
mkdir -p ${output}

for iter in {1..2}; do
	for temp in 100; do
		for alpha in 0.6 0.8 0.9; do
			for beta in 100 150 200; do
				for n in 1000; do
					for eval in ${evals[@]}; do
						for param in ${params[@]}; do
							name_param=${param//double-program/doubleprogram}
							name_param=${name_param//reuse-tree/reusetree}
							name_param=${name_param//--/-}
							param=${param//--/ --}
							
							alpha_name=${alpha//./}
						
							lower_eval=$(echo ${eval} | tr "A-Z" "a-z")
							output_exp="${output}ibr-sa-${lower_eval}-beta${beta}-alpha${alpha_name}-temp${temp}-n${n}${name_param}-v${iter}"
							log_file="log-${lower_eval}-beta${beta}-alpha${alpha_name}-temp${temp}-n${n}${name_param}-v${iter}"
							program_file="programs-${lower_eval}-beta${beta}-alpha${alpha_name}-temp${temp}-n${n}${name_param}-v${iter}"
				
							#echo ${output_exp}
							#echo ${log_file}
							#echo ${program_file}
							#echo ${param}
				
							sbatch --output=${output_exp} --export=log_file="${log_file}",param="${param}",temp=${temp},alpha=${alpha},beta=${beta},program_file=${program_file},eval=${eval},n=${n} run_ibr_sa.sh
						done
					done
				done
			done
		done
	done
done

