package com.setting.dl.google.googlesettingupdate;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class Set<T> extends HashSet<T>{
   
   @SafeVarargs
   public Set(T... elems){
      
      addAll(Arrays.asList(elems));
   }
   
   public Set(Collection<T> collection){
      
      super(collection);
   }
   
   //A fark B
   public Set<T> difference(Set<T> b){
      
      Set<T> difSet = new Set<>();
      
      for (T t : this) if(!b.contains(t)) difSet.add(t);
      
      return difSet;
   }
   
   public Set<T> intersection(Set<T> b) {
      
      Set<T> intSet = new Set<>();
      Set<T> tempSet = new Set<>();
      
      for (T t : this) if(b.contains(t)) tempSet.add(t);
      
      for (T t : b) if(tempSet.contains(t)) intSet.add(t);
      
      return intSet;
   }
   
   public Set<T> union(Set<T> b){
      
      Set<T> unionSet = new Set<>();
      
      unionSet.addAll(this);
      unionSet.addAll(b);
      
      return unionSet;
   }
   
   public boolean isDisjoint(Set<T> b) {
      
      return intersection(b).size() == 0;
   }
   
   public boolean isSubset(Set<T> b) {
      
      for(T t : this) if(!b.contains(t)) return false;
      
      return true;
   }
   
   public boolean isSuperset(Set<T> b) {
      
      for(T t : b) if(!this.contains(t)) return false;
      
      return true;
   }
}
