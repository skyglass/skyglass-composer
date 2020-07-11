package skyglass.composer.dto.i18n;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Translate {
	public static enum Invokation {
		AFTER, BEFORE;
	}

	Invokation invokation() default Invokation.AFTER;
}
