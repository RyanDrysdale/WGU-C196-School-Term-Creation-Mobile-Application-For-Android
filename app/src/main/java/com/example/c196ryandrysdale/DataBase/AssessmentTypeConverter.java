package com.example.c196ryandrysdale.DataBase;

import android.text.TextUtils;

import androidx.room.TypeConverter;

import com.example.c196ryandrysdale.Entities.AssessmentType;


public class AssessmentTypeConverter {
    @TypeConverter
    public static String fromAssessmentTypeToString(AssessmentType assessmentType) {
        if(assessmentType == null) {
            return null;
        }
        return assessmentType.name();
    }

    @TypeConverter
    public static AssessmentType fromStringToAssessmentType(String assessmentType) {
        if(TextUtils.isEmpty(assessmentType)) {
            return AssessmentType.OA;
        }
        return AssessmentType.valueOf(assessmentType);
    }
}