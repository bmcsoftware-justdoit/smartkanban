package com.bmc.justdoit.smartkanban.api;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import com.bmc.justdoit.smartkanban.agiletools.WorkItem;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * REST Web Service
 *
 * @author gokumar
 */
@Path("kanban/stickies/tasks/{requestId}")
public class KanbanTasksStickiesResource {

    @GET
    @Produces("application/json")
    public List<WorkItem> getWorkItem(@Context ServletContext context, @PathParam("requestId") String requestId) {

        try {
            String path = context.getRealPath("generator-requests");
            String requestPath = path + "/" + requestId;// + "/items.json";

            File[] files = new File(requestPath).listFiles();
            List<WorkItem> items = new ArrayList<WorkItem>();
            
            ObjectMapper mapper = new ObjectMapper();
//            WorkItem item = mapper.readValue(itemsFile, WorkItem.class);
//            System.out.println("Items: " + items.size());
            
            for (File file : files) {
                if (file.getName().contains(".json")) {
                    WorkItem item = mapper.readValue(file, WorkItem.class);
                    items.add(item);
                }
            }
            return items;
        } catch (IOException ex) {
            System.out.println("Unable to fetch list of work items.");
            ex.printStackTrace();
            return null;
        }
    }
}
