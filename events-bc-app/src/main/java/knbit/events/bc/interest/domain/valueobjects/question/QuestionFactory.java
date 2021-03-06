package knbit.events.bc.interest.domain.valueobjects.question;

import knbit.events.bc.interest.domain.enums.AnswerType;
import knbit.events.bc.interest.domain.policies.completingquestionnaire.AnswerPolicy;
import knbit.events.bc.interest.domain.policies.completingquestionnaire.MultipleChoiceAnswerPolicy;
import knbit.events.bc.interest.domain.policies.completingquestionnaire.SingleChoiceAnswerPolicy;
import knbit.events.bc.interest.domain.policies.completingquestionnaire.TextChoiceAnswerPolicy;

/**
 * Created by novy on 26.05.15.
 */
public class QuestionFactory {

    public static Question newQuestion(QuestionData questionData) {
        final AnswerPolicy validationPolicy = findPolicy(questionData);

        return Question.of(
                questionData.title(),
                questionData.description(),
                validationPolicy
        );
    }

    private static AnswerPolicy findPolicy(QuestionData questionData) {
        final AnswerType answerType = questionData.answerType();
        switch (answerType) {
            case SINGLE_CHOICE:
                return new SingleChoiceAnswerPolicy(questionData.possibleAnswers());
            case MULTIPLE_CHOICE:
                return new MultipleChoiceAnswerPolicy(questionData.possibleAnswers());
            case TEXT:
                return new TextChoiceAnswerPolicy();
            default:
                throw new IllegalArgumentException();
        }
    }

}
