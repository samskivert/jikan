#
# $Id$
#
# Proguard configuration file for Jikan

-injars ../lib/samskivert.jar(com/samskivert/util/**,com/samskivert/Log.class)
-injars ../lib/google-collect.jar(!META-INF/*)
-injars ../lib/gdata-core-1.0.jar(!META-INF/*,!**/apt/**)
-injars ../lib/gdata-client-1.0.jar(!META-INF/*)
-injars ../lib/gdata-client-meta-1.0.jar(!META-INF/*)
-injars ../lib/gdata-calendar-2.0.jar(!META-INF/*)
-injars ../lib/gdata-calendar-meta-2.0.jar(!META-INF/*)
-injars ../dist/jikan.jar(!META-INF/*)

-libraryjars ../lib/swt.jar

-dontskipnonpubliclibraryclasses
-dontoptimize
-dontobfuscate

-outjars ../dist/jikan-pro.jar

-keep public class * extends java.lang.Enum {
    *;
}

-keep public class com.samskivert.jikan.Jikan {
    public static void main (java.lang.String[]);
}
