package com.example.c196ryandrysdale.UI.Assessment;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import com.example.c196ryandrysdale.DataBase.ScheduleRepository;
import com.example.c196ryandrysdale.Entities.AssessmentEntity;
import com.example.c196ryandrysdale.Entities.AssessmentType;
import com.example.c196ryandrysdale.R;
import com.example.c196ryandrysdale.UI.Course.CourseEditAssessmentListActivity;
import com.example.c196ryandrysdale.Util.DatePickerFragment;
import com.example.c196ryandrysdale.Util.DateUtils;
import com.example.c196ryandrysdale.Util.Receiver;
import com.google.android.material.snackbar.Snackbar;

import java.time.ZoneId;
import java.util.List;

public class AssessmentEditActivity extends AppCompatActivity {
    private ScheduleRepository scheduleRepository;

    public static int courseIdAssEditPage = -1;
    public static int termIdAssEditPage = -1;

    int assessmentID;
    EditText assessmentTitle;
    RadioButton OARadio;
    RadioButton PARadio;
    EditText endDate;
    EditText startDate;
    int courseID;

    AssessmentEntity currentAssessment;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_edit);
        //Set Variables
        System.out.println(courseIdAssEditPage);
        courseIdAssEditPage = getIntent().getIntExtra("courseID", -1);
        courseID = getIntent().getIntExtra("courseID", -1);
        assessmentID = getIntent().getIntExtra("assessmentID", -1);
        termIdAssEditPage = getIntent().getIntExtra("termID", -1);

        System.out.println(courseIdAssEditPage);

        //If not creating a new Entity, fills out current fields
        scheduleRepository = new ScheduleRepository((getApplication()));
        List<AssessmentEntity> allAssessments = scheduleRepository.getAllAssessments();

        for(AssessmentEntity assessment:allAssessments){
            if (assessment.getAssessmentID() == assessmentID)
                currentAssessment = assessment;
        }

        assessmentTitle = findViewById(R.id.assessment_title_edit);
        OARadio = findViewById(R.id.assessment_OA);
        PARadio = findViewById(R.id.assessment_PA);
        endDate = findViewById(R.id.assessment_end_date_edit);
        startDate = findViewById(R.id.assessment_start_date_edit);

        if (currentAssessment != null){
            assessmentTitle.setText(currentAssessment.getAssessmentTitle());
            switch (currentAssessment.getAssessmentType()){
                case OA:
                    OARadio.setChecked(true);
                    break;
                case PA:
                    PARadio.setChecked(true);
                    break;
            }
            endDate.setText(currentAssessment.getEndDate().format(DateUtils.dtf));
            startDate.setText(currentAssessment.getStartDate().format(DateUtils.dtf));
        }

    }



    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_assessment, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.notificationAssessStart) {
            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.assessment_snackbar), "Notification for Start date Set", Snackbar.LENGTH_LONG);
            mySnackbar.show();
            Intent intentStart = new Intent(AssessmentEditActivity.this, Receiver.class);
            intentStart.putExtra("courseAlert","Course " + assessmentTitle.getText().toString() + " starts today");
            PendingIntent senderStart = PendingIntent.getBroadcast(AssessmentEditActivity.this,++CourseEditAssessmentListActivity.numAlert,intentStart,0);
            AlarmManager alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
            long startDateMillis = DateUtils.parseDate(startDate.getText().toString()).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, startDateMillis, senderStart);
            return true;
        }
        if (id == R.id.notificationAssessEnd) {
            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.assessment_snackbar), "Notification for End date Set", Snackbar.LENGTH_LONG);
            mySnackbar.show();
            Intent intentEnd = new Intent(AssessmentEditActivity.this, Receiver.class);
            intentEnd.putExtra("courseAlert","Course " + assessmentTitle.getText().toString() + " ends today");
            PendingIntent senderEnd = PendingIntent.getBroadcast(AssessmentEditActivity.this,++CourseEditAssessmentListActivity.numAlert,intentEnd,0);
            AlarmManager alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
            long endDateMillis = DateUtils.parseDate(endDate.getText().toString()).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, endDateMillis, senderEnd);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDateClickerDialog(View view) {
        int viewID = view.getId();
        TextView datePickerView = findViewById(viewID);
        DialogFragment newFragment = new DatePickerFragment(datePickerView);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addAssessmentFromScreen(View view) {
        if (assessmentTitle.getText().toString().trim().isEmpty() || startDate.getText().toString().trim().isEmpty() || endDate.getText().toString().trim().isEmpty() || (!OARadio.isChecked() && !PARadio.isChecked())){
            Toast.makeText(this, "All fields must be filled prior to saving Assessment", Toast.LENGTH_LONG).show();
            return;
        }

        AssessmentEntity assessment = null;

        if (assessmentID == -1) {
            List<AssessmentEntity> allAssessments = scheduleRepository.getAllAssessments();
            assessmentID = allAssessments.get(allAssessments.size() - 1).getAssessmentID();
            ++assessmentID;
        }
        if (OARadio.isChecked()) {
            assessment = new AssessmentEntity(assessmentID, assessmentTitle.getText().toString(), AssessmentType.OA, DateUtils.parseDate(startDate.getText().toString()), DateUtils.parseDate(endDate.getText().toString()),courseID);
        }
        else if (PARadio.isChecked()){
            assessment = new AssessmentEntity(assessmentID, assessmentTitle.getText().toString(), AssessmentType.PA, DateUtils.parseDate(startDate.getText().toString()), DateUtils.parseDate(endDate.getText().toString()),courseID);
        }
        scheduleRepository.insert(assessment);

        Intent intent = new Intent(AssessmentEditActivity.this, CourseEditAssessmentListActivity.class);
        intent.putExtra("assessmentSaved",true);
        startActivity(intent);
    }
}