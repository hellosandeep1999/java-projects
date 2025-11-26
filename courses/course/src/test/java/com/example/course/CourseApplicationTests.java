package com.example.course;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
class CourseApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void testBookAccess_Age21() throws Exception {
		mockMvc.perform(get("/student/book-access")
				.param("studentId", "S001")
				.param("age", "21"))
				.andExpect(status().isOk())
				.andExpect(content().string("Student S001 can read this book (age > 20)"));
	}

	@Test
	void testBookAccess_Age26() throws Exception {
		mockMvc.perform(get("/student/book-access")
				.param("studentId", "S002")
				.param("age", "26"))
				.andExpect(status().isOk())
				.andExpect(content().string("Student S002 can read this book (age > 25)"));
	}

	@Test
	void testBookAccess_Age19() throws Exception {
		mockMvc.perform(get("/student/book-access")
				.param("studentId", "S003")
				.param("age", "19"))
				.andExpect(status().isOk())
				.andExpect(content().string("Student S003 is not eligible to read this book"));
	}

	@Test
	void testBookAccess_MissingStudentId() throws Exception {
		mockMvc.perform(get("/student/book-access")
				.param("age", "22"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testBookAccess_MissingAge() throws Exception {
		mockMvc.perform(get("/student/book-access")
				.param("studentId", "S004"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void testBookAccess_InvalidAge() throws Exception {
		mockMvc.perform(get("/student/book-access")
				.param("studentId", "S005")
				.param("age", "invalid"))
				.andExpect(status().isBadRequest());
	}
}
