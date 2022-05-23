import argparse
from os.path import isfile, join
from os import listdir
import csv
import numpy as np
import matplotlib.pyplot as plt
from scipy.interpolate import interp1d
from scipy import interpolate
import matplotlib

import seaborn as sns
import pandas as pd


def read_training_data(folder):
    files = [f for f in listdir(folder) if isfile(join(folder, f)) and f != '.DS_Store']  

    time_elapsed_by_method = {}
    winning_rate_by_method = {}
    imitation_value_by_method = {}
       
    for filename in files:
        # try-catch to ignore missing values
        try:
            time_elapsed = [0]
            winning_rate = [0]
            imitation_value = [0]
            
            with open(join(folder, filename)) as data_file:
                if filename[5] == '0' or filename[5] == '1':
                    filename = filename[0:8] 
                else:
                    filename = filename[0:7]
                content = data_file.readlines()
                for data in content:
                    curr_string = data
                    curr_string = curr_string.split("\t")
                    time_elapsed.append(float(curr_string[0]))
                    winning_rate.append(float(curr_string[1]))
                    imitation_value.append(float(curr_string[2]))
                if filename not in time_elapsed_by_method:
                    time_elapsed_by_method[filename] = []
                    winning_rate_by_method[filename] = []
                    imitation_value_by_method[filename] = []
                    
                time_elapsed_by_method[filename].append(time_elapsed)
                winning_rate_by_method[filename].append(winning_rate)
                imitation_value_by_method[filename].append(imitation_value)
        except Exception as e:
            continue
    return time_elapsed_by_method, winning_rate_by_method, imitation_value_by_method

def get_name(filename, algo_names):
    return algo_names[int(filename[-1])]

def min_max_time(time_elapsed_by_method, accepted_methods):
    
    maximum_times = []
    for method_name, all_times in time_elapsed_by_method.items():
        
        if method_name not in accepted_methods:
            continue
        
        for run_index in range(len(all_times)):
            maximum_times.append(max(all_times[run_index]))
            print(max(all_times[run_index]))
            
            
    return min(maximum_times)

def generate_graph(parameters, map_name, matches_name_file, algo_names, match, type_graph):
    time_elapsed_by_method, winning_rate_by_method, imitation_value_by_method = read_training_data(parameters.log_folder)

    accepted_methods = set(['out_' + str(match) + '_' + str(algo) for algo in algo_names])
    print('accepted_methods = ', accepted_methods)
    #exit()
    union_winrate = []
    union_time = []
    name_column = []

    min_max_value = min_max_time(time_elapsed_by_method, accepted_methods)
    
    if type_graph == 'WinningRate':
        y_axis_to_be_used = winning_rate_by_method 
    else:
        y_axis_to_be_used = imitation_value_by_method 


    for method_name, all_winrate in y_axis_to_be_used.items():
        
        if method_name not in accepted_methods:
            continue
        
        reduced_name = get_name(method_name, algo_names)
        
        for run_index in range(len(all_winrate)):            
            x_range = np.linspace(0, min_max_value, num=50, endpoint=True)
            interpolated_function = interp1d(time_elapsed_by_method[method_name][run_index], y_axis_to_be_used[method_name][run_index])
            interpolated_winrate = interpolated_function(x_range)
            
            for i in range(len(x_range)):
                # For zoomed plots, comment if not necessary
                #if x_range[i] > 20000:
                #    break
                union_winrate.append(interpolated_winrate[i])
                union_time.append(x_range[i])
                name_column.append(reduced_name)
        
    font = {'family' : 'normal',
        'weight' : 'normal',
        'size'   : 18}

    matplotlib.rc('font', **font)

    iter_frame = pd.DataFrame({'winrate':union_winrate, 'time':union_time, 'name':name_column})
 
    fig, ax = plt.subplots()
    sns_plot = sns.lineplot(x='time', y='winrate', hue='name', style='name', markers=False, data=iter_frame)
    ax.set_ylabel('Winning Rate')
    ax.set_xlabel('Running Time (Seconds)')
    #fig.suptitle(map_name)
    plt.title(map_name)
    matplotlib.pyplot.locator_params(axis='x', nbins=6)
    handles, labels = ax.get_legend_handles_labels()
    ax.legend(handles=handles[0:], labels=labels[0:])
    axes = plt.gca()
    # Limitar o alcance do eixo x
    #axes.set_xlim([0,1000])
    axes.set_ylim(-0.05, 1.05)
    #axes.set_xlim(-1000, 20000)
    plot_name = matches_name_file[int(match)]
    sns_plot.get_figure().savefig(plot_name + '.pdf', bbox_inches='tight')
    #plt.show()

def main():
    parser = argparse.ArgumentParser()
    
    parser.add_argument('-logs', action='store', dest='log_folder', 
                        default='results/', 
                        help='Folder with the log files')
    
    parameters = parser.parse_args()
    

    
    map_name =  {
    0  : "Training Strategy: A3N, 16 x 16 Map",
    1  : "Training Strategy: RR, 16 x 16 Map",
    2  : "Training Strategy: COAC, 16 x 16 Map",
    3  : "Training Strategy: A3N, 24 x 24 Map",
    4  : "Training Strategy: RR, 24 x 24 Map",
    5  : "Training Strategy: COAC, 24 x 24 Map",
    6  : "Training Strategy: A3N, 32 x 32 Map",
    7  : "Training Strategy: RR, 32 x 32 Map",
    8  : "Training Strategy: COAC, 32 x 32 Map",
    9  : "Training Strategy: A3N, 64 x 64 Map",
    10 : "Training Strategy: RR, 64 x 64 Map",
    11 : "Training Strategy: COAC, 64 x 64 Map",
    }
    
    matches_name_file = {
    0  : "A3N16x16Map_UCT",
    1  : "RR16x16Map_UCT",
    2  : "COAC16x16Map_UCT",
    3  : "A3N24x24Map_UCT",
    4  : "RR24x24Map_UCT",
    5  : "COAC24x24Map_UCT",
    6  : "A3N32x32Map_UCT",
    7  : "RR32x32Map_UCT",
    8  : "COAC32x32Map_UCT",
    9  : "A3N64x64Map_UCT",
    10 : "RR64x64Map_UCT",
    11 : "COAC64x64Map_UCT",
    }


    algo_names = {
        2:"UCT Baseline",
        4:"Sketch-UCT(A)",
        5:"Sketch-UCT(O)",
    }

    # 'WinningRate' for winning rate as y-axis, else imitation value is used
    type_graph = 'WinningRate'
    
    # Loop over the graphs you want to generate
    for i in range(len(map_name)):
    #for i in [0,1,2]:
    #for i in [8]:
        match = i
        generate_graph(parameters, map_name[i], matches_name_file, algo_names, match, type_graph)

    
if __name__ == "__main__":
    main()