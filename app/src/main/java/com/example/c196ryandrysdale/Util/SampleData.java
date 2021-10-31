package com.example.c196ryandrysdale.Util;



import android.annotation.SuppressLint;

import com.example.c196ryandrysdale.Entities.AssessmentEntity;
import com.example.c196ryandrysdale.Entities.AssessmentType;
import com.example.c196ryandrysdale.Entities.CourseEntity;
import com.example.c196ryandrysdale.Entities.CourseStatus;
import com.example.c196ryandrysdale.Entities.TermEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SampleData {

    @SuppressLint("NewApi")
    private static final LocalDate DATE = LocalDate.of(2021,9,1);

    @SuppressLint("NewApi")
    public static List<TermEntity> getSampleTerms() {
        List<TermEntity> terms = new ArrayList<>();
        terms.add(new TermEntity(0, "Term 1" , DATE, DATE.plusMonths(3)));
        terms.add(new TermEntity(1, "Term 2" , DATE.plusMonths(3), DATE.plusMonths(6)));
        terms.add(new TermEntity(2, "Term 3" , DATE.plusMonths(6), DATE.plusMonths(9)));

        return terms;
    }

    @SuppressLint("NewApi")
    public static List<CourseEntity> getSampleCourses() {
        List<CourseEntity> courses = new ArrayList<>();
        courses.add(new CourseEntity(0, "Math Course", DATE, DATE.plusMonths(1),  CourseStatus.IN_PROGRESS, "Test course notes to be completed, first assignment due, contact instructor, etc stuff", "Jimmy Johnson","541-555-555", "JJohnson@email.com", 0));
        courses.add(new CourseEntity(1, "English Course", DATE.plusMonths(1), DATE.plusMonths(2),  CourseStatus.DROPPED, "Test Course Notes", "Jimmy Johnson","541-555-555", "JJohnson@email.com", 0));
        courses.add(new CourseEntity(2, "History Course", DATE.plusMonths(2), DATE.plusMonths(3),  CourseStatus.PLAN_TO_TAKE, "Test Course Notes", "Jimmy Johnson","541-555-555", "JJohnson@email.com", 0));
        courses.add(new CourseEntity(3, "Geography Course", DATE, DATE.plusMonths(1),  CourseStatus.IN_PROGRESS, "Test Course Notes", "James Reed","541-555-555", "JReed@email.com", 1));
        courses.add(new CourseEntity(4, "Politics Course", DATE.plusMonths(1), DATE.plusMonths(3),  CourseStatus.IN_PROGRESS, "Test Course Notes", "James Reed","541-555-555", "JReed@@email.com", 1));
        courses.add(new CourseEntity(5, "Computer Science Course", DATE, DATE.plusMonths(3),  CourseStatus.IN_PROGRESS, "Test Course Notes", "Mary Clancy","541-555-555", "MClancy@email.com", 2));

        return courses;
    }

    @SuppressLint("NewApi")
    public static List<AssessmentEntity> getSampleAssessments() {
        List<AssessmentEntity> assessments = new ArrayList<>();
        assessments.add(new AssessmentEntity(0, "Sample Assessment 1", AssessmentType.OA, DATE, DATE.plusMonths(1),0));
        assessments.add(new AssessmentEntity(1, "Sample Assessment 2", AssessmentType.PA, DATE, DATE.plusMonths(1),0));
        assessments.add(new AssessmentEntity(2, "Sample Assessment 3", AssessmentType.PA, DATE, DATE.plusMonths(1),1));
        assessments.add(new AssessmentEntity(3, "Sample Assessment 4", AssessmentType.OA, DATE, DATE.plusMonths(1),1));


        return assessments;
    }
}