from dsl import base_dsl
from dsl.base_dsl import Node, VarList

class InitialSymbol(base_dsl.Node):
    def __init__(self):
        super().__init__()
        
        self.number_children = 1
    
    def interpret(self, env):
        return self.children[0].interpret(env)
    
    def to_string(self):
        return self.children[0].to_string()
    
    @staticmethod
    def leftmost_hole(node, considers_hole_nodes=True):
        
        for i in range(node.number_children):
                        
            if node.current_child == i:
                return node, i
            
            if considers_hole_nodes and isinstance(node.children[i], HoleNode):               
                return node, i
            
            if isinstance(node.children[i], Node):
#                 print(node.children[i])
                incomplete_node, child_id = InitialSymbol.leftmost_hole(node.children[i], considers_hole_nodes)
            
                if incomplete_node is not None:
                    return incomplete_node, child_id
        return None, None
    
    @staticmethod
    def shallowest_hole(node, considers_hole_nodes=True):
        import queue
        
        open_list = queue.Queue()
        open_list.put(node)
        
        while not open_list.empty():
            current = open_list.get()
            
            for i in range(current.number_children):
                            
                if current.current_child == i:
                    return current, i
                
                if considers_hole_nodes and isinstance(current.children[i], HoleNode):               
                    return node, i
                
                if isinstance(current.children[i], Node):
                    open_list.put(current.children[i])
        return None, None
    
    @staticmethod
    def all_hole_nodes(node, hole_nodes_set, considers_hole_nodes=True):
        for i in range(node.number_children):
                        
            if node.current_child <= i:
                hole_nodes_set.append((node, i))
                continue
            
            if considers_hole_nodes and isinstance(node.children[i], HoleNode):
                hole_nodes_set.append((node, i))               
                continue
            
            if isinstance(node.children[i], Node):
                InitialSymbol.all_hole_nodes(node.children[i], hole_nodes_set)
    
class HoleNode(base_dsl.Node):
    def __init__(self):
        super().__init__()
        
        self.number_children = 0
        
    def interpret(self, env):
        raise Exception('Unimplemented method: interpret')
    
    def to_string(self):
        return "?"
        
InitialSymbol.accepted_types = [set([base_dsl.ITE.class_name(), base_dsl.Argmax.class_name(), base_dsl.Sum.class_name(), base_dsl.AssignActionToReturn.class_name()])]
