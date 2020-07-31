package microservices.book.gamification.event;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Event received when a multiplication has been solved in the system.
 * Provides some context information about the multiplication.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
class MultiplicationSolvedEvent implements Serializable {

	private static final long serialVersionUID = -22650480270605790L;

	private Long multiplicationResultAttemptId;

	private Long userId;

	private boolean correct;

}
