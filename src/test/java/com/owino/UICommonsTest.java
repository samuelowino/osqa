package com.owino;
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
import com.owino.core.Result;
import com.owino.core.UICommons;
import javafx.scene.image.Image;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
public class UICommonsTest {
    @Test
    @DisplayName("should load javafx image from resource dir")
    public void shouldLoadImageFileTest() {
        var imageFileName = "testImage.png";
        var result = UICommons.loadImage(imageFileName);
        assertThat(result).isInstanceOf(Result.Success.class);
        if (result instanceof Result.Success<Image> (Image finalImage)) {
            assertThat(finalImage).isNotNull();
        }
    }
}
