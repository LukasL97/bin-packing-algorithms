# Bin Packing Algorithms

This app demonstrates some algorithms for the 2D Bin Packing Problem. The goal
is to place randomly generated rectangles into as few as possible square boxes
with a certain side length.

## Run the app

-   `bash run.sh`
-   Open `localhost:3000` in browser

_Requirements_:
-   Docker
-   sbt

## Algorithms

### Greedy

The Greedy approaches place rectangles one after another into the boxes given a
certain selection strategy (order in which rectangles are considered) and
placing strategy (how a rectangle is placed into a given partial solution).

#### Size-ordered Greedy

-   **Selection strategy**: Rectangles are considered in descending order according to
    their size (area).
-   **Placing strategy**: A top-left-first strategy is used. A rectangle is placed at
    the most top-left point of the first box it fits in.

#### Box Closing Greedy

The Box Closing variant of the Greedy algorithm differs from the previous
approach in that not all used boxes are considered for placing a new rectangle
in a step. Boxes can be closed, which results in no further attempt to place a
rectangle in a later step in this box.

-   **Selection strategy**: Rectangles are again considered in descending order
    according to their size. However, if in a step the thereby selected rectangle
    does not fit into the currently open box, other rectangles will be tried. Up to
    10 other rectangles are selected, distributed somewhat equally over their sizes.
    The first one that fits is selected for placing. If none fits the box is closed
    and the initially selected rectangle in this step is placed into a new empty box.
-   **Placing strategy**: Rectangles are placed into the open box if possible or
    otherwise into a new empty box (see selection strategy).

### Local Search

The Local Search approaches start with a full solution with all rectangles placed
and iteratively
change this solution to find better solutions according to an objective function.
The changes are defined implicitly as a neighborhood of a given solution. In
each step the Local Search algorithm searches the neighborhood of the current
solution for a solution that achieves a better score according to the objective
function. This solution is then used for the next step. The algorithm terminates
if no better solution is found in a step.

The standard objective function to be minimized
maps a solution to a sequence of real-valued scores,
one for each box. The box score is defined as roughly the fill grade of the box,
but skewed towards the top-left corner, i.e. the more bottom-right a rectangle
is, the higher the box score. The scores of two solutions are compared by first
considering the length of each sequence. A longer sequence indicates more boxes
used, i.e. a higher overall score. If the number of boxes used is identical,
the comparison goes iteratively through all box scores, starting from the last
box and compares the box scores until finding an inequality between the two box
score sequences in some box.

#### Rectangle Permutation Local Search

This variant is based on the Box Closing Greedy with regard to placing rectangles.
The neighborhood consists of changes in the permutation of rectangles.

-   **Start solution**: Rectangles are placed in the order they are created using
    the top-left-first strategy. If a rectangle does not fit into the currently
    open box, this box is closed immediately (without trying any other rectangle)
    and the rectangle is placed into a new empty box.
-   **Neighborhood**: The order of rectangles is changed, which leads to changes in the
    solution as rectangles are placed in the new order. The neighborhood consists
    of one new solution for each box that is not yet completely full. The new solution
    is created by pulling up rectangles from later boxes and place them directly after
    the rectangles of the considered box in the permutation. The placing algorithm
    then places these rectangles into the considered box. The pulled-up rectangles
    are chosen in such a way that they fit into the considered box and fill as much
    empty area of the box as possible.

#### Geometric Local Search

This variant moves rectangles from one box to another using again the top-left-first
placing approach.

-   **Start solution**: Each rectangle is placed in its own box, i.e. the start solution
    uses as many boxes as there are rectangles.
-   **Neighborhood**: Multiple neighborhoods are combined to form one large neighborhood
    of different changes in a solution.
    -   _Box Reordering Neighborhood_: Boxes are reordered in a descending way according
        to their fill grade.
    -   _Box Merge Neighborhood_: The last n boxes are considered so that the sum
        of the n fill grades is at most 80% of a single box' area. These boxes are
        merged by placing the rectangles from all n boxes in a single box according to
        the top-left-first placing approach. This is done for different n.
    -   _Box Pull-Up Neighborhood_: A rectangle in box i is pulled up into box
        i-1 and placed there according to the top-left-first placing approach.
        One solution is created for each rectangle. Additionally solutions are created
        in which multiple rectangles are pulled up this way, until a rectangle
        does not fit into the box in which they are pulled up.

#### Overlapping Local Search

This variant differs from Geometric Local Search in that overlaps between rectangles
are allowed for the early steps of the algorithm. The allowed overlap decreases
linearly with the number of steps, starting at 100% and reaching 0% at step 100.
The objective function penalizes exceeded overlaps. The algorithm does not stop if
overlaps are present in a solution, even if no better solution is found in a step.

-   **Start solution**: Rectangles are placed according to the top-left-first
    strategy, but without checking for conflicts between rectangles. This means that
    a rectangle can be placed at a free position in a box, even if it overlaps with
    other already placed rectangles in this box.
-   **Neighborhood**: The same neighborhoods as in Geometric Local Search are used,
    i.e. _Box Reordering Neighborhood_, _Box Merge Neighborhood_, and
    _Box Pull-Up Neighborhood_. However, the top-left-first placing approach takes
    into account the currently allowed overlap in this step when placing the
    rectangles. Additionally, an
    _Exceeded Overlap Outsourcing Neighborhood_ is used. This neighborhood
    generates one solution in each step in which the current allowed overlap is
    exceeded by some rectangle pairs in the current solution. These illegal
    overlaps are resolved by collecting rectangles in such a way that all overlapping
    rectangle pairs are covered (i.e. one rectangle from each pair is selected). These
    rectangles are then placed into a new empty box.
-   **Objective function**: The standard objective function for Local Search is changed
    by additionally considering the number of rectangle pairs that exceed
    the overlap allowed in this step. When comparing the objective scores of two
    solutions, these exceeded overlap counts are considered first. If identical,
    the comparison falls back to comparing the box score sequences as usual.
