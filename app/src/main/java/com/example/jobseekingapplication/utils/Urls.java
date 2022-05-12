package com.example.jobseekingapplication.utils;

public class Urls {
    public static String BASE_URL = "http://std.scit.co/std-job-seeker/public/api/";
//    public static String BASE_URL = "http://192.168.43.130/std-job-seeking/public/api/";
    public static  String FILE_BASE_URL = "http://std.scit.co/std-job-seeker/public/";
//    public static  String FILE_BASE_URL = "http://192.168.43.130/std-job-seeking/public/";

    public static String LOG_IN = BASE_URL + "login";

    //JOBSEEKER
    public static final String REGISTER_JOBSEEKER = BASE_URL + "register_job_seeker";
    public static final String UPDATE_JOBSEEKER = BASE_URL + "update_job_seeker";
    public static final String GET_SKILLS = BASE_URL+ "get_skills";
    public static final String GET_APPLIED_JOBS = BASE_URL+  "get_job_applications" ;

    public static String REGISTER_COMPANY = BASE_URL + "register_company";
    public static String GET_JOB_VACANCIES = BASE_URL + "get_job_opportunities";
    public static String APPLY_FOR_JOB_VACANCY = BASE_URL + "apply_job";

    //Company
    public static final String GET_COMPANY_JOBS = BASE_URL + "get_my_jobs";
    public static final String ADD_JOB = BASE_URL + "add_job";
    public static final String DELETE_JOB = BASE_URL + "delete_job";
    public static final String GET_JOB_REQUESTS = BASE_URL + "get_job_requests";
    public static final String UPDATE_COMPANY = BASE_URL + "update_company";
    public static final String RESET_PASSWORD_REQUEST = BASE_URL + "reset_password_request";
    public static final String SET_NEW_PASSWORD = BASE_URL + "set_new_password";
    public static final String CHANGE_REQUEST_STATUS = BASE_URL + "change_request_status";

}
