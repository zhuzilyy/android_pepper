package com.hzjz.pepper.config;

public class ApiConfig {
    //    private static String ServerUrl = "http://192.168.51.93/";
//    private static String ServerUrl = "http://192.168.51.101:8099/";
//    private static String ServerUrl = "http://192.168.51.189:8099/";
//    private static String ServerUrl = "http://jy00712345.55555.io:8099/";
   // private static String ServerUrl = "http://www.lnhzjz.xyz/";
      public static String ServerUrl = "http://192.168.0.112:8000/";
    /**
     * pmtest01@edu.com 超级用户
     * pmtest02@edu.com 普通用户
     * 12345abcdE~!
     *
     * @return
     */
    public static String getLogin() {
        return ServerUrl + "authUser/login";
    }
    public static String getMainList() {
        return ServerUrl + "ppTraining/listForSearch";
    }

    public static String delList() {
        return ServerUrl + "ppTraining/delTrainingByIdList";
    }

    /**
     * 根据TrainingID获取StudentList
     * @param stateId
     * @return
     */
    public static String getStudentList() {
        return ServerUrl + "pepregStudent/getPepregStudentListByTrainingId";
    }
    /**
     * 更新training 中 PepregStudent
     * @param stateId
     * @return
     */
    public static String updatePepregStudent() {
        return ServerUrl + "pepregStudent/updatePepregStudent";
    }

    /**
     * 根据TrainingID批量修改Students状态
     * @param stateId
     * @return
     */
    public static String updateStudentStatusByTrainingId() {
        return ServerUrl + "pepregStudent/updateStudentStatusByTrainingId";
    }

    /**
     * 用户获取州列表
     * @param stateId
     * @return
     */
    public static String getSearchState() {
        return ServerUrl + "stateC/list";
    }

    /**
     * 获取District列表
     * 根据StateID获取District列表
     * @param stateId
     * @return
     */
    public static String getSearchDist() {
        return ServerUrl + "districtC/listByStateId";
    }

    /**
     * 获取Subject列表
     * @return
     */
    public static String getSearchSubject() {
        return ServerUrl + "SubjectC/list";
    }

    /**
     * 获取Pepper_Courses列表
     * @return
     */
    public static String getSearchCourses() {
        return ServerUrl + "modulestore/getPepperCoursesList";
    }

    /**
     * 获取PepregTraining详情
     * @return
     */
    public static String getTrainingInfoById() {
        return ServerUrl + "ppTraining/getTrainingInfoById";
    }

    /**
     * 保存PepregTraining详情
     * @return
     */
    public static String saveTrainingInfoById() {
        return ServerUrl + "ppTraining/saveTrainingByPOJO";
    }

    /**
     * 获取School列表
     * @return
     */
    public static String getSchoolList() {
        return ServerUrl + "schoolC/listByStateId";
    }

    /**
     * 根据email查询用户
     * @return
     */
    public static String searchEmail() {
        return ServerUrl + "authUser/findUserByEmail";
    }

    /**
     * 根据trainingId查询Instructor列表
     * @return
     */
    public static String getInstructor() {
        return ServerUrl + "pepregInstructor/findPepregInstructorListByTrainingId";
    }

    /**
     * 根据输入年月获取本月每日训练总数
     * @return
     */
    public static String getMonthSine() {
        return ServerUrl + "myCalendar/getMonthSine";
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
