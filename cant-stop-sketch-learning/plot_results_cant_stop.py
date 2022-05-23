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
    winrate_by_method = {}
    time_by_method = {}
       
    for filename in files:
        winrate = [0]
        time_seconds = [0]
        
        with open(join(folder, filename)) as data_file:
            if '.' in filename:
                continue
            filename = filename[0:filename.rfind('-')]
            reader = csv.reader(data_file, delimiter = ',')
            for row in reader:
                if 'sketch' in filename:
                    time_seconds.append(float(row[2]))
                else:
                    time_seconds.append(float(row[3]))            
                winrate.append(float(row[1]))

            if filename not in winrate_by_method:
                winrate_by_method[filename] = []
                time_by_method[filename] = []
            winrate_by_method[filename].append(winrate)
            time_by_method[filename].append(time_seconds)
            
    return winrate_by_method, time_by_method

def get_name(filename):
    if 'sketch-sa-log-evalstatebasedimitationagent' in filename:
        name = 'Sketch-SA(O)'

    elif 'sketch-sa-log-evalstatebasedimitationrandomagent' in filename:
        name = 'Sketch-SA(O)'

    elif 'sketch-sa-log-evalstatebasedimitationglennagent' in filename:
        name = 'Sketch-SA(O)'

    elif 'sketch-sa-log-evalactionbasedimitationagent' in filename:
        name = 'Sketch-SA(A)'

    elif 'sketch-sa-log-evalactionbasedimitationrandomagent' in filename:
        name = 'Sketch-SA(A)'

    elif 'sketch-sa-log-evalactionbasedimitationglennagent' in filename:
        name = 'Sketch-SA(A)'
    elif 'sa-log-evaldoubleprogramdefeatsstrategy' in filename:
        name = 'SA Baseline'

    elif 'hybrid-uct-log-evalstatebasedimitationagent' in filename:
        name = 'Sketch-UCT(O)'

    elif 'hybrid-uct-log-evalstatebasedimitationrandomagent' in filename:
        name = 'Sketch-UCT(O)'

    elif 'hybrid-uct-log-evalstatebasedimitationglennagent' in filename:
        name = 'Sketch-UCT(O)'

    elif 'hybrid-uct-log-evalactionbasedimitationagent' in filename:
        name = 'Sketch-UCT(A)'

    elif 'hybrid-uct-log-evalactionbasedimitationrandomagent' in filename:
        name = 'Sketch-UCT(A)'

    elif 'hybrid-uct-log-evalactionbasedimitationglennagent' in filename:
        name = 'Sketch-UCT(A)'

    elif 'uct-log-evaldoubleprogramdefeatsstrategy' in filename:
        name = 'UCT Baseline'
    #print(filename)
    return name

def min_max_time(time_by_method, accepted_methods):
    
    maximum_times = []
    for method_name, all_times in time_by_method.items():
        if method_name not in accepted_methods:
            continue
        
        for run_index in range(len(all_times)):
            maximum_times.append(max(all_times[run_index]))
            print(max(all_times[run_index]))
            
            
    return min(maximum_times)

