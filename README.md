Team Aware UI extentions
========================

This plugin adds 3 new UI items

Team View 
---------

Simple list view where you configure a team name instead of a job list. All jobs in said team 
is included in the view

Team Aware Views TabBar 
------------------------
A ViewTab replacement that filters the visible tabs. 
  * If a view is a team view, display only if the user has access
  * If other view type display only if has jobs


Project And Team Aware Views TabBar
-----------------------------------

A ViewTab which puts two rows of tab. Upper row is a list of projects and lower row is a list of subteams within said project. It works by splitting the viewname into two parts based on the configured delimiter where the first part becomes the project and the second part the subteam. If a view name does not have the specified delimiter then they will be placed in a project called "Other"

The visibility of a tab is the same as for the "Team Aware Views TabBar"

This TabBar grew out of our usage where we have a team for each environment in each project, so with team views the number of tabs grew rapidly. Our teams are always named "(project)_(environment)" and people generally belong to a subset of the project hence no need to see the other projects






