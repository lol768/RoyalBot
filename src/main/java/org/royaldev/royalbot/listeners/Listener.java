package org.royaldev.royalbot.listeners;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation should be applied to any method in an {@link org.royaldev.royalbot.listeners.IRCListener} that should
 * be marked as a listener. The method this is applied to should only have one argument, and it should be a subclass of
 * {@link org.pircbotx.hooks.Event}.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Listener {
}
