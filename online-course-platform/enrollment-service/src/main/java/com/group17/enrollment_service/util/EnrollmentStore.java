package com.group17.enrollment_service.util;

import com.group17.enrollment_service.model.EnrollmentRecord;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class EnrollmentStore {

    // 你專案目前就是 enrollment-service/data/enrollments.txt
    private static final String DATA_PATH = "data/enrollments.txt";

    private Path path() {
        Path p = Paths.get(DATA_PATH);
        try {
            Files.createDirectories(p.getParent());
            if (!Files.exists(p)) Files.createFile(p);
        } catch (IOException e) {
            throw new RuntimeException("Init data file failed: " + e.getMessage(), e);
        }
        return p;
    }

    // ====== Public APIs ======

    public synchronized void add(String studentId, int courseId) {
        String sid = normalizeStudentId(studentId);
        validateCourseId(courseId);

        List<String> cleaned = readAndCleanLines(true); // true = 寫回清理結果
        Set<String> set = new LinkedHashSet<>(cleaned);

        String row = sid + "," + courseId;
        if (!set.contains(row)) {
            set.add(row);
            writeAllLines(new ArrayList<>(set));
        }
    }

    public synchronized void remove(String studentId, int courseId) {
        String sid = normalizeStudentId(studentId);
        validateCourseId(courseId);

        List<String> cleaned = readAndCleanLines(true);
        String target = sid + "," + courseId;

        List<String> kept = cleaned.stream()
                .filter(line -> !line.equals(target))
                .collect(Collectors.toList());

        writeAllLines(kept);
    }

    public synchronized boolean hasEnrollment(String studentId, int courseId) {
        String sid = normalizeStudentId(studentId);
        validateCourseId(courseId);

        String target = sid + "," + courseId;

        // 讀檔時順便清理（避免一直累積髒資料）
        for (String line : readAndCleanLines(true)) {
            if (line.equals(target)) return true;
        }
        return false;
    }

    public synchronized List<Integer> findCourseIdsByStudent(String studentId) {
        String sid = normalizeStudentId(studentId);

        List<Integer> ids = new ArrayList<>();
        for (String line : readAndCleanLines(true)) {
            ParsedRow row = parseLine(line);
            if (row != null && row.studentId.equals(sid)) {
                ids.add(row.courseId);
            }
        }
        return ids;
    }

    public synchronized List<EnrollmentRecord> listAll() {
        List<EnrollmentRecord> result = new ArrayList<>();
        for (String line : readAndCleanLines(true)) {
            ParsedRow row = parseLine(line);
            if (row != null) {
                result.add(new EnrollmentRecord(row.studentId, row.courseId));
            }
        }
        return result;
    }

    // ====== Internal helpers ======

    private void validateCourseId(int courseId) {
        if (courseId <= 0) throw new IllegalArgumentException("courseId 必須為正整數");
    }

    private String normalizeStudentId(String studentId) {
        if (studentId == null) throw new IllegalArgumentException("studentId 必填");
        String sid = studentId.trim();
        if (sid.isEmpty()) throw new IllegalArgumentException("studentId 必填");

        // 擋掉前端常見錯誤值（你圖中就是 undefined）
        if ("undefined".equalsIgnoreCase(sid) || "null".equalsIgnoreCase(sid)) {
            throw new IllegalArgumentException("studentId 無效，請重新登入");
        }

        // 可選：如果你們學號/代碼格式固定，建議開這段（例如 S001）
        // if (!sid.matches("S\\d{3}")) throw new IllegalArgumentException("studentId 格式錯誤（例：S001）");

        return sid;
    }

    // 讀檔 + 清理（移除空白行、格式錯誤行、studentId=undefined/null、courseId 非數字）
    private List<String> readAndCleanLines(boolean rewriteIfChanged) {
        List<String> raw = readAllLinesRaw();
        List<String> cleaned = new ArrayList<>();
        boolean changed = false;

        for (String line : raw) {
            String t = (line == null) ? "" : line.trim();
            if (t.isEmpty()) { changed = true; continue; }

            ParsedRow row = parseLine(t);
            if (row == null) { changed = true; continue; }

            // 用標準化格式輸出（避免多餘空白）
            cleaned.add(row.studentId + "," + row.courseId);
        }

        // 去重但保留順序
        List<String> dedup = new ArrayList<>(new LinkedHashSet<>(cleaned));
        if (dedup.size() != cleaned.size()) changed = true;

        if (rewriteIfChanged && changed) {
            writeAllLines(dedup);
        }
        return dedup;
    }

    private List<String> readAllLinesRaw() {
        try {
            return Files.readAllLines(path(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Read data failed: " + e.getMessage(), e);
        }
    }

    private void writeAllLines(List<String> lines) {
        try {
            Files.write(path(), lines, StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new RuntimeException("Write data failed: " + e.getMessage(), e);
        }
    }

    private ParsedRow parseLine(String t) {
        // 預期格式：studentId,courseId
        String[] parts = t.split(",");
        if (parts.length != 2) return null;

        String sid = parts[0].trim();
        String cidStr = parts[1].trim();

        if (sid.isEmpty()) return null;
        if ("undefined".equalsIgnoreCase(sid) || "null".equalsIgnoreCase(sid)) return null;

        int cid;
        try {
            cid = Integer.parseInt(cidStr);
        } catch (NumberFormatException e) {
            return null;
        }
        if (cid <= 0) return null;

        return new ParsedRow(sid, cid);
    }

    private static class ParsedRow {
        final String studentId;
        final int courseId;
        ParsedRow(String studentId, int courseId) {
            this.studentId = studentId;
            this.courseId = courseId;
        }
    }
}
