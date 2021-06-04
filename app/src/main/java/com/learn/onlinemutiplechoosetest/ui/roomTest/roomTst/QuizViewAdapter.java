package com.learn.onlinemutiplechoosetest.ui.roomTest.roomTst;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.learn.onlinemutiplechoosetest.R;
import com.learn.onlinemutiplechoosetest.model.Answer;
import com.learn.onlinemutiplechoosetest.model.Quiz;

import java.util.List;

import lombok.Setter;


public class QuizViewAdapter extends RecyclerView.Adapter<QuizViewAdapter.ItemViewHolder> {

    private final String TAG = getClass().getSimpleName();
    private Context context;
    private List<Quiz> quizzes;

    @Setter
    private boolean isShowEditOptionMenu = false;

    public QuizViewAdapter(Context context, List<Quiz> quizzes) {
        this.context = context;
        this.quizzes = quizzes;
    }

    @Setter
    private OnAnswerCheckedListener onAnswerCheckedListener;
    @Setter
    private OnUpdateQuiz onUpdateQuiz;

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.quiz_item, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Quiz quiz = quizzes.get(position);
        holder.tvQuizTitle.setText("QUESTION " + (position + 1) + ": " + quiz.getTitle());
        //set up edit menu
        holder.cbContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(context);
        if (isShowEditOptionMenu == true) {
            holder.tvOptions.setVisibility(View.VISIBLE);
            holder.tvOptions.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(context, holder.tvOptions);
                popupMenu.inflate(R.menu.quiz_option_menu);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(item ->
                        {
                            switch (item.getItemId()) {
                                case R.id.edit_quiz: {
                                    Dialog dialog = new Dialog(context);
                                    dialog.setContentView(R.layout.answer_edit_dialog);
                                    dialog.show();
                                    TextView title = (TextView) dialog.findViewById(R.id.textView8);
                                    title.setText(quiz.getTitle());
                                    ViewGroup answersViewGroup = (ViewGroup) dialog.findViewById(R.id.adasd);
                                    List<Answer> answers = quiz.getAnswers();
                                    for (int k = 0; k < answers.size(); k++) {
                                        Answer answer = answers.get(k);
                                        ViewGroup aView = (ViewGroup) inflater.inflate(R.layout.answer_edit_entry, answersViewGroup, false);
                                        CheckBox checkBox = aView.findViewById(R.id.checkBox);
                                        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                            if (isChecked) {
                                                for (int i = 0; i < answersViewGroup.getChildCount(); i++) {
                                                    ViewGroup child = (ViewGroup) answersViewGroup.getChildAt(i);
                                                    CheckBox checkBox1 = (CheckBox) child.getChildAt(0);
                                                    if (!checkBox1.equals(checkBox)) {
                                                        checkBox1.setChecked(false);
                                                    }
                                                }
                                            }
                                        });
                                        EditText et = aView.findViewById(R.id.editTextTextPersonName);
                                        et.setText(answer.getContent());
                                        if (answer.isTrue()) {
                                            checkBox.setChecked(true);
                                        } else {
                                            checkBox.setChecked(false);
                                        }
                                        answersViewGroup.addView(aView);
                                    }

                                    Button btnUpdate = dialog.findViewById(R.id.button2);
                                    btnUpdate.setOnClickListener(view -> {
                                        for (int i = 0; i < answersViewGroup.getChildCount(); i++) {
                                            ViewGroup vg = (ViewGroup) answersViewGroup.getChildAt(i);
                                            CheckBox cb = (CheckBox) vg.getChildAt(0);
                                            EditText et = (EditText) vg.getChildAt(1);
                                            answers.get(i).setContent(et.getText().toString().trim());
                                            if (cb.isChecked()) {
                                                answers.get(i).setTrue(true);
                                            } else {
                                                answers.get(i).setTrue(false);
                                            }
                                        }
                                        //view group
                                        notifyItemRangeChanged(position, quizzes.size());
                                        dialog.dismiss();
                                    });

                                    return true;
                                }
                                case R.id.remove_quiz: {

                                    return true;
                                }
                            }
                            return false;
                        }
                );
            });

        }
        //
        int i;

        for (i = 0; i < quiz.getAnswers().size(); i++) {
            ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.answer_entry_2, holder.cbContainer, false);
            Answer a = quiz.getAnswers().get(i);
            CheckBox cb = vg.findViewById(R.id.cb_entry);
            if (a.isTrue() && isShowEditOptionMenu) {
                cb.setChecked(true);
            }
            TextView cbText = vg.findViewById(R.id.cb_text);
            holder.cbContainer.addView(vg);

            cbText.setText(a.getContent());
            cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                //
                if (onAnswerCheckedListener != null) {
                    if (isChecked) {
                        for (int j = 0; j < holder.cbContainer.getChildCount(); j++) {
                            ViewGroup answerEntry = (ViewGroup) holder.cbContainer.getChildAt(j);
                            CheckBox checkbox = (CheckBox) answerEntry.getChildAt(0);
                            if (checkbox != cb) {
                                checkbox.setChecked(false);
                            }
                        }
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
        TextView tvQuizTitle, tvOptions;
        ViewGroup cbContainer;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuizTitle = itemView.findViewById(R.id.tv_quizTitle);
            tvOptions = itemView.findViewById(R.id.quiz_mn_options);
            cbContainer = itemView.findViewById(R.id.cb_container);
        }
    }

    public interface OnAnswerCheckedListener {
        void onAnswerChecked(boolean isChecked, Quiz quiz, Answer answer);
    }

    public interface OnUpdateQuiz {
        void updateQuiz(ViewGroup answersViewGroup, List<Answer> answers, Quiz quiz);
    }
}
