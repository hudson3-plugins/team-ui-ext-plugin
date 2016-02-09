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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import net.sf.json.JSONObject;
import org.eclipse.hudson.plugins.teamuiext.teamview.TeamView;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class ProjectTeamViewTabBar extends ViewsTabBar {

    
    @DataBoundConstructor
    public ProjectTeamViewTabBar() {
        
    }

    public List<SortedMap<String, ViewHolder> >  getTabs(List<View> views, View currentView) {
        DescriptorImpl descriptor = (DescriptorImpl) getDescriptor();
        String delimiter = descriptor.getDelimiter();
        
        if (views == null || currentView == null) {
            System.out.println("INPUT TO PLUGIN IS NULL"  + views + ", " + currentView);
        }             
        
        List<SortedMap<String, ViewHolder> > result = new ArrayList<SortedMap<String, ViewHolder>>();
        
        String[] currentViewnameParts = currentView.getViewName().split(delimiter);

        //System.out.println("---- START of PLUGIN ---");
        debugArray("Current view parts ", currentViewnameParts);
                
        for (View v : views) {
           if (!checkView(v, currentView)) {
                continue;
            }            
            String[] parts = v.getViewName().split(delimiter);

            debugArray("Evaluation view parts ", parts);
            
            
            for (int i=0; i<parts.length; i++) {                
                boolean isCurrent = v == currentView;
                
                //System.out.println("Part " + i +"=" +parts[i]+ "," + isLast + ", " + isCurrent);
                
              
                boolean hasPreviousLevelsMatched = isCurrent  || hasPreviousLevelsMatched(currentViewnameParts, parts, i);
                //System.out.println("hasPreviousLevelsMatched: " + isCurrent + ", " + hasPreviousLevelsMatched(currentViewnameParts, parts, i));
                if (hasPreviousLevelsMatched) {
                    SortedMap<String, ViewHolder> viewHolderCollection = getViewHolderCollection(result, i);
                    ViewHolder currentVh = viewHolderCollection.get(parts[i]);
                    if (currentVh == null) {
                        currentVh = new ViewHolder();
                        currentVh.name = parts[i];
                        currentVh.url = v.getAbsoluteUrl();
                        currentVh.active = false;
                        viewHolderCollection.put(currentVh.name, currentVh);                        
                    }
                    if (isCurrent) {
                        currentVh.active=true;
                    }
                }               
            }
           
        }
        debugResult(result);
        //System.out.println("---- END of PLUGIN --- " +result.size());
        return result;
    }
        
    private SortedMap<String, ViewHolder>  getViewHolderCollection(List<SortedMap<String, ViewHolder> > result, int index) {
        
        if (result.size() > index) {
            return result.get(index);
        }
        //System.out.println("Creating collection for index:" +index);
        SortedMap<String, ViewHolder> viewHolderCollection = new TreeMap<String, ViewHolder>();
        result.add(viewHolderCollection);
        return viewHolderCollection;        
    }
    private boolean hasPreviousLevelsMatched(String[] currentViewParts, String[] evaluationViewParts, int index) {
        if (index == 0) {
            return true;
        }
        for (int i=0; i<index; i++) {
            if (currentViewParts.length<=i || !currentViewParts[i].equals(evaluationViewParts[i])) {
                return false;
            }
        }
        return true;        
    }
        
   private void debugArray(String prefix, String[] arr) {
//       StringBuilder sb = new StringBuilder(prefix);
//       for (int i=0; i<arr.length; i++) {
//           sb.append(i).append(":").append(arr[i]).append(", ");
//       }
//       System.out.println(sb.toString());
   }
   
    private void debugResult(List<SortedMap<String, ViewHolder> > result) {
//        StringBuilder sb = new StringBuilder("Plugin result: ");
//        
//        for (int i=0; i<result.size(); i++) {
//            SortedMap<String, ViewHolder> row = result.get(i);
//            sb.append("\n row:").append(i).append(" [ ");
//            for (Map.Entry<String, ViewHolder> vh : row.entrySet()) {
//                sb.append(vh.getValue()).append(", ");
//            }
//            sb.append("]");
//        }
//        System.out.println(sb.toString());             
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
            vh.url = v.getAbsoluteUrl();
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
            try {
                return Hudson.getInstance().getTeamManager().isCurrentUserHasAccessToTeam(tv.getTeamName());
            } catch (NullPointerException npe) {
                System.out.println("View Config problem: Cannot find team for view: " +tv.getTeamName());
                return false;
            }
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
            return delimiter != null ? delimiter : "_";
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
