package com.owino.desktop;
/*
 * Copyright (C) 2026 Samuel Owino
 *
 * OSQA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OSQA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OSQA.  If not, see <https://www.gnu.org/licenses/>.
 */
import javafx.geometry.Insets;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
public class STYLES {
    public static String FORM_SECTION_BORDER = """
            -fx-border-color: gray;
            -fx-border-width: 1;
            -fx-border-radius: 5;
            -fx-padding: 10;
            """;
    public static String TITLE_LABEL = """
            -fx-font-size: 32px;
            -fx-font-weight: bold;
            -fx-font-family: 'System Bold';
            -fx-text-fill: #2c3e50;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 2);
            """;
    public static Color OSQA_GREEN = Color.rgb(59, 237, 63);
    public static Color TRANSLUCENT_GREEN = Color.rgb(11, 64, 12,0.5);
    public static Color TRANSLUCENT_BLUE = Color.rgb(39, 19, 209,0.3);
    public static Color OSQA_BLUE = Color.rgb(70, 88, 232);
    public static Color LIGHT_TRANSLUCENT_BLUE = Color.rgb(128, 119, 212,0.5);
    public static Color OSQA_LIGHT_BLUE = Color.rgb(185, 179, 242);
    public static Color LIGHT_TRANSLUCENT_RED = Color.rgb(130, 47, 5,0.5);
    public static Color OSQA_RED = Color.rgb(245, 106, 37);
    public static CornerRadii CARDS_CORNER_RADIUS = new CornerRadii(12);
    public static Insets CARDS_INTERNAL_MARGIN = new Insets(3,0,3,0);
}
