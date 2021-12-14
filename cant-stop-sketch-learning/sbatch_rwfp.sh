#!/bin/bash

declare -a params=("--double-programs")
declare -a evals=("EvalDoubleProgramDefeatsStrategy")

output="output/"
mkdir -p ${output}

for iter in {1..10}; do
	for n in 1000; do
		for eval in ${evals[@]}; do
			for param in ${params[@]}; do
				name_param=${param//--/}
				name_param=${param//-/}
				param=${param//--/ --}
				
				alpha_name=${alpha//./}
			
				lower_eval=$(echo ${eval} | tr "A-Z" "a-z")
				output_exp="${output}rwfp-${lower_eval}-n${n}-${name_param}-v${iter}"
				log_file="log-${lower_eval}-n${n}-${name_param}-v${iter}"
				program_file="programs-${lower_eval}-n${n}-${name_param}-v${iter}"
	
				sbatch --output=${output_exp} --export=log_file="${log_file}",param="${param}",program_file=${program_file},eval=${eval},n=${n} run_rwfp.sh
			done
		done
	done
done

