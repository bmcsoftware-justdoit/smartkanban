/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.kanban.queue;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author gokumar
 */
public class KanbanQueue {
    public static final ConcurrentLinkedQueue CREATOR_QUEUE = new ConcurrentLinkedQueue();
    public static final ConcurrentLinkedQueue DECODER_QUEUE = new ConcurrentLinkedQueue();
}