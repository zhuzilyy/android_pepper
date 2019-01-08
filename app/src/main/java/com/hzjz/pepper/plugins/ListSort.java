package com.hzjz.pepper.plugins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListSort {
    public static List sortList(HashMap<Integer, String> map) {
        List<HashMap.Entry<Integer, String>> list = new ArrayList<HashMap.Entry<Integer, String>>(map.entrySet());
        Collections.sort(list, new Comparator<HashMap.Entry<Integer, String>>() {
            @Override
            public int compare(Map.Entry<Integer, String> o1, Map.Entry<Integer, String> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        return list;
    }

    public String judgeStatus(String trainingDate, String allowRegistration, String studentStatus, String allowStudentAttendance) {
        String judgeresult = "";
        if (DateUtil.compare_date(trainingDate)) {
            if (allowRegistration.equals("true")) {
                if (studentStatus.equals("")) {
                    judgeresult = "register";
                } else if (studentStatus.equals("Registered")) {
                    judgeresult = "registered";
                }
            } else {
                judgeresult = "cantregister";
            }
        } else {
            if (allowStudentAttendance.equals("true")) {
                judgeresult = "attend";
            } else {
                judgeresult = "attended";
            }
        }
        return "";
    }
}
