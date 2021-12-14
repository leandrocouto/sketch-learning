import itertools
from dsl import base_dsl

class VarList(base_dsl.VarList):
    def __init__(self):
        super().__init__()

class VarScalarFromArray(base_dsl.VarScalarFromArray):
    def __init__(self):
        super().__init__()
    
class VarScalar(base_dsl.VarScalar):
    def __init__(self):
        super().__init__()
    
class NumericConstant(base_dsl.NumericConstant):
    def __init__(self):
        super().__init__()

class DifficultyScore(base_dsl.DifficultyScore):
    def __init__(self):
        super().__init__()

class StringConstant(base_dsl.StringConstant):
    def __init__(self):
        super().__init__()

class NumberAdvancedByAction(base_dsl.NumberAdvancedByAction):
    def __init__(self):
        super().__init__()

class IsNewNeutral(base_dsl.IsNewNeutral):
    def __init__(self):
        super().__init__()

class NumberAdvancedThisRound(base_dsl.NumberAdvancedThisRound):
    def __init__(self):
        super().__init__()
        
class PlayerWinsAfterStopping(base_dsl.PlayerWinsAfterStopping):
    def __init__(self):
        super().__init__()

class AreThereNeutralsToPlay(base_dsl.AreThereNeutralsToPlay):
    def __init__(self):
        super().__init__()

class LocalList(base_dsl.LocalList):
    def __init__(self):
        super().__init__()

class PositionsOpponentHasSecuredInColumn(base_dsl.PositionsOpponentHasSecuredInColumn):
    def __init__(self):
        super().__init__()

class PositionsPlayerHasSecuredInColumn(base_dsl.PositionsPlayerHasSecuredInColumn):
    def __init__(self):
        super().__init__()        

class Times(base_dsl.Times):
    def __init__(self):
        super().__init__()
    
    @staticmethod
    def grow(plist, size):       
        new_programs = []
        
        # generates all combinations of cost of size 2 varying from 1 to size - 1
        combinations = list(itertools.product(range(1, size - 1), repeat=2))

        for c in combinations:           
            # skip if the cost combination exceeds the limit
            if c[0] + c[1] + 1 != size:
                continue
                    
            # retrive bank of programs with costs c[0], c[1], and c[2]
            program_set1 = plist.get_programs(c[0])
            program_set2 = plist.get_programs(c[1])
                
            for t1, programs1 in program_set1.items():                
                # skip if t1 isn't a node accepted by Lt
                if t1 not in Times.accepted_rules(0):
                    continue
                
                for p1 in programs1:                       

                    for t2, programs2 in program_set2.items():                
                        # skip if t1 isn't a node accepted by Lt
                        if t2 not in Times.accepted_rules(1):
                            continue
                        
                        for p2 in programs2:
    
                            times = Times()
                            times.add_child(p1)
                            times.add_child(p2)
                            new_programs.append(times)
            
                            yield times
        return new_programs  

class Minus(base_dsl.Minus):
    def __init__(self):
        super().__init__()
    
    @staticmethod
    def grow(plist, size):               
        new_programs = []

        # generates all combinations of cost of size 2 varying from 1 to size - 1
        combinations = list(itertools.product(range(1, size - 1), repeat=2))
 
        for c in combinations:                       
            # skip if the cost combination exceeds the limit
            if c[0] + c[1] + 1 != size:
                continue
                 
            # retrive bank of programs with costs c[0], c[1], and c[2]
            program_set1 = plist.get_programs(c[0])
            program_set2 = plist.get_programs(c[1])
                 
            for t1, programs1 in program_set1.items():                
                # skip if t1 isn't a node accepted by Lt
                if t1 not in Minus.accepted_rules(0):
                    continue
                 
                for p1 in programs1:                       
 
                    for t2, programs2 in program_set2.items():                
                        # skip if t1 isn't a node accepted by Lt
                        if t2 not in Minus.accepted_rules(1):
                            continue
                         
                        for p2 in programs2:
     
                            minus = Minus()
                            minus.add_child(p1)
                            minus.add_child(p2)
                            new_programs.append(minus)
             
                            yield minus
        return new_programs  
 
