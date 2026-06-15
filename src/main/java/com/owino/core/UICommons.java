package com.owino.core;
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
import javafx.scene.image.Image;
import java.io.File;
import java.io.IOException;
public class UICommons {
    public static Result<Image> loadImage(String fileName) {
        var file = new StringBuilder("images")
                .append(File.separator)
                .append(fileName)
                .toString();
        var inputStream = UICommons.class.getClassLoader().getResourceAsStream(file);
        if (inputStream == null) return new Result.Failure<Image>( new IOException("""
                Failed to load image asset, input stream is null
                """));
        return new Result.Success<>(new Image(inputStream));
    }
}
