![zerokol.com](http://2.bp.blogspot.com/-5iIoZXBw2bg/T1n753kamDI/AAAAAAAAAxo/CjU5hGy4QbY/s400/joystickview-screen.png)

Para modo de uso em Portugês: [JoystickView - Uma view customizada que simula um Joystick no Android.](http://www.zerokol.com/2012/03/joystickview-uma-view-customizada-que.html)

For more info, go to: [JoystickView - A custom Android View to simulates a Joystickm Joystick no Android ](http://www.zerokol.com/2012/03/joystickview-custom-android-view-to.html)

## JoystickView - Android

Android JoystickView is a Custom View that simulates a Joystick for interactive applications for Android, 
as simple aim is allows access values ​​of angle and power of the virtual Joystick movement.

### Usage

### Quick Usage

Step 1 - Download or Clone the library (using Git, or a zip archive to unzip)

Step 2 - Open your project in Android Studio

Step 3 - Go to File > New > Import Module

Step 4 - Find and select JoystickView in your project tree

Step 5 - Right-click your app in project view and select "Open Module Settings"

Step 6 - Click the "Dependencies" tab and then the '+' button (Module Dependency)

Step 7 - Select "joystickView"

That is all!



### SHOW ME THE CODE

At this point, you just need to include the View in any layout to start to use the JoystickView, for example:

```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <com.zerokol.views.joystickView.JoystickView
        android:id="@+id/joystickView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />

</android.support.constraint.ConstraintLayout>
```

But certainly you will want to manipulate and extract values from JoystickView, for that you can use this source for a complete reference:


activity_main.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/MainLinerLayout"
    android:layout_width="fill_parent"
    android:layout_height="fill_parent"
    android:orientation="vertical">

    <com.zerokol.views.joystickView.JoystickView
        android:id="@+id/joystickView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />

    <LinearLayout
        android:id="@+id/InfoLinearLayout"
        android:layout_width="fill_parent"
        android:layout_height="fill_parent"
        android:orientation="vertical">

        <TextView
            android:id="@+id/rightsTextView"
            android:layout_width="fill_parent"
            android:layout_height="wrap_content"
            android:gravity="center"
            android:text="@string/rights_lab"
            android:textAppearance="?android:attr/textAppearanceSmall" />

        <TableLayout
            android:id="@+id/tableLayout1"
            android:layout_width="fill_parent"
            android:layout_height="wrap_content">

            <TableRow
                android:id="@+id/tableRow1"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content">

                <TextView
                    android:id="@+id/angletextViewLab"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="@string/angle_lab"
                    android:textAppearance="?android:attr/textAppearanceLarge" />

                <TextView
                    android:id="@+id/angleTextView"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="@string/none"
                    android:textAppearance="?android:attr/textAppearanceMedium" />
            </TableRow>

            <TableRow
                android:id="@+id/tableRow2"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content">

                <TextView
                    android:id="@+id/powerTextViewLab"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="@string/power_lab"
                    android:textAppearance="?android:attr/textAppearanceLarge" />

                <TextView
                    android:id="@+id/powerTextView"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="@string/none"
                    android:textAppearance="?android:attr/textAppearanceMedium" />
            </TableRow>

            <TableRow
                android:id="@+id/tableRow3"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content">

                <TextView
                    android:id="@+id/directionTextViewLab"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="@string/direction_lab"
                    android:textAppearance="?android:attr/textAppearanceLarge" />

                <TextView
                    android:id="@+id/directionTextView"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="@string/none"
                    android:textAppearance="?android:attr/textAppearanceMedium" />
            </TableRow>
        </TableLayout>

    </LinearLayout>

</LinearLayout>
```

MainActivity.java
```java
package com.zerokol.myapplication;

import com.zerokol.views.joystickView.JoystickView;
import com.zerokol.views.joystickView.JoystickView.OnJoystickMoveListener;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    private TextView angleTextView;
    private TextView powerTextView;
    private TextView directionTextView;
    // Importing also other views
    private JoystickView joystick;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        angleTextView = (TextView) findViewById(R.id.angleTextView);
        powerTextView = (TextView) findViewById(R.id.powerTextView);
        directionTextView = (TextView) findViewById(R.id.directionTextView);
        //Referencing also other views
        joystick = (JoystickView) findViewById(R.id.joystickView);

        //Event listener that always returns the variation of the angle in degrees, motion power in percentage and direction of movement
        joystick.setOnJoystickMoveListener(new OnJoystickMoveListener() {

            @Override
            public void onValueChanged(int angle, int power, int direction) {
                // TODO Auto-generated method stub
                angleTextView.setText(" " + String.valueOf(angle) + "°");
                powerTextView.setText(" " + String.valueOf(power) + "%");
                switch (direction) {
                    case JoystickView.FRONT:
                        directionTextView.setText(R.string.front_lab);
                        break;
                    case JoystickView.FRONT_RIGHT:
                        directionTextView.setText(R.string.front_right_lab);
                        break;
                    case JoystickView.RIGHT:
                        directionTextView.setText(R.string.right_lab);
                        break;
                    case JoystickView.RIGHT_BOTTOM:
                        directionTextView.setText(R.string.right_bottom_lab);
                        break;
                    case JoystickView.BOTTOM:
                        directionTextView.setText(R.string.bottom_lab);
                        break;
                    case JoystickView.BOTTOM_LEFT:
                        directionTextView.setText(R.string.bottom_left_lab);
                        break;
                    case JoystickView.LEFT:
                        directionTextView.setText(R.string.left_lab);
                        break;
                    case JoystickView.LEFT_FRONT:
                        directionTextView.setText(R.string.left_front_lab);
                        break;
                    default:
                        directionTextView.setText(R.string.center_lab);
                }
            }
        }, JoystickView.DEFAULT_LOOP_INTERVAL);
    }
}
```

strings.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>

    <string name="app_name">My Application</string>
    <string name="angle_lab">Angle:</string>
    <string name="power_lab">Power:</string>
    <string name="direction_lab">Direc:</string>
    <string name="none"></string>
    <string name="front_lab">Front</string>
    <string name="front_right_lab">Front-Right</string>
    <string name="right_lab">Right</string>
    <string name="right_bottom_lab">Right-Bottom</string>
    <string name="bottom_lab">Bottom</string>
    <string name="bottom_left_lab">Left-Bottom</string>
    <string name="left_lab">Left</string>
    <string name="left_front_lab">Front-Left</string>
    <string name="center_lab">Center</string>
    <string name="rights_lab">http://zerokol.com</string>

</resources>
```

This is an implementation of a custom android view that works like a Joystick, this view controls two variables, angle motion and power motion of the screen touch.

[![Creative Commons](http://i.creativecommons.org/l/by-sa/3.0/88x31.png)](http://creativecommons.org/licenses/by-sa/3.0/)

JoystickView by [AJ Alves](http://zerokol.com) is licensed under a [Creative Commons Attribution-ShareAlike 3.0 Unported License](http://creativecommons.org/licenses/by-sa/3.0/).
Based on a work at [github.com](http://github.com/zerokol/Joystick) and page [zerokol.com/2012/03/joystickview-uma-view-customizada-que.html](http://www.zerokol.com/2012/03/joystickview-uma-view-customizada-que.html).
Permissions beyond the scope of this license may be available at [http://github.com/zerokol](http://github.com/zerokol).

## Special Thanks to:

#### @mksmbrtsh (https://github.com/mksmbrtsh)
#### @Mayco-Anderson (https://github.com/Mayco-Anderson)