class Plus(base_dsl.Plus):
    def __init__(self):
        super().__init__()
    
    @staticmethod
    def grow(plist, size):               
        new_programs = []         
        # generates all combinations of cost of size 2 varying from 1 to size - 1
        combinations = list(itertools.product(range(1, size - 1), repeat=2))
 
        for c in combinations:                       
            # skip if the cost combination exceeds the limit
            if c[0] + c[1] + 1 != size:
                continue
                 
            # retrive bank of programs with costs c[0], c[1], and c[2]
            program_set1 = plist.get_programs(c[0])
            program_set2 = plist.get_programs(c[1])
                 
            for t1, programs1 in program_set1.items():                
                # skip if t1 isn't a node accepted by Lt
                if t1 not in Plus.accepted_rules(0):
                    continue
                 
                for p1 in programs1:                       
 
                    for t2, programs2 in program_set2.items():                
                        # skip if t1 isn't a node accepted by Lt
                        if t2 not in Plus.accepted_rules(0):
                            continue
                         
                        for p2 in programs2:
     
                            plus = Plus()
                            plus.add_child(p1)
                            plus.add_child(p2)
                            new_programs.append(plus)
             
                            yield plus
        return new_programs  
     
 
class Function(base_dsl.Function):
    def __init__(self):
        super().__init__() 
    
    @staticmethod
    def grow(plist, size):
        new_programs = []
          
        program_set = plist.get_programs(size - 1)
                     
        for t1, programs1 in program_set.items():                
            # skip if t1 isn't a node accepted by Lt
            if t1 not in Function.accepted_rules(0):
                continue
             
            for p1 in programs1:                       
 
                func = Function()
                func.add_child(p1)
                new_programs.append(func)
         
                yield func
        return new_programs
    
class AssignActionToReturn(base_dsl.AssignActionToReturn):
    def __init__(self):
        super().__init__() 
     
    @staticmethod
    def grow(plist, size):
        new_programs = []
          
        program_set = plist.get_programs(size - 1)
                     
        for t1, programs1 in program_set.items():                
            # skip if t1 isn't a node accepted by Lt
            if t1 not in AssignActionToReturn.accepted_rules(0):
                continue
             
            for p1 in programs1:                       
 
                assign = AssignActionToReturn()
                assign.add_child(p1)
                new_programs.append(assign)
         
                yield assign
        return new_programs

class ITE(base_dsl.ITE):
    def __init__(self):
        super().__init__()
    
    @staticmethod
    def grow(plist, size):               
        new_programs = []
        
        # generates all combinations of cost of size 2 varying from 1 to size - 1
        combinations = list(itertools.product(range(0, size), repeat=3))
 
        for c in combinations:                       
            # skip if the cost combination exceeds the limit
            if c[0] + c[1] + c[2] + 1 != size:
                continue
                 
            # retrive bank of programs with costs c[0], c[1], and c[2]
            program_set1 = plist.get_programs(c[0])
            program_set2 = plist.get_programs(c[1])
            program_set3 = plist.get_programs(c[2])
                 
            for t1, programs1 in program_set1.items():                

                if t1 not in ITE.accepted_rules(0):
                    continue
                 
                for p1 in programs1:                       
 
                    for t2, programs2 in program_set2.items():                

                        if t2 not in ITE.accepted_rules(1):
                            continue
                         
                        for p2 in programs2:
                            
                            for t3, programs3 in program_set3.items():                

                                if t3 not in ITE.accepted_rules(2):
                                    continue
                                 
                                for p3 in programs3:
     
                                    ite = ITE()
                                    ite.add_child(p1)
                                    ite.add_child(p2)
                                    ite.add_child(p3)
                                    new_programs.append(ite)
                     
                                    yield ite
        return new_programs  

class In(base_dsl.In):
    def __init__(self):
        super().__init__()
        
    @staticmethod
    def grow(plist, size):               
        new_programs = []
         
        # generates all combinations of cost of size 2 varying from 1 to size - 1
        combinations = list(itertools.product(range(0, size), repeat=2))
 
        for c in combinations:                       
            # skip if the cost combination exceeds the limit
            if c[0] + c[1] + 1 != size:
                continue
                 
            # retrive bank of programs with costs c[0], c[1], and c[2]
            program_set1 = plist.get_programs(c[0])
            program_set2 = plist.get_programs(c[1])
                 
            for t1, programs1 in program_set1.items():                
                # skip if t1 isn't a node accepted by Lt
                if t1 not in In.accepted_rules(0):
                    continue
                 
                for p1 in programs1:                       
 
                    for t2, programs2 in program_set2.items():                
                        # skip if t1 isn't a node accepted by Lt
                        if t2 not in In.accepted_rules(1):
                            continue
                         
                        for p2 in programs2:
     
                            in_p = In()
                            in_p.add_child(p1)
                            in_p.add_child(p2)
                            new_programs.append(in_p)
             
                            yield in_p
        return new_programs  

