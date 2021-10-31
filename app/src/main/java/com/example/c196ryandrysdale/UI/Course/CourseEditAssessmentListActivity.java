package com.example.c196ryandrysdale.UI.Course;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.c196ryandrysdale.DataBase.ScheduleRepository;
import com.example.c196ryandrysdale.Entities.AssessmentEntity;
import com.example.c196ryandrysdale.Entities.CourseEntity;
import com.example.c196ryandrysdale.Entities.CourseStatus;
import com.example.c196ryandrysdale.R;
import com.example.c196ryandrysdale.UI.Assessment.AssessmentEditActivity;
import com.example.c196ryandrysdale.UI.Term.term_edit_course_list;
import com.example.c196ryandrysdale.Util.DatePickerFragment;
import com.example.c196ryandrysdale.Util.DateUtils;
import com.example.c196ryandrysdale.Util.Receiver;
import com.google.android.material.snackbar.Snackbar;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CourseEditAssessmentListActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ScheduleRepository scheduleRepository;

    public static int numAlert;

    public static int termID = -1;

    Calendar calendarInstance = Calendar.getInstance();

    int courseID;
    EditText courseTitle;
    EditText startDate;
    EditText endDate;
    EditText courseNotes;
    Spinner courseStatus;
    EditText editInstructorName;
    EditText editInstructorPhone;
    EditText editInstructorEmail;

    ImageView courseNotesImage;

    CourseEntity currentCourse;

    public static int numAssessments;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_edit_assessment_list);
        System.out.println(AssessmentEditActivity.courseIdAssEditPage);
        //Set Course Status Spinner Contents
        setSpinnerContents();
        //Set Variables
        Button addAssessmentBtn = (Button) findViewById(R.id.addAssessment);
        courseID = getIntent().getIntExtra("courseID", -1);
        termID = getIntent().getIntExtra("termID", -1);
        //Set variable to return back to correct Course Entity from Assessment
        if (courseID == -1) {
            courseID = AssessmentEditActivity.courseIdAssEditPage;
        }
        if (termID == -1)
            termID = AssessmentEditActivity.termIdAssEditPage;
        //If not creating a new Entity, fills out current fields
        scheduleRepository = new ScheduleRepository((getApplication()));
        List<CourseEntity> allCourses = scheduleRepository.getAllCourses();

        for(CourseEntity course:allCourses){
            if (course.getCourseID() == courseID)
                currentCourse = course;
        }

        courseTitle = findViewById(R.id.course_title_edit);
        startDate = findViewById(R.id.course_start_date_edit);
        endDate = findViewById(R.id.course_end_date_edit);
        courseNotes = findViewById(R.id.course_notes_edit_text);
        courseStatus = findViewById(R.id.course_status_spinner);
        editInstructorName = findViewById(R.id.instructor_name_edit_text);
        editInstructorPhone = findViewById(R.id.instructor_phone_edit_text);
        editInstructorEmail = findViewById(R.id.instructor_email_edit_text);

        if(currentCourse != null){
            courseTitle.setText(currentCourse.getCourseTitle());
            startDate.setText(currentCourse.getStartDate().format(DateUtils.dtf));
            endDate.setText(currentCourse.getEndDate().format(DateUtils.dtf));
            courseNotes.setText(currentCourse.getCourseNotes());
            courseStatus.setSelection(currentCourse.getStatus().ordinal());
            editInstructorName.setText(currentCourse.getInstructorName());
            editInstructorPhone.setText(currentCourse.getInstructorPhone());
            editInstructorEmail.setText(currentCourse.getInstructorEmail());
        }
        else {

            addAssessmentBtn.setVisibility(View.GONE);
        }

        courseNotes.setVisibility(View.GONE);

        //------Set and show associated Assessments-----------------//
        scheduleRepository = new ScheduleRepository(getApplication());
        RecyclerView recyclerView = findViewById(R.id.assessment_recyclerview);
        final CourseEditAssessmentListAdapter adapter = new CourseEditAssessmentListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<AssessmentEntity> filteredAssessmentList = new ArrayList<>();
        for(AssessmentEntity assessment:scheduleRepository.getAllAssessments()){
            if (assessment.getCourseID() == courseID)
                filteredAssessmentList.add(assessment);
        }
        numAssessments = filteredAssessmentList.size();
        adapter.setAssessments(filteredAssessmentList);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                scheduleRepository.delete(adapter.getAssessmentAt(viewHolder.getAdapterPosition()));
                adapter.mAssessments.remove(viewHolder.getAdapterPosition());
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                Snackbar snackbar = Snackbar.make(findViewById(R.id.course_snackbar), "Assessment deleted", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }).attachToRecyclerView(recyclerView);

        if (getIntent().getBooleanExtra("courseSaved", false))
            Toast.makeText(this,"Course Saved",Toast.LENGTH_LONG).show();

        if (getIntent().getBooleanExtra("assessmentSaved", false))
            Toast.makeText(this,"Assessment Saved",Toast.LENGTH_LONG).show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_course, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, courseTitle.getText().toString() + " " + courseNotes.getText().toString());
            // (Optional) Here we're setting the title of the content
            sendIntent.putExtra(Intent.EXTRA_TITLE, courseTitle.getText().toString() + " Notes");
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
            return true;
        }
        if (id == R.id.notificationStart) {
            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.course_snackbar), "Notification for Start date Set", Snackbar.LENGTH_LONG);
            mySnackbar.show();
            Intent intentStart = new Intent(CourseEditAssessmentListActivity.this, Receiver.class);
            intentStart.putExtra("courseAlert","Course " + courseTitle.getText().toString() + " starts today");
            PendingIntent senderStart = PendingIntent.getBroadcast(CourseEditAssessmentListActivity.this,++numAlert,intentStart,0);
            AlarmManager alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
            long startDateMillis = DateUtils.parseDate(startDate.getText().toString()).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, startDateMillis, senderStart);
            return true;
        }
        if (id == R.id.notificationEnd) {
            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.course_snackbar), "Notification for End date Set", Snackbar.LENGTH_LONG);
            mySnackbar.show();
            Intent intentEnd = new Intent(CourseEditAssessmentListActivity.this, Receiver.class);
            intentEnd.putExtra("courseAlert","Course " + courseTitle.getText().toString() + " ends today");
            PendingIntent senderEnd = PendingIntent.getBroadcast(CourseEditAssessmentListActivity.this,++numAlert,intentEnd,0);
            AlarmManager alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
            long endDateMillis = DateUtils.parseDate(endDate.getText().toString()).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, endDateMillis, senderEnd);
            return true;
        }
        if (id == R.id.showNotes) {
            courseNotes.setVisibility(View.VISIBLE);
            courseNotesImage.setVisibility(View.VISIBLE);
        }

        return super.onOptionsItemSelected(item);
    }

    public void setSpinnerContents() {
        //------Spinner selection options ----//
        // Spinner element
        Spinner courseStatusSpinner = (Spinner) findViewById(R.id.course_status_spinner);

        // Spinner click listener
        courseStatusSpinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<CourseStatus> categories = new ArrayList<CourseStatus>();
        categories.add(CourseStatus.IN_PROGRESS);
        categories.add(CourseStatus.COMPLETED);
        categories.add(CourseStatus.DROPPED);
        categories.add(CourseStatus.PLAN_TO_TAKE);

        // Creating adapter for spinner
        ArrayAdapter<CourseStatus> dataAdapter = new ArrayAdapter<CourseStatus>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        courseStatusSpinner.setAdapter(dataAdapter);
    }

    public void goToAssessmentEdit(View view) {
        if (numAssessments > 4){
            Toast.makeText(this, "Each course can only have a maximum of 5 Assessments", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(CourseEditAssessmentListActivity.this, AssessmentEditActivity.class);
        intent.putExtra("courseID", courseID);
        intent.putExtra("termID", termID);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addCourseFromScreen(View view) {
        if (courseTitle.getText().toString().trim().isEmpty() ||
                startDate.getText().toString().trim().isEmpty() ||
                endDate.getText().toString().trim().isEmpty() ||
                editInstructorName.getText().toString().trim().isEmpty() ||
                editInstructorPhone.getText().toString().trim().isEmpty() ||
                editInstructorPhone.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "All fields must be filled prior to saving Course", Toast.LENGTH_LONG).show();
            return;
        }

        CourseEntity course;

        if (courseID == -1) {
            List<CourseEntity> allCourses = scheduleRepository.getAllCourses();
            courseID = allCourses.get(allCourses.size() - 1).getCourseID();
            ++courseID;
        }

        course = new CourseEntity
                (
                        courseID,
                        courseTitle.getText().toString(),
                        DateUtils.parseDate(startDate.getText().toString()),
                        DateUtils.parseDate(endDate.getText().toString()),
                        CourseStatus.fromString(courseStatus.getSelectedItem().toString()),
                        courseNotes.getText().toString(),
                        editInstructorName.getText().toString(),
                        editInstructorPhone.getText().toString(),
                        editInstructorEmail.getText().toString(),
                        termID
                );
        scheduleRepository.insert(course);

        Intent intent = new Intent(CourseEditAssessmentListActivity.this, term_edit_course_list.class);
        intent.putExtra("courseSaved",true);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void showDateClickerDialog(View view) {
        int viewID = view.getId();
        TextView datePickerView = findViewById(viewID);
        DialogFragment newFragment = new DatePickerFragment(datePickerView);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
}