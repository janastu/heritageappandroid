package org.janastu.heritageapp.geoheritagev2.client.pojo;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by DESKTOP on 3/12/2016.
 */
public class CheckedHeritageCategoryMap {


    static Map<String, Boolean> mapHeritageSelected = new TreeMap<String, Boolean>();

    public static void addToMap(String value , Boolean r)
    {
        //value.trim();
        mapHeritageSelected.put(value, r);
    }

    public static List<String> getFilteredCategories()
    {
        ArrayList<String> aList = new ArrayList();
        Iterator entries = mapHeritageSelected.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry thisEntry = (Map.Entry) entries.next();
            String key = (String)thisEntry.getKey();
            Boolean value = (Boolean)thisEntry.getValue();
            if(value == true)
            {
                aList.add(key);
            }

        }
        return aList;
    }


}
