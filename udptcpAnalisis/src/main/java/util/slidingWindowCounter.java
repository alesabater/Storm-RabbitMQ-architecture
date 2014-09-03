/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.io.Serializable;
import java.util.Map;

/**
 *
 * @author asabater
 */
public class slidingWindowCounter implements Serializable{
    
    private static final long serialVersionUID = -2645063988768785810L;
    private slotBaseCounter objCounter;
    private int headSlot;
    private int tailSlot;
    private int windowLengthInSlots;
    
    public slidingWindowCounter(int windowLengthInSlots) {
        /*if (windowLengthInSlots < 2) {
          throw new IllegalArgumentException(
              "Window length in slots must be at least two (you requested " + windowLengthInSlots + ")");
        } */
        this.windowLengthInSlots = windowLengthInSlots;
        this.objCounter = new slotBaseCounter(this.windowLengthInSlots);
        this.headSlot = 0;
        this.tailSlot = slotAfter(headSlot);
    }
    
    public void incrementCount(String idEquipo) {
    objCounter.incrementCount(idEquipo, headSlot);
  }
    
    public Map<String, Long> getCountsThenAdvanceWindow() {
    Map<String, Long> counts = objCounter.getCounts(headSlot); //Posicion de Long[0]->media Long[1]->ultimoSlot
    objCounter.wipeZeros();
    objCounter.wipeSlot(tailSlot);
    advanceHead();
    return counts;
  }
    
    private void advanceHead() {
    headSlot = tailSlot;
    tailSlot = slotAfter(tailSlot);
  }
    
    private int slotAfter(int slot) {
    return (slot + 1) % windowLengthInSlots;
  }
    
}
