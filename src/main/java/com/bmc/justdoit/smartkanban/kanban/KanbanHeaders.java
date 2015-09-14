/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.kanban;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gokumar
 */
public class KanbanHeaders {

    private static List<String> headers = new ArrayList<String>();

    static {
        KanbanHeaders.headers.add("StoryBacklog");
        KanbanHeaders.headers.add("TaskBacklog");
        KanbanHeaders.headers.add("DevInProgress");
        KanbanHeaders.headers.add("DevComplete");
        KanbanHeaders.headers.add("TestInProgress");
        KanbanHeaders.headers.add("TestComplete");
        KanbanHeaders.headers.add("Done");
        KanbanHeaders.headers.add("Accepted");
    }

    public static List<String> getHeaders() {
        return headers;
    }

    public static void setHeaders(List<String> headers) {
        KanbanHeaders.headers = headers;
    }

}
