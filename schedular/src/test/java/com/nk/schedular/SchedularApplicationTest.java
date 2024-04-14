package com.nk.schedular;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.nk.schedular.exception.NoContentException;
import com.nk.schedular.exception.UnAuthorizedException;

@SpringBootTest
class SchedularApplicationTest {

    @Test
	void contextLoads() {
		Assertions.assertTrue(true);
	}
	@Test
		void testNoContentException() {
			String message = "Test Message";
			NoContentException exception = new NoContentException(message);
			assertEquals(message, exception.getMessage());
		}
		@Test
		void testUnAuthorizedException() {
			String message = "Test Message";
			UnAuthorizedException exception = new UnAuthorizedException(message);
			assertEquals(message, exception.getMessage());
		}
}