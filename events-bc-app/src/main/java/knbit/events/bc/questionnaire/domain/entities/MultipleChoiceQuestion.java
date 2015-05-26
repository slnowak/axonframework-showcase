package knbit.events.bc.questionnaire.domain.entities;

import knbit.events.bc.questionnaire.domain.enums.QuestionType;
import knbit.events.bc.questionnaire.domain.exceptions.IncorrectChoiceException;
import knbit.events.bc.questionnaire.domain.valueobjects.ids.QuestionId;
import knbit.events.bc.questionnaire.domain.valueobjects.question.*;
import knbit.events.bc.questionnaire.domain.valueobjects.submittedanswer.CheckableAnswer;
import knbit.events.bc.questionnaire.domain.valueobjects.submittedanswer.MultipleChoiceAnswer;

import java.util.List;

/**
 * Created by novy on 25.05.15.
 */
public class MultipleChoiceQuestion extends Question {

    private final List<DomainAnswer> possibleAnswers;

    public MultipleChoiceQuestion(QuestionId questionId, QuestionTitle title, QuestionDescription description, List<DomainAnswer> possibleAnswers) {
        super(questionId, title, description);
        this.possibleAnswers = possibleAnswers;
    }

    @Override
    public AnsweredQuestion check(MultipleChoiceAnswer answer) {
        checkForIdEquality(answer);
        checkForChoiceCorrectness(answer);

        return AnsweredQuestion.of(
                id, title, description, QuestionType.MULTIPLE_CHOICE, possibleAnswers, answer.unwrap()
        );
    }

    @Override
    protected Class<? extends CheckableAnswer> expectedAnswerClass() {
        return MultipleChoiceAnswer.class;
    }

    private void checkForChoiceCorrectness(MultipleChoiceAnswer answer) {
        if (!possibleAnswers.containsAll(answer.unwrap())) {
            throw new IncorrectChoiceException(id);
        }

    }
}