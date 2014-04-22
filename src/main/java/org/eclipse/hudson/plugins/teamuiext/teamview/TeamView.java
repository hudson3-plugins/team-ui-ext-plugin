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

package org.eclipse.hudson.plugins.teamuiext.teamview;

import hudson.Extension;
import hudson.model.Hudson;
import hudson.model.ListView;
import hudson.model.TopLevelItem;
import hudson.model.ViewDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import org.eclipse.hudson.security.team.Team;
import org.eclipse.hudson.security.team.TeamManager;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import hudson.Util;

public class TeamView extends ListView {

    private String teamName;

    @DataBoundConstructor
    public TeamView(String name) {
        super(name);
    }

    public String getTeamName() {
        return this.teamName;
    }

    @Override
    public synchronized List<TopLevelItem> getItems() {
        TeamManager teamManager = Hudson.getInstance().getTeamManager();
        Team team = teamManager.getTeams().get(this.teamName);

        if (teamName == null || team == null || team.getJobNames() == null) {
            return new ArrayList<TopLevelItem>();
        }

        List<TopLevelItem> result = new ArrayList<TopLevelItem>(team.getJobNames().size());
        for (String jobName : team.getJobNames()) {
            TopLevelItem item = Hudson.getInstance().getItem(jobName);
            result.add(item);
        }
        return result;
    }

    @Override
    protected void submit(StaplerRequest req) throws ServletException, Descriptor.FormException, IOException {
        teamName = req.getParameter("teamview.teamname");
        super.submit(req);
    }

    @Extension
    public static final class TeamViewDescriptor extends ViewDescriptor {

        @Override
        public String getDisplayName() {
            return "Team View";
        }

        public FormValidation doCheckUrl(@QueryParameter final String value)
                throws IOException, ServletException {
            return new FormValidation.URLCheck() {
                @Override
                protected FormValidation check() throws IOException,
                        ServletException {
                    String msgServerUrl = Util.fixEmpty(value);
                    if (msgServerUrl == null) { // nothing entered yet
                        return FormValidation.error("Team name cannot be empty");
                    }
                    if (Hudson.getInstance().getTeamManager().getTeams().containsKey(value)) {
                        return FormValidation.ok();
                    } else {
                        return FormValidation.error("Team not found");
                    }
                }
            }.check();
        }

    }
}
