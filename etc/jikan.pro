#
# $Id$
#
# Proguard configuration file for Jikan

-injars ../lib/samskivert.jar(!**/*Log4JLogger*,!**/FileUtil.class,
  com/samskivert/Log.class,com/samskivert/io/StreamUtil.class,com/samskivert/util/*)
-injars ../lib/google-collect.jar(!META-INF/*)
-injars ../lib/gdata-core-1.0.jar(!META-INF/*,!**/apt/**)
-injars ../lib/gdata-client-1.0.jar(!META-INF/*)
-injars ../lib/gdata-client-meta-1.0.jar(!META-INF/*)
-injars ../lib/gdata-calendar-2.0.jar(!META-INF/*)
-injars ../lib/gdata-calendar-meta-2.0.jar(!META-INF/*)
-injars ../dist/jikan.jar(!META-INF/*)

-libraryjars ../lib/swt.jar
-libraryjars ../lib/jsr305.jar
-libraryjars <java.home>/lib/jce.jar

-dontskipnonpubliclibraryclasses
-dontoptimize
-dontobfuscate

-outjars ../dist/jikan-pro.jar

-keepattributes *Annotation*

-keep public class * extends java.lang.Enum {
    *;
}

-keep public class com.samskivert.jikan.Jikan {
    public static void main (java.lang.String[]);
}
