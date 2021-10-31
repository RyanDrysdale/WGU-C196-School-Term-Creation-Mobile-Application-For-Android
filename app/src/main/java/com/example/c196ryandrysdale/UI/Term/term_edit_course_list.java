package com.example.c196ryandrysdale.UI.Term;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.c196ryandrysdale.DataBase.ScheduleRepository;
import com.example.c196ryandrysdale.Entities.CourseEntity;
import com.example.c196ryandrysdale.Entities.TermEntity;
import com.example.c196ryandrysdale.R;
import com.example.c196ryandrysdale.UI.Assessment.AssessmentEditActivity;
import com.example.c196ryandrysdale.UI.Course.CourseEditAssessmentListActivity;
import com.example.c196ryandrysdale.Util.DatePickerFragment;
import com.example.c196ryandrysdale.Util.DateUtils;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class term_edit_course_list extends AppCompatActivity {
    private
    ScheduleRepository scheduleRepository;

    int id;
    String title;
    LocalDate startDate;
    LocalDate endDate;
    EditText editTitle;
    EditText editStartDate;
    EditText editEndDate;

    TermEntity currentTerm;

    public static int numCourses;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_edit_course_list);

        Button addCourseBtn = (Button) findViewById(R.id.addCourse);

//------Fill Term Edit Fields if editing a Term-----------------//
        id=getIntent().getIntExtra("termID", -1);
        if(id == -1)
            id = CourseEditAssessmentListActivity.termID;
        scheduleRepository = new ScheduleRepository(getApplication());
        List<TermEntity> allTerms = scheduleRepository.getAllTerms();

        for(TermEntity term:allTerms){
            if(term.getTermID() == id)
                currentTerm = term;
        }

        editTitle = findViewById(R.id.term_name_edit);
        editStartDate = findViewById(R.id.term_start_date_edit);
        editEndDate = findViewById(R.id.term_end_date_edit);

        if(currentTerm != null){
            title = currentTerm.getTermTitle();
            startDate = currentTerm.getStartDate();
            endDate = currentTerm.getEndDate();
        }
        else {
            addCourseBtn.setVisibility(View.GONE);

        }
        if(id != -1){

            editTitle.setText(title);
            editStartDate.setText(startDate.format(DateUtils.dtf));
            editEndDate.setText(endDate.format(DateUtils.dtf));
        }

//------Set and show associated Courses-----------------//
        scheduleRepository = new ScheduleRepository(getApplication());
        RecyclerView recyclerView = findViewById(R.id.course_recyclerview);
        final TermEditCourseListAdapter adapter = new TermEditCourseListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<CourseEntity> filteredCourseEntityList = new ArrayList<>();
        for(CourseEntity course: scheduleRepository.getAllCourses()){
            if (course.getTermID() == id)
                filteredCourseEntityList.add(course);
        }
        numCourses = filteredCourseEntityList.size();
        adapter.setCourses(filteredCourseEntityList);
        adapter.setAssessments(scheduleRepository.getAllAssessments());

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                scheduleRepository.delete(adapter.getCourseAt(viewHolder.getAdapterPosition()));
                adapter.mCourses.remove(viewHolder.getAdapterPosition());
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                Snackbar snackbar = Snackbar.make(findViewById(R.id.snackbar_termedit), "Course deleted", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }).attachToRecyclerView(recyclerView);

        if (getIntent().getBooleanExtra("courseSaved", false))
            Toast.makeText(this,"Course Saved",Toast.LENGTH_LONG).show();
    }


    public void goToCourseEditAssessmentList(View view) {
        Intent intent = new Intent(term_edit_course_list.this, CourseEditAssessmentListActivity.class);
        intent.putExtra("termID", id);
        AssessmentEditActivity.courseIdAssEditPage = -1;
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addTermFromScreen(View view) {
        if (editTitle.getText().toString().trim().isEmpty() || editStartDate.getText().toString().trim().isEmpty() || editEndDate.getText().toString().isEmpty()) {
            Toast.makeText(this, "All fields must be filled prior to saving Term", Toast.LENGTH_LONG).show();
            return;
        }

        TermEntity term;

        if (id != -1)
            term = new TermEntity(id, editTitle.getText().toString(), DateUtils.parseDate(editStartDate.getText().toString()), DateUtils.parseDate(editEndDate.getText().toString()));
        else {
            List<TermEntity> allTerms = scheduleRepository.getAllTerms();
            id = allTerms.get(allTerms.size() - 1).getTermID();
            term = new TermEntity(++id, editTitle.getText().toString(), DateUtils.parseDate(editStartDate.getText().toString()), DateUtils.parseDate(editEndDate.getText().toString()));
        }
        scheduleRepository.insert(term);

        Intent intent = new Intent(term_edit_course_list.this, term_list.class);
        intent.putExtra("termSaved",true);
        startActivity(intent);
    }

    public void showDateClickerDialog(View view) {
        int viewID = view.getId();
        TextView datePickerView = findViewById(viewID);
        DialogFragment newFragment = new DatePickerFragment(datePickerView);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

}