package com.bmc.justdoit.smartkanban.agiletools.jira;

import com.bmc.justdoit.smartkanban.agiletools.AgileTool;
import com.bmc.justdoit.smartkanban.agiletools.PhysicalKanbanStatus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.rcarz.jiraclient.BasicCredentials;
import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.Issue.SearchResult;
import net.rcarz.jiraclient.IssueType;
import net.rcarz.jiraclient.JiraClient;
import net.rcarz.jiraclient.JiraException;
import net.rcarz.jiraclient.greenhopper.GreenHopperClient;
import net.rcarz.jiraclient.greenhopper.RapidView;
import net.rcarz.jiraclient.greenhopper.Sprint;

import com.bmc.justdoit.smartkanban.agiletools.SprintInfo;
import com.bmc.justdoit.smartkanban.agiletools.SprintQuery;
import com.bmc.justdoit.smartkanban.agiletools.TeamInfo;
import com.bmc.justdoit.smartkanban.agiletools.WorkItem;
import com.bmc.justdoit.smartkanban.agiletools.WorkItemType;
import com.bmc.justdoit.smartkanban.api.objects.LoginRequest;
import com.bmc.justdoit.smartkanban.api.objects.LoginResponse;
import java.util.Collection;
import net.rcarz.jiraclient.Status;

public class JiraClientFacade extends AgileTool {

    private String jirServer = "http://jira-cor.bmc.com:80";

    public LoginResponse login(LoginRequest request) {
		// TODO Auto-generated method stub

        LoginResponse resp = new LoginResponse();
        resp.setErrorCode(0);
        Map<String, String> attrs = new HashMap<String, String>();
        attrs.put("userName", request.getLoginId());
        attrs.put("password", request.getPassword());
        resp.setAttributes(attrs);
        return resp;
    }

    public WorkItem getWorkItem(Map<String, String> authAttrs, String workItemId) {
        JiraClient client = getJiraClient(authAttrs);
        return getWorkItem(client, workItemId);
    }

