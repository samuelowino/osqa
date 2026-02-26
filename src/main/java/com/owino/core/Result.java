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
public sealed interface Result<T> {
    record Success<T>(T value) implements Result<T>{}
    record Failure<T>(Throwable error) implements Result<T>{}
    static <T> Result<T> success(T value){
        return new Success<>(value);
    }
    static <T> Result<T> failure(String message){
        return new Failure<>(new Throwable(message));
    }
}
