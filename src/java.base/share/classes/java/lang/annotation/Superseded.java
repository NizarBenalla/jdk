package java.lang.annotation;

import static java.lang.annotation.ElementType.*;

/**
 *  The annotation interface {@code java.lang.Superseded} is used to indicate that the using this element is discouraged in favor of newer APIs.
 *  When used it should be complemented with a javadoc @superseded tag to indicate replacement APIs
 *  The @Superseded annotation should always be present if the @superseded javadoc tag is present, and vice-versa.
 *  @since 25
 */

@Documented
@Retention(RetentionPolicy.CLASS)
@Target(value={CONSTRUCTOR, METHOD, PACKAGE, MODULE, TYPE})
public @interface Superseded {
}
