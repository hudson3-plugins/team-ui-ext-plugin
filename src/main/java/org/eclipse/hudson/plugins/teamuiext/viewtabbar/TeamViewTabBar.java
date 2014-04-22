/*
 * Copyright (c) 2014 Henrik Lynggaard Hansen
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Henrik Lynggaard Hansen - initial code
 */

package org.eclipse.hudson.plugins.teamuiext.viewtabbar;

import hudson.Extension;
import hudson.model.Hudson;
import hudson.model.View;
import hudson.views.ViewsTabBar;
import hudson.views.ViewsTabBarDescriptor;
import org.eclipse.hudson.plugins.teamuiext.teamview.TeamView;
import org.kohsuke.stapler.DataBoundConstructor;

public class TeamViewTabBar extends ViewsTabBar{

    
    @DataBoundConstructor
    public TeamViewTabBar() {
    }
    
    public boolean checkView(View view, View currentView) {
        if (view == currentView) {
            return true;
        }
        if (view instanceof TeamView) {
            TeamView tv = (TeamView) view;
            return Hudson.getInstance().getTeamManager().isCurrentUserHasAccessToTeam(tv.getTeamName());            
        }
        
        return view.getItems().size() > 0;
    }
    
    @Extension
    public static class DescriptorImpl extends ViewsTabBarDescriptor {

        @Override
        public String getDisplayName() {
            return "Team Aware Views TabBar";
        }
    }    
}
