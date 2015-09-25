/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.kanban.queue;

import com.bmc.justdoit.smartkanban.agiletools.SprintQuery;
import com.bmc.justdoit.smartkanban.api.objects.KanbanDecoderRequest;
import com.bmc.justdoit.smartkanban.api.objects.KanbanGeneratorRequest;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author gokumar
 */
public class KanbanQueue {
    public static final BlockingQueue<KanbanGeneratorRequest> CREATOR_QUEUE = new LinkedBlockingQueue<KanbanGeneratorRequest>();
    public static final BlockingQueue<KanbanDecoderRequest> DECODER_QUEUE = new LinkedBlockingDeque<KanbanDecoderRequest>();
    
    public static final BlockingQueue<SprintQuery> STICKY_REQUEST_QUEUE = new LinkedBlockingQueue<SprintQuery>();
}