class LT(base_dsl.LT):
    def __init__(self):
        super().__init__()
    
    @staticmethod
    def grow(plist, size):               
        new_programs = []
                 
        # generates all combinations of cost of size 2 varying from 1 to size - 1
        combinations = list(itertools.product(range(0, size), repeat=2))
 
        for c in combinations:                       
            # skip if the cost combination exceeds the limit
            if c[0] + c[1] + 1 != size:
                continue
                 
            # retrive bank of programs with costs c[0], c[1], and c[2]
            program_set1 = plist.get_programs(c[0])
            program_set2 = plist.get_programs(c[1])
                 
            for t1, programs1 in program_set1.items():                
                # skip if t1 isn't a node accepted by Lt
                if t1 not in LT.accepted_rules(0):
                    continue
                 
                for p1 in programs1:                       
 
                    for t2, programs2 in program_set2.items():                
                        # skip if t1 isn't a node accepted by Lt
                        if t2 not in LT.accepted_rules(1):
                            continue
                         
                        for p2 in programs2:
     
                            lt = LT()
                            lt.add_child(p1)
                            lt.add_child(p2)
                            new_programs.append(lt)
             
                            yield lt
        return new_programs  

class Argmax(base_dsl.Argmax):
    def __init__(self):
        super().__init__()
     
    @staticmethod
    def grow(plist, size):       
        new_programs = []
        program_set = plist.get_programs(size - 1)
                     
        for t1, programs1 in program_set.items():                
            # skip if t1 isn't a node accepted by Lt
            if t1 not in Argmax.accepted_rules(0):
                continue
             
            for p1 in programs1:                       
 
                am = Argmax()
                am.add_child(p1)
                new_programs.append(am)
         
                yield am
        return new_programs
 
class Sum(base_dsl.Sum):
    def __init__(self):
        super().__init__()
    
    @staticmethod
    def grow(plist, size):       
        new_programs = []
        # defines which nodes are accepted in the AST
        program_set = plist.get_programs(size - 1)
                     
        for t1, programs1 in program_set.items():                
            # skip if t1 isn't a node accepted by Lt
            if t1 not in Sum.accepted_rules(0):
                continue
             
            for p1 in programs1:                       
 
                sum_p = Sum()
                sum_p.add_child(p1)
                new_programs.append(sum_p)
         
                yield sum_p
        return new_programs
 
class Map(base_dsl.Map):
    def __init__(self):
        super().__init__()
    
    @staticmethod
    def grow(plist, size):  
        new_programs = []
                 
        # generates all combinations of cost of size 2 varying from 1 to size - 1
        combinations = list(itertools.product(range(0, size), repeat=2))
         
        for c in combinations:         
            # skip if the cost combination exceeds the limit
            if c[0] + c[1] + 1 != size:
                continue
                     
            # retrive bank of programs with costs c[0], c[1], and c[2]
            program_set1 = plist.get_programs(c[0])
            program_set2 = plist.get_programs(c[1])
             
#             if c[1] == 0:
#                 if VarList.className() not in program_set2:
#                     program_set2[VarList.className()] = []
#                 program_set2[VarList.className()].append(None)
                 
            for t1, programs1 in program_set1.items():                
                # skip if t1 isn't a node accepted by Lt
                if t1 not in Map.accepted_rules(0):
                    continue
                 
                for p1 in programs1:                       
     
                    for t2, programs2 in program_set2.items():
                                    
                        # skip if t2 isn't a node accepted by Map
                        if t2 not in Map.accepted_rules(1):
                            continue
                         
                        for p2 in programs2:
     
                            m = Map()
                            m.add_child(p1)
                            m.add_child(p2)
                            new_programs.append(m)
             
                            yield m
        return new_programs 

