#
# $Id$
#
# Proguard configuration file for Jikan

-injars ../lib/samskivert.jar(!META-INF/*,!**/velocity/**,!**/xml/**)
-injars ../dist/jikan.jar(!META-INF/*)

-libraryjars <java.home>/lib/rt.jar
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
