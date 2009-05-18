#
# $Id$
#
# Proguard configuration file for Jikan

-injars ../lib/samskivert.jar(com/samskivert/util/**,com/samskivert/Log.class)
-injars ../lib/google-collect.jar(!META-INF/*)
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
