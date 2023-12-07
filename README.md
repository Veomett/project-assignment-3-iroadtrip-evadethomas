# CS 245 (Fall 2023) - Assignment 3 - IRoadTrip

Can you plan the path for a road trip from one country to another?

**PLEASE NOTE (for grader):**

It said something about "you are committing "CRLF" files. I don't know what that means but I think I fixed it.
I figured I'd give a heads-up in case it breaks something. I also am running out of time for comments
and styling, but everything else is working, so I'm going to add more past the deadline with lots of commits.

**READ-ME:**

This project takes in two countries from the given state_name, borders and cap-dist files, and puts all of their data
into many different hashmaps to ultimately create a map of edges. The user types in a country name, if accepted, it's
accessed from a hashmap of all possible country names (including aliases), which finds the 3-letter country code.
This code is used to access the main-edge map, created from the border file. The findPath function runs through these
edges, and gets the shortest path from the first country to another.

There is a function that handles edge cases, misalligned names and codes thoughout the files etc...


**References:**
Please note to save time, as I kept noticing the amount of times I had to iterate through a specific piece of a hashmap,
I decided it would be more efficent to pull code from chat GPT to do this in a few places. I am including this as my
reference. I also used the slides from class, geeksforgeeks and an explination from chat GPT to help me build dijerka's
algorithm for this project.