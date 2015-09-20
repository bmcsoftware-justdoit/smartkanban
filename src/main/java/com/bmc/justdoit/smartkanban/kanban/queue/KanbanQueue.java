/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.kanban.queue;

import com.bmc.justdoit.smartkanban.agiletools.SprintQuery;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author gokumar
 */
public class KanbanQueue {
    public static final ConcurrentLinkedQueue CREATOR_QUEUE = new ConcurrentLinkedQueue();
    public static final ConcurrentLinkedQueue DECODER_QUEUE = new ConcurrentLinkedQueue();
    
    public static final BlockingQueue<SprintQuery> STICKY_REQUEST_QUEUE = new LinkedBlockingQueue<SprintQuery>();
}
