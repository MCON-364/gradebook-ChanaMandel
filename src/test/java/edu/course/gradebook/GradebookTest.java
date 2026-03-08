package edu.course.gradebook;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

public class GradebookTest {
    private Gradebook gradebook;

    @BeforeEach
    void setup() {
        gradebook = new Gradebook();
    }

    @Test
    void testAddStudentSuccess() {
        assertTrue(gradebook.addStudent("Alice"));
    }

    @Test
    void testAddStudentDuplicate() {
        gradebook.addStudent("Alice");       // add Alice first time
        assertFalse(gradebook.addStudent("Alice"));  // second time should fail
    }

    @Test
    void testAddGradeSuccess() {
        gradebook.addStudent("Alice");
        assertTrue(gradebook.addGrade("Alice", 90));
    }

    @Test
    void testAddGradeStudentNotFound() {
        assertFalse(gradebook.addGrade("Alice", 90));
    }

    @Test
    void testAddGradeActuallyAddsGrade() {
        gradebook.addStudent("Alice");
        gradebook.addGrade("Alice", 90);
        var grades = gradebook.findStudentGrades("Alice");
        assertTrue(grades.isPresent());
        assertTrue(grades.get().contains(90));
    }

    @Test
    void testRemoveStudentSuccess() {
        gradebook.addStudent("Alice");
        assertTrue(gradebook.removeStudent("Alice"));
    }

    @Test
    void testRemoveStudentNotFound() {
        assertFalse(gradebook.removeStudent("Alice"));
    }

    @Test
    void testRemoveStudentActuallyRemoves() {
        gradebook.addStudent("Alice");
        gradebook.removeStudent("Alice");
        var grades = gradebook.findStudentGrades("Alice");
        assertTrue(grades.isEmpty());
    }

    @Test
    void testAverageForSuccess() {
        gradebook.addStudent("Alice");
        gradebook.addGrade("Alice", 90);
        gradebook.addGrade("Alice", 80);
        var avg = gradebook.averageFor("Alice");
        assertTrue(avg.isPresent());
        assertEquals(85.0, avg.get());
    }

    @Test
    void testAverageForNoGrades() {
        gradebook.addStudent("Alice");
        var avg = gradebook.averageFor("Alice");
        assertTrue(avg.isEmpty());
    }

    @Test
    void testAverageForStudentNotFound() {
        var avg = gradebook.averageFor("Alice");
        assertTrue(avg.isEmpty());
    }

    @Test
    void testLetterGradeA() {
        gradebook.addStudent("Alice");
        gradebook.addGrade("Alice", 95);
        var letter = gradebook.letterGradeFor("Alice");
        assertTrue(letter.isPresent());
        assertEquals("A", letter.get());
    }

    @Test
    void testLetterGradeB() {
        gradebook.addStudent("Alice");
        gradebook.addGrade("Alice", 85);
        var letter = gradebook.letterGradeFor("Alice");
        assertEquals("B", letter.get());
    }

    @Test
    void testLetterGradeC() {
        gradebook.addStudent("Alice");
        gradebook.addGrade("Alice", 75);
        var letter = gradebook.letterGradeFor("Alice");
        assertEquals("C", letter.get());
    }

    @Test
    void testLetterGradeD() {
        gradebook.addStudent("Alice");
        gradebook.addGrade("Alice", 65);
        var letter = gradebook.letterGradeFor("Alice");
        assertEquals("D", letter.get());
    }

    @Test
    void testLetterGradeF() {
        gradebook.addStudent("Alice");
        gradebook.addGrade("Alice", 55);
        var letter = gradebook.letterGradeFor("Alice");
        assertEquals("F", letter.get());
    }

    @Test
    void testLetterGradeNoGrades() {
        gradebook.addStudent("Alice");
        var letter = gradebook.letterGradeFor("Alice");
        assertTrue(letter.isEmpty());
    }

    @Test
    void testClassAverageSuccess() {
        gradebook.addStudent("Alice");
        gradebook.addGrade("Alice", 90);
        gradebook.addGrade("Alice", 80);
        gradebook.addStudent("Bob");
        gradebook.addGrade("Bob", 70);
        var avg = gradebook.classAverage();
        assertTrue(avg.isPresent());
        assertEquals(80.0, avg.get());
    }

    @Test
    void testClassAverageNoStudents() {
        var avg = gradebook.classAverage();
        assertTrue(avg.isEmpty());
    }

    @Test
    void testClassAverageNoGrades() {
        gradebook.addStudent("Alice");
        gradebook.addStudent("Bob");
        var avg = gradebook.classAverage();
        assertTrue(avg.isEmpty());
    }

    @Test
    void testUndoAddGrade() {
        gradebook.addStudent("Alice");
        gradebook.addGrade("Alice", 90);
        gradebook.undo();
        var grades = gradebook.findStudentGrades("Alice");
        assertTrue(grades.isPresent());
        assertTrue(grades.get().isEmpty());
    }

    @Test
    void testUndoRemoveStudent() {
        gradebook.addStudent("Alice");
        gradebook.removeStudent("Alice");
        gradebook.undo();
        var grades = gradebook.findStudentGrades("Alice");
        assertTrue(grades.isPresent());
    }

    @Test
    void testUndoEmptyStack() {
        assertFalse(gradebook.undo());
    }

    @Test
    void testUndoMultipleOperations() {
        gradebook.addStudent("Alice");
        gradebook.addGrade("Alice", 90);
        gradebook.addGrade("Alice", 80);
        gradebook.undo();
        var grades = gradebook.findStudentGrades("Alice");
        assertTrue(grades.isPresent());
        assertEquals(1, grades.get().size());
    }

    @Test
    void testRecentLogSuccess() {
        gradebook.addStudent("Alice");
        gradebook.addStudent("Bob");
        gradebook.addStudent("Charlie");
        var log = gradebook.recentLog(2);
        assertEquals(2, log.size());
        assertEquals("Added student Charlie", log.get(1));
    }

    @Test
    void testRecentLogFewerThanMax() {
        gradebook.addStudent("Alice");
        gradebook.addStudent("Bob");
        var log = gradebook.recentLog(10);
        assertEquals(2, log.size());
    }

    @Test
    void testRecentLogEmpty() {
        var log = gradebook.recentLog(10);
        assertTrue(log.isEmpty());
    }
}
