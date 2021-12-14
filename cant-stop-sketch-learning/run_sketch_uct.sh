#!/bin/bash
#SBATCH --cpus-per-task=1   # maximum CPU cores per GPU request: 6 on Cedar, 16 on Graham.
#SBATCH --mem=12000M        # memory per node
#SBATCH --time=03-02:00      # time (DD-HH:MM)
#SBATCH --output=%N-%j.out  # %N for node name, %j for jobID
#SBATCH --account=rrg-lelis

module load python/3.6

virtualenv --no-download $SLURM_TMPDIR/env
source $SLURM_TMPDIR/env/bin/activate
pip install --no-index --upgrade pip
pip install numpy --no-index

cp -r src $SLURM_TMPDIR/

python -u $SLURM_TMPDIR/src/main.py -search SketchUCT ${param} --hole-node -e ${eval} -e_2 EvalDoubleProgramDefeatsStrategy -sim_function ${sim_function} -beta ${beta} -alpha ${alpha}  -n ${n} -sims ${sims} -c ${c} -log_file ${log_file} -program_file ${program_file} -time 36000 -time_2 223200
