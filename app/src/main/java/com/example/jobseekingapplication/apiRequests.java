package com.example.jobseekingapplication;


public interface apiRequests {
//There app has an admin panel please let me know when you start working on it /
    /**
     * ====User types====:
     *   int USER_TYPE_JOB_SEEKER = 0;
     *   int USER_TYPE_COMPANY = 1;
     */
    /**
     * ===Status===:
     *  int STATUS_PROCESSING = 0;
     *  int STATUS_REJECTED = -1;
     *  int STATUS_ACCEPTED = 1;
     */
    /**login
     * @param String email
     * @param String password
     * @param int type
     * @return JobSeeker /or/ company
     */
    /**register_company
     *
     * @param String name
     * @param String email
     * @param String phone
     * @param String password
     * @param String education
     * @param String work_experience
     * @param String language
     * @param String summary
     * @param String skill_id
     * @param String cv
     *
     * @return job_seeker
     */
    /**update_job_seeker
     *
     * @param String phone  /optional/
     * @param String education /optional/
     * @param String work_experience /optional/
     * @param String language /optional/
     * @param String summary /optional/
     * @param String skills /optional/
     * @param String cv /optional/
     *
     * @return job_seeker
     */
    /**register_company
     *
     * @param String email
     * @param String password
     * @param String name
     * @param String address
     * @param String details
     *
     * @return company
     */
    /**update_company
     *
     * @param String name /optional/
     * @param String address /optional/
     * @param String details /optional/
     * @param String password /optional/
     *
     * @return company
     */
    /**get_jobs
     * no params
     *
     * @return list of job vacancies
     * each item has these info
     * -int id;
     * -String company_name //form companies table using company_id
     * -String job_position_title;
     * -String required_skill; //form skills table using skill_id
     * -String required_experience;
     * -String work_type;
     * -String work_time;
     * -String salary_range;
     */
    /**apply_job
     *
     * @param int job_seeker_id
     * @param int job_id
     *
     * @return status of api request
     */


    /**get_job_applications
     *note: Job Request which were requested by job_seeker:
     * @param int job_seeker_id
     *
     * @return list of job_requests
     * each item has these info:
     *- int id;
     *- String company_name; //company which posted the job
     *- String job_title; //form job_vacancies table using job_id
     *- int status;
     *- String created_at;
     */
    /**get_my_jobs
     * note: list of jobs which were posted by this company
     * @param int company_id
     *
     * @return list of job_vacancies
     * each item has these info
     * -int id;
     * -String job_position_title;
     * -String required_skill; using skill_id
     * -String required_experience;
     * -String work_type;
     * -String work_time;
     * -String salary_range;
     */

    /**get_job_requests
     * note: list of job_requests for jobs were posted by this company
     *  @param int company_id
     *
     * @return list of job_requests
     * each item has these info:
     * -int id;
     * -String job_title; //form job_vacancies table using job_id
     * -String job_seeker_name; //from job_seekers table using his id
     * -int status;
     * -String created_at;
     */
    /**change_request_status
     *
     * @param int company_id
     * @param int job_id
     * @param int status
     *
     * @return status of api request
     */

    /**verify_user
     * as in the previous apps
     */

    /**add_job
     *@param  String jobPositionTitle;
     *@param  String requiredSkills;
     *@param  String requiredExperience;
     *@param  String workType;
     *@param  String workTime;
     *@param  String salaryRange;
     *@param  String companyName;
     * @return reuqest status
     */
    /**reset_password_request
     * @param String email
     *
     * @return verification_code
     *
     */
    /**reset_password_request
     * @param String email
     * @param String password
     * @return request status
     *
     */
    //TODO: reset password
}
