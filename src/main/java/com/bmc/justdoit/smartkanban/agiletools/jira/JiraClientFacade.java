package com.bmc.justdoit.smartkanban.agiletools.jira;

import com.bmc.justdoit.smartkanban.agiletools.AgileTool;
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

import com.bmc.justdoit.smartkanban.agiletools.AgileToolIntf;
import com.bmc.justdoit.smartkanban.agiletools.SprintInfo;
import com.bmc.justdoit.smartkanban.agiletools.SprintQuery;
import com.bmc.justdoit.smartkanban.agiletools.TeamInfo;
import com.bmc.justdoit.smartkanban.agiletools.WorkItem;
import com.bmc.justdoit.smartkanban.agiletools.WorkItemType;
import com.bmc.justdoit.smartkanban.api.objects.LoginRequest;
import com.bmc.justdoit.smartkanban.api.objects.LoginResponse;
import java.util.Collection;
import java.util.Set;

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
		
		WorkItem wItem = null;
		JiraClient client =  getJiraClient(authAttrs);
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

	public List<WorkItem> getWorkItems(Map<String, String> authAttrs,
			SprintQuery query) {
		
		String jql = 
				//Limestone-IC03 Hawk Eye
				"project = TOM AND issuetype in (Story, Spike, \"Technical debt\", Bug, Improvement)  AND Sprint in (" + query.getSprint() + ")" +
						" AND \"Scrum Team Name\" =  " + "\"" + query.getTeam() + "\"";
		
		System.out.println(jql);
		
		JiraClient client = getJiraClient(authAttrs);
		List<WorkItem> wItems = new ArrayList<WorkItem>();
		try {
			SearchResult res = client.searchIssues(jql);
			if(res.issues != null){
				for(Issue issue :  res.issues){
					WorkItem wItem = convertIssueToWorkItem(issue);
					wItems.add(wItem);
					
					List<WorkItem> subTasks = new ArrayList<WorkItem>();
					List<Issue> subIssues = issue.getSubtasks();
					if(subIssues != null && subIssues.size() > 0){
						for(Issue subIssue : subIssues){
							subTasks.add(convertIssueToWorkItem(subIssue));
						}
					}else{
						System.out.println("Sub issues for issue " + wItem.getId() + ", " + subIssues );
					}
				
					wItem.setSubTaks(subTasks);
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
		List<TeamInfo> teams  = new ArrayList<TeamInfo>();
		try {
			List<RapidView> views = client.getRapidViews();
			for (RapidView view : views) {
				try{
					System.out.println("Fetching sprints of view =" + view.getId() +" - " +  view.getName());
					List<Sprint> sprints = view.getSprints();
					List<SprintInfo> sprintInfos = new ArrayList<SprintInfo>();
					for (Sprint sprint : sprints) {
						sprintInfos.add(new SprintInfo(sprint.getId(), sprint.getName()));
					}
				}catch(JiraException e){
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
        
        private JiraClient getJiraClient(Map<String, String> authAttrs){
		String userName = authAttrs.get("userName");
		String password = authAttrs.get("password");
		BasicCredentials creds = new BasicCredentials(userName, password);
	    JiraClient jira = new JiraClient(jirServer, creds);
	    
	    //jira.getRestClient().getHttpClient().getConnectionManager().
	    
	    return jira;
	}
	
	
	private GreenHopperClient getGreenHopperClient(Map<String, String> authAttrs){
		
		return new GreenHopperClient(getJiraClient(authAttrs));
	}
        
        private WorkItem convertIssueToWorkItem(Issue issue) {
		WorkItem wItem = new WorkItem();
		wItem.setId(issue.getKey());
		wItem.setTitle(issue.getSummary() );
		wItem.setType(getMappedType(issue));
		wItem.setDescription(issue.getDescription());
		wItem.setStatus(issue.getStatus().getName());
		wItem.setEstimation("" + issue.getTimeEstimate());
		wItem.setRemaining("" + (issue.getTimeEstimate() - issue.getTimeSpent() ));
		
		return wItem;
	}
	
	private WorkItemType getMappedType(Issue issue){
		IssueType type = issue.getIssueType();
		String typeName = type.getName();
		if(typeName == null || typeName.equalsIgnoreCase("Story")) return  WorkItemType.USER_STORY; 
		if(typeName.equalsIgnoreCase("Technical debt")) return WorkItemType.TECHNICLE_DEBT;
		if(typeName.equalsIgnoreCase("Spike")) return WorkItemType.SPIKE;
		if(typeName.equalsIgnoreCase("Bug")) return WorkItemType.DEFECT;
		if(typeName.equalsIgnoreCase("Improvement")) return WorkItemType.ENHANCEMENT;
		if(typeName.equalsIgnoreCase("Sub-task")) return WorkItemType.SUB_TASK;
		
		return WorkItemType.USER_STORY;
	}

    
    public boolean updateWorkItems(Map<String, String> authAttrs, Collection<WorkItem> items) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
