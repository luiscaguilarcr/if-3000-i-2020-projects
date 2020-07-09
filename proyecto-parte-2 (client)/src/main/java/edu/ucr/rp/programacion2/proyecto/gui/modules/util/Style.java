package edu.ucr.rp.programacion2.proyecto.gui.modules.util;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import static edu.ucr.rp.programacion2.proyecto.gui.modules.util.UIConstants.*;


public class Style{
    /**
     * @param label labelrecibe
     * @param type  int - 1 Title, 2 Subtitle, 3 normal.
     * @author Jeison
     */
    public static void setLabelConfig(Label label, int type) {
        label.setTextAlignment(TextAlignment.CENTER);
        switch (type) {
            case 1://Title
                label.setFont(new Font(FONT, TITLE_FONT_SIZE));
                break;
            case 2://Subtitle
                label.setFont(new Font(FONT, SUBTITLE_FONT_SIZE));
                break;
            case 3://Normal
                label.setFont(new Font(FONT, NORMAL_FONT_SIZE));
                break;
        }//End switch
    }//End setLabelConfig()

    public static final void setLabelColor(Label label, String hexCode){
        label.setTextFill(Color.valueOf(hexCode));
    }
}