    public List<WorkItem> getWorkItems(Map<String, String> authAttrs,
            SprintQuery query) {

        String jql
                = //Limestone-IC03 Hawk Eye
                "project = " + query.getProject() + " AND issuetype in (Story, Spike, \"Technical debt\", Bug, Improvement)  AND Sprint in (\"" + query.getSprint() + "\")"
                + " AND \"Scrum Team Name\" =  " + "\"" + query.getTeam() + "\"";

        System.out.println(jql);

        JiraClient client = getJiraClient(authAttrs);
        List<WorkItem> wItems = new ArrayList<WorkItem>();
        try {
            SearchResult res = client.searchIssues(jql);
            if (res.issues != null) {
                for (Issue issue : res.issues) {
                    WorkItem wItem = convertIssueToWorkItem(issue);
                    wItems.add(wItem);

                    List<WorkItem> subTasks = new ArrayList<WorkItem>();
                    List<Issue> subIssues = issue.getSubtasks();
                    if (subIssues != null && subIssues.size() > 0) {
                        int totalTimeEst = 0;
                        int totalTimeSpent = 0;
                        for (Issue subIssue : subIssues) {
                            subIssue.refresh();
                            subTasks.add(convertIssueToWorkItem(subIssue));
                            totalTimeEst += subIssue.getTimeEstimate();
                            totalTimeSpent += subIssue.getTimeSpent();
                        }
                        wItem.setEstimation(convertMinuteToHrOrDayString(totalTimeEst));
                        wItem.setRemaining(convertMinuteToHrOrDayString(totalTimeEst - totalTimeSpent));
                    } else {
                        System.out.println("Sub issues for issue " + wItem.getId() + ", " + subIssues);
                    }
                    
                    wItem.setSubTasks(subTasks);
                }
            }
        } catch (JiraException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return wItems;
    }

    public boolean updateWorkItem(Map<String, String> authAttrs, WorkItem item) {
        // TODO Auto-generated method stub
        return false;
    }

    public List<TeamInfo> getSprintTeams(Map<String, String> authAttrs) {
        GreenHopperClient client = getGreenHopperClient(authAttrs);
        List<TeamInfo> teams = new ArrayList<TeamInfo>();
        try {
            List<RapidView> views = client.getRapidViews();
            for (RapidView view : views) {
                try {
                    System.out.println("Fetching sprints of view =" + view.getId() + " - " + view.getName());
                    List<Sprint> sprints = view.getSprints();
                    List<SprintInfo> sprintInfos = new ArrayList<SprintInfo>();
                    for (Sprint sprint : sprints) {
                        sprintInfos.add(new SprintInfo(sprint.getId(), sprint.getName()));
                    }
                } catch (JiraException e) {
                    System.out.println(e.getMessage());
                }
                teams.add(new TeamInfo(view.getId(), view.getName(), null));

            }
        } catch (JiraException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return teams;
    }

    public Collection<WorkItem> updateWorkItems(Map<String, String> authAttrs, Collection<WorkItem> items) {
        if (items == null) {
            return null;
        }

        System.out.println("Receieved workitems to update JIRA. Count " + items.size());
        Map<String, Integer> kanbanStatusMap = new HashMap<String, Integer>();
        for (PhysicalKanbanStatus status : getSupportedPhysicalKanbanStatuses()) {
            kanbanStatusMap.put(status.getLabel(), status.getKey());
        }

        JiraClient client = getJiraClient(authAttrs);

        Collection<WorkItem> updatedItems = new ArrayList<WorkItem>();
        for (WorkItem item : items) {
            Integer itemKanbanStatus = kanbanStatusMap.get(item.getPhysicalKanbanStatus());
            System.out.println("Processing item " + item.getId());
            if (null == itemKanbanStatus) {
                System.out.println("Found unsupported kanban status " + item.getPhysicalKanbanStatus() + ". Skipping update");
                continue;
            }

            try {
                Issue issue = client.getIssue(item.getId());
                Status status = issue.getStatus();
                String jiraTransition = getNewJiraTransition(status.getName(), itemKanbanStatus);

                if (jiraTransition == null) {
                    System.out.println("Skipping update as no status change. Kanban Status = " + item.getPhysicalKanbanStatus() + ", Jira Status = " + status.getName());
                    continue;
                }

                System.out.println("Applying transition " + jiraTransition + ". Kanban Status = " + item.getPhysicalKanbanStatus() + ", Current Jira Status = " + status.getName());
                issue.transition().execute(jiraTransition);
            } catch (JiraException ex) {
                System.out.println("Failed to retrieve/update JIRA issue " + item.getId());
                ex.printStackTrace();
            }
        }

        return null;
    }

    private final static String STATUS_OPEN = "Open";
    private final static String STATUS_REOPENED = "Reopened";
    private final static String STATUS_INPROGRESS = "In Progress";
    private final static String STATUS_RESOLVED = "Resolved";
    private final static String STATUS_CLOSED = "Closed";

    private final static String TRANSITION_OPEN = "Open";
    private final static String TRANSITION_REOPEN = "Re-Open";
    private final static String TRANSITION_INPROGRESS = "In Progress";
    private final static String TRANSITION_RESOLVE = "Resolve";
    private final static String TRANSITION_CLOSE = "Close";

    private String getNewJiraTransition(String currentJiraStatus, int itemKanbanStatus) {
        switch (itemKanbanStatus) {
            case 0: {
                //Backlog
                if (currentJiraStatus == null) {
                    return TRANSITION_OPEN;
                }
                if (!currentJiraStatus.equals(STATUS_OPEN) && !currentJiraStatus.equals(STATUS_REOPENED)) {
                    return TRANSITION_REOPEN;
                }
                break;
            }
            case 1: {
                //Development in progress
                if (currentJiraStatus == null || !currentJiraStatus.equals(STATUS_INPROGRESS)) {
                    return TRANSITION_INPROGRESS;
                }
                break;
            }
            case 2: {
                //Development complete
                if (currentJiraStatus == null || !currentJiraStatus.equals(STATUS_RESOLVED)) {
                    return TRANSITION_RESOLVE;
                }
                break;
            }
            case 3: {
                //Testing in Progress
                if (currentJiraStatus == null || currentJiraStatus.equals(STATUS_OPEN) || currentJiraStatus.equals(STATUS_REOPENED)) {
                    return TRANSITION_INPROGRESS;
                }
                if (currentJiraStatus.equals(STATUS_INPROGRESS)) {
                    return TRANSITION_RESOLVE;
                }
                break;
            }
            case 4: {
                //Done
                if (currentJiraStatus == null || !currentJiraStatus.equals(STATUS_RESOLVED)) {
                    return TRANSITION_RESOLVE;
                }
                break;
            }
            case 5: {
                //Accepted
                if (currentJiraStatus == null || !currentJiraStatus.equals(STATUS_CLOSED)) {
                    return TRANSITION_CLOSE;
                }
                break;
            }
        }

        return null;
    }

    private JiraClient getJiraClient(Map<String, String> authAttrs) {
        String userName = authAttrs.get("userName");
        String password = authAttrs.get("password");
        BasicCredentials creds = new BasicCredentials(userName, password);
        JiraClient jira = new JiraClient(jirServer, creds);

	    //jira.getRestClient().getHttpClient().getConnectionManager().
        return jira;
    }

    private GreenHopperClient getGreenHopperClient(Map<String, String> authAttrs) {

        return new GreenHopperClient(getJiraClient(authAttrs));
    }

    private WorkItem convertIssueToWorkItem(Issue issue) {
        WorkItem wItem = new WorkItem();
        wItem.setId(issue.getKey());
        wItem.setTitle(issue.getSummary());
        wItem.setType(getMappedType(issue));
        wItem.setDescription(issue.getDescription());
        wItem.setStatus(issue.getStatus().getName());
        wItem.setEstimation(convertMinuteToHrOrDayString(issue.getTimeEstimate()));
        wItem.setRemaining(convertMinuteToHrOrDayString(issue.getTimeEstimate() - issue.getTimeSpent()));
        wItem.setAssignee((issue.getAssignee() == null) ? "unassigned" : issue.getAssignee().getDisplayName());
        return wItem;
    }
    
    private String  convertMinuteToHrOrDayString(int mins){
        if(mins <= 0 ) return "0H";
        int hrs = mins/3600;
        if(hrs >= 8){
            int days = hrs / 8;
            int balanceHrs = hrs % 8;
            return (days + "D" + ( (balanceHrs>0)?" " + balanceHrs+"H": "" ) );
        }else{
            return hrs + "H";
        }
    }

    private WorkItemType getMappedType(Issue issue) {
        IssueType type = issue.getIssueType();
        String typeName = type.getName();
        if (typeName == null || typeName.equalsIgnoreCase("Story")) {
            return WorkItemType.USER_STORY;
        }
        if (typeName.equalsIgnoreCase("Technical debt")) {
            return WorkItemType.TECHNICLE_DEBT;
        }
        if (typeName.equalsIgnoreCase("Spike")) {
            return WorkItemType.SPIKE;
        }
        if (typeName.equalsIgnoreCase("Bug")) {
            return WorkItemType.DEFECT;
        }
        if (typeName.equalsIgnoreCase("Improvement")) {
            return WorkItemType.ENHANCEMENT;
        }
        if (typeName.equalsIgnoreCase("Sub-task")) {
            return WorkItemType.SUB_TASK;
        }

        return WorkItemType.USER_STORY;
    }

    private WorkItem getWorkItem(JiraClient client, String workItemId) {
        WorkItem wItem = null;
        try {
            Issue issue = client.getIssue(workItemId);
            wItem = convertIssueToWorkItem(issue);
            System.out.println("Work item retrieved. " + wItem.getTitle());
        } catch (JiraException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return wItem;
    }

}
