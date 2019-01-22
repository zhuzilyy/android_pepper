package com.hzjz.pepper.config;

public class ApiConfig {
    //    private static String ServerUrl = "http://192.168.51.93/";
//    private static String ServerUrl = "http://192.168.51.101:8099/";
//    private static String ServerUrl = "http://192.168.51.189:8099/";
//    private static String ServerUrl = "http://jy00712345.55555.io:8099/";
      public static String ServerUrl = "https://www0.pepperpd.com/";
      //public static String ServerUrl = "http://192.168.0.112:8000/";
    /**
     * pmtest01@edu.com 超级用户
     * pmtest02@edu.com 普通用户
     * 12345abcdE~!
     * @return
     */
    public static String getLogin() {
        return ServerUrl + "mobile/login";
    }
    public static String getMainList() {
        return ServerUrl + "mobile/pd_list";
    }

    public static String delList() {
        return ServerUrl + "mobile/pd_delete";
    }
    /**
     * 根据TrainingID获取StudentList
     * @return
     */
    public static String getStudentList() {
        return ServerUrl + "mobile/student_list";
    }
    /**
     * 更新training 中 PepregStudent
     * @return
     */
    public static String updatePepregStudent() {
        return ServerUrl + "mobile/add_student";
    }
    /**
     * 删除注册的学生
     * @return
     */
    public static String deletePepregStudent() {
        return ServerUrl + "mobile/delete_student";
    }
    /**
     * @return
     */
    public static String attendancePepregStudent() {
        return ServerUrl + "mobile/attend_student";
    }
    /**
     * viladate
     * @return
     */
    public static String validatePepregStudent() {
        return ServerUrl + "mobile/validate_student";
    }

    /**
     * 根据TrainingID批量修改Students状态
     * @return
     */
    public static String updateStudentStatusByTrainingId() {
        return ServerUrl + "pepregStudent/updateStudentStatusByTrainingId";
    }

    /**
     * 用户获取州列表
     * @return
     */
    public static String getSearchState() {
        return ServerUrl + "mobile/state_list";
    }
    /**
     * 获取District列表
     * 根据StateID获取District列表
     * @return
     */
    public static String getSearchDist() {
        return ServerUrl + "mobile/district_list";
    }
    /**
     * 获取Subject列表
     * @return
     */
    public static String getSearchSubject() {
        return ServerUrl + "mobile/subject_list";
    }

    /**
     * 获取Pepper_Courses列表
     * @return
     */
    public static String getSearchCourses() {
        return ServerUrl + "mobile/course_list";
    }

    /**
     * 获取PepregTraining详情
     * @return
     */
    public static String getTrainingInfoById() {
        return ServerUrl + "mobile/pd_get";
    }

    /**
     * 保存PepregTraining详情
     * @return
     */
    public static String saveTrainingInfoById() {
        return ServerUrl + "mobile/pd_save";
    }

    /**
     * 获取School列表
     * @return
     */
    public static String getSchoolList() {
        return ServerUrl + "mobile/school_list";
    }

    /**
     * 根据email查询用户
     * @return
     */
    public static String searchEmail() {
        //return ServerUrl + "authUser/findUserByEmail";
        return ServerUrl +"mobile/email_list";
    }

    /**
     * 根据trainingId查询Instructor列表
     * @return
     */
    public static String getInstructor() {
        return ServerUrl + "mobile/pd_get";
    }

    /**
     * 根据输入年月获取本月每日训练总数
     * @return
     */
    public static String getMonthSine() {
        return ServerUrl + "mobile/get_calendar";
    }

    /**
     * 根据输入日期查询‘我’相关的training列表
     * @return
     */
    public static String getTrainingListForMByData() {
        return ServerUrl + "myCalendar/getTrainingListForMByData";
    }

    /**
     * 根据输入月份查询‘我’相关的training列表
     * @return
     */
    public static String getTrainingListForMByMonth() {
        return ServerUrl + "myCalendar/getTrainingListForMByMonth";
    }
}
