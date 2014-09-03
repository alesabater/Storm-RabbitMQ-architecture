/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author asabater
 */
public class slotBaseCounter implements Serializable{
    
    private static final long serialVersionUID = 5487841531854564154L;
    private Map<String, long[]> objToCounts = new HashMap<String, long[]>();
    private int numSlots;
    
    public slotBaseCounter(int numSlots) {
        if (numSlots <= 0) {
          throw new IllegalArgumentException("Number of slots must be greater than zero (you requested " + numSlots + ")");
        }
        this.numSlots = numSlots;
    }
    
    public void incrementCount(String idEquipo, int slot) {
        long[] counts = objToCounts.get(idEquipo);
        if (counts == null) {
          counts = new long[this.numSlots];
          objToCounts.put(idEquipo, counts);
        }
        //System.out.println("PROTOCOLO --> "+idEquipo+" CUENTA:"+computeTotalCount(idEquipo));
        counts[slot]++;
    }
    
    public long getCount(String idEquipo, int slot) {
        long[] counts = objToCounts.get(idEquipo);
        if (counts == null) {
          return 0;
        }
        else {
          return counts[slot];
        }
    }
    
    //Regresa la suma de todos los slots por objeto
    public Map<String, Long> getCounts(int headSlot) {
        //Map<String, Long> result = new HashMap<String, Long>();
        Map<String, Long> result = new HashMap<String, Long>();
        //Long cuentaSlot = new Long();
        for (String obj : objToCounts.keySet()) {
          long cuentaSlot = computeTotalCount(obj);
          result.put(obj, cuentaSlot);
          //System.out.println("CUENTA FINAAAL --> "+obj+" CUENTA:"+cuentaSlot);
        }
        return result;
    }
    
    //Regresa cuanta total para una fila (Para un objeto)
    private long computeTotalCount(String idEquipo) {
        long[] curr = objToCounts.get(idEquipo);
        long total = 0;
        for (long l : curr) {
          total += l;
        }
        return total;
    }
    
     /**
   * Resetea a 0 el mismo slot para todos los equipos
   *
   * @param slot
   */
    public void wipeSlot(int slot) {
      for (String obj : objToCounts.keySet()) {
        resetSlotCountToZero(obj, slot);
      }
    }
    
    private void resetSlotCountToZero(String idEquipo, int slot) {
      long[] counts = objToCounts.get(idEquipo);
      counts[slot] = 0;
    }
    
    private boolean shouldBeRemovedFromCounter(String idEquipo) {
      return computeTotalCount(idEquipo) == 0;
    }
    
    /**
   * Elimina equipos cuya cuenta total sea igual a 0 (Para liberar memoria).
   */
    public void wipeZeros() {
      Set<String> objToBeRemoved = new HashSet<String>();
      for (String obj : objToCounts.keySet()) {
        if (shouldBeRemovedFromCounter(obj)) {
          objToBeRemoved.add(obj);
        }
      }
      for (String obj : objToBeRemoved) {
        objToCounts.remove(obj);
      }
    }
}
