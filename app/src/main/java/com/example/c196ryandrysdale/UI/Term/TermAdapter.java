package com.example.c196ryandrysdale.UI.Term;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.c196ryandrysdale.Entities.CourseEntity;
import com.example.c196ryandrysdale.Entities.TermEntity;
import com.example.c196ryandrysdale.R;

import java.util.List;

public class TermAdapter extends RecyclerView.Adapter<TermAdapter.TermViewHolder> {

    private final LayoutInflater mInflater;
    private final Context context;
    public List<TermEntity> mTerms;
    private List<CourseEntity> mCourses;


    public TermAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    class TermViewHolder extends RecyclerView.ViewHolder {
        private final TextView termItemView;
        private final TextView courseItemList;

        private TermViewHolder(View itemView){
            super(itemView);
            termItemView = itemView.findViewById(R.id.term_item_text_view);
            courseItemList = itemView.findViewById(R.id.term_course_list);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick (View v) {
                    int position = getAdapterPosition();
                    final TermEntity currentTerm = mTerms.get(position);
                    Intent intent = new Intent(context, term_edit_course_list.class);
                    intent.putExtra("termID", currentTerm.getTermID());
                    intent.putExtra("position", position);
                    context.startActivity(intent);
                }
            });
        }

    }

    @Override
    public TermViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.term_list_item, parent, false);
        return new TermViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TermViewHolder holder, int position) {

        if(mTerms != null) {
            final TermEntity currentTerm = mTerms.get(position);
            holder.termItemView.setText((currentTerm.getTermTitle()));

            String filteredCourseEntityList = "";
            for(CourseEntity course: mCourses) {
                if (course.getTermID() == currentTerm.getTermID())
                    filteredCourseEntityList = filteredCourseEntityList + course.getCourseTitle() + "\n";
            }
            if (filteredCourseEntityList != "")
                holder.courseItemList.setText(filteredCourseEntityList);
            else
                holder.courseItemList.setVisibility(View.GONE);

        } else {
            holder.termItemView.setText("no text");
        }
    }

    @Override
    public int getItemCount() {
        if (mTerms != null)
            return mTerms.size();
        else return 0;
    }

    public void setTerms(List<TermEntity> terms) {
        mTerms = terms;
        notifyDataSetChanged();
    }

    public void setCourses(List<CourseEntity> courses) {
        mCourses = courses;
        notifyDataSetChanged();
    }

    public TermEntity getTermAt(int position) {
        return mTerms.get(position);
    }

}