package com.learn.onlinemutiplechoosetest.ui.roomTest.roomTst;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.learn.onlinemutiplechoosetest.R;
import com.learn.onlinemutiplechoosetest.model.Answer;
import com.learn.onlinemutiplechoosetest.model.Quiz;

import java.util.List;

import lombok.Setter;


public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.ItemViewHolder> {

    private Context context;
    private List<Quiz> quizzes;

    public QuizAdapter(Context context, List<Quiz> quizzes) {
        this.context = context;
        this.quizzes = quizzes;
    }

    @Setter
    private OnAnswerCheckedListener onAnswerCheckedListener;

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.quiz_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Quiz quiz = quizzes.get(position);
        holder.tvQuizTitle.setText("QUESTION " +( position+1) + ": " + quiz.getTitle());
        int i;
        for (i = 0; i < quiz.getAnswers().size(); i++) {
            Answer a = quiz.getAnswers().get(i);
            CheckBox cb = (CheckBox) LayoutInflater.from(context).inflate(R.layout.answer_entry, holder.cbContainer, false);
            holder.cbContainer.addView(cb);
            cb.setText(a.getContent());
            int finalI = i;
            cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (onAnswerCheckedListener != null) {
                    if (isChecked) {
                        int childCount = holder.cbContainer.getChildCount();
                        for (int j = 0; j < childCount; j++) {
                            if (finalI != j) {
                                CheckBox checkBox = (CheckBox) holder.cbContainer.getChildAt(j);
                                checkBox.setChecked(false);
                            }
                        }
                        Toast.makeText(context, "count " + childCount, Toast.LENGTH_SHORT).show();
                    }
                    onAnswerCheckedListener.onAnswerChecked(isChecked, quiz, a);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return this.quizzes.size();
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuizTitle;
        ViewGroup cbContainer;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuizTitle = itemView.findViewById(R.id.tv_quizTitle);
            cbContainer = itemView.findViewById(R.id.cb_container);
        }
    }

    public interface OnAnswerCheckedListener {
        void onAnswerChecked(boolean isChecked, Quiz quiz, Answer answer);
    }
}
