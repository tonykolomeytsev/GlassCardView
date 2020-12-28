# GlassCardView
### (CardView with blur effect)

Implementation of CardView with support for dynamically blurred background (frozen glass effect) like in ios. In the glasscardview module, the library itself. Demonstration of features in the app module.

GlassCardView can be used as a regular FrameLayout. He reacts to his own changes in size and position. Redraws the blurry image when changes in the view hierarchy. In the simplest case, GlassCardView caches the last bitmap frame of the parent container. This frame will be used by other GlassCardViews in the same container.

If you want to optimize the performance of the set of GlassCardView, you can point them to the general frameBufferView (by default they use their parent as frameBufferView).

### Features:

- Blurred background (with blur radius setting)
- Rounded corners
- Elevation shadow
- Overlay color customization (supports solor state lists)

## Using

### Default option

Just add to the layout:

```xml
<kekmech.glasscardview.GlassCardView
    android:id="@+id/glass"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">
    
    <!-- YOUR CONTENT -->
    
</kekmech.glasscardview.GlassCardView>
```

**Profit!**

### More detailed setting

-  `android:elevation` to add shadow.
-  `app:glassBlurRadius` - blur radius in `dp`, default is `32dp`
-  `app:glassCornerRadius` - corner radius in `dp`, default is `4dp`
-  `app:glassBackgroundColor` - overlay color, default is `#ffffff`
-  `app:glassOpacity` - overlay color opacity, default is `0.6f`

```xml
<kekmech.glasscardview.GlassCardView
    android:id="@+id/glass"
    android:layout_width="300dp"
    android:layout_height="100dp"
    android:elevation="2dp"
    app:glassBlurRadius="32dp"
    app:glassCornerRadius="4dp"
    app:glassBackgroundColor="#ffffff"
    app:glassOpacity="0.6">
    
    <!-- YOUR CONTENT -->
    
</kekmech.glasscardview.GlassCardView>
```

Please, choose the closest possible root layout for GlassCardView.

## Basic corner cases

### 1. Blur does not work if the GlassCardView is not in the root container with a transparent background.

Example:

```xml
<ConstraintLayout ... android:id="@+id/container">
    <FrameLayout>
        <GlassCardView android:id="@+id/glass" />
    </FrameLayout>
</ConstraintLayout>
```

In such a case, just specify the root element as the `frameBufferView` for the GlassCardView during the view binding.

```kotlin
glass.frameBufferView = container
```

### 2. You want to optimize your memory consumption.

All GlassCardViews on the screen will cache the parent bitmap. If you want them all to use a common bitmap, give all GlassCardViews a common frameBuferView.

This can be done in the same way as in the previous example:

```kotlin
listOf(glass1, glass2, glass3).forEach { glass ->
    glass.frameBuferView = container
}
```

