/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bmc.justdoit.smartkanban.kanban.error;

/**
 *
 * @author gokumar
 */
public enum ErrorCode {
    COULD_NOT_DECODE_KANBAN,
    COULD_NOT_CREATE_KANBAN,
    INVALID_HEADERS,
    NO_QR_CODES_IDENTIFIED,
    NO_HEADERS_AVAILABLE,
    NO_WORKITEMS_FOUND,
    COULD_NOT_CREATE_QR_CODE
}
