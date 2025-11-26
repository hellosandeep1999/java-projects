package com.example.course.http.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@org.springframework.web.bind.annotation.RestController
public class Controller {

    @GetMapping("/")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello");
    }

    @GetMapping("/student/book-access")
    public ResponseEntity<String> bookAccess(
            @RequestParam("studentId") String studentId,
            @RequestParam("age") int age) {

        String message;
        switch (age) {
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
                message = "Student " + studentId + " can read this book (age > 20)";
                break;
            default:
                if (age > 25) {
                    message = "Student " + studentId + " can read this book (age > 25)";
                } else {
                    message = "Student " + studentId + " is not eligible to read this book";
                }
                break;
        }
        return ResponseEntity.ok(message);
    }

}
