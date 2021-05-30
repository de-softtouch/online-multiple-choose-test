package com.learn.onlinemutiplechoosetest.ui.quiz;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.learn.onlinemutiplechoosetest.MainActivity;
import com.learn.onlinemutiplechoosetest.R;
import com.learn.onlinemutiplechoosetest.model.Answer;
import com.learn.onlinemutiplechoosetest.model.Quiz;
import com.learn.onlinemutiplechoosetest.ui.roomTest.roomTst.RoomViewModel;
import com.learn.onlinemutiplechoosetest.utils.RoomUtils;

import java.util.ArrayList;
import java.util.List;

public class QuizNewDialog extends DialogFragment implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();

    private TextInputEditText etRightAnswer;
    private TextInputEditText etQuizName;
    private Button btnAddAnswer;
    private ViewGroup viewGroup;
    private Button btnSubmitQuiz;
    private List<TextInputEditText> etWrongAnswers = new ArrayList<>();
    private RoomViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(RoomViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_quiz_new, container, false);
        etQuizName = root.findViewById(R.id.et_quizNamee);
        viewGroup = root.findViewById(R.id.wrong_answer_container);
        etRightAnswer = root.findViewById(R.id.et_rightAnswer);
        btnAddAnswer = root.findViewById(R.id.btn_addAnswer);
        btnSubmitQuiz = root.findViewById(R.id.btn_submitNewQuiz);
        btnSubmitQuiz.setOnClickListener(this);
        btnAddAnswer.setOnClickListener(v -> {
            if (etWrongAnswers.size() < 3) {
                TextInputEditText et = (TextInputEditText) inflater.inflate(R.layout.wrong_answer_entry, viewGroup, false);
                viewGroup.addView(et);
                et.requestFocus();
                etWrongAnswers.add(et);
                ((MainActivity) getContext()).closeInputMethod();
            } else {
                Toast.makeText(getContext(), "Only 3 answer", Toast.LENGTH_LONG).show();
            }

        });

        return root;
    }


    private void submitQuiz() {
        Quiz quiz = new Quiz();
        List<Answer> answers = new ArrayList<>();
        //wrong answers
        for (TextInputEditText et :
                etWrongAnswers) {
            String a = et.getText().toString().trim();
            if (!TextUtils.isEmpty(a)) {
                Answer answer = new Answer();
                answer.setContent(a);
                answer.setTrue(false);
                answers.add(answer);
            }
        }

        //true answer
        Answer trueAnswer = new Answer();
        trueAnswer.setContent(etRightAnswer.getText().toString().trim());
        trueAnswer.setTrue(true);
        answers.add(trueAnswer);


        quiz.setScore(1);
        quiz.setAnswers(answers);
        quiz.setTitle(etQuizName.getText().toString());
        List<Quiz> quizzes = RoomUtils.addNewQuiz(viewModel.getQuizzes().getValue()
                , quiz);
        viewModel.getQuizzes().setValue(quizzes);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submitNewQuiz: {
                if (TextUtils.isEmpty(etQuizName.getText().toString())) {
                    etQuizName.setError("You are not write your quiz title");
                    return;
                }
                if (TextUtils.isEmpty(etRightAnswer.getText().toString())) {
                    etRightAnswer.setError("Your right answer is empty!");
                    return;
                }
                if (etWrongAnswers.size() < 2) {
                    Toast.makeText(getContext(), "There is at least two wrong answers", Toast.LENGTH_SHORT).show();
                    return;
                }
                submitQuiz();
                this.dismiss();
                break;
            }
        }
    }
}
