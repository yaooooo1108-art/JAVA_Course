package com.group17.enrollment_service.service;

import com.group17.enrollment_service.client.CourseClient;
import com.group17.enrollment_service.model.CourseDto;
import com.group17.enrollment_service.model.EnrollmentRecord;
import com.group17.enrollment_service.util.EnrollmentStore;
import com.group17.enrollment_service.util.LogWriter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnrollmentService {

    private final CourseClient courseClient;
    private final LogWriter logWriter;
    private final EnrollmentStore store;

    public EnrollmentService(CourseClient courseClient, LogWriter logWriter, EnrollmentStore store) {
        this.courseClient = courseClient;
        this.logWriter = logWriter;
        this.store = store;
    }

    /**
     * 加選流程（重點：防呆 + A/B 資料一致性）
     * 1) 先檢查本地是否已選過
     * 2) 向 Course Service(A) 查課程 + 檢查是否額滿
     * 3) 先寫入本地 enrollments（避免更新 A 成功但本地沒寫）
     * 4) 更新 A 的 enrolled +1；若失敗，rollback 本地寫入
     */
    public CourseDto enroll(String studentId, int courseId) {

        String sid = normalizeStudentId(studentId);

        // 1) 重複加選防呆
        if (store.hasEnrollment(sid, courseId)) {
            throw new IllegalArgumentException("你已經選過這門課了");
        }

        // 2) 向 A 查課程
        CourseDto course = courseClient.getCourse(courseId);

        // 3) 容量檢查
        if (course.getEnrolled() >= course.getCapacity()) {
            throw new IllegalArgumentException("課程已滿，無法加選");
        }

        // 4) 先寫入本地
        store.add(sid, courseId);

        // 5) 再更新 A（若失敗則 rollback 本地）
        try {
            CourseDto updated = courseClient.updateEnrolled(courseId, +1);
            logWriter.append("ENROLL studentId=" + sid + " courseId=" + courseId);
            return updated;
        } catch (RuntimeException ex) {
            // rollback
            store.remove(sid, courseId);
            logWriter.append("ENROLL_FAIL studentId=" + sid + " courseId=" + courseId + " reason=" + ex.getMessage());
            throw ex;
        }
    }

    /**
     * 退選流程（重點：防呆 + 一致性）
     * 1) 先檢查本地有沒有選過（沒選過就不能退）
     * 2) 更新 A enrolled -1（若失敗本地不動）
     * 3) 成功後再移除本地
     */
    public CourseDto drop(String studentId, int courseId) {

        String sid = normalizeStudentId(studentId);

        // 1) 沒選過不能退
        if (!store.hasEnrollment(sid, courseId)) {
            throw new IllegalArgumentException("你尚未選這門課，無法退選");
        }

        // 2) 先更新 A（避免本地先移除，結果 A 更新失敗造成不一致）
        CourseDto updated = courseClient.updateEnrolled(courseId, -1);

        // 3) 更新成功再移除本地
        store.remove(sid, courseId);
        logWriter.append("DROP studentId=" + sid + " courseId=" + courseId);

        return updated;
    }

    public List<CourseDto> listAllCourses() {
        return courseClient.getAllCourses();
    }

    public List<Integer> listCourseIds(String studentId) {
        return store.findCourseIdsByStudent(normalizeStudentId(studentId));
    }

    public List<CourseDto> listCourseDetails(String studentId) {
        List<Integer> ids = store.findCourseIdsByStudent(normalizeStudentId(studentId));
        return courseClient.getCourses(ids);
    }

    // 管理/報表用：列出全部選課紀錄
    public List<EnrollmentRecord> listAllEnrollments() {
        return store.listAll();
    }

    private String normalizeStudentId(String studentId) {
    if (studentId == null) {
        throw new IllegalArgumentException("studentId 必填");
    }
    String sid = studentId.trim();
    if (sid.isEmpty()) {
        throw new IllegalArgumentException("studentId 必填");
    }
    // 擋掉前端常見錯誤值，避免污染 enrollments.txt
    if ("undefined".equalsIgnoreCase(sid) || "null".equalsIgnoreCase(sid)) {
        throw new IllegalArgumentException("studentId 無效，請重新登入");
    }
    return sid;
}

}
