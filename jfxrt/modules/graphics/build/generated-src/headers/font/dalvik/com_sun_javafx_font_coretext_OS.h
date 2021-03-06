/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_sun_javafx_font_coretext_OS */

#ifndef _Included_com_sun_javafx_font_coretext_OS
#define _Included_com_sun_javafx_font_coretext_OS
#ifdef __cplusplus
extern "C" {
#endif
#undef com_sun_javafx_font_coretext_OS_kCFURLPOSIXPathStyle
#define com_sun_javafx_font_coretext_OS_kCFURLPOSIXPathStyle 0L
#undef com_sun_javafx_font_coretext_OS_kCTFontOrientationDefault
#define com_sun_javafx_font_coretext_OS_kCTFontOrientationDefault 0L
#undef com_sun_javafx_font_coretext_OS_kCTFontManagerScopeProcess
#define com_sun_javafx_font_coretext_OS_kCTFontManagerScopeProcess 1L
#undef com_sun_javafx_font_coretext_OS_kCGBitmapByteOrder32Big
#define com_sun_javafx_font_coretext_OS_kCGBitmapByteOrder32Big 16384L
#undef com_sun_javafx_font_coretext_OS_kCGBitmapByteOrder32Little
#define com_sun_javafx_font_coretext_OS_kCGBitmapByteOrder32Little 8192L
#undef com_sun_javafx_font_coretext_OS_kCGImageAlphaPremultipliedFirst
#define com_sun_javafx_font_coretext_OS_kCGImageAlphaPremultipliedFirst 2L
#undef com_sun_javafx_font_coretext_OS_kCGImageAlphaNone
#define com_sun_javafx_font_coretext_OS_kCGImageAlphaNone 0L
/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CGBitmapContextGetData
 * Signature: (JIII)[B
 */
JNIEXPORT jbyteArray JNICALL Java_com_sun_javafx_font_coretext_OS_CGBitmapContextGetData
  (JNIEnv *, jclass, jlong, jint, jint, jint);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CGRectApplyAffineTransform
 * Signature: (Lcom/sun/javafx/font/coretext/CGRect;Lcom/sun/javafx/font/coretext/CGAffineTransform;)V
 */
JNIEXPORT void JNICALL Java_com_sun_javafx_font_coretext_OS_CGRectApplyAffineTransform
  (JNIEnv *, jclass, jobject, jobject);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CGPathApply
 * Signature: (J)Lcom/sun/javafx/geom/Path2D;
 */
JNIEXPORT jobject JNICALL Java_com_sun_javafx_font_coretext_OS_CGPathApply
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CGPathGetPathBoundingBox
 * Signature: (J)Lcom/sun/javafx/font/coretext/CGRect;
 */
JNIEXPORT jobject JNICALL Java_com_sun_javafx_font_coretext_OS_CGPathGetPathBoundingBox
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CFStringCreateWithCharacters
 * Signature: (J[CJJ)J
 */
JNIEXPORT jlong JNICALL Java_com_sun_javafx_font_coretext_OS_CFStringCreateWithCharacters__J_3CJJ
  (JNIEnv *, jclass, jlong, jcharArray, jlong, jlong);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CTFontCopyAttributeDisplayName
 * Signature: (J)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_sun_javafx_font_coretext_OS_CTFontCopyAttributeDisplayName
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CTFontDrawGlyphs
 * Signature: (JSDDJ)V
 */
JNIEXPORT void JNICALL Java_com_sun_javafx_font_coretext_OS_CTFontDrawGlyphs
  (JNIEnv *, jclass, jlong, jshort, jdouble, jdouble, jlong);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CTFontGetAdvancesForGlyphs
 * Signature: (JISLcom/sun/javafx/font/coretext/CGSize;)D
 */
JNIEXPORT jdouble JNICALL Java_com_sun_javafx_font_coretext_OS_CTFontGetAdvancesForGlyphs
  (JNIEnv *, jclass, jlong, jint, jshort, jobject);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CTFontGetBoundingRectForGlyphUsingTables
 * Signature: (JSS[I)Z
 */
JNIEXPORT jboolean JNICALL Java_com_sun_javafx_font_coretext_OS_CTFontGetBoundingRectForGlyphUsingTables
  (JNIEnv *, jclass, jlong, jshort, jshort, jintArray);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CTRunGetGlyphs
 * Signature: (JII[I)I
 */
JNIEXPORT jint JNICALL Java_com_sun_javafx_font_coretext_OS_CTRunGetGlyphs
  (JNIEnv *, jclass, jlong, jint, jint, jintArray);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CTRunGetStringIndices
 * Signature: (JI[I)I
 */
JNIEXPORT jint JNICALL Java_com_sun_javafx_font_coretext_OS_CTRunGetStringIndices
  (JNIEnv *, jclass, jlong, jint, jintArray);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CTRunGetPositions
 * Signature: (JI[F)I
 */
JNIEXPORT jint JNICALL Java_com_sun_javafx_font_coretext_OS_CTRunGetPositions
  (JNIEnv *, jclass, jlong, jint, jfloatArray);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    kCFAllocatorDefault
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_sun_javafx_font_coretext_OS_kCFAllocatorDefault
  (JNIEnv *, jclass);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    kCFTypeDictionaryKeyCallBacks
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_sun_javafx_font_coretext_OS_kCFTypeDictionaryKeyCallBacks
  (JNIEnv *, jclass);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    kCFTypeDictionaryValueCallBacks
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_sun_javafx_font_coretext_OS_kCFTypeDictionaryValueCallBacks
  (JNIEnv *, jclass);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    kCTFontAttributeName
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_sun_javafx_font_coretext_OS_kCTFontAttributeName
  (JNIEnv *, jclass);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CFArrayGetCount
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_sun_javafx_font_coretext_OS_CFArrayGetCount
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CFArrayGetValueAtIndex
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL Java_com_sun_javafx_font_coretext_OS_CFArrayGetValueAtIndex
  (JNIEnv *, jclass, jlong, jlong);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CFAttributedStringCreate
 * Signature: (JJJ)J
 */
JNIEXPORT jlong JNICALL Java_com_sun_javafx_font_coretext_OS_CFAttributedStringCreate
  (JNIEnv *, jclass, jlong, jlong, jlong);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CFDictionaryAddValue
 * Signature: (JJJ)V
 */
JNIEXPORT void JNICALL Java_com_sun_javafx_font_coretext_OS_CFDictionaryAddValue
  (JNIEnv *, jclass, jlong, jlong, jlong);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CFDictionaryCreateMutable
 * Signature: (JJJJ)J
 */
JNIEXPORT jlong JNICALL Java_com_sun_javafx_font_coretext_OS_CFDictionaryCreateMutable
  (JNIEnv *, jclass, jlong, jlong, jlong, jlong);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CFDictionaryGetValue
 * Signature: (JJ)J
 */
JNIEXPORT jlong JNICALL Java_com_sun_javafx_font_coretext_OS_CFDictionaryGetValue
  (JNIEnv *, jclass, jlong, jlong);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CFRelease
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_sun_javafx_font_coretext_OS_CFRelease
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CFStringCreateWithCharacters
 * Signature: (J[CJ)J
 */
JNIEXPORT jlong JNICALL Java_com_sun_javafx_font_coretext_OS_CFStringCreateWithCharacters__J_3CJ
  (JNIEnv *, jclass, jlong, jcharArray, jlong);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CFURLCreateWithFileSystemPath
 * Signature: (JJJZ)J
 */
JNIEXPORT jlong JNICALL Java_com_sun_javafx_font_coretext_OS_CFURLCreateWithFileSystemPath
  (JNIEnv *, jclass, jlong, jlong, jlong, jboolean);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CGBitmapContextCreate
 * Signature: (JJJJJJI)J
 */
JNIEXPORT jlong JNICALL Java_com_sun_javafx_font_coretext_OS_CGBitmapContextCreate
  (JNIEnv *, jclass, jlong, jlong, jlong, jlong, jlong, jlong, jint);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CGContextFillRect
 * Signature: (JLcom/sun/javafx/font/coretext/CGRect;)V
 */
JNIEXPORT void JNICALL Java_com_sun_javafx_font_coretext_OS_CGContextFillRect
  (JNIEnv *, jclass, jlong, jobject);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CGContextRelease
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_sun_javafx_font_coretext_OS_CGContextRelease
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CGContextSetAllowsFontSmoothing
 * Signature: (JZ)V
 */
JNIEXPORT void JNICALL Java_com_sun_javafx_font_coretext_OS_CGContextSetAllowsFontSmoothing
  (JNIEnv *, jclass, jlong, jboolean);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CGContextSetAllowsAntialiasing
 * Signature: (JZ)V
 */
JNIEXPORT void JNICALL Java_com_sun_javafx_font_coretext_OS_CGContextSetAllowsAntialiasing
  (JNIEnv *, jclass, jlong, jboolean);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CGContextSetAllowsFontSubpixelPositioning
 * Signature: (JZ)V
 */
JNIEXPORT void JNICALL Java_com_sun_javafx_font_coretext_OS_CGContextSetAllowsFontSubpixelPositioning
  (JNIEnv *, jclass, jlong, jboolean);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CGContextSetAllowsFontSubpixelQuantization
 * Signature: (JZ)V
 */
JNIEXPORT void JNICALL Java_com_sun_javafx_font_coretext_OS_CGContextSetAllowsFontSubpixelQuantization
  (JNIEnv *, jclass, jlong, jboolean);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CGContextSetRGBFillColor
 * Signature: (JDDDD)V
 */
JNIEXPORT void JNICALL Java_com_sun_javafx_font_coretext_OS_CGContextSetRGBFillColor
  (JNIEnv *, jclass, jlong, jdouble, jdouble, jdouble, jdouble);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CGContextTranslateCTM
 * Signature: (JDD)V
 */
JNIEXPORT void JNICALL Java_com_sun_javafx_font_coretext_OS_CGContextTranslateCTM
  (JNIEnv *, jclass, jlong, jdouble, jdouble);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CGColorSpaceCreateDeviceGray
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_sun_javafx_font_coretext_OS_CGColorSpaceCreateDeviceGray
  (JNIEnv *, jclass);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CGColorSpaceCreateDeviceRGB
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_sun_javafx_font_coretext_OS_CGColorSpaceCreateDeviceRGB
  (JNIEnv *, jclass);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CGColorSpaceRelease
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_sun_javafx_font_coretext_OS_CGColorSpaceRelease
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CGPathRelease
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_sun_javafx_font_coretext_OS_CGPathRelease
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CTFontCreateWithName
 * Signature: (JDLcom/sun/javafx/font/coretext/CGAffineTransform;)J
 */
JNIEXPORT jlong JNICALL Java_com_sun_javafx_font_coretext_OS_CTFontCreateWithName
  (JNIEnv *, jclass, jlong, jdouble, jobject);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CTFontCreatePathForGlyph
 * Signature: (JSLcom/sun/javafx/font/coretext/CGAffineTransform;)J
 */
JNIEXPORT jlong JNICALL Java_com_sun_javafx_font_coretext_OS_CTFontCreatePathForGlyph
  (JNIEnv *, jclass, jlong, jshort, jobject);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CTFontManagerRegisterFontsForURL
 * Signature: (JIJ)Z
 */
JNIEXPORT jboolean JNICALL Java_com_sun_javafx_font_coretext_OS_CTFontManagerRegisterFontsForURL
  (JNIEnv *, jclass, jlong, jint, jlong);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CTLineCreateWithAttributedString
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_sun_javafx_font_coretext_OS_CTLineCreateWithAttributedString
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CTLineGetGlyphRuns
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_sun_javafx_font_coretext_OS_CTLineGetGlyphRuns
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CTLineGetGlyphCount
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_sun_javafx_font_coretext_OS_CTLineGetGlyphCount
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CTLineGetTypographicBounds
 * Signature: (J)D
 */
JNIEXPORT jdouble JNICALL Java_com_sun_javafx_font_coretext_OS_CTLineGetTypographicBounds
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CTRunGetGlyphCount
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_sun_javafx_font_coretext_OS_CTRunGetGlyphCount
  (JNIEnv *, jclass, jlong);

/*
 * Class:     com_sun_javafx_font_coretext_OS
 * Method:    CTRunGetAttributes
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_sun_javafx_font_coretext_OS_CTRunGetAttributes
  (JNIEnv *, jclass, jlong);

#ifdef __cplusplus
}
#endif
#endif
