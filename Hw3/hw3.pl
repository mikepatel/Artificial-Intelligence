% Michael Patel
% CSC 520
% Hw 3
% Q3

% write('\33\[2J').

%#############################################################
% Taxonomy
% edge(SourceNode, RelationshipType, DestinationNode)

% toplevel rules
% relations
edge(humans, ako, creatures).
edge(birds, ako, creatures).
edge(man, ako, humans).
edge(turkey, ako, birds).
edge(louis, isa, man).
edge(albert, isa, man).
edge(frank, isa, turkey).

%#############################################################
% properties
property(humans, legs, two). % default
property(louis, legs, one).
property(birds, fly, yes). % default
property(turkey, fly, no).

%#############################################################
% any-depth hierarchy
% RELATIONS
% Base base
rel(SourceNode, RelationshipType, DestinationNode) :- edge(SourceNode, RelationshipType, DestinationNode).

% Recursive cases
rel(SourceNode, RelationshipType, DestinationNode) :- 
														edge(SourceNode, RelationshipType, IntermediateNode),
														rel(IntermediateNode, ako, DestinationNode).

%#############################################################
% PROPERTIES
% Base case
hasProp(SourceNode, Property, Value) :- property(SourceNode, Property, Value).

% Recursive cases
hasProp(Child, Property, Value) :- 
											edge(Child, isa, Parent),
											hasProp(Parent, Property, Value),
											\+ property(Child, Property, _). % don't override with parent property (default)

hasProp(Child, Property, Value) :- 
											edge(Child, ako, Parent),
											hasProp(Parent, Property, Value),
											\+ property(Child, Property, _). % don't override with parent property (default)