def main():
    parser = argparse.ArgumentParser()
    
    parser.add_argument('-logs', action='store', dest='log_folder', 
                        default='results/', 
                        help='Folder with the log files')
    
    parameters = parser.parse_args()
    
    winrate_by_method, time_by_method = read_training_data(parameters.log_folder)
    accepted_methods = set(
                        [
                        
                        
                        
                        
                        #'sketch-sa-log-evalstatebasedimitationagent-beta200-alpha09-temp100-n1000-doubleprogramsreusetree',
                        #'sketch-sa-log-evalstatebasedimitationglennagent-beta200-alpha09-temp100-n1000-doubleprogramsreusetree',
                        #'sketch-sa-log-evalstatebasedimitationrandomagent-beta200-alpha09-temp100-n1000-doubleprogramsreusetree',
                        #'sketch-sa-log-evalactionbasedimitationagent-beta200-alpha09-temp100-n1000-doubleprogramsreusetree',
                        #'sketch-sa-log-evalactionbasedimitationglennagent-beta200-alpha09-temp100-n1000-doubleprogramsreusetree',
                        #'sketch-sa-log-evalactionbasedimitationrandomagent-beta200-alpha09-temp100-n1000-doubleprogramsreusetree',
                        #'sa-log-evaldoubleprogramdefeatsstrategy-beta200-alpha09-temp100-n1000-doubleprogramsreusetree',

                        
                        
                        #'hybrid-uct-log-evalstatebasedimitationagent-beta200-alpha09-n1000-c10-sa-doubleprograms',
                        #'hybrid-uct-log-evalstatebasedimitationglennagent-beta200-alpha09-n1000-c10-sa-doubleprograms',
                        'hybrid-uct-log-evalstatebasedimitationrandomagent-beta200-alpha09-n1000-c10-sa-doubleprograms',
                        #'hybrid-uct-log-evalactionbasedimitationagent-beta200-alpha09-n1000-c10-sa-doubleprograms',
                        #'hybrid-uct-log-evalactionbasedimitationglennagent-beta200-alpha09-n1000-c10-sa-doubleprograms',
                        'hybrid-uct-log-evalactionbasedimitationrandomagent-beta200-alpha09-n1000-c10-sa-doubleprograms',
                        'uct-log-evaldoubleprogramdefeatsstrategy-beta200-alpha09-n1000-c10-sa-doubleprograms',
                        ]
                    )

    union_winrate = []
    union_time = []
    name_column = []
    
    min_max_value = min_max_time(time_by_method, accepted_methods)
    
    for method_name, all_winrate in winrate_by_method.items():
        
        if method_name not in accepted_methods:
            continue
        
        reduced_name = get_name(method_name)
        for run_index in range(len(all_winrate)):            
            x_range = np.linspace(0, min_max_value, num=50, endpoint=True)
            interpolated_function = interp1d(time_by_method[method_name][run_index], winrate_by_method[method_name][run_index])
            interpolated_winrate = interpolated_function(x_range)
            
            
            
            for i in range(len(x_range)):
                if x_range[i] > 172800:
                    break
                union_winrate.append(interpolated_winrate[i])
                union_time.append(x_range[i])
                name_column.append(reduced_name)
         
    iter_frame = pd.DataFrame({'winrate':union_winrate, 'time':union_time, 'name':name_column})
    
    font = {'family' : 'normal',
        'weight' : 'normal',
        'size'   : 18}

    matplotlib.rc('font', **font)

    fix, ax = plt.subplots()
    palette ={"UCT Baseline": "C0", "Sketch-UCT(A)": "C1", "Sketch-UCT(O)": "C2"}
    dashes = {"UCT Baseline": (0, ()), "Sketch-UCT(A)": (0, (5, 1)), "Sketch-UCT(O)": (0, (1, 1))}
    #sns_plot = sns.lineplot(x='time', y='winrate', hue='name', style='name', markers=False, data=iter_frame)
    #sns_plot = sns.lineplot(x='time', y='winrate', hue='name', style='name', markers=False, data=iter_frame, palette=palette, dashes=[(4, 1), (1, 1), (1, 0)]) #(a,o,baseline)
    sns_plot = sns.lineplot(x='time', y='winrate', hue='name', style='name', markers=False, data=iter_frame, palette=palette, dashes=[(4, 1), (1, 1), (1, 0)]) #(a,o,baseline)
    #sns_plot = sns.lineplot(x='time', y='winrate', hue='name', style="name", style_order="name", markers=False, data=iter_frame)
    
    ax.set_ylabel('Winning Rate')
    ax.set_xlabel('Running Time (Seconds)')
    ax.set_ylim(-0.015, 0.5)
    matplotlib.pyplot.locator_params(axis='x', nbins=6)
    #Limit x-axis range
    #axes = plt.gca()
    #axes.set_xlim([0,50000])

    if 'sketch-sa-log-evalstatebasedimitationagent' in list(accepted_methods)[0]:
        plt.title("Training Strategy: Human Player")
        plot_name = 'sa_human'
    elif 'sketch-sa-log-evalactionbasedimitationagent' in list(accepted_methods)[0]:
        plt.title("Training Strategy: Human Player")
        plot_name = 'sa_human'

    elif 'sketch-sa-log-evalstatebasedimitationglennagent' in list(accepted_methods)[0]:
        plt.title("Training Strategy: Glenn and Aloi's")
        plot_name = 'sa_glenn'
    elif 'sketch-sa-log-evalactionbasedimitationglennagent' in list(accepted_methods)[0]:
        plt.title("Training Strategy: Glenn and Aloi's")
        plot_name = 'sa_glenn'

    elif 'sketch-sa-log-evalstatebasedimitationrandomagent' in list(accepted_methods)[0]:
        plt.title("Training Strategy: Random")
        plot_name = 'sa_random'
    elif 'sketch-sa-log-evalactionbasedimitationrandomagent' in list(accepted_methods)[0]:
        plt.title("Training Strategy: Random")
        plot_name = 'sa_random'


    if 'hybrid-uct-log-evalstatebasedimitationagent' in list(accepted_methods)[0]:
        plt.title("Training Strategy: Human Player")
        plot_name = 'uct_human'
    elif 'hybrid-uct-log-evalactionbasedimitationagent' in list(accepted_methods)[0]:
        plt.title("Training Strategy: Human Player")
        plot_name = 'uct_human'

    elif 'hybrid-uct-log-evalstatebasedimitationglennagent' in list(accepted_methods)[0]:
        plt.title("Training Strategy: Glenn and Aloi's")
        plot_name = 'uct_glenn'
    elif 'hybrid-uct-log-evalactionbasedimitationglennagent' in list(accepted_methods)[0]:
        plt.title("Training Strategy: Glenn and Aloi's")
        plot_name = 'uct_glenn'

    elif 'hybrid-uct-log-evalstatebasedimitationrandomagent' in list(accepted_methods)[0]:
        plt.title("Training Strategy: Random")
        plot_name = 'uct_random'
    elif 'hybrid-uct-log-evalactionbasedimitationrandomagent' in list(accepted_methods)[0]:
        plt.title("Training Strategy: Random")
        plot_name = 'uct_random'




    handles, labels = ax.get_legend_handles_labels()
    #print('handles = ', handles)
    #print('labels = ', labels)
    new_handle = [handles[2], handles[0], handles[1]]
    new_lable = [labels[2], labels[0], labels[1]]
    ax.legend(handles=new_handle[0:], labels=new_lable[0:], loc='upper right', title_fontsize='8')
    #ax.legend(handles=handles[0:], labels=labels[0:], loc='lower right')
    #plot_name = 'test'
    sns_plot.get_figure().savefig(plot_name + '.pdf', bbox_inches='tight')
    #plt.show()

    
if __name__ == "__main__":
    main()