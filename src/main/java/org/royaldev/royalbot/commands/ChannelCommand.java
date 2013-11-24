package org.royaldev.royalbot.commands;

import org.mozilla.javascript.ClassShutter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrapFactory;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;
import org.royaldev.royalbot.BotUtils;

public abstract class ChannelCommand implements IRCCommand {

    static {
        ContextFactory.initGlobal(new SandboxContextFactory());
    }

    /**
     * Gets the name of the command.
     *
     * @return Name of the command
     */
    public abstract String getBaseName();

    /**
     * Gets the channel for this command to execute in.
     *
     * @return Channel to execute in
     */
    public abstract String getChannel();

    /**
     * Gets the JavaScript for the command.
     *
     * @return JavaScript in String
     */
    public abstract String getJavaScript();

    @Override
    public void onCommand(GenericMessageEvent event, String[] args) {
        if (!(event instanceof MessageEvent)) return; // how?
        final MessageEvent me = (MessageEvent) event;
        final Context c = ContextFactory.getGlobal().enterContext();
        c.setClassShutter(new ClassShutter() {
            @Override
            public boolean visibleToScripts(String className) {
                if (className.equals("org.royaldev.royalbot.BotUtils")) return true; // allow BotUtils
                else if (className.equals("org.pircbotx.PircBotX")) return false; // no bot access
                else if (className.startsWith("org.royaldev.royalbot")) return false; // no package access
                return true;
            }
        });
        final Scriptable s = c.initStandardObjects();
        ScriptableObject.putProperty(s, "event", Context.javaToJS(me, s)); // provide message event for ease
        ScriptableObject.putProperty(s, "args", Context.javaToJS(args, s));
        try {
            c.evaluateString(s, getJavaScript(), getName(), 1, null);
        } catch (Exception e) {
            final String url = BotUtils.linkToStackTrace(e);
            event.respond("Exception!" + ((url != null) ? " (" + url + ")" : ""));
        } finally {
            Context.exit();
        }
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.MESSAGE;
    }

    @Override
    public String getName() {
        return getBaseName() + ":" + getChannel();
    }

    private static class SandboxContextFactory extends ContextFactory {
        @Override
        protected Context makeContext() {
            Context cx = super.makeContext();
            cx.setWrapFactory(new SandboxWrapFactory());
            return cx;
        }
    }

    private static class SandboxWrapFactory extends WrapFactory {
        @Override
        public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class staticType) {
            return new SandboxNativeJavaObject(scope, javaObject, staticType);
        }
    }

    private static class SandboxNativeJavaObject extends NativeJavaObject {
        public SandboxNativeJavaObject(Scriptable scope, Object javaObject, Class staticType) {
            super(scope, javaObject, staticType);
        }

        @Override
        public Object get(String name, Scriptable start) {
            if (name.equals("getClass")) return NativeJavaObject.NOT_FOUND; // no reflection
            else if (name.equals("getBot")) return NativeJavaObject.NOT_FOUND; // no bot access
            return super.get(name, start);
        }
    }
}
