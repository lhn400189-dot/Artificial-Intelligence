Assignment 1 – Environment Simulator (Model-based Agent)
Course: COMP 3711
Name: Haonan Li
Student ID: T00765761

This program simulates a vacuum cleaner agent in a 2x2 environment using a model-based approach.
It reads 5 command-line arguments and determines the next move.

Arguments:
1. Current Location (A/B/C/D)
2. Status of Square A (true = clean, false = dirty)
3. Status of Square B
4. Status of Square C
5. Status of Square D

Tested scenarios include:
- All squares clean
- Dirty current square
- Horizontal vs vertical preference
- No diagonal movement
- Transition through intermediate squares