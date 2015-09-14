/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.kanban.creator;

import com.bmc.justdoit.smartkanban.kanban.queue.KanbanCreatorQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 *
 * @author gokumar
 */
public class KanbanCreatorQueueProcessor extends HttpServlet implements Runnable {

    @Override
    public void init() throws ServletException {
        System.out.println("Initializing KanbanCreator Queue");
        new Thread(new KanbanCreatorQueueProcessor()).start();
    }

    public void run() {
        while (true) {
            try {
                synchronized (KanbanCreatorQueue.creatorQueue) {
//                    System.out.println("Synced......" + KanbanCreatorQueue.creatorQueue.size());
                    while (!KanbanCreatorQueue.creatorQueue.isEmpty()) {
                        String request = KanbanCreatorQueue.creatorQueue.poll().toString();
                        System.out.println("Got a request to create >>>>>>>> " + request);
                        KanbanCreator kanbanCreator = new KanbanCreator(request, null);
                        Thread th = new Thread(kanbanCreator);
                        th.start();
                    }
                }
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                Logger.getLogger(KanbanCreatorQueueProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
