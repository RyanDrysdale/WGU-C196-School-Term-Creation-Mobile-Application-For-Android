package com.example.c196ryandrysdale.DataBase;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;


import com.example.c196ryandrysdale.DOA.AssessmentDAO;
import com.example.c196ryandrysdale.DOA.CourseDAO;
import com.example.c196ryandrysdale.DOA.TermDAO;
import com.example.c196ryandrysdale.Entities.AssessmentEntity;
import com.example.c196ryandrysdale.Entities.CourseEntity;
import com.example.c196ryandrysdale.Entities.TermEntity;
import com.example.c196ryandrysdale.Util.SampleData;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {TermEntity.class, CourseEntity.class, AssessmentEntity.class}, version = 2)
@TypeConverters({DataConverter.class, CourseStatusConverter.class, AssessmentTypeConverter.class})
public abstract class ScheduleDatabase extends RoomDatabase {
    public abstract AssessmentDAO assessmentDao();
    public abstract CourseDAO courseDao();
    public abstract TermDAO termDao();

    private static final int NUMBER_OF_THREADS = 4;

    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static volatile ScheduleDatabase INSTANCE;

    static ScheduleDatabase getDatabase(final Context context) {
        if(INSTANCE == null) {
            synchronized (ScheduleDatabase.class) {
                if(INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), ScheduleDatabase.class, "schedule_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            TermDAO mTermDao = INSTANCE.termDao();
            CourseDAO mCourseDao = INSTANCE.courseDao();
            AssessmentDAO mAssessmentDao = INSTANCE.assessmentDao();

            databaseWriteExecutor.execute(() -> {

                mTermDao.deleteAllTerms();
                mCourseDao.deleteAllCourses();
                mAssessmentDao.deleteAllAssessments();

                mTermDao.insertAll(SampleData.getSampleTerms());
                mCourseDao.insertAll(SampleData.getSampleCourses());
                mAssessmentDao.insertAll(SampleData.getSampleAssessments());

            });
        }
    };
}
