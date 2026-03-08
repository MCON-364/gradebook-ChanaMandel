package edu.course.gradebook;

import java.util.*;

public class Gradebook {

    private final Map<String, List<Integer>> gradesByStudent = new HashMap<>();
    private final Deque<UndoAction> undoStack = new ArrayDeque<>();
    private final LinkedList<String> activityLog = new LinkedList<>();

    public Optional<List<Integer>> findStudentGrades(String name) {
        return Optional.ofNullable(gradesByStudent.get(name));
    }

    public boolean addStudent(String name) {
        if (gradesByStudent.containsKey(name)) {
            return false;
        }

        gradesByStudent.put(name, new ArrayList<>());
        activityLog.add("Added student " + name);
        return true;
    }

    public boolean addGrade(String name, int grade) {
        var grades = gradesByStudent.get(name);
        if (grades == null) {
            return false;
        }

        grades.add(grade);
        undoStack.push((gradebook) -> grades.remove(grades.size() -1));
        activityLog.add("Added grade " + grade + " for " + name);
        return true;
    }

    public boolean removeStudent(String name) {
        var grades = gradesByStudent.get(name);
        if (grades == null) {
            return false;
        }

        var savedGrades = new ArrayList<>(grades);

        gradesByStudent.remove(name);
        UndoAction undoThis = (gradebook -> gradesByStudent.put(name, savedGrades));
        undoStack.push(undoThis);

        activityLog.add("Removed student " + name);
        return true;
    }

    public Optional<Double> averageFor(String name) {
        var grades = gradesByStudent.get(name);
        if (grades == null || grades.isEmpty()) {
            return Optional.empty();
        }

        var sum = 0;
        for (var grade : grades) {
            sum += grade;
        }

        var average = (double) sum / grades.size();
        return Optional.of(average);
    }

    public Optional<String> letterGradeFor(String name) {
        var avgOpt = averageFor(name);
        if (avgOpt.isEmpty()) {
            return Optional.empty();
        }

        var average = avgOpt.get();

        var letter = switch(average.intValue() / 10) {
            case 10, 9 -> "A";
            case 8 -> "B";
            case 7 -> "C";
            case 6 -> "D";
            default -> {
                yield "F";
            }
        };

        return Optional.of(letter);
    }

    public Optional<Double> classAverage() {
        var sum = 0;
        var count = 0;
        for (var grades : gradesByStudent.values()) {
            for( var grade : grades) {
                sum += grade;
                count++;
            }
        }

        if (count == 0) {
            return Optional.empty();
        }

        return Optional.of((double) sum / count);
    }

    public boolean undo() {
        if (undoStack.isEmpty()) {
            return false;
        }

        var action = undoStack.pop();

        action.undo(this);

        activityLog.add("Undo performed");

        return true;
    }

    public List<String> recentLog(int maxItems) {
        if(activityLog.size() <= maxItems) {
            return new ArrayList<>(activityLog);
        }

        var startIndex = activityLog.size() - maxItems;
        return new ArrayList<>(activityLog.subList(startIndex, activityLog.size()));
    }
}
