package com.obsidiandynamics.yconf;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.*;

/**
 *  Instruction to reflectively assign an attribute value or a constructor
 *  parameter.<p>
 *  
 *  When injecting a field, both {@code name} and {@code type} are optional, and
 *  can be inferred from the field being injected. However, when injecting a constructor
 *  parameter, the {@code name} must be supplied.<p>
 *  
 *  The {@code mapper} is always optional, defaulting to the registered type mapper
 *  of the inferred or supplied type.
 */
@Documented
@Retention(RUNTIME)
@Target({FIELD, PARAMETER})
public @interface YInject {
  String name() default "";
  Class<?> type() default Void.class;
  Class<? extends TypeMapper> mapper() default NullMapper.class;
}
