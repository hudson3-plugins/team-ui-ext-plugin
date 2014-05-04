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
package org.eclipse.hudson.plugins.teamuiext.projectviewtabbar;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.View;
import hudson.views.ViewsTabBar;
import hudson.views.ViewsTabBarDescriptor;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import net.sf.json.JSONObject;
import org.eclipse.hudson.plugins.teamuiext.teamview.TeamView;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class ProjectTeamViewTabBar extends ViewsTabBar {

    
    @DataBoundConstructor
    public ProjectTeamViewTabBar() {
        
    }

    public Collection<ViewHolder> getProjects(List<View> views, View currentView) {
        DescriptorImpl descriptor = (DescriptorImpl) getDescriptor();
        SortedSet<ViewHolder> result = new TreeSet<ViewHolder>();

        String[] parentParts = currentView.getViewName().split(descriptor.getDelimiter(), 2);
        String parentName = parentParts.length > 1 ? parentParts[0] : "Other";

        for (View v : views) {
            if (!checkView(v, currentView)) {
                continue;
            }

            ViewHolder vh = new ViewHolder();

            String[] parts = v.getViewName().split(descriptor.getDelimiter(), 2);

            vh.name = parts.length > 1 ? parts[0] : "Other";
            vh.url = v.getUrl();
            vh.active = parentName.equals(vh.name);
            result.add(vh);
        }
        return result;
    }

    public Collection<ViewHolder> getTeams(List<View> views, View currentView) {

        DescriptorImpl descriptor = (DescriptorImpl) getDescriptor();
        String[] parentParts = currentView.getViewName().split(descriptor.getDelimiter(), 2);
        String parentName = parentParts.length > 1 ? parentParts[0] : "Other";

        SortedSet<ViewHolder> result = new TreeSet<ViewHolder>();

        for (View v : views) {
            if (!checkView(v, currentView)) {
                continue;
            }

            ViewHolder vh = new ViewHolder();
            String[] parts = v.getViewName().split(descriptor.getDelimiter(), 2);
            String project = parts.length > 1 ? parts[0] : "Other";
            vh.name = parts.length > 1 ? parts[1] : parts[0];
            vh.url = v.getUrl();
            vh.active = (v == currentView);

            if (project.equals(parentName)) {
                result.add(vh);
            }
        }

        return result;
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
        private String delimiter;
        
        @Override
        public String getDisplayName() {
            return "Project and Team Aware Views TabBar";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws Descriptor.FormException {
            delimiter = json.getString("delimiter");            
            save();
            return true;
        }

        public String getDelimiter() {
            return delimiter != null ?delimiter : "_";
        }
        
        
    }
    
    public static class ViewHolder implements Comparable<ViewHolder> {

        private String name;
        private String url;
        private boolean active;

        public int compareTo(ViewHolder o) {
            return name.compareTo(o.name);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 37 * hash + (this.name != null ? this.name.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ViewHolder other = (ViewHolder) obj;
            if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "ViewHolder{" + "name=" + name + ", url=" + url + ", active=" + active + '}';
        }

    }

}
