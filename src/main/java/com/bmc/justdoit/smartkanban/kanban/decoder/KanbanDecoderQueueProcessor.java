/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.kanban.decoder;

import com.bmc.justdoit.smartkanban.kanban.queue.KanbanDecoderQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 *
 * @author gokumar
 */
public class KanbanDecoderQueueProcessor extends HttpServlet implements Runnable {

    @Override
    public void init() throws ServletException {
        System.out.println("Initializing KanbanDecoder Queue");
        new Thread(new KanbanDecoderQueueProcessor()).start();
    }

    public void run() {
        while (true) {
            try {
                synchronized (KanbanDecoderQueue.decoderQueue) {
//                    System.out.println("Synced......" + KanbanDecoderQueue.decoderQueue.size());
                    while (!KanbanDecoderQueue.decoderQueue.isEmpty()) {
                        String request = KanbanDecoderQueue.decoderQueue.poll().toString();
                        System.out.println("Got a request to decode >>>>>>>> " + request);
                        KanbanDecoder kanbanDecoder = new KanbanDecoder(request, request);
                        Thread th = new Thread(kanbanDecoder);
                        th.start();
                    }
                }
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                Logger.getLogger(KanbanDecoderQueueProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
