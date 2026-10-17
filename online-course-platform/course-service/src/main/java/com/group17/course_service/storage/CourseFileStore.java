package com.group17.course_service.storage;

import com.group17.course_service.model.Course;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@Component
public class CourseFileStore {

    private static final String DIR = "data";
    private static final String FILE = "data/courses_data.txt";

    public CourseFileStore() {
        try {
            Files.createDirectories(Paths.get(DIR));
            if (!Files.exists(Paths.get(FILE))) {
                Files.createFile(Paths.get(FILE));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to init course data file", e);
        }
    }

    // 啟動時讀檔
    public Map<Integer, Course> load() {
        Map<Integer, Course> map = new LinkedHashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#") || line.isBlank()) continue;

                String[] p = line.split(",");
                int id = Integer.parseInt(p[0]);
                String title = p[1];
                int capacity = Integer.parseInt(p[2]);
                int enrolled = Integer.parseInt(p[3]);

                map.put(id, new Course(id, title, capacity, enrolled));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load courses_data.txt", e);
        }
        return map;
    }

    // 每次異動後寫檔
    public void save(Collection<Course> courses) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE))) {
            bw.write("# id,title,capacity,enrolled\n");
            for (Course c : courses) {
                bw.write(String.format(
                        "%d,%s,%d,%d%n",
                        c.getId(),
                        c.getTitle(),
                        c.getCapacity(),
                        c.getEnrolled()
                ));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save courses_data.txt", e);
        }
    }
}
