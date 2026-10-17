package com.group17.enrollment_service.client;

import com.group17.enrollment_service.model.CourseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
public class CourseClient {

    private final WebClient webClient;

    public CourseClient(@Value("${courseService.baseUrl}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    private Mono<? extends Throwable> asError(HttpStatusCode status, String body) {
        // 讓 B 把 A 的錯誤訊息原封不動丟回前端（更好 demo）
        String msg = (body == null || body.isBlank())
                ? ("Course Service error: " + status.value())
                : body;
        return Mono.error(new IllegalArgumentException(msg));
    }

    public CourseDto getCourse(int courseId) {
        return webClient.get()
                .uri("/courses/{id}", courseId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res ->
                        res.bodyToMono(String.class).flatMap(body -> asError(res.statusCode(), body))
                )
                .bodyToMono(CourseDto.class)
                .block();
    }

    public CourseDto updateEnrolled(int courseId, int delta) {
        return webClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/courses/{id}/enrolled")
                        .queryParam("delta", delta)
                        .build(courseId))
                .retrieve()
                .onStatus(HttpStatusCode::isError, res ->
                        res.bodyToMono(String.class).flatMap(body -> asError(res.statusCode(), body))
                )
                .bodyToMono(CourseDto.class)
                .block();
    }

    public List<CourseDto> getAllCourses() {
        return webClient.get()
                .uri("/courses")
                .retrieve()
                .onStatus(HttpStatusCode::isError, res ->
                        res.bodyToMono(String.class).flatMap(body -> asError(res.statusCode(), body))
                )
                .bodyToFlux(CourseDto.class)
                .collectList()
                .block();
    }

    // 簡單版：逐一抓（課程數少時 demo 足夠）
    public List<CourseDto> getCourses(List<Integer> courseIds) {
        List<CourseDto> result = new ArrayList<>();
        for (int id : courseIds) {
            result.add(getCourse(id));
        }
        return result;
    }

    // ===== Teacher/Admin CRUD (proxy to Course Service A) =====
    public CourseDto createCourse(CourseDto dto) {
        return webClient.post()
                .uri("/courses")
                .bodyValue(dto)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res ->
                        res.bodyToMono(String.class).flatMap(body -> asError(res.statusCode(), body))
                )
                .bodyToMono(CourseDto.class)
                .block();
    }

    public CourseDto updateCourse(int courseId, CourseDto dto) {
        return webClient.put()
                .uri("/courses/{id}", courseId)
                .bodyValue(dto)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res ->
                        res.bodyToMono(String.class).flatMap(body -> asError(res.statusCode(), body))
                )
                .bodyToMono(CourseDto.class)
                .block();
    }

    public void deleteCourse(int courseId) {
        webClient.delete()
                .uri("/courses/{id}", courseId)
                .retrieve()
                .onStatus(HttpStatusCode::isError, res ->
                        res.bodyToMono(String.class).flatMap(body -> asError(res.statusCode(), body))
                )
                .bodyToMono(Void.class)
                .block();
    }
}
