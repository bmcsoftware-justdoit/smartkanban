package com.bmc.justdoit.smartkanban.api;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import com.bmc.justdoit.smartkanban.agiletools.AgileToolFactory;
import com.bmc.justdoit.smartkanban.agiletools.AgileToolIntf;
import com.bmc.justdoit.smartkanban.agiletools.WorkItem;
import com.bmc.justdoit.smartkanban.agiletools.WorkItemType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * REST Web Service
 *
 * @author smijithkp
 */
@Path("kanban/stickies/{requestId}")
public class KanbanStickies {

    @Context
    private UriInfo context;

    public KanbanStickies() {

    }

    @GET
    @Produces("application/json")
    public List<WorkItem> getWorkItem(@Context ServletContext context, @PathParam("requestId") String requestId) {

        try {
            String path = context.getRealPath("generator-requests");
            String requestPath = path + "/" + requestId;

            File requestDir = new File(requestPath);
            File[] files = requestDir.listFiles();
            System.out.println(">>>>>>>>> " + requestPath);
            System.out.println(">>>>>>>>> " + files.length);
            ObjectMapper mapper = new ObjectMapper();
            List<WorkItem> items = new ArrayList<WorkItem>();
            
            for (File file : files) {
                if (file.getName().contains(".json")) {
                    WorkItem item = mapper.readValue(file, WorkItem.class);
                    items.add(item);
                }
            }
            return items;
        } catch (IOException ex) {
            System.out.println("Unable to fetch list of work items.");
            return null;
        }
    }
}
