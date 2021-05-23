package com.learn.onlinemutiplechoosetest.ui.roomTest.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

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
        List<Answer> answers = quiz.getAnswers();
        int q = position + 1;
        holder.tvQuizTitle.setText("QUESTION " + q + ": " + quiz.getTitle());

        holder.a1.setText(answers.get(0).getContent());
        holder.a2.setText(answers.get(1).getContent());
        holder.a3.setText(answers.get(2).getContent());
        if (answers.size() > 3) {
            holder.a4.setText(answers.get(3).getContent());
        }
        holder.a1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (onAnswerCheckedListener != null) {
                if (isChecked) {
                    holder.a2.setChecked(false);
                    holder.a3.setChecked(false);
                    holder.a4.setChecked(false);
                }
                onAnswerCheckedListener.onAnswerChecked(isChecked, quiz, answers.get(0));
            }
        });
        holder.a2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (onAnswerCheckedListener != null) {
                if (isChecked) {
                    holder.a1.setChecked(false);
                    holder.a3.setChecked(false);
                    holder.a4.setChecked(false);
                }
                onAnswerCheckedListener.onAnswerChecked(isChecked, quiz, answers.get(1));
            }
        });
        holder.a3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (onAnswerCheckedListener != null) {
                if (isChecked) {
                    holder.a2.setChecked(false);
                    holder.a1.setChecked(false);
                    holder.a4.setChecked(false);
                }
                onAnswerCheckedListener.onAnswerChecked(isChecked, quiz, answers.get(2));
            }
        });
        holder.a4.setVisibility(View.INVISIBLE);
        if (answers.size() > 3) {
            holder.a4.setVisibility(View.VISIBLE);
            holder.a4.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (onAnswerCheckedListener != null) {
                    if (isChecked) {
                        holder.a2.setChecked(false);
                        holder.a3.setChecked(false);
                        holder.a1.setChecked(false);
                    }
                    onAnswerCheckedListener.onAnswerChecked(isChecked, quiz, answers.get(3));
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
        CheckBox a1, a2, a3, a4;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuizTitle = itemView.findViewById(R.id.tv_quizTitle);
            a1 = itemView.findViewById(R.id.a1);
            a2 = itemView.findViewById(R.id.a2);
            a3 = itemView.findViewById(R.id.a3);
            a4 = itemView.findViewById(R.id.a4);
        }
    }

    public interface OnAnswerCheckedListener {
        void onAnswerChecked(boolean isChecked, Quiz quiz, Answer answer);
    }
}
