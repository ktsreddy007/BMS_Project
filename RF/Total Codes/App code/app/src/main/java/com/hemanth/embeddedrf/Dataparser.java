package com.hemanth.embeddedrf;

import java.util.ArrayList;
import java.util.List;

public class Dataparser {
   private List<String> list;
   private char[] array;
   public List<String> parseData(String data){
       list=new ArrayList<>();
       array=data.toCharArray();
       StringBuilder builder=new StringBuilder();
       if (array[0]=='@') {
           for (int i = 1; i <=array.length;i++){
               if (array[i]=='*'){
                   list.add(builder.toString());
                   builder=new StringBuilder();
               }else if (array[i]=='~'){
                   break;
               }else {
                   builder.append(array[i]);
               }
           }
       }
       return list;
   }

}
