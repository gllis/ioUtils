package com.gllis.util;

import com.gllis.App;

import java.awt.*;

public class ComponentUtil {
    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    private static Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(App.mainFrame.getGraphicsConfiguration());

    private static int screenWidth = screenSize.width - screenInsets.left - screenInsets.right;

    private static int screenHeight = screenSize.height - screenInsets.top - screenInsets.bottom;

    /**
     * Set up the component preferSize and position it in the center of the screen
     */
    public static void setPreferSizeAndLocateToCenter(Component component, int preferWidth, int preferHeight) {
        component.setBounds((screenWidth - preferWidth) / 2, (screenHeight - preferHeight) / 2,
                preferWidth, preferHeight);
        Dimension preferSize = new Dimension(preferWidth, preferHeight);
        component.setPreferredSize(preferSize);
    }

    /**
     * Set the component preferSize and position it in the center of the screen (based on the percentage of screen width)
     */
    public static void setPreferSizeAndLocateToCenter(Component component, double preferWidthPercent, double preferHeightPercent) {
        int preferWidth = (int) (screenWidth * preferWidthPercent);
        int preferHeight = (int) (screenHeight * preferHeightPercent);
        setPreferSizeAndLocateToCenter(component, preferWidth, preferHeight);
    }

    public static void setPreferredSize() {

    }
